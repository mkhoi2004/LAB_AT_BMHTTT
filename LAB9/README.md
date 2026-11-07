# LAB 10 – Remote DNS Cache Poisoning trong môi trường pfSense cô lập

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
| Tên lab | LAB 10 – Remote DNS Cache Poisoning / Kaminsky Simulation |
| Nền tảng | VirtualBox và pfSense; kế thừa DNS-LAB của bài Local DNS Attack |
| Máy ảo | pfSense · DNS-SERVER (BIND9 / resolver mô phỏng) · VICTIM · ATTACKER Kali · APP-TEST/AUTH-DNS-RELAY · DMZ-WEB |
| DNS-LAB | `dns-lab`, subnet `10.9.0.0/24`; pfSense `10.9.0.1`; DNS-SERVER `10.9.0.10`; VICTIM `10.9.0.100` |
| ATTACKNET | `attack-net`, subnet `10.10.0.0/24`; pfSense `10.10.0.1`; Kali `10.10.0.200` |
| Hạ tầng kế thừa | AUTH-DNS/RELAY `172.16.6.20`; DMZ-WEB `172.16.6.10`; Host MGMT `192.168.56.1` |
| Tên miền thử nghiệm | `example.lab10.test`; truy vấn kiểm chứng `www.example.lab10.test` |
| Công cụ / script | BIND9, dig, tcpdump/Wireshark; `vuln_resolver_lab10.py`, `kaminsky_remote_lab10.py`, `evil_dns_lab10.py`, `auth_dns_relay_lab10.py` |
| Tài nguyên theo hướng dẫn | `01_DNS_SERVER` → `07_REPORT`, `SHA256SUMS.txt` |
| Link video minh họa | *(điền link)* |

### Khác với bài Local DNS Attack

| Nội dung | Local DNS Attack | LAB 10 |
|----------|------------------|--------|
| Vị trí Kali | Cùng DNS-LAB với resolver/client | Subnet ATTACKNET riêng, đi qua pfSense |
| Promiscuous Mode | Allow VMs trong lúc bắt gói | Deny |
| Quan sát query upstream | Kali nhìn thấy query trong mô hình lab | Kali không được thấy luồng resolver ↔ AUTH-DNS |
| Cơ chế thử nghiệm | Dùng thông tin query quan sát được | Mô phỏng đoán TXID bằng batch qua race relay |
| Resolver trong pha tấn công | BIND9 theo cấu hình lab local | Resolver mô phỏng có source port cố định `33333` |
| Mục tiêu cache | Bản ghi địa chỉ trong bài local | Delegation NS và glue A của zone mô phỏng |

> **Giới hạn mô hình:** LAB 10 tạm dừng BIND9 để dùng resolver có điểm yếu được tạo có chủ đích. Race relay chỉ mô phỏng quá trình đoán TXID, tránh phát hàng chục nghìn gói. Kết quả không chứng minh BIND9 hiện đại bị khai thác theo cùng cách.

**Phạm vi:** chỉ dùng mạng private và tên miền `.test` trong tài liệu. Kali không có card DNSLAB hoặc NAT hoạt động trong pha tấn công; ATTACKNET không được mở ra WAN.

---

## 2. Cách dựng môi trường

**Phần A – Tách ATTACKER sang ATTACKNET**

1. Tắt pfSense, DNS-SERVER, VICTIM và Kali để tạo snapshot. Tài liệu dùng tên `LAB10_START_FROM_LAB9` cho pfSense; sao lưu Kali trước khi tháo card DNSLAB.
2. Thêm card pfSense vào **Internal Network `attack-net`**; gán interface **ATTACKNET**, IPv4 tĩnh `10.10.0.1/24`, **không chọn Upstream Gateway**.
3. Tháo hoặc disable card `dns-lab` trên Kali. Gắn card `attack-net`, đặt IP `10.10.0.200/24`, gateway `10.10.0.1`, **Promiscuous Mode = Deny**.
4. Cài package qua NAT tạm nếu cần, rồi tắt NAT trước pha tấn công. Giữ nguyên DNS-SERVER `10.9.0.10`, VICTIM `10.9.0.100` và DNS của VICTIM là `10.9.0.10`.
5. Tạo ruleset bên dưới theo thứ tự trên từng interface; **Apply Changes → Reset States**. Không thay đổi LAN-DOMAIN hoặc Anti-Lockout rule.

### Ruleset tối thiểu

| Interface | # | Action | Source | Destination | Protocol / Port | Mục đích |
|-----------|---|--------|--------|-------------|-----------------|----------|
| DNSLAB | 1 | Pass | `10.9.0.100` | `10.9.0.10` | TCP/UDP 53 | Client truy vấn resolver |
| DNSLAB | 2 | Pass | `10.9.0.10` | `172.16.6.20` | UDP 53 | Resolver hỏi AUTH-DNS hợp lệ |
| DNSLAB | 3 | Pass | `10.9.0.10` | `10.10.0.200` | UDP 53 | Resolver hỏi evil DNS sau poisoning |
| DNSLAB | 4 | Pass | `10.9.0.0/24` | `10.10.0.200` | TCP 80 | VICTIM mở web mô phỏng |
| DNSLAB | 5 | Block + Log | `10.9.0.0/24` | `10.6.0.0/24` | Any | Bảo vệ LAN-DOMAIN |
| DNSLAB | 6 | Block + Log | `10.9.0.0/24` | `192.168.56.0/24` | Any | Bảo vệ MGMT |
| DNSLAB | 7 | Block + Log | `10.9.0.0/24` | Any | Any | Cô lập các lưu lượng còn lại |
| ATTACKNET | 1 | Pass | `10.10.0.200` | `10.9.0.10` | TCP/UDP 53 | Kali kích hoạt query tới resolver mô phỏng |
| ATTACKNET | 2 | Pass | `10.10.0.200` | `172.16.6.20` | UDP 5300 | Batch TXID gửi tới race relay của lab |
| ATTACKNET | 3 | Block + Log | `10.10.0.0/24` | `10.6.0.0/24` | Any | Bảo vệ LAN-DOMAIN |
| ATTACKNET | 4 | Block + Log | `10.10.0.0/24` | `192.168.56.0/24` | Any | Bảo vệ MGMT |
| ATTACKNET | 5 | Block + Log | `10.10.0.0/24` | Any | Any | Chặn WAN và đích không thuộc thử nghiệm |

> VICTIM và DNS-SERVER cùng subnet nên trao đổi trực tiếp giữa hai máy không đi qua pfSense. Rule UDP/5300 phục vụ riêng race relay và phải được disable/xóa sau lab.

**Phần B – Baseline BIND9 và xác nhận tính remote**

6. **DNS-SERVER:** sao lưu cấu hình trước; nạp BIND baseline trong `01_DNS_SERVER`. Forward `example.lab10.test` tới `172.16.6.20`, giới hạn recursion cho client tin cậy theo cấu hình baseline.
7. Giữ `dnssec-validation auto`; ngoại lệ `validate-except` chỉ dành cho namespace `.test` mô phỏng trong tài nguyên. Kiểm tra cấu hình rồi khởi động BIND9.
8. **APP-TEST:** dừng service DNS cũ nếu đang chạy; chạy `auth_dns_relay_lab10.py`. AUTH-DNS trả `www.example.lab10.test → 172.16.6.10`, giữ cửa sổ chờ khoảng 3 giây cho tên ngẫu nhiên và chỉ nhận batch UDP/5300 từ Kali lab.
9. **Baseline VICTIM:** xác nhận `www.example.lab10.test → 172.16.6.10` trước khi chuyển sang pha tấn công.
10. **Kiểm tra remote:** bắt gói trên Kali trong lúc VICTIM tạo query. Kali không được nhìn thấy lưu lượng `10.9.0.10 ↔ 172.16.6.20`. Khi phân tích cuộc đua DNS, đặt capture tại DNS-SERVER hoặc APP-TEST.

**Phần C – Mô phỏng poisoning và kiểm chứng cache**

11. **DNS-SERVER:** dừng BIND9 và chạy `vuln_resolver_lab10.py` theo tài nguyên. Resolver mô phỏng bind `10.9.0.10:53`, nhận query từ VICTIM/Kali lab và phát upstream từ UDP source port cố định `33333`.
12. **Kali:** chạy `evil_dns_lab10.py` tại `10.10.0.200:53` và web mô phỏng từ `fake_index.html` tại TCP/80 ở các terminal riêng.
13. **Task 1 – Remote Cache Poisoning:** chạy `kaminsky_remote_lab10.py`. Script kích hoạt query bằng tên ngẫu nhiên dưới `example.lab10.test`, rồi gửi 16 batch × 4096 TXID tới relay để mô phỏng không gian 16-bit. Kali không sniff TXID; relay phát phản hồi giả khi batch chứa TXID thật.
14. Kiểm tra AUTH-DNS có `[PENDING]` và `[HIT]`, resolver có `[CACHE] delegation poisoned`. Đối chiếu capture để xác nhận authority NS `ns.evil.example.lab10.test` và glue A `10.10.0.200`.
15. **Task 2 – Phân tích cache:** từ VICTIM, kiểm tra `www.example.lab10.test → 10.10.0.200` và mở trang mô phỏng. Chứng minh resolver chuyển query tiếp theo tới `10.10.0.200:53`; đối chiếu log, gói tin và kết quả client.
16. Giải thích NS, glue A và kiểm tra bailiwick. NS mô phỏng nằm trong `example.lab10.test`; không suy rộng rằng resolver thực tế sẽ chấp nhận mọi Additional record.

**Phần D – Khôi phục và hardening**

17. Dừng resolver mô phỏng, evil DNS, web mô phỏng và script tấn công khi không còn cần kiểm thử; khôi phục BIND9 baseline, kiểm tra cấu hình và khởi động lại dịch vụ.
18. Làm sạch cache client/resolver, xác nhận kết quả hợp lệ `172.16.6.10` khi AUTH-DNS còn phục vụ. Từ VICTIM, tạo nhiều query tên ngẫu nhiên và bắt gói để ghi nhận source port của BIND9 thay đổi thay vì cố định `33333`.
19. Kiểm tra Kali ATTACKNET không còn được recursion qua BIND baseline. Phân tích vai trò riêng của recursion ACL, source-port randomization, DNSSEC và segmentation.
20. Disable/xóa rule UDP/5300; dừng relay sau kiểm thử. Disable/remove ATTACKNET nếu không dùng tiếp; giữ Kali Promiscuous Mode = Deny hoặc khôi phục snapshot.

---

## 3. Các tình huống kiểm thử

*Đánh dấu sau khi có ảnh hoặc log từ môi trường thực tế.*

| STT | Tình huống | Kết quả mong đợi / minh chứng | Trạng thái |
|-----|------------|------------------------------|------------|
| 0 | Tạo ATTACKNET | Interface `10.10.0.1/24`, không Upstream Gateway | ☐ |
| 1 | Chuyển mạng Kali | `10.10.0.200`; không card DNSLAB/NAT hoạt động; Promiscuous Mode = Deny | ☐ |
| 2 | Ruleset cô lập | Chỉ mở các luồng lab; chặn LAN/MGMT/WAN | ☐ |
| 3 | BIND baseline và AUTH-DNS | Cấu hình hợp lệ; VICTIM nhận `172.16.6.10` | ☐ |
| 4 | Xác nhận remote | Kali không thấy traffic DNS-SERVER ↔ AUTH-DNS | ☐ |
| 5 | Chuyển resolver mô phỏng | BIND dừng; upstream dùng `10.9.0.10:33333` | ☐ |
| 6 | Evil DNS và fake web | Dịch vụ mô phỏng sẵn sàng tại `10.10.0.200` | ☐ |
| 7 | Task 1 – đoán TXID mô phỏng | AUTH-DNS có `[PENDING]`, `[HIT]`; resolver có `[CACHE]` | ☐ |
| 8 | Kiểm chứng delegation | Có NS/glue giả và query tiếp theo tới evil DNS | ☐ |
| 9 | Task 2 – hậu quả tại VICTIM | DNS trả `10.10.0.200`; mở trang LAB10 Fake Site | ☐ |
| 10 | Restore BIND9 | BIND chạy lại; VICTIM nhận `172.16.6.10` | ☐ |
| 11 | Hardening | Source port thay đổi; Kali không được recursion; bỏ rule UDP/5300 | ☐ |
| 12 | Báo cáo | Đủ ảnh/log/pcap, bảng so sánh và 12 câu hỏi phân tích | ☐ |

### Bảng đối chiếu kết quả

| Kiểm thử | Baseline | Sau poisoning mô phỏng | Sau khôi phục |
|----------|----------|------------------------|---------------|
| Resolver hoạt động | BIND9 baseline | `vuln_resolver_lab10.py` | BIND9 baseline |
| `www.example.lab10.test` | `172.16.6.10` | `10.10.0.200` nếu thành công | `172.16.6.10` |
| DNS upstream cho zone thử nghiệm | `172.16.6.20` | Evil DNS `10.10.0.200` | `172.16.6.20` |
| Source port upstream | Ngẫu nhiên theo BIND | Cố định `33333` | Ngẫu nhiên theo BIND |
| Kali thấy query resolver ↔ AUTH-DNS | Không | Không | Không |

*Đây là kết quả mong đợi; điền kết quả đo thực tế vào báo cáo.*

---

## 4. Cấu trúc thư mục repo

*Cấu trúc dự kiến; chỉ thêm báo cáo, log và ảnh sau khi thực hiện.*

```text
LAB_AT_BMHTTT/
└── LAB10/
    ├── README.md
    ├── CNPM1-Lab10_1150080059-Tran_Nguyen_Minh_Khoi.docx
    ├── outputs/
    │   ├── baseline_dns.txt
    │   ├── kali_remote_check.txt
    │   ├── auth_dns_relay.txt
    │   ├── resolver_cache.txt
    │   ├── victim_after_poison.txt
    │   ├── restore_bind9.txt
    │   └── source_port_randomization.txt
    ├── captures/
    │   ├── remote_poisoning.pcapng
    │   └── bind9_hardening.pcapng
    └── screenshots/
```

> Chỉ đưa lên repo bằng chứng đã rà soát; không đăng mật khẩu, token, private key hoặc `config.xml` chưa làm sạch. Ảnh cần thể hiện interface/ruleset, tính remote, baseline, `[PENDING]`/`[HIT]`/`[CACHE]`, kết quả VICTIM và hardening.

---

## 5. Lỗi gặp phải và cách khắc phục

| Lỗi | Nguyên nhân / cách kiểm tra | Cách khắc phục |
|-----|----------------------------|----------------|
| VICTIM không resolve baseline | Sai DNS, BIND/forward zone hoặc AUTH-DNS chưa hoạt động | Kiểm tra `10.9.0.10`, service và rule DNSLAB → AUTH-DNS UDP/53 |
| Kali thấy traffic DNSLAB | Còn card `dns-lab` hoặc cấu hình capture không đúng mô hình | Disable card DNSLAB, đặt Promiscuous Mode = Deny |
| AUTH-DNS không có `[PENDING]` | Resolver mô phỏng chưa chạy, query sai zone hoặc firewall chặn | Kiểm tra service, domain `example.lab10.test` và rule UDP/53 |
| Relay không có `[HIT]` | UDP/5300 bị chặn, sai IP Kali hoặc hết cửa sổ race | Kiểm tra rule, IP `10.10.0.200` và delay theo tài nguyên |
| Resolver không in `[CACHE]` | Phản hồi giả tới muộn hoặc sai source port/NS/glue | Đối chiếu capture ở DNS-SERVER/APP-TEST, kiểm tra `33333` và chạy lại theo hướng dẫn |
| VICTIM vẫn nhận IP hợp lệ sau poison | Cache client còn hoặc BIND vẫn đang phục vụ thay resolver mô phỏng | Flush cache client, xác nhận tiến trình đang giữ `10.9.0.10:53` |
| Post-poison query bị timeout | Evil DNS chưa chạy hoặc thiếu rule UDP/53 tới Kali | Kiểm tra `10.9.0.10 → 10.10.0.200:53` |
| Fake web không mở | HTTP server chưa chạy hoặc thiếu rule TCP/80 | Kiểm tra dịch vụ và rule DNSLAB → Kali TCP/80 |
| BIND không chạy lại | Cấu hình lỗi hoặc resolver mô phỏng còn giữ cổng 53 | Dừng resolver mô phỏng, chạy `named-checkconf`, xem `journalctl -u bind9 -n 50` |
| Mất WebGUI pfSense | ATTACKNET không phải mạng quản trị | Truy cập MGMT `192.168.56.254`; giữ Anti-Lockout rule |

---

## 6. Cam kết

- Chỉ thực hành trên VM lab, mạng private và namespace `.test` được chỉ định.
- Ảnh, log và packet capture trong báo cáo lấy từ môi trường thực tế của sinh viên.
- Phân biệt rõ BIND9 baseline, resolver mô phỏng có điểm yếu và BIND9 sau khôi phục.
- Không dùng kết quả mô phỏng để khẳng định resolver hiện đại hoặc hệ thống Internet có cùng lỗ hổng.
- Kết thúc lab: dừng công cụ mô phỏng, khôi phục BIND9, bỏ rule UDP/5300 và duy trì cô lập mạng.
- Không đưa mật khẩu, token, private key hoặc cấu hình chứa bí mật lên repo.

---

**Tài liệu hướng dẫn:** `LAB10_HuongDan.docx`  
**Người thực hiện:** Trần Nguyễn Minh Khôi – 1150080059  
**Ngày hoàn thành:** *(điền ngày thực tế)*
