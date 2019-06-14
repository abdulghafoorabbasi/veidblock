package org.acreo.cleint.resources;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.entities.RoleCO;
import org.acreo.common.entities.RoleCOList;

import dnl.utils.text.table.TextTable;

public class VeidblockView {

	public void displayResource(ResourceCO resourceCO) {
		
		if(Objects.isNull(resourceCO)){
			return;
		}

		System.out.println("- - - Resource Personal Info - - - ");
		
		System.out.println("\t uid   : " + resourceCO.getResourceId());
		System.out.println("\t Name          : " + resourceCO.getName());
		System.out.println("\t Username      : " + resourceCO.getUsername());
		System.out.println("\t Password      : " + resourceCO.getPassword());
		System.out.println("\t Email         : " + resourceCO.getEmail());
		
		System.out.println("\t Org. ID       : " + resourceCO.getOrganizationId());
		System.out.println("\t Address 1     : " + resourceCO.getAddress1());
		System.out.println("\t Address 2     : " + resourceCO.getAddress2());
		System.out.println("\t Post Code     : " + resourceCO.getPostCode());
		System.out.println("\t Location (x,y): " + resourceCO.getLocation());
		System.out.println("\t City          : " + resourceCO.getCity());
		System.out.println("\t State         : " + resourceCO.getState());
		System.out.println("\t Country       : " + resourceCO.getCountry());
		System.out.println("\t Creation Date : " + resourceCO.getCreationDate());
		List<String> rol = resourceCO.getRoles();
		String roles = "";
		for (String aRole : rol) {
			roles += aRole + "/";
		}
		if (roles.endsWith("/")) {
			roles = roles.substring(0, roles.length() - 1);
		}
		
		System.out.println("\t Role(s)       : " + roles);
		System.out.println("\t Mobile        : " + resourceCO.getMobile());
		System.out.println("\t Phone         : " + resourceCO.getPhone());
		System.out.println("\t Backup Email  : " + resourceCO.getBackupEmail());
		System.out.println("\t URL           : " + resourceCO.getUrl());
		System.out.println("\t Creator       : " + (resourceCO.getCreatedBy().equals("" + resourceCO.getResourceId()) ? "Self"
				: "" + resourceCO.getCreatedBy()));
	}

	public void displayResourceList(ResourceCOList resourceCOList) {
		if(Objects.isNull(resourceCOList)){
			return;
		}
		String columnNames[] = { "No.","T", "Resource Id", "Name", "Email", "Username", "Password", "Creation Date",
				"Org. ID", "Address 1", "Address 2", "Post code", "Location", "City", "State", "Country", "Role(s)", "Mobile", "Phone",
				"Backup Email", "URL", "Owner Id" };
		String data[][] = new String[resourceCOList.size()][columnNames.length];
		int i = 0;
		
		for (ResourceCO resourceCO : resourceCOList) {
			int colNo = 0;
			data[i][colNo] = ""+ (i+1);
			data[i][++colNo] = (resourceCO.isUser()? "* " : "x ");
			data[i][++colNo] = "" + resourceCO.getResourceId();
			data[i][++colNo] = resourceCO.getName();
			data[i][++colNo] = resourceCO.getEmail();
			data[i][++colNo] = resourceCO.getUsername();
			data[i][++colNo] = resourceCO.getPassword();
			data[i][++colNo] = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resourceCO.getCreationDate());
			data[i][++colNo] = ""+resourceCO.getOrganizationId();
			data[i][++colNo] = resourceCO.getAddress1();
			data[i][++colNo] = resourceCO.getAddress2();
			data[i][++colNo] = resourceCO.getPostCode();
			data[i][++colNo] = resourceCO.getLocation();
			data[i][++colNo] = resourceCO.getCity();
			data[i][++colNo] = resourceCO.getState();
			data[i][++colNo] = resourceCO.getCountry();
			List<String> rol = resourceCO.getRoles();
			String roles = "";
			for (String aRole : rol) {
				roles += aRole + "/";
			}
			if (roles.endsWith("/")) {
				roles = roles.substring(0, roles.length() - 1);
			}
			data[i][++colNo] = roles;
			data[i][++colNo] = resourceCO.getMobile();
			data[i][++colNo] = resourceCO.getPhone();
			data[i][++colNo] = resourceCO.getBackupEmail();
			data[i][++colNo] = resourceCO.getUrl();
			data[i][++colNo] = resourceCO.getCreatedBy().equals("" + resourceCO.getResourceId()) ? "Self"
					: "" + resourceCO.getCreatedBy();
			i++;
		}
		TextTable textTable = new TextTable(columnNames, data);
		textTable.setAddRowNumbering(false);
		textTable.setSort(0);
		textTable.printTable();
	}
	
	public void displayRoles(String message,  RoleCO roleCo){
		if(Objects.isNull(roleCo)){
			return;
		}
		System.out.println(message);
		System.out.println("Role Description ");
		System.out.println("\t Role Id      : " + roleCo.getRoleId());
		System.out.println("\t Role         : " + roleCo.getRole());
		System.out.println("\t Description  : " + roleCo.getDescription());
		System.out.println("\t Created By   : " + roleCo.getCreatedBy());
		
	}
	public void displayRoleList(RoleCOList roleCOList) {
		if(Objects.isNull(roleCOList)){
			return;
		}
		String columnNames[] = { "No.","Role Id", "Role", "Decription", "Created By"};
		String data[][] = new String[roleCOList.size()][columnNames.length];
		int i = 0;
		
		for (RoleCO roleCO : roleCOList) {
			
			int colNo = 0;
			data[i][colNo] = ""+ (i+1);
			data[i][++colNo] = ""+roleCO.getRoleId();
			data[i][++colNo] = roleCO.getRole();
			data[i][++colNo] = roleCO.getDescription();
			data[i][++colNo] = roleCO.getCreatedBy();
			i++;
		}
		TextTable textTable = new TextTable(columnNames, data);
		textTable.setAddRowNumbering(false);
		textTable.setSort(0);
		textTable.printTable();
	}
}