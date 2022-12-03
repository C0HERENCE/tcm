package cn.ccwisp.tcm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
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
        simpleMailMessage.setFrom("TCM");
        mailSender.send(simpleMailMessage);
    }

    public Map.Entry<String,String> generateCaptcha() {
        Random random = new Random();
        int i = random.nextInt(999999);
        String v = String.format("%06d", i);
        String k = new BCryptPasswordEncoder().encode(v);
        return new AbstractMap.SimpleEntry<>(k, v);
    }

    public void validate() {

    }
}
