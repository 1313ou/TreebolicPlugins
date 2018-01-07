#!/bin/bash

apps="treebolicOwlPlugin treebolicFilesPlugin treebolicDotPlugin"

for a in $apps; do
	echo $a
	./gradlew :${a}:assembleRelease
done
