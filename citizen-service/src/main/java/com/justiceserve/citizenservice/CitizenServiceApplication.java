package com.justiceserve.citizenservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication @EnableDiscoveryClient @EnableFeignClients
public class CitizenServiceApplication {
	public static void main(String[] args) { SpringApplication.run(CitizenServiceApplication.class, args); }
}
