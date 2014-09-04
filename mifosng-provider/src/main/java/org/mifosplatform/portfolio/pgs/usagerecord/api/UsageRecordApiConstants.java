/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UsageRecordApiConstants {

    public static final String RESOURCE_NAME = "usagerecord";

 // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    
    // request parameters
    public static final String idParamName = "id";
    public static final String serviceAccountIdParamName = "serviceAccountId";
    public static final String dateParamName = "date";
    public static final String metricTypeParamName = "metricType";
    public static final String noOfUnitsParamName = "numberOfUnits";
    public static final String startDateParamName = "startDate";
    public static final String endDateParamName = "endDate";

    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(serviceAccountIdParamName,
    		dateParamName, metricTypeParamName, noOfUnitsParamName, startDateParamName, endDateParamName, localeParamName,
    		dateFormatParamName));

    // update parameters
    public static final Set<String> UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(noOfUnitsParamName));

}