package org.acreo.cleint.resources;

import org.acreo.cleint.RestClient;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.exceptions.VeidblockException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IPVCmdClient {
	
	/*@GET
	@Timed
	//@UnitOfWork
	@RolesAllowed({ "" })
	public ResourceCOList getResources() {
		System.out.println(headers.getHeaderString("RID"));
		IpvUtils ipvUtils = new IpvUtils();
		ResourceCOList resourceDTOs = ipvUtils.resourceToResourceDTO(ipvDelegate.getResources());
		
		addAuthorizationHeader();
		return resourceDTOs;
	}*/

	RestClient restClient = null;
	public IPVCmdClient (){
	try {
		restClient = RestClient.builder().baseUrl("http://localhost:8000").build();
	} catch (VeidblockException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public void getResources() {
		ResourceCO co = new ResourceCO();
		try {
			String targetResource = "/resource"; 
			Representation response = restClient.get(targetResource,  null);
			try {
				ResourceCOList list =  new ObjectMapper().readValue(response.getBody().toString(), ResourceCOList.class);
				System.out.println("Number of Registered Resources : "+list.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (VeidblockException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	
	
	
	// =========================== Resource As A User ==========================
	/*@GET
	@Timed
	@Path("user")
	public ResourceCOList getUserResources() {
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.resourceToResourceDTO(ipvDelegate.getResources(0));
	}*/

	/*@POST
	@Timed
	@Path("user")
	public ResourceCO createUserResource(@NotNull @Valid final ResourceCO resourceDto) {
		IpvUtils ipvUtils = new IpvUtils();
		Resource resource = ipvUtils.toResource(resourceDto);
		resource.setRtype("0");
		IpUtils ipUtils = new IpUtils();
		resource.setResourceId(ipUtils.generateId());
		resource.setCreatedBy(resource.getResourceId());
		resource.setCreationDate(new Date());
		return ipvUtils.toResourceDTO(ipvDelegate.createResource(resource));
	}*/

	/*@GET
	@Timed
	@Path("user/id/{identity}")
	public ResourceCO getUserResource(@PathParam("identity") final String identity) {

		Resource resource = ipvDelegate.getResource(identity, 0);
		if (Objects.isNull(resource)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);
	}*/

	/*@GET
	@Timed
	@Path("user/resname/{identity}")
	public ResourceCO getUserResource(@PathParam("identity") final Long identity) {

		Resource resource = ipvDelegate.getResource(identity, 0);
		if (Objects.isNull(resource)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);
	}*/

	/*@PUT
	@Timed
	@Path("user/id{identity}")
	public ResourceCO editUserResource(@NotNull @Valid final Resource resource,
			@PathParam("identity") final Long identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 0);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		resource.setRtype("0");

		Resource resourceUpdated = ipvDelegate.editResource(resource, resourceRet.getUsername());
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceUpdated);
	}*/

	/*@PUT
	@Timed
	@Path("user/resname/{identity}")
	public ResourceCO editUserResource(@NotNull @Valid final Resource resource,
			@PathParam("identity") final String username) {
		Resource resourceRet = ipvDelegate.getResource(username, 0);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", username), Status.NOT_FOUND);
		}
		resource.setRtype("0");

		Resource resourceUpdated = ipvDelegate.editResource(resource, username);
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceUpdated);
	}*/

	/*@DELETE
	@Timed
	@Path("user/resname/{identity}")
	public ResourceCO deleteUserResource(@PathParam("identity") final Long identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 0);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		ipvDelegate.deleteResource(resourceRet.getUsername());

		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceRet);
	}*/

	/*@DELETE
	@Timed
	@Path("user/id/{identity}")
	public ResourceCO deleteUserResource(@PathParam("identity") final String identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 0);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		ipvDelegate.deleteResource(resourceRet.getUsername());
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceRet);
	}*/

	// =========================== Resource As A Service
	// ==========================
	/*@GET
	@Timed
	@Path("server")
	public ResourceCOList getServerResources() {
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.resourceToResourceDTO(ipvDelegate.getResources(1));
	}*/

	/*@POST
	@Timed
	@Path("server")
	public ResourceCO createServerResource(@NotNull @Valid final ResourceCO resourceDto) {
		IpvUtils ipvUtils = new IpvUtils();
		Resource resource = ipvUtils.toResource(resourceDto);
		resource.setRtype("1");
		IpUtils ipUtils = new IpUtils();
		resource.setResourceId(ipUtils.generateId());
		resource.setCreatedBy(resource.getResourceId());
		resource.setCreationDate(new Date());
		return ipvUtils.toResourceDTO(ipvDelegate.createResource(resource));
	}*/

	/*@GET
	@Timed
	@Path("server/id/{identity}")
	public ResourceCO getServerResource(@PathParam("identity") final String identity) {

		Resource resource = ipvDelegate.getResource(identity, 1);
		if (Objects.isNull(resource)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);

	}*/

	/*@GET
	@Timed
	@Path("server/resname/{identity}")
	public ResourceCO getServerResource(@PathParam("identity") final Long identity) {

		Resource resource = ipvDelegate.getResource(identity, 1);
		if (Objects.isNull(resource)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);
	}*/

	/*@PUT
	@Timed
	@Path("server/id{identity}")
	public ResourceCO editServerResource(@NotNull @Valid final Resource resource,
			@PathParam("identity") final Long identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 1);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		resource.setRtype("1");
		ipvDelegate.editResource(resource, resourceRet.getUsername());

		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);
	}*/

	/*@PUT
	@Timed
	@Path("server/resname/{identity}")
	public ResourceCO editServerResource(@NotNull @Valid final Resource resource,
			@PathParam("identity") final String username) {
		Resource resourceRet = ipvDelegate.getResource(username, 1);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", username), Status.NOT_FOUND);
		}
		resource.setRtype("1");
		ipvDelegate.editResource(resource, username);

		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resource);
	}*/

	/*@DELETE
	@Timed
	@Path("server/resname/{identity}")
	public ResourceCO deleteServerResource(@PathParam("identity") final Long identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 1);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		ipvDelegate.deleteResource(resourceRet.getUsername());

		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceRet);
	}*/

	/*@DELETE
	@Timed
	@Path("server/id/{identity}")
	public ResourceCO deleteServerResource(@PathParam("identity") final String identity) {
		Resource resourceRet = ipvDelegate.getResource(identity, 1);
		if (Objects.isNull(resourceRet)) {
			throw new WebApplicationException(String.format("Resource '%s' not found !", identity), Status.NOT_FOUND);
		}
		ipvDelegate.deleteResource(resourceRet.getUsername());

		IpvUtils ipvUtils = new IpvUtils();
		return ipvUtils.toResourceDTO(resourceRet);
	}*/
	
	public static void main(String arg[]){
		
	}
}
