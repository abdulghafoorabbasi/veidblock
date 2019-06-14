package org.acreo.auth.twofactor;

import java.security.cert.X509Certificate;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.crypto.Encryption;
import org.acreo.security.utils.PEMStream;


public class PdcVerifier {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private PairDeviceService pairDeviceService;
	public PdcVerifier(PairDeviceService pairDeviceService){
		this.pairDeviceService= pairDeviceService;
	}
	
	public PairDevice verify(SignedPdc signedPdc) throws VeidblockException {
		PEMStream pemStream = new PEMStream();
		X509Certificate x509Certificate  = null;
		if(signedPdc.getClientCertificate() == null ){
			throw new VeidblockException("Client certificate is null !");
		}
		
		try {
			x509Certificate = pemStream.pem2x509Cert(signedPdc.getClientCertificate());
			
		} catch (Exception e) {
			throw new VeidblockException("Problems when processing client certificate. It should be in PEM format !");
		}
		x509Certificate.getPublicKey();
		Encryption encryption = new Encryption(new CryptoPolicy());
		byte pdc_uid[] = null;
		try {
			pdc_uid = encryption.decrypt(x509Certificate.getPublicKey(), signedPdc.getSignature().getBytes(),ENCODING_DECODING_SCHEME.BASE64);
		} catch (VeidblockException e) {
			throw new VeidblockException("Problems when decrypting signed data (dpc) !");
		}
		if(pdc_uid  == null){
			throw new VeidblockException("Problems when decrypting signed data (dpc) !");
		}
		String strPdc_uid = new String(pdc_uid); 
		if(!strPdc_uid.contains("|")){
			throw new VeidblockException("Invalid received data (dpc|uid) !");
		}
		String tokenz [] = strPdc_uid.split("|");
		String uid= tokenz[0];
		String dpcSeq = tokenz[1];
		tokenz = dpcSeq.split("-");
		String dpc = tokenz[0];
		String seq = tokenz[1];
		// get DPC record
		PairDevice pairDevice = this.pairDeviceService.getPairedDevice(uid, dpc);
		long seqNo = pairDevice.getSeqNo(); 
		if(seqNo+1 != Long.parseLong(seq)){
			throw new VeidblockException("Invalid received packet !");
		}
		if(pairDevice.getDpc().equals(dpc)){
			return pairDevice;
		} else{
			throw new VeidblockException("Verification failed because pair device code is invalid !");
		}		
	}
}
