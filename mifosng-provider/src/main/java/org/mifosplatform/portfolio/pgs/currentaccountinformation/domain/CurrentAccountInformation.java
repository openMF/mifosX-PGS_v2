package org.mifosplatform.portfolio.pgs.currentaccountinformation.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.api.CurrentAccountInformationApiConstants;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_current_account_information")
public class CurrentAccountInformation extends AbstractPersistable<Long> {

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_account_id")
	private ServiceAccount serviceAccount;
	
    @Column(name = "pgs_client_id", nullable = false)
    private Long clientId;
	
	@Column(name = "savings_id", unique = true, nullable = false)
	private Long savingsId;

	@Column(name = "balance", nullable = true)
	private double balance;

	protected CurrentAccountInformation() {
		//
	}

	public static CurrentAccountInformation CurrentAccountInformation(final ServiceAccount serviceAccount, final Long clientId, 
			final Long savingsId, final double balance){
		return new CurrentAccountInformation(serviceAccount, clientId, savingsId, balance);
	}
	
	private CurrentAccountInformation(final ServiceAccount serviceAccount, final Long clientId, final Long savingsId, final double balance) {
		this.serviceAccount = serviceAccount;
		this.clientId = clientId; 
		this.savingsId = savingsId;
		this.balance = balance;
	}
	
	   public Map<String, Object> update(final JsonCommand command) {

	        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

	        if (command.isChangeInDoubleParameterNamed(CurrentAccountInformationApiConstants.balanceParamName, this.balance)) {
	            final double newValue = command.doubleValueOfParameterNamed(CurrentAccountInformationApiConstants.balanceParamName);
	            actualChanges.put(CurrentAccountInformationApiConstants.balanceParamName, newValue);
	            this.balance = newValue;
	        }

	        return actualChanges;
	    }
}
