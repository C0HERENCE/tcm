package cn.ccwisp.tcm.service;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void send(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setText(text);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setFrom("ccwisp@qq.com");
        mailSender.send(simpleMailMessage);
    }

    public Pair generateCaptcha() {
        Random random = new Random();
        int i = random.nextInt(999999);
        String v = String.format("%06d", i);
        String k = new BCryptPasswordEncoder().encode(v);
        return new Pair<>(k,v);
    }

    public void validate() {

    }
}
