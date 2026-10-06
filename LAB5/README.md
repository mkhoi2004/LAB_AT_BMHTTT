# LAB 5 – Cấu hình tường lửa pfSense (Firewall, NAT, DMZ)

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
| Tên lab | LAB 5 – Cấu hình tường lửa pfSense (Firewall, NAT, DMZ) |
| Phiên bản môi trường | Windows 11 Host + VirtualBox 7.x + pfSense CE 2.7.2-RELEASE (amd64) + Windows Server 2019/2022 + Ubuntu Server 22.04/24.04 |
| Mạng thực hành | WAN: Bridged · LAN: Host-Only `10.0.0.0/8` · DMZ: Internal Network `dmz-net` `172.16.0.0/16` |
| Tường lửa | pfSense (WAN DHCP · LAN `10.0.0.1/8` · DMZ `172.16.0.1/16`) |
| Máy trong LAN | Domain Controller `10.0.0.2/8` · LAN-Test (Ubuntu) `10.0.0.3/8` · Máy thật quản trị `10.0.0.100/8` |
| Máy trong DMZ | DMZ-Web (Windows Server + IIS) `172.16.0.2/16` |
| Bộ cài | `pfSense-CE-2.7.2-RELEASE-amd64.iso.gz` (mirror Netgate) |
| SHA-256 (.iso.gz) | `883fb7bc64fe548442ed007911341dd34e178449f8156ad65f7381a02b7cd9e4` |
| Link video minh họa | *https://www.youtube.com/@Tr%E1%BA%A7nNguy%E1%BB%85nMinhKh%C3%B4i-22* |

---

## 2. Cách dựng môi trường

1. Trên máy thật chạy `ipconfig` và `route print`; bảo đảm WAN/VPN/adapter ảo **không overlap** `10.0.0.0/8` và `172.16.0.0/16`. Không dùng NAT mặc định `10.0.2.0/24` cho WAN.
2. Tải `pfSense-CE-2.7.2-RELEASE-amd64.iso.gz`, kiểm tra SHA-256 bằng `Get-FileHash -Algorithm SHA256`, chỉ giải nén (7-Zip) khi hash khớp.
3. VirtualBox → Network Manager → Host-Only Networks: IPv4 host `10.0.0.100`, mask `255.0.0.0`, **tắt DHCP Server**, không đặt Gateway/DNS trên card này.
4. Tạo VM pfSense (FreeBSD 64-bit, 2 GB RAM, 2 vCPU, 16–20 GB đĩa) với 3 adapter: **1 = Bridged (WAN)**, **2 = Host-Only (LAN)**, **3 = Internal Network `dmz-net` (DMZ)**.
5. Gắn ISO, cài đặt, tháo ISO; trên console đặt LAN `10.0.0.1/8` (không bật DHCP).
6. Dựng Domain Controller (`10.0.0.2/8`, GW `10.0.0.1`, DNS `10.0.0.2`, forwarder `8.8.8.8`), vào WebGUI `https://10.0.0.1`, chạy Setup Wizard, đổi mật khẩu admin.
7. Gán OPT1 = DMZ `172.16.0.1/16`; dựng DMZ-Web (`172.16.0.2/16`, GW `172.16.0.1`, bật IIS); dựng LAN-Test (`10.0.0.3/8`, GW `10.0.0.1`) khi làm Tình huống 2.
8. Trên tab LAN: Disable hai *Default allow LAN to any* (giữ Anti-Lockout Rule) → **Reset States** → tạo rule nền tảng `Pass LAN net → Any`.
9. Tạo snapshot `Before-LAB5` cho các VM trước khi làm các tình huống.

**Lưu ý đạo đức & an toàn:** pfSense CE 2.7.2 là bản cũ, chỉ dùng trong mạng lab ảo cô lập; không dùng cho production hoặc làm firewall Internet-facing. Bỏ tick *Block private/bogon networks* chỉ trong lúc kiểm thử lab.

---

## 3. Các tình huống đã thực hiện

Đánh dấu ✅ sau khi chạy xong và có ảnh bằng chứng.

| STT | Tình huống | Kết quả mong đợi | Trạng thái |
|-----|------------|------------------|------------|
| 1 | Kiểm tra SHA-256 của `.iso.gz` | Hash trùng giá trị công bố | ☐ |
| 2 | Cài pfSense, đặt LAN `10.0.0.1/8` trên console | Máy thật ping được `10.0.0.1` | ☐ |
| 3 | Domain Controller + DNS Forwarder | `ping 8.8.8.8`, `Resolve-DnsName` hoạt động | ☐ |
| 4 | WebGUI + Setup Wizard + Dashboard | Dashboard hiển thị WAN/LAN đúng | ☐ |
| 5 | Cấu hình DMZ + dựng DMZ-Web (IIS) | `curl.exe http://localhost` thành công | ☐ |
| 6 | Kiểm tra Outbound NAT (Automatic/Hybrid) | Có Automatic Rules cho LAN và DMZ | ☐ |
| 7 | Chuẩn hóa ruleset LAN, Disable rule + Reset States | DC mất Internet với phiên ping mới | ☐ |
| 8 | **TH1** – Chặn ICMP, cho DNS/HTTP/HTTPS | Ping fail; DNS và HTTPS OK | ☐ |
| 9 | **TH2** – Chỉ cho một host (`10.0.0.2`) ra Internet | DC ra được; LAN-Test không | ☐ |
| 10 | **TH3** – Cô lập DMZ khỏi LAN (có baseline) | Baseline OK → sau Block ping DC fail; DMZ vẫn ra Internet | ☐ |
| 11 | **TH4** – Port Forward WAN:8080 → `172.16.0.2:80` | Máy thật thấy trang IIS | ☐ |
| 12 | **TH5** – Bật log và đọc Firewall Log | Có bản ghi *Block* của rule đã bật log | ☐ |

---

## 4. Cấu trúc thư mục repo

```
LAB_AT_BMHTTT/
└── LAB5/
    ├── README.md
    ├── CNPM1-LAB5_1150080059-Tran_Nguyen_Minh_Khoi.docx   # Báo cáo Word
    ├── outputs/                                          # Log/output đã làm sạch
    │   ├── sha256_check.txt
    │   ├── firewall_rules_LAN.txt
    │   ├── firewall_log_block_icmp.txt
    │   └── ...
    ├── evidence_sha256.csv                               # Hash bằng chứng
    └── screenshots/                                      # Ảnh chụp theo thứ tự trong báo cáo
```

> Không đưa `config.xml` của pfSense lên repo khi chưa làm sạch (chứa hash mật khẩu và cấu hình hệ thống).

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân | Cách khắc phục |
|-----|-------------|----------------|
| Hash `.iso.gz` không khớp | Tải lỗi/sai file | Tải lại từ mirror Netgate, không giải nén khi hash chưa khớp |
| Tên card là `em0/em1` hoặc `vtnet0/1` khác hình mẫu | Loại NIC ảo khác nhau | Đối chiếu MAC và thứ tự Adapter 1 = WAN, 2 = LAN, 3 = DMZ |
| Máy thật không ping được `10.0.0.1` | Sai Host-Only, IP host chưa `10.0.0.100/8`, DHCP còn bật | Kiểm tra Adapter 2 và Network Manager; tắt DHCP |
| WAN không nhận IP | Bridged chọn sai card vật lý | Chọn đúng Wi-Fi/Ethernet đang dùng; hoặc dùng NAT Network `192.168.250.0/24` |
| Vào `https://10.0.0.1` báo cảnh báo chứng chỉ | Chứng chỉ tự ký | Chọn Advanced → Proceed (chỉ trong lab) |
| Đã Disable rule nhưng vẫn ping được | State cũ còn tồn tại (stateful firewall) | Dừng `ping -t`, Apply Changes, **Reset States**, ping phiên mới |
| Ping DMZ → LAN fail trước khi Block | Windows Firewall trên DC chặn ICMP | Thêm rule `LAB-Allow-ICMPv4-Echo` bằng `netsh`, làm baseline trước |
| Rule Block không có tác dụng | Block nằm dưới Pass tổng quát | Đưa Block lên trên Pass (rule xét từ trên xuống) |
| Port Forward `8080` không vào được | IIS chưa chạy, Windows Firewall DMZ-Web, chưa bỏ tick Block private/bogon, thử sai IP | Kiểm tra IIS và `Diagnostics → Test Port`, bỏ tick Block private/bogon (lab), truy cập đúng IP WAN |
| Tự khóa WebGUI | Xóa/Disable Anti-Lockout Rule | Giữ Anti-Lockout; nếu lỡ, vào console chọn `8) Shell` rồi chạy `pfctl -d` tạm thời để vào lại |
| Chỉ ping `google.com` rồi kết luận firewall chặn | Kết quả còn phụ thuộc DNS | Tách phép thử: `ping 8.8.8.8` (IP) và `Resolve-DnsName example.com -Server 8.8.8.8` (DNS) |
| Clone DC để làm LAN-Test gây lỗi AD | Clone DC không theo quy trình AD DS | Tạo LAN-Test là VM độc lập (Ubuntu minimal) |

---

## 6. Cam kết

- Ảnh chụp lấy trực tiếp từ VM/PC của sinh viên; output/log khớp timestamp bài làm.
- Không sử dụng ảnh/log của người khác.
- Không upload mật khẩu, token, cookie, `config.xml` chưa làm sạch, dữ liệu cá nhân hay thông tin định danh hệ thống thật.
- Chỉ thực hành trong mạng lab cô lập do chính mình dựng.

---

**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** 10/2026
