import argparse
import base64
import binascii
import secrets
from pathlib import Path

from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad


def _validate_key_iv(key: bytes, iv: bytes) -> None:
    if not isinstance(key, bytes) or len(key) not in (16, 24, 32):
        raise ValueError("AES key must be 16, 24, or 32 bytes.")
    if not isinstance(iv, bytes) or len(iv) != AES.block_size:
        raise ValueError("AES-CBC IV must be exactly 16 bytes.")


def _decode_ciphertext(ciphertext: str) -> bytes:
    if not isinstance(ciphertext, str) or not ciphertext:
        raise ValueError("Ciphertext must not be empty.")
    try:
        encrypted = base64.b64decode(ciphertext, validate=True)
    except (binascii.Error, ValueError) as exc:
        raise ValueError("Ciphertext must be valid Base64.") from exc
    if not encrypted or len(encrypted) % AES.block_size:
        raise ValueError("AES-CBC ciphertext must contain complete 16-byte blocks.")
    return encrypted


def encrypt_text(plaintext: str, key: bytes, iv: bytes) -> str:
    _validate_key_iv(key, iv)
    if not isinstance(plaintext, str):
        raise ValueError("Plaintext must be text.")
    try:
        padded = pad(plaintext.encode("utf-8"), AES.block_size)
    except UnicodeEncodeError as exc:
        raise ValueError("Plaintext must be valid UTF-8 text.") from exc
    encrypted = AES.new(key, AES.MODE_CBC, iv).encrypt(padded)
    return base64.b64encode(encrypted).decode("ascii")


def decrypt_text(ciphertext: str, key: bytes, iv: bytes) -> str:
    _validate_key_iv(key, iv)
    encrypted = _decode_ciphertext(ciphertext)
    try:
        plaintext = unpad(AES.new(key, AES.MODE_CBC, iv).decrypt(encrypted), AES.block_size)
        return plaintext.decode("utf-8")
    except (ValueError, UnicodeDecodeError) as exc:
        raise ValueError("Ciphertext has invalid padding or is not UTF-8 for this key and IV.") from exc


def format_ciphertext_file(iv: bytes, ciphertext: str) -> str:
    _validate_key_iv(bytes(32), iv)
    _decode_ciphertext(ciphertext)
    return f"IV={iv.hex().upper()}\n{ciphertext}\n"


def parse_ciphertext_file(content: str) -> tuple[bytes, str]:
    if not isinstance(content, str):
        raise ValueError("Ciphertext file must be text.")
    lines = content.splitlines()
    if len(lines) != 2 or not lines[0].startswith("IV="):
        raise ValueError("File must contain exactly an IV=HEX line and a Base64 ciphertext line.")
    iv_hex = lines[0][3:].strip()
    if len(iv_hex) != AES.block_size * 2:
        raise ValueError("AES-CBC IV must contain exactly 32 HEX characters.")
    try:
        iv = bytes.fromhex(iv_hex)
    except ValueError as exc:
        raise ValueError("AES-CBC IV must contain valid HEX.") from exc
    _validate_key_iv(bytes(32), iv)
    ciphertext = lines[1].strip()
    _decode_ciphertext(ciphertext)
    return iv, ciphertext


class App:
    def __init__(self, root=None):
        import tkinter as tk
        from tkinter import filedialog, messagebox, ttk

        self._tk = tk
        self._filedialog = filedialog
        self._messagebox = messagebox
        self._ttk = ttk
        self.root = root if root is not None else tk.Tk()
        self.root.title("LAB 2 - AES-CBC")
        self.root.minsize(760, 560)

        self.key_var = tk.StringVar(value="")
        self.iv_var = tk.StringVar(value="")
        self.status_var = tk.StringVar(value="Generate or enter an AES key and IV.")

        self._build_ui()

    def _build_ui(self) -> None:
        tk = self._tk
        ttk = self._ttk
        root = self.root
        root.columnconfigure(0, weight=1)
        root.rowconfigure(2, weight=1)
        root.rowconfigure(4, weight=1)

        key_frame = ttk.Frame(root, padding=10)
        key_frame.grid(row=0, column=0, sticky="ew")
        key_frame.columnconfigure(1, weight=1)
        ttk.Label(key_frame, text="Key (HEX):").grid(row=0, column=0, sticky="w", padx=4, pady=4)
        ttk.Entry(key_frame, textvariable=self.key_var).grid(row=0, column=1, sticky="ew", padx=4, pady=4)
        ttk.Label(key_frame, text="IV (32 HEX chars):").grid(row=1, column=0, sticky="w", padx=4, pady=4)
        ttk.Entry(key_frame, textvariable=self.iv_var).grid(row=1, column=1, sticky="ew", padx=4, pady=4)
        ttk.Button(key_frame, text="Generate Key + IV", command=self.generate_key_iv).grid(
            row=0, column=2, rowspan=2, padx=8
        )

        ttk.Label(root, text="Plaintext").grid(row=1, column=0, sticky="w", padx=14)
        self.plaintext_text = tk.Text(root, height=8, wrap="word")
        self.plaintext_text.grid(row=2, column=0, sticky="nsew", padx=12, pady=(2, 8))

        ttk.Label(root, text="Ciphertext (Base64)").grid(row=3, column=0, sticky="w", padx=14)
        self.ciphertext_text = tk.Text(root, height=8, wrap="word")
        self.ciphertext_text.grid(row=4, column=0, sticky="nsew", padx=12, pady=(2, 8))

        buttons = ttk.Frame(root, padding=(10, 4))
        buttons.grid(row=5, column=0, sticky="ew")
        ttk.Button(buttons, text="Encrypt", command=self.encrypt).pack(side="left", padx=4)
        ttk.Button(buttons, text="Decrypt", command=self.decrypt).pack(side="left", padx=4)
        ttk.Button(buttons, text="Open File", command=self.open_file).pack(side="left", padx=4)
        ttk.Button(buttons, text="Save Ciphertext", command=self.save_file).pack(side="left", padx=4)

        ttk.Label(root, textvariable=self.status_var, padding=10).grid(row=6, column=0, sticky="ew")

    def generate_key_iv(self) -> None:
        self.key_var.set(secrets.token_bytes(32).hex().upper())
        self.iv_var.set(secrets.token_bytes(AES.block_size).hex().upper())
        self.status_var.set("Generated a 256-bit AES key and a fresh IV.")

    def encrypt(self) -> None:
        try:
            ciphertext = encrypt_text(
                self.plaintext_text.get("1.0", "end-1c"),
                bytes.fromhex(self.key_var.get().strip()),
                bytes.fromhex(self.iv_var.get().strip()),
            )
        except (ValueError, binascii.Error) as exc:
            self.status_var.set(str(exc))
            return
        self.ciphertext_text.delete("1.0", "end")
        self.ciphertext_text.insert("1.0", ciphertext)
        self.status_var.set("Encryption complete.")

    def decrypt(self) -> None:
        try:
            plaintext = decrypt_text(
                self.ciphertext_text.get("1.0", "end-1c").strip(),
                bytes.fromhex(self.key_var.get().strip()),
                bytes.fromhex(self.iv_var.get().strip()),
            )
        except (ValueError, binascii.Error) as exc:
            self.status_var.set(str(exc))
            return
        self.plaintext_text.delete("1.0", "end")
        self.plaintext_text.insert("1.0", plaintext)
        self.status_var.set("Decryption complete.")

    def open_file(self) -> None:
        filename = self._filedialog.askopenfilename(
            title="Open AES-CBC ciphertext",
            filetypes=(("Text files", "*.txt"), ("All files", "*.*")),
        )
        if not filename:
            return
        try:
            content = Path(filename).read_text(encoding="utf-8")
            iv, ciphertext = parse_ciphertext_file(content)
        except (OSError, UnicodeError, ValueError, binascii.Error) as exc:
            self.status_var.set(f"Could not open ciphertext file: {exc}")
            return
        self.iv_var.set(iv.hex().upper())
        self.ciphertext_text.delete("1.0", "end")
        self.ciphertext_text.insert("1.0", ciphertext)
        self.status_var.set("Ciphertext and IV loaded. Enter the matching key to decrypt.")

    def save_file(self) -> None:
        ciphertext = self.ciphertext_text.get("1.0", "end-1c").strip()
        try:
            iv = bytes.fromhex(self.iv_var.get().strip())
            content = format_ciphertext_file(iv, ciphertext)
        except (ValueError, binascii.Error) as exc:
            self.status_var.set(str(exc))
            return
        filename = self._filedialog.asksaveasfilename(
            title="Save AES-CBC ciphertext",
            defaultextension=".txt",
            initialfile="aes-cbc-ciphertext.txt",
            filetypes=(("Text files", "*.txt"), ("All files", "*.*")),
        )
        if not filename:
            return
        try:
            Path(filename).write_text(content, encoding="utf-8")
        except OSError as exc:
            self.status_var.set(f"Could not save ciphertext file: {exc}")
            return
        self.status_var.set("Saved IV and ciphertext. The key is not stored in the file.")


def _run_self_test() -> None:
    key = secrets.token_bytes(32)
    iv = secrets.token_bytes(AES.block_size)
    plaintext = "LAB 2 AES-CBC self-test — " * 2048
    ciphertext = encrypt_text(plaintext, key, iv)
    if decrypt_text(ciphertext, key, iv) != plaintext:
        raise RuntimeError("AES-CBC self-test round trip failed.")


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description="LAB 2 AES-CBC encryption application.")
    parser.add_argument("--self-test", action="store_true", help="run a crypto round-trip without opening Tkinter")
    args = parser.parse_args(argv)
    if args.self_test:
        _run_self_test()
        print("SELF-TEST OK")
        return 0

    import tkinter as tk

    root = tk.Tk()
    App(root)
    root.mainloop()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
