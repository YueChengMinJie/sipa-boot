#!/usr/bin/env bash

#***********************************************
#Author:        caszhou
#Args:          $1-版本号
#Version:       1.0
#Date:          2019-08-03 09:45:51
#FileName:      nexus
#Description:   nexus script
#***********************************************

set -ex

export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk17

./gradlew assemble
./gradlew publish
