#!/usr/bin/env python
import sys
import math
import os
import re

# 90% confidence intervals with n<30 degrees of freedom
t = [ 6.314, 2.920, 2.353, 2.132, 2.015, 1.943, 1.895, 1.860, 1.833, 1.812,
1.796, 1.782, 1.771, 1.761, 1.753, 1.746, 1.740, 1.734, 1.729, 1.725,
1.721, 1.717, 1.714, 1.711, 1.708, 1.706, 1.703, 1.701, 1.699, 1.697 ]

# results: tilewidth -> sps measurements
results = { }

# calculate confidence intervals using student's t distribution
def calcdist():
	global results
	for w in sorted(results):
		sps = results[w]
		sum = 0
		n = len(sps)
		for x in sps:
			sum += x

		mean = sum / n

		p = 0
		for x in sps:
			p += math.pow(x - mean, 2)

		# std deviation
		s = math.sqrt(p / (n-1))

		c1 = mean - t[n-1] * (s / math.sqrt(n))
		c2 = mean + t[n-1] * (s / math.sqrt(n))
		print w, mean, c1, c2

def parselog(path):
	global results
	with open(path) as f:
		for line in f.readlines():
			items = line.split()
			w = int(items[0])
			sps = float(items[1])
			if w not in results:
				results[w] = []
			results[w].append(sps)

def main():
	if len(sys.argv) < 2:
		print 'usage: calcdist.py <LOGFILES>'
		sys.exit(1)
		return

	for i in range(1, len(sys.argv)):
		parselog(sys.argv[i])

	calcdist()

if __name__ == "__main__":
	main()
