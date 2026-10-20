#!/usr/bin/env bash
set -euo pipefail
# LAB7 WEB-PROXY base packages - Ubuntu Server 24.04 LTS
sudo apt update
sudo apt install -y squid e2guardian clamav clamav-daemon curl openssl
sudo systemctl enable squid e2guardian clamav-daemon || true
sudo systemctl restart squid
sudo systemctl restart clamav-daemon || true
sudo systemctl restart e2guardian || true
printf '
Installed versions:
'
squid -v | head -n 3 || true
e2guardian -v || true
clamscan --version || true
