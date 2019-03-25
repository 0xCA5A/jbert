#!/bin/bash

find . -name "*.tgz"

sbt clean universal:packageZipTarball

find . -name "*.tgz"

TGZ="./target/universal/jbert-0.1.tgz"

scp ${TGZ} pi@10.0.50.120:/tmp
ssh pi@10.0.50.120 "tar xf /tmp/*.tgz -C /tmp"
