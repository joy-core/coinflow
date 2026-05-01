package me.sunlianhui.coinflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.sunlianhui.coinflow.modules.*.mapper")
public class CoinFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoinFlowApplication.class, args);
	}

}
