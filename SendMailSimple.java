import java.io.*;
import java.nio.file.*;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * Simple Java mail sender using values from .env
 * Sends: Subject "hello", body "testtesttest" to nicatmazanli@gmail.com
 */
public class SendMailSimple {
    public static void main(String[] args) {
        try {
            Map<String, String> env = loadDotEnv(Paths.get(".env"));

            String host = env.getOrDefault("MAIL_HOST", "smtp.gmail.com");
            String port = env.getOrDefault("MAIL_PORT", "587");
            String username = env.get("MAIL_USERNAME");
            String password = env.get("MAIL_PASSWORD");
            String from = env.getOrDefault("MAIL_FROM", username);
            String to = "nicatmazanli@gmail.com";

            if (username == null || password == null) {
                System.err.println("Missing MAIL_USERNAME or MAIL_PASSWORD in .env");
                return;
            }

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.debug", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("hello");
            msg.setText("testtesttest");
            msg.setSentDate(new Date());

            System.out.println("Sending mail...");
            Transport.send(msg);
            System.out.println("✅ Mail sent successfully to " + to);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> loadDotEnv(Path path) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (!Files.exists(path)) return map;
        for (String raw : Files.readAllLines(path)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int idx = line.indexOf('=');
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String val = line.substring(idx + 1).trim();
                if ((val.startsWith("\"") && val.endsWith("\"")) ||
                    (val.startsWith("'") && val.endsWith("'"))) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(key, val);
            }
        }
        return map;
    }
}
