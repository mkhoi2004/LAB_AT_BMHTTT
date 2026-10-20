LAB7 WEB-PROXY CONFIGURATION NOTES (Ubuntu Server 24.04 LTS)

Target IP: 10.6.0.30/24
Gateway: 10.6.0.1
DNS: 10.6.0.10

Architecture:
Client browser -> E2Guardian :8080 -> Squid :3128 -> pfSense -> destination.

1) Squid
- Keep Squid listening only on localhost:3128 for this lab.
- Allow E2Guardian (localhost) to use Squid.
Suggested fragment is in squid_lab7.conf.fragment. Merge it into /etc/squid/squid.conf after backing up the original.

2) E2Guardian
- Main config: /etc/e2guardian/e2guardian.conf
- Filter group 1: /etc/e2guardian/e2guardianf1.conf
- Default lists: /etc/e2guardian/lists/
- Copy entries from lists/hard_block_sites.txt to bannedsitelist and also to bannedsitelistwithbypass if the installed package has that file/list enabled.
- Copy warning_sites.txt to bannedsitelist but DO NOT place them in the non-bypass list. Set bypass=300 in e2guardianf1.conf to create a 5-minute temporary acknowledgement bypass.
- Copy allow_sites.txt to exceptionsitelist.
- For antivirus scanning enable the clamdscan content scanner in e2guardian.conf and verify clamav-daemon is running.
- Restart: sudo systemctl restart squid e2guardian clamav-daemon

3) 100 MB requirement
E2Guardian maxcontentfilecachescansize limits the maximum object it caches/scans; it is not a strict Sophos-equivalent download-size deny policy in every E2Guardian build. LAB7 therefore treats the 100 MB hard-size block as an OPTIONAL extension. The mandatory assessment is AV scanning plus domain/category controls. If the instructor requires strict >100 MB denial, implement the optional Squid reply_body_max_size ACL in the provided fragment and verify on the local 110MB test file.

4) Entertainment 60-minute requirement
Sophos category quota has no exact pfSense-native equivalent. LAB7 replaces it with a deterministic 60-minute policy window using pfSense Schedule, or a Captive Portal hard timeout of 60 minutes when the instructor wants session-based time control.

5) HTTPS inspection
Optional only. Use files in 06_HTTPS_OPTIONAL, import the generated root CA into the LAB client, and never use this CA outside the isolated lab.
