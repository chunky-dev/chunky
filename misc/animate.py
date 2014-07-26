# sample script for reating animations

import json
import os
import math
from os import path
from subprocess import call
from shutil import copyfile

scene_dir = 'D:\\Users\\Jesper\\.chunky\\scenes\\'
filename = path.join(scene_dir, 'skycompare.json')
with open(filename, 'r') as fp:
	sdf = json.load(fp)

dump_filename = path.join(scene_dir, 'skycompare.dump')
num = 500
num2 = float(2*500)
azimuth = 3*math.pi/2
for i in range(0, num):
	sdf['spp'] = 0
	sdf['sppTarget'] = 10
	sdf['sun']['altitude'] = math.pi * (500-i)/num2
	sdf['sun']['azimuth'] = azimuth
	with open(filename, 'w') as fp:
		json.dump(sdf, fp)
	if (path.isfile(dump_filename)):
		os.remove(dump_filename)
	call(['java', '-jar', 'chunky.jar', '-render', 'skycompare'])
	copyfile(path.join(scene_dir, 'skycompare-10.png'), 'frame%03d.png' % (i+501))
