# Chunky

Chunky is a Minecraft mapping and rendering tool.

Quick links:

* [Documentation][1]
* [Troubleshooting][2]
* [Subreddit][3]
* [Development Blog][4]


## Copyright & License

Chunky is Copyright (c) 2010-2017, Jesper Öqvist <jesper@llbit.se>

Permission to modify and redistribute is granted under the terms of
the GPLv3 license. See the file `LICENSE` for the full license.

Chunky uses the following 3rd party libraries:

* **Markdown by John Gruber.**
  Markdown is covered by the Modified BSD License.
See the file `licenses/Markdown.txt` for the full license and copyright notice.
* **Apache Commons Math library by the Apache Software Foundation.**
  The library is covered by the Apache License, version 2.0.
See the file `licenses/Apache-2.0.txt` for the full license text.
See the file `licenses/commons-math.txt` for the copyright notices.


## Launching Chunky

Chunky uses a lot of memory. If Chunky has too little memory to work with it
may slow down to a crawl or crash. The memory limit can be increased in the
Chunky Launcher.


## Rendering using the command line (Headless Mode)

It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:

    java -jar chunky.jar -render SceneName


Where SceneName is the name of the scene to render. You can read more about
[headless rendering here.][5]


## Shutdown when render completes on Unix-like Systems (Mac OS X, Linux, BSD)

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

Press Escape, then type `:wq`.

You may need to restart or log out and in for this to take effect.

This will only allow `sudo shutdown` to run without a password; no other
commands run with `sudo` will be affected.


## Hacking on Chunky

To build Chunky, run the `gradlew` script in the project root directory:

   ./gradlew jar

This just builds the core libraries. To build an installable file takes
a bit more work; [refer to this repository][7].

Chunky is split into four subprojects:

* **chunky** - the core rendering and GUI project
* **lib** - common code required by the other projects
* **launcher** - the launcher
* **releasetools** - tool used for packaging releases

If you want to hack on Chunky itself you will need to load the `chunky` and
`lib` directories in your favorite editor. If available, use a Gradle project
import option.


### Code Style

The [Google Java style guide][6] should be followed for new code (2 spaces for
indentation, no tabs). If you want to contribute code to Chunky please make
your code look similar to the rest of the code, and refer to the style guide
when in doubt.


## Additional Information

More information about Chunky, including a short getting started guide and
rendering tips are available at the [Chunky Documentation page.][1]

[1]: http://chunky.llbit.se
[2]: http://chunky.llbit.se/troubleshooting.html
[3]: http://www.reddit.com/r/chunky
[4]: http://llbit.se
[5]: http://chunky.llbit.se/headless.html
[6]: https://google.github.io/styleguide/javaguide.html
[7]: https://github.com/llbit/chunky-releasetools
