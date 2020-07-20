package com.consumer.iot;

import com.consumer.iot.dto.response.DeviceResponse;
import com.consumer.iot.dto.response.ResponseObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IotApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IotApplicationTests {
	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();


	@Test
	public void testE2E() throws JSONException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject json = new JSONObject();
		json.put("filepath", "C:/Users/amitk/MyStuffs/data.csv");

		HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
		ResponseEntity<ResponseObject> responseObject = restTemplate.exchange(
				createURLWithPort("/iot/event/v1"), HttpMethod.POST, request, new ParameterizedTypeReference<ResponseObject>() {});

		assertNotNull(responseObject);
		assertEquals(responseObject.getStatusCode(), HttpStatus.OK);
		assertEquals(responseObject.getBody().getDescription(), "Data refreshed ..");

		String url = createURLWithPort("/iot/event/v1");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("ProductId", "WG11155638")
				.queryParam("tstmp", "1582605137000");

		ResponseEntity<DeviceResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, DeviceResponse.class);

		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getId(), "WG11155638");
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
}
