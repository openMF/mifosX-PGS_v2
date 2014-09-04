/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class UsageRecordMetricEnumerations {

    public static EnumOptionData metricType(final Integer metricTypeId) {
        return metricType(UsageRecordMetricType.fromInt(metricTypeId));
    }

    public static EnumOptionData metricType(final UsageRecordMetricType metricType) {
        EnumOptionData optionData = new EnumOptionData(UsageRecordMetricType.KWH.getValue().longValue(),
                UsageRecordMetricType.KWH.getCode(), "KwH");
        switch (metricType) {
            case KWH:
                optionData = new EnumOptionData(UsageRecordMetricType.KWH.getValue().longValue(),
                        UsageRecordMetricType.KWH.getCode(), "KwH");
            break;
            case ELAPSEDTIME:
                optionData = new EnumOptionData(UsageRecordMetricType.ELAPSEDTIME.getValue().longValue(),
                        UsageRecordMetricType.ELAPSEDTIME.getCode(), "ElapsedTime");
            break;
        }

        return optionData;
    }
}