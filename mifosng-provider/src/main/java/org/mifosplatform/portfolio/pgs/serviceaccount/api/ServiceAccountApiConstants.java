/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccount.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServiceAccountApiConstants {

    public static final String RESOURCE_NAME = "serviceaccount";

 // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    
    // request parameters
    public static final String idParamName = "id";
    public static final String clientIdParamName = "clientId";
    public static final String currentAccountInformationParamName = "currentAccountInformation";
    public static final String usageRecordsParamName = "usageRecords";
    public static final String transactionRecordsParamName = "transactionRecords";
    public static final String isActiveParamName = "isActive";
    public static final String amountParamName = "amount";
    public static final String statusParamName = "status";
    public static final String serviceAccountStatusHistoriesParamName = "serviceAccountStatusHistories";
    public static final String serviceOfferingParamName = "serviceOffering";

    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(clientIdParamName, 
    		currentAccountInformationParamName, usageRecordsParamName, transactionRecordsParamName, isActiveParamName,
    		amountParamName, statusParamName, serviceAccountStatusHistoriesParamName, serviceOfferingParamName));

    // update parameters
    public static final Set<String> UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(transactionRecordsParamName, 
    	amountParamName, usageRecordsParamName));

}