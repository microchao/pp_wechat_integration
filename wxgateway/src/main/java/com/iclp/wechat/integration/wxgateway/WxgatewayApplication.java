package com.iclp.wechat.integration.wxgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

//	@RequestMapping(value = "/search",method = RequestMethod.GET)
	public String serviceInstancesByApplicationName() {
		String object =   restTemplate().getForObject("http://ppsearch:8888/test",String.class);
//		this.discoveryClient.getInstances("ppsearch");
		return object.toString();
	}
}
