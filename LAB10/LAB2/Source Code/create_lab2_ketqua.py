import os
import docx
from docx.shared import Inches
import win32com.client

BASE_DIR = r"C:\Users\ADMIN\Documents\GitHub\LAB_AT_BMHTTT\LAB10\LAB2"
SRC_DOCX = os.path.join(BASE_DIR, "LAB2_HuongDan.docx")
OUT_DOCX = os.path.join(BASE_DIR, "Lab2_KetQua.docx")
OUT_PDF = os.path.join(BASE_DIR, "Lab2_KetQua.pdf")
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "screenshots")

IMG_MAP = {
    "Hình 2.1.": ("hinh_2_1_menu.png", 5.4),
    "Hình 2.3.": ("hinh_2_3_ketnoisql.png", 4.8),
    "Hình 2.4.": ("hinh_2_4_lichsu.png", 5.8),
    "Hình 2.5.": ("hinh_2_5_des.png", 5.4),
    "Hình 2.6.": ("hinh_2_6_des_encrypt.png", 5.4),
    "Hình 2.7.": ("hinh_2_7_des_error.png", 5.4),
    "Hình 2.8.": ("hinh_2_8_tripledes.png", 5.4),
    "Hình 2.9.": ("hinh_2_9_tripledes_encrypt.png", 5.4),
    "Hình 2.10.": ("hinh_2_10_aes.png", 5.8),
    "Hình 2.11.": ("hinh_2_11_aes_encrypt.png", 5.8),
    "Hình 2.12.": ("hinh_2_12_rc4.png", 5.4),
    "Hình 2.13.": ("hinh_2_13_rc4_encrypt.png", 5.4),
    "Hình 2.14.": ("hinh_2_14_twofish.png", 5.4),
    "Hình 2.15.": ("hinh_2_15_twofish_encrypt.png", 5.4),
    "Hình 2.16.": ("hinh_2_16_aesctr.png", 5.4),
    "Hình 2.17.": ("hinh_2_17_aesctr_encrypt.png", 5.4),
    "Hình 2.18.": ("hinh_2_18_python_aes_cbc.png", 5.4),
    "Hình 2.19.": ("hinh_2_19_menu.png", 5.4),
}

def build_ketqua_docx():
    print("Opening source document:", SRC_DOCX)
    doc = docx.Document(SRC_DOCX)

    replaced_count = 0
    for i, p in enumerate(doc.paragraphs):
        for prefix, (img_file, width_in) in IMG_MAP.items():
            if prefix in p.text:
                img_path = os.path.join(SCREENSHOTS_DIR, img_file)
                if not os.path.exists(img_path):
                    print(f"Warning: image {img_path} not found!")
                    continue

                drawings = p._p.xpath(".//w:drawing")
                if drawings:
                    old_drawing = drawings[0]
                    # Create temporary paragraph and run with picture
                    temp_p = doc.add_paragraph()
                    temp_run = temp_p.add_run()
                    temp_run.add_picture(img_path, width=Inches(width_in))
                    new_drawing = temp_run._r.xpath(".//w:drawing")[0]

                    # Replace old drawing with new drawing
                    old_drawing.getparent().replace(old_drawing, new_drawing)
                    # Remove temp paragraph
                    temp_p._p.getparent().remove(temp_p._p)
                    replaced_count += 1
                    print(f"[{replaced_count}] Replaced image for {prefix} with {img_file}")
                break

    print(f"Total images replaced: {replaced_count}")
    doc.save(OUT_DOCX)
    print("Saved DOCX to:", OUT_DOCX, f"({os.path.getsize(OUT_DOCX)} bytes)")

def convert_to_pdf():
    print("Converting to PDF via Word COM...")
    word = win32com.client.DispatchEx("Word.Application")
    word.Visible = False
    word.DisplayAlerts = False
    try:
        doc = word.Documents.Open(OUT_DOCX)
        doc.SaveAs(OUT_PDF, FileFormat=17) # wdFormatPDF = 17
        doc.Close()
        print("Exported PDF to:", OUT_PDF, f"({os.path.getsize(OUT_PDF)} bytes)")
    finally:
        word.Quit()

if __name__ == "__main__":
    build_ketqua_docx()
    convert_to_pdf()
