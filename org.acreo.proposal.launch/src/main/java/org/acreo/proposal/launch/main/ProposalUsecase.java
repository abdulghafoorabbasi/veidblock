package org.acreo.proposal.launch.main;

import java.util.Scanner;

import org.acreo.proposal.launch.citizen.ManageUsers;
import org.acreo.proposal.launch.cityadmin.CityAdminMain;
import org.acreo.proposal.launch.districtadminA.DistrictAdminAMain;
import org.acreo.proposal.launch.districtadminB.DistrictAdminBMain;

public class ProposalUsecase {

	public static void main(String[] args) {
		while (true)
			try {
				System.out.println("**************** Note ******************");
				System.out.println("* First create District Admin A  using *");
				System.out.println("* Manage Citizens and then switch to   *");
				System.out.println("* Manage District Admin A.             *");
				System.out.println("****************************************");
				System.out.println("*********************************");
				System.out.println("*  1. Manage Citizens           *");
				System.out.println("*  2. Manage City Admin         *");
				System.out.println("*  3. Manage District Admin A   *");
				System.out.println("*  4. Manage District Admin B   *");
				System.out.println("*  5. Exit                      *");
				System.out.println("*********************************");
				System.out.print("Please enter option : ");
				Scanner reader = new Scanner(System.in);
				String temp = reader.nextLine();
				int i = Integer.parseInt(temp);
				switch (i) {
				case 1:
					new ManageUsers().manage(args);
					break;
				case 2:
					new CityAdminMain().cityadmin(args);
					break;
				case 3:
					new DistrictAdminAMain().districtAdminA(args);
					break;
				case 4:
					new DistrictAdminBMain().districtAdminB(args);
					break;
				case 5:
					System.exit(0);
					break;
				default:
					System.out.println("Invalid option !");
				}
				System.out.print("\n\n");

			} catch (Exception exp) {
				exp.printStackTrace();
			}

	}

}
