package org.acreo.auth.main;

import java.util.Date;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.sql.DataSource;

import org.acreo.auth.VeidblockConfiguration;
import org.acreo.auth.resources.CertificateResource;
import org.acreo.auth.resources.PairDeviceResource;
import org.acreo.auth.resources.VerificationResource;
import org.acreo.auth.twofactor.PairDeviceCleaner;
import org.acreo.auth.twofactor.PairDeviceService;
import org.acreo.auth.util.JWTokenHealthCheck;
import org.acreo.auth.util.VerificationHealthCheck;
import org.acreo.auth.veidblock.VeidblockAuth;
import org.acreo.auth.veidblock.VeidblockAuthFilter;
import org.acreo.common.entities.VeidblockConfig;
import org.acreo.common.utils.RestClient;
import org.acreo.init.InitSetup;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.LocalCredentialManager;
import org.acreo.init.SystemInfo;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;
import org.acreo.security.certificate.CertificateSuite;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class AuthApplicaiton extends Application<VeidblockConfiguration> {

	private static final String SQL = "sql";
	final static Logger logger = LoggerFactory.getLogger(AuthApplicaiton.class);

	private static final String VERIFICATION_SERVICE = "Verification service";

	public static void main(String[] args) throws Exception {
		new AuthApplicaiton().run(args);
	}

	@Override
	public void run(VeidblockConfiguration configuration, Environment environment) {

		logger.info("====== Initializing Authentication mService ======");
		enableCORS(environment);

		// ------------------------------------------------------------------------------
		VeidblockConfig veidblockConfig = configuration.getVeidblockConfig();

		String storeName = "vs";

		CertificateSuite certificateSuite = new CertificateSuite(storeName, 5);

		LocalCredentialManager localCredentialManager = new LocalCredentialManager("verification"); 
		// Password used for Certificate Store
		
		LocalCertificateManager localCertificateManager = new LocalCertificateManager(certificateSuite,
				localCredentialManager.getPassword(), veidblockConfig);

		final RestClient restIpvClient = configuration.getIpvConnectorConfig().build(environment);
		InitSetup.build(restIpvClient, localCredentialManager);
		createCredentials(localCertificateManager);

		//This is used to publish certificate in the chain, lets check about identity  
		//Thread th = new CertInChainPublisher(localCertificateManager);
		//th.start();
		// ------------------------------------------------------------------------------

		final DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), SQL);
		/*
		 * try { createDB(configuration.getDataSourceFactory()); } catch
		 * (Exception e1) { e1.printStackTrace(); return; }
		 */
		DBI dbi = new DBI(dataSource);

		VerificationHealthCheck verificationHealthCheck = new VerificationHealthCheck(
				dbi.onDemand(VerificationService.class));
		environment.healthChecks().register(VERIFICATION_SERVICE, verificationHealthCheck);
		JWTokenHealthCheck jwTokenHealthCheck = new JWTokenHealthCheck(dbi.onDemand(JWSTokenService.class));
		environment.healthChecks().register(VERIFICATION_SERVICE, jwTokenHealthCheck);

		VerificationService verificationService = dbi.onDemand(VerificationService.class);
		JWSTokenService jwsTokenService = dbi.onDemand(JWSTokenService.class);
		PairDeviceService pairDeviceService = dbi.onDemand(PairDeviceService.class);
		Thread pairDeviceCleaner = new PairDeviceCleaner(pairDeviceService, 1);
		pairDeviceCleaner.start(); 
		VeidblockAuth authenticator = new VeidblockAuth(localCertificateManager, verificationService, jwsTokenService);
		VeidblockAuthFilter filter = new VeidblockAuthFilter(authenticator);

		// Register filter
		environment.jersey().register(new AuthDynamicFeature(filter));
		String vblaURL = "http://localhost:10000";
		// Register resources
		environment.jersey().register(
				new VerificationResource(vblaURL, localCertificateManager, verificationService, jwsTokenService));
		//environment.jersey().register(new LedgerClientResource(vblaURL, verificationService));
		environment.jersey().register(new CertificateResource(localCertificateManager));
		environment.jersey().register(new PairDeviceResource(localCertificateManager,pairDeviceService));
		
		

		SystemInfo systemInfo = new SystemInfo("Verification", "1.0.0", "RISE Acreo, AB, Kista, Sweden", "",
				new Date());
		InitSetup.displayTag(systemInfo);

	}

	private void enableCORS(Environment environment) {
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	}

	public void createCredentials(LocalCertificateManager localCertificateManager) {
		try {

			logger.info("Extracting Authentication mService registration data from local resource !");

			logger.info("Checking Authentication mService local certificate !");
			if (localCertificateManager.fetchCertificate() == null) {

				logger.info("Authentication mService local certificate does not exist !");

				logger.info("Creating local certificate for Authentication mService !");
				localCertificateManager.generateSelfSignedCert();
				logger.info("Local certificate for Authentication mService successfully created !");

			} else {
				logger.info("Local certificate for Authentication mService already exists !");
			}
		} catch (Exception exception) {
			logger.error("Exception in init process !");
			exception.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void initialize(Bootstrap<VeidblockConfiguration> bootstrap) {
		bootstrap.addBundle(new SwaggerBundle<VeidblockConfiguration>() {
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(VeidblockConfiguration configuration) {
				return configuration.swaggerBundleConfiguration;
			}
		});
	}
}
