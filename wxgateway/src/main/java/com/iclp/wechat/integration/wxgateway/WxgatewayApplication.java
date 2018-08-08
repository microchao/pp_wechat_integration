package com.iclp.wechat.integration.wxgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WxgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WxgatewayApplication.class, args);
	}
}


@RestController
class ServiceInstanceRestController {

	@Autowired
	private DiscoveryClient discoveryClient;


	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
