#!/usr/bin/env bash
set -euo pipefail

# LAB7 DMZ-WEB setup - Ubuntu Server 24.04 LTS
# Run as a sudo-capable user on 172.16.6.10/24.

sudo apt update
sudo apt install -y nginx curl

ROOT=/var/www/lab7
sudo mkdir -p "$ROOT"

make_site() {
  name="$1"; title="$2"; text="$3"
  sudo mkdir -p "$ROOT/$name"
  cat <<EOF | sudo tee "$ROOT/$name/index.html" >/dev/null
<!doctype html><html lang="vi"><head><meta charset="utf-8"><title>$title</title>
<style>body{font-family:Arial,sans-serif;max-width:900px;margin:60px auto;line-height:1.5} .tag{padding:8px 12px;background:#eef3f8;border-left:5px solid #2f5597}</style></head>
<body><h1>$title</h1><div class="tag">LAB 7 - Website mo phong noi bo</div><p>$text</p></body></html>
EOF
  cat <<EOF | sudo tee "/etc/nginx/sites-available/$name.lab7.test" >/dev/null
server {
  listen 80;
  server_name $name.lab7.test;
  root $ROOT/$name;
  index index.html;
  location / { try_files \$uri \$uri/ =404; }
}
EOF
  sudo ln -sf "/etc/nginx/sites-available/$name.lab7.test" "/etc/nginx/sites-enabled/$name.lab7.test"
}

sudo rm -f /etc/nginx/sites-enabled/default
make_site normal "Normal Site" "Website duoc phep truy cap binh thuong."
make_site china "China-Sim Site" "Website mo phong nhom dia ly Trung Quoc de kiem thu policy."
make_site mideast "MiddleEast-Sim Site" "Website mo phong nhom dia ly Trung Dong de kiem thu policy."
make_site jobs "Job Search Site" "Website mo phong nhom tim viec."
make_site weapons "Weapons Warning Site" "Website mo phong nhom can canh bao."
make_site politics "Politics Warning Site" "Website mo phong nhom can canh bao."
make_site religion "Religion Warning Site" "Website mo phong nhom can canh bao."
make_site education "Education Site" "Website giao duc luon duoc phep."
make_site entertainment "Entertainment Site" "Website giai tri de kiem thu gio truy cap."
make_site games "Games Site" "Website game de kiem thu gio truy cap."
make_site streaming "Streaming Site" "Website streaming mo phong - che do canh bao."
make_site blockedgame "Blocked Game Site" "Website game bi chan hoan toan."
make_site download "Download Test Site" "Thu muc nay chua file kich thuoc va EICAR test."

sudo mkdir -p "$ROOT/download/files"
# Real-size zero-filled files for HTTP size tests.
sudo dd if=/dev/zero of="$ROOT/download/files/10MB.bin" bs=1M count=10 status=none
sudo dd if=/dev/zero of="$ROOT/download/files/100MB.bin" bs=1M count=100 status=none
sudo dd if=/dev/zero of="$ROOT/download/files/110MB.bin" bs=1M count=110 status=none

cat <<'EOF' | sudo tee "$ROOT/download/index.html" >/dev/null
<!doctype html><html><head><meta charset="utf-8"><title>LAB7 Download Test</title></head>
<body><h1>LAB7 Download Test</h1><ul>
<li><a href="/files/10MB.bin">10MB.bin</a></li>
<li><a href="/files/100MB.bin">100MB.bin</a></li>
<li><a href="/files/110MB.bin">110MB.bin</a></li>
<li><a href="/files/eicar.com.txt">eicar.com.txt (benign antivirus test string)</a></li>
</ul></body></html>
EOF

# Build the standard EICAR test string from fragments to reduce accidental AV triggering on the resource package itself.
python3 - <<'PY2' | sudo tee "$ROOT/download/files/eicar.com.txt" >/dev/null
parts=["X5O!P%@AP[4\\PZX54(P^)","7CC)7}$EICAR-STANDARD-","ANTIVIRUS-TEST-FILE!$H+H*"]
print(''.join(parts))
PY2

sudo nginx -t
sudo systemctl enable --now nginx
sudo systemctl reload nginx

echo "LAB7 DMZ-WEB ready. Test: curl -H 'Host: normal.lab7.test' http://127.0.0.1/"
