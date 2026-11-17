import subprocess
import sys
import unittest
from pathlib import Path

from aes_cbc_gui import (
    decrypt_text,
    encrypt_text,
    format_ciphertext_file,
    parse_ciphertext_file,
)


class AesCbcTests(unittest.TestCase):
    def test_round_trips_long_utf8_text(self):
        plaintext = "Xin chào Lab 2! " * 2048
        key = bytes(range(32))
        iv = bytes(range(16))

        ciphertext = encrypt_text(plaintext, key, iv)

        self.assertEqual(plaintext, decrypt_text(ciphertext, key, iv))

    def test_rejects_invalid_key_and_iv_lengths(self):
        with self.assertRaises(ValueError):
            encrypt_text("text", b"short", bytes(16))
        with self.assertRaises(ValueError):
            encrypt_text("text", bytes(32), bytes(8))

    def test_rejects_malformed_ciphertext(self):
        for ciphertext in ("not-base64!", "", "AA=="):
            with self.subTest(ciphertext=ciphertext):
                with self.assertRaises(ValueError):
                    decrypt_text(ciphertext, bytes(32), bytes(16))

    def test_ciphertext_file_round_trip_preserves_iv_and_ciphertext_only(self):
        iv = bytes(range(16))
        ciphertext = encrypt_text("file payload", bytes(range(32)), iv)

        content = format_ciphertext_file(iv, ciphertext)

        self.assertEqual([f"IV={iv.hex().upper()}", ciphertext], content.splitlines())
        self.assertEqual((iv, ciphertext), parse_ciphertext_file(content))

    def test_ciphertext_file_parser_rejects_invalid_structure(self):
        with self.assertRaises(ValueError):
            parse_ciphertext_file("ciphertext only")

    def test_cli_self_test_prints_exact_success_line(self):
        script = Path(__file__).resolve().parents[1] / "aes_cbc_gui.py"
        result = subprocess.run(
            [sys.executable, str(script), "--self-test"],
            check=True,
            capture_output=True,
            text=True,
        )

        self.assertEqual("SELF-TEST OK\n", result.stdout)
        self.assertEqual("", result.stderr)


if __name__ == "__main__":
    unittest.main()
