package org.mifosplatform.portfolio.pgs.pgsclient.domain.mifosclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Object used to create a Mifos Client in a MifosX instance. 
*/
public class MifosClient {
	
	private final String MIFOS_INSTANCE_URL = "https://demo.openmf.org/mifosng-provider/api/v1/clients"; // TODO Should this be hardcoded?
	private final String CONTENT_TYPE = "application/json";
	private final String MIFOS_INSTANCE_AUTHORIZATION = "Basic bWlmb3M6cGFzc3dvcmQ=";
	private final String MIFOS_INSTANCE_TENANT_ID = "default";
	
	private final static Logger logger = LoggerFactory.getLogger(MifosClient.class);
	private URL obj;
	private HttpURLConnection connection;
	private Long officeId; // TODO Should this be hardcoded?
    private String firstname;
    private String lastname;
    private boolean isActive;
    private Long savingsProductId;
    private String locale;
    private String dateFormat;
    private String activationDate;
    
    public MifosClient(Long officeId, String firstname, String lastname, boolean isActive,
			Long savingsProductId, String locale, String dateFormat,
			String activationDate) {
		this.officeId = officeId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.isActive = isActive;
		this.savingsProductId = savingsProductId;
		this.locale = locale;
		this.dateFormat = dateFormat;
		this.activationDate = activationDate;
	}
	public String createInMifosInstance() throws IOException{
    	
    	JsonObject mifosClient = new JsonObject();
    	mifosClient.addProperty("officeId", officeId);
    	mifosClient.addProperty("firstname", firstname);
    	mifosClient.addProperty("lastname", lastname);
    	mifosClient.addProperty("active", isActive);
    	mifosClient.addProperty("savingsProductId", savingsProductId);
    	mifosClient.addProperty("activationDate", activationDate);
    	mifosClient.addProperty("locale", locale);
    	mifosClient.addProperty("dateFormat", dateFormat);
    	
    	final String rawData = mifosClient.toString();

		try {
			obj = new URL(MIFOS_INSTANCE_URL);
			connection = (HttpURLConnection)obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty( "Content-Type", CONTENT_TYPE );
			connection.setRequestProperty( "Content-Length", String.valueOf(rawData.length()));
			connection.setRequestProperty( "X-Mifos-Platform-TenantId", MIFOS_INSTANCE_TENANT_ID);
			connection.setRequestProperty( "Authorization", MIFOS_INSTANCE_AUTHORIZATION); // TODO make dynamic
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			os.write(rawData.getBytes());
 
			long responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("\nHttpURLConnection results : ");
			logger.info("Sending 'POST' request to URL : " + MIFOS_INSTANCE_URL);
			logger.info("Raw data: " + rawData);
			logger.info("Response Code : " + responseCode);
			logger.info("Response Message : " + responseMessage);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			logger.info(response.toString());
			
			return response.toString();
			
		} catch (MalformedURLException e) {

			throw new RuntimeException(e);
		}
	}
	
	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public void setSavingsProductId(Long savingsProductId) {
		this.savingsProductId = savingsProductId;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
    	
}
