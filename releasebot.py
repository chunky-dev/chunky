# coding=utf-8
# Releasebot Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>

# requires PRAW, Launchpadlib

# Release Candidates are not built by this script!
# Snapshots are currently built by proprietary script

import json
import sys
import praw
import re
import io
import traceback
import ftplib
import codecs
import os
from subprocess import call
from getpass import getpass
from datetime import datetime
from string import join
from launchpadlib.launchpad import Launchpad
from os import path

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
				with codecs.open(notes_fn, 'r', encoding='utf-8') as f:
					self.release_notes = f.read().replace('\r', '')
			except:
				print "Error: release notes not found!"
				print "Please edit release_notes-%s.txt!" % self.milestone
				sys.exit(1)

		try:
			# load changelog
			with codecs.open("ChangeLog.txt", 'r', encoding='utf-8') as f:
				f.readline() # skip version line
				while True:
					line = f.readline().rstrip()
					if not line: break
					self.changelog += line + '\n'
		except:
			print "Error: could not read ChangeLog!"
			sys.exit(1)

		if not self.changelog:
			print "Error: ChangeLog is empty!"
			sys.exit(1)

def publish(version):
	if raw_input('Build release? [y/n] ') == "y":
		if call(['cmd', '/c', 'ant', '-Dversion=' + version.full, 'release']) is not 0:
			print "Error: Ant build failed!"
			sys.exit(1)
		if call(['makensis', 'Chunky.nsi']) is not 0:
			print "Error: NSIS failed!"
			sys.exit(1)
	if not version.rc:
		if raw_input('Publish files? [y/n] ') == "y":
			(is_new, exe, zip, jar) = publish_release(version)
			patch_url(version, jar)
			write_markup(version, exe, zip)
		if raw_input('Post release thread? [y/n] ') == "y":
			post_release_thread(version)
		if raw_input('Upload latest.json? [y/n] ') == "y":
			ftpupload(version)
		if raw_input('Update documentation? [y/n] ') == "y":
			update_docs(version)

def ftpupload(version):
	while True:
		user = raw_input('ftp user: ')
		pw = getpass(prompt='ftp password: ')
		try:
			ftp = ftplib.FTP('ftp.llbit.se')
			ftp.login(user, pw)
			break
		except ftplib.error_perm:
			print "Login failed, please try again"
	ftp.cwd('chunkyupdate')
	with open('latest.json', 'rb') as f:
		ftp.storbinary('STOR latest.json', f)
	ftp.cwd('lib')
	with open('build/chunky-core-%s.jar' % version.milestone, 'rb') as f:
		ftp.storbinary('STOR chunky-core-%s.jar' % version.milestone, f)
	ftp.quit()

def update_docs(version):
	docs_dir = '../git/chunky-docs'
	while not path.exists(docs_dir):
		docs_dir = raw_input('documentation repo: ')
	os.mkdir('%s/docs/release/%s' % (docs_dir, version.milestone))
	with codecs.open('build/release_notes-%s.md' % version.milestone,'r',encoding='utf-8') as src:
		with codecs.open('%s/docs/release/%s/release_notes.md' % (docs_dir, version.milestone),'w',encoding='utf-8') as dst:
			dst.write('''Chunky %s
============

''' % version.milestone)
			dst.write(src.read())

def lp_upload_file(version, release, filename, description, content_type, file_type):
	# TODO handle re-uploads
	FILE_TYPES = dict(
		tarball='Code Release Tarball',
		readme='README File',
		release_notes='Release Notes',
		changelog='ChangeLog File',
		installer='Installer file')
	print "Uploading %s..." % filename
	try:
		release_file = release.add_file(
			filename=filename,
			description=description,
			file_content=open('build/' + filename, 'rb').read(),
			content_type=content_type,
			file_type=FILE_TYPES[file_type])
		return 'https://launchpad.net/chunky/%s/%s/+download/%s' \
			% (version.series, version.milestone, filename)
	except:
		exc_type, exc_value, exc_traceback = sys.exc_info()
		print "File upload error:"
		traceback.print_exception(exc_type, exc_value, exc_traceback)
		return None

def publish_release(version):
	if raw_input('Publish to production? [y/n] ') == "y":
		server = 'production'
	else:
		server = 'staging'

	launchpad = Launchpad.login_with('Releasebot', server, 'lpcache')

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
	jar_url = lp_upload_file(
		version,
		release,
		'chunky-core-%s.jar' % version.full,
		'Core Library',
		'application/java-archive',
		'installer')
	assert jar_url
	print jar_url
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
	return (is_new_release, exe_url, zip_url, jar_url)

"output markdown"
def write_markup(version, exe_url, zip_url):
	text = '''###Downloads

* [Windows installer](%s)
* [Cross-platform binaries](%s)
* [Only launcher (win, mac, linux)](http://chunkyupdate.llbit.se/ChunkyLauncher.jar)

###Release Notes

''' % (exe_url, zip_url)
	text += version.release_notes + '''

###ChangeLog

'''
	text += version.changelog
	with codecs.open('build/release_notes-%s.md' % version.milestone, 'w', encoding='utf-8') as f:
		f.write(text)
	with codecs.open('build/version-%s.properties' % version.milestone, 'w', encoding='utf-8') as f:
		f.write('''version=%s
exe.dl.link=%s
zip.dl.link=%s''' % (version.milestone, exe_url, zip_url))

"post reddit release thread"
def post_release_thread(version):
	try:
		with codecs.open('build/release_notes-%s.md' % version.milestone, 'r', encoding='utf-8') as f:
			text = f.read()
	except IOError:
		print "Error: reddit post must be in build/release_notes-%s.md" % version.milestone
		return
	r = praw.Reddit(user_agent='releasebot')
	pw = getpass(prompt='releasebot login: ')
	r.login('releasebot', pw)
	post = r.submit('chunky', 'Chunky %s released!' % version.full,
		text=text)
	post.set_flair('announcement', 'announcement')
	post.sticky()
	print "Submitted Reddit release thread!"

"patch url into latest.json"
def patch_url(version, url):
	print "Patching latest.json"
	j = None
	with open('latest.json', 'r') as f:
		j = json.load(f)
	if not j:
		print 'Error: could not read latest.json'
		sys.exit(1)
	libs = j['libraries']
	core_lib_name = 'chunky-core-%s.jar' % version.full
	patched = False
	for lib in libs:
		if lib['name'] == core_lib_name:
			lib['url'] = url
			patched = True
			break
	if not patched:
		print 'Error: failed to patch url in latest.json: core lib not found!'
		sys.exit(1)
	with open('latest.json', 'w') as f:
		json.dump(j, f)

### MAIN
version = None
do_ftpupload = False
do_update_docs = False
for arg in sys.argv[1:]:
	if arg == '-h' or arg == '--h' or arg == '-help' or arg == '--help':
		print "usage: releasebot [VERSION] [COMMAND]"
		print "commands:"
		print "-ftp    upload latest.json to FTP server"
		print
		print "This utility creates a new release of Chunky"
		print "Required Python libraries: launchpadlib, praw"
		print "Upgrade with pip install --upgrade PKG"
		sys.exit(0)
	elif arg == '-ftp':
		do_ftpupload = True
	elif arg == '-docs':
		do_update_docs = True
	else:
		version = Version(arg)

if version == None:
	version = Version(raw_input('Enter version: '))

if do_ftpupload:
	ftpupload(version)
	sys.exit(0)

if do_update_docs:
	update_docs(version)
	sys.exit(0)

print "Ready to build version %s!" % version.full
try:
	publish(version)
	if raw_input('All done. Push git changes? [y/n] ') == "y":
		call(['git', 'push', 'origin', 'master'])# push version bump commit
		call(['git', 'push', 'origin', version.full])# push version tag
except:
	exc_type, exc_value, exc_traceback = sys.exc_info()
	print "Unexpected error:"
	traceback.print_exception(exc_type, exc_value, exc_traceback)
	print "Release aborted."
	raw_input()
