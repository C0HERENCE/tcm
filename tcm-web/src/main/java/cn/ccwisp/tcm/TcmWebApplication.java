package cn.ccwisp.tcm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.ccwisp.tcm.generated")
public class TcmWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcmWebApplication.class, args);
	}

}
