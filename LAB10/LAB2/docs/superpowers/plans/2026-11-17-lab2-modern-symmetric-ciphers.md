# Lab 2: Modern Symmetric Ciphers Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Lab 2 Java/Swing and Python/Tkinter applications, local SQL setup, and a results DOCX with genuine screenshots of verified behavior.

**Architecture:** Create a standalone Maven project in this folder with crypto, database, and Swing form classes under `week_02`. Keep the Python AES-CBC GUI in a separate script with testable crypto functions. Build the report from an untouched copy of the supplied guide, inserting screenshots only after the corresponding behavior is verified.

**Tech Stack:** Java 17, Maven, Swing, JUnit 5, Microsoft SQL Server JDBC, BouncyCastle Twofish, Python 3.10+, Tkinter, `cryptography`, and DOCX/XML tooling.

**Spec:** Design content supplied and read in the task context; the expected file `docs/superpowers/specs/2026-11-17-lab2-modern-symmetric-ciphers-design.md` was not present when implementation began.

## Global Constraints

- Java 17, Maven, Swing, UTF-8, SQL Server JDBC, package `week_02`; follow the Vietnamese naming and presentation style in LAB1.
- Keep LAB2 as a standalone `LAB2_MaHoaHienDai` project; do not modify LAB1 or `LAB2_HuongDan.docx`.
- Use BouncyCastle for Twofish and Python's `cryptography` package for AES-CBC.
- DES and TripleDES use CBC with random 8-byte IVs prefixed to Base64 ciphertext; RC4 displays HEX. These are educational demonstrations only.
- AES uses AES-256-GCM, PBKDF2-HMAC-SHA256, 120,000 iterations, 16-byte salt, 12-byte nonce, and an authentication tag; require a successful login before AES operations.
- Twofish uses CBC, a 16-byte IV, and a 256-bit SHA-256-derived key. AES-CTR uses a 32-byte key, 12-byte nonce, and no padding.
- SQL Server database default is `LAB2_MaHoaHienDai`, local host/port defaults are `127.0.0.1:14333`; the operator enters connection credentials. Never embed or store database passwords in source.
- Store only user salt/hash and crypto operation metadata in SQL. Use prepared statements; never store plaintext, ciphertext, or keys in history.
- The Python GUI uses AES-256-CBC, a fresh random 16-byte IV, PKCS7 padding, and persists IV plus ciphertext only.
- `Lab2_KetQua.docx` must preserve the guide's text, tables, figure captions, and organization; insert genuine screenshots for figures 2.1 and 2.3–2.19, preserve the original design-time figure 2.2, and do not claim unverified database behavior.

## Review Focus

- Invalid, truncated, or malformed Base64/HEX inputs must return a clear error rather than crash a form; test malformed cipher inputs in `CipherTest`.
- DES-CBC and TripleDES-CBC are unauthenticated: test detected bad padding where reproducible, but do not claim every wrong key or tampering attempt is detectable.
- AES-GCM must reject modified ciphertext/tag, while login is required before either AES operation; test tampering in `CipherTest` and the unauthenticated UI state in `FormTest`.
- AES-CTR must preserve arbitrary UTF-8 byte lengths (including >1,000 characters), validate key/nonce byte lengths, and must not pad; test decoded ciphertext length and round trips in `CipherTest`.
- Database unavailability, duplicate usernames, and incorrect credentials must be explicit; SQL history must contain metadata only. Test validation and SQL integration when a real configured SQL Server is available.

---

### Task 1: Creating the Java project and cipher core

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/week_02/CipherUtil.java`
- Create: `src/main/java/week_02/DESCipher.java`
- Create: `src/main/java/week_02/TripleDESCipher.java`
- Create: `src/main/java/week_02/AESCipher.java`
- Create: `src/main/java/week_02/RC4Cipher.java`
- Create: `src/main/java/week_02/TwofishCipher.java`
- Create: `src/main/java/week_02/AESCTRCipher.java`
- Test: `src/test/java/week_02/CipherTest.java`

**Interfaces:**
- `CipherUtil.utf8(String) -> byte[]`, `text(byte[]) -> String`, `b64(byte[]) -> String`, `fromB64(String) -> byte[]`, `hex(byte[]) -> String`, `fromHex(String) -> byte[]`, `randomBytes(int) -> byte[]`, `deriveKey(char[], byte[], int iterations, int keyBytes) -> byte[]`, and `requireKey(String) -> void`.
- `DESCipher.encrypt(String plaintext, String key)` / `decrypt(String ciphertext, String key)`.
- `TripleDESCipher.encrypt(String plaintext, String key)` / `decrypt(String ciphertext, String key)`.
- `AESCipher.encrypt(String plaintext, String password)` / `decrypt(String ciphertext, String password)`.
- `RC4Cipher.encrypt(String plaintext, String key)` / `decrypt(String hexCiphertext, String key)`.
- `TwofishCipher.newIv()`, `encrypt(String plaintext, String key, String ivHex)`, `decrypt(String ciphertext, String key, String ivHex)`, `toFile(String ivHex, String ciphertext)`, `fromFile(String content)`.
- `AESCTRCipher.newKey()`, `newNonce()`, `encrypt(String plaintext, String keyBase64, String nonceBase64)`, `decrypt(String ciphertextBase64, String keyBase64, String nonceBase64)`, `toFile(String nonceBase64, String ciphertextBase64)`, `fromFile(String content)`.
- All cipher APIs throw `IllegalArgumentException` for invalid user input and surface cryptographic failures as explicit errors; no silent or success-shaped fallback.

- [ ] **Step 1: Write focused failing JUnit tests** for DES and TripleDES round trips/key lengths; the required RC4 vector `Plaintext`/`Key` → `BBF316E8D940AF0AD3`; AES-GCM round trip and tamper rejection; Twofish IV/file round trip; and AES-CTR ordinary and >1,000-character round trips with decoded ciphertext byte length equal to UTF-8 plaintext byte length.
- [ ] **Step 2: Run `mvn test`** and confirm the tests fail because the project/classes are not implemented.
- [ ] **Step 3: Implement the Maven project and cipher APIs** with group `vn.edu.lab`, artifact `LAB2_MaHoaHienDai`, version `1.0-SNAPSHOT`, Java release 17, JUnit 5, Microsoft SQL Server JDBC, and `bcprov-jdk18on`; configure `week_02.frm_Menu` as the exec main class. Use exact IV, key, salt, nonce, tag, padding, and iteration values from Global Constraints. Encode AES-CTR key and nonce as Base64 as in the guide; serialize Twofish files as `IV=<HEX>` followed by Base64 ciphertext, and CTR files as `NONCE=<Base64>` followed by Base64 ciphertext.
- [ ] **Step 4: Run `mvn test`** and confirm all cipher tests pass, including malformed Base64/HEX and invalid key-size cases.
- [ ] **Step 5: Commit** the project and cipher-core files with message `feat: add lab2 cipher core`.

### Task 2: Adding SQL configuration, history, and user authentication

**Files:**
- Create: `src/main/java/week_02/DbConfig.java`
- Create: `src/main/java/week_02/HistoryDao.java`
- Create: `src/main/java/week_02/UserDao.java`
- Create: `03_SQL_SERVER/01_Tao_CSDL.sql`
- Create: `03_SQL_SERVER/02_Tao_Tai_Khoan_SQL.ps1`
- Create: `03_SQL_SERVER/03_Kiem_Tra_Du_Lieu.sql`
- Test: `src/test/java/week_02/UserDaoTest.java`
- Test: `src/test/java/week_02/HistoryDaoTest.java`

**Interfaces:**
- `DbConfig.set(String server, int port, String database, String user, char[] password)`, `open()`, `test()`, and `clear()`; connection values remain in memory and default to `127.0.0.1`, `14333`, and `LAB2_MaHoaHienDai`.
- `HistoryDao.insert(String algorithm, String operation, String username, int inputLength, int outputLength)`, `tryInsert(String algorithm, String operation, String username, int inputLength, int outputLength) -> String`, `emptyModel() -> DefaultTableModel`, and `loadLatest(int top) -> DefaultTableModel`. Lengths are UTF-8 byte counts.
- `UserDao.register(String username, char[] password)`, `login(String username, char[] password)`, and `newTicket()`. Registration requires an 8-character minimum, stores a random 16-byte salt and 32-byte PBKDF2-HMAC-SHA256 hash at 120,000 iterations, and login compares hashes in constant time. A successful login ticket is 16 random bytes represented as 32 uppercase HEX characters.

- [ ] **Step 1: Write tests** for username/password validation, ticket shape, failed-login behavior, and duplicate-user handling using a narrow persistence seam or a configured SQL integration test; assert history interfaces accept metadata only.
- [ ] **Step 2: Run the focused tests** and verify they fail before implementation.
- [ ] **Step 3: Implement JDBC access and local setup scripts.** Match the guide's `dbo.app_users` (`user_id`, unique `username`, 16-byte `salt`, 32-byte `password_hash`, UTC `created_at`) and `dbo.crypto_history` (`history_id`, `algorithm`, `operation`, nullable `app_username`, input/output lengths, UTC `created_at`). Use prepared statements. The PowerShell login setup prompts for a hidden password at runtime and never writes it to a file; include no example/default secret.
- [ ] **Step 4: Run the focused tests and build.** If SQL Server is configured, run the integration test against `127.0.0.1:14333`, register/login, record all four AES actions, and verify table rows and absence of content/key columns. Otherwise, record SQL integration as an unmet environment prerequisite, not a pass.
- [ ] **Step 5: Commit** SQL and authentication files with message `feat: add lab2 sql auth and history`.

### Task 3: Building the Java Swing forms

**Files:**
- Create: `src/main/java/week_02/FileTextIO.java`
- Create: `src/main/java/week_02/frm_Menu.java`
- Create: `src/main/java/week_02/frm_DES.java`
- Create: `src/main/java/week_02/frm_TripleDES.java`
- Create: `src/main/java/week_02/frm_AES.java`
- Create: `src/main/java/week_02/frm_RC4.java`
- Create: `src/main/java/week_02/frm_Twofish.java`
- Create: `src/main/java/week_02/frm_AES_CTR.java`
- Create: `src/main/java/week_02/frm_KetNoiSQL.java`
- Create: `src/main/java/week_02/frm_LichSu.java`
- Test: `src/test/java/week_02/FormTest.java`

**Interfaces:**
- `FileTextIO.openText(JFrame, JTextArea) -> String`, `saveText(JFrame, JTextArea, String defaultName) -> String`, `openString(JFrame) -> String`, and `saveString(JFrame, String content, String defaultName) -> String` use UTF-8 and Swing file choosers.
- `frm_Menu` is the application entry point and opens each of the six cipher forms plus SQL connection and history forms.
- Each non-menu form uses `DISPOSE_ON_CLOSE`; the menu alone exits the application.

- [ ] **Step 1: Write form behavior tests** for menu navigation targets/default close behavior and AES encryption/decryption being unavailable before login; test model and save-format helpers without showing dialogs.
- [ ] **Step 2: Run `mvn test`** and confirm the new form tests fail before implementation.
- [ ] **Step 3: Implement the nine Vietnamese Swing forms** with the guide's labels and workflows. Hook each cipher form to its cipher API; provide open/save for text-based forms, save/open of IV or nonce plus ciphertext for Twofish/CTR, registration/login/ticket gating for AES, runtime SQL credential entry, and history refresh. Show actionable errors for invalid input and unavailable SQL; do not store passwords in files or history.
- [ ] **Step 4: Run `mvn test` and `mvn compile exec:java`.** Verify each form opens/closes correctly and each file workflow restores the required metadata and ciphertext. Confirm the application starts without an available database and reports its connection state explicitly.
- [ ] **Step 5: Commit** Java forms and file I/O with message `feat: add lab2 swing interface`.

### Task 4: Creating the Python AES-CBC application

**Files:**
- Create: `aes_cbc_gui.py`
- Create: `requirements.txt`
- Test: `tests/test_aes_cbc_gui.py`

**Interfaces:**
- `encrypt_text(plaintext: str, key: bytes, iv: bytes) -> str` returns Base64 ciphertext after UTF-8 encoding and PKCS7 padding.
- `decrypt_text(ciphertext: str, key: bytes, iv: bytes) -> str` validates and decrypts that representation, raising `ValueError` for malformed or unauthentic data.
- The `App` Tkinter class provides generated key/IV, encrypt/decrypt, and file open/save; saved files contain `IV=<HEX>` and ciphertext only.

- [ ] **Step 1: Write failing Python tests** for a UTF-8 long-text round trip, invalid key/IV/ciphertext, and the CLI self-test success output.
- [ ] **Step 2: Run `python -m unittest discover -s tests -v`** and confirm the expected failures.
- [ ] **Step 3: Implement the independent crypto functions and Tkinter interface.** Generate a 32-byte key and fresh 16-byte IV with `secrets`; keep the `--self-test` path independent of Tkinter window creation and print exactly `SELF-TEST OK` on success.
- [ ] **Step 4: Run `python -m unittest discover -s tests -v` and `python aes_cbc_gui.py --self-test`**; verify long text round-trips and the command prints `SELF-TEST OK`.
- [ ] **Step 5: Commit** Python source, dependency manifest, and tests with message `feat: add python aes cbc lab`.

### Task 5: Documenting setup and run instructions

**Files:**
- Create: `README.md`

- [ ] **Step 1: Write the README** with Java and Python run/test commands, SQL Server Express prerequisites and setup order, operator-provided SQL credentials, and the educational-only warning for DES, TripleDES, RC4, and unauthenticated CTR.
- [ ] **Step 2: Check every command** against the Maven/Python files and SQL scripts created in Tasks 1–4; remove any command that cannot be run as written.
- [ ] **Step 3: Commit** the README with message `docs: add lab2 setup instructions`.

### Task 6: Producing the verified results report

**Files:**
- Create: `screenshots/` (genuine application captures only)
- Create: `tools/build_report.py`
- Create: `Lab2_KetQua.docx`
- Preserve unchanged: `LAB2_HuongDan.docx`

- [ ] **Step 1: Verify report prerequisites** by recording the source DOCX SHA-256 and confirming each required Java/Python behavior plus database integration status. Do not fabricate DB output or screenshots.
- [ ] **Step 2: Capture actual application screenshots** for the menu (2.1), SQL connection (2.3), populated history (2.4), and functional results for figures 2.5–2.19. Preserve the source's design-time NetBeans illustration for 2.2. If a database-dependent behavior cannot be tested locally, leave it explicitly unverified rather than presenting a simulated result.
- [ ] **Step 3: Write and run `tools/build_report.py`** to copy the guide document and insert the corresponding genuine screenshots at their existing result sections while retaining its text, tables, captions, and organization.
- [ ] **Step 4: Verify the output DOCX** opens as a ZIP/DOCX, retains the guide's section/caption structure and original figure 2.2, includes all verified screenshots, and that the source guide SHA-256 is unchanged. Record any environment-blocked database result in the README/report without claiming success.
- [ ] **Step 5: Commit** report-generation source, screenshots, and final report with message `docs: add verified lab2 results report`.

## Self-Review

- **Spec coverage:** Tasks 1–3 cover the Java project, six cipher topics, shared I/O, menu, SQL connection/history, and AES login. Task 4 covers Python AES-CBC and `--self-test`. Task 5 covers operator setup and run instructions. Task 6 covers genuine screenshots and the report.
- **Step scan:** Each task has a failing-test/implementation/verification sequence where applicable; report and README tasks use direct artifact checks.
- **Type consistency:** Cipher APIs, SQL DAO signatures, and Python crypto function signatures are declared in the owning task before downstream use.
- **Review focus:** All five identified failure classes have an explicit test or real SQL integration verification in the task that owns the behavior.
- **Proportion:** The plan describes file boundaries, interfaces, test outcomes, and key spec values without transcribing implementations.
