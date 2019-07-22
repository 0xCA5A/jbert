if [[ $# -ne 1 ]]; then
    echo "[!] Define a target host"
    exit 1;
fi
TARGET_HOST=$1

echo -e "[i] Create a Ansible hosts file"
echo -e "[jberts]\n${TARGET_HOST}" > ansible/hosts
cat ansible/hosts

echo -e "[i] Run Ansible playbook ${PLAYBOOK}"
cd ansible
time ansible-playbook ${PLAYBOOK} -i hosts -u ${REMOTE_USER}
cd -

echo -e "[i] Remove generated ansible hosts file"
rm -v ansible/hosts

echo -e "[i] Done"
