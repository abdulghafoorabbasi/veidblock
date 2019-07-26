package org.acreo.ledger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.acreo.common.utils.PeerConnection;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class LedgerConfiguration extends Configuration {
	private static final String DATABASE = "database";

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

	@Valid
	@NotNull
	private DataSourceFactory dataSourceFactory = new DataSourceFactory();

	@JsonProperty(DATABASE)
	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	@JsonProperty(DATABASE)
	public void setDataSourceFactory(final DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}
}