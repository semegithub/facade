package com.openshift.cloudnative.poc.autoscaling.facade;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiOperation;

@RestController
public class ScalingController {
	
	final static String url = "http://child-seme-lab-child.apps-crc.testing/child";

	@GetMapping(path = "/", produces = "text/html")
	@ApiOperation("API status")
	public String status() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade ready on host " + hostname + "\n";

		System.out.println(message);

		return message;
	}

	@GetMapping(path = "/noCPUcall", produces = "text/html")
	@ApiOperation("Direct API call")
	public String noCPUcall() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname;
		long timer = System.currentTimeMillis();
		message += " done in " + (System.currentTimeMillis() - timer) + "[ms]";
		System.out.println(message);

		return message;
	}

	@GetMapping(path = "/highCPUChildHighCPULoadAll", produces = "text/html")
	public String highCPUChildHighCPULoadAll(
			@RequestParam(value = "loopNumber", defaultValue = "1000") Integer loopNumber,
			@RequestParam(value = "childLoopNumber", defaultValue = "1000") Integer childLoopNumber) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - call childHighCPULoadAll -> (";

		try {
			long timer = System.currentTimeMillis();
			generateCPU(loopNumber);
			
			// Prepare acceptable media type
		    List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		    acceptableMediaTypes.add(MediaType.TEXT_HTML);

		    // Prepare header
		    HttpHeaders headers = new HttpHeaders();
		    headers.setAccept(acceptableMediaTypes);
		    HttpEntity<String> entity = new HttpEntity<String>(headers);
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.exchange(url+"/childHighCPULoadAll", HttpMethod.GET, entity, String.class, childLoopNumber);
			message += result.getBody();
			message +=") facade call done in " + (System.currentTimeMillis() - timer) + "[ms]";
		} catch (Exception e) {
			message += e.getMessage();
		} finally {
			System.out.println(message);
		}
		return message;
	}

	@GetMapping(path = "/childLoadAll/parentCPUDelay/{counter}/childCPUDelay/{childcounter}/findAll", produces = "text/html")
	public String childloadall(
			@RequestParam(value = "counter", defaultValue = "0") Integer counter,
			@RequestParam(value = "childcounter", defaultValue = "0") Integer childcounter) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " with CPU delay of " + counter + " - call childLoadAll -> (";

		try {
			long timer = System.currentTimeMillis();
			generateCPU(counter);
			
			// Prepare acceptable media type
		    List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		    acceptableMediaTypes.add(MediaType.TEXT_HTML);

		    // Prepare header
		    HttpHeaders headers = new HttpHeaders();
		    headers.setAccept(acceptableMediaTypes);
		    HttpEntity<String> entity = new HttpEntity<String>(headers);
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.exchange(url+"/childCPUDelay/" + childcounter + "/findAll", HttpMethod.GET, entity, String.class);
			message += result.getBody();
			message +=") facade call done in " + (System.currentTimeMillis() - timer) + "[ms]";
		} catch (Exception e) {
			message += e.getMessage();
		} finally {
			System.out.println(message);
		}
		return message;
	}

	
	@GetMapping(path = "/noCPURedirectCall", produces = "text/html")
	public String highCPURedirectCall() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - no CPU API redirect  ";

		long timer = System.currentTimeMillis();
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject("http://localhost:8080/child/noCPUCall", String.class);
		message += result;

		message += " done in " + (System.currentTimeMillis() - timer) + "[ms]";

		System.out.println(message);
		return message;
	}

	@GetMapping(path = "/highCPUCall", produces = "text/html")
	@ApiOperation(value = "Heavy CPU API call", notes = "Generate CPU by looping on cipher.update(), default value for the number of loops is 1000.")
	public String highCPUcall(
			@RequestParam(value = "loopNumber", required = false, defaultValue = "1000") Integer loopNumber) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - high CPU API call ";
		long timer = System.currentTimeMillis();
		generateCPU(loopNumber);
		message += " done in " + (System.currentTimeMillis() - timer) + "[ms]";
		System.out.println(message);
		return message;
	}

	private void generateCPU(Integer loopNumber) {
		for (int i = 0; i < loopNumber; i++) {
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
				cipher.doFinal();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
