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

cd dist
pnpm publish --no-git-checks --registry http://127.0.0.1:8081/repository/npm-sipa
