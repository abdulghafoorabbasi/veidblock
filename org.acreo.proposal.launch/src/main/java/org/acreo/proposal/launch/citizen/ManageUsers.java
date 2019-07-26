package org.acreo.proposal.launch.citizen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.exceptions.VeidblockException;

public class ManageUsers {

	public void manage(String[] args) throws VeidblockException, IOException {

		if (args != null && args.length >= 1) {
			new ManageUsers().loadPropertyFile(args[0]);
		}
		ResourceCO admin = new ManageUsers().loadPasswordFile("ipv", args[1]);

		new ManageUsers().options("" + admin.getResourceId(), admin.getPassword(), args[1]);
	}

	public static void main(String[] args) throws VeidblockException, IOException {
		if (new File(args[3]).exists()) {
			new File(args[3]).delete();
		}
		if (args != null && args.length >= 1) {
			new ManageUsers().loadPropertyFile(args[0]);
		}
		ResourceCO admin = new ManageUsers().loadPasswordFile("ipv", args[1]);

		new ManageUsers().options("" + admin.getResourceId(), admin.getPassword(), args[1]);
	}

	public void options(String uidSuper, String password, String fileName) throws IOException, VeidblockException {

		while (true)
			try {
				System.out.println("***************************************");
				System.out.println("*  1. Create City Administrator       *");
				System.out.println("*  2. Create District A Administrator *");
				System.out.println("*  3. Create District B Administrator *");
				System.out.println("*  4. Exit               *");
				System.out.println("*************************");
				System.out.print("Please select Administrative area : ");
				Scanner reader = new Scanner(System.in);
				String temp = reader.nextLine();
				int i = Integer.parseInt(temp);
				switch (i) {
				case 1:
					createCityAdmin(uidSuper, password);
					break;
				case 2:
					createDistrict_A_Admin(uidSuper, password);

					break;
				case 3:
					createDistrict_B_Admin(uidSuper, password);
					createCitizens("distrcit_b_admin", 100, fileName);
					break;
				case 4:
					System.exit(0);
					break;
				default:
					System.out.println("Invalid option !");
				}
				System.out.print("\n\n");

			} catch (NumberFormatException nfe) {
				System.err.println("Invalid Format!");
			}
	}

	public void createCityAdmin(String uidSupper, String password) throws VeidblockException, IOException {
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uidSupper));
		resourceCO.setPassword(password);

		// Authenticate with Supper user and then create Admin users
		ClientAuthenticator authenticator = ClientAuthenticator.builder()
				.uniqueIdentifier("" + resourceCO.getResourceId()).password(resourceCO.getPassword())
				.application("" + resourceCO.getResourceId()).verifier(new Configuration().getAuthServerUrl()).build();

		if (authenticator.autheticate()) {
			System.out.println("Supper user successfully autheticated !");
			System.out.println("Access Token Path: \n\t" + authenticator.getTokenPath());
		}

		System.out.println("Creating City Administrator . . . ");
		ResourceCO resourceCOAdmin = new ResourceCO();
		resourceCOAdmin.setName("City Administrator");
		resourceCOAdmin.setEmail("cityadmin@gmail.com");
		resourceCOAdmin.setUsername("cityadmin");
		resourceCOAdmin.setPassword("cityadmin1234");
		resourceCOAdmin.setOrganizationName("RISE Acreo, AB");
		resourceCOAdmin.setOrganizationUnit("NetLab");
		resourceCOAdmin.setStreet("Isafjordsgatan 22");
		resourceCOAdmin.setCity("16440, Kista");
		resourceCOAdmin.setState("NA");
		resourceCOAdmin.setCountry("SE");
		resourceCOAdmin.setMobile("0105220000");
		resourceCOAdmin.setPhone("0105220000");
		resourceCOAdmin.setBackupEmail("cityadmin@gmail.com");
		resourceCOAdmin.setUrl("http://127.0.0.1:8000/user/resname/ipv");

		Identity identity = Identity.builder().resource(resourceCOAdmin).build();

		ResourceCO admin = identity.registerResourceByAdmin(authenticator);
		admin.setPassword(resourceCOAdmin.getPassword());
		identity = Identity.builder().resource(admin).build();
		identity.assignRole("Admin", authenticator);
		System.out.println("City Administrator successfully registered !");

	}

	public void createDistrict_A_Admin(String uidSupper, String password) throws VeidblockException, IOException {
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uidSupper));
		resourceCO.setPassword(password);

		// Authenticate with Supper user and then create Admin users
		ClientAuthenticator authenticator = ClientAuthenticator.builder()
				.uniqueIdentifier("" + resourceCO.getResourceId()).password(resourceCO.getPassword())
				.application(resourceCO.getResourceId() + "").verifier(new Configuration().getAuthServerUrl()).build();

		if (authenticator.autheticate()) {
			System.out.println("Supper user successfully autheticated !");
			System.out.println("Access Token Path: \n\t" + authenticator.getTokenPath());
		}

		System.out.println("Creating Distrcit A Administrator . . . ");
		ResourceCO resourceCOAdmin = new ResourceCO();
		resourceCOAdmin.setName("Distrcit A Administrator");
		resourceCOAdmin.setEmail("distrcit_a_admin@gmail.com");
		resourceCOAdmin.setUsername("distrcit_a_admin");
		resourceCOAdmin.setPassword("distrcit_a_admin1234");
		resourceCOAdmin.setOrganizationName("RISE Acreo, AB");
		resourceCOAdmin.setOrganizationUnit("NetLab");
		resourceCOAdmin.setStreet("Isafjordsgatan 22");
		resourceCOAdmin.setCity("16440, Kista");
		resourceCOAdmin.setState("NA");
		resourceCOAdmin.setCountry("SE");
		resourceCOAdmin.setMobile("0105221111");
		resourceCOAdmin.setPhone("0105221111");
		resourceCOAdmin.setBackupEmail("distrcit_a_admin@gmail.com");
		resourceCOAdmin.setUrl("http://127.0.0.1:8000/user/resname/ipv");

		Identity identity = Identity.builder().resource(resourceCOAdmin).build();

		ResourceCO admin = identity.registerResourceByAdmin(authenticator);
		admin.setPassword(resourceCOAdmin.getPassword());
		identity = Identity.builder().resource(admin).build();
		identity.assignRole("Admin", authenticator);

		System.out.println("distrcit A Administrator successfully registered !");
	}

	public void createDistrict_B_Admin(String uidSupper, String password) throws VeidblockException, IOException {
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uidSupper));
		resourceCO.setPassword(password);

		// Authenticate with Supper user and then create Admin users
		ClientAuthenticator authenticator = ClientAuthenticator.builder()
				.uniqueIdentifier("" + resourceCO.getResourceId()).password(resourceCO.getPassword())
				.application(resourceCO.getResourceId() + "").verifier(new Configuration().getAuthServerUrl()).build();

		if (authenticator.autheticate()) {
			System.out.println("Supper user successfully autheticated !");
			System.out.println("Access Token Path: \n\t" + authenticator.getTokenPath());
		}

		System.out.println("Creating Distrcit B Administrator . . . ");
		ResourceCO resourceCOAdmin = new ResourceCO();
		resourceCOAdmin.setName("Distrcit B Administrator");
		resourceCOAdmin.setEmail("distrcit_b_admin@gmail.com");
		resourceCOAdmin.setUsername("distrcit_b_admin");
		resourceCOAdmin.setPassword("distrcit_b_admin1234");
		resourceCOAdmin.setOrganizationName("RISE Acreo, AB");
		resourceCOAdmin.setOrganizationUnit("NetLab");
		resourceCOAdmin.setStreet("Isafjordsgatan 22");
		resourceCOAdmin.setCity("16440, Kista");
		resourceCOAdmin.setState("NA");
		resourceCOAdmin.setCountry("SE");
		resourceCOAdmin.setMobile("0105221111");
		resourceCOAdmin.setPhone("0105221111");
		resourceCOAdmin.setBackupEmail("distrcit_b_admin@gmail.com");
		resourceCOAdmin.setUrl("http://127.0.0.1:8000/user/resname/ipv");

		Identity identity = Identity.builder().resource(resourceCOAdmin).build();

		ResourceCO admin = identity.registerResourceByAdmin(authenticator);
		admin.setPassword(resourceCOAdmin.getPassword());
		identity = Identity.builder().resource(admin).build();
		identity.assignRole("Admin", authenticator);
		System.out.println("distrcit B Administrator successfully registered !");
	}

	public void createCitizens(String username, int totalResidents, String fileName)
			throws VeidblockException, IOException {
		ResourceCO admin = loadPasswordFile(username, fileName);
		System.out.println("Authenticating as Admin user to create District Administrator . . .");
		ClientAuthenticator authenticator = ClientAuthenticator.builder().uniqueIdentifier("" + admin.getResourceId())
				.password(admin.getPassword()).application(admin.getResourceId() + "")
				.verifier(new Configuration().getAuthServerUrl()).build();

		if (authenticator.autheticate()) {
			System.out.println("Admin user successfully autheticated !");
			System.out.println("Access Token Path: \n\t" + authenticator.getTokenPath());
		}
		System.out.println("District Administrator " + username + " is registering citizens . . .");
		for (int citizenNo = 0; citizenNo < totalResidents; citizenNo++) {
			createCitizens(citizenNo, authenticator);
		}

		System.out.println("Done.");

	}

	public void createCitizens(int citizenNo, ClientAuthenticator authenticator) throws VeidblockException {
		String password = "pass" + citizenNo;
		ResourceCO resourceCOAdmin = new ResourceCO();
		resourceCOAdmin.setName("citizen_" + citizenNo);
		resourceCOAdmin.setEmail("citizen_" + citizenNo + "@gmail.com");
		resourceCOAdmin.setUsername("citizen" + citizenNo);
		resourceCOAdmin.setPassword(password);
		resourceCOAdmin.setOrganizationName("RISE Acreo, AB");
		resourceCOAdmin.setOrganizationUnit("NetLab");
		resourceCOAdmin.setStreet("Isafjordsgatan 22");
		resourceCOAdmin.setCity("16440, Kista");
		resourceCOAdmin.setState("NA");
		resourceCOAdmin.setCountry("SE");
		resourceCOAdmin.setMobile("01052255" + citizenNo);
		resourceCOAdmin.setPhone("01052255" + citizenNo);
		resourceCOAdmin.setBackupEmail("citizen_back" + citizenNo + "@gmail.com");
		resourceCOAdmin.setUrl(new Configuration().getIPVServerUrl() + "/user/resname/ipv");
		Identity identity = Identity.builder().resource(resourceCOAdmin).build();
		ResourceCO resourceCO2 = identity.registerResourceByAdmin(authenticator);
		identity = Identity.builder().resource(resourceCO2).build();
		identity.assignRole("User", authenticator);
	}

	public void loadPropertyFile(String config) {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(config);
			prop.load(input);
			new Configuration(prop.getProperty(Configuration.AUTH_SERVER), prop.getProperty(Configuration.IPV_SERVER),
					prop.getProperty(Configuration.LEDGER_SERVER));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ResourceCO loadPasswordFile(String username, String fileName) throws VeidblockException {
		ResourceCO resourceCO = new ResourceCO();
		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			String tmep;
			stream.forEach(temp -> {
				String tokens[] = temp.split("=");
				String userName = tokens[0].trim();
				if (username.equals(userName)) {

					String uid = tokens[1].trim();
					resourceCO.setResourceId(Long.parseLong(uid));
					String password = tokens[2].trim();
					resourceCO.setPassword(password);
					return;
				}
			});
			return resourceCO;

		} catch (IOException e) {
			throw new VeidblockException(e);
		}
	}

}
