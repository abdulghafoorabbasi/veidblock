package org.acreo.ledger.transactions.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Enumeration;

import org.acreo.common.Representation;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainUtils {
	private LocalCertificateManager localCertificateManager;
	public ChainUtils(LocalCertificateManager localCertificateManager){
		this.localCertificateManager = localCertificateManager;
	}
	
	public Representation<String> verify(String publicKeyEncoded) {
		try {
			PublicKey publicKey = localCertificateManager.fetchCertificate().getPublicKey();
			String localPublicKey  = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			
			ObjectMapper objectMapper = new ObjectMapper();
			String temp = objectMapper.readValue(publicKeyEncoded, String.class);
			if (!temp.equals(localPublicKey)) {
				return new Representation<String>(HttpStatus.NOT_FOUND_404,"");
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			return new Representation<String>(HttpStatus.INTERNAL_SERVER_ERROR_500, "Internal server error !");
		}
		return new Representation<String>(HttpStatus.OK_200, "Successfully verified Public Key!");
	}
	
	
	
	public InetAddress getLocalHostLANAddress() throws UnknownHostException {
	    try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                        return inetAddr;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	            return candidateAddress;
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        return jdkSuppliedAddress;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}	
}
