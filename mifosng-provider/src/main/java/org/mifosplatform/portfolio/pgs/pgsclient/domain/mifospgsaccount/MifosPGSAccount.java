package org.mifosplatform.portfolio.pgs.pgsclient.domain.mifospgsaccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.mifosplatform.portfolio.pgs.pgsclient.domain.mifosclient.MifosClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.sun.istack.FinalArrayList;

/**
 * 
 * @author antoniocarella
 * Creates a PGS account in MifosX instance
 */

public class MifosPGSAccount {

	private final String MIFOS_INSTANCE_URL = "https://demo.openmf.org/mifosng-provider/api/v1/savingsaccounts"; // TODO Should this be hardcoded?
	private final String CONTENT_TYPE = "application/json";
	private final String MIFOS_INSTANCE_AUTHORIZATION = "Basic bWlmb3M6cGFzc3dvcmQ=";
	private final String MIFOS_INSTANCE_TENANT_ID = "default";
	private String responseString;
	private String approveResponseString;
	private String activateResponseString;
	
	private final static Logger logger = LoggerFactory.getLogger(MifosClient.class);
	private URL obj;
	private HttpURLConnection connection;
	// TODO Should these be hardcoded?
	// Since (I believe) these parameters are mandated by the savings product, then can't/shouldn't 
	// they just be hardcoded in?
	@Expose
	private long productId = 47;
	@Expose
	private long nominalAnnualInterestRate = 0;
	@Expose
	private boolean withdrawalFeeForTransfers = false;
	@Expose
	private boolean allowOverdraft = false;
	@Expose
	private double overdraftLimit = 4;
	@Expose
	private boolean enforceMinRequiredBalance = false;
	@Expose
	private long interestCompoundingPeriodType = 4;
	@Expose
	private long interestPostingPeriodType = 7;
	@Expose
	private long interestCalculationType = 2;
	@Expose
	private long interestCalculationDaysInYearType = 365;
	@Expose
	private ArrayList<String> charges = null;
	@Expose
	private String monthDayFormat = "dd MMM";
	@Expose
	private String submittedOnDate;
	@Expose
	private String locale;
	@Expose
	private String dateFormat;
	@Expose
	private long clientId;
	
	public MifosPGSAccount(long productId, long nominalAnnualInterestRate,
			boolean withdrawalFeeForTransfers, boolean allowOverdraft,
			double overdraftLimit, boolean enforceMinRequiredBalance,
			long interestCompoundingPeriodType, long interestPostingPeriodType,
			long interestCalculationType,
			long interestCalculationDaysInYearType, String submittedOnDate,
			String locale, String dateFormat, String monthDayFormat,
			ArrayList<String> charges, long clientId) {
		this.productId = productId;
		this.nominalAnnualInterestRate = nominalAnnualInterestRate;
		this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
		this.allowOverdraft = allowOverdraft;
		this.overdraftLimit = overdraftLimit;
		this.enforceMinRequiredBalance = enforceMinRequiredBalance;
		this.interestCompoundingPeriodType = interestCompoundingPeriodType;
		this.interestPostingPeriodType = interestPostingPeriodType;
		this.interestCalculationType = interestCalculationType;
		this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
		this.submittedOnDate = submittedOnDate;
		this.locale = locale;
		this.dateFormat = dateFormat;
		this.monthDayFormat = monthDayFormat;
		this.charges = charges;
		this.clientId = clientId;
	}

	public MifosPGSAccount(final String locale, final String dateFormat, final String submittedOnDate,
			final long clientId){
		this.locale = locale;
		this.dateFormat = dateFormat;
		this.submittedOnDate = submittedOnDate;
		this.clientId = clientId;
	}
	
	public String createinMifosInstance() {
		
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		final String rawData = gson.toJson(this);
		
		//final String rawData = mifosPGSAccount.toString();
		
		try {
			obj = new URL(MIFOS_INSTANCE_URL);
			connection = (HttpURLConnection)obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty( "Content-Type", CONTENT_TYPE );
			//connection.setRequestProperty( "Content-Length", String.valueOf(rawData.length()));
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

			if (responseCode == 200){
				
				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.info(response.toString());
				
				responseString = response.toString();
			} else {			
				responseString = responseMessage;
			}
		} catch (MalformedURLException e) {

			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseString;
	}
	
public String approve(String savingsId) {
	
		//TODO apply DRY principles to this code!
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("locale", locale);
		jsonObj.addProperty("dateFormat", dateFormat);
		jsonObj.addProperty("approvedOnDate", submittedOnDate);
		
		final String rawData = jsonObj.toString();
		
		try {
			obj = new URL(MIFOS_INSTANCE_URL + "/" + savingsId + "?command=approve");
			connection = (HttpURLConnection)obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty( "Content-Type", CONTENT_TYPE );
			//connection.setRequestProperty( "Content-Length", String.valueOf(rawData.length()));
			connection.setRequestProperty( "X-Mifos-Platform-TenantId", MIFOS_INSTANCE_TENANT_ID);
			connection.setRequestProperty( "Authorization", MIFOS_INSTANCE_AUTHORIZATION); // TODO make dynamic
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			os.write(rawData.getBytes());
 
			long responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("\nHttpURLConnection results : ");
			logger.info("Sending 'POST' request to URL : " + MIFOS_INSTANCE_URL + "/" + savingsId + "?command=approve");
			logger.info("Raw data: " + rawData);
			logger.info("Response Code : " + responseCode);
			logger.info("Response Message : " + responseMessage);

			if (responseCode == 200){
				
				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.info(response.toString());
				
				approveResponseString = response.toString();
			} else {			
				approveResponseString = responseMessage;
			}
		} catch (MalformedURLException e) {

			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		responseString = "Approval: " + approveResponseString;
		
		return responseString;
	}
	
	public String activate(String savingsId){
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("locale", locale);
		jsonObj.addProperty("dateFormat", dateFormat);
		jsonObj.addProperty("activatedOnDate", submittedOnDate);
		
		final String rawData = jsonObj.toString();
		
		try {
			obj = new URL(MIFOS_INSTANCE_URL + "/" + savingsId + "?command=activate");
			connection = (HttpURLConnection)obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty( "Content-Type", CONTENT_TYPE );
			//connection.setRequestProperty( "Content-Length", String.valueOf(rawData.length()));
			connection.setRequestProperty( "X-Mifos-Platform-TenantId", MIFOS_INSTANCE_TENANT_ID);
			connection.setRequestProperty( "Authorization", MIFOS_INSTANCE_AUTHORIZATION); // TODO make dynamic
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			os.write(rawData.getBytes());
 
			long responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			logger.info("\nHttpURLConnection results : ");
			logger.info("Sending 'POST' request to URL : " + MIFOS_INSTANCE_URL + "/" + savingsId + "?command=activate");
			logger.info("Raw data: " + rawData);
			logger.info("Response Code : " + responseCode);
			logger.info("Response Message : " + responseMessage);

			if (responseCode == 200){
				
				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.info(response.toString());
				
				activateResponseString = response.toString();
			} else {			
				activateResponseString = responseMessage;
			}
		} catch (MalformedURLException e) {

			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		responseString = "Activation: " + activateResponseString;
		
		return responseString;
		
	}
	
	public void setProductId(long productId) {
		this.productId = productId;
	}

	public void setNominalAnnualInterestRate(long nominalAnnualInterestRate) {
		this.nominalAnnualInterestRate = nominalAnnualInterestRate;
	}

	public void setWithdrawalFeeForTransfers(boolean withdrawalFeeForTransfers) {
		this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
	}

	public void setAllowOverdraft(boolean allowOverdraft) {
		this.allowOverdraft = allowOverdraft;
	}

	public void setOverdraftLimit(double overdraftLimit) {
		this.overdraftLimit = overdraftLimit;
	}

	public void setEnforceMinRequiredBalance(boolean enforceMinRequiredBalance) {
		this.enforceMinRequiredBalance = enforceMinRequiredBalance;
	}

	public void setInterestCompoundingPeriodType(long interestCompoundingPeriodType) {
		this.interestCompoundingPeriodType = interestCompoundingPeriodType;
	}

	public void setInterestPostingPeriodType(long interestPostingPeriodType) {
		this.interestPostingPeriodType = interestPostingPeriodType;
	}

	public void setInterestCalculationType(long interestCalculationType) {
		this.interestCalculationType = interestCalculationType;
	}

	public void setInterestCalculationDaysInYearType(
			long interestCalculationDaysInYearType) {
		this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
	}

	public void setSubmittedOnDate(String submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setMonthDayFormat(String monthDayFormat) {
		this.monthDayFormat = monthDayFormat;
	}

	public void setCharges(ArrayList<String> charges) {
		this.charges = charges;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

}
