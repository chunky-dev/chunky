package se.AEnterpise.ZipForExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipForExport {
		

	public static void Zip (String archive, String sceneDir, String sceneName) {

		try {
			FileOutputStream fos = new FileOutputStream(archive);
			ZipOutputStream zos = new ZipOutputStream(fos);

			String file1Name = sceneDir + "/" + sceneName + ".cvf";
			String file2Name = sceneDir + "/" + sceneName + ".cvf.backup";
			String file3Name = sceneDir + "/" + sceneName + ".dump";
			String file4Name = sceneDir + "/" + sceneName + ".dump.backup";
			String file5Name = sceneDir + "/" + sceneName + ".foliage";
			String file6Name = sceneDir + "/" + sceneName + ".grass";
			String file7Name = sceneDir + "/" + sceneName + ".octree";

			addToZipFile(file1Name, zos);
			addToZipFile(file2Name, zos);
			addToZipFile(file3Name, zos);
			addToZipFile(file4Name, zos);
			addToZipFile(file5Name, zos);
			addToZipFile(file6Name, zos);
			addToZipFile(file7Name, zos);

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
 
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

}