package org.acreo.auth.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.acreo.common.entities.Endorsement;
import org.acreo.common.exceptions.VeidblockException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnrollmentIO {

	public boolean saveEndorsement(String endorsementJson) throws VeidblockException {
		File file = new File("enrollment.env");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(endorsementJson.getBytes());
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
		return true;
	}

	public String readEndorsement() throws VeidblockException {
		File file = new File("enrollment.env");
		try {
			FileInputStream fos = new FileInputStream(file);
			byte array[] = new byte[(int) file.length()];
			fos.read(array);
			return new String(array);
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
	}

	public boolean isEndorsementExisits() {
		File file = new File("enrollment.env");
		return file.exists();
	}

	public Endorsement getEndorsement() throws VeidblockException {

		String entStr = readEndorsement();
		ObjectMapper objectMapper = new ObjectMapper();
		try{
			Endorsement endorsement = objectMapper.readValue(entStr, Endorsement.class);
		return endorsement ;
		}catch (Exception exp){
			throw new VeidblockException(exp);
		}
	}
}
