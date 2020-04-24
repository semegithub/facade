package com.openshift.cloudnative.poc.autoscaling.facade;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ScalingController {

	@GetMapping(path = "/init", produces = "text/html")
	public String init() {
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

	@GetMapping(path = "/facadedelayedredirect", produces = "text/html")
	public String facadedelayedredirect() {
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
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
		}

		System.out.println(message);

		return message;
	}
}
