package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.api.ServiceAccountStatusHistoryApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_service_account_status_history")
public class ServiceAccountStatusHistory extends AbstractPersistable<Long>{
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_account_id")
	private ServiceAccount serviceAccount;

	@Column(name = "date", nullable = true)
	private Date date;

	@Column(name = "change_enum", nullable = false)
	private Integer change;

	protected ServiceAccountStatusHistory() {
		//
	}

	public static ServiceAccountStatusHistory createNew(final ServiceAccount serviceAccount, final Date date, final int change){
		return new ServiceAccountStatusHistory(serviceAccount, date, change);
	}
	
	private ServiceAccountStatusHistory(final ServiceAccount serviceAccount, final Date date, final int change) {
		this.serviceAccount = serviceAccount;
		this.date = date;
		this.change = change;
	}
	
	   public Map<String, Object> update(final JsonCommand command) {

	        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>();
	        
	        if (command.isChangeInDateParameterNamed(ServiceAccountStatusHistoryApiConstants.dateParamName, this.date)) {
	            final Date newValue = command.DateValueOfParameterNamed(ServiceAccountStatusHistoryApiConstants.dateParamName);
	            actualChanges.put(ServiceAccountStatusHistoryApiConstants.dateParamName, newValue);
	            this.date = newValue;
	        }
	        
	        if (command.isChangeInIntegerSansLocaleParameterNamed(ServiceAccountStatusHistoryApiConstants.changeParamName, this.change)) {
	            final Integer newValue = command.integerValueOfParameterNamedSansLocale(ServiceAccountStatusHistoryApiConstants.changeParamName);
	            actualChanges.put(ServiceAccountStatusHistoryApiConstants.changeParamName, newValue);
	            this.change = newValue;
	        }

	        return actualChanges;
	    }

	public ServiceAccount getServiceAccount() {
		return this.serviceAccount;
	}

	public void setServiceAccount(ServiceAccount serviceAccount) {
		this.serviceAccount = serviceAccount;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getChange() {
		return this.change;
	}

	public void setChange(Integer change) {
		this.change = change;
	}
}
