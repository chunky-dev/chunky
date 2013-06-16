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
package se.llbit.chunky.renderer.ui;

import static org.jocl.CL.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import se.llbit.chunky.renderer.cl.CLDevice;
import se.llbit.chunky.renderer.cl.CLPlatform;
import se.llbit.chunky.renderer.cl.CLRenderManager;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;

/**
 * A dialog to select an OpenCL device to run the OpenCL renderer on.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class CLDeviceSelector extends JDialog {

	private static final Logger logger =
			Logger.getLogger(CLDeviceSelector.class);

	/**
	 * The available CL platforms
	 */
	private List<CLPlatform> platforms = new ArrayList<CLPlatform>();

	/**
	 * CL device map
	 */
	private Map<CLPlatform, List<CLDevice>> deviceMap =
			new ConcurrentHashMap<CLPlatform, List<CLDevice>>();

	private JComboBox platformList;
	private JComboBox deviceList;
	private JLabel deviceInfo = new JLabel();

	/**
	 * Constructor
	 * @param parent
	 * @param world
	 * @param chunks
	 */
	public CLDeviceSelector(final JFrame parent, final World world,
			final Collection<ChunkPosition> chunks) {

		super(parent, "OpenCL Device Selector");

		enumeratePlatforms();

		JLabel platformLbl = new JLabel("Platform:");

		platformList = new JComboBox(platforms.toArray());

		JLabel deviceLbl = new JLabel("Device:");

		JButton runBtn = new JButton("Run on selected device");
		runBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CLPlatform platform = null;
				int plaf = platformList.getSelectedIndex();
				if (plaf >= 0 && plaf < platforms.size()) {
					platform = platforms.get(plaf);
				}
				CLDevice device = null;
				int dev = deviceList.getSelectedIndex();
				if (dev >= 0 && dev < deviceList.getItemCount()) {
					device = (CLDevice) deviceList.getItemAt(dev);
				}
				if (platform != null && device != null) {
					CLRenderManager manager = new CLRenderManager(parent);
					manager.setupOpenCL(platform.id, device.id, world, chunks);
					manager.start();
					setVisible(false);
					dispose();
				}
			}
		});

		deviceList = new JComboBox();

		updateDeviceList();
		updateDeviceInfo();

		deviceList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDeviceInfo();
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout.createParallelGroup()
				.addComponent(platformLbl)
				.addComponent(platformList)
				.addComponent(deviceLbl)
				.addComponent(deviceList)
				.addComponent(deviceInfo)
				.addComponent(runBtn)
			)
			.addContainerGap()
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(platformLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(platformList)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(deviceLbl)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(deviceList)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(deviceInfo)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(runBtn)
			.addContainerGap()
		);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	/**
	 * Enumerate all available OpenCL platforms and devices.
	 */
	public void enumeratePlatforms() {

		// don't want to do error checks
		setExceptionsEnabled(true);

		int[] num_platforms = new int[1];
		clGetPlatformIDs(0, null, num_platforms);

		if (num_platforms[0] == 0) {
			logger.error("No OpenCL platforms available!");
			return;
		}

		cl_platform_id[] platform_id = new cl_platform_id[num_platforms[0]];
		clGetPlatformIDs(platform_id.length, platform_id, null);

		// enumerate platforms and devices

		byte[] data = new byte[500];
		Pointer dataPtr = Pointer.to(data);
		long[] dataSize = new long[1];
		long[] cl_long = new long[1];
		int[] cl_uint = new int[1];

		for (int plaf = 0; plaf < platform_id.length; ++plaf) {

			clGetPlatformInfo(platform_id[plaf], CL_PLATFORM_NAME, 500, dataPtr, dataSize);
			String name = new String(data, 0, (int) dataSize[0] - 1);

			clGetPlatformInfo(platform_id[plaf], CL_PLATFORM_VERSION, 500, dataPtr, dataSize);
			String version = new String(data, 0, (int) dataSize[0] - 1);

			CLPlatform platform = new CLPlatform(platform_id[plaf], name, version);
			platforms.add(platform);

			int[] num_devices = new int[1];
			clGetDeviceIDs(platform_id[plaf], CL_DEVICE_TYPE_ALL, 0, null, num_devices);

			cl_device_id[] devices = new cl_device_id[num_devices[0]];
			clGetDeviceIDs(platform_id[plaf], CL_DEVICE_TYPE_ALL, num_devices[0], devices, null);

			List<CLDevice> deviceList = new ArrayList<CLDevice>();

			for (int dev = 0; dev < devices.length; ++dev) {
				clGetDeviceInfo(devices[dev], CL_DEVICE_TYPE, Sizeof.cl_long, Pointer.to(cl_long), null);

				String deviceType = "Unknown";
				switch ((int) cl_long[0]) {
				case (int) CL_DEVICE_TYPE_CPU:
					deviceType = "CPU";
					break;
				case (int) CL_DEVICE_TYPE_GPU:
					deviceType = "GPU";
					break;
				case (int) CL_DEVICE_TYPE_ACCELERATOR:
					deviceType = "ACCELERATOR";
					break;
				case (int) CL_DEVICE_TYPE_DEFAULT:
					deviceType = "DEFAULT";
					break;
				}

				clGetDeviceInfo(devices[dev], CL_DEVICE_NAME, 500, dataPtr, dataSize);
				String deviceName = new String(data, 0, (int) dataSize[0] - 1);

				clGetDeviceInfo(devices[dev], CL_DEVICE_MAX_COMPUTE_UNITS, Sizeof.cl_uint, Pointer.to(cl_uint), null);

				int computeUnits = cl_uint[0];

				clGetDeviceInfo(devices[dev], CL_DEVICE_MAX_WORK_GROUP_SIZE, Sizeof.size_t, Pointer.to(cl_uint), null);

				int workGroupSize = cl_uint[0];

				CLDevice device = new CLDevice(devices[dev],
						deviceType, deviceName, computeUnits, workGroupSize);
				deviceList.add(device);
			}

			deviceMap.put(platform, deviceList);
		}
	}

	private void updateDeviceInfo() {
		int dev = deviceList.getSelectedIndex();
		if (dev >= 0 && dev < deviceList.getItemCount()) {
			CLDevice device = (CLDevice) deviceList.getItemAt(dev);
			deviceInfo.setText(device.getInfoString());
		}
	}

	private void updateDeviceList() {
		int plaf = platformList.getSelectedIndex();
		if (plaf >= 0 && plaf < platforms.size()) {
			// quick & dirty way to update the combo box!
			Object[] devices = deviceMap.get(platforms.get(plaf)).toArray();
			deviceList.setModel(new JComboBox(devices).getModel());
		}
	}

}
