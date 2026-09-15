# Báo cáo Bài Thực Hành

## 1. Thông tin sinh viên

- **Họ và tên sinh viên:** Trần Nguyễn Minh Khôi
- **Mã số sinh viên (MSSV):** 1150080059

## 2. Thông tin bài Lab

- **Tên bài Lab:** Lab 1: Bắt gói tin Telnet - SSH (Examining SSH & Telnet in Wireshark)

## 3. Nội dung đã thực hiện

### 3.1. Thiết lập môi trường mạng lab

Cấu hình mô hình gồm 3 máy ảo (Client, Server, Attacker) kết nối nội bộ qua switch ảo, kiểm tra kết nối thông qua lệnh `ping` [cite: 1].

### 3.2. Cấu hình và thử nghiệm dịch vụ Telnet

- Cài đặt và kích hoạt Telnet Server trên máy chủ, cấu hình tài khoản thử nghiệm và thêm vào nhóm TelnetClients [cite: 1].
- Sử dụng PuTTY/Command Prompt từ máy Client để kết nối Telnet tới Server (cổng 23) và thực hiện các thao tác lệnh cơ bản như xem, tạo thư mục (`dir`, `mkdir`) [cite: 1].
- Thay đổi mật khẩu tài khoản thành mật khẩu phức tạp (>10 ký tự, có chữ, số, ký tự đặc biệt) để kiểm tra hành vi bắt gói tin [cite: 1].

### 3.3. Cấu hình và thử nghiệm dịch vụ SSH

- Cài đặt và cấu hình SSH Server trên máy chủ (sử dụng Cygwin với gói `openssh` hoặc dịch vụ OpenSSH tương ứng) [cite: 1].
- Khởi động dịch vụ `sshd` và thiết lập quy tắc tường lửa cho cổng 22 [cite: 1].
- Sử dụng PuTTY từ máy Client kết nối đến Server qua SSH, xác thực host-key fingerprint ở lần kết nối đầu tiên và đăng nhập bằng tài khoản thử nghiệm để thực hiện các lệnh quản trị (`ls`, `mkdir`) [cite: 1].

### 3.4. Bắt và phân tích gói tin bằng Wireshark

- Sử dụng Wireshark trên máy Attacker (hoặc trực tiếp trên Client/Server) với bộ lọc `tcp.port == 23` để bắt và phân tích phiên truyền dữ liệu Telnet, minh chứng dữ liệu đăng nhập và lệnh hiển thị dưới dạng văn bản rõ (plaintext) [cite: 1].
- Sử dụng bộ lọc `tcp.port == 22` để bắt phiên SSH, quan sát quá trình bắt tay và metadata nhưng xác nhận payload đã được mã hóa hoàn toàn [cite: 1].

## 4. Kết quả thực hiện

### 4.1. Đối với Telnet

Wireshark bắt được toàn bộ các gói tin truyền tải qua cổng 23. Khi sử dụng tính năng *Follow TCP Stream*, thông tin tài khoản đăng nhập (username, password) và các lệnh thực thi (`dir`, `mkdir`) hiển thị rõ ràng dưới dạng plaintext, chứng minh giao thức Telnet không có cơ chế mã hóa kênh truyền [cite: 1].

Việc đổi mật khẩu dài hay phức tạp không làm thay đổi việc lộ thông tin này [cite: 1].

### 4.2. Đối với SSH

Wireshark ghi nhận quá trình thiết lập kết nối TCP, quá trình bắt tay (handshake), địa chỉ IP, kích thước và thời điểm gói tin qua cổng 22. Tuy nhiên, toàn bộ nội dung trao đổi (gói tin payload chứa thông tin xác thực và câu lệnh) đã được mã hóa an toàn, không thể đọc trực tiếp bằng mắt thường [cite: 1].

## 5. Các lưu ý cần thiết để giảng viên kiểm tra hoặc chạy lại bài

- **Môi trường bắt gói tin:** Trên các switch ảo hiện đại, máy Attacker thứ ba có thể không nhìn thấy lưu lượng unicast giữa Client và Server. Do đó, ưu tiên chạy Wireshark trực tiếp trên máy Client hoặc Server, hoặc cấu hình port mirroring/TAP nếu bắt qua máy thứ ba [cite: 1].
- **Bộ lọc Wireshark:** Sử dụng filter `tcp.port == 23` để quan sát lưu lượng Telnet và `tcp.port == 22` để quan sát lưu lượng SSH [cite: 1].
- **Quyền truy cập dịch vụ:** Đảm bảo tài khoản thử nghiệm trên server đã được cấp quyền truy cập dịch vụ tương ứng (ví dụ: nằm trong nhóm TelnetClients đối với Telnet trên Windows Server cũ hoặc cấu hình user hợp lệ) và tường lửa cho phép các cổng TCP 23 và TCP 22 [cite: 1].
