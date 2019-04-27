#!/bin/bash

time sbt clean debian:packageBin
find . -name "*.deb"
