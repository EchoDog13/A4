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
kb-mbp-m3.local, 8/10/2024, PrivateKeyEntry,
Certificate fingerprint (SHA-256): 2A:F8:0E:65:C1:23:D5:87:4F:6B:8E:9C:2F:5C:63:D3:0E:90:98:68:21:C6:DC:01:87:CC:41:0B:00:92:29:EB
root, 8/10/2024, trustedCertEntry,
Certificate fingerprint (SHA-256): 2C:C5:C6:86:E8:A2:C6:73:14:DA:EF:6F:F5:9D:39:4C:59:44:E4:5C:B0:0B:B5:A0:14:EB:6E:7F:A7:F7:42:5F

## Step 4

F: The exception from the first command:

PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

G: The exce