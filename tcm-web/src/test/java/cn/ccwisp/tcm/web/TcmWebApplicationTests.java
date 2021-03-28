package cn.ccwisp.tcm.web;

import cn.ccwisp.tcm.generated.mapper.HerbsInfoMapper;
import cn.ccwisp.tcm.search.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TcmWebApplicationTests {
	@Autowired
	TestService testService;
	@Test
	void contextLoads() {
		testService.TestMethod();
	}

}
