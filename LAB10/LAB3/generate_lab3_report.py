# -*- coding: utf-8 -*-
"""
Script tạo file Lab3_KetQua.docx và xuất ra Lab3_KetQua.pdf
Báo cáo kết quả thực hành Lab 3: Mật mã khóa công khai (RSA, ECC, ECIES) và Hệ mã lai RSA + AES-GCM
Sinh viên: Trần Nguyễn Minh Khôi - MSSV: 1150080059 - Lớp: 11_ĐH_CNPM1
"""

import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn
import win32com.client

BASE_DIR = r"C:\Users\ADMIN\Documents\GitHub\LAB_AT_BMHTTT\LAB10\LAB3"
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "screenshots")
OUT_DOCX = os.path.join(BASE_DIR, "Lab3_KetQua.docx")
OUT_PDF = os.path.join(BASE_DIR, "Lab3_KetQua.pdf")

# Palette màu chuyên nghiệp
COLOR_PRIMARY = RGBColor(27, 54, 93)     # #1B365D Dark Navy Blue
COLOR_SECONDARY = RGBColor(43, 87, 154)  # #2B579A Steel Blue
COLOR_TEXT = RGBColor(33, 37, 41)        # #212529 Dark Charcoal
COLOR_GRAY = RGBColor(108, 117, 125)     # #6C757D Muted Gray
COLOR_SUCCESS = RGBColor(40, 167, 69)    # #28A745 Green
COLOR_DANGER = RGBColor(220, 53, 69)     # #DC3545 Red

def set_cell_shading(cell, color_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=120, bottom=120, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def set_table_borders(table, color="D3D3D3", sz="4", val="single"):
    tblPr = table._tbl.tblPr
    borders = parse_xml(
        f'<w:tblBorders {nsdecls("w")}>'
        f'<w:top w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:bottom w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:insideH w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:insideV w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:left w:val="none"/>'
        f'<w:right w:val="none"/>'
        f'</w:tblBorders>'
    )
    tblPr.append(borders)

def add_header_info_table(doc):
    table = doc.add_table(rows=7, cols=2)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    data = [
        ("Họ và tên", "Trần Nguyễn Minh Khôi"),
        ("MSSV", "1150080059"),
        ("Lớp", "11_ĐH_CNPM1"),
        ("Nhóm", "Không có"),
        ("Ngày thực hành", "17/11/2026"),
        ("Link YouTube", "https://www.youtube.com/@Tr%E1%BA%A7nNguy%E1%BB%85nMinhKh%C3%B4i-22"),
        ("Link GitHub", "https://github.com/mkhoi2004/LAB_AT_BMHTTT/tree/main/LAB10/LAB3")
    ]

    col_widths = [Inches(1.8), Inches(4.7)]

    for row_idx, (k, v) in enumerate(data):
        row = table.rows[row_idx]
        cell_k, cell_v = row.cells[0], row.cells[1]

        cell_k.width = col_widths[0]
        cell_v.width = col_widths[1]

        set_cell_margins(cell_k, top=100, bottom=100, left=140, right=140)
        set_cell_margins(cell_v, top=100, bottom=100, left=140, right=140)

        set_cell_shading(cell_k, "F0F4F8")
        set_cell_shading(cell_v, "FFFFFF" if row_idx % 2 == 0 else "F9FBFC")

        p_k = cell_k.paragraphs[0]
        p_k.paragraph_format.space_before = Pt(2)
        p_k.paragraph_format.space_after = Pt(2)
        r_k = p_k.add_run(k)
        r_k.bold = True
        r_k.font.name = "Segoe UI"
        r_k.font.size = Pt(10.5)
        r_k.font.color.rgb = COLOR_PRIMARY

        p_v = cell_v.paragraphs[0]
        p_v.paragraph_format.space_before = Pt(2)
        p_v.paragraph_format.space_after = Pt(2)
        r_v = p_v.add_run(v)
        r_v.font.name = "Segoe UI"
        r_v.font.size = Pt(10.5)
        if "http" in v:
            r_v.font.color.rgb = COLOR_SECONDARY
            r_v.underline = True
        else:
            r_v.font.color.rgb = COLOR_TEXT

    set_table_borders(table, color="B0C4DE", sz="6")

def add_heading_1(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(18)
    p.paragraph_format.space_after = Pt(6)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Segoe UI"
    r.font.size = Pt(15)
    r.bold = True
    r.font.color.rgb = COLOR_PRIMARY
    return p

def add_heading_2(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(13)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Segoe UI"
    r.font.size = Pt(12.5)
    r.bold = True
    r.font.color.rgb = COLOR_SECONDARY
    return p

def add_heading_3(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(9)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Segoe UI"
    r.font.size = Pt(11)
    r.bold = True
    r.font.color.rgb = COLOR_TEXT
    return p

def add_body_paragraph(doc, text, bold_prefix=None, italic=False):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = "Segoe UI"
        r_pre.font.size = Pt(10.5)
        r_pre.bold = True
        r_pre.font.color.rgb = COLOR_PRIMARY
    r = p.add_run(text)
    r.font.name = "Segoe UI"
    r.font.size = Pt(10.5)
    r.italic = italic
    r.font.color.rgb = COLOR_TEXT
    return p

def add_callout(doc, text, title="LƯU Ý / GHI CHÚ QUAN TRỌNG"):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    table.columns[0].width = Inches(6.5)

    cell = table.rows[0].cells[0]
    set_cell_shading(cell, "F0F4F8")
    set_cell_margins(cell, top=120, bottom=120, left=180, right=180)

    # Left border only (accent line)
    tcPr = cell._tc.get_or_add_tcPr()
    borders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        f'<w:left w:val="single" w:sz="24" w:space="0" w:color="1B365D"/>'
        f'<w:top w:val="none"/>'
        f'<w:bottom w:val="none"/>'
        f'<w:right w:val="none"/>'
        f'</w:tcBorders>'
    )
    tcPr.append(borders)

    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(3)
    r_t = p.add_run(title + "\n")
    r_t.bold = True
    r_t.font.name = "Segoe UI"
    r_t.font.size = Pt(10.5)
    r_t.font.color.rgb = COLOR_PRIMARY

    r_c = p.add_run(text)
    r_c.font.name = "Segoe UI"
    r_c.font.size = Pt(10)
    r_c.font.color.rgb = COLOR_TEXT

    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

def add_code_block(doc, code_text, caption=None):
    if caption:
        p_cap = doc.add_paragraph()
        p_cap.paragraph_format.space_before = Pt(6)
        p_cap.paragraph_format.space_after = Pt(2)
        r_c = p_cap.add_run(caption)
        r_c.bold = True
        r_c.font.name = "Segoe UI"
        r_c.font.size = Pt(10)
        r_c.font.color.rgb = COLOR_SECONDARY

    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    table.columns[0].width = Inches(6.5)

    cell = table.rows[0].cells[0]
    set_cell_shading(cell, "F7F9FA")
    set_cell_margins(cell, top=100, bottom=100, left=140, right=140)

    tcPr = cell._tc.get_or_add_tcPr()
    borders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        f'<w:top w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        f'<w:bottom w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        f'<w:left w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        f'<w:right w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        f'</w:tcBorders>'
    )
    tcPr.append(borders)

    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.05
    r = p.add_run(code_text.strip())
    r.font.name = "Consolas"
    r.font.size = Pt(9)
    r.font.color.rgb = RGBColor(36, 41, 46)

    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

def add_figure_image(doc, img_name, caption_text, width_inches=5.4):
    img_path = os.path.join(SCREENSHOTS_DIR, img_name)
    if not os.path.exists(img_path):
        print(f"Warning: Image file not found: {img_path}")
        return

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(4)
    run = p.add_run()
    run.add_picture(img_path, width=Inches(width_inches))

    p_cap = doc.add_paragraph()
    p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_cap.paragraph_format.space_before = Pt(2)
    p_cap.paragraph_format.space_after = Pt(10)
    r_cap = p_cap.add_run(caption_text)
    r_cap.font.name = "Segoe UI"
    r_cap.font.size = Pt(9.5)
    r_cap.bold = True
    r_cap.italic = True
    r_cap.font.color.rgb = COLOR_PRIMARY

def add_styled_table(doc, headers, data, col_widths=None):
    table = doc.add_table(rows=len(data) + 1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    if col_widths is None:
        total_w = 6.5
        w_each = total_w / len(headers)
        col_widths = [Inches(w_each)] * len(headers)

    # Header Row
    hdr_row = table.rows[0]
    for i, title in enumerate(headers):
        cell = hdr_row.cells[i]
        cell.width = col_widths[i]
        set_cell_shading(cell, "1B365D")
        set_cell_margins(cell, top=100, bottom=100, left=120, right=120)
        p = cell.paragraphs[0]
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(2)
        r = p.add_run(title)
        r.bold = True
        r.font.name = "Segoe UI"
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    # Data Rows
    for r_idx, row_data in enumerate(data):
        row = table.rows[r_idx + 1]
        bg = "FFFFFF" if r_idx % 2 == 0 else "F8F9FA"
        for c_idx, val in enumerate(row_data):
            cell = row.cells[c_idx]
            cell.width = col_widths[c_idx]
            set_cell_shading(cell, bg)
            set_cell_margins(cell, top=80, bottom=80, left=120, right=120)
            p = cell.paragraphs[0]
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
            r = p.add_run(str(val))
            r.font.name = "Segoe UI"
            r.font.size = Pt(9.5)
            r.font.color.rgb = COLOR_TEXT

    set_table_borders(table, color="D0D7DE", sz="4")
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

print("Helper functions ready.")

def build_report():
    doc = Document()

    # Cấu hình lề trang tiêu chuẩn A4 (1 inch = 2.54 cm)
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    # 1. Tiêu đề báo cáo
    p_inst = doc.add_paragraph()
    p_inst.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_inst.paragraph_format.space_before = Pt(0)
    p_inst.paragraph_format.space_after = Pt(2)
    r_inst = p_inst.add_run("TRƯỜNG ĐẠI HỌC CÔNG THƯƠNG TP. HỒ CHÍ MINH\nKHOA CÔNG NGHỆ THÔNG TIN - BỘ MÔN AN TOÀN THÔNG TIN")
    r_inst.font.name = "Segoe UI"
    r_inst.font.size = Pt(11)
    r_inst.bold = True
    r_inst.font.color.rgb = COLOR_SECONDARY

    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(12)
    p_title.paragraph_format.space_after = Pt(4)
    r_title = p_title.add_run("BÁO CÁO THỰC HÀNH - BÀI LAB 3")
    r_title.font.name = "Segoe UI"
    r_title.font.size = Pt(20)
    r_title.bold = True
    r_title.font.color.rgb = COLOR_PRIMARY

    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(16)
    r_sub = p_sub.add_run("MẬT MÃ KHÓA CÔNG KHAI (RSA, ECC, ECIES) VÀ HỆ MÃ LAI RSA + AES-GCM")
    r_sub.font.name = "Segoe UI"
    r_sub.font.size = Pt(12)
    r_sub.bold = True
    r_sub.font.color.rgb = COLOR_SECONDARY

    # 2. Bảng thông tin sinh viên
    add_header_info_table(doc)

    p_sep = doc.add_paragraph()
    p_sep.paragraph_format.space_before = Pt(6)
    p_sep.paragraph_format.space_after = Pt(12)

    # =========================================================================
    # PHẦN 0. TỔNG QUAN
    # =========================================================================
    add_heading_1(doc, "PHẦN 0. TỔNG QUAN")
    add_body_paragraph(doc, "Bài thực hành này giúp sinh viên hiểu bản chất và trực tiếp cài đặt các hệ mật mã khóa công khai hiện đại, bao gồm:", bold_prefix="Mục tiêu môn học: ")
    add_body_paragraph(doc, "1. Hiểu và lập trình mật mã RSA (sử dụng cơ chế đệm an toàn OAEP với SHA-256). Nhận biết giới hạn độ dài bản rõ nghiêm ngặt của RSA (tối đa 190 byte với khóa 2048-bit; 318 byte với 3072-bit).")
    add_body_paragraph(doc, "2. Hiểu và ứng dụng mật mã đường cong elliptic (ECC) theo sơ đồ chuẩn ECIES: sử dụng trao đổi khóa ECDH trên đường cong chuẩn NIST secp256r1, kết hợp hàm dẫn xuất khóa HKDF-SHA256 (RFC 5869) và thuật toán mã hóa đối xứng xác thực AES-256-GCM.")
    add_body_paragraph(doc, "3. Thiết kế và hiện thực hệ mã lai (Hybrid Encryption) kết hợp RSA và AES-GCM để mã hóa hiệu năng cao cho dữ liệu lớn không giới hạn kích thước, tương tự mô hình thực tế trong các giao thức TLS, PGP, S/MIME.")
    add_body_paragraph(doc, "4. Hiện thực chữ ký số ECDSA (SHA256withECDSA trên secp256r1) đảm bảo 3 tính chất an toàn then chốt: Tính toàn vẹn (Integrity), Tính xác thực (Authenticity) và Tính chống chối bỏ (Non-repudiation).")
    add_body_paragraph(doc, "5. Tích hợp cơ sở dữ liệu Microsoft SQL Server lưu trữ nhật ký thao tác (Audit History) tuân thủ bảo mật: chỉ ghi độ dài dữ liệu và trạng thái thành công/thất bại, tuyệt đối không lưu khóa bí mật.")

    add_callout(doc, "Mọi thuật toán trong bài sử dụng thư viện chuẩn của Java (java.security, javax.crypto) và driver Microsoft JDBC (mssql-jdbc). Mật khẩu SQL Server cho tài khoản bài lab là LabUser@2026. Cơ sở dữ liệu mặc định là LAB3_MaHoaKhoaCongKhai.", title="LƯU Ý CẤU HÌNH HỆ THỐNG")

    add_figure_image(doc, "hinh_3_1_menu.png", "Hình 3.1. Cửa sổ chính frm_Menu khi hoàn thành toàn bộ ứng dụng Lab 3", width_inches=5.2)

    add_heading_2(doc, "Bảng 3.1. Dữ liệu kiểm thử bắt buộc (Test Cases)")
    tbl31_headers = ["Bài", "Dữ liệu thử nghiệm", "Kết quả quan sát chuẩn"]
    tbl31_data = [
        ["3.1 RSA Console", "Bản rõ \"HELLO RSA\", Khóa 2048 & 3072 bit", "Decrypt(Encrypt(x)) = x; Đo thời gian tạo khóa (ms); Kiểm tra giới hạn 190 byte"],
        ["3.2 RSA Swing", "Plaintext \"HELLO RSA\", Khóa 2048 bit", "Mỗi lần Encrypt sinh bản mã Base64 khác nhau (do đệm ngẫu nhiên OAEP); Decrypt đúng"],
        ["3.3 ECC", "Plaintext \"HELLO ECC\", secp256r1", "Mã hóa và giải mã thành công; Nếu dùng khóa riêng khác giải mã -> Báo lỗi AEADBadTagException"],
        ["3.4 RSA + AES", "Văn bản dài > 1000 ký tự", "Mã hóa và giải mã văn bản dài thành công; Sửa 1 ký tự bản mã -> AES-GCM phát hiện và từ chối"],
        ["3.5 ECDSA", "Message \"THONG DIEP A\"", "Sign bằng khóa riêng -> Verify bằng khóa công khai báo VALID; Sửa thông điệp báo INVALID"],
        ["SQL Server", "Bảng dbo.History", "Lưu vết tự động các thao tác ENCRYPT, DECRYPT, SIGN, VERIFY, kiểm tra qua form frm_LichSu"]
    ]
    add_styled_table(doc, tbl31_headers, tbl31_data, [Inches(1.4), Inches(2.3), Inches(2.8)])

    # =========================================================================
    # PHẦN 1. CHUẨN BỊ PROJECT, LỚP DÙNG CHUNG VÀ SQL SERVER
    # =========================================================================
    add_heading_1(doc, "PHẦN 1. CHUẨN BỊ PROJECT, LỚP DÙNG CHUNG VÀ SQL SERVER")
    add_heading_2(doc, "1.1. Cấu hình Maven pom.xml")
    add_body_paragraph(doc, "Project được khởi tạo theo chuẩn Maven với GroupId vn.edu.lab, ArtifactId LAB3_MaHoaKhoaCongKhai, Java Release 17/21 và tích hợp thư viện Microsoft JDBC Driver 12.8.1.jre11 kết nối SQL Server.")

    pom_content = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>vn.edu.lab</groupId>
    <artifactId>LAB3_MaHoaKhoaCongKhai</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
        <exec.mainClass>week_03.frm_Menu</exec.mainClass>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <version>12.8.1.jre11</version>
        </dependency>
    </dependencies>
</project>"""
    add_code_block(doc, pom_content, caption="Cấu hình tệp pom.xml:")

    add_heading_2(doc, "1.2. Màn hình thiết kế giao diện NetBeans GUI Builder")
    add_body_paragraph(doc, "Giao diện bài thực hành được xây dựng thông qua NetBeans GUI Builder bằng kéo-thả thành phần Swing (JFrame, JPanel, JLabel, JTextField, JTextArea, JButton, JTable), thiết lập thuộc tính trực quan và gắn sự kiện ActionListener.")
    add_figure_image(doc, "hinh_3_2_netbeans.png", "Hình 3.2. Màn hình NetBeans khi thiết kế form frm_RSA (các vùng làm việc)", width_inches=5.2)

    add_heading_2(doc, "1.3. Lớp tiện ích CryptoSupport và FileTextIO")
    add_body_paragraph(doc, "Lớp CryptoSupport đảm nhiệm các thao tác mã hóa dùng chung: sinh mảng byte ngẫu nhiên an toàn SecureRandom, chuyển đổi Base64 và UTF-8, mã hóa/giải mã AES-256-GCM với thẻ xác thực Polyval 16 byte, dẫn xuất khóa HKDF-SHA256 theo chuẩn RFC 5869, cùng định dạng lưu trữ tệp khóa PUBLIC/PRIVATE.")
    add_body_paragraph(doc, "Lớp FileTextIO cung cấp các hộp thoại mở/lưu tệp chuẩn UTF-8 (JFileChooser) phục vụ việc nạp và lưu trữ khóa mã hóa ra tệp .txt trên đĩa.")

    add_heading_2(doc, "1.4. Thiết lập Cơ sở dữ liệu và Phân quyền SQL Server")
    add_body_paragraph(doc, "Hệ thống sử dụng cơ sở dữ liệu LAB3_MaHoaKhoaCongKhai với bảng dbo.History ghi vết lịch sử. Tài khoản người dùng lab3_user được áp dụng nguyên tắc đặc quyền tối thiểu (Least Privilege): chỉ có quyền INSERT và SELECT trên bảng dbo.History.")

    sql_ddl = """USE master;
GO
IF DB_ID(N'LAB3_MaHoaKhoaCongKhai') IS NULL
    CREATE DATABASE LAB3_MaHoaKhoaCongKhai;
GO
USE LAB3_MaHoaKhoaCongKhai;
GO
IF OBJECT_ID(N'dbo.History', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.History (
        Id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        Algorithm NVARCHAR(30) NOT NULL,
        Operation NVARCHAR(12) NOT NULL,
        InputLength INT NOT NULL,
        Success BIT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Lab3_History_CreatedAt DEFAULT SYSDATETIME(),
        CONSTRAINT CK_Lab3_InputLength CHECK (InputLength >= 0),
        CONSTRAINT CK_Lab3_Operation CHECK (Operation IN (N'ENCRYPT', N'DECRYPT', N'SIGN', N'VERIFY'))
    );
END;
GO"""
    add_code_block(doc, sql_ddl, caption="Kịch bản 01_Tao_CSDL.sql:")

    add_heading_2(doc, "1.5. Các form tương tác SQL Server: frm_KetNoiSQL và frm_LichSu")
    add_body_paragraph(doc, "Form frm_KetNoiSQL cho phép cấu hình tham số kết nối động (Server, Port, Database, User, Password) và kiểm tra kết nối JDBC đến SQL Server.")
    add_figure_image(doc, "hinh_3_3_ketnoisql.png", "Hình 3.3. Form frm_KetNoiSQL sau khi kiểm tra kết nối thành công đến SQL Server", width_inches=4.8)

    add_body_paragraph(doc, "Form frm_LichSu truy vấn và hiển thị 50 bản ghi thao tác gần nhất từ bảng dbo.History thông qua JTable và HistoryDao:")
    add_figure_image(doc, "hinh_3_4_lichsu.png", "Hình 3.4. Form frm_LichSu hiển thị dữ liệu lịch sử thao tác thực tế từ bảng dbo.History", width_inches=5.8)

    # =========================================================================
    # PHẦN 2. CÁC BÀI THỰC HÀNH MẬT MÃ KHÓA CÔNG KHAI
    # =========================================================================
    add_heading_1(doc, "PHẦN 2. CÁC BÀI THỰC HÀNH MẬT MÃ KHÓA CÔNG KHAI")

    # --- BÀI 3.1 ---
    add_heading_2(doc, "BÀI 3.1. MẬT MÃ RSA – CHƯƠNG TRÌNH CONSOLE (RSADemo)")
    add_body_paragraph(doc, "Thuật toán RSA dựa trên độ khó của bài toán phân tích số nguyên lớn n = p*q ra thừa số nguyên tố. Trong thực tế, các kích thước khóa chuẩn là 2048-bit và 3072-bit.")
    add_body_paragraph(doc, "Để ngăn chặn các tấn công chọn bản rõ và đảm bảo tính ngẫu nhiên của bản mã, cơ chế đệm OAEP (Optimal Asymmetric Encryption Padding) kết hợp hàm băm SHA-256 được sử dụng. Giới hạn độ dài bản rõ tối đa được tính theo công thức: MaxBytes = (bits / 8) - 2 * HASH_LEN - 2 = (bits / 8) - 66 byte.")

    add_body_paragraph(doc, "Kết quả thực thi chương trình RSADemo trên màn hình Console với kích thước khóa 2048-bit:")
    add_figure_image(doc, "rsademo_2048.png", "Kết quả thực thi RSADemo với khóa RSA 2048-bit (thời gian tạo khóa 152 ms, bản rõ 190 byte)", width_inches=5.6)

    add_body_paragraph(doc, "Kết quả thực thi chương trình RSADemo trên màn hình Console với kích thước khóa 3072-bit:")
    add_figure_image(doc, "rsademo_3072.png", "Kết quả thực thi RSADemo với khóa RSA 3072-bit (thời gian tạo khóa 486 ms, bản rõ 318 byte)", width_inches=5.6)

    add_heading_3(doc, "Bảng 3.7. Bảng ghi kết quả thực nghiệm Bài 3.1")
    tbl37_headers = ["Kích thước khóa", "Thời gian sinh khóa (ms)", "Độ dài bản mã Base64", "Giới hạn bản rõ tối đa"]
    tbl37_data = [
        ["2048 bit", "145 – 152 ms", "344 ký tự", "190 byte"],
        ["3072 bit", "486 – 520 ms", "512 ký tự", "318 byte"]
    ]
    add_styled_table(doc, tbl37_headers, tbl37_data, [Inches(1.5), Inches(1.8), Inches(1.8), Inches(1.4)])

    # --- BÀI 3.2 ---
    add_heading_2(doc, "BÀI 3.2. MẬT MÃ RSA VỚI GIAO DIỆN SWING (frm_RSA)")
    add_body_paragraph(doc, "Form frm_RSA cài đặt hoàn chỉnh giao diện tương tác đồ họa: chọn kích thước khóa (2048/3072 bit), sinh cặp khóa (Generate Keys), mã hóa (Encrypt) bằng khóa công khai và giải mã (Decrypt) bằng khóa riêng.")

    add_figure_image(doc, "hinh_3_5_rsa.png", "Hình 3.5. Form frm_RSA ban đầu khi vừa khởi động", width_inches=5.2)
    add_figure_image(doc, "hinh_3_6_rsa_encrypt.png", "Hình 3.6. Form frm_RSA sau khi sinh khóa 2048-bit, mã hóa và giải mã thành công chuỗi \"HELLO RSA\"", width_inches=5.2)

    # --- BÀI 3.3 ---
    add_heading_2(doc, "BÀI 3.3. MẬT MÃ ĐƯỜNG CONG ELLIPTIC (ECC - secp256r1)")
    add_body_paragraph(doc, "Mật mã ECC dựa trên bài toán logarit rời rạc trên nhóm điểm của đường cong elliptic. Với cùng mức an toàn 128-bit, khóa ECC secp256r1 (P-256) chỉ cần 256-bit, tương đương khóa RSA 3072-bit nhưng kích thước nhỏ gọn hơn rất nhiều.")
    add_body_paragraph(doc, "Bài thực hành hiện thực sơ đồ tích hợp ECIES: (1) Sinh cặp khóa tạm thời (ephemeral key pair); (2) Thỏa thuận bí mật chung ECDH; (3) Dẫn xuất khóa phiên AES 256-bit qua HKDF-SHA256; (4) Mã hóa dữ liệu bằng AES-256-GCM. Bản mã đóng gói bao gồm: Khóa công khai tạm (91 byte) || Salt (16 byte) || Nonce (12 byte) || Dữ liệu AES-GCM + Tag xác thực.")

    add_figure_image(doc, "hinh_3_7_ecc.png", "Hình 3.7. Form frm_ECC ban đầu sau khi thiết kế", width_inches=5.2)
    add_figure_image(doc, "hinh_3_8_ecc_encrypt.png", "Hình 3.8. Form frm_ECC: mã hóa và giải mã chuẩn xác thông điệp \"HELLO ECC\"", width_inches=5.2)
    add_figure_image(doc, "hinh_3_9_ecc_error.png", "Hình 3.9. Form frm_ECC: giải mã bằng khóa riêng khác (khóa sai) bị từ chối với ngoại lệ AEADBadTagException", width_inches=5.2)

    # --- BÀI 3.4 ---
    add_heading_2(doc, "BÀI 3.4. KẾT HỢP RSA VỚI AES (HỆ MÃ LAI - HYBRID ENCRYPTION)")
    add_body_paragraph(doc, "Do RSA bị giới hạn nghiêm ngặt về kích thước bản rõ (tối đa 190 byte với khóa 2048-bit), trong thực tế người ta sử dụng mô hình Hệ mã lai (Hybrid Encryption):")
    add_body_paragraph(doc, "• Dữ liệu văn bản dài được mã hóa cực nhanh bằng AES-256-GCM (khóa phiên ngẫu nhiên 32 byte + nonce 12 byte).")
    add_body_paragraph(doc, "• Khóa phiên AES được bọc (wrapped) bằng thuật toán bất đối xứng RSA-OAEP (256 byte).")
    add_body_paragraph(doc, "• Bản mã hoàn chỉnh bao gồm: Khóa AES đã bọc (256 byte) || Nonce (12 byte) || Dữ liệu AES-GCM có kèm Tag toàn vẹn 16 byte.")

    add_figure_image(doc, "hinh_3_10_rsaaes.png", "Hình 3.10. Form frm_RSA_AES ban đầu sau khi thiết kế", width_inches=5.2)
    add_figure_image(doc, "hinh_3_11_rsaaes_encrypt.png", "Hình 3.11. Form frm_RSA_AES: mã hóa và giải mã hoàn chỉnh văn bản dài (>1000 ký tự)", width_inches=5.2)
    add_figure_image(doc, "hinh_3_12_rsaaes_tamper.png", "Hình 3.12. Form frm_RSA_AES: sau khi sửa 1 ký tự bản mã (nút Tamper), giải mã lập tức bị AES-GCM từ chối", width_inches=5.2)

    # --- BÀI 3.5 ---
    add_heading_2(doc, "BÀI 3.5. BÀI TẬP NÂNG CAO – CHỮ KÝ SỐ ECDSA")
    add_body_paragraph(doc, "Chữ ký số ECDSA (sử dụng thuật toán SHA256withECDSA trên đường cong secp256r1) thực hiện ký thông điệp bằng khóa riêng và xác minh tính hợp lệ bằng khóa công khai.")
    add_body_paragraph(doc, "• Khi thông điệp và khóa nguyên vẹn: Trạng thái hiển thị \"VALID\" với màu xanh lá.")
    add_body_paragraph(doc, "• Khi thông điệp bị thay đổi dù chỉ 1 ký tự (ví dụ từ THONG DIEP A sang THONG DIEP B) hoặc sử dụng khóa công khai sai: Trạng thái hiển thị \"INVALID\" với màu đỏ.")

    add_figure_image(doc, "hinh_3_13_ecdsa.png", "Hình 3.13. Form frm_ECC_Signature ban đầu sau khi thiết kế", width_inches=5.2)
    add_figure_image(doc, "hinh_3_14_ecdsa_valid.png", "Hình 3.14. Form frm_ECC_Signature: chữ ký hợp lệ hiển thị chữ VALID (màu xanh lá)", width_inches=5.2)
    add_figure_image(doc, "hinh_3_15_ecdsa_invalid.png", "Hình 3.15. Form frm_ECC_Signature: sửa thông điệp thành THONG DIEP B hiển thị chữ INVALID (màu đỏ)", width_inches=5.2)

    # =========================================================================
    # PHẦN 3. MENU CHÍNH VÀ KIỂM TRA LỊCH SỬ SQL SERVER
    # =========================================================================
    add_heading_1(doc, "PHẦN 3. MENU CHÍNH VÀ KIỂM TRA LỊCH SỬ SQL SERVER")
    add_body_paragraph(doc, "Cửa sổ chính frm_Menu đóng vai trò điều hướng trung tâm, cho phép mở nhanh các module: 3.2 RSA (Swing), 3.3 ECC, 3.4 RSA + AES, 3.5 Chữ ký ECDSA, Kết nối SQL Server và Xem lịch sử.")

    add_figure_image(doc, "hinh_3_16_menu.png", "Hình 3.16. Form frm_Menu hoàn chỉnh kết nối đầy đủ các chức năng của hệ thống", width_inches=5.0)

    add_body_paragraph(doc, "Kết quả truy vấn kiểm tra trực tiếp từ SQL Server Management Studio (SSMS) bằng tệp 03_KiemTra_LichSu.sql chứng minh toàn bộ nhật ký thao tác đã được lưu vết thành công:")
    add_figure_image(doc, "ssms_03_kiemtra_lichsu.png", "Kết quả thực thi kịch bản 03_KiemTra_LichSu.sql trong SQL Server Management Studio", width_inches=5.6)

    # =========================================================================
    # PHẦN 4. BẢNG TỔNG KẾT VÀ BẢNG LỖI THƯỜNG GẶP
    # =========================================================================
    add_heading_1(doc, "PHẦN 4. BẢNG LỖI THƯỜNG GẶP VÀ CÁCH KHẮC PHỤC")
    tbl315_headers = ["Thông báo lỗi / Hiện tượng", "Nguyên nhân kỹ thuật", "Cách xử lý khắc phục"]
    tbl315_data = [
        ["cannot find symbol variable txt_...", "Đặt sai tên biến điều khiển trên giao diện", "Nhấp phải điều khiển -> Change Variable Name... đặt lại đúng tên"],
        ["No suitable driver found for jdbc:sqlserver", "Thiếu thư viện mssql-jdbc trong pom.xml", "Thêm dependency mssql-jdbc vào pom.xml và Clean and Build lại"],
        ["Login failed for user 'lab3_user'", "Sai mật khẩu hoặc SQL Server chưa bật Mixed Mode", "Kiểm tra mật khẩu LabUser@2026; Bật SQL Server and Windows Authentication mode"],
        ["Bản rõ ... vượt giới hạn 190 byte", "RSA-2048 chỉ mã hóa được dữ liệu ngắn", "Chuyển sang sử dụng Hệ mã lai RSA + AES (Bài 3.4) cho văn bản dài"],
        ["AEADBadTagException: Tag mismatch", "Sai khóa riêng giải mã hoặc bản mã bị sửa đổi", "Nạp đúng tệp khóa đã lưu khi mã hóa; Kiểm tra bản mã không bị sai lệch"],
        ["BadPaddingException / Decryption error", "Bản mã RSA mã hóa bằng khóa công khai khác", "Sử dụng đúng cặp khóa tương ứng đã tạo ban đầu"]
    ]
    add_styled_table(doc, tbl315_headers, tbl315_data, [Inches(2.1), Inches(2.2), Inches(2.2)])

    # =========================================================================
    # PHẦN 5. KẾT LUẬN VÀ DANH SÁCH TỰ KIỂM TRA TRƯỚC KHI NỘP BÀI
    # =========================================================================
    add_heading_1(doc, "PHẦN 5. KẾT LUẬN VÀ DANH SÁCH TỰ KIỂM TRA")
    add_body_paragraph(doc, "Bài thực hành Lab 3 đã hoàn thành xuất sắc và đầy đủ 100% các yêu cầu:", bold_prefix="Kết luận đánh giá: ")
    add_body_paragraph(doc, "✓ Đã làm chủ cơ chế mã hóa bất đối xứng RSA với đệm OAEP-SHA256 trên cả giao diện Console và Swing.")
    add_body_paragraph(doc, "✓ Đã hiện thực thành công sơ đồ ECIES trên đường cong secp256r1 kết hợp HKDF và AES-256-GCM.")
    add_body_paragraph(doc, "✓ Đã triển khai hoàn chỉnh Hệ mã lai RSA + AES-GCM cho văn bản dài với cơ chế phát hiện sửa đổi bản mã.")
    add_body_paragraph(doc, "✓ Đã xây dựng chữ ký số ECDSA xác thực tính toàn vẹn (VALID / INVALID).")
    add_body_paragraph(doc, "✓ Đã kết nối cơ sở dữ liệu Microsoft SQL Server lưu vết nhật ký thao tác tự động, an toàn và bảo mật.")

    tbl_check_headers = ["STT", "Hạng mục minh chứng kiểm tra", "Trạng thái hoàn thành"]
    tbl_check_data = [
        ["1", "Ảnh Output RSADemo 2048 và 3072 bit + Bảng 3.7", "ĐÃ HOÀN THÀNH (Đầy đủ minh chứng)"],
        ["2", "Ảnh frm_RSA mã hóa/giải mã HELLO RSA", "ĐÃ HOÀN THÀNH (Hình 3.5 & 3.6)"],
        ["3", "Ảnh frm_ECC đúng và lỗi khi dùng khóa sai", "ĐÃ HOÀN THÀNH (Hình 3.7, 3.8 & 3.9)"],
        ["4", "Ảnh frm_RSA_AES văn bản dài > 1000 ký tự + phát hiện sửa đổi", "ĐÃ HOÀN THÀNH (Hình 3.10, 3.11 & 3.12)"],
        ["5", "Ảnh frm_ECC_Signature: VALID và INVALID", "ĐÃ HOÀN THÀNH (Hình 3.13, 3.14 & 3.15)"],
        ["6", "Ảnh frm_LichSu + ảnh SSMS truy vấn 03_KiemTra_LichSu.sql", "ĐÃ HOÀN THÀNH (Hình 3.4 & SSMS)"],
        ["7", "Không nộp khóa riêng thật, tuân thủ an toàn thông tin", "ĐÃ TUÂN THỦ 100%"]
    ]
    add_styled_table(doc, tbl_check_headers, tbl_check_data, [Inches(0.6), Inches(4.3), Inches(1.6)])

    # Lưu tệp DOCX
    doc.save(OUT_DOCX)
    print("Successfully built DOCX:", OUT_DOCX, f"({os.path.getsize(OUT_DOCX)} bytes)")

def convert_docx_to_pdf():
    print("Converting DOCX to PDF via Word COM...")
    word = win32com.client.DispatchEx("Word.Application")
    word.Visible = False
    word.DisplayAlerts = 0
    try:
        doc = word.Documents.Open(OUT_DOCX)
        doc.SaveAs(OUT_PDF, FileFormat=17) # 17 = wdFormatPDF
        doc.Close()
        print("Successfully exported PDF:", OUT_PDF, f"({os.path.getsize(OUT_PDF)} bytes)")
    except Exception as e:
        print("Error during PDF conversion:", e)
    finally:
        word.Quit()

if __name__ == "__main__":
    build_report()
    convert_docx_to_pdf()
