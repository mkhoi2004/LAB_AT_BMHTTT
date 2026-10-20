#!/usr/bin/env bash
set -euo pipefail
# Run on WEB-PROXY only. Creates a LAB-ONLY root CA for E2Guardian MITM testing.
DIR=/etc/e2guardian/private
sudo mkdir -p "$DIR/generatedcerts"
sudo openssl genrsa -out "$DIR/ca.key" 4096
sudo openssl req -new -x509 -days 3650 -key "$DIR/ca.key" -out "$DIR/ca.pem" -subj "/C=VN/O=LAB7 Training/CN=LAB7 E2Guardian Root CA"
sudo openssl x509 -in "$DIR/ca.pem" -outform DER -out "$DIR/ca.der"
sudo openssl genrsa -out "$DIR/cert.key" 4096
sudo chown -R e2guardian:e2guardian "$DIR" 2>/dev/null || true
sudo chmod 700 "$DIR"
sudo chmod 600 "$DIR/ca.key" "$DIR/cert.key"
echo "Created $DIR/ca.pem and $DIR/ca.der. Import ONLY into isolated LAB clients."
