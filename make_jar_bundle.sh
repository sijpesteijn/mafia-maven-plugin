#!/bin/sh
VERSION=$1
cd target
jar -cvf bundle.jar mafia-maven-plugin-$VERSION-javadoc.jar mafia-maven-plugin-$VERSION-javadoc.jar.asc mafia-maven-plugin-$VERSION-sources.jar mafia-maven-plugin-$VERSION-sources.jar.asc mafia-maven-plugin-$VERSION.jar mafia-maven-plugin-$VERSION.jar.asc mafia-maven-plugin-$VERSION.pom mafia-maven-plugin-$VERSION.pom.asc
