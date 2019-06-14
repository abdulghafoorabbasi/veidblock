package org.acreo.cleint.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class SmartContractLoader extends ClassLoader {

	public SmartContractLoader(ClassLoader parent) {
		super(parent);
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		
		try {

			InputStream input = new FileInputStream(new File("/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.proposal.launch/target/classes/org/acreo/proposal/launch/ProposalSmartContract.class"));
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();

			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}

			input.close();

			byte[] classData = buffer.toByteArray();

			return defineClass(null, classData, 0, classData.length);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {

		ClassLoader classLoader = SmartContractLoader.class.getClassLoader();
		SmartContractLoader smartContractLoader = new SmartContractLoader(classLoader);
		Class myObjectClass = classLoader.loadClass("org.acreo.proposal.launch.ProposalSmartContract");

		org.acreo.common.entities.lc.SmartContract object1 = (org.acreo.common.entities.lc.SmartContract)myObjectClass.newInstance();
		object1.start(null);
		

	}

}