#!/bin/bash

time gradle clean shadowDistTar jbertDebPackage
find . -name "*.deb"
