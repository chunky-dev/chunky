# Contributing

Thank you for investing your time in contributing to our project! Any contribution you make will be reflected in Chunky; first within our development builds prior to stable release.

Read our [Code of Conduct](CODE_OF_CONDUCT.md) to keep our community approachable and respectable.

In this guide you will get an overview of the contribution workflow from opening an issue, creating a PR, reviewing, and merging the PR.

When contributing to this repository, please consider discussing significant changes you wish to make via an issue or the [#tech](https://discord.com/channels/541221265512464394/545374333883777037) channel on our [Discord server][chunky-discord] prior to making a change. Correcting spelling or grammar mistakes, or fixing minor bugs are not considered significant. A full rewrite, adding new features, etc. can be considered more significant changes.


## New contributor guide

To get an overview of the project, read the [README](README.md).

Chunky is split into four subprojects:

* chunky - the core rendering and GUI project
* lib - common code required by the other projects
* launcher - the launcher
* releasetools - tool used for packaging releases

Here are some resources to help you get started with open source contributions:

- [Finding ways to contribute to open source on GitHub](https://docs.github.com/en/get-started/exploring-projects-on-github/finding-ways-to-contribute-to-open-source-on-github)
- [Set up Git](https://docs.github.com/en/get-started/quickstart/set-up-git)
- [GitHub flow](https://docs.github.com/en/get-started/quickstart/github-flow)
- [Collaborating with pull requests](https://docs.github.com/en/github/collaborating-with-pull-requests)


## Getting started

### Issues

#### Create a new issue

If you spot a problem with the docs, [search if an issue already exists](https://docs.github.com/en/github/searching-for-information-on-github/searching-on-github/searching-issues-and-pull-requests#search-by-the-title-body-or-comments). If a related issue doesn't exist, you can open a [new issue](https://github.com/chunky-dev/chunky/issues/new). 

#### Solve an issue

Scan through our [existing issues](https://github.com/chunky-dev/chunky/issues) to find one that interests you. You can narrow down the search using `labels` as filters. If you are not sure where to start we have labelled some issues as [good first issues](https://github.com/chunky-dev/chunky/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22). As a general rule, we don’t assign issues to anyone. If you find an issue to work on, you are welcome to open a PR with a fix. However, we would request that you double-check what PRs are open and whether another contributor is working on an issue already.

### Make Changes

#### Make changes in the UI

Click on the pencil icon at the top right of any text file (code, markdown, etc.) to make small changes such as a typo, sentence fixes, broken links, etc. Please [create a pull request](#pull-request) for a review. 


#### Make (more significant) changes locally

IntelliJ Community Edition is recommended, get it [here](https://www.jetbrains.com/toolbox-app/)
 - You'll want to download JetBrains Toolbox, and download `IntelliJ IDEA Community Edition` from there

##### Fork the project to your account
Go to https://github.com/chunky-dev/chunky, click on `Fork` (usually in the top right)

##### Clone the project.
with IntelliJ: `File` -> `New` -> `Project from Version Control`
- In the url put `https://github.com/GITHUB_USERNAME/chunky`
- You can also select a location to clone to here.

with GitHub desktop: `Clone a repository` -> select `URL`, paste `https://github.com/GITHUB_USERNAME/chunky`

with CLI: `git clone git@github.com:GITHUB_USERNAME/chunky.git` or (outdated) `git clone https://github.com/GITHUB_USERNAME/chunky`

##### Open the Project
From inside IntelliJ (if you cloned with IntelliJ you can likely skip this step)
- `File` -> `Open` -> Select the first `build.gradle` file within the cloned folder
  (You may also select the folder containing the `build.gradle` file)

You may have to wait some time while IntelliJ indexes the project, and imports it. (The little loading bar in the bottom right) 

##### Select the correct JDK version to use to build the project
Java 17 SDK (e.g. openjdk-17-jdk on Ubuntu):
- `File` -> `Project Structure` -> `Project` (should be the default)
  - If IntelliJ detected your install: select the `SDK` dropdown, select a Java 17 JDK install
  - If not: click `Edit`, then the little `+` at the top, `Add JDK` and find your Java 17 JDK install location (select the main jdk folder).
    - Go back to the `Project` tab, and select the newly added jdk.

Add `se.llbit.util.annotation.Nullable` and `se.llbit.util.annotation.NotNull` to the compiler options of Intellij for IntelliSense recommendations
- `File` -> `Settings` -> `Build, Execution, Deployment` -> `Compiler` (select it, not the dropdown)
  - Click `Configure annotations...`
    - In the `Nullable` tab click the little `+` to add `Nullable (se.llbit.util.annotation)`
    - In the `NotNull` tab click the little `+` to add `NotNull (se.llbit.util.annotation.)`
    - Intellij must be restarted to update IntelliSense

##### Create a new branch for your change
In the bottom right of IntelliJ, there is a New Branch button, by default it will say `master`, select it, at the top select `New Branch`, and name it after what you're intending to fix. (ideally something very short, no more than a few words). eg: `fix_scene_tab_typo` or `fix-scene-tab-typo`  is fine.
 - Make sure the `Checkout branch` box is checked when creating a new branch (to actually change to it once it's created)

##### Make your change
Some useful IntelliJ keybindings to help you navigate around:
 - `Ctrl + N`, Find a file (class) by name

##### Commit your changes
After making your changes, select the `Commit` button (looks like a green tick) in the top right
Select all the changes you want to make, and add a description of what you changed.

### Testing
Prior to making a Pull Request please test your changes. Within IntelliJ, you can build and run Chunky by clicking on the green arrow next to `chunky\src\java\se.llbit\chunky\main\Chunky.java:line64` assuming you have got IntelliJ setup correctly. At the time of writing, Chunky's gradlew version is 7.4.

To build Chunky externally, run the `gradlew` script in the project root directory. Gradle is set-up with a few main tasks:

* build - Build Chunky, documentation, and run tests.
* release - Build and save files ready for a release to a Chunky update site. Outputs to `build/release`
* buildReleaseJar - Build an installer JAR. Outputs to `build/installer`
* docs - Build the documentation. Outputs to `build/docs`
* install - Create a publishable maven repository for Chunky core. Outputs to `build/maven`
* clean - Cleans the project. Removes old builds.

A custom version can be specified with `-PnewVersion="<version>"`. A custom prerelease tag can be specified with `-PprereleaseTag="<tag>"`. The default version is in the format: `{major}.{minor}.{patch}-{tag (DEV)}.{commits since last tag}.g{git hash of commit}`


### Pull Request

When you're finished with the changes, create a pull request, also known as a PR.
- Provide a short but descriptive name and for more complex changes an extended description.
- Don't forget to [link PR to issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) if you are solving one.
- Enable the checkbox to [allow maintainer edits](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/allowing-changes-to-a-pull-request-branch-created-from-a-fork) so the branch can be updated for a merge.
Once you submit your PR, a team member will review your proposal. We may ask questions or request for additional information.
- We may ask for changes to be made before a PR can be merged, either using [suggested changes](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/incorporating-feedback-in-your-pull-request) or pull request comments. You can apply suggested changes directly through the UI. You can make any other changes in your fork, then commit them to your branch.
- As you update your PR and apply changes, mark each conversation as [resolved](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/commenting-on-a-pull-request#resolving-conversations).
- If you run into any merge issues, checkout this [git tutorial](https://lab.github.com/githubtraining/managing-merge-conflicts) to help you resolve merge conflicts and other issues.

If you get stuck at any point along your PR journey, do not hesitate to ask for help or opinions on Discord or in the PR's discussion. We appreciate your contribution and want it to be merged as much as you do. 😉 

### Your PR is merged!

Congratulations :tada::tada: The Chunky team thanks you :sparkles:. 

Once your PR is merged, your contributions will be publicly available in the next Chunky snapshot and eventually ship in a stable release.

# Attribution

This Contributing guide is adapted from the [GitHub docs](https://docs.github.com/en) [contributing guide](https://github.com/github/docs/blob/main/CONTRIBUTING.md).

[chunky-discord]: https://discord.gg/VqcHpsF

