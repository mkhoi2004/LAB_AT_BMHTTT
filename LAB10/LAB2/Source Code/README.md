# LAB 2 - Modern Symmetric Ciphers

The lab contains a Java Swing application for six symmetric-cipher exercises, SQL Server account/history storage, and a separate Python AES-CBC/Tkinter application.

## Requirements

- JDK 17 or newer and Maven.
- Python 3.9 or newer with Tkinter available.
- SQL Server Express or another SQL Server instance for account registration, login, and history.

## Java application

From this directory:

```powershell
mvn test
mvn compile exec:java
```

The application opens at the cipher menu. SQL credentials are entered at runtime and are kept in memory only. Without a configured database, cipher exercises remain available except AES-GCM, which requires a successful application login; SQL-dependent actions show their connection errors in the form.

### SQL Server setup

Run the scripts in this order:

1. In SQL Server Management Studio, execute `03_SQL_SERVER/01_Tao_CSDL.sql` using an operator account allowed to create the database and tables.
2. In PowerShell, run `.\03_SQL_SERVER\02_Tao_Tai_Khoan_SQL.ps1`. Enter the SQL Server address and a new password when prompted. The password prompt is hidden; the script configures the `lab2_user` SQL login and its least-privilege grants.
3. In the application's SQL Server Connection form, enter the same server and port, database `LAB2_MaHoaHienDai`, username `lab2_user`, and the password supplied in step 2. Use **Test Connection**.
4. Optionally execute `03_SQL_SERVER/03_Kiem_Tra_Du_Lieu.sql` in SSMS to inspect database rows.

The account-setup script's default endpoint is `127.0.0.1,14333`; change the endpoint in the script prompt and application form to match the SQL Server instance. Ensure TCP/IP and SQL authentication are enabled when using the `lab2_user` SQL login. The JDBC integration test is skipped when a SQL Server is not reachable; a skipped test does not verify database integration.

## Python AES-CBC application

Install the declared dependency, run the GUI, and run tests:

```powershell
python -m pip install -r requirements.txt
python aes_cbc_gui.py
python -m unittest discover -s tests -v
python aes_cbc_gui.py --self-test
```

The self-test does not create a Tkinter window and prints exactly `SELF-TEST OK` when its round trip succeeds. The GUI generates a 256-bit key and a fresh 16-byte IV. Saved ciphertext files contain only an `IV=<HEX>` line and a Base64 ciphertext line; the key is never written to those files.

## Security limitations

This software is an educational exercise, not a production encryption tool. DES, TripleDES, and RC4 are obsolete. AES-CTR and the CBC examples do not authenticate ciphertext; malformed padding is detectable, but tampering or a wrong key is not guaranteed to be detected. Do not use these modes or this application to protect real data. Prefer a maintained authenticated-encryption design for real applications.
