#!/usr/bin/env bash

#create a directory and use it to clone
#the git repo used to display build data and test reports
git init ethercis-core
cd ethercis-core/
git remote add origin https://github.com/aboww/travis-test.git > /dev/null 2>&1
git pull origin

#use a shallow clone to minimise traffic
#TODO: trigger a script to archive contents of this repo and reset
#contents to a single entry
git pull --depth=1 origin master