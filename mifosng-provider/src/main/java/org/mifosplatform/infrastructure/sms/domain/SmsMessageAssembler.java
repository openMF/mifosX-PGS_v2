/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.sms.SmsApiConstants;
import org.mifosplatform.infrastructure.sms.exception.SmsNotFoundException;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class SmsMessageAssembler {

    private final SmsMessageRepository smsMessageRepository;
    private final PGSClientRepositoryWrapper pgsClientRepositoryWrapper;
    private final StaffRepositoryWrapper staffRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SmsMessageAssembler(final SmsMessageRepository smsMessageRepository, final PGSClientRepositoryWrapper pgsClientRepository, 
    		final StaffRepositoryWrapper staffRepository, final FromJsonHelper fromApiJsonHelper) {
        this.smsMessageRepository = smsMessageRepository;
        this.pgsClientRepositoryWrapper = pgsClientRepository;
        this.staffRepository = staffRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public SmsMessage assembleFromJson(final JsonCommand command) {

        final JsonElement element = command.parsedJson();

        String mobileNo = null;

        PGSClient pgsClient = null;
        if (this.fromApiJsonHelper.parameterExists(SmsApiConstants.clientIdParamName, element)) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(SmsApiConstants.clientIdParamName, element);
            pgsClient = this.pgsClientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            mobileNo = pgsClient.mobileNo();
        }

        Staff staff = null;
        if (this.fromApiJsonHelper.parameterExists(SmsApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(SmsApiConstants.staffIdParamName, element);
            staff = this.staffRepository.findOneWithNotFoundDetection(staffId);
            mobileNo = staff.mobileNo();
        }

        final Long gatewayId = this.fromApiJsonHelper.extractLongNamed(SmsApiConstants.gatewayIdParamName, element);
        
        final String message = this.fromApiJsonHelper.extractStringNamed(SmsApiConstants.messageParamName, element);

        return SmsMessage.pendingSms(pgsClient, staff, message, mobileNo, gatewayId);
    }

    public SmsMessage assembleFromResourceId(final Long resourceId) {
        final SmsMessage sms = this.smsMessageRepository.findOne(resourceId);
        if (sms == null) { throw new SmsNotFoundException(resourceId); }
        return sms;
    }
}