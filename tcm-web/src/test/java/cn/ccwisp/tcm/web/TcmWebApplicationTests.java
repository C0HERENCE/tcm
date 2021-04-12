package cn.ccwisp.tcm.web;

import cn.ccwisp.tcm.search.service.TestService;
import cn.ccwisp.tcm.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class TcmWebApplicationTests {
	@Autowired
	TestService testService;
	@Autowired
	MailService mailService;

	@Test
	void contextLoads() {
		System.out.println(new BCryptPasswordEncoder().encode("218817"));
	}

	@Test
	void SendMailTest() {
		mailService.send("749976734@qq.com", "欢迎", "你的验证马");
	}

}
