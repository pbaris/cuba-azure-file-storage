#!/usr/bin/env bash

usage()
{
    echo
    echo "usage: ./publish.sh"
    echo "Publish the package to Maven Central"
    echo
}

./gradlew clean assemble uploadArchives