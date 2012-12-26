package se.llbit.chunky.launcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Launcher for the Chunky application
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyLauncher {
	
	/**
	 * Chunky launcher entry point
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		try {
			ClassLoader parentCL = ChunkyLauncher.class.getClassLoader();
			
			// build list of library jar files
			CodeSource src = ChunkyLauncher.class
					.getProtectionDomain().getCodeSource();
			List<URL> jars = new ArrayList<URL>();
			
			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream in = new ZipInputStream(jar.openStream());
				ZipEntry entry = null;
				
				File tmpDir = null;
				
				while ( (entry = in.getNextEntry()) != null ) {
					String name = entry.getName();
					if (name.startsWith("lib") && name.endsWith(".jar")) {
						if (tmpDir == null) {
							tmpDir = createTempDir();
						}
						jars.add(unpackJar(parentCL, name, tmpDir));
					}
				}
			}
			
			URL[] urls = new URL[jars.size()];
			for (int i = 0; i < jars.size(); ++i)
				urls[i] = jars.get(i);
			URLClassLoader childCL = new URLClassLoader(urls, parentCL);
			
			Class<?> mainClass = Class.forName(
					"se.llbit.chunky.main.Chunky", true, childCL);
			Object instance = mainClass.newInstance();
			Method runMethod = mainClass.getDeclaredMethod("run", String[].class);
			runMethod.invoke(instance, new Object[] { args });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create temporary directory for unpacked jar files
	 * @return The temporary directory file handle
	 */
	private static File createTempDir() {
		File dir = new File(System.getProperty("java.io.tmpdir"), "Chunky");
		if (!dir.exists()) {
			dir.mkdir();
		}
		dir.setReadable(true, false);
		dir.setWritable(true, false);
		dir.setExecutable(true, false);
		return dir;
	}

	/**
	 * Unpack the jar file to a temporary directory. Idea is from JDotSoft's JarClassLoader.
	 * @param name
	 * @return
	 * @throws IOException 
	 */
	private static URL unpackJar(ClassLoader parentCL, String name, File tmpDir)
			throws IOException {
		
		System.out.println("Unpacking library: " + name);
		
		File tmpFile = File.createTempFile("lib", ".jar", tmpDir);
		tmpFile.deleteOnExit();
		tmpFile.setReadable(true, false);
		tmpFile.setWritable(true, false);
		tmpFile.setExecutable(true, false);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile));
		InputStream in = parentCL.getResourceAsStream(name);
		byte[] buffer = new byte[4096];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
		return tmpFile.toURI().toURL();
	}

}
