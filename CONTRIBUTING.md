# Contributing

Thank you for investing your time in contributing to our project! Any contribution you make will be reflected in Chunky; first within our development builds prior to stable release.

Read our [Code of Conduct](CODE_OF_CONDUCT.md) to keep our community approachable and respectable.

In this guide you will get an overview of the contribution workflow from opening an issue, creating a PR, reviewing, and merging the PR.

When contributing to this repository, please consider discussing significant changes you wish to make via an issue or the [#tech](https://discord.com/channels/541221265512464394/545374333883777037) channel on our [Discord server][chunky-discord] prior to making a change. Correcting spelling or grammar mistakes, or fixing minor bugs are not considered significant. A full rewrite, adding new features, etc. Can be considered more significant changes.


## New contributor guide

To get an overview of the project, read the [README](README.md). Here are some resources to help you get started with open source contributions:

- [Finding ways to contribute to open source on GitHub](https://docs.github.com/en/get-started/exploring-projects-on-github/finding-ways-to-contribute-to-open-source-on-github)
- [Set up Git](https://docs.github.com/en/get-started/quickstart/set-up-git)
- [GitHub flow](https://docs.github.com/en/get-started/quickstart/github-flow)
- [Collaborating with pull requests](https://docs.github.com/en/github/collaborating-with-pull-requests)

## Getting started

### Issues

#### Create a new issue

If you spot a problem with the docs, [search if an issue already exists](https://docs.github.com/en/github/searching-for-information-on-github/searching-on-github/searching-issues-and-pull-requests#search-by-the-title-body-or-comments). If a related issue doesn't exist, you can open a [new issue](https://github.com/chunky-dev/chunky/issues/new). 

#### Solve an issue

Scan through our [existing issues](https://github.com/chunky-dev/chunky/issues) to find one that interests you. You can narrow down the search using `labels` as filters. As a general rule, we don’t assign issues to anyone. If you find an issue to work on, you are welcome to open a PR with a fix. However we would request that you double check what PRs are open and whether or not another contributor is working on an issue already.

### Make Changes

#### Make changes in the UI

Click on the pencil icon at the top right of any text file (code, markdown, etc.) to make small changes such as a typo, sentence fixes, broken links, etc. Please [create a pull request](#pull-request) for a review. 


#### Make (more significant) changes locally

Intellij Community Edition is recommended, get it [here](https://www.jetbrains.com/toolbox-app/)
 - You'll want to download JetBrains Toolbox, and download `Intellij IDEA Community Edition` from there

##### Fork the project to your account
Go to https://github.com/chunky-dev/chunky, click on `Fork` (usually in the top right)

##### Clone the project.
with Intellij: `File` -> `New` -> `Project from Version Control`
- In the url put `https://github.com/GITHUB_USERNAME/chunky`
- You can also select a location to clone to here.

with Github desktop: `Clone a repository` -> select `URL`, paste `https://github.com/GITHUB_USERNAME/chunky`

with CLI: `git clone git@github.com:GITHUB_USERNAME/chunky.git` or (outdated) `git clone https://github.com/GITHUB_USERNAME/chunky`

##### Open the Project
From inside Intellij (if you cloned with intellij you can likely skip this step)
- `File` -> `Open` -> Select the first `build.gradle` file within the cloned folder

You may have to wait some time while intellij indexes the project, and imports it. (The little loading bar in the bottom right) 

##### Select the correct JDK version to use to build the project
Oracle Java 8:
- `File` -> `Project Structure` -> `Project` (should be the default)
  - If intellij detected your install: select the `SDK` dropdown, select the oracle 8 jdk install
  - If not: click `Edit`, then the little `+` at the top, `Add JDK` and find your oracle 8 jdk install location (select the main jdk folder).
    - Go back to the `Project` tab, and select the newly added jdk.

TODO: ADD INFO ABOUT NULLABLE AND NOTNULL ANNOTATIONS (adding them to compiler options)

##### Create a new branch for your change
In the bottom right of Intellij, there is a fork button, by default it will say `master`, select it, at the top select `New Branch`, and name it after what you're intending to fix. (ideally something very short, no more than a few words). eg: `fix_scene_tab_typo` or `fix-scene-tab-typo`  is fine.
 - Make sure the `Checkout branch` box is checked when creating a new branch (to actually change to it once it's created)

##### Make your change
Some useful Intellij keybinds to help you navigate around:
 - `Ctrl-n`, Find a file (class) by name

##### Commit your changes
After making your changes, select the `Commit` button (looks like a green tick) in the top right
Select all of the changes you want to make, and add a description of what you changed.

### Pull Request

When you're finished with the changes, create a pull request, also known as a PR.
- Provide a short but descriptive name and for more complex changes an extended description.
- Don't forget to [link PR to issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) if you are solving one.
- Enable the checkbox to [allow maintainer edits](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/allowing-changes-to-a-pull-request-branch-created-from-a-fork) so the branch can be updated for a merge.
Once you submit your PR, a Docs team member will review your proposal. We may ask questions or request for additional information.
- We may ask for changes to be made before a PR can be merged, either using [suggested changes](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/incorporating-feedback-in-your-pull-request) or pull request comments. You can apply suggested changes directly through the UI. You can make any other changes in your fork, then commit them to your branch.
- As you update your PR and apply changes, mark each conversation as [resolved](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/commenting-on-a-pull-request#resolving-conversations).
- If you run into any merge issues, checkout this [git tutorial](https://lab.github.com/githubtraining/managing-merge-conflicts) to help you resolve merge conflicts and other issues.

### Your PR is merged!

Congratulations :tada::tada: The Chunky team thanks you :sparkles:. 

Once your PR is merged, your contributions will be publicly available in the next development build of Chunky and eventually a stable release!

# Attribution

This Contributing guide is adapted from the [GitHub docs](https://docs.github.com/en) [contributing guide](https://github.com/github/docs/blob/main/CONTRIBUTING.md).

[chunky-discord]: https://discord.gg/VqcHpsF

