package org.mifosplatform.portfolio.pgs.serviceoffering.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceoffering.api.ServiceOfferingApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_service_offering")
public class ServiceOffering extends AbstractPersistable<Long>{

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_account_id")
	private ServiceAccount serviceAccount;
	
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "description", nullable = true)
	private String description;

	//TODO think about how this will work exactly, and consider an enum of {first, second}                                                                                    
	@Column(name = "fee_structure_enum", nullable = false)
	private Integer feeStructure;

	protected ServiceOffering() {
		//
	}

	public static ServiceOffering ServiceOffering(final ServiceAccount serviceAccount, final String name, final String description, final Integer feeStructure){
		return new ServiceOffering(serviceAccount, name, description, feeStructure);
	}
	
	private ServiceOffering(final ServiceAccount serviceAccount, final String name, final String description, final Integer feeStructure) {
		this.serviceAccount = serviceAccount;
		this.name = name;
		this.description = description;
		this.feeStructure = feeStructure;
	}
	
	   public Map<String, Object> update(final JsonCommand command) {

	        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

	        if (command.isChangeInStringParameterNamed(ServiceOfferingApiConstants.nameParamName, this.name)) {
	            final String newValue = command.stringValueOfParameterNamed(ServiceOfferingApiConstants.nameParamName);
	            actualChanges.put(ServiceOfferingApiConstants.nameParamName, newValue);
	            this.name = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        if (command.isChangeInStringParameterNamed(ServiceOfferingApiConstants.descriptionParamName, this.description)) {
	            final String newValue = command.stringValueOfParameterNamed(ServiceOfferingApiConstants.descriptionParamName);
	            actualChanges.put(ServiceOfferingApiConstants.descriptionParamName, newValue);
	            this.description = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        if (command.isChangeInIntegerSansLocaleParameterNamed(ServiceOfferingApiConstants.feeStructureParamName, this.feeStructure)) {
	            final Integer newValue = command.integerValueOfParameterNamedSansLocale(ServiceOfferingApiConstants.feeStructureParamName);
	            actualChanges.put(ServiceOfferingApiConstants.feeStructureParamName, newValue);
	            this.feeStructure = newValue;
	        }

	        return actualChanges;
	    }
}
