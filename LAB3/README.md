# Báo cáo Bài Thực Hành – LAB 3

## 1. Thông tin sinh viên

- **Họ và tên sinh viên:** Trần Nguyễn Minh Khôi
- **Mã số sinh viên (MSSV):** 1150080059

## 2. Thông tin bài Lab

- **Tên bài Lab:** Lab 3: Nhận diện và ứng phó các mối đe dọa đến an toàn thông tin  
  (Identifying and Responding to Information Security Threats)
- **Năm học:** 2026–2027
- **Bộ môn:** An toàn Thông tin

## 3. Phiên bản môi trường thực hành

| Máy  | Hệ điều hành              | Vai trò chính |
|------|---------------------------|---------------|
| WS1  | Windows Server 2025       | Endpoint chính: Defender, Sysmon, Autoruns, Process Explorer, Event Log, EICAR, persistence, HTTP loopback |
| WS2  | Windows Server 2025       | Máy tham chiếu / backup Evidence |
| UBU  | Ubuntu (22.04/24.04 LTS)  | Phân tích offline, tcpdump/Wireshark, SHA-256, mẫu phishing/CSV |

| Thành phần              | Phiên bản / cấu hình |
|-------------------------|----------------------|
| Endpoint protection     | Microsoft Defender (Real-time + Tamper Protection bật) trên WS1 |
| Shell                   | Windows PowerShell 5.1 / 7.x (WS1, WS2); bash (Ubuntu) |
| Sysmon                  | 15.22 (schema 4.90) – cài trên WS1 |
| Autoruns                | 14.3 – cài trên WS1 |
| Process Explorer        | 17.14 – cài trên WS1 |
| Wireshark / tcpdump     | 4.6.x hoặc mới hơn (WS1 và/hoặc Ubuntu) |
| Python                  | 3.10+ (WS1) / python3 (Ubuntu) |
| Gói dữ liệu             | LAB3_Threats_Assets.zip |
| SHA-256 gói dữ liệu     | 96236f95ce59d0cc37f9b7ab8fbb04522f21870cd0b53aad6e52da5a23655439 |

> **Tài liệu hướng dẫn chi tiết:** `LAB3_HuongDan_ChiTiet_2WS2025_1Ubuntu.docx`  
> (điều chỉnh từ bài lab gốc Windows 11 workstation sang cấu hình 2× Windows Server 2025 + 1 Ubuntu)

## 4. Cách dựng môi trường (tóm tắt)

### WS1 (Windows Server 2025 – máy chính)
1. Tạo thư mục `C:\LAB3\{Evidence,Tools,Downloads,Assets}`.
2. Sao chép `LAB3_Threats_Assets.zip` → kiểm tra SHA-256 → giải nén.
3. Cài Python ≥ 3.10 và Wireshark (giữ Npcap).
4. Tải Sysmon, Autoruns, Process Explorer từ `download.sysinternals.com` → giải nén vào `C:\LAB3\Tools`.
5. Xác nhận phiên bản công cụ trước khi thực hành.

### WS2 (Windows Server 2025 – máy phụ)
- Tạo `C:\LAB3\Evidence_Backup` để lưu bản sao Evidence từ WS1.
- Không bắt buộc cài Sysmon/Autoruns.

### Ubuntu
```bash
sudo apt update
sudo apt install -y wireshark tcpdump python3 coreutils
mkdir -p ~/LAB3/Evidence ~/LAB3/Assets
# Sao chép samples/, data/, scripts/ từ gói LAB3 vào ~/LAB3/Assets
```

**Mạng:** Host-only / Internal Network giữa 3 máy. Mọi traffic tải chỉ tạo trên `127.0.0.1` của WS1.

## 5. Các tình huống đã thực hiện

| Tình huống | Nội dung chính | Máy chính | Kết quả |
|------------|----------------|-----------|---------|
| **TH0** | Baseline hệ thống (OS, Defender, Firewall, process) | WS1 | PASS |
| **TH1** | Risk register ≥ 5 tài sản + phân loại 5 nguồn đe dọa | WS1 (+ thảo luận) | PASS |
| **TH2** | EICAR + Defender detection/quarantine | WS1 | PASS |
| **TH3** | Event 4624/4625/4648 + password rotation (lab3user) | WS1 | PASS |
| **TH4** | Persistence LAB3_Run_Demo + LAB3_Persistence_Demo; listener 127.0.0.1:8080 | WS1 | PASS |
| **TH5** | Capture HTTP loopback (plaintext) vs TLS/443 | WS1 (+ Ubuntu tùy chọn) | PASS |
| **TH6** | local_load_test.py (50 req/5 worker) + phân tích ddos_sample.csv & mailbomb_sample.csv | WS1 (+ Ubuntu) | PASS |
| **TH7** | Phân tích phishing_email.txt (≥ 5 chỉ dấu) + phân loại 6 case Social Engineering | WS1 (+ Ubuntu) | PASS |
| **Cleanup** | Xóa artefact LAB3, verify Defender, SHA-256 Evidence | WS1 | PASS |

## 6. Tóm tắt PASS/FAIL

Tất cả các tình huống TH0–TH7 và bước Cleanup đạt **PASS** theo điều kiện trong hướng dẫn chi tiết.

## 7. Lỗi gặp phải và cách khắc phục

| Lỗi | Cách khắc phục |
|-----|----------------|
| Defender chặn ngay khi `Set-Content` file EICAR | Đây là hành vi mong đợi. Ghi lỗi vào `eicar_write_error.txt`, kiểm tra Protection history / `Get-MpThreatDetection`. |
| Sysmon chưa hiện Event ID 1 ngay sau cài | Tạo process Notepad → refresh log Operational. |
| Không thấy listener 8080 | Kiểm tra `Get-NetTCPConnection -LocalPort 8080 -State Listen`; đảm bảo bind `127.0.0.1`. |
| Version công cụ lệch yêu cầu | Dừng thực hành, báo giảng viên trước khi tiếp tục. |
| winget không có trên Server | Cài Python/Wireshark thủ công từ trang chính thức. |

**Nguyên tắc chung:** Giữ Real-time Protection + Tamper Protection bật; không tạo exclusion; chỉ thao tác Host-only / localhost.

## 8. Cấu trúc Evidence chính (trên WS1)

```
C:\LAB3\Evidence\
├── start_time.txt
├── baseline_os.txt / baseline_defender.txt / baseline_firewall.txt
├── baseline_network.txt / baseline_processes.txt
├── defender_eicar.txt
├── auth_events_before_rotation.txt
├── autoruns_before.csv / autoruns_after.csv / autoruns_diff.txt
├── sysmon_persistence.txt
├── local_load_test.txt
├── ddos_sources.txt
├── mail_sender_counts.txt / mail_volume.txt
├── evidence_sha256.csv
└── (ảnh chụp H3–H11)
```

## 9. Lưu ý quan trọng

- Mọi ảnh giao diện chụp trực tiếp từ VM/PC sinh viên sau khi thực hiện bước tương ứng.
- Không nhập tài khoản/mật khẩu thật, token, cookie, email cá nhân vào máy lab.
- Không tắt Defender, không tạo exclusion, không phục hồi file bị quarantine.
- Script tải chỉ nhắm `127.0.0.1:8080`; không sửa để chỉ ra IP/hostname khác.
- Không thực hiện ARP poisoning, DNS spoofing, mail bombing thật, DDoS thật hoặc MITM chủ động ra ngoài phạm vi lab.
- Không upload mật khẩu, token, dữ liệu cá nhân, email thật, log chưa làm sạch lên GitHub.
- Sau khi nộp đủ Evidence, có thể revert snapshot sạch nếu đã tạo.

## 10. File đính kèm trong thư mục LAB3/

- `README.md` (file này)
- `LAB3_HuongDan_ChiTiet_2WS2025_1Ubuntu.docx` – hướng dẫn chi tiết đầy đủ lệnh & checklist
- `evidence_sha256.csv` (sau khi hoàn thành)
- Các file output/log đã làm sạch (không chứa mật khẩu/token)
