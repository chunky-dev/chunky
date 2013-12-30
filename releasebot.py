# coding=utf-8
# Releasebot Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>

# requires PRAW, Launchpadlib

# Release Candidates are not built by this script!
# Snapshots are currently built by proprietary script

import sys
import praw
import re
import io
import traceback
from subprocess import call
from getpass import getpass
from datetime import datetime
from string import join
from launchpadlib.launchpad import Launchpad

class Version:
	regex = re.compile('^(\d+\.\d+\.\d+)-?([a-zA-Z]*\.?\d*)$')
	full = ''
	milestone = ''
	suffix = ''
	series = ''
	rc = ''
	changelog = ''
	release_notes = ''

	def __init__(self, version):
		self.full = version
		regex = re.compile('^(\d+\.\d+\.\d+)-?([a-zA-Z]*\.?\d*)$')
		r = regex.match(version)
		assert r, "Invalid version name: %s (expected e.g. 1.2.13-rc.1)" % version
		self.milestone = r.groups()[0]
		self.suffix = r.groups()[1]
		self.series = join(self.milestone.split('.')[:2], '.')
		if self.suffix.startswith('rc.'):
			self.rc = self.suffix[3:]
		else:
			notes_fn = "release_notes-%s.txt" % self.milestone
			try:
				with open(notes_fn, 'r') as f:
					self.release_notes = f.read()
			except:
				print "Error: release_notes not found!"
				print "Please edit release_notes-%s.txt!" % self.milestone
				sys.exit(1)

		try:
			# load changelog
			with open("ChangeLog.txt", 'r') as f:
				f.readline() # skip version line
				while True:
					line = f.readline()
					if not line.rstrip(): break
					self.changelog += line
		except:
			print "Error: could not read ChangeLog!"
			sys.exit(1)

		if not self.changelog:
			print "Error: ChangeLog is empty!"
			sys.exit(1)

def publish(version):
	should_build = raw_input('Build release? [y/n] ')
	if should_build == "y":
		if call(['cmd', '/c', 'ant', '-Dversion=' + version.full, 'release']) is not 0:
			print "Error: Ant build failed!"
			sys.exit(1)
		if call(['makensis', 'Chunky.nsi']) is not 0:
			print "Error: NSIS failed!"
			sys.exit(1)
		if call(['git', 'archive',
			'--output=build/chunky-%s.tar.gz'%version.full,
			'--prefix=chunky-%s/'%version.full,
			version.full]) is not 0:
			print "Error: git archiving failed!"
			sys.exit(1)
	should_publish = raw_input('Publish files? [y/n] ')
	if should_publish == "y":
		if not version.rc:
			(new_release, exe_url, zip_url) = publish_release(version)
			post_release_thread(version, exe_url, zip_url)

def publish_release(version):
	launchpad = Launchpad.login_with('Releasebot', 'production', 'lpcache')

	chunky = launchpad.projects['chunky']

	# check if release exists
	release = None
	for r in chunky.releases:
		if r.version == version.milestone:
			release = r
			print "Previous %s release found: proceeding to upload additional files." \
				% version.milestone
			break

	is_new_release = release is None

	if release is None:
		# check if milestone exists
		milestone = None
		for ms in chunky.all_milestones:
			if ms.name == version.milestone:
				milestone = ms
				break

		# create milestone (and series) if needed
		if milestone is None:
			series = None
			for s in chunky.series:
				if s.name == version.series:
					series = s
					break
			if series is None:
				series = chunky.newSeries(
					name=version.series,
					summary="The current stable series for Chunky. NB: The code is maintained separately on GitHub.")
				print "Series %s created. Please manually update the series summary:" % version.series
				print series

			milestone = series.newMilestone(name=version.milestone)
			print "Milestone %s created." % version.milestone

		# create release
		release = milestone.createProductRelease(
			release_notes=version.release_notes,
			changelog=version.changelog,
			date_released=datetime.today())
		milestone.is_active = False
		print "Release %s created" % version.milestone

	assert release is not None

	# upload release files
	tarball_url = lp_upload_file(
		version,
		release,
		'chunky-%s.tar.gz' % version.full,
		'Source Code',
		'application/x-tar',
		'tarball')
	assert tarball_url
	print tarball_url
	zip_url = lp_upload_file(
		version,
		release,
		'Chunky-%s.zip' % version.full,
		'Binaries',
		'application/zip',
		'installer')
	assert zip_url
	print zip_url
	exe_url = lp_upload_file(
		version,
		release,
		'Chunky-%s.exe' % version.full,
		'Windows Installer',
		'application/octet-stream',
		'installer')
	assert exe_url
	print exe_url
	return (is_new_release, exe_url, zip_url)

"post reddit release thread"
def post_release_thread(version, exe_url, zip_url):
	should_post = raw_input('Post release thread? [y/n] ')
	if should_post != "y":
		return
	r = praw.Reddit(user_agent='releasebot')
	pw = getpass(prompt='releasebot login: ')
	r.login('releasebot', pw)
	post = r.submit('chunky', 'Version %s released!' % version.full,
		text=('''###Downloads

* [Windows installer](%s)
* [Cross-platform binaries](%s)

###Release Notes

''' % (exe_url, zip_url)) + version.release_notes + '''

###ChangeLog

''' + version.changelog)
	post.set_flair('announcement', 'announcement')
	print "Submitted Reddit release thread!"

version = Version(raw_input('Enter version: '))
print "Ready to build version %s" + version.full
try:
	publish(version)
	should_push = raw_input('All done. Push git changes? [y/n] ')
	if should_push == "y":
		call(['git', 'push', 'origin', 'master'])# push version bump commit
		call(['git', 'push', 'origin', version.full])# push version tag
except:
	exc_type, exc_value, exc_traceback = sys.exc_info()
	print "Unexpected error:"
	traceback.print_exception(exc_type, exc_value, exc_traceback)
	print "Release aborted."
	raw_input()
