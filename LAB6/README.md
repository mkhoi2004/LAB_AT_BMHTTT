# LAB 6 – Xác thực Active Directory và chính sách truy cập trên pfSense

**Họ và tên:** Trần Nguyễn Minh Khôi  
**MSSV:** 1150080059  
**Lớp:** CNPM1  
**Bộ môn:** An toàn thông tin  
**Học phần:** Thực hành An toàn hệ thống thông tin  
**Năm học:** 2026–2027  

---

## 1. Thông tin Lab

| Mục | Nội dung |
|-----|----------|
| Tên lab | LAB 6 – Xác thực Active Directory và chính sách truy cập trên pfSense (LDAP, Captive Portal, Alias, Schedule, DMZ test sites) |
| Phiên bản môi trường | Windows 11 Host + VirtualBox 7.x + pfSense CE 2.7.2-RELEASE (VM kiểu BSD / FreeBSD 64-bit) + Windows Server 2025 (DC01) + Ubuntu Server 22.04/24.04 (DMZ-WEB) + Windows 11 (Client-A/B) |
| Mạng thực hành | WAN: NAT Network `LAB6-WAN` `192.168.250.0/24` · MGMT: Host-Only `192.168.56.0/24` · LAN: Internal `lab6-lan` `10.6.0.0/24` · DMZ: Internal `lab6-dmz` `172.16.6.0/24` |
| Tường lửa | pfSense-LAB6 (WAN DHCP · MGMT `192.168.56.254/24` · LAN `10.6.0.1/24` · DMZ `172.16.6.1/24`) |
| Máy trong LAN | DC01 `10.6.0.10/24` (domain `lab6.test`) · Client-A `10.6.0.21/24` (sv_a) · Client-B `10.6.0.22/24` (sv_b) |
| Máy trong DMZ | DMZ-WEB (Ubuntu + Nginx) `172.16.6.10/24` + IP mô phỏng `.20` JOB · `.30` SOCIAL · `.40` RESTRICTED |
| Máy quản trị | Máy thật `192.168.56.1/24` → WebGUI `https://192.168.56.254` |
| Bộ cài | `pfSense-CE-2.7.2-RELEASE-amd64.iso.gz` (mirror Netgate) |
| SHA-256 (.iso.gz) | `883fb7bc64fe548442ed007911341dd34e178449f8156ad65f7381a02b7cd9e4` (bản 2.7.2; nếu dùng bản khác thì đối chiếu trên trang tải) |
| Link video minh họa | *https://www.youtube.com/watch?v=YAFoc6C1zWw* |

---

## 2. Cách dựng môi trường

1. Trên máy thật chạy `ipconfig` và `route print`; bảo đảm WAN/VPN/adapter ảo **không overlap** `192.168.250.0/24`, `192.168.56.0/24`, `10.6.0.0/24`, `172.16.6.0/24`. Không dùng NAT mặc định `10.0.2.0/24` cho WAN.
2. Tải `pfSense-CE-2.7.2-RELEASE-amd64.iso.gz`, kiểm tra SHA-256 bằng `Get-FileHash -Algorithm SHA256`, chỉ giải nén (7-Zip) khi hash khớp.
3. VirtualBox → Tools → Network: tạo **NAT Network** `LAB6-WAN` (`192.168.250.0/24`, bật DHCP); **Host-Only** `192.168.56.0/24` với IP host `192.168.56.1`, **tắt DHCP Server**, không đặt Gateway/DNS trên card này. Internal Network `lab6-lan`, `lab6-dmz` chỉ cần gõ đúng tên khi gắn vào VM.
4. Tạo VM `pfSense-LAB6` (Type BSD / FreeBSD 64-bit, 2 GB RAM, 2 vCPU, 16–20 GB đĩa) với 4 adapter: **1 = NAT Network `LAB6-WAN` (WAN)**, **2 = Host-Only (MGMT)**, **3 = Internal `lab6-lan` (LAN)**, **4 = Internal `lab6-dmz` (DMZ)**. Cài đặt, tháo ISO.
5. Console pfSense: Assign Interfaces theo thứ tự Adapter/MAC (WAN = Adapter 1, `lan` = Adapter 2 (MGMT), OPT1 = Adapter 3, OPT2 = Adapter 4); đặt MGMT `192.168.56.254/24`, LAN `10.6.0.1/24`, DMZ `172.16.6.1/24`, tắt DHCP server ở mọi interface.
6. Vào `https://192.168.56.254`, chạy Setup Wizard (timezone `Asia/Ho_Chi_Minh`, giữ nguyên IP interface `lan`), đổi Description: `LAN`→**MGMT**, `OPT1`→**LAN**, `OPT2`→**DMZ**. Tạo hai rule tạm `TEMP-BUILD` (LAN any, DMZ any) để dựng các máy.
7. Dựng **DC01** (Windows Server 2025): IP `10.6.0.10/24`, cài AD DS + DNS, forest `lab6.test`, forwarder DNS, OU `LAB6-Users`, user `sv_a`, `sv_b`, service account `svc_pfsense_ldap` (đặt trong container `Users`, **không** cấp Domain Admin); mở ICMP Echo bằng `New-NetFirewallRule`.
8. Dựng **DMZ-WEB** (Ubuntu): IP `172.16.6.10` + `.20/.30/.40` (netplan), cài Nginx (4 server block, mỗi IP một trang nhận diện). Dựng **Client-A/B** với IP tĩnh `10.6.0.21` / `10.6.0.22`, DNS `10.6.0.10` (có thể dùng 1 VM client và đổi IP luân phiên).
9. pfSense: tạo **LDAP Authentication Server** `LAB6-AD` → test `sv_a`, `sv_b` ở Diagnostics → Authentication → tạo **Captive Portal** zone `LAB6_USERS` trên LAN (thêm `10.6.0.10` vào Allowed IP) → đăng nhập portal cho Client-A/B **trước khi** siết rule.
10. Tạo **Alias** (`CLIENT_A`, `CLIENT_B`, `POLICY_CLIENTS`, `GENERAL_SITE`, `JOB_SITE`, `SOCIAL_SITE`, `RESTRICTED_SITE`, `WEB_PORTS`, `DC01_HOST`) và **Schedule** (`LUNCH_WINDOW`, `WORK_HOURS`, `TEST_NOW`).
11. Xóa/disable rule `TEMP-BUILD LAN`, tạo rule hạ tầng `DC01_HOST → Any` → **Reset States** → làm baseline → thực hiện từng kịch bản.
12. Tạo snapshot `Before-LAB6` cho các VM trước khi làm các kịch bản.

**Lưu ý đạo đức & an toàn:** pfSense CE 2.7.2 là bản cũ, chỉ dùng trong mạng lab ảo cô lập; không dùng cho production hoặc làm firewall Internet-facing. LDAP TCP/389 không mã hóa chỉ dùng trong lab; triển khai thật dùng LDAPS/TLS. Các "website mô phỏng" chỉ là Nginx nội bộ, không truy cập website nhạy cảm thật.

---

## 3. Các tình huống đã thực hiện

Đánh dấu ✅ sau khi chạy xong và có ảnh bằng chứng.

| STT | Tình huống | Kết quả mong đợi | Trạng thái |
|-----|------------|------------------|------------|
| 1 | Kiểm tra SHA-256 của `.iso.gz` | Hash trùng giá trị công bố | ☐ |
| 2 | Dựng pfSense-LAB6 (4 adapter), đặt IP trên console | Máy thật ping được `192.168.56.254`; ảnh 4 adapter | ☐ |
| 3 | WebGUI + Setup Wizard + đổi tên interface MGMT/LAN/DMZ | Dashboard hiển thị đúng 4 interface | ☐ |
| 4 | DC01: AD DS + DNS + OU + user + service account | `Get-ADUser` thấy `sv_a`, `sv_b`; `svc_pfsense_ldap` trong `Users` | ☐ |
| 5 | DMZ-WEB: 4 IP + Nginx | `curl` `.10/.20/.30/.40` trả đúng tên site | ☐ |
| 6 | **LDAP** – Diagnostics → Authentication | `sv_a` và `sv_b` xác thực thành công | ☐ |
| 7 | **Captive Portal** trên LAN dùng LAB6-AD | Portal yêu cầu login; thấy phiên `10.6.0.21` / `10.6.0.22` | ☐ |
| 8 | Alias + Schedule | `Diagnostics → Tables` hiển thị đúng; schedule đúng múi giờ | ☐ |
| 9 | **KB1** – Client-A bị chặn JOB_SITE | `.10` vào được; `.20` bị chặn + có log | ☐ |
| 10 | **KB2** – Cấm RESTRICTED_SITE cho A và B | Cả A, B bị chặn `.40`, vẫn vào `.10` | ☐ |
| 11 | **KB3** – Client-B chỉ vào SOCIAL_SITE trong giờ nghỉ | Trong schedule vào được; ngoài schedule bị Block | ☐ |
| 12 | **KB4** – Chỉ cho HTTP/HTTPS trong giờ hành chính | HTTP/HTTPS OK; ping và ngoài giờ bị Block | ☐ |
| 13 | **KB5** – Cô lập DMZ khỏi LAN (có baseline) | Baseline OK → sau Block DMZ→DC01 fail; DMZ vẫn ra Internet; LAN→`.10` vẫn OK | ☐ |
| 14 | **KB6** – So sánh Block và Reject | Reject lỗi nhanh; Block timeout | ☐ |
| 15 | **KB7** – Đọc Firewall Log và State Table | Có bản ghi Block khớp; state biến mất sau Reset States | ☐ |
| 16 | Ruleset tổng hợp + ma trận kiểm thử + 12 câu hỏi | Đủ 8 rule LAN đúng thứ tự; bảng kết quả đã điền | ☐ |
| 17 | Xuất `config.xml` cuối cùng | File cấu hình (mã hóa) để nộp cho giảng viên | ☐ |

---

## 4. Cấu trúc thư mục repo

```
LAB_AT_BMHTTT/
└── LAB6/
    ├── README.md
    ├── CNPM1-Lab6_1150080059-Tran_Nguyen_Minh_Khoi.docx   # Báo cáo Word
    ├── outputs/                                          # Log/output đã làm sạch
    │   ├── sha256_check.txt
    │   ├── ldap_test_sv_a_sv_b.txt
    │   ├── firewall_rules_LAN.txt
    │   ├── firewall_log_block.txt
    │   ├── reject_vs_block_timing.txt
    │   └── ...
    ├── evidence_sha256.csv                               # Hash bằng chứng
    └── screenshots/                                      # Ảnh chụp theo thứ tự trong báo cáo
```

> **Không** đưa `config.xml` của pfSense lên repo công khai: file chứa hash mật khẩu, thông tin LDAP bind và cấu hình hệ thống. Chỉ nộp cho giảng viên theo hướng dẫn của môn (nên bật *Encrypt this configuration file* khi xuất).

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân | Cách khắc phục |
|-----|-------------|----------------|
| Hash `.iso.gz` không khớp | Tải lỗi/sai file | Tải lại từ mirror Netgate, không giải nén khi hash chưa khớp |
| Tên card là `em0/em1` hoặc `vtnet0/1` khác hình mẫu | Loại NIC ảo khác nhau | Đối chiếu MAC và thứ tự Adapter 1 = WAN, 2 = MGMT, 3 = LAN, 4 = DMZ |
| Không vào được `https://192.168.56.254` | Host-Only sai, IP host chưa `192.168.56.1/24`, MGMT không phải interface `lan` (thiếu Anti-Lockout) | Kiểm tra Adapter 2 và Network Manager; gán MGMT làm `lan` trong console |
| Máy ping `10.6.0.1` hoặc `apt update` trên DMZ-WEB không được | LAN/DMZ chưa có rule nào (default deny) | Tạo rule tạm `TEMP-BUILD` ở mục 2.6; xóa sau khi dựng xong |
| LDAP test lỗi *strong authentication required* trên Windows Server 2025 | DC yêu cầu LDAP signing, bind đơn giản TCP/389 bị từ chối | Chỉ trong lab: đặt *Domain controller: LDAP server signing requirements* = None trong Default Domain Controllers Policy, `gpupdate /force`; thực tế dùng LDAPS |
| LDAP test lỗi khác | Sai Base DN/Container, bind DN/mật khẩu, service account bị khóa, không route tới `10.6.0.10` | Test Port 389, bật debug, đọc System Log; kiểm tra bind DN `CN=svc_pfsense_ldap,CN=Users,...` |
| Captive Portal không hiện | Dùng HTTPS/trang cache, session cũ, zone chưa Enable | Dùng `http://172.16.6.10` trong cửa sổ ẩn danh; disconnect session cũ |
| DC01 mất Internet / DMZ→DC01 không phản hồi sau khi bật portal | DC01 bị Captive Portal chặn như client chưa login | Thêm `10.6.0.10` vào *Allowed IP Addresses* (Both) |
| Đã đổi rule nhưng kết quả không đổi | State cũ còn tồn tại (stateful firewall) | Apply Changes, **Reset States**, tạo kết nối mới |
| Rule Block không có tác dụng | Block nằm dưới Pass tổng quát | Đưa Block lên trên Pass (rule xét từ trên xuống, first match) |
| Schedule không đúng giờ | Sai timezone pfSense hoặc chưa chờ cron (≈1 phút) | Đặt `Asia/Ho_Chi_Minh`, chờ ~1 phút rồi Reset States |
| Ping DMZ → DC01 fail trước khi Block | Windows Firewall trên DC01 chặn ICMP | Thêm rule `LAB6-Allow-ICMPv4-Echo`, làm baseline trước |
| Ngoài giờ B vẫn vào được SOCIAL_SITE | Thiếu rule Block bên dưới rule Pass có schedule; rule 6 cho qua | Thêm rule 4 `Block CLIENT_B → SOCIAL_SITE` ngay dưới rule 3 |
| Log không thấy packet | Chưa bật *Log packets*, không có traffic mới, lọc sai IP | Bật log đúng rule, tạo traffic mới, lọc theo source IP client |
| Tự khóa WebGUI | Xóa/Disable Anti-Lockout Rule | Giữ Anti-Lockout; nếu lỡ, console `8) Shell` → `pfctl -d` tạm thời để vào lại |
| Clone DC để làm client gây lỗi AD | Clone DC không theo quy trình AD DS | Tạo client là VM độc lập (Windows 11/Ubuntu) |

---

## 6. Cam kết

- Ảnh chụp lấy trực tiếp từ VM/PC của sinh viên; output/log khớp timestamp bài làm.
- Không sử dụng ảnh/log của người khác.
- Không upload mật khẩu, token, cookie, `config.xml` chưa làm sạch, dữ liệu cá nhân hay thông tin định danh hệ thống thật.
- Chỉ thực hành trong mạng lab cô lập do chính mình dựng.

---

**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** 10/2026
