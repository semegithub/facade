package com.openshift.cloudnative.poc.autoscaling.facade;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class ScalingController {

	@GetMapping(path = "/", produces = "text/html")
	@ApiOperation("Api status")
	public String status() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + "\n";

		System.out.println(message);

		return message;
	}
	
	@GetMapping(path = "/lightCPUcall", produces = "text/html")
	@ApiOperation("Light CPU call")
	public String lightCPUcall() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + "\n";

		System.out.println(message);

		return message;
	}

//	@GetMapping(path = "/facadelightredirect", produces = "application/text")
//	public String lightredirect() {
//		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
//		String message = "Facade on host " + hostname + " - light redirect ";
//
//		RestTemplate restTemplate = new RestTemplate();
//		String result = restTemplate.getForObject("http://localhost:8080/child/init", String.class);
//		message += result;
//
//		System.out.println(message);
//		return message;
//	}

	@GetMapping(path = "/highCPUcall", produces = "text/html")
	@ApiOperation("Heavy CPU call")
	public String highCPUcall() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - delayed redirect ";
		for (int i = 0; i < 1000000; i++) {
			try {
				byte[] iv = new byte[16];
				new SecureRandom().nextBytes(iv);

				// IV
				IvParameterSpec ivSpec = new IvParameterSpec(iv);

				// Key
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secretKey = generator.generateKey();

				// Encrypt
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
				cipher.update("0123456789012345".getBytes());

				byte[] data = cipher.doFinal();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println(message);

		return message;
	}
}
