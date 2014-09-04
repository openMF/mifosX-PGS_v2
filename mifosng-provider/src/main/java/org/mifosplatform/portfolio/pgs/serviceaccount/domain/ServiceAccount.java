package org.mifosplatform.portfolio.pgs.serviceaccount.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformation;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecord;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecord;
import org.mifosplatform.portfolio.pgs.serviceaccount.api.ServiceAccountApiConstants;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistory;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOffering;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_service_account")
public class ServiceAccount extends AbstractPersistable<Long>{


	@OneToOne(optional = false)
	@JoinColumn(name = "pgs_client", unique = true, nullable = false)
	private PGSClient pgsClient;
	
	@OneToOne
	@JoinColumn(name = "current_account_information")
	private CurrentAccountInformation currentAccountInformation;
	
	@OneToMany(mappedBy="serviceAccount")
	private List<UsageRecord> usageRecords = new LinkedList<UsageRecord>();
	
	@OneToMany(mappedBy="serviceAccount")
	private List<TransactionRecord> transactionRecords = new ArrayList<TransactionRecord>();
	
	@Column(name = "amount", unique = true, nullable = false)
	private Double amount;
	
	@Column(name = "is_activated", unique = true, nullable = false)
	private boolean isActivated;
	
	@OneToMany(mappedBy="serviceAccount")
	private List<ServiceAccountStatusHistory> serviceAccountStatusHistories = 
		new ArrayList<ServiceAccountStatusHistory>();
	
	@OneToOne
	@JoinColumn(name = "service_offering")
	private ServiceOffering serviceOffering;

	protected ServiceAccount() {
		//
	}

	public static ServiceAccount createNew(final PGSClient pgsClient, final CurrentAccountInformation currentAccountInformation, 
    		final LinkedList<UsageRecord> usageRecords, 
    		final ArrayList<TransactionRecord> transactionRecords, 
    		final ArrayList<ServiceAccountStatusHistory> serviceAccountStatusHistories,
    		final ServiceOffering serviceOffering, 
    		final double amount, 
    		final boolean isActivated){

		return new ServiceAccount(pgsClient, currentAccountInformation, usageRecords, transactionRecords, amount,
				isActivated, serviceAccountStatusHistories, serviceOffering);
	}
	
	public static ServiceAccount createNew(final PGSClient pgsClient, final CurrentAccountInformation currentAccountInformation, 
    		final LinkedList<UsageRecord> usageRecords, final ArrayList<TransactionRecord> transactionRecords, 
    		final ArrayList<ServiceAccountStatusHistory> serviceAccountStatusHistories,
    		final ServiceOffering serviceOffering, final JsonCommand command){
		
		final Double amount = command.doubleValueOfParameterNamed(ServiceAccountApiConstants.amountParamName);
		final boolean isActivated = command.booleanPrimitiveValueOfParameterNamed(ServiceAccountApiConstants.isActiveParamName);

		return new ServiceAccount(pgsClient, currentAccountInformation, usageRecords, transactionRecords, amount,
				isActivated, serviceAccountStatusHistories, serviceOffering);
	}

	private ServiceAccount(final PGSClient pgsClient, final CurrentAccountInformation currentAccountInformation, 
    		final LinkedList<UsageRecord> usageRecords, final ArrayList<TransactionRecord> transactionRecords, 
    		final Double amount, final boolean isActivated, 
    		final ArrayList<ServiceAccountStatusHistory> serviceAccountStatusHistories, final ServiceOffering serviceOffering) {
		this.pgsClient = pgsClient;
		this.currentAccountInformation = currentAccountInformation;
		this.usageRecords = usageRecords;
		this.transactionRecords = transactionRecords;
		this.amount = amount;
		this.isActivated = isActivated;
		this.serviceAccountStatusHistories = serviceAccountStatusHistories;
		this.serviceOffering = serviceOffering;
	}

	public Map<String, Object> update(final JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

		
		if (command.isChangeInDoubleParameterNamed(ServiceAccountApiConstants.amountParamName, this.amount)) {
			final Double newValue = command.doubleValueOfParameterNamed(ServiceAccountApiConstants.amountParamName);
			actualChanges.put(ServiceAccountApiConstants.amountParamName, newValue);
			this.amount = newValue;
		}
		
		if (command.isChangeInBooleanParameterNamed(ServiceAccountApiConstants.isActiveParamName, this.isActivated)) {
			final Boolean newValue = command.booleanObjectValueOfParameterNamed(ServiceAccountApiConstants.isActiveParamName);
			actualChanges.put(ServiceAccountApiConstants.isActiveParamName, newValue);
			this.isActivated = newValue;
		}
		
		return actualChanges;
	}
	
	public PGSClient getPgsClient() {
		return this.pgsClient;
	}

	public void setPgsClient(PGSClient pgsClient) {
		this.pgsClient = pgsClient;
	}

	public CurrentAccountInformation getCurrentAccountInformation() {
		return this.currentAccountInformation;
	}

	public void setCurrentAccountInformation(
			CurrentAccountInformation currentAccountInformation) {
		this.currentAccountInformation = currentAccountInformation;
	}

	public List<UsageRecord> getUsageRecords() {
		return this.usageRecords;
	}

	public void setUsageRecords(LinkedList<UsageRecord> usageRecords) {
		this.usageRecords = usageRecords;
	}

	public List<TransactionRecord> getTransactionRecords() {
		return this.transactionRecords;
	}

	public void setTransactionRecords(
			ArrayList<TransactionRecord> transactionRecords) {
		this.transactionRecords = transactionRecords;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public boolean isActivated() {
		return this.isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public List<ServiceAccountStatusHistory> getServiceAccountStatusHistories() {
		return this.serviceAccountStatusHistories;
	}

	public void setServiceAccountStatusHistories(
			ArrayList<ServiceAccountStatusHistory> serviceAccountStatusHistories) {
		this.serviceAccountStatusHistories = serviceAccountStatusHistories;
	}

	public ServiceOffering getServiceOffering() {
		return this.serviceOffering;
	}

	public void setServiceOffering(ServiceOffering serviceOffering) {
		this.serviceOffering = serviceOffering;
	}
}
