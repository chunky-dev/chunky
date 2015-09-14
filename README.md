Chunky
======

Chunky is a Minecraft mapping and rendering tool.

Quick links:

* [Wiki](http://chunky.llbit.se)
* [Subreddit](http://www.reddit.com/r/chunky)
* [Development Blog](http://llbit.se)

Copyright & License
-------------------

Chunky is Copyright (c) 2010-2014, Jesper Öqvist <jesper@llbit.se>

Permission to modify and redistribute is granted under the terms of
the GPLv3 license. See the file license/LICENSE.txt for the full license.

Chunky uses the following 3rd party libraries:

* **JOCL by Marco Hutter.**
  JOCL is covered by the MIT/X11 license.
See the file license/JOCL.txt for the full license and copyright notice.
* **MWC64X by David Thomas.**
  MWC64X is covered by the Modified BSD License.
See the file license/MWC64X.txt for the full license and copyright notice.
* **Markdown by John Gruber.**
  Markdown is covered by the Modified BSD License.
See the file license/Markdown.txt for the full license and copyright notice.
* **Apache Commons Math library by the Apache Software Foundation.**
  The library is covered by the Apache License, version 2.0.
See the file license/Apache-2.0.txt for the full license text.
See the file license/commons-math.txt for the copyright notices.

Launching Chunky
----------------

Chunky uses a lot of memory. If Chunky has too little memory to work with it
may slow down to a crawl or crash. The memory limit can be increased in the
Chunky Launcher.

Rendering using the command line (Headless Mode)
------------------------------------------------

It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:

    java -jar chunky.jar -render SceneName

Where SceneName is the name of the scene to render. You can read more about
[headless rendering on the Wiki](http://chunky.llbit.se/headless.html).

Shutdown when render completes on Unix-like Systems (Mac OS X, Linux, BSD)
--------------------------------------------------------------------------

In the Advanced tab of the Render Controls window, you can check the checkbox
that says "Shutdown when render completes" to shut down your computer when the
set SPP target is reached.  (This can be toggled while rendering.)

On Unix-like systems, the `shutdown` terminal command has to be run as root
using `sudo`.  For various reasons, Chunky cannot prompt for the password to
`sudo`, so you must configure your system to allow the command to run without a
password.

Open a terminal (such as bash) and run `sudo visudo`, providing your password.

Add the following line at the end of the file: (press Insert to type)

    %user_name ALL=(ALL) NOPASSWD: /sbin/shutdown

Replace `user_name` with your username.

Press Escape, then type ":wq".

You may need to restart or log out and in for this to take effect.

This will only allow `sudo shutdown` to run without a password; no other
commands run with `sudo` will be affected.

Hacking on Chunky
-----------------

To build Chunky you will need Apache Ant and Perl. Chunky is built using an
Apache Ant build script found in the project root directory. The `dist` target
builds Chunky and outputs binaries to the `build` directory.

Chunky is split into four projects:

* **chunky** - main Chunky project
* **lib** - common code required by the other projects
* **launcher** - the launcher
* **releasetools** - Ant task for building releases

If you want to hack on Chunky itself you will need to set up two projects.
Eclipse project files for `chunky` and `lib` are included. It should be
fairly simple to set those up in Eclipse. You will need to add a dependency
from `chunky` to `lib`.


###Code Style

The standard Eclipse Java style is used, with slight modifications. If you
want to contribute code to Chunky please make your code look similar to the
rest of the code.

Additional Information
----------------------

More information about Chunky, including a short getting started guide and
rendering tips are available at the [Chunky Wiki](http://chunky.llbit.se/).
