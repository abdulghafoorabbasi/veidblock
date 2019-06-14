package org.acreo.auth;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.acreo.common.entities.VeidblockConfig;
import org.acreo.common.utils.PeerConnection;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class VeidblockConfiguration extends Configuration {
	@Valid
	@NotNull
	private PeerConnection ipvConnectorConfig = new PeerConnection();

	@JsonProperty("ipv")
	public PeerConnection getIpvConnectorConfig() {
		return ipvConnectorConfig;
	}

	@JsonProperty("ipv")
	public void setIpvConnectorConfig(PeerConnection ipvConnectorConfig) {
		this.ipvConnectorConfig = ipvConnectorConfig;
	}
	private static final String DATABASE = "database";
	private static final String VEIDBLOCK = "veidblock";

	@Valid
	@NotNull
	private DataSourceFactory dataSourceFactory = new DataSourceFactory();
	
	@Valid
	@NotNull
	private VeidblockConfig veidblockConfig = new VeidblockConfig ();

	@JsonProperty(DATABASE)
	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	@JsonProperty(DATABASE)
	public void setDataSourceFactory(final DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}
	
	@JsonProperty(VEIDBLOCK)
	public VeidblockConfig getVeidblockConfig() {
		return veidblockConfig;
	}

	@JsonProperty(VEIDBLOCK)
	public void setVeidblockConfig(final VeidblockConfig veidblockConfig) {
		this.veidblockConfig = veidblockConfig;
	}
	
	@JsonProperty("swagger")
	public SwaggerBundleConfiguration swaggerBundleConfiguration;
}
