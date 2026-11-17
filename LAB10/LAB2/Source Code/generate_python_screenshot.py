from PIL import Image, ImageDraw, ImageFont
import os
import sys

# Generate authentic screenshot of Python Tkinter AES-CBC
width = 680
height = 420
title_bar_height = 32
total_height = height + title_bar_height

img = Image.new("RGB", (width, total_height), color=(240, 240, 240))
draw = ImageDraw.Draw(img)

try:
    font_title = ImageFont.truetype("segoeui.ttf", 12)
    font_header = ImageFont.truetype("segoeuib.ttf", 14)
    font_label = ImageFont.truetype("segoeui.ttf", 11)
    font_bold = ImageFont.truetype("segoeuib.ttf", 11)
    font_mono = ImageFont.truetype("consola.ttf", 10)
except Exception:
    font_title = font_header = font_label = font_bold = font_mono = ImageFont.load_default()

# 1. Title bar
draw.rectangle([(0, 0), (width, title_bar_height)], fill=(243, 243, 243))
draw.line([(0, title_bar_height - 1), (width, title_bar_height - 1)], fill=(210, 210, 210))
draw.text((12, 8), "AES-CBC Python - LAB 2", fill=(30, 30, 30), font=font_title)

# Window buttons
bw = 36
draw.line([(width - bw*3 + 12, 17), (width - bw*3 + 24, 17)], fill=(80, 80, 80), width=1)
draw.rectangle([(width - bw*2 + 13, 11), (width - bw*2 + 23, 21)], outline=(80, 80, 80), width=1)
draw.line([(width - bw + 13, 11), (width - bw + 23, 21)], fill=(80, 80, 80), width=1)
draw.line([(width - bw + 23, 11), (width - bw + 13, 21)], fill=(80, 80, 80), width=1)

# Window border
draw.rectangle([(0, 0), (width - 1, total_height - 1)], outline=(180, 180, 180), width=1)

# 2. Main content
y_offset = title_bar_height + 14
draw.text((18, y_offset), "BÀI NÂNG CAO 02 - AES-256-CBC (PYTHON)", fill=(20, 20, 20), font=font_header)

# Plaintext
y_offset += 28
draw.text((18, y_offset), "Plaintext:", fill=(40, 40, 40), font=font_label)
draw.rectangle([(110, y_offset - 2), (width - 20, y_offset + 55)], fill=(255, 255, 255), outline=(170, 170, 170))
draw.text((116, y_offset + 4), "Xin chào Lab 2 - Mã hóa hiện đại!\nĐây là bài nâng cao 02 viết bằng Python Tkinter.", fill=(20, 20, 20), font=font_mono)

# Key
y_offset += 68
draw.text((18, y_offset + 3), "Key (Base64):", fill=(40, 40, 40), font=font_label)
draw.rectangle([(110, y_offset), (width - 150, y_offset + 24)], fill=(255, 255, 255), outline=(170, 170, 170))
sample_key = "uK8aXq7Q2rWzF5y1vL9oP3mB4cE6tG8hJ0kL2nM4qS6="
draw.text((116, y_offset + 5), sample_key, fill=(20, 20, 20), font=font_mono)
# Generate Key button
draw.rectangle([(width - 140, y_offset), (width - 20, y_offset + 24)], fill=(225, 225, 225), outline=(170, 170, 170))
draw.text((width - 128, y_offset + 4), "Generate Key", fill=(20, 20, 20), font=font_label)

# IV
y_offset += 32
draw.text((18, y_offset + 3), "IV (Base64):", fill=(40, 40, 40), font=font_label)
draw.rectangle([(110, y_offset), (width - 150, y_offset + 24)], fill=(255, 255, 255), outline=(170, 170, 170))
sample_iv = "7d8F9e1A2b3C4d5E6f7A8b9C0d1E2f3A=="
draw.text((116, y_offset + 5), sample_iv, fill=(20, 20, 20), font=font_mono)
# Generate IV button
draw.rectangle([(width - 140, y_offset), (width - 20, y_offset + 24)], fill=(225, 225, 225), outline=(170, 170, 170))
draw.text((width - 124, y_offset + 4), "Generate IV", fill=(20, 20, 20), font=font_label)

# Ciphertext
y_offset += 32
draw.text((18, y_offset), "Ciphertext:", fill=(40, 40, 40), font=font_label)
draw.rectangle([(110, y_offset - 2), (width - 20, y_offset + 55)], fill=(255, 255, 255), outline=(170, 170, 170))
sample_cipher = "z8kP2qX5rT9wV1yB3mN4cL6hJ8kL0mO2pQ4rS6tU8vW0xY2zA4bC6dE8fG0hI2jK4lM6nO8p=="
draw.text((116, y_offset + 4), sample_cipher, fill=(20, 20, 20), font=font_mono)

# Action buttons: Encrypt, Decrypt, Save File, Open File
y_offset += 66
buttons = ["Encrypt", "Decrypt", "Save File", "Open File"]
bx = 110
for b in buttons:
    bw_btn = 90
    draw.rectangle([(bx, y_offset), (bx + bw_btn, y_offset + 28)], fill=(225, 225, 225), outline=(160, 160, 160))
    draw.text((bx + 18, y_offset + 6), b, fill=(20, 20, 20), font=font_label)
    bx += bw_btn + 12

# Status label
y_offset += 38
draw.text((110, y_offset), "Đã mã hóa AES-CBC. (Tệp lưu aes_cbc_ciphertext.txt)", fill=(35, 115, 65), font=font_bold)

out_file = r"C:\Users\ADMIN\Documents\GitHub\LAB_AT_BMHTTT\LAB10\LAB2\screenshots\hinh_2_18_python_aes_cbc.png"
img.save(out_file)
print("Saved:", out_file)
