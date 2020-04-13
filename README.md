<p align="center">
  <a href="https://material-ui.com/" rel="noopener" target="_blank"><img width="100" src="https://raw.githubusercontent.com/llbit/chunky-docs/master/images/logo.png" alt="Chunky logo"></a>
</p>
<h1 align="center">Chunky</h1>
<div align="center">

Chunky is a Minecraft rendering tool that uses Path Tracing to create realistic images of your Minecraft worlds.

[Discord server][15] ·
[Documentation][8] ·
[Troubleshooting][2] ·
[Subreddit][3]
</div>


## Quick start guide

_Prerequisites:_ Chunky requires **Java 8**. It is recommended to have the **64-bit** version if you have a 64-bit operating system (you most likely do). If you haven't installed it yet, [download Windows Offline (64-bit) from here][13].

1. Download [the Chunky Launcher][12] and open it
2. Install the latest version of Chunky by clicking on _Check for Updates_
3. Click on _Launch Chunky_ start rendering your beautiful buildings

If you have any questions, please don't hesitate to reach out via Reddit, Discord or GitHub.

## Frequently Asked Questions

<details>
<summary><strong>Why is there noise/grain/random bright dots in the render?</strong></summary>

This is not a bug, but an unfortunate effect of [the rendering algorithm][9] used in Chunky. Torches and other small light sources cause a very random illumination and it takes a long time to render such light nicely.


You can disable emitters under the Lighting tab in the Render Controls dialog to remove most of the random bright dots. Note that rendering for a longer time will eventually remove the noise, though it may take a very long time.

Another way of removing the noise is using the [Denoiser Plugin][10]. While this can yield good results in most cases, it may distort the image in some cases.
</details>

<details>
<summary><strong>How long does it take to render an image?</strong></summary>

This depends on your CPU, the size of the image and the lighting conditions of the scene you are rendering. You can use the tips from the previous answer to get away with shorter render times.
</details>

<details>
<summary><strong>Why do I see blue question marks or red crosses instead of blocks?</strong></summary>

Chunky renders blue question marks for unsupported blocks. Maybe your Chunky version is outdated or the block is not yet supported. If the latter is the case, please file a bug report.

Red crosses are caused by missing textures. Please ensure that you're using a texturepack for the Minecraft version for the world you are rendering.
</details>

<details>
<summary><strong>Why can I only open 1.12 worlds but not 1.13+ worlds (or vice versa)?</strong></summary>

Minecraft 1.13 introduced a new world format that is incompatible with the old format. Chunky 2 is only compatible with the new world format and Chunky 1 is only compatible with the old world format.

We [have plans][14] to improve this one day. For now, you'll need to use the appropriate Chunky version for your Minecraft version.
</details>

<details>
<summary><strong>Is GPU rendering supported?</strong></summary>

GPU support is not actively being worked on right now. If you'd like to tackle this, PRs are welcome!
</details>

<details>
<summary><strong>Why are mobs not rendered?</strong></summary>

Chunky currently can't render all entities. Future support for rendering more entities is planned, so stay tuned!
</details>

<details>
<summary><strong>Can Chunky render mod blocks?</strong></summary>

No. Due to the vast number of mods, this is not feasible at the moment.
</details>

<details>
<summary><strong>Where can I find good skymaps?</strong></summary>

The [skymaps page][11] has some good links. Another good place is the #skymaps channel on our Discord server.
</details>

<details>
<summary><strong>Chunky keeps freezing or crashing</strong></summary>

Chunky uses a lot of memory. If Chunky has too little memory to work with it
may slow down to a crawl or crash. The memory limit can be increased in the
Chunky Launcher.
</details>

<details>
<summary><strong>Rendering using the command line (Headless Mode)</strong></summary>

It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:

    java -jar chunky.jar -render SceneName


Where SceneName is the name of the scene to render. You can read more about
[headless rendering here.][5]
</details>

<details>
<summary><strong>Shutdown when render completes on Unix-like Systems (Mac OS X, Linux, BSD)</strong></summary>

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
</details>

More information about Chunky, including a short getting started guide and
rendering tips are available at the [Chunky Documentation page][1]. For more insights into Chunky's development, see the [development blog][4].


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


## Copyright & License

Chunky is Copyright (c) 2010-2019, Jesper Öqvist <jesper@llbit.se>

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


[1]: http://chunky.llbit.se
[2]: http://chunky.llbit.se/troubleshooting.html
[3]: http://www.reddit.com/r/chunky
[4]: http://llbit.se
[5]: http://chunky.llbit.se/headless.html
[6]: https://google.github.io/styleguide/javaguide.html
[7]: https://github.com/llbit/chunky-releasetools
[8]: https://lemaik.github.io/chunky/
[9]: https://chunky.llbit.se/path_tracing.html
[10]: https://github.com/leMaik/chunky-denoiser
[11]: https://chunky.llbit.se/skymaps.html
[12]: http://chunkyupdate.lemaik.de/ChunkyLauncher.jar
[13]: https://www.java.com/en/download/manual.jsp
[14]: https://github.com/llbit/chunky/issues/553
[15]: https://discord.gg/VqcHpsF
