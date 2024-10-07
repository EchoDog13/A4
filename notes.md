# Notes to be completed for Assignment 4 (part of the assessment)

## Step 1

A: Passphrase for ca-private.pem:
waikatoPrivatePem1

B: Password for ca-cert.jks:
keyStore

C: The CA's certificate fingerprint (SHA256) in ca-cert.jks:
SubjectKeyIdentifier [
KeyIdentifier [
0000: EC 24 3B 36 37 83 77 11 35 A4 54 7D 88 CB 2C BC .$;67.w.5.T...,.
0010: 98 91 0F 39 ...9
]
]

## Step 2

D: Passphrase for server.jks:
serverKeyStore (with a space after)

E: The CA's certificate fingerprint (SHA256) in server.jks:
69:13:89:FB:7D:6D:11:3A:D8:80:51:29:9A:6F:D6:16:99:24:42:A5:28:B6:32:AF:B0:21:9E:E6:F3:BE:B4:09

## Step 4

F: The exception from the first command:
PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
G: The exception from the second command:
No name matching localhost found
keytool -certreq -alias KB-MBP-M3 -file server.csr \-keystore server.jks

keytool -import -trustcacerts -alias root -file ca-cert.pem \
-keystore server.jks
keytool -import -alias KB-MBP-M3 \
-file server-cert.pem -keystore server.jks
