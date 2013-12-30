Chunky
======

Chunky is a Minecraft mapping and rendering tool.

Quick links:

* [Wiki](http://chunky.llbit.se)
* [Subreddit](http://www.reddit.com/r/chunky)
* [Development Blog](http://llbit.se)

Copyright & License
-------------------

Chunky is Copyright (c) 2010-2013, Jesper Öqvist <jesper@llbit.se>

Permission to modify and redistribute is granted under the terms of
the GPLv3 license. See the file license/LICENSE.txt for the full license.

Chunky uses the following 3rd party libraries:

* **JOCL by Marco Hutter.**
  JOCL is covered by the MIT/X11 license.
See the file license/JOCL.txt for the full license and copyright notice.
* **MWC64X by David Thomas.**
  MWC64X is covered by the Modified BSD License.
See the file license/MWC64X.txt for the full license and copyright notice.
* **log4j by the Apche Software Foundation.**
  The log4j library is covered by the Apache License, version 2.0.
See the file license/Apache-2.0.txt for the full license text.
See the file license/log4j.txt for the copyright notice.
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
may slow down to a crawl or crash. You can give increase the memory limit in
the Chunky Launcher, or you can run Chunky from the command line with a command
like this:

    java -Xmx4g -jar chunky.jar

The above command starts Chunky with a memory limit of 4 gigabyte. Change the
4 in `-Xmx4g` to the number of gigabyte you want Chunky to use at most.

Rendering using the command line (Headless Mode)
------------------------------------------------

It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:

    java -jar chunky.jar -render SceneName

Where SceneName is the name of the scene to render. You can read more about
[headless rendering on the Wiki](http://chunky.llbit.se/index.php?title=Headless_Rendering).

Hacking on Chunky
-----------------

Chunky is split into four projects:

* **chunky** - main Chunky project
* **lib** - common code required by Chunky and the launcher
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
