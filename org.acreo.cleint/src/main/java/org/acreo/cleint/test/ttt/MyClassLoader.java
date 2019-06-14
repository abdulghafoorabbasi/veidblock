package org.acreo.cleint.test.ttt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.acreo.common.entities.lc.SmartContract;

public class MyClassLoader extends ClassLoader {

	private byte classData [];
	public MyClassLoader(ClassLoader parent,byte classData []) {
		super(parent);
		this.classData =classData;
	}

	public void writeFile(String filePath , byte [] data){
		Path path = Paths.get(filePath);
		try {
			Files.write(path, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public byte[]readFile(String filePath){
		Path path = Paths.get(filePath);
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Class loadClass(String name) throws ClassNotFoundException {
		if (!"org.acreo.proposal.launch.ProposalSmartContract".equals(name))
			return super.loadClass(name);

		try {
			//String url = "file:/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/target/classes/org/acreo/cleint/test/ttt/MyObject.class";
/*			String url = "file:/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.proposal.launch/target/classes/org/acreo/proposal/launch/ProposalSmartContract.class";
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();

			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}

			input.close();

			byte[] classData = buffer.toByteArray();
			
			writeFile("/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/target/abc.txt",classData);
			classData = readFile("/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/target/abc.txt");*/
			//JOptionPane.showMessageDialog(null, "============================");
			return defineClass("org.acreo.proposal.launch.ProposalSmartContract", classData, 0, classData.length);

		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {

		byte[] classData = null;
		String filePath = "/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/target/abc.txt";
		//writeFile("/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/target/abc.txt",classData);
		Path path = Paths.get(filePath);
		try {
			classData = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
		MyClassLoader classLoader = new MyClassLoader(parentClassLoader,classData);
		
		Class myObjectClass = classLoader.loadClass("org.acreo.proposal.launch.ProposalSmartContract");
		SmartContract object1 = (SmartContract) myObjectClass.newInstance();
		object1.init(null);
		

	}

}