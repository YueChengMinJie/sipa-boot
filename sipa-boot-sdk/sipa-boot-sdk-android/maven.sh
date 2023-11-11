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

export JAVA_HOME=/usr/lib/jvm/jdk-17-oracle-x64
export SDK_HOME=/opt/cmdline-tools/latest
export PATH=/opt/cmdline-tools/bin:$PATH
export PATH=${SDK_HOME}/platform-tools:$PATH
export ANDROID_HOME=/opt/cmdline-tools/latest

cp -f /mnt/gitops-component/.android/local.properties ./

./gradlew assemble
./gradlew publish
