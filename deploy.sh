#!/bin/bash

TGZ="./target/universal/jbert-0.1.tgz"
TARGET_DIR="/home/pi/"
TARGET_HOST=
TARGET_USER="pi"

if [[ $# -ne 1 ]]; then
    echo "[!] Define a target host, exit here"
    exit 1;
fi
TARGET_HOST=$1

echo -e "[i] Build tarball"
sbt clean universal:packageZipTarball

if [[ $? -ne 0 ]]; then
    echo "[!] Failed compiling / building tarball, exit here"
    exit 1;
fi

echo -e "[i] Copy and extract application"
scp ${TGZ} ${TARGET_USER}@${TARGET_HOST}:${TARGET_DIR}
ssh ${TARGET_USER}@${TARGET_HOST} "tar xf ${TARGET_DIR}/*.tgz -C ${TARGET_DIR}"

echo -e "[i] Done"