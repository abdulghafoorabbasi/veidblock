package org.acreo.cleint;

import java.util.Date;

import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.exceptions.VeidblockException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VeidBlockClient {

	
	public static void listRegisterResources() {
		ResourceCO co = new ResourceCO();
		try {
			RestClient restClient = RestClient.builder().baseUrl("http://localhost:8000").build(); 
			Representation response = restClient.get("/resource",  null);
			try {
				ResourceCOList list =  new ObjectMapper().readValue(response.getBody().toString(), ResourceCOList.class);
				System.out.println("Number of Registered Resources : "+list.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (VeidblockException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void fetchRegisterResource() {
		ResourceCO co = new ResourceCO();
		try {
			RestClient restClient = RestClient.builder().baseUrl("http://localhost:8000").build(); 
			Representation response = restClient.get("/resource/user/id/abdul",  null);
			try {
				ResourceCO resourceCO=  new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
				System.out.println("Registered Resources : "+resourceCO.getResourceId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (VeidblockException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void registerUser() {
		ResourceCO co = new ResourceCO();
		try {
			RestClient restClient = RestClient.builder().baseUrl("http://localhost:8000").build();
			
			ResourceCO resourceCO = new ResourceCO();
			
			resourceCO.setActiveStatus(true);
			resourceCO.setBackupEmail("backupEmail");
			resourceCO.setCity("city");
			resourceCO.setCountry("country");
			resourceCO.setCreatedBy("1");
			resourceCO.setCreationDate(new Date());
			resourceCO.setEmail("email");
			resourceCO.setMobile("mobile");
			resourceCO.setName("Name");
			resourceCO.setUsername("shahwaiz");
			resourceCO.setOrganizationId(10L);
			
			resourceCO.setAddress1("Address1()");
			resourceCO.setAddress2("Address2()");
			resourceCO.setPostCode("PostCode");
			resourceCO.setLocation("Location");
			
			resourceCO.setPassword("12345678");
			resourceCO.setPhone("phone");
			resourceCO.setRoles(null);
			resourceCO.setState("state");
			
			resourceCO.setUrl("url");
			Representation response = restClient.post("/resource/user",  resourceCO, null);
			try {
				System.out.println(response.getBody().toString());
				ResourceCO resourceCO2 =  new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
				System.out.println("Registered Resource : "+resourceCO2.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (VeidblockException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void main(String arg []){
		registerUser();
		//listRegisterResources();
		//fetchRegisterResource();
	}
}
