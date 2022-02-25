<p align="center">
  <a href="http://chunky.llbit.se" rel="noopener" target="_blank"><img width="100" src="https://raw.githubusercontent.com/llbit/chunky-docs/master/images/logo.png" alt="Chunky logo"></a>
</p>
<h1 align="center">Chunky</h1>
<div align="center">

Chunky is a Minecraft rendering tool that uses Path Tracing to create realistic images of your Minecraft worlds.

[Discord server][chunky-discord] ·
[Documentation][chunky-dev] ·
[Troubleshooting][chunky-dev-troubleshooting] ·
[Subreddit][chunky-reddit]
</div>


## Quick start guide

_Prerequisites:_ Chunky requires **Java 17**. It is recommended to have the **64-bit** version if you have a 64-bit operating system (you most likely do). If you haven't installed Java, you can [download it from here, selecting Temurin 17 LTS][JDK]. You also need (Open) **JavaFX 17** LTS, which you can [download from here][jfx] and extract it; We cover valid extraction locations and manually adding the JavaFX module under the [Troubleshooting article][chunky-dev-troubleshooting].

1. Download [the Chunky Launcher][chunkylauncherJAR] and open it
2. Install the latest version of Chunky by clicking on _Check for Updates_
3. Click on _Launch Chunky_ start rendering your beautiful buildings

For guides and more information please checkout the [Documentation][chunky-dev]. If you have any questions, please don't hesitate to reach out via [Reddit][chunky-reddit], [Discord][chunky-discord], or GitHub.


## Frequently Asked Questions

<details>
<summary><strong>Why is there noise/grain/random bright dots in the render?</strong></summary>

> This is not a bug, but an unfortunate effect of [the rendering algorithm][chunky-dev-rendering] used in Chunky. Torches and other small light sources cause a very random illumination and it takes a long time to render such light nicely.
> 
> You can disable emitters under the Lighting tab in the Render Controls dialog to remove most of the random bright dots. Note that rendering for a longer time will eventually remove the noise, though it may take a very long time.
> 
> Another way of removing the noise is using the [Denoiser Plugin][chunky-denoiser]. While this can yield good results in most cases, it may distort the image in some cases.
</details>

<details>
<summary><strong>How long does it take to render an image?</strong></summary>

> This depends on your CPU, the size of the image and the lighting conditions of the scene you are rendering. You can use the tips from the previous answer to get away with shorter render times.
</details>

<details>
<summary><strong>Why do I see blue question marks or red crosses instead of blocks?</strong></summary>

> Chunky renders blue question marks for unsupported blocks. Maybe your Chunky version is outdated or the block is not yet supported. If the latter is the case, please file a bug report.
> 
> Red crosses are caused by missing textures. Please ensure that you're using a texturepack for the Minecraft version for the world you are rendering.
</details>

<details>
<summary><strong>Which Minecraft versions are supported?</strong></summary>

> Starting with Chunky 2.4.0, we support Minecraft 1.2-1.17 worlds and Cubic Chunks for Minecraft 1.10-1.12 worlds.
> 
> We typically add new blocks shortly after a new Minecraft snapshot is released. Use the latest Chunky snapshot to render them until a new Chunky version is released.
</details>

<details>
<summary><strong>Is GPU rendering supported?</strong></summary>

> There is a work-in-progress [OpenCL plugin for Chunky][chunky-opencl]. If you'd like to help with this, PRs are welcome!
</details>

<details>
<summary><strong>Why are mobs not rendered?</strong></summary>

> Chunky currently can't render all entities. Future support for rendering more entities is planned, so stay tuned!
</details>

<details>
<summary><strong>Can Chunky render mod blocks?</strong></summary>

> No. Due to the vast number of mods, this is not feasible at the moment. However support for JSON-defined block models is being worked on.
</details>

<details>
<summary><strong>Where can I find good skymaps?</strong></summary>

> The [skymaps page][chunky-dev-skymaps] has some good links. Another good place is the #skymaps channel on our [Discord server][chunky-discord].
</details>

<details>
<summary><strong>Chunky keeps freezing or crashing</strong></summary>

> Chunky uses a lot of memory. If Chunky has too little memory to work with it may slow down to a crawl or crash. The memory limit can be increased in the Chunky Launcher.
</details>

<details>
<summary><strong>Rendering using the command line (Headless Mode)</strong></summary>

> It is possible to render a scene from the command line. First set up a scene
using the GUI. Don't forget to save the scene. Then run the following on the
command line:
> 
>     java -jar chunky.jar -render SceneName
>
> Where SceneName is the name of the scene to render. You can read more about [headless rendering here.][chunky-dev-headless]
</details>

<details>
<summary><strong>Shutdown when render completes on Unix-like Systems (Mac OS X, Linux, BSD)</strong></summary>

> In the Advanced tab of the Render Controls window, you can check the checkbox
that says "Shutdown when render completes" to shut down your computer when the
set SPP target is reached.  (This can be toggled while rendering.)
> 
> On Unix-like systems, the `shutdown` terminal command has to be run as root
using `sudo`.  For various reasons, Chunky cannot prompt for the password to
`sudo`, so you must configure your system to allow the command to run without a
password.
> 
> Open a terminal (such as bash) and run `sudo visudo`, providing your password.
> 
> Add the following line at the end of the file: (press Insert to type)
> 
>     %user_name ALL=(ALL) NOPASSWD: /sbin/shutdown
>
> Replace `user_name` with your username.
> 
> Press Escape, then type `:wq`.
> 
> You may need to restart or log out and in for this to take effect.
> 
> This will only allow `sudo shutdown` to run without a password; no other
commands run with `sudo` will be affected.
</details>

<details>
<summary><strong>What about the Chunky SpigotMC plugin?</strong></summary>

> The [Chunky SpigotMC plugin](https://www.spigotmc.org/resources/chunky.81534/) is an unfortunate name collision and is unrelated to this project. Chunky (SpigotMC plugin) is a handy plugin to quickly pre-generate server chunks should you need that functionality. You can also find [Chunky (SpigotMC Plugin) on GitHub](https://github.com/pop4959/Chunky).
</details>

More information about Chunky, including a short getting started guide and
rendering tips are available at the [Chunky Documentation page][chunky-dev]. For more insights into Chunky's development, keep an eye on the [Discord][chunky-discord]; messages from contributors can sometimes give you insight into what everyone is working on.



## Hacking on Chunky

To build Chunky, run the `gradlew` script in the project root directory: `./gradlew jar`

This just builds the core libraries. Building an installable file takes
a bit more work; [refer to this repository][chunky-releasetools].

Chunky is split into four subprojects:

* **chunky** - the core rendering and GUI project
* **lib** - common code required by the other projects
* **launcher** - the launcher
* **releasetools** - tool used for packaging releases

If you want to hack on Chunky itself you will need to load the `chunky` and
`lib` directories in your favorite editor. If available, use a Gradle project
import option.


### Code Style

The [Google Java style guide][Google-styleguide] should be followed for new code (2 spaces for
indentation, no tabs). If you want to contribute code to Chunky please make
your code look similar to the rest of the code, and refer to the style guide
when in doubt.


## Copyright & License

Chunky is Copyright (c) 2010-2021, Jesper Öqvist <jesper@llbit.se> and [Chunky Contributors][chunky-contributors]. 

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
* **FastUtil by Sebastiano Vigna**.
  FastUtil is covered by Apache License, version 2.0. 
See the file `licenses/Apache-2.0.txt` for the full license text.
See the file `licenses/fast-util.txt` for the copyright notice.
* **Simplex noise implementation by Stefan Gustavson and Keijiro Takahashi**.
Released in the public domain.

## Special Thanks

![YourKit](https://www.yourkit.com/images/yklogo.png)
---
YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/%22%3EYourKit"> Java Profiler</a>,
<a href="https://www.yourkit.com/.net/profiler/%22%3EYourKit"> .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/%22%3EYourKit"> YouMonitor</a>.

![](IntelliJ.jpg)
---
JetBrains supports core contributors of non-commercial open source projects by providing them with professional coding tools free of charge. JetBrains is the creator of <a href="https://www.jetbrains.com/idea/"> JRE IDE</a>, <a href="https://kotlinlang.org/"> Kotlin</a>, and much more.

[chunky-dev]: https://chunky-dev.github.io/docs/
[chunky-dev-troubleshooting]: https://chunky-dev.github.io/docs/faq/troubleshooting/
[chunky-reddit]: http://www.reddit.com/r/chunky
[chunky-dev-headless]: https://chunky-dev.github.io/docs/user_interface/headless/
[Google-styleguide]: https://google.github.io/styleguide/javaguide.html
[chunky-releasetools]: https://github.com/llbit/chunky-releasetools
[chunky-contributors]: https://github.com/chunky-dev/chunky/graphs/contributors
[chunky-dev-rendering]: https://chunky-dev.github.io/docs/rendering/
[chunky-denoiser]: https://github.com/leMaik/chunky-denoiser
[chunky-dev-skymaps]: https://chunky-dev.github.io/docs/rendering/skymaps/
[chunkylauncherJAR]: http://chunkyupdate.lemaik.de/ChunkyLauncher.jar
[JDK]: https://adoptium.net/
[chunky-opencl]: https://github.com/alexhliu/ChunkyClPlugin
[chunky-discord]: https://discord.gg/VqcHpsF
[jfx]: https://gluonhq.com/products/javafx/
