# coding=utf-8
# SHIPIT Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>

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
import platform
from subprocess import call, Popen, PIPE
from getpass import getpass
from datetime import datetime
from string import join
from launchpadlib.launchpad import Launchpad
from os import path
from shutil import copyfile

class Credentials:
	initialized = False
	credentials = {}

	def init(self):
		if not self.initialized and path.exists('credentials.gpg'):
			proc = Popen(cmd(['gpg', '--decrypt',
				'credentials.gpg']), stdout=PIPE)
			creds = proc.communicate()[0]
			self.credentials = json.loads(creds)
		self.initialized = True


	def get(self, key):
		self.init()
		if key not in self.credentials:
			self.credentials[key] = raw_input(key+': ')
			self.save()
		return self.credentials[key]

	def getpass(self, key):
		if key not in self.credentials:
			self.credentials[key] = getpass(prompt=key+': ')
			self.save()
		return self.credentials[key]

	def remove(self, key):
		del self.credentials[key]
		self.save()

	def save(self):
		proc = Popen(cmd(['gpg', '--output', 'credentials.gpg', '-r', 'jesper@llbit.se', '--encrypt']), stdin=PIPE)
		proc.communicate(json.dumps(self.credentials))
		if proc.returncode is not 0:
			print "Warning: failed to encrypt credentials!"

class Version:
	regex = re.compile('^(\d+\.\d+(\.\d+)?)(-[a-zA-Z]*\.?\d*)?$')
	full = ''
	milestone = ''
	suffix = ''
	series = ''
	changelog = ''
	release_notes = ''

	def __init__(self, version):
		self.full = version
		r = self.regex.match(version)
		if not r:
			print "Invalid version name: %s (expected e.g. 1.2.13-alpha1)" % version
			sys.exit(1)
		self.milestone = r.groups()[0]
		self.suffix = r.groups()[2]
		self.series = join(self.milestone.split('.')[:2], '.')
		notes_fn = 'release_notes-%s.txt' % self.milestone
		notes_fn2 = 'release_notes-%s.txt' % self.full
		if not path.exists(notes_fn):
			if path.exists(notes_fn2):
				notes_fn = notes_fn2
			else:
				print "Error: release notes not found!"
				print "Please edit release_notes-%s.txt!" % self.milestone
				sys.exit(1)
		if not path.exists(notes_fn2):
			copyfile(notes_fn, notes_fn2)
		else:
			notes_fn = notes_fn2
		try:
			with codecs.open(notes_fn, 'r', encoding='utf-8') as f:
				self.release_notes = f.read().replace('\r', '')
		except:
			print "Error: failed to read release notes!"
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

	def jar_file(self):
		return 'chunky-core-%s.jar' % self.full

	def tar_file(self):
		return 'chunky-%s.tar.gz' % self.full

	def zip_file(self):
		return 'Chunky-%s.zip' % self.full

	def exe_file(self):
		return 'Chunky-%s.exe' % self.full

	def sign_files(self):
		sign_file(self.jar_file())
		sign_file(self.zip_file())
		sign_file(self.tar_file())
		sign_file(self.exe_file())

def on_win():
	return platform.system() == 'Windows'

def cmd(cmd):
	if on_win():
		return ['cmd', '/c'] + cmd
	else:
		return cmd

def sign_file(filename):
	while True:
		passphrase = credentials.getpass('gpg passphrase')
		print "Signing build/" + filename
		proc = Popen(cmd(['gpg', '--passphrase-fd', '0', '--detach-sig', 'build/' + filename]), stdin=PIPE)
		if on_win():
			proc.communicate(passphrase + "\r\n")
		else:
			proc.communicate(passphrase + "\n")
		if proc.returncode is not 0:
			credentials.remove('gpg passphrase')
			print "Failed to sign file: " + filename
			if raw_input('Retry? [y/N] ') == 'y':
				continue
			else:
				sys.exit(1)
		break

def print_prerelease_checklist():
	print "Pre-Release Checklist:"
	print "    * Update PRAW (pip install praw --upgrade)"
	print "    * Update Launchpadlib"
	print "    * Edit release notes (check for typos)"
	print "    * Update ChangeLog (check for typos)"

def build_release(version):
	if version.suffix:
		print "Error: non-release version string speicifed (remove suffix)"
		sys.exit(1)
	print_prerelease_checklist()
	print "Ready to build version %s!" % version.full
	if raw_input('Build release? [y/N] ') == 'y':
		if call(cmd(['ant', '-Dversion=' + version.full, 'release'])) is not 0:
			print "Error: Ant build failed!"
			sys.exit(1)
		if call(['makensis', 'Chunky.nsi']) is not 0:
			print "Error: NSIS build failed!"
			sys.exit(1)
		version.sign_files()
	if raw_input('Publish to Launchpad? [y/N] ') == 'y':
		(is_new, exe, zip, jar) = publish_launchpad(version)
		patch_url(version, jar)
		write_release_notes(version, exe, zip)
	if raw_input('Publish to FTP? [y/N] ') == 'y':
		publish_ftp(version)
	if raw_input('Post release thread? [y/N] ') == 'y':
		post_release_thread(version)
	if raw_input('Update documentation? [y/N] ') == 'y':
		update_docs(version)

def build_snapshot(version):
	if not version.suffix:
		print "Error: non-snapshot version string speicifed (add suffix)"
		sys.exit(1)
	print_prerelease_checklist()
	print "Ready to build snapshot %s!" % version.full
	if raw_input('Build snapshot? [y/N] ') == "y":
		if call(['git', 'tag', '-a', version.full, '-m', 'Snapshot build']) is not 0:
			print "Error: git tag failed!"
			sys.exit(1)
		if call(cmd(['ant', '-Ddebug=true', 'dist'])) is not 0:
			print "Error: Ant build failed!"
			sys.exit(1)
	if raw_input('Publish snapshot to FTP? [y/N] ') == "y":
		publish_snapshot_ftp(version)
	if raw_input('Post snapshot thread? [y/N] ') == "y":
		post_snapshot_thread(version)

def reddit_login():
	while True:
		user = credentials.get('reddit user')
		pw = credentials.getpass('reddit password')
		try:
			r = praw.Reddit(user_agent=user)
			r.login('releasebot', pw)
			return r
		except praw.errors.InvalidUserPass:
			credentials.remove('reddit user')
			credentials.remove('reddit password')
			print "Login failed, please try again"

def ftp_login():
	while True:
		user = credentials.get('ftp user')
		pw = credentials.getpass('ftp password')
		try:
			ftp = ftplib.FTP('ftp.llbit.se')
			ftp.login(user, pw)
			return ftp
		except ftplib.error_perm:
			credentials.remove('ftp user')
			credentials.remove('ftp password')
			print "Login failed, please try again"

def publish_snapshot_ftp(version):
	ftp = ftp_login()
	ftp.cwd('chunkyupdate')
	with open('build/ChunkyLauncher.jar', 'rb') as f:
		ftp.storbinary('STOR ChunkyLauncher.jar', f)
	with open('latest.json', 'rb') as f:
		ftp.storbinary('STOR snapshot.json', f)
	ftp.cwd('lib')
	with open('build/chunky-core-%s.jar' % version.full, 'rb') as f:
		ftp.storbinary('STOR chunky-core-%s.jar' % version.full, f)
	ftp.quit()

def publish_ftp(version):
	ftp = ftp_login()
	ftp.cwd('chunkyupdate')
	with open('build/ChunkyLauncher.jar', 'rb') as f:
		ftp.storbinary('STOR ChunkyLauncher.jar', f)
	with open('latest.json', 'rb') as f:
		ftp.storbinary('STOR latest.json', f)
	ftp.cwd('lib')
	with open('build/chunky-core-%s.jar' % version.full, 'rb') as f:
		ftp.storbinary('STOR chunky-core-%s.jar' % version.full, f)
	ftp.quit()

def publish_launcher(version):
	ftp = ftp_login()
	ftp.cwd('chunkyupdate')
	with open('build/ChunkyLauncher.jar', 'rb') as f:
		ftp.storbinary('STOR ChunkyLauncher.jar', f)
	ftp.quit()

def update_docs(version):
	docs_dir = '../git/chunky-docs'
	while not path.exists(docs_dir):
		docs_dir = raw_input('documentation repo: ')
	copyfile('build/version-%s.properties' % version.full, path.join(docs_dir, 'version.properties'))
	version_dir = '%s/docs/release/%s' % (docs_dir, version.full)
	if not path.exists(version_dir):
		os.mkdir(version_dir)
	with codecs.open('build/release_notes-%s.md' % version.full,'r',encoding='utf-8') as src:
		with codecs.open('%s/docs/release/%s/release_notes.md' % (docs_dir, version.full),'w',encoding='utf-8') as dst:
			dst.write('''Chunky %s
============

''' % version.full)
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
		signature_fn = filename + '.sig'
		release_file = release.add_file(
			filename=filename,
			description=description,
			file_content=open('build/' + filename, 'rb').read(),
			signature_content=open('build/' + signature_fn, 'rb').read(),
			signature_filename=signature_fn,
			content_type=content_type,
			file_type=FILE_TYPES[file_type])
		return 'https://launchpad.net/chunky/%s/%s/+download/%s' \
			% (version.series, version.milestone, filename)
	except:
		exc_type, exc_value, exc_traceback = sys.exc_info()
		print "File upload error:"
		traceback.print_exception(exc_type, exc_value, exc_traceback)
		return None

def check_file_exists(filename):
	if not path.exists('build/' + filename):
		print "Error: required artifact %s not found!" % filename
		sys.exit(1)
	if not path.exists('build/' + filename + '.sig'):
		print "Error: required signature for %s not found!" % filename
		sys.exit(1)

def publish_launchpad(version):
	# check that required files exist
	check_file_exists(version.jar_file())
	check_file_exists(version.tar_file())
	check_file_exists(version.zip_file())
	check_file_exists(version.exe_file())
	if raw_input('Publish to production? [y/N] ') == "y":
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
		version.jar_file(),
		'Core Library',
		'application/java-archive',
		'installer')
	assert jar_url
	print jar_url
	tarball_url = lp_upload_file(
		version,
		release,
		version.tar_file(),
		'Source Code',
		'application/x-tar',
		'tarball')
	assert tarball_url
	print tarball_url
	zip_url = lp_upload_file(
		version,
		release,
		version.zip_file(),
		'Binaries',
		'application/zip',
		'installer')
	assert zip_url
	print zip_url
	exe_url = lp_upload_file(
		version,
		release,
		version.exe_file(),
		'Windows Installer',
		'application/octet-stream',
		'installer')
	assert exe_url
	print exe_url
	return (is_new_release, exe_url, zip_url, jar_url)

"output markdown"
def write_release_notes(version, exe_url, zip_url):
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
	with codecs.open('build/release_notes-%s.md' % version.full, 'w', encoding='utf-8') as f:
		f.write(text)
	with codecs.open('build/version-%s.properties' % version.full, 'w', encoding='utf-8') as f:
		f.write('''version=%s
exe.dl.link=%s
zip.dl.link=%s''' % (version.milestone, exe_url, zip_url))

"post reddit release thread"
def post_release_thread(version):
	try:
		with codecs.open('build/release_notes-%s.md' % version.full, 'r', encoding='utf-8') as f:
			text = f.read()
	except IOError:
		print "Error: reddit post must be in build/release_notes-%s.md" % version.full
		return
	r = reddit_login()
	post = r.submit('chunky', 'Chunky %s released!' % version.full,
		text=text)
	post.set_flair('announcement', 'announcement')
	post.sticky()
	print "Submitted release thread!"

"post reddit release thread"
def post_snapshot_thread(version):
	r = reddit_login()
	post = r.submit('chunky', 'Chunky Snapshot %s' % version.full,
		text='''###Snapshot %s

A new snapshot for Chunky is now available. The snapshot is mostly untested,
so please make sure to backup your scenes before using it.

[The snapshot can be downloaded using the launcher.](http://chunky.llbit.se/snapshot.html)

###Notes

*These are preliminary release notes for upcoming features (which may not be fully functional).*

%s

###ChangeLog

''' % (version.full, version.release_notes) + version.changelog)
	post.set_flair('announcement', 'announcement')
	print "Submitted snapshot thread!"

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
	patched = False
	for lib in libs:
		if lib['name'] == version.jar_file():
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
options = {
	'ftp': False,
	'docs': False,
	'snapshot': False,
	'sign': False,
	'launcher': False
}
do_ftpupload = False
do_update_docs = False
for arg in sys.argv[1:]:
	if arg == '-h' or arg == '--h' or arg == '-help' or arg == '--help':
		print "usage: SHIPIT [VERSION] [COMMAND]"
		print "commands:"
		print "    -ftp         upload latest.json to FTP server"
		print "    -docs        update documentation"
		print "    -snapshot    build snapshot instead of release"
		print "    -launcher    upload the launcher to the FTP server"
		print
		print "This utility creates a new release of Chunky"
		print "Required Python libraries: launchpadlib, PRAW"
		print "Upgrade with >pip install --upgrade <PKG>"
		sys.exit(0)
	else:
		if arg.startswith('-'):
			matched = False
			for key in options.keys():
				if arg == '-'+key:
					options[key] = True
					matched = True
					break
			if not matched:
				print "Error: unknown command: %s" % arg
				sys.exit(1)
		elif version is None:
			version = Version(arg)
		else:
			print "Error: redundant argument: %s" % arg
			sys.exit(1)

try:
	credentials = Credentials()

	if options['launcher']:
		publish_launcher(version)
		sys.exit(0)

	if version == None:
		version = Version(raw_input('Enter version: '))

	if options['ftp']:
		publish_ftp(version)
	elif options['docs']:
		update_docs(version)
	elif options['snapshot']:
		build_snapshot(version)
	elif options['sign']:
		# test cryptosigning
		version.sign_files()
	else:
		build_release(version)
		if raw_input('Push git release commit? [y/N] ') == "y":
			call(['git', 'push', 'origin', 'master'])# push version bump commit
			call(['git', 'push', 'origin', version.full])# push version tag
		print "All done."
except SystemExit:
	raise
except:
	exc_type, exc_value, exc_traceback = sys.exc_info()
	print "Unexpected error:"
	traceback.print_exception(exc_type, exc_value, exc_traceback)
	print "Release aborted."
	raw_input()

