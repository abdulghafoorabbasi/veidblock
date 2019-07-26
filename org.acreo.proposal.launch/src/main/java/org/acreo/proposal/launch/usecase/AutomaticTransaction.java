package org.acreo.proposal.launch.usecase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.proposal.launch.citizen.ManageUsers;
import org.acreo.proposal.launch.districtadminA.CitizenDistrictAMain;
import org.acreo.proposal.launch.districtadminA.DistrictAdminAMain;

public class AutomaticTransaction {

	public void doTransactions(String args[]) throws VeidblockException, IOException {

		if (args != null && args.length >= 1) {
			new ManageUsers().loadPropertyFile(args[0]);
		}
		downloadPasswordFile(args);
		ResourceCO admin = new ManageUsers().loadPasswordFile("ipv", args[1]);

		new ManageUsers().createDistrict_A_Admin("" + admin.getResourceId(), admin.getPassword());
		downloadPasswordFile(args);
		ResourceCO daa_resourceCO = new ManageUsers().loadPasswordFile("distrcit_a_admin", args[1]);;
		new DistrictAdminAMain().createProposalChain(daa_resourceCO);
		new DistrictAdminAMain().publishProposal(daa_resourceCO);
				
		CitizenDistrictAMain planningProposalMain = new CitizenDistrictAMain();
		int noc = 100;
		// This method wil lcreat 100 users
		planningProposalMain.usecaseProposalResponse(noc, args[1]);
		downloadPasswordFile(args);
		planningProposalMain.publisCitizenCertificates(noc, args[1]);
		downloadPasswordFile(args);
		planningProposalMain.sendComments(noc, args[1]);
	}

	public void verification(String fileName) throws VeidblockException {
		ResourceCO resourceCO = new ManageUsers().loadPasswordFile("distrcit_a_admin", fileName);
		DistrictAdminAMain districtAdminAMain = new DistrictAdminAMain();
		TransactionHeaderCO transactionHeaderCO = districtAdminAMain.listChaincodesResponse(resourceCO);
		if (!Objects.isNull(transactionHeaderCO)) {
			for (int i = 0; i < 100; i++) {
				TransactionCO transactionCO = districtAdminAMain.listResponses(i, resourceCO, transactionHeaderCO,
						true);
				districtAdminAMain.verifyTransaction(resourceCO, transactionCO);
			}
		}
	}
	
	public static void main(String []args) throws VeidblockException, IOException{
		new AutomaticTransaction().doTransactions(args); 
		new AutomaticTransaction().verification(args[1]);
	}
	
	public void downloadPasswordFile(String args[]){
		if (args != null && args.length >= 1) {
			new ManageUsers().loadPropertyFile(args[0]);
		}
		try {
			RestClient restClient = RestClient.builder().baseUrl(new Configuration().getIPVServerUrl()).build();
			Representation representation = restClient.get("/resource/file/dd", null);
			System.out.println(representation.getBody().toString());
			
			if(new File(args[1]).exists()){
				new File(args[1]).delete();
				new File(args[1]).createNewFile();
			}
			
			Path path = Paths.get(args[1]);
			Files.write(path, representation.getBody().toString().getBytes());
			

		} catch (Exception e) {
			return ;
		}
	}
	
	/*
	 * System.out.println("Sending proposal using threads !"); new Thread() {
	 * public void run() { try { sendComments(noc, fileName); } catch
	 * (VeidblockException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } }.start();
	 */

}
