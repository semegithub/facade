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

	//final static String url = "http://child-seme-lab-child.apps-crc.testing/child";
	final static String url = "http://child-seme-lab-child.mycluster-254101-05af698fb93eb5b37005f56f283946bf-0000.eu-de.containers.appdomain.cloud";

	@GetMapping(path = "/", produces = "text/html")
	@ApiOperation("API status")
	public String status() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade ready on host " + hostname + "\n";

		System.out.println(message);

		return message;
	}

	@GetMapping(path = "/healthz")
	public String healthz() {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade health check probe on host " + hostname + " \n";

		System.out.println(message);

		return message;
	}

	@GetMapping(path = "/highCPUChildHighCPULoadAll", produces = "text/html")
	public String highCPUChildHighCPULoadAll(
			@RequestParam(value = "stressCounter", defaultValue = "1000") Integer stressCounter,
			@RequestParam(value = "childstressCounter", defaultValue = "1000") Integer childstressCounter) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - CPU stress counter:" + stressCounter
				+ " - call childHighCPULoadAll -> {";

		try {
			long timer = System.currentTimeMillis();
			generateCPU(stressCounter);

			// Prepare acceptable media type
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.TEXT_HTML);

			// Prepare header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.exchange(url + "/childHighCPULoadAll", HttpMethod.GET, entity,
					String.class, childstressCounter);
			message += result.getBody();
			message += "} facade call done in " + (System.currentTimeMillis() - timer) + "[ms]";
		} catch (Exception e) {
			message += e.getMessage();
		} finally {
			System.out.println(message);
		}
		return message;
	}
	
	@GetMapping(path = "/highCPUChildHighCPUCall", produces = "text/html")
	public String highCPUChildHighCPUCall(
			@RequestParam(value = "stressCounter", defaultValue = "1000") Integer stressCounter,
			@RequestParam(value = "childstressCounter", defaultValue = "1000") Integer childstressCounter) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - CPU stress counter:" + stressCounter
				+ " - call childHighCPUCall -> {";

		try {
			long timer = System.currentTimeMillis();
			generateCPU(stressCounter);

			// Prepare acceptable media type
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.TEXT_HTML);

			// Prepare header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.exchange(url + "/childHighCPUCall", HttpMethod.GET, entity,
					String.class, childstressCounter);
			message += result.getBody();
			message += "} facade call done in " + (System.currentTimeMillis() - timer) + "[ms]";
		} catch (Exception e) {
			message += e.getMessage();
		} finally {
			System.out.println(message);
		}
		return message;
	}


//	@GetMapping(path = "/childLoadAll/parentCPUDelay/{counter}/childCPUDelay/{childcounter}/findAll, /childLoadAll/parentCPUDelay/childCPUDelay/{childcounter}/findAll, /childLoadAll/parentCPUDelay/{counter}/childCPUDelay/findAll, /childLoadAll/parentCPUDelay/childCPUDelay/findAll", produces = "text/html")
//	public String childloadall(@PathVariable(value = "counter", required = false) Optional<Integer> counter,
//			@RequestParam(value = "childcounter", required = false) Optional<Integer> childcounter) {
//		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
//		String message = "Facade on host " + hostname + " with CPU delay of " + counter + " - call childLoadAll -> (";
//
//		try {
//			long timer = System.currentTimeMillis();
//			if (counter.isPresent())
//				generateCPU(counter.get().intValue());
//
//			// Prepare acceptable media type
//			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
//			acceptableMediaTypes.add(MediaType.TEXT_HTML);
//
//			// Prepare header
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(acceptableMediaTypes);
//			HttpEntity<String> entity = new HttpEntity<String>(headers);
//			
//			int delayCounter =0;
//			if (!childcounter.isPresent())
//				delayCounter = childcounter.get().intValue();
//			
//			RestTemplate restTemplate = new RestTemplate();
//			ResponseEntity<String> result = restTemplate.exchange(url + "/childCPUDelay/" + delayCounter + "/findAll",
//					HttpMethod.GET, entity, String.class);
//			message += result.getBody();
//			message += ") facade call done in " + (System.currentTimeMillis() - timer) + "[ms]";
//		} catch (Exception e) {
//			message += e.getMessage();
//		} finally {
//			System.out.println(message);
//		}
//		return message;
//	}


	@GetMapping(path = "/highCPUCall", produces = "text/html")
	@ApiOperation(value = "Heavy CPU API call")
	public String highCPUcall(
			@RequestParam(value = "stressCounter", required = false, defaultValue = "1000") Integer stressCounter) {
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		String message = "Facade on host " + hostname + " - CPU stress counter:" + stressCounter;
		long timer = System.currentTimeMillis();
		generateCPU(stressCounter);
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
