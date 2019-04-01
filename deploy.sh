#!/bin/bash

APP_ARCHIVE=
TARGET_DIR="/home/pi/"
TARGET_HOST=
TARGET_USER="pi"


if [[ $# -ne 2 ]]; then
    echo "[!] Define a target host and the path to the application tgz archive"
    exit 1;
fi
TARGET_HOST=$1
APP_ARCHIVE=$2


echo -e "[i] Build tarball"
sbt clean universal:packageZipTarball

if [[ $? -ne 0 ]]; then
    echo "[!] Failed compiling / building tarball, exit here"
    exit 1;
fi

if [[ ! -f ${APP_ARCHIVE} ]]; then
    echo "[!] Application tgz archive '${APP_ARCHIVE}' not found"
    exit 1;
fi

echo -e "[i] Copy and extract application package"
scp ${APP_ARCHIVE} ${TARGET_USER}@${TARGET_HOST}:${TARGET_DIR}
ssh ${TARGET_USER}@${TARGET_HOST} "tar xf ${TARGET_DIR}/*.tgz -C ${TARGET_DIR}"

echo -e "[i] Done"