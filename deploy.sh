#!/bin/bash

APP_ARCHIVE=
PROJECT_NAME="jbert"
TARGET_DIR="/home/pi"
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


echo -e "[i] Remove old application data on target"
ssh "${TARGET_USER}@${TARGET_HOST}" "sudo rm -vrf ${TARGET_DIR}/${PROJECT_NAME}*"


echo -e "[i] Copy application package"
scp "${APP_ARCHIVE}" "${TARGET_USER}@${TARGET_HOST}:${TARGET_DIR}"


echo -e "[i] Extract application package"
ssh "${TARGET_USER}@${TARGET_HOST}" "tar -vxf ${TARGET_DIR}/*.tgz -C ${TARGET_DIR}"


echo -e "[i] Create symlink"
ssh "${TARGET_USER}@${TARGET_HOST}" "find ${TARGET_DIR} -type d -name \"${PROJECT_NAME}*\" -exec ln -s {} ${TARGET_DIR}/${PROJECT_NAME} ';' "


echo -e "[i] Copy systemd service file"
scp systemd/${PROJECT_NAME}.service ${TARGET_USER}@${TARGET_HOST}:/tmp
ssh "${TARGET_USER}@${TARGET_HOST}" "sudo mv /tmp/${PROJECT_NAME}.service* /etc/systemd/system"


echo -e "[i] Refresh systemd config"
ssh "${TARGET_USER}@${TARGET_HOST}" "sudo systemctl enable ${PROJECT_NAME}"
ssh "${TARGET_USER}@${TARGET_HOST}" "sudo systemctl daemon-reload"


echo -e "[i] Restart the jbert application"
ssh "${TARGET_USER}@${TARGET_HOST}" "sudo systemctl restart ${PROJECT_NAME}"


echo -e "[i] Done"
