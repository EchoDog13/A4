# Notes to be completed for Assignment 4 (part of the assessment)

## Step 1

A: Passphrase for ca-private.pem:
password
B: Password for ca-cert.jks:
password

C: The CA's certificate fingerprint (SHA256) in ca-cert.jks:
SubjectKeyIdentifier [
KeyIdentifier [
0000: EC 24 3B 36 37 83 77 11 35 A4 54 7D 88 CB 2C BC .$;67.w.5.T...,.
0010: 98 91 0F 39 ...9
]
]

## Step 2

D: Passphrase for server.jks:
passcode
E: The CA's certificate fingerprint (SHA256) in server.jks:
root, 8/10/2024, trustedCertEntry,
Certificate fingerprint (SHA-256): 2C:C5:C6:86:E8:A2:C6:73:14:DA:EF:6F:F5:9D:39:4C:59:44:E4:5C:B0:0B:B5:A0:14:EB:6E:7F:A7:F7:42:5F

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
