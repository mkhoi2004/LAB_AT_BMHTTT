# LAB 7 – Xây dựng chính sách Web Protection trên pfSense + Web Proxy (VMware, 3 máy ảo)

**Họ và tên:** Trần Nguyễn Minh Khôi  
**MSSV:** 1150080059  
**Lớp:** CNPM1  
**Bộ môn:** An toàn thông tin  
**Học phần:** Thực hành An toàn hệ thống thông tin  
**Năm học:** 2026–2027  
**Youtube** https://www.youtube.com/@Tr%E1%BA%A7nNguy%E1%BB%85nMinhKh%C3%B4i-22
**Github** https://github.com/mkhoi2004/LAB_AT_BMHTTT/tree/main/LAB7
---

## 1. Thông tin Lab

| Mục | Nội dung |
|-----|----------|
| Tên lab | LAB 7 – Web Protection: pfSense (firewall) + WEB-PROXY (E2Guardian → Squid → ClamAV) |
| Nền tảng | Windows 11 (máy thật) + **VMware Workstation** |
| Máy ảo | **pfSense-LAB6** (CE 2.7.2) · **DC01** Windows Server 2025 (AD/DNS + **IIS** + **Client**) · **WEB-PROXY** Ubuntu Server 24.04 LTS |
| Mạng | Cả 3 VM cùng VMnet LAN `10.6.0.0/24` (pfSense `10.6.0.1`, DC01 `10.6.0.10`, WEB-PROXY `10.6.0.30`); WAN của pfSense ra Internet |
| Luồng web | DC01 (Edge/curl) → **E2Guardian `10.6.0.30:8080`** → **Squid `127.0.0.1:3128`** → site IIS `10.6.0.10` hoặc Internet qua pfSense |
| Website mô phỏng | 13 FQDN `*.lab7.test` – IIS host header trên DC01, DNS trỏ `10.6.0.10` |
| Bộ tài nguyên | `LAB7_Resources.zip` (list, template block, script CA HTTPS…) |
| Link video minh họa | *(điền link)* |

> KHÔNG cài Squid/SquidGuard/Lightsquid trên pfSense (Netgate đã deprecated). pfSense chỉ làm firewall.

### Khác đề gốc (vì chỉ có 3 VM)

| Đề gốc | Bản này |
|---|---|
| Client-A/B | **DC01 (10.6.0.10)** làm client → alias `LAB7_CLIENTS` = `10.6.0.10` |
| DMZ-WEB Nginx | **IIS trên DC01**, 13 site host header |
| Schedule 60 phút trên pfSense | **Squid time ACL** (`acl ent_window time ...`) – *time-window policy*, không phải quota |
| Test direct bị chặn tới site DMZ | Test direct tới **Internet** (`example.com`), vì site IIS nằm local trên DC01 |

---

## 2. Cách dựng môi trường

1. Snapshot `LAB7_START` cho pfSense + DC01; xuất `config.xml` pfSense (mã hóa).
2. Tạo VM **WEB-PROXY** (Ubuntu 24.04, 2 GB RAM, 2 vCPU, 20 GB, tick OpenSSH), NIC **cùng VMnet LAN** với pfSense/DC01. Giữ rule `TEMP-BUILD` đến khi xong mục 6.
3. Nếu Captive Portal LAB 6 còn bật: thêm `10.6.0.30` vào *Allowed IP* hoặc tắt zone.
4. WEB-PROXY: netplan `10.6.0.30/24` (GW `10.6.0.1`, DNS `10.6.0.10`, card thường là `ens33`), `timedatectl set-timezone Asia/Ho_Chi_Minh`.
5. `sudo apt install -y squid e2guardian clamav clamav-daemon curl openssl`; `freshclam` nếu ClamAV chưa chạy.
6. **Squid:** `http_port 127.0.0.1:3128`; thêm `acl ent_sites dstdomain entertainment.lab7.test games.lab7.test`, `acl ent_window time MTWHFAS hh:mm-hh:mm` (60 phút), `http_access deny ent_sites !ent_window` **trước** `http_access allow localhost`; `reply_body_max_size 100 MB all`; `squid -k parse`.
7. **E2Guardian:** `filterip=10.6.0.30`, `filterports=8080`, `proxyip=127.0.0.1`, `proxyport=3128`, bật `contentscanner` clamd, `enablessl=off`; f1: `reportinglevel=3`, `bypass=300`, `contentscanexceptions=off`. Nạp list: hard block → `bannedsitelist` + non-bypass; warning → `bannedsitelist`; allow → `exceptionsitelist`. Kiểm tra không có dòng `**` bật.
8. **DC01 (PowerShell admin):** cài IIS, tạo 13 site host header, `fsutil` tạo 10/100/110 MB, **thêm Defender exclusion** `C:\inetpub\lab7\download` rồi tạo `eicar.com.txt`; tạo zone DNS `lab7.test` (13 bản ghi A → `10.6.0.10`).
9. **DC01 làm client:** đặt proxy `10.6.0.30:8080` (Settings hoặc registry `ProxyEnable/ProxyServer`).
10. **pfSense:** Alias → ruleset LAN → **Reset States** → kiểm thử chống bypass.

### Ruleset LAN (thứ tự từ trên xuống)

| # | Action | Source | Destination | Port | Ý nghĩa |
|---|--------|--------|-------------|------|---------|
| 1 | **Block** (log) TCP/UDP | LAB7_CLIENTS (`10.6.0.10`) | Any | WEB_PORTS | Chặn web trực tiếp |
| 2 | Pass | DC01_HOST | Any | Any | Hạ tầng DC01 – **phải nằm dưới rule 1** |
| 3 | Pass TCP | LAB7_PROXY | Any | WEB_PORTS | Proxy ra web |
| 4 | Pass UDP | LAB7_PROXY | Any | 123 | NTP cho proxy |
| 5 | Pass TCP | LAN net | Any | MAIL_PORTS | Email |
| 6 | **Block** (log) | LAN net | Any | Any | Explicit deny |

> Client và proxy cùng subnet nên DC01 → proxy không đi qua pfSense; ép proxy hiệu lực nhờ rule 1 chặn mọi đường ra web trực tiếp.

**Lưu ý đạo đức & an toàn:** chỉ dùng trong lab cô lập. HTTPS inspection chỉ trên VM lab; DC01 là Domain Controller nên snapshot trước và **gỡ CA sau khi test**. **Private key CA không rời WEB-PROXY**, không nộp/đưa lên repo. EICAR không phải mã độc; chỉ loại trừ thư mục `download` khỏi Defender, không tắt Defender.

---

## 3. Các tình huống đã thực hiện

| STT | Tình huống | Kết quả mong đợi | Trạng thái |
|-----|------------|------------------|------------|
| 1 | Dựng WEB-PROXY `10.6.0.30` | Ping `10.6.0.1`, `10.6.0.10` OK | ☐ |
| 2 | Squid chỉ nghe localhost; E2Guardian `:8080` | `ss -lntp` đúng | ☐ |
| 3 | IIS 13 site + DNS `lab7.test` + file test | `Get-Website`, `Resolve-DnsName` OK | ☐ |
| 4 | Alias + ruleset LAN | Rule 1 trên rule 2, Reset States | ☐ |
| 5 | **KB1** Baseline `normal` | Qua proxy OK; direct `example.com` bị chặn | ☐ |
| 6 | **KB2** Geo-Sim `china`+`mideast` | Hard block, không link bypass | ☐ |
| 7 | **KB3** Job `jobs` | Hard block | ☐ |
| 8 | **KB4** Warning `weapons/politics/religion` | Cảnh báo; bypass 5 phút | ☐ |
| 9 | **KB5** Education | Vào bình thường | ☐ |
| 10 | **KB6** Entertainment/Game 60 phút | 200 trong `ent_window`, 403 ngoài | ☐ |
| 11 | **KB7** Streaming | Cảnh báo, bypass tạm | ☐ |
| 12 | **KB8** `blockedgame` | Hard block | ☐ |
| 13 | **KB9** EICAR | Bị ClamAV chặn + log | ☐ |
| 14 | **KB10** 100/110 MB | 110 MB bị từ chối (nếu đạt) | ☐ |
| 15 | Trang block tùy biến | Ảnh trang block của mình | ☐ |
| 16 | Đối chiếu log pfSense / E2Guardian / Squid | Khớp request | ☐ |
| 17 | *(Mở rộng)* HTTPS inspection | Lỗi cert trước import, tin cậy sau import | ☐ |
| 18 | 10 câu hỏi + xuất `config.xml` | Hoàn thành báo cáo | ☐ |

---

## 4. Cấu trúc thư mục repo

```
LAB_AT_BMHTTT/
└── LAB7/
    ├── README.md
    ├── CNPM1-Lab7_1150080059-Tran_Nguyen_Minh_Khoi.docx
    ├── outputs/                 # log/output đã làm sạch
    │   ├── proxy_listen_ports.txt
    │   ├── firewall_rules_LAN.txt
    │   ├── e2guardian_access_block.txt
    │   ├── squid_access.txt
    │   ├── eicar_test.txt
    │   └── size_100_110MB.txt
    ├── evidence_sha256.csv
    └── screenshots/
```

> **Không** đưa lên repo: `config.xml`, `ca.key`, `cert.key`, mật khẩu.

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân | Cách khắc phục |
|-----|-------------|----------------|
| Tắt proxy vẫn ra web | Rule Block nằm dưới `DC01_HOST → Any`; chưa Reset States | Đưa rule 1 lên trên, Apply, Reset States |
| WEB-PROXY không ra Internet | Thiếu rule 3 / Captive Portal chặn / DNS forwarder | Kiểm tra rule, Allowed IP, `nslookup` |
| Proxy 502 | Squid chưa chạy, DNS, IIS site dừng, Windows Firewall DC01 | `systemctl status`, `Get-Website`, kiểm tra TCP/80 |
| IIS 404.3 khi tải `.bin` | Thiếu MIME | `appcmd ... fileExtension='.bin'` |
| EICAR biến mất trên DC01 | Defender xóa | Thêm exclusion thư mục `download` trước khi tạo |
| Mọi site bị chặn | Dòng `**` bật trong `bannedsitelist` | Comment dòng đó, restart E2Guardian |
| Không có link bypass | `bypass=300`, `reportinglevel`, site ở non-bypass, đang HTTPS | Test bằng HTTP trên Edge |
| Time ACL sai | Sai múi giờ proxy; dòng deny nằm dưới `allow localhost` | `timedatectl`, sửa thứ tự, restart Squid |
| `reply_body_max_size` lỗi | Cú pháp theo phiên bản | `squid -k parse`; dùng `100 MB all` hoặc ghi rõ không hỗ trợ |
| 100MB và 110MB đều tải được | Chưa có ACL kích thước | Không kết luận đạt, ghi nguyên nhân |
| ClamAV không quét | Socket quyền / daemon chưa chạy | `usermod -aG clamav e2guardian`, `freshclam` |
| HTTPS lỗi chứng chỉ | Chưa import CA lab | Import `ca.der` vào Trusted Root (rồi gỡ khi xong) |

---

## 6. Cam kết

- Ảnh chụp và log lấy trực tiếp từ môi trường của sinh viên; khớp timestamp bài làm.
- Không dùng ảnh/log của người khác; không upload mật khẩu, token, private key, `config.xml` chưa làm sạch.
- Chỉ thực hành trong mạng lab cô lập do chính mình dựng.

---

**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** 10/2026
