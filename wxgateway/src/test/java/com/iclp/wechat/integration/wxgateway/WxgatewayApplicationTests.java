package com.iclp.wechat.integration.wxgateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxgatewayApplicationTests {

	@Test
	public void contextLoads() {
	}

	public static void main(String[] args) {
		MultiValueMap map = new LinkedMultiValueMap();
		map.add("aa","bb");
		System.out.println(map);
	}
}
