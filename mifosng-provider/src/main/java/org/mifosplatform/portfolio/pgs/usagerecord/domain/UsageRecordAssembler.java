/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.domain;

import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.mifosplatform.portfolio.pgs.serviceoffering.exception.ServiceOfferingNotFoundException;
import org.mifosplatform.portfolio.pgs.usagerecord.api.UsageRecordApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class UsageRecordAssembler {

	private final UsageRecordRepository usageRecordRepository;
	private final ServiceAccountRepository serviceAccountRepository;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public UsageRecordAssembler(final UsageRecordRepository usageRecordRepository,
			final FromJsonHelper fromApiJsonHelper, 
			final ServiceAccountRepository serviceAccountRepository) {
		this.usageRecordRepository = usageRecordRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.serviceAccountRepository = serviceAccountRepository;
	}

	public UsageRecord assembleFromJson(final JsonCommand command) {

		final JsonElement element = command.parsedJson();
		long serviceAccountId = 0;
		LocalDate localdate = null;
		Date date = null;
		double noOfUnits = 0.0;
		Integer metricType = null;
		Date startDate = null;
		Date endDate = null;
		
		if (command.getServiceAccountId() != null) {
			serviceAccountId = command.getServiceAccountId();
		}

		final ServiceAccount serviceAccount = this.serviceAccountRepository.findOne(serviceAccountId);

		if (this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.dateParamName, element)) {
			localdate = this.fromApiJsonHelper.extractLocalDateNamed(UsageRecordApiConstants.dateParamName, element);
			if (localdate != null) {
				date = localdate.toDate();
			} else {
				date = LocalDate.now().toDate();
			}

		}

		if (this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.noOfUnitsParamName, element)) {
			noOfUnits = this.fromApiJsonHelper.extractDoubleNamed(UsageRecordApiConstants.noOfUnitsParamName, element);

		}

		if (this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.metricTypeParamName, element)) {
			metricType = this.fromApiJsonHelper.extractIntegerNamed(UsageRecordApiConstants.metricTypeParamName, element, Locale.getDefault());
		}

		if (this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.startDateParamName, element)) {
			localdate = this.fromApiJsonHelper.extractLocalDateNamed(UsageRecordApiConstants.startDateParamName, element);
			if (localdate != null) {
				startDate = localdate.toDate();
			} else {
				startDate = LocalDate.now().toDate();
			}
		}

		if (this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.endDateParamName, element)) {
			localdate = this.fromApiJsonHelper.extractLocalDateNamed(UsageRecordApiConstants.endDateParamName, element);
			if (localdate != null) {
				endDate = localdate.toDate();
			} else {
				endDate = LocalDate.now().toDate();
			}
		}

		return UsageRecord.createNew(serviceAccount, date, metricType, noOfUnits, startDate, endDate);
	}

	public UsageRecord assembleFromResourceId(final Long resourceId) {
		final UsageRecord usageRecord = this.usageRecordRepository.findOne(resourceId);
		if (usageRecord == null) { throw new ServiceOfferingNotFoundException(resourceId); }
		return usageRecord;
	}
}