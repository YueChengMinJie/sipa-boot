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

nvm use 16
pnpm install
pnpm build
pnpm run nexus
