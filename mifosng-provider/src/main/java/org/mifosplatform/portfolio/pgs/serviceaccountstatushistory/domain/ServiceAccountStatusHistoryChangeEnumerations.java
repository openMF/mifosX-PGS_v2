/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class ServiceAccountStatusHistoryChangeEnumerations {

    public static EnumOptionData change(final Integer feeStructureId) {
        return change(ServiceAccountStatusHistoryChangeType.fromInt(feeStructureId));
    }

    public static EnumOptionData change(final ServiceAccountStatusHistoryChangeType feeStructure) {
        EnumOptionData optionData = new EnumOptionData(ServiceAccountStatusHistoryChangeType.ACTIVATION.getValue().longValue(),
                ServiceAccountStatusHistoryChangeType.ACTIVATION.getCode(), "Activation");
        switch (feeStructure) {
            case ACTIVATION:
                optionData = new EnumOptionData(ServiceAccountStatusHistoryChangeType.ACTIVATION.getValue().longValue(),
                        ServiceAccountStatusHistoryChangeType.ACTIVATION.getCode(), "Activation");
            break;
            case DEACTIVATION:
                optionData = new EnumOptionData(ServiceAccountStatusHistoryChangeType.DEACTIVATION.getValue().longValue(),
                        ServiceAccountStatusHistoryChangeType.DEACTIVATION.getCode(), "Deactivation");
            break;
        }

        return optionData;
    }
}