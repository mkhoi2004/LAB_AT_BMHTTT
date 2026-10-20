LAB 7 - pfSense Web Protection - STUDENT RESOURCES

Muc dich: bo tai nguyen phuc vu bai LAB 7 da chuyen tu Sophos UTM sang pfSense.
LAB 7 ke thua LAB 6 (pfSense-LAB6, DC01, Client-A, Client-B, DMZ-WEB) va bo sung WEB-PROXY 10.6.0.30.

Thanh phan moi can bo sung:
1) WEB-PROXY: Ubuntu Server 24.04 LTS, 2 GB RAM, 2 vCPU, 20 GB disk, LAN 10.6.0.30/24, GW 10.6.0.1, DNS 10.6.0.10.
2) DMZ-WEB: dung script 01_DMZ_WEB/setup_lab7_dmz_web.sh de tao cac website mo phong va file kiem thu.
3) DC01: dung 02_DNS/Add-LAB7-DNS.ps1 de tao ban ghi lab7.test neu giang vien cho phep dung script.
4) WEB-PROXY: cai Squid + E2Guardian + ClamAV. Cac file trong 03_WEB_PROXY la danh sach mau va huong dan patch cau hinh.

Luu y quan trong:
- KHONG cai Squid/SquidGuard truc tiep tren pfSense. Netgate da danh dau cac package nay la deprecated do cac lo hong upstream chua sua.
- Squid/E2Guardian trong LAB nay chay tren VM Ubuntu rieng. pfSense van la gateway/firewall va ep client di qua proxy.
- Cac domain .lab7.test la website mo phong noi bo, giup ket qua lab lap lai duoc va khong phu thuoc website that.
- HTTPS inspection la phan mo rong. Chi bat trong mang lab co kiem soat va phai import CA lab vao client.
