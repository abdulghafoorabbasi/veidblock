package org.acreo.auth.util;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.acreo.common.entities.ResourceCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.entities.Organization;
import org.acreo.ip.entities.Resource;
import org.acreo.ip.entities.Role;
import org.acreo.security.utils.PEMStream;
import org.acreo.veidblock.JWSToken;
import org.acreo.veidblock.JWSTokenHandler;
import org.acreo.veidblock.token.JWToken;

public class JWSTokenHelper {
	private Resource resource;
	private String scp;
	private JWSToken jWSToken;
	private LocalCertificateManager localCertificateManager;

	public JWSTokenHelper(Resource resource, LocalCertificateManager localCertificateManager, String scp)
			throws VeidblockException {
		this.resource = resource;
		this.scp = scp;
		this.localCertificateManager = localCertificateManager;
		createToken();
	}

	private void createToken() throws VeidblockException {
		PublicKey publicKey = localCertificateManager.fetchCertificate().getPublicKey();
		PEMStream pemStream = new PEMStream();
		String encodedPublicKey = pemStream.toBase64String(publicKey);

		JWSTokenHandler handler = new JWSTokenHandler(localCertificateManager.fetchCertificate(),
				localCertificateManager.getAuthServerPrivateKey());
		JWToken jwToken = handler.generateToken(resource.getResourceId() + "",
				localCertificateManager.getVeidblockConfig().getVerifier(),
				localCertificateManager.getVeidblockConfig().getValidator(), encodedPublicKey, scp);
		try {
			jWSToken = new JWSToken(jwToken);
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
	}

	public JWSToken getJWSToken() {
		return jWSToken;
	}

	public ResourceCO toResourceDTO(Resource resource) {
		ResourceCO dto = new ResourceCO();

		dto.setOrganizationId(resource.getOrganizationId());

		dto.setAddress1(resource.getAddress1());
		dto.setAddress2(resource.getAddress2());
		dto.setPostCode(resource.getPostCode());
		dto.setLocation(resource.getLocation());

		dto.setCity(resource.getCity());
		dto.setState(resource.getState());
		dto.setCountry(resource.getCountry());

		dto.setResourceId(resource.getResourceId());
		dto.setName(resource.getName());
		dto.setEmail(resource.getEmail());

		dto.setUsername(resource.getUsername());
		dto.setPassword(resource.getPassword());

		dto.setCreationDate(resource.getCreationDate());
		dto.setOrganizationId(resource.getOrganizationId());

		List<Role> roles = resource.getRoles();
		List<String> copyRoles = new ArrayList<String>();
		if (roles != null) {
			for (Role role : roles) {
				copyRoles.add(role.getRole());
			}
		} else {
			dto.setRoles(null);
		}
		dto.setMobile(resource.getMobile());
		dto.setPhone(resource.getPhone());
		dto.setBackupEmail(resource.getBackupEmail());
		dto.setActiveStatus(resource.isActiveStatus());
		dto.setUrl(resource.getUrl());

		return dto;
	}

}
