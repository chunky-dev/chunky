/* Copyright (c) 2014 Tim De Keyser <aenterprise2@gmail.com>
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

package se.llbit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * Utility class for exporting scenes to zip files.
 *
 * @author Tim De Keyser <aenterprise2@gmail.com>
 */
public class ZipExport {
	private static final Logger logger = Logger.getLogger(ZipExport.class);

	/**
	 * Export a scene to a zip file.
	 * @param archive zip file to write
	 * @param sceneDir
	 * @param sceneName zip prefix
	 * @param extensions file extensions to include
	 */
	public static void zip(File archive, File sceneDir, String sceneName,
			String[] extensions) {
		try {
			FileOutputStream fos = new FileOutputStream(archive);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String extension : extensions) {
				addToZipFile(zos, sceneDir, sceneName, sceneName + extension);
			}

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}

	}

	private static void addToZipFile(ZipOutputStream zos, File sceneDir,
			String prefix, String fileName) throws FileNotFoundException,
			IOException {

		File file = new File(sceneDir, fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(prefix + "/" + fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[4096];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
}
