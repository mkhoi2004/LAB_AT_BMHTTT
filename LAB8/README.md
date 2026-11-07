# LAB 8 – Local DNS Attack trên pfSense và mạng ảo cô lập

**Họ và tên:** Trần Nguyễn Minh Khôi  
**MSSV:** 1150080059  
**Lớp:** CNPM1  
**Bộ môn:** An toàn thông tin  
**Học phần:** Thực hành An toàn hệ thống thông tin  
**Năm học:** 2026–2027  
**Youtube:** https://www.youtube.com/@Tr%E1%BA%A7nNguy%E1%BB%85nMinhKh%C3%B4i-22

---

## 1. Thông tin Lab

| Mục | Nội dung |
|-----|----------|
| Tên lab | LAB 8 – Local DNS Attack: HOSTS-file redirection, Direct DNS Spoofing và DNS Cache Poisoning |
| Nền tảng | VirtualBox; kế thừa pfSense, APP-TEST và DMZ-WEB của môi trường trước |
| Máy ảo | pfSense · DNS-SERVER (Ubuntu Server 24.04, BIND9) · VICTIM (Ubuntu 24.04) · ATTACKER (Kali Linux) · APP-TEST/AUTH-DNS · DMZ-WEB |
| Mạng thực hành | Internal Network `dns-lab`, subnet `10.9.0.0/24` |
| Địa chỉ | pfSense DNSLAB `10.9.0.1`; DNS-SERVER `10.9.0.10`; VICTIM `10.9.0.100`; Kali `10.9.0.200` |
| Hạ tầng kế thừa | APP-TEST/AUTH-DNS `172.16.6.20`; DMZ-WEB `172.16.6.10`; Host MGMT `192.168.56.1` |
| DNS thử nghiệm | Zone `lab9.test`; forward zone `external.lab9.test` → AUTH-DNS `172.16.6.20` |
| Công cụ | BIND9, dig, nslookup/resolvectl, Wireshark, tcpdump, Scapy |
| Tài nguyên theo hướng dẫn | `01_DNS_SERVER` → `07_REPORT`, `SHA256SUMS.txt` |
| Link video minh họa | *(điền link)* |

> **Lưu ý số hiệu:** file `LAB8_Local_DNS_Attack_pfSense_2026.docx` ghi LAB 8 ở trang đầu nhưng nhiều phần bên trong ghi LAB 9. README này dùng **LAB 8** theo tên bài yêu cầu; giữ nguyên `lab9.test`, `dns_spoof_lab9.py` và tên snapshot trong tài liệu để tránh lệch cấu hình.

### Mục tiêu và điều kiện thử nghiệm

- Cấu hình BIND9 làm DNS authoritative cho zone nội bộ và recursive/cache resolver cho DNS-LAB.
- Phân biệt chuyển hướng bằng `/etc/hosts`, giả mạo phản hồi tới client và đầu độc cache của resolver.
- Đối chiếu transaction ID, UDP port, query name và kết quả phân giải trên gói tin.
- Khôi phục môi trường và kiểm chứng các biện pháp phòng thủ sau thử nghiệm.

**Phạm vi:** chỉ dùng các VM và tên miền `.test` trong mạng lab. NAT chỉ bật tạm để cài gói; tắt trước pha tấn công. Trang web mô phỏng không thu thập thông tin đăng nhập.

---

## 2. Cách dựng môi trường

**Phần A – Mạng và chính sách pfSense**

1. Tắt các VM cần thay đổi và tạo snapshot. Tài liệu dùng tên `LAB9_START_FROM_LAB8` cho pfSense; sao lưu Kali trước khi thêm card mạng.
2. Thêm card mạng cho pfSense vào **Internal Network `dns-lab`**. Gán interface **DNSLAB**, IPv4 tĩnh `10.9.0.1/24`, **không chọn Upstream Gateway**.
3. Gắn DNS-SERVER, VICTIM và Kali vào cùng `dns-lab`; đặt IP theo bảng ở mục 1, gateway `10.9.0.1`.
4. Cài đủ package khi còn NAT, sau đó tắt card NAT trên ba VM. Riêng card `dns-lab` của Kali đặt **Promiscuous Mode = Allow VMs** trong thời gian bắt gói.
5. Tạo ruleset DNSLAB bên dưới, **Apply Changes → Reset States**. Giữ nguyên mạng LAN-DOMAIN và mạng quản trị hiện có.

### Ruleset DNSLAB từ trên xuống

| # | Action | Source | Destination | Protocol / Port | Mục đích |
|---|--------|--------|-------------|-----------------|----------|
| 1 | Pass | `10.9.0.10` | `172.16.6.20` | TCP/UDP 53 | Resolver truy vấn AUTH-DNS mô phỏng |
| 2 | Pass | `10.9.0.0/24` | `172.16.6.10` | TCP 80 | Truy cập website hợp lệ trong DMZ |
| 3 | Block + Log | `10.9.0.0/24` | `10.6.0.0/24` | Any | Bảo vệ LAN-DOMAIN |
| 4 | Block + Log | `10.9.0.0/24` | `192.168.56.0/24` | Any | Bảo vệ mạng quản trị |
| 5 | Block + Log | `10.9.0.0/24` | Any | Any | Cô lập các lưu lượng còn lại, gồm WAN |

**Phần B – DNS, client và web mô phỏng**

6. **DNS-SERVER:** cài BIND9 và công cụ kiểm thử; sao lưu cấu hình gốc. Nạp `named.conf.options`, `named.conf.local`, zone thuận/nghịch từ `01_DNS_SERVER`.
7. Cấu hình authoritative cho `lab9.test`, recursion/cache cho `10.9.0.0/24`, forward `external.lab9.test` tới `172.16.6.20`. Thiết lập `send-cookie no` trong tài nguyên chỉ áp dụng cho AUTH-DNS mô phỏng.
8. Kiểm tra bằng `named-checkconf`, `named-checkzone`, rồi khởi động lại BIND9. Kiểm thử cả zone thuận và zone nghịch trước khi làm các task.
9. **VICTIM:** dùng duy nhất DNS `10.9.0.10`; cấu hình qua Netplan hoặc Network Settings, kiểm tra bằng `resolvectl`. Không sửa trực tiếp `/etc/resolv.conf`.
10. **Kali:** cài Scapy, Wireshark, tcpdump; chuẩn bị `dns_spoof_lab9.py` và `fake_index.html` từ tài nguyên. Chạy web mô phỏng tại `10.9.0.200`.
11. **APP-TEST:** chạy `auth_dns_delayed.py`. Tên `www.external.lab9.test` phải trả `172.16.6.10`; phản hồi chậm có chủ đích khoảng 1,5 giây để quan sát cuộc đua DNS.

**Phần C – Baseline và ba nhiệm vụ**

12. **Baseline:** từ VICTIM và DNS-SERVER, xác nhận `www.external.lab9.test → 172.16.6.10`. Trên Kali, bắt gói card `dns-lab` với filter `dns.qry.name == "www.external.lab9.test"`. Chỉ chuyển sang thử nghiệm khi baseline đúng.
13. **Task 1 – HOSTS-file redirection:** sao lưu rồi sửa `/etc/hosts` trên VICTIM theo hướng dẫn để `www.lab9.test` trỏ `10.9.0.200`. Đối chiếu `getent`/`curl` với `dig @10.9.0.10 www.lab9.test`; sau đó khôi phục hosts và xóa cache client.
14. **Task 2 – Direct DNS Spoofing:** làm sạch cache, chạy script chế độ `victim` trên đúng interface DNS-LAB, rồi tạo truy vấn mới từ VICTIM. Kiểm tra phản hồi giả trả `10.9.0.200`, transaction ID và UDP port khớp query. Ghi nhận nếu phản hồi hợp lệ thắng cuộc đua.
15. **Task 3 – DNS Cache Poisoning:** làm sạch cache resolver, chạy script chế độ `resolver`, tạo query từ VICTIM. Dừng script rồi truy vấn lại; nếu poisoning thành công, resolver vẫn trả `10.9.0.200` trong TTL mô phỏng 120 giây. Đối chiếu thêm cache dump BIND9.
16. **Khôi phục:** dừng script spoofing và fake web trên Kali, flush cache BIND9 và VICTIM, khôi phục hosts; giữ AUTH-DNS phục vụ để kiểm thử và xác nhận DNS trở về `172.16.6.10`. Sau khi hoàn tất kiểm thử phòng thủ, dừng AUTH-DNS và trả Promiscuous Mode của Kali về **Deny**.
17. **Phòng thủ:** bỏ ngoại lệ tắt DNS COOKIE, khởi động lại BIND9, lặp kiểm thử theo hướng dẫn và ghi nhận khác biệt. Phân tích thêm segmentation, DNS egress control, source-port randomization và DNSSEC cho zone có ký.

> DNSSEC không tự bảo vệ zone `.test` unsigned trong bài. Chỉ kết luận spoofing/poisoning thành công khi có kết quả truy vấn và bằng chứng gói tin/cache phù hợp.

---

## 3. Các tình huống kiểm thử

*Đánh dấu sau khi có ảnh hoặc log từ môi trường thực tế.*

| STT | Tình huống | Kết quả mong đợi / minh chứng | Trạng thái |
|-----|------------|------------------------------|------------|
| 0 | Dựng DNSLAB và đặt IP các VM | Đúng subnet/IP; NAT đã tắt trong pha tấn công | ☐ |
| 1 | Interface và ruleset pfSense | DNSLAB `10.9.0.1/24`, không gateway; chặn LAN/MGMT/WAN | ☐ |
| 2 | Kiểm tra BIND9 | `named-checkconf` và `named-checkzone` thành công | ☐ |
| 3 | Zone thuận/nghịch | Kết quả đúng cấu hình tài nguyên | ☐ |
| 4 | Baseline external | `www.external.lab9.test → 172.16.6.10` | ☐ |
| 5 | Kali quan sát DNS | Thấy query VICTIM → resolver và resolver → AUTH-DNS | ☐ |
| 6 | Task 1 – hosts | `getent`/`curl` tới Kali; `dig` trực tiếp vẫn trả DNS gốc | ☐ |
| 7 | Khôi phục Task 1 | Hosts được phục hồi trước Task 2 | ☐ |
| 8 | Task 2 – direct spoofing | VICTIM nhận `10.9.0.200` khi phản hồi giả thắng; có pcap | ☐ |
| 9 | Task 3 – poisoning | Cache resolver chứa `10.9.0.200`; dừng script vẫn trả IP giả trong TTL | ☐ |
| 10 | Flush và restore | Truy vấn qua resolver trở về `172.16.6.10` | ☐ |
| 11 | Phòng thủ | Promiscuous Mode = Deny; bỏ ngoại lệ DNS COOKIE; ghi nhận kiểm thử | ☐ |
| 12 | Báo cáo | Đủ ảnh, log, bảng baseline/attack/restore và 10 câu hỏi phân tích | ☐ |

---

## 4. Cấu trúc thư mục repo

*Cấu trúc dự kiến; chỉ thêm báo cáo, log và ảnh sau khi thực hiện.*

```text
LAB_AT_BMHTTT/
└── LAB8/
    ├── README.md
    ├── CNPM1-Lab8_1150080059-Tran_Nguyen_Minh_Khoi.docx
    ├── outputs/
    │   ├── bind_validation.txt
    │   ├── baseline_dns.txt
    │   ├── task1_hosts.txt
    │   ├── task2_direct_spoof.txt
    │   ├── task3_cache_dump.txt
    │   ├── restore_dns.txt
    │   └── hardening_results.txt
    ├── captures/
    │   ├── task2_direct_spoof.pcapng
    │   └── task3_cache_poisoning.pcapng
    └── screenshots/
```

> Báo cáo đặt tên Lab8 theo README này; tài liệu gốc đang ghi mẫu tên Lab9, cần đối chiếu yêu cầu nộp của giảng viên. Chỉ đưa lên repo bằng chứng đã rà soát; không đăng mật khẩu, private key hoặc `config.xml` chưa làm sạch.

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân / cách kiểm tra | Cách khắc phục |
|-----|----------------------------|----------------|
| VICTIM không phân giải được | Sai DNS client hoặc BIND không listen tại `10.9.0.10` | Kiểm tra Netplan/`resolvectl`, dịch vụ BIND và địa chỉ listen |
| Resolver không hỏi APP-TEST | Forward zone sai, cache còn hoặc rule chặn DNS | Kiểm tra `named.conf.local`, flush cache, kiểm tra UDP/TCP 53 |
| Kali không thấy query | Sai interface hoặc Promiscuous Mode = Deny | Chọn card `dns-lab`, bật Allow VMs trong lúc thực hành |
| Script không có dòng forged | Sai domain/mode hoặc query được trả từ cache | Đối chiếu tham số trong tài nguyên, xóa cache rồi tạo query mới |
| VICTIM luôn nhận `172.16.6.10` | Phản hồi hợp lệ tới trước | Đối chiếu pcap, kiểm tra delay AUTH-DNS theo hướng dẫn |
| Task 2 đạt nhưng Task 3 không đạt | Resolver từ chối phản hồi, tuple/cookie không khớp | Kiểm tra packet details và ngoại lệ COOKIE chỉ cho AUTH-DNS mô phỏng |
| Không thấy cache dump | File dump nằm ở directory khác | Kiểm tra directory BIND; tìm file `*dump*` trong `/var/cache/bind` |
| BIND restart lỗi | Cấu hình hoặc zone chưa hợp lệ | Chạy `named-checkconf`, `named-checkzone`; xem `journalctl -u bind9 -n 50` |
| Mất WebGUI pfSense | Truy cập từ sai mạng | Quản trị qua MGMT tại `192.168.56.254`; giữ Anti-Lockout rule |

---

## 6. Cam kết

- Chỉ thực hành trong mạng ảo cô lập và trên các VM do mình quản trị.
- Ảnh, log và packet capture dùng làm kết quả phải lấy từ môi trường thực tế của sinh viên.
- Báo cáo phân biệt rõ baseline, kết quả tấn công và kết quả khôi phục; không dùng hình trong tài liệu làm bằng chứng đã thực hiện.
- Dừng công cụ mô phỏng, khôi phục hosts/cache và trả Promiscuous Mode về Deny sau buổi lab.
- Không đưa mật khẩu, token, private key hoặc cấu hình chứa bí mật lên repo.

---

**Tài liệu hướng dẫn:** `LAB8_Local_DNS_Attack_pfSense_2026.docx`  
**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** *(điền ngày thực tế)*
