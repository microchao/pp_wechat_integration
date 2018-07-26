package iclp.pp.ppsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PpsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PpsearchApplication.class, args);
	}
}
