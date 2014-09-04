/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceoffering.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class ServiceOfferingFeeStructureEnumerations {

    public static EnumOptionData feeStructure(final Integer feeStructureId) {
        return feeStructure(ServiceOfferingFeeStructureType.fromInt(feeStructureId));
    }

    public static EnumOptionData feeStructure(final ServiceOfferingFeeStructureType feeStructure) {
        EnumOptionData optionData = new EnumOptionData(ServiceOfferingFeeStructureType.ASYOUGO.getValue().longValue(),
                ServiceOfferingFeeStructureType.ASYOUGO.getCode(), "AsYouGo");
        switch (feeStructure) {
            case ASYOUGO:
                optionData = new EnumOptionData(ServiceOfferingFeeStructureType.ASYOUGO.getValue().longValue(),
                        ServiceOfferingFeeStructureType.ASYOUGO.getCode(), "AsYouGo");
            break;
            case UPFRONT:
                optionData = new EnumOptionData(ServiceOfferingFeeStructureType.UPFRONT.getValue().longValue(),
                        ServiceOfferingFeeStructureType.UPFRONT.getCode(), "UpFront");
            break;
        }

        return optionData;
    }
}