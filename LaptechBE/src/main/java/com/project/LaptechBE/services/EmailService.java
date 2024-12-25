package com.project.LaptechBE.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
        private final JavaMailSender mailSender;

        public boolean sendEmail(String to, String subject, String text, String html) {
            try {
                // Tạo một email
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                // Cấu hình email
                helper.setFrom("laptech123456@gmail.com"); // Địa chỉ email gửi
                helper.setTo(to); // Địa chỉ email nhận
                helper.setSubject(subject); // Tiêu đề
                helper.setText(text, html != null); // Nội dung dạng text hoặc HTML
                if (html != null) {
                    helper.setText(html, true); // Nội dung dạng HTML
                }

                // Gửi email
                mailSender.send(message);
                return true;
            } catch (MessagingException e) {
                System.err.println("Error sending email: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
}
