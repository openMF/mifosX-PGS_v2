/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.pgs.usagerecord.api.UsageRecordApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class UsageRecordDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public UsageRecordDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, UsageRecordApiConstants.CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(UsageRecordApiConstants.RESOURCE_NAME);
        
            
            if (!this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.dateParamName, element)) {
                baseDataValidator.reset().parameter(UsageRecordApiConstants.dateParamName).failWithCode("must.have.date.parameter");
            }
            
            if (!this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.noOfUnitsParamName, element)) {
                baseDataValidator.reset().parameter(UsageRecordApiConstants.noOfUnitsParamName)
                	.failWithCode("must.have.no.of.units.parameter");
            }
            
            if (!this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.metricTypeParamName, element)) {
                baseDataValidator.reset().parameter(UsageRecordApiConstants.metricTypeParamName)
                	.failWithCode("must.have.metric.type.parameter");
            }

            if (!this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.startDateParamName, element)) {
                baseDataValidator.reset().parameter(UsageRecordApiConstants.startDateParamName)
                	.failWithCode("must.have.start.date.parameter");
            }
            

            if (!this.fromApiJsonHelper.parameterExists(UsageRecordApiConstants.endDateParamName, element)) {
                baseDataValidator.reset().parameter(UsageRecordApiConstants.endDateParamName)
                	.failWithCode("must.have.end.date.parameter");
            }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, UsageRecordApiConstants.UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(UsageRecordApiConstants.RESOURCE_NAME);

        //TODO improve data validation

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}