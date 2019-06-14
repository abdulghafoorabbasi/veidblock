package org.acreo.cleint.main;

import java.io.File;
//auth -n 701785347 -p 123456789
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import org.acreo.cleint.RestClient;
import org.acreo.cleint.resources.AuthorizeCommand;
import org.acreo.cleint.resources.DelResourcesCommands;
import org.acreo.cleint.resources.GetResourcesCommands;
import org.acreo.cleint.resources.LCCommands;
import org.acreo.cleint.resources.ListResourcesCommands;
import org.acreo.cleint.resources.ResourceRegistration;
import org.acreo.cleint.resources.RoleCommands;
import org.acreo.cleint.resources.UpdateResourcesCommands;
import org.acreo.cleint.resources.VeidblockView;
import org.acreo.clientapi.connector.CertificateConnector;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.entities.RoleCO;
import org.acreo.common.entities.RoleCOList;
import org.acreo.common.exceptions.VeidblockException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;




public class Ipvcli {

	RestClient restClient = null;
	RestClient restClientVerifier = null;
	String verifier = "http://localhost:9000";

	private ClientAuthenticator authenticator = null;
	private Configuration configuration = null;

	public Ipvcli() {
		// For
	}

	public Ipvcli(Configuration configuration) {

		verifier = configuration.getAuthServerUrl();
		try {
			restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
			restClientVerifier = RestClient.builder().baseUrl(configuration.getAuthServerUrl()).build();
		} catch (VeidblockException e) {
		}
	}

	private String AUTH = "auth";
	private String QUIT = "quit";

	private String AUTHZ = "authz";
	private String CERT = "cert";
	private String USER = "user";
	private String RES = "res";
	private String ROLE = "role";
	private String HELP = "help";
	private String EXIT = "exit";
	private String NAME_OPTION_LONG = "name";
	private String NAME_OPTION_SHORT = "n";
	private String LC_GET_AUTHZ_OPTION_SHORT = "lc";

	private String PASSWORD_OPTION_LONG = "password";
	private String PASSWORD_OPTION_SHORT = "p";

	private String TOKEN_OPTION_LONG = "token";
	private String TOKEN_OPTION_SHORT = "t";

	private String FILE_OPTION_LONG = "file";
	private String FILE_OPTION_SHORT = "f";

	private String URL_OPTION_LONG = "url";
	private String URL_OPTION_SHORT = "url";

	private String ADD_OPTION_LONG = "add";
	private String ADD_OPTION_SHORT = "add";

	private String LIST_OPTION_LONG = "list";
	private String LIST_OPTION_SHORT = "l";

	private String LIST_ALL_OPTION_LONG = "all";
	private String LIST_ALL_OPTION_SHORT = "all";

	private String GET_OPTION_LONG = "get";
	private String GET_OPTION_SHORT = "get";

	private String UID_OPTION_LONG = "userid";
	private String UID_OPTION_SHORT = "uid";

	private String PUT_OPTION_LONG = "put";
	private String PUT_OPTION_SHORT = "put";

	private String DEL_OPTION_LONG = "del";
	private String DEL_OPTION_SHORT = "del";

	private String RID_OPTION_LONG = "roleid";
	private String RID_OPTION_SHORT = "rid";

	private String ASSIGN_OPTION_LONG = "assign";
	private String ASSIGN_OPTION_SHORT = "asg";

	private String TO_OPTION_LONG = "to";
	private String TO_OPTION_SHORT = "to";

	private String RL_OPTION_LONG = "role";
	private String RL_OPTION_SHORT = "rl";

	private String RM_OPTION_LONG = "remove";
	private String RM_OPTION_SHORT = "rm";

	private String FROM_OPTION_LONG = "from";
	private String FROM_OPTION_SHORT = "from";

	private String DATA_OPTION_LONG = "data";
	private String DATA_OPTION_SHORT = "d";

	public void printHelp() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("\n***************************************************");
		buffer.append("\n\n\t*---Authetication--- ");
		buffer.append("\n\tauth -n <username> -p <password>");
		buffer.append("\n\tquit -uid <user-id>");
		buffer.append("\n\tauth -n <user-id> -p <password>");
		buffer.append("\n\tauth -t -f <access-token>");
		buffer.append("\n\n\t*---Authorization--- ");
		buffer.append("\n\tauthz -from <uid-sender> -to <uid-receiver> -d -f <access-token>");
		buffer.append("\n\tcert -n <user-id> -p <password> -url <URL>");
		buffer.append("\n\n\t*---User Management--- ");
		buffer.append("\n\tuser -add");
		buffer.append("\n\tuser -l");
		buffer.append("\n\tuser -get -uid <User id>");
		buffer.append("\n\tuser -get -n <Username>");
		buffer.append("\n\tuser -put -uid <User id>");
		buffer.append("\n\tuser -put -n <Username>");
		buffer.append("\n\tuser -del -uid <User id>");
		buffer.append("\n\tuser -del -n <Username>");
		buffer.append("\n\n\t*---Resource Management--- ");
		buffer.append("\n\tres -add");
		buffer.append("\n\tres -l");
		buffer.append("\n\tres -all");
		buffer.append("\n\tres -get -uid <User id>");
		buffer.append("\n\tres -get -n <Username>");
		buffer.append("\n\tres -put -uid <User id>");
		buffer.append("\n\tres -put -n <Username>");
		buffer.append("\n\tres -del -uid <User id>");
		buffer.append("\n\tres -del -n <Username>");

		buffer.append("\n\n\t*---Role Management--- ");
		buffer.append("\n\trole -add");
		buffer.append("\n\trole -all");
		buffer.append("\n\trole -get -rid <Role id>");

		buffer.append("\n\trole -get -uid <User id>");

		buffer.append("\n\trole -del -rid <Role id>");
		buffer.append("\n\trole -asg -to <User id> -rl <role name>");
		buffer.append("\n\trole -rm -from <User id> -rl <role name>");
		buffer.append("\n\texit");
		buffer.append("\n\n\t*---Ledger functions--- ");
		buffer.append("\n\tlc -uid <user id>");

		buffer.append("\n\n **************Important Note**************");
		buffer.append("\n *Always use following command to logout. *");
		buffer.append("\n *                  quit                  *");
		buffer.append("\n ******************************************");
		buffer.append("\n***************************************************");
		System.out.println(buffer.toString());
	}

	private Options generateOptions() {
		final Options options = new Options();
		options.addOption(Option.builder(NAME_OPTION_SHORT).required(false).hasArg(true).longOpt(NAME_OPTION_LONG)
				.desc("user name or id").build());
		options.addOption(Option.builder(PASSWORD_OPTION_SHORT).required(false).hasArg(true)
				.longOpt(PASSWORD_OPTION_LONG).desc("Password for authentication").build());

		options.addOption(Option.builder(TOKEN_OPTION_SHORT).required(false).hasArg(false).longOpt(TOKEN_OPTION_LONG)
				.desc("Password for authentication").build());

		options.addOption(Option.builder(FILE_OPTION_SHORT).required(false).hasArg(true).longOpt(FILE_OPTION_LONG)
				.desc("Enter file path").build());

		options.addOption(Option.builder(URL_OPTION_SHORT).required(false).hasArg(true).longOpt(URL_OPTION_LONG)
				.desc("URL").build());

		options.addOption(Option.builder(ADD_OPTION_SHORT).required(false).hasArg(false).longOpt(ADD_OPTION_LONG)
				.desc("Add an entry").build());

		options.addOption(Option.builder(LIST_OPTION_SHORT).required(false).hasArg(false).longOpt(LIST_OPTION_LONG)
				.desc("List entries").build());

		options.addOption(Option.builder(LIST_ALL_OPTION_SHORT).required(false).hasArg(false)
				.longOpt(LIST_ALL_OPTION_LONG).desc("List all entries").build());

		options.addOption(Option.builder(GET_OPTION_SHORT).required(false).hasArg(false).longOpt(GET_OPTION_LONG)
				.desc("Fetch an entry").build());

		options.addOption(Option.builder(UID_OPTION_SHORT).required(false).hasArg(true).longOpt(UID_OPTION_LONG)
				.desc("User or resource identity ").build());

		options.addOption(Option.builder(PUT_OPTION_SHORT).required(false).hasArg(false).longOpt(PUT_OPTION_LONG)
				.desc("Update an entry").build());

		options.addOption(Option.builder(DEL_OPTION_SHORT).required(false).hasArg(false).longOpt(DEL_OPTION_LONG)
				.desc("Delete an entry").build());

		options.addOption(Option.builder(RID_OPTION_SHORT).required(false).hasArg(true).longOpt(RID_OPTION_LONG)
				.desc("Role id").build());

		options.addOption(Option.builder(ASSIGN_OPTION_SHORT).required(false).hasArg(false).longOpt(ASSIGN_OPTION_LONG)
				.desc("Assign role to resource/user").build());

		options.addOption(Option.builder(TO_OPTION_SHORT).required(false).hasArg(true).longOpt(TO_OPTION_LONG)
				.desc("Assign role to ").build());

		options.addOption(Option.builder(RL_OPTION_SHORT).required(false).hasArg(true).longOpt(RL_OPTION_LONG)
				.desc("Role").build());

		options.addOption(Option.builder(RM_OPTION_SHORT).required(false).hasArg(false).longOpt(RM_OPTION_LONG)
				.desc("Remove role").build());

		options.addOption(Option.builder(FROM_OPTION_SHORT).required(false).hasArg(true).longOpt(FROM_OPTION_LONG)
				.desc("Remove role from ").build());

		options.addOption(Option.builder(DATA_OPTION_SHORT).required(false).hasArg(false).longOpt(DATA_OPTION_LONG)
				.desc("Specify data ").build());

		return options;
	}

	// 5.1.39
	// user del -uid 21736315
	static String authCmd = "auth -n 701785347 -p 123456789";
	static String getUserList = "user -l";
	static String getUserInfo = "user -get -uid 839336039";
	static String issueCertInfo = "cert -uid 43293293 -p shah -url http://localhost:9000/cert/request";

	static String listResourcesInfo = "res -l";
	static String listAllInfo = "res -all";
	static String listAllRoles = "role -all";
	static String getRole = "role -get -rid 142971216";
	static String getUserRoles = "role -get -uid 397425386";
	static String assignRole = "role -asg -to 761204906 -rl user";
	static String authzCommand = "authz -from 761204906 -to 761204906 -d -f /home/aghafoor/auth.json";

	static String fetchAuthzBlockCommand = "lc -uid 43293293";

	static String addRole = "role -add";

	/*> auth -n exp -p b;*5L;3ra5-;
	> user -get -n exp*/

	public static void main(String[] args) {

		if (args != null && args.length >= 1) {
			new Ipvcli().loadPropertyFile(args[0]);
		}
		Ipvcli ipvcli = new Ipvcli(new Configuration());
		try {
			ipvcli.quitCommand();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("For help enter help");
		while (true) {
			String command = getInput("> ");
			try {
				if (!(command == null || command.equalsIgnoreCase("")))
					ipvcli.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public static String getInput(String message) {
		Scanner reader = new Scanner(System.in);
		System.out.print(message);
		return reader.nextLine();
	}

	// role -as -to person -be 1121211

	private CommandLine generateCommandLine(final Options options, final String[] commandLineArguments)
			throws Exception {
		final CommandLineParser cmdLineParser = new DefaultParser();
		CommandLine commandLine = null;
		try {
			commandLine = cmdLineParser.parse(options, commandLineArguments);
		} catch (ParseException parseException) {
			throw new Exception("ERROR: Unable to parse command-line arguments " + Arrays.toString(commandLineArguments)
					+ " due to: " + parseException);
		}
		return commandLine;
	}

	public void execute(String command) throws Exception {
		String args[] = command.split(" ");
		CommandLine commandLine = generateCommandLine(generateOptions(), args);
		List<String> cmds = commandLine.getArgList();
		if (cmds.size() > 0) {
			if (cmds.get(0).equals(AUTH)) {
				authCommands(commandLine);
				return;
			}
			if (cmds.get(0).equals(QUIT)) {
				quitCommand();
				return;
			} else if (cmds.get(0).equals(AUTHZ)) {
				authorizeCmd(commandLine);
				return;
			} else if (cmds.get(0).equals(CERT)) {
				certCmd(commandLine);
				return;
			} else if (cmds.get(0).equals(LC_GET_AUTHZ_OPTION_SHORT)) {
				lcCmd(commandLine);
				return;
			} else if (cmds.get(0).equals(USER)) {
				userCommands(commandLine);
			} else if (cmds.get(0).equals(RES)) {
				resourceCommands(commandLine);
			} else if (cmds.get(0).equals(ROLE)) {
				roleCommands(commandLine);
			} else if (cmds.get(0).equals(HELP)) {
				printHelp();
			} else if (cmds.get(0).equals(EXIT)) {
				System.out.println("Closing CLI !");
				System.out.println("---------------------------Done---------------------------");
				System.exit(1);
			} else {
				System.out.println("Invalid command !");
			}

		}
	}

	private void quitCommand() throws Exception {

		File dir = new File("accessveidblock");
		if (!dir.exists()) {
			return;
		}
		for (File file : dir.listFiles())
			if (!file.isDirectory()) {
				System.out.println(file.getAbsolutePath());
				file.delete();
			}
		authenticator = null;
	}

	private void authCommands(CommandLine commandLine) throws Exception {
		Option oo[] = commandLine.getOptions();
		if (commandLine.hasOption(NAME_OPTION_LONG)) {
			String name = commandLine.getOptionValue(NAME_OPTION_LONG);
			if (commandLine.hasOption(PASSWORD_OPTION_LONG)) {
				String password = commandLine.getOptionValue(PASSWORD_OPTION_LONG);
				try {

					authenticator = ClientAuthenticator.builder().uniqueIdentifier(name).password(password)
							.verifier(verifier).build();

					if (authenticator.autheticate()) {
						System.out.println("User successfully autheticated !");
						// System.out.println("Access Token Path: \n\t" +
						// authenticator.getTokenPath());
						return;
					} else {
						System.out.println("Error is in authentication ");
						return;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				throw new Exception("Invalid command, command: " + authCmd);
			}
		} else {
			throw new Exception("Invalid command, command: " + authCmd);
		}
		System.err.println("Invalid command !");
	}

	private void userCommands(CommandLine commandLine) throws Exception {
		Option oo[] = commandLine.getOptions();

		if (commandLine.hasOption(ADD_OPTION_LONG)) {

			ResourceCO resourceCO = ResourceRegistration.registerUser(authenticator);
			new VeidblockView().displayResource(resourceCO);
			return;
		}

		if (commandLine.hasOption(LIST_OPTION_LONG)) {
			if (Objects.isNull(authenticator)) {
				System.err.println("Please autheticate first and then execute other commands !");
				return;
			}
			ResourceCOList resourceCOList = ListResourcesCommands.listUserResources(restClient, verifier,
					authenticator);
			new VeidblockView().displayResourceList(resourceCOList);
			return;
		}

		if (commandLine.hasOption(GET_OPTION_LONG)) {
			if (Objects.isNull(authenticator)) {
				System.err.println("Please autheticate first and then execute other commands !");
				return;
			}
			if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uid = commandLine.getOptionValue(UID_OPTION_LONG);
				ResourceCO resourceCO = GetResourcesCommands.getUser(uid, authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;
			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {
				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = GetResourcesCommands.getUser(name, authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}

		if (commandLine.hasOption(PUT_OPTION_LONG)) {
			if (Objects.isNull(authenticator)) {
				System.err.println("Please autheticate first and then execute other commands !");
				return;
			}
			if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uidStr = commandLine.getOptionValue(UID_OPTION_LONG);
				try {
					long uid = Long.parseLong(uidStr);
					ResourceCO resourceCO = UpdateResourcesCommands.updateUserById(restClient, verifier, uid,
							authenticator);
					new VeidblockView().displayResource(resourceCO);
					return;
				} catch (Exception exp) {
					throw new Exception("Invalid id !");
				}

			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {
				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = UpdateResourcesCommands.updateUserByUsername(restClient, verifier, name,
						authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(DEL_OPTION_LONG)) {

			if (Objects.isNull(authenticator)) {
				System.err.println("Please autheticate first and then execute other commands !");
				return;
			}

			if (commandLine.hasOption(UID_OPTION_LONG)) {

				String uidStr = commandLine.getOptionValue(UID_OPTION_LONG);
				try {

					long uid = Long.parseLong(uidStr);
					ResourceCO resourceCO = DelResourcesCommands.deleteUserById(restClient, verifier, uid,
							authenticator);
					new VeidblockView().displayResource(resourceCO);
					return;
				} catch (Exception exp) {

					throw new Exception("Invalid id !");
				}

			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {

				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = DelResourcesCommands.deleteUserByUsername(restClient, verifier, name,
						authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;
			} else {

				throw new Exception("Invalid command !");
			}
		}
		System.err.println("Invalid command !");
	}

	private void resourceCommands(CommandLine commandLine) throws Exception {
		if (Objects.isNull(authenticator)) {
			System.err.println("Please autheticate first and then execute other commands !");
			return;
		}
		Option oo[] = commandLine.getOptions();

		if (commandLine.hasOption(ADD_OPTION_LONG)) {
			ResourceRegistration.registerResource(authenticator);
			return;
		}
		if (commandLine.hasOption(LIST_OPTION_LONG)) {

			ResourceCOList resourceCOList = ListResourcesCommands.listServerResources(restClient, verifier,
					authenticator);
			new VeidblockView().displayResourceList(resourceCOList);
			return;
		}
		if (commandLine.hasOption(LIST_ALL_OPTION_LONG)) {

			ResourceCOList resourceCOList = ListResourcesCommands.listResources(restClient, verifier, authenticator);
			new VeidblockView().displayResourceList(resourceCOList);
			return;
		}

		if (commandLine.hasOption(GET_OPTION_LONG)) {
			if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uid = commandLine.getOptionValue(UID_OPTION_LONG);

				ResourceCO resourceCO = GetResourcesCommands.getResource(uid, authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;

			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {

				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = GetResourcesCommands.getResource(name, authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;

			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(PUT_OPTION_LONG)) {
			if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uidStr = commandLine.getOptionValue(UID_OPTION_LONG);
				try {
					long uid = Long.parseLong(uidStr);
					ResourceCO resourceCO = UpdateResourcesCommands.updateResourceById(restClient, verifier, uid,
							authenticator);
					new VeidblockView().displayResource(resourceCO);
					return;

				} catch (Exception exp) {
					throw new Exception("Invalid id !");
				}
			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {
				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = UpdateResourcesCommands.updateResourceByName(restClient, verifier, name,
						authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;

			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(DEL_OPTION_LONG)) {
			if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uidStr = commandLine.getOptionValue(UID_OPTION_LONG);
				try {
					long uid = Long.parseLong(uidStr);
					ResourceCO resourceCO = DelResourcesCommands.deleteResourceById(restClient, verifier, uid,
							authenticator);
					new VeidblockView().displayResource(resourceCO);
					return;
				} catch (Exception exp) {
					throw new Exception("Invalid id !");
				}

			} else if (commandLine.hasOption(NAME_OPTION_LONG)) {
				String name = commandLine.getOptionValue(NAME_OPTION_LONG);
				ResourceCO resourceCO = DelResourcesCommands.deleteResourceByName(restClient, verifier, name,
						authenticator);
				new VeidblockView().displayResource(resourceCO);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		} else {
			throw new Exception("Invalid command !");
		}

	}

	private void roleCommands(CommandLine commandLine) throws Exception {
		if (Objects.isNull(authenticator)) {
			System.err.println("Please autheticate first and then execute other commands !");
			return;
		}

		Option oo[] = commandLine.getOptions();
		if (commandLine.hasOption(ADD_OPTION_LONG)) {
			RoleCO roleCo = RoleCommands.addRole(restClient, verifier, authenticator);
			new VeidblockView().displayRoles("Following role successfully added !", roleCo);
			return;
		}

		if (commandLine.hasOption(LIST_ALL_OPTION_LONG)) {

			RoleCOList roleCOList = RoleCommands.listRoles(restClient, verifier, authenticator);
			new VeidblockView().displayRoleList(roleCOList);
			return;
		}

		if (commandLine.hasOption(GET_OPTION_LONG)) {
			if (commandLine.hasOption(RID_OPTION_LONG)) {
				String rid = commandLine.getOptionValue(RID_OPTION_LONG);
				RoleCO roleCo = RoleCommands.getRole(restClient, verifier, rid, authenticator);
				new VeidblockView().displayRoles("", roleCo);
				return;
			} else if (commandLine.hasOption(UID_OPTION_LONG)) {
				String uid = commandLine.getOptionValue(UID_OPTION_LONG);
				RoleCOList roleCOList = RoleCommands.getRolesAssignedToResource(restClient, verifier, uid,
						authenticator);
				new VeidblockView().displayRoleList(roleCOList);

				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(DEL_OPTION_LONG)) {
			if (commandLine.hasOption(RID_OPTION_LONG)) {
				String rid = commandLine.getOptionValue(RID_OPTION_LONG);
				RoleCO roleCo = RoleCommands.delRole(restClient, verifier, rid, authenticator);
				roleCo.setDescription("Not Specified");
				roleCo.setRole("Not Specified");
				roleCo.setCreatedBy("Not Specified");
				new VeidblockView().displayRoles("Following role successfully deleted !", roleCo);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(ASSIGN_OPTION_LONG)) {
			if (commandLine.hasOption(TO_OPTION_LONG) && commandLine.hasOption(RL_OPTION_LONG)) {
				String to = commandLine.getOptionValue(TO_OPTION_LONG);
				String rl = commandLine.getOptionValue(RL_OPTION_LONG);
				RoleCO roleCo = RoleCommands.assignRole(restClient, verifier, to, rl, authenticator);
				new VeidblockView().displayRoles("Following role successfully assigned !", roleCo);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}
		if (commandLine.hasOption(RM_OPTION_LONG)) {
			if (commandLine.hasOption(FROM_OPTION_LONG) && commandLine.hasOption(RL_OPTION_LONG)) {
				String from = commandLine.getOptionValue(FROM_OPTION_LONG);
				String rl = commandLine.getOptionValue(RL_OPTION_LONG);
				RoleCO roleCo = RoleCommands.deleteRole(restClient, verifier, from, rl, authenticator);
				new VeidblockView().displayRoles("Following role successfully delelted from user's role list !",
						roleCo);
				return;
			} else {
				throw new Exception("Invalid command !");
			}
		}
		System.err.println("Invalid command !");
	}

	// authz -from 761204906 -to 761204906 -d -f /home/aghafoor/auth.json
	public void authorizeCmd(CommandLine commandLine) throws Exception {
		if (Objects.isNull(authenticator)) {
			System.err.println("Please autheticate first and then execute other commands !");
			return;
		}
		Option oo[] = commandLine.getOptions();

		if (commandLine.hasOption(TO_OPTION_LONG)) {
			String to = commandLine.getOptionValue(TO_OPTION_LONG);
			if (commandLine.hasOption(FROM_OPTION_LONG)) {
				String from = commandLine.getOptionValue(FROM_OPTION_LONG);
				AuthorizeCommand.authorizeResource(restClientVerifier, from, to, verifier, authenticator);
				return;
			} else {
				throw new Exception("Invalid command !");
			}

		} else {
			throw new Exception("Invalid command !");
		}

	}

	// lc -uid 761204906
	public void lcCmd(CommandLine commandLine) throws Exception {
		if (Objects.isNull(authenticator)) {
			System.err.println("Please autheticate first and then execute other commands !");
			return;
		}
		Option oo[] = commandLine.getOptions();

		if (commandLine.hasOption(UID_OPTION_LONG)) {
			String uid = commandLine.getOptionValue(UID_OPTION_LONG);
			LCCommands.extractAuthorizedBlocks(restClientVerifier, verifier, uid, authenticator);
			return;
		} else {
			throw new Exception("Invalid command !");
		}

	}

	public void certCmd(CommandLine commandLine) throws Exception {
		Option oo[] = commandLine.getOptions();

		if (commandLine.hasOption(UID_OPTION_LONG)) {
			String uid = commandLine.getOptionValue(UID_OPTION_LONG);
			if (commandLine.hasOption(PASSWORD_OPTION_LONG)) {
				String password = commandLine.getOptionValue(PASSWORD_OPTION_LONG);
				if (commandLine.hasOption(URL_OPTION_LONG)) {
					String verifyerURL = commandLine.getOptionValue(URL_OPTION_LONG);
					new CertificateConnector().createCertificate(uid, password, verifyerURL);
					return;
				} else {
					throw new Exception("Invalid command !");
				}
			} else {
				throw new Exception("Invalid command !");
			}

		} else {
			throw new Exception("Invalid command !");
		}
	}
}
