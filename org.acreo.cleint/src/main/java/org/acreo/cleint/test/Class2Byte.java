package org.acreo.cleint.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Class2Byte {
	public static void main(String args[]) throws Exception {

		Calendar mydate = new GregorianCalendar();
		String string = "2018-04-04 02:43:34";

		
		
		//System.out.println(new Class2Byte().checkExpiryDate(string));
		
		// Date ss =new Date("2018-04-04 02:43:34");

		/*
		 * System.out.println(ClassLoaderInput.class.getProtectionDomain().
		 * getCodeSource().getLocation().getPath());
		 * System.out.println(ClassLoaderInput.class.getCanonicalName());
		 * 
		 * String classFilePath = ClassLoaderInput.class.getCanonicalName();
		 * classFilePath = classFilePath .replace(".", File.separator);
		 * 
		 * String path =
		 * ClassLoaderInput.class.getProtectionDomain().getCodeSource().
		 * getLocation().getPath() + File.separator + classFilePath+".class";
		 * 
		 * final File fileName = new File(path);
		 * 
		 * int _offset=0; int _read=0;
		 * 
		 * InputStream fileInputStream = new FileInputStream(fileName);
		 * FileOutputStream fileOutputStream = new FileOutputStream(
		 * "/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.cleint/src/main/java/org/acreo/cleint/test/ClassLoaderInput.txt"
		 * ); PrintStream printStream = new PrintStream(fileOutputStream);
		 * StringBuffer bytesStringBuffer = new StringBuffer();
		 * 
		 * byte[] byteArray = new byte[(int)fileName.length()]; while (_offset <
		 * byteArray.length && (_read=fileInputStream.read(byteArray, _offset,
		 * byteArray.length-_offset)) >= 0) _offset += _read;
		 * 
		 * fileInputStream.close(); for (int index = 0; index <
		 * byteArray.length; index++)
		 * bytesStringBuffer.append(byteArray[index]+",");
		 * 
		 * printStream.print(bytesStringBuffer.length()==0 ? "" :
		 * bytesStringBuffer.substring(0, bytesStringBuffer.length()-1));
		 */
	}

	

}
