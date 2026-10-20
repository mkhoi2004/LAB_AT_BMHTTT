HTTPS inspection is OPTIONAL.

Use only on isolated LAB clients. The browser/client must trust the LAB7 E2Guardian Root CA.
Required E2Guardian main settings (paths shown for provided generator):
enablessl = on
cacertificatepath = '/etc/e2guardian/private/ca.pem'
caprivatekeypath = '/etc/e2guardian/private/ca.key'
certprivatekeypath = '/etc/e2guardian/private/cert.key'
generatedcertpath = '/etc/e2guardian/private/generatedcerts/'

In e2guardianf1.conf:
sslmitm = on

After changes restart E2Guardian. If a browser reports an untrusted issuer, verify the CA was imported into the Trusted Root store of the LAB client. Do not export or reuse the LAB root CA outside this exercise.
