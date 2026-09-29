# LAB 4 – Khảo sát và đánh giá bề mặt mạng bằng Nmap

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
| Tên lab | LAB 4 – Khảo sát và đánh giá bề mặt mạng bằng Nmap |
| Phiên bản môi trường | Windows 11 Host + VirtualBox 7.x + Kali Linux 2024.x + Metasploitable 2 |
| Mạng thực hành | VirtualBox Host-Only `192.168.56.0/24` |
| Máy quét | Kali Linux (Host-Only: `192.168.56.10`) |
| Máy đích | Metasploitable 2 (Host-Only: `192.168.56.101`) |
| Công cụ chính | Nmap (kèm Npcap trên Windows), Zenmap (tùy chọn) |
| Link video minh họa | *https://www.youtube.com/@Tr%E1%BA%A7nNguy%E1%BB%85nMinhKh%C3%B4i-22* |

---

## 2. Cách dựng môi trường

1. Cài VirtualBox bản hiện hành, bật Intel VT-x/AMD-V trong BIOS.
2. Tạo Host-Only Network `192.168.56.0/24` (có thể bật DHCP).
3. Cài/import Kali Linux VM → Adapter 1 = Host-Only (NAT chỉ bật tạm khi cần `apt update`).
4. Import Metasploitable 2 → **chỉ** Host-Only Adapter (tuyệt đối không Bridged).
5. Trên Windows Host: cài Nmap + Npcap + Zenmap từ [nmap.org/download.html](https://nmap.org/download.html).
6. Trên Kali: `sudo apt update && sudo apt install nmap` (Zenmap tùy chọn).
7. Tạo snapshot `Before-LAB4` cho cả hai VM trước khi thực hành.
8. Kiểm tra kết nối: `ping -c 4 192.168.56.101` từ Kali.

**Lưu ý đạo đức:** Chỉ quét các máy ảo do chính mình dựng trong mạng Host-Only. Không quét IP/tên miền bên ngoài khi chưa được ủy quyền.

---

## 3. Các tình huống đã thực hiện

| STT | Tình huống | Kết quả |
|-----|------------|---------|
| 1 | Host discovery (`-sn`) toàn dải 192.168.56.0/24 | PASS |
| 2 | TCP Connect scan (`-sT`) | PASS |
| 3 | SYN scan (`-sS`) | PASS |
| 4 | FIN / Xmas / NULL scan | PASS |
| 5 | ACK scan | PASS |
| 6 | UDP scan (20 cổng phổ biến) | PASS |
| 7 | Version detection (`-sV`) | PASS |
| 8 | OS detection (`-O`) | PASS |
| 9 | Aggressive scan (`-A`) | PASS |
| 10 | NSE smb-os-discovery + smb-vuln-ms17-010 | PASS |
| 11 | Xuất kết quả (-oN, -oX, -oG, -oA, xsltproc) | PASS |
| 12 | So sánh before/after hardening | PASS |

---

## 4. Cấu trúc thư mục repo

```
LAB_AT_BMHTTT/
└── LAB4/
    ├── README.md
    ├── CNPM1-LAB4_1150080059-Tran_Nguyen_Minh_Khoi.docx   # Báo cáo Word
    ├── outputs/                                          # Log/output đã làm sạch
    │   ├── host_discovery.txt
    │   ├── syn_scan.nmap
    │   ├── version_detection.xml
    │   ├── aggressive_scan.gnmap
    │   └── ...
    ├── evidence_sha256.csv                               # Hash bằng chứng
    └── screenshots/                                      # Ảnh chụp (nếu có)
```

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân | Cách khắc phục |
|-----|-------------|----------------|
| `nmap: command not found` (Windows) | PATH chưa nạp sau cài đặt | Đóng/mở lại Terminal/PowerShell, hoặc cài lại với tùy chọn Register Nmap Path |
| Ping thất bại giữa Kali ↔ Metasploitable | Sai adapter / subnet / firewall ICMP | Kiểm tra cùng Host-Only Network, IP không trùng, máy đích đã boot |
| `-sS` báo cần quyền root | SYN scan dùng raw packet | Chạy với `sudo` |
| NSE script timeout / không kết nối | Cổng 445 filtered/closed hoặc dịch vụ không chạy | Ghi nhận đúng trạng thái, không suy diễn “đã vá” |
| Zenmap không mở trên Kali | Thiếu gói GTK / DISPLAY | Hoàn thành lab bằng Terminal (không bắt buộc Zenmap) |

---

## 6. Cam kết

- Ảnh chụp lấy trực tiếp từ VM/PC của sinh viên; output/log khớp timestamp bài làm.
- Không sử dụng ảnh/log của người khác.
- Không upload mật khẩu, token, cookie, dữ liệu cá nhân hay thông tin định danh hệ thống thật.
- Chỉ thực hành trên mạng Host-Only do chính mình dựng.

---

**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** 09/2026
