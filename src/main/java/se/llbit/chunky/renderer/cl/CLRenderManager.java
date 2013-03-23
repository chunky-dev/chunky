/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.renderer.cl;

import static org.jocl.CL.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import se.llbit.chunky.renderer.ProgressListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.ui.Chunk3DView;
import se.llbit.chunky.renderer.ui.ViewListener;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;

import se.llbit.j99.castor.Symbol;
import se.llbit.j99.fragment.CommentFragmenter;
import se.llbit.j99.fragment.CompositeFragment;
import se.llbit.j99.fragment.FragmentReader;
import se.llbit.j99.fragment.IFragment;
import se.llbit.j99.fragment.IFragmenter;
import se.llbit.j99.fragment.LineFragment;
import se.llbit.j99.fragment.LineFragmenter;
import se.llbit.j99.fragment.LineSplicer;
import se.llbit.j99.fragment.TrigraphReplacer;
import se.llbit.j99.pp.Identifier;
import se.llbit.j99.pp.Macro;
import se.llbit.j99.pp.ObjMacro;
import se.llbit.j99.pp.PPState;
import se.llbit.j99.pp.PPVisitor;
import se.llbit.j99.pp.SourceFile;
import se.llbit.j99.pp.StringLit;
import se.llbit.j99.problem.CompileProblem;
import se.llbit.j99.util.DirectiveParser;
import se.llbit.math.Color;
import se.llbit.math.Matrix3d;
import se.llbit.math.Octree;
import se.llbit.math.Vector3d;

@SuppressWarnings("javadoc")
public class CLRenderManager extends Thread implements Renderer,
	ProgressListener, ViewListener {

	private static final Logger logger =
			Logger.getLogger(CLRenderManager.class);

	private int workItems;
	private long[] globalWorkSize;
	private long[] localWorkSize;

	private cl_kernel kernel;
	private cl_mem sampleBuffer;
	private cl_mem octreeBuffer;
	private cl_mem blockColorBuffer;
	private cl_mem transformBuffer;
	private cl_mem originBuffer;
	private cl_mem seedBuffer;
	private cl_command_queue commandQueue;
	private int bufferWidth;
	private int bufferHeight;
	private BufferedImage buffer;
	private BufferedImage backBuffer;
	private final Chunk3DView view;
	private Octree octree;
	private final Vector3d origin = new Vector3d();
	private final Vector3d up = new Vector3d(0, 1, 0);
	private double pitch = 0;
	private double yaw = - Math.PI / 2;
	private final Matrix3d transform = new Matrix3d();
	private final Matrix3d tmpTransform = new Matrix3d();
	private int numSamples = 0;
	private long renderTime = 0;
	private double fov = 100;

	public CLRenderManager(JFrame parent) {

		super("Render Manager");

		view = new Chunk3DView(this, parent);
		bufferWidth = 400;
		bufferHeight = 400;
		globalWorkSize = new long[] { bufferWidth, bufferHeight };
		localWorkSize = new long[] { 16, 16 };
		workItems = (int) (globalWorkSize[0] * globalWorkSize[1]);
		view.getCanvas().setPreferredSize(new Dimension(bufferWidth, bufferHeight));
		view.pack();
		view.setLocationRelativeTo(parent);
		view.setVisible(true);
		view.getCanvas().setRenderer(this);
		buffer = new BufferedImage(bufferWidth, bufferHeight,
				BufferedImage.TYPE_INT_ARGB);
		backBuffer = new BufferedImage(bufferWidth, bufferHeight,
				BufferedImage.TYPE_INT_ARGB);
	}

	public void setupOpenCL(cl_platform_id platform_id, cl_device_id device_id,
			World world, Collection<ChunkPosition> chunks) {
		cl_context_properties contextProps = new cl_context_properties();
		contextProps.addProperty(CL_CONTEXT_PLATFORM, platform_id);

		cl_context context = clCreateContext(contextProps, 1, new cl_device_id[] { device_id },
				null, null, null);
		commandQueue = clCreateCommandQueue(context, device_id, 0, null);

		String kernelSource = processKernel();
		cl_program program = clCreateProgramWithSource(context, 1,
				new String[] {kernelSource}, null, null);
		clBuildProgram(program, 0, null, "-cl-mad-enable", null, null);
		kernel = clCreateKernel(program, "path_trace", null);
		sampleBuffer = clCreateBuffer(context, CL_MEM_READ_WRITE,
				3 * bufferWidth * bufferHeight * Sizeof.cl_float, null, null);

		Scene scene = new Scene();
		world.setDimension(0);
		scene.loadChunks(this, world, chunks);
		octree = scene.getOctree();
		origin.set(scene.calcCenterCamera());
		origin.sub(scene.getOrigin());
		int[] octreeData = octree.toDataBuffer();
		logger.info("octree size: " + (4 * octreeData.length) + " bytes");
		octreeBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY,
				octreeData.length * Sizeof.cl_uint, null, null);
		clEnqueueWriteBuffer(commandQueue, octreeBuffer, CL_TRUE, 0,
				octreeData.length * Sizeof.cl_int, Pointer.to(octreeData),
				0, null, null);
		blockColorBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY,
				3 * 256 * Sizeof.cl_float, null, null);
		transformBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY,
				9 * Sizeof.cl_float, null, null);
		originBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY,
				3 * Sizeof.cl_float, null, null);
		seedBuffer = clCreateBuffer(context, CL_MEM_READ_WRITE,
				workItems * Sizeof.cl_uint2, null, null);

		Random random = new Random(System.currentTimeMillis());
		// seed the RNGs
		int[] buf = new int[workItems*2];
		for (int y = 0; y < globalWorkSize[1]; ++y) {
			for (int x = 0; x < globalWorkSize[0]; ++x) {
				buf[(int) ((x + y*globalWorkSize[0])*2)] = random.nextInt();
				buf[(int) ((x + y*globalWorkSize[1])*2 + 1)] = random.nextInt();
			}
		}
		clEnqueueWriteBuffer(commandQueue, seedBuffer, CL_TRUE, 0,
				workItems * Sizeof.cl_uint2, Pointer.to(buf),
				0, null, null);

		float[] blockColor = new float[256*3];
		for (int j = 0; j < 256; ++j) {
			Block block = Block.get(j);
			float[] color;
			switch (block.id) {
			case Block.LEAVES_ID:
			case Block.GRASS_ID:
			case Block.TALLGRASS_ID:
				color = Biomes.getGrassColorLinear(0);
				System.out.print(String.format("\t0x%08X,", Biomes.getGrassColor(0)));
				break;
			default:
				color = block.getTexture().getAvgColorLinear();
				System.out.print(String.format("\t0x%08X,", block.getTexture().getAvgColor()));
			}
			if (j > 0 && j % 8 == 0)
				System.out.println();
			blockColor[j*3] = color[0];
			blockColor[j*3 + 1] = color[1];
			blockColor[j*3 + 2] = color[2];
		}
		clEnqueueWriteBuffer(commandQueue, blockColorBuffer, CL_TRUE, 0,
				3 * 256 * Sizeof.cl_float, Pointer.to(blockColor),
				0, null, null);

		updateTransform();
		updateOrigin();
	}

	private String processKernel() {
		//Preprocessor preprocessor = new Preprocessor(in, problems, includeDirs, basePath)

		try {

			String resourceName = "/kernel.c";
			String basePath = "";
			InputStream in = CLRenderManager.class
					.getResourceAsStream(resourceName);
			if (in == null) {
				logger.warn("Could not load OpenCL kernel!");
				throw new Error("Could not load OpenCL kernel!");
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ArrayList<CompileProblem> problems = new ArrayList<CompileProblem>();
			IFragmenter fragmenter = new CommentFragmenter(new LineSplicer(new TrigraphReplacer(
							new LineFragmenter(resourceName, in))), problems);

			PPState state = new PPState();
			state.setProblemCollection(problems);
			state.setIncludeDirs(new LinkedList<String>());
			// predefined macros
			// TODO: these need to be attached to the root tree
			state.define(new Identifier("_WIN32_"),
					new ObjMacro(new Identifier("_WIN32_"),
					new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>()));
			/*state.define(new Identifier("AMBIENT_OCCLUSION"),
					new ObjMacro(new Identifier("AMBIENT_OCCLUSION"),
					new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>()));*/
			/*state.define(new Identifier("RNGTEST"),
					new ObjMacro(new Identifier("RNGTEST"),
					new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>()));*/

			IFragment processed = preprocess(fragmenter, state, resourceName, basePath);

			in.close();

			CompileProblem.reportProblems(problems);
			if (!CompileProblem.isCritical(problems)) {

				Reader reader = new FragmentReader(processed);

				while (reader.ready())
					out.write((char)reader.read());
				reader.close();
				out.close();
				String kernel = new String(out.toByteArray());
				return kernel;
			}

		} catch (IOException e) {
			logger.error("Fatal IO error", e);
		} catch (RuntimeException e) {
			logger.error("Fatal error", e);
		}

		return null;
	}

	private static IFragment preprocess(IFragmenter in, PPState state, String fn, String basePath) {
		Identifier name = new Identifier("__FILE__");
		StringLit filename = new StringLit("\""+fn+"\"");
		name.setToken(new Symbol(new LineFragment("@j99", 0, 0, "__FILE__")));
		filename.setToken(new Symbol(new LineFragment("@j99", 0, 0, "\""+fn+"\"")));
		Macro fileMacro = new ObjMacro(name,
				new se.llbit.j99.pp.List<se.llbit.j99.pp.Token>().add(filename));// TODO: this needs to be attached to the root tree
		state.define(name, fileMacro);

		DirectiveParser parser = new DirectiveParser(in, state);
		SourceFile root = parser.parse();
		PPVisitor visitor = new PPVisitor(state, basePath);
		root.accept(visitor);
		return new CompositeFragment(visitor.getResult());
	}

	public void run() {
		float[] samples;
		long frameStart;

		while (!isInterrupted()) {
			synchronized (this) {

				frameStart = System.currentTimeMillis();
				clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(sampleBuffer));
				clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(originBuffer));
				clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(octreeBuffer));
				clSetKernelArg(kernel, 3, Sizeof.cl_uint, Pointer.to(new int[] {octree.depth}));
				clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(transformBuffer));
				clSetKernelArg(kernel, 5, Sizeof.cl_float, Pointer.to(new float[] { (float) fov }));
				clSetKernelArg(kernel, 6, Sizeof.cl_mem, Pointer.to(seedBuffer));
				clSetKernelArg(kernel, 7, Sizeof.cl_uint, Pointer.to(new int[] {numSamples}));
				clSetKernelArg(kernel, 8, Sizeof.cl_mem, Pointer.to(blockColorBuffer));

				clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
						globalWorkSize, localWorkSize, 0, null, null);
				samples = new float[bufferWidth*bufferHeight*3];
				clEnqueueReadBuffer(commandQueue, sampleBuffer, CL_TRUE, 0,
						3 * bufferWidth * bufferHeight * Sizeof.cl_float, Pointer.to(samples),
						0, null, null);

				renderTime += System.currentTimeMillis() - frameStart;
			}

			numSamples += 1;
			if (numSamples % 10 == 0) {
				logger.info("SPS: " + (int) ((numSamples * workItems) / Math.max(1, (renderTime/1000.))));
			}
			updateCanvas(samples);
		}
	}

	public synchronized void refresh() {
		numSamples = 0;
		renderTime = 0;
	}

	public synchronized void updateTransform() {
		tmpTransform.rotZ(pitch);
		transform.rotY(yaw);
		transform.mul(tmpTransform);
		float[] mat = new float[9];
		mat[0] = (float) transform.m11;
		mat[1] = (float) transform.m12;
		mat[2] = (float) transform.m13;
		mat[3] = (float) transform.m21;
		mat[4] = (float) transform.m22;
		mat[5] = (float) transform.m23;
		mat[6] = (float) transform.m31;
		mat[7] = (float) transform.m32;
		mat[8] = (float) transform.m33;
		clEnqueueWriteBuffer(commandQueue, transformBuffer, CL_TRUE, 0,
				mat.length * Sizeof.cl_float, Pointer.to(mat),
				0, null, null);
	}

	public synchronized void updateOrigin() {
		float[] o = new float[3];
		o[0] = (float) origin.x;
		o[1] = (float) origin.y;
		o[2] = (float) origin.z;
		clEnqueueWriteBuffer(commandQueue, originBuffer, CL_TRUE, 0,
				o.length * Sizeof.cl_float, Pointer.to(o),
				0, null, null);
	}

	private void updateCanvas(float[] samples) {
		try {
			synchronized (buffer) {
				DataBufferInt dataBuffer =
						(DataBufferInt) backBuffer.getRaster().getDataBuffer();
				int[] imgData = dataBuffer.getData();

				// paint the back buffer
				for (int i = 0; i < bufferWidth*bufferHeight; ++i) {
					imgData[i] = Color.getRGB(
							Math.min(1, Math.pow(samples[i*3], 1/2.2)),
							Math.min(1, Math.pow(samples[i*3+1], 1/2.2)),
							Math.min(1, Math.pow(samples[i*3+2], 1/2.2)));
				}

				// flip buffers
				BufferedImage tmp = buffer;
				buffer = backBuffer;
				backBuffer = tmp;
			}
			view.getCanvas().repaint();
		} catch (IllegalStateException e) {
			logger.error("Unexpected exception while rendering back buffer", e);
		}
	}

	@Override
	public void drawBufferedImage(Graphics g, int width, int height) {
		synchronized (buffer) {
			g.drawImage(buffer, 0, 0, width, height, null);
		}
	}

	@Override
	public void setBufferFinalization(boolean flag) {
	}

	@Override
	public void setProgress(String task, int done, int start, int target) {
	}

	@Override
	public void setProgress(String task, int done, int start, int target,
			String eta) {
	}

	@Override
	public synchronized void onStrafeLeft() {
		Vector3d d = new Vector3d(1, 0, 0);
		tmpTransform.rotY(yaw);
		tmpTransform.transform(d);
		Vector3d right = new Vector3d();
		right.cross(up, d);
		origin.scaleAdd(-1, right, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public synchronized void onStrafeRight() {
		Vector3d d = new Vector3d(1, 0, 0);
		tmpTransform.rotY(yaw);
		tmpTransform.transform(d);
		Vector3d right = new Vector3d();
		right.cross(up, d);
		origin.scaleAdd(1, right, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public synchronized void onMoveForward() {
		Vector3d d = new Vector3d(0, -1, 0);
		transform.transform(d);
		origin.scaleAdd(1, d, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public synchronized void onMoveBackward() {
		Vector3d d = new Vector3d(0, -1, 0);
		transform.transform(d);
		origin.scaleAdd(-1, d, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public void onMoveForwardFar() {
	}

	@Override
	public void onMoveBackwardFar() {
	}

	@Override
	public synchronized void onMoveUp() {
		origin.scaleAdd(1, up, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public synchronized void onMoveDown() {
		origin.scaleAdd(-1, up, origin);
		updateOrigin();
		refresh();
	}

	@Override
	public void onMouseDragged(int dx, int dy) {
		double dyaw = - (Math.PI / 250) * dx;
		double dpitch = (Math.PI / 250) * dy;
		double fov = 70;
		double fovRad = (fov / 360) * Math.PI;
		this.yaw += dyaw * fovRad;
		this.pitch += dpitch * fovRad;

		this.pitch = Math.min(0, this.pitch);
		this.pitch = Math.max(-Math.PI, this.pitch);

		if (this.yaw > Math.PI * 2)
			this.yaw -= Math.PI * 2;
		else if (this.yaw < -Math.PI * 2)
			this.yaw += Math.PI * 2;

		updateTransform();
		refresh();
	}

	@Override
	public void setViewVisible(boolean visible) {
		if (!visible) {
			interrupt();
		}
	}

	@Override
	public void onZoom(int diff) {
		double scale = Camera.MAX_FOV - Camera.MIN_FOV;
		fov = fov + diff * scale/20;
		fov = Math.max(Camera.MIN_FOV, fov);
		fov = Math.min(Camera.MAX_FOV, fov);
		refresh();
	}
}
