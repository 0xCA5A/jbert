#!/bin/bash

APP_DEB_PACKAGE=
TARGET_HOST=
REMOTE_USER=pi


if [[ $# -ne 2 ]]; then
    echo "[!] Define a target host and the path to the Debian application package"
    exit 1;
fi
TARGET_HOST=$1
APP_DEB_PACKAGE=$2


if [[ ! -f ${APP_DEB_PACKAGE} ]]; then
    echo "[!] Debian installation package '${APP_DEB_PACKAGE}' not found"
    exit 1;
fi
APP_DEB_PACKAGE_NAME=$(basename "${APP_DEB_PACKAGE}")


echo -e "[i] Symlink Debian installation package to ansible directory"
ln -fvs "$(pwd)/${APP_DEB_PACKAGE}" ansible

echo -e "[i] Create a Ansible hosts file"
echo -e "[jberts]\n${TARGET_HOST}" > ansible/hosts
cat ansible/hosts

echo -e "[i] Run Ansible playbook"
cd ansible
time ansible-playbook deploy.yml -i hosts -u ${REMOTE_USER} -e "local_jbert_deb_package=${APP_DEB_PACKAGE_NAME}"
cd -

echo -e "[i] Remove generated ansible hosts file"
rm -v ansible/hosts

echo -e "[i] Remove symlink to Debian installation package in ansible directory"
rm -v "ansible/${APP_DEB_PACKAGE_NAME}"


echo -e "[i] Done"
