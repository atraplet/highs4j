# HiGHS Solver for Java

*Work in progress*

[![Build](https://github.com/atraplet/highs4j/actions/workflows/build.yml/badge.svg)](https://github.com/atraplet/highs4j/actions/workflows/build.yml)
[![Codecov](https://codecov.io/github/atraplet/highs4j/graph/badge.svg?token=H3EN61962F)](https://codecov.io/github/atraplet/highs4j)
[![Maven Central](https://img.shields.io/maven-central/v/com.ustermetrics/highs4j)](https://central.sonatype.com/artifact/com.ustermetrics/highs4j)
[![Apache License, Version 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/atraplet/highs4j/blob/master/LICENSE)

*This library requires JDK 25 or later.*

highs4j (HiGHS Solver for Java) is a Java library that provides an interface from the Java programming language to
the native open source mathematical programming solver [HiGHS](https://highs.dev). It invokes the solver through
Java's [Foreign Function and Memory (FFM) API](https://docs.oracle.com/en/java/javase/25/core/foreign-function-and-memory-api.html).

## Usage

### Dependency

Add the latest version from [Maven Central](https://central.sonatype.com/artifact/com.ustermetrics/highs4j) to
your `pom.xml`

```
<dependency>
    <groupId>com.ustermetrics</groupId>
    <artifactId>highs4j</artifactId>
    <version>x.y.z</version>
</dependency>
```

### Native Library

Either add the latest version of [highs4j-native](https://github.com/atraplet/highs4j-native)
from [Maven Central](https://central.sonatype.com/artifact/com.ustermetrics/highs4j-native) to your `pom.xml`

```
<dependency>
    <groupId>com.ustermetrics</groupId>
    <artifactId>highs4j-native</artifactId>
    <version>x.y.z</version>
    <classifier>platform</classifier>
    <scope>runtime</scope>
</dependency>
```

where `x.y.z` is the version of the library and `platform` is one of `linux_64`, `windows_64`, or `osx_arm64`. If no
`classifier` is specified, binaries for all platforms are included.

Or alternatively install the native solver on the machine and add the location to the `java.library.path`. highs4j
dynamically loads the native solver.

### Run Code

Since highs4j invokes some restricted methods of the FFM API,
use `--enable-native-access=com.ustermetrics.highs4j --enable-native-access=org.scijava.nativelib` or
`--enable-native-access=ALL-UNNAMED` (if you are not using the Java Platform Module System) to avoid warnings from the
Java runtime.

## Build

### Java bindings

The directory `./bindings` contains the files and scripts needed to generate the Java bindings. The actual bindings are
under `./src/main/java` in the package `com.ustermetrics.highs4j.bindings`.

The scripts depend on the [jextract](https://jdk.java.net/jextract/) tool, which mechanically generates Java bindings
from native library headers.

The bindings are generated in two steps: First, `./bindings/generate_includes.sh` generates the dumps of the included
symbols in the `includes.txt` file. Replace absolute platform dependent path with relative platform independent path in
the comments. Remove unused includes. Second, `./bindings/generate_bindings.sh` generates the actual Java bindings.
Add `NativeLoader.loadLibrary.` Remove platform dependent layout constants and make the code platform independent.

## Release

Update the version in the `pom.xml`, create a tag, and push it by running

```
export VERSION=X.Y.Z
git checkout --detach HEAD
sed -i -E "s/<version>[0-9]+\-SNAPSHOT<\/version>/<version>$VERSION<\/version>/g" pom.xml
git commit -m "v$VERSION" pom.xml
git tag v$VERSION
git push origin v$VERSION
```

This will trigger the upload of the package to Maven Central via GitHub Actions.

Then, go to the GitHub repository [releases page](https://github.com/atraplet/highs4j/releases) and update the
release.

## Credits

This project is based on the native open source mathematical programming
solver [HiGHS](https://highs.dev), which is developed and maintained by Julian Hall, Ivet Galabova, Qi
Huangfu, Leona Gottwald, Michael Feldmeier, and other contributors. For details see https://highs.dev,
https://ergo-code.github.io/HiGHS, and https://github.com/ERGO-Code/HiGHS.
