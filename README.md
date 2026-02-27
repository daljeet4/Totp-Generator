# Totp-Generator
OTP Generator – Java Swing GUI

A desktop-based Time-Based One-Time Password (TOTP) Generator built using Java Swing.
This application implements the RFC 6238 standard using HMAC-SHA1 and generates 6-digit OTP codes compatible with Google Authenticator.

🚀 Features

🔑 Generate secure Base32 secret keys

⏱ Generate 6-digit TOTP (30-second validity)

🔄 Auto-refresh OTP every second

✅ OTP verification with time-window tolerance

🖥 User-friendly Java Swing GUI

🔐 Implements HMAC-SHA1 cryptographic algorithm

🛠 Technologies Used

Java (JDK 8+)

Swing (GUI)

Java Cryptography (HMAC-SHA1)

RFC 6238 Standard

📸 Application Preview

(You can add screenshot here later)

⚙️ How It Works

A Base32 secret key is generated.

Current Unix time is divided into 30-second time steps.

HMAC-SHA1 is applied using the secret key.

Dynamic truncation is performed.

A 6-digit OTP is generated.

OTP refreshes every 30 seconds.

🧠 TOTP Formula

TOTP = HOTP(Secret, CurrentTime / 30)

Where:

HOTP uses HMAC-SHA1

Time step = 30 seconds

Output = 6-digit numeric code

▶️ How To Run
1️⃣ Compile
javac Main.java
2️⃣ Run
java Main




Help you push this properly to GitHub

Or combine this with Login + MySQL
