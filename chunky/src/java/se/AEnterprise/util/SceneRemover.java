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


package se.AEnterprise.util;

import java.io.File;


public class SceneRemover {
	
	public static void FileRemoveParser(String sceneDir, String sceneName, int SPP){
		String file1 = sceneDir + "/" + sceneName + ".cvf";
		String file2 = sceneDir + "/" + sceneName + ".cvf.backup";
		String file3 = sceneDir + "/" + sceneName + ".dump";
		String file4 = sceneDir + "/" + sceneName + ".dump.backup";
		String file5 = sceneDir + "/" + sceneName + ".foliage";
		String file6 = sceneDir + "/" + sceneName + ".grass";
		String file7 = sceneDir + "/" + sceneName + ".octree";
		
		remove(file1);
		remove(file2);
		remove(file3);
		remove(file4);
		remove(file5);
		remove(file6);
		remove(file7);
	
	for (int i = 0; i>=SPP; i+=50){
		String file8 = sceneDir + "/" + sceneName + ".png";
		remove (file8);
		}
	}
	
	public static void remove (String fileName){
		File file = new File (fileName);
		if (file.exists()){
			file.delete();
		}
	}
}
