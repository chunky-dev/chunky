#!/bin/sh

if [ $# -lt "1" ]; then
    echo "Usage: $0 VERSION"
    exit 1
fi

VERSION=$1

echo "version=$VERSION" > src/main/res/Version.properties
git commit -m "Bumped version string"
git tag -a $VERSION -m "Version $VERSION"
cd win
cmd //c release.bat
