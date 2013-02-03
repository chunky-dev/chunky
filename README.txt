LICENSE
=======

Chunky is Copyright (c) 2010-2013, Jesper Öqvist <jesper@llbit.se>

Permission to modify and redistribute is granted under the terms of
the GPLv3 license.
See the file license/LICENSE.txt for the full license.

Chunky uses the library JOCL by Marco Hutter.
JOCL is covered by the MIT/X11 license.
See the file license/JOCL.txt for the full license and copyright notice.

Chunky uses the MWC64X random number generator by David Thomas.
MWC64X is covered by the BSD license.
See the file license/MWC64X.txt for the full license and copyright notice.

Chunky uses the log4j library by the Apche Software Foundation.
The log4j library is covered by the Apache License, version 2.0.
See the file license/LOG4J.txt for the full license.
See the file license/NOTICE for the copyright notice.

BUILDING
========

There is an Apache Ant script provided with Chunky that can be used to build
the project. The default target will compile the code. To build a jar file use
the jar or release targets.

RUNNING
=======

Chunky uses a lot of memory. In order to give Chunky extra memory to work with
you can run Chunky from the command line with for example

    java -jar Chunky.jar -Xmx4g -Xms512m

This will start with 512 MiB minimum heap size and 4 GiB maximum.

ADDITIONAL INFORMATION
======================

More information about Chunky, including a short getting started guide and
rendering tips are available at the Chunky Wiki:

    http://chunky.llbit.se/
