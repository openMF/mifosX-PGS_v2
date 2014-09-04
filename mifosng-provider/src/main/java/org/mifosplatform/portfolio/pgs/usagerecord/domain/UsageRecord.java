package org.mifosplatform.portfolio.pgs.usagerecord.domain;

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
import org.mifosplatform.portfolio.pgs.usagerecord.api.UsageRecordApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_usage_record")
public class UsageRecord extends AbstractPersistable<Long>{

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_account_id")
	private ServiceAccount serviceAccount;

	@Column(name = "date", unique = true, nullable = false)
	private Date date;
	
	@Column(name = "metric_type_enum", nullable = false)
	private Integer metricType;
	
	@Column(name = "number_of_units", nullable = false)
	private double noOfUnits;
	
	//TODO change to DateTime
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	//TODO change to DateTime
	@Column(name = "end_date", nullable = false)
	private Date endDate;

	protected UsageRecord() {
		//
	}

	public static UsageRecord createNew(final ServiceAccount serviceAccount, final Date date, final Integer metricTrype, 
    		final double noOfUnits, final Date startDate, final Date endDate){
		return new UsageRecord(serviceAccount, date, metricTrype, noOfUnits, startDate, endDate);
	}
	
	private UsageRecord(final ServiceAccount serviceAccount, final Date date, final Integer metricType, 
    		final double noOfUnits, final Date startDate, final Date endDate){
		this.serviceAccount = serviceAccount;
		this.date = date;
		this.metricType = metricType;
		this.noOfUnits = noOfUnits;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	   public Map<String, Object> update(final JsonCommand command) {

	        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

	        if (command.isChangeInDoubleParameterNamed(UsageRecordApiConstants.noOfUnitsParamName, this.noOfUnits)) {
	            final double newValue = command.doubleValueOfParameterNamed(UsageRecordApiConstants.noOfUnitsParamName);
	            actualChanges.put(UsageRecordApiConstants.noOfUnitsParamName, newValue);
	            this.noOfUnits = newValue;
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

	public Integer getMetricType() {
		return this.metricType;
	}

	public void setMetricType(Integer metricType) {
		this.metricType = metricType;
	}

	public double getNoOfUnits() {
		return this.noOfUnits;
	}

	public void setNoOfUnits(double noOfUnits) {
		this.noOfUnits = noOfUnits;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
