import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.time.Instant;

public class Main extends JFrame {

    private static final long TIME_STEP_SECONDS = 30L;
    private static final int TOTP_DIGITS = 6;
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private JTextField secretField;
    private JLabel totpLabel;
    private JTextField verifyField;
    private JLabel statusLabel;

    public Main() {
        setTitle("TOTP Generator - Java GUI");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        startAutoRefresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        secretField = new JTextField();
        JButton generateSecretBtn = new JButton("Generate Secret");
        generateSecretBtn.addActionListener(e ->
                secretField.setText(generateBase32Secret(16)));

        JButton generateTotpBtn = new JButton("Generate TOTP");
        generateTotpBtn.addActionListener(e ->
                updateTotp());

        totpLabel = new JLabel("TOTP: ------", SwingConstants.CENTER);
        totpLabel.setFont(new Font("Arial", Font.BOLD, 22));

        verifyField = new JTextField();
        JButton verifyBtn = new JButton("Verify Code");
        verifyBtn.addActionListener(e ->
                verifyCode());

        statusLabel = new JLabel("", SwingConstants.CENTER);

        panel.add(new JLabel("Secret (Base32):"));
        panel.add(secretField);
        panel.add(generateSecretBtn);
        panel.add(generateTotpBtn);
        panel.add(totpLabel);
        panel.add(new JLabel("Enter Code to Verify:"));
        panel.add(verifyField);
        panel.add(verifyBtn);
        panel.add(statusLabel);

        add(panel);
    }

    private void updateTotp() {
        String secret = secretField.getText().trim();
        if (!secret.isEmpty()) {
            String code = generateTotp(secret);
            totpLabel.setText("TOTP: " + code);
        }
    }

    private void verifyCode() {
        String secret = secretField.getText().trim();
        String code = verifyField.getText().trim();

        if (verifyTotp(secret, code, 1)) {
            statusLabel.setText("✅ Code Verified!");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("❌ Invalid Code!");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(1000, e -> updateTotp());
        timer.start();
    }

    // ================= TOTP LOGIC =================

    private static String generateBase32Secret(int length) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(BASE32_CHARS.charAt(rnd.nextInt(BASE32_CHARS.length())));
        }
        return sb.toString();
    }

    private static byte[] base32Decode(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        int buffer = 0, bitsLeft = 0, index = 0;
        byte[] result = new byte[base32.length() * 5 / 8];

        for (char c : base32.toCharArray()) {
            int val = BASE32_CHARS.indexOf(c);
            buffer <<= 5;
            buffer |= val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[index++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        return result;
    }

    private static String generateTotp(String secret) {
        try {
            byte[] key = base32Decode(secret);
            long counter = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;

            byte[] data = new byte[8];
            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (counter & 0xFF);
                counter >>= 8;
            }

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, TOTP_DIGITS);
            return String.format("%06d", otp);

        } catch (Exception e) {
            return "ERROR";
        }
    }

    private static boolean verifyTotp(String secret, String code, int window) {
        long currentCounter = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
        for (int i = -window; i <= window; i++) {
            if (generateTotpForCounter(secret, currentCounter + i).equals(code)) {
                return true;
            }
        }
        return false;
    }

    private static String generateTotpForCounter(String secret, long counter) {
        try {
            byte[] key = base32Decode(secret);

            byte[] data = new byte[8];
            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (counter & 0xFF);
                counter >>= 8;
            }

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, TOTP_DIGITS);
            return String.format("%06d", otp);

        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
