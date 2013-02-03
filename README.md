Chunky
======

Chunky is a Minecraft mapping and rendering tool.

Quick links:

* [Wiki](http://chunky.llbit.se)
* [Subreddit](http://www.reddit.com/r/chunky)
* [Dev Blog](http://llbit.se)

Copyright & License
-------------------

Chunky is Copyright (c) 2010-2013, Jesper Öqvist <jesper@llbit.se>

Permission to modify and redistribute is granted under the terms of
the GPLv3 license.
See the file LICENSE.txt for the full license.

Chunky uses the library JOCL by Marco Hutter.
JOCL is covered by the MIT/X11 license.
See the file JOCL.txt for the full license and copyright notice.

Chunky uses the MWC64X random number generator by David Thomas.
MWC64X is covered by the BSD license.
See the file MWC64X.txt for the full license and copyright notice.

Chunky uses the log4j library by the Apche Software Foundation.
The log4j library is covered by the Apache License, version 2.0.
See the file LOG4J.txt for the full license.
See the file NOTICE for the copyright notice.

Building
--------

There is an Apache Ant script provided with Chunky that can be used to build
the project. The default target will compile the code. To build a jar file use
the `jar` or `release` targets.

Running
-------

Chunky uses a lot of memory. In order to give Chunky extra memory to work with
you can run Chunky from the command line with for example

    java -jar Chunky.jar -Xmx4g -Xms512m

This will start with 512 MiB minimum heap size and 4 GiB maximum.

Rendering using the command line
--------------------------------

It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:

    java -jar ~/Chunky/Chunky.jar -render SceneName.cvf

Where SceneName is the name of the scene to render. When run this way Chunky
will not shut down, it will just keep rendering. You can press `Ctrl-C` to
force Chunky to quit. The renders will be saved periodically in the scene
directory.

Additional Information
----------------------

More information about Chunky, including a short getting started guide and
rendering tips are available at the [Chunky Wiki](http://chunky.llbit.se/).
