package org.mifosplatform.portfolio.pgs.transactionrecord.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceoffering.api.ServiceOfferingApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_transaction_record")
public class TransactionRecord extends AbstractPersistable<Long>{

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_account_id")
	private ServiceAccount serviceAccount;
	
	@Column(name = "description", nullable = true)
	private String description;
	
	@Column(name = "date", unique = true, nullable = false)
	private Date date;
	
	@Column(name = "type_enum", nullable = false)
	private Integer type;

	@Column(name = "amount", unique = true, nullable = false)
	private double amount;
	
	protected TransactionRecord() {
		//
	}

	public static TransactionRecord createNew(final ServiceAccount serviceAccount, final String description, final Date date,
		final Integer type, final double amount){
		return new TransactionRecord(serviceAccount, description, date, type, amount);
	}
	
	private TransactionRecord(final ServiceAccount serviceAccount, final String description, final Date date,
			final Integer type, final double amount) {
		this.serviceAccount = serviceAccount;
		this.description = description;
		this.date = date;
		this.type = type;
		this.amount = amount;
	}
	
	   public Map<String, Object> update(final JsonCommand command) {

	        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

	        
	        if (command.isChangeInStringParameterNamed(ServiceOfferingApiConstants.descriptionParamName, this.description)) {
	            final String newValue = command.stringValueOfParameterNamed(ServiceOfferingApiConstants.descriptionParamName);
	            actualChanges.put(ServiceOfferingApiConstants.descriptionParamName, newValue);
	            this.description = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        return actualChanges;
	    }

	public ServiceAccount getServiceAccount() {
		return this.serviceAccount;
	}

	public void setServiceAccount(ServiceAccount serviceAccount) {
		this.serviceAccount = serviceAccount;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
