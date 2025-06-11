---
description: How to install ducklib
---

# Installation

## Prerequisites

Required:

* git
* some knowledge of Gradle build scripts

## Step 1: Cloning FtcRobotController

First, clone the FtcRobotController repo if you haven't already:

```shell
git clone https://github.com/FIRST-Tech-Challenge/FtcRobotController
```

## Step 2: Cloning ducklib

Now, we'll clone ducklib into the same directory you cloned FtcRobotController into:

```shell
git clone https://github.com/escape-velocity-14343/ducklib
```

Now, your file tree should look something like this:

```
FTC projects folder
├ FtcRobotController
│ ├ TeamCode
│ ├ FtcRobotController
│ ...
└ ducklib
  ├ ducklib
  ├ ducklib-ftc
  ├ ducklib-test
  ├ docs
  ...
```

We're going to be using `includeBuild` to include a dependency to another project.

## Step 3: Add projects to `FtcRobotController`

**NOTE**:
If you clone [the quickstart](https://github.com/escape-velocity-14343/ducklib-quickstart) instead of the normal FtcRobotController
(this is assuming I keep it up to date)
you won't need to do this,
since it already has the gradle files modified correctly.

Now, open the `settings.gradle` file in `FtcRobotController`.
You should have something like this by default:

```groovy
include ':FtcRobotController'
include ':TeamCode'
```

Now, add

```groovy
includeBuild("../ducklib/ducklib") {
    dependencySubstitution {
        substitute module("com.escapevelocity.ducklib:core") using project(":")
    }
}

includeBuild("../ducklib/ducklib-ftc") {
    dependencySubstitution {
        substitute module("com.escapevelocity.ducklib:ftc") using project(":")
    }
}
```

to the end.
This will tell Gradle to include the ducklib projects in the build as well as the normal TeamCode projects,
with the package name `com.escapevelocity.ducklib:<core/ftc>`

## Step 4: Add ducklib as a dependency

Now, you're ready to add ducklib as a dependency.
Open up `build.gradle` in `FtcRobotController/TeamCode`,
and add ducklib as a dependency inside the dependency block:

```groovy
dependencies {
    implementation project(':FtcRobotController')
    implementation 'com.escapevelocity.ducklib:core'
    implementation 'com.escapevelocity.ducklib:ftc'
}
```

The whole `FtcRobotController/TeamCode/build.gradle` should look like this:

```groovy
apply from: '../build.common.gradle'
apply from: '../build.dependencies.gradle'
apply plugin: 'org.jetbrains.kotlin.android'

android {
    namespace = 'org.firstinspires.ftc.teamcode'
    kotlinOptions {
        jvmTarget = '1.8'
    }

    packagingOptions {
        jniLibs.useLegacyPackaging true
    }
}

dependencies {
    implementation project(':FtcRobotController')
    implementation 'com.escapevelocity.ducklib:core'
    implementation 'com.escapevelocity.ducklib:ftc'
}
```

Run a Gradle sync
(tip: open up the command palette to run any action by double-pressing shift)
and there shouldn't be any errors.
ducklib should be ready to use!
