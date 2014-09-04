/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class TransactionRecordTypeEnumerations {

    public static EnumOptionData type(final Integer typeId) {
        return type(TransactionRecordType.fromInt(typeId));
    }

    public static EnumOptionData type(final TransactionRecordType type) {
        EnumOptionData optionData = new EnumOptionData(TransactionRecordType.CREDIT.getValue().longValue(),
                TransactionRecordType.CREDIT.getCode(), "Credit");
        switch (type) {
            case CREDIT:
                optionData = new EnumOptionData(TransactionRecordType.CREDIT.getValue().longValue(),
                        TransactionRecordType.CREDIT.getCode(), "Credit");
            break;
            case DEBIT:
                optionData = new EnumOptionData(TransactionRecordType.DEBIT.getValue().longValue(),
                        TransactionRecordType.DEBIT.getCode(), "Debit");
            break;
        }

        return optionData;
    }
}