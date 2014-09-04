/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class PGSClientEnumerations {

    public static EnumOptionData status(final Integer statusId) {
        return status(PGSClientStatus.fromInt(statusId));
    }

    public static EnumOptionData status(final PGSClientStatus status) {
        EnumOptionData optionData = new EnumOptionData(PGSClientStatus.INVALID.getValue().longValue(), PGSClientStatus.INVALID.getCode(),
                "Invalid");
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData(PGSClientStatus.INVALID.getValue().longValue(), PGSClientStatus.INVALID.getCode(), "Invalid");
            break;
            case PENDING:
                optionData = new EnumOptionData(PGSClientStatus.PENDING.getValue().longValue(), PGSClientStatus.PENDING.getCode(), "Pending");
            break;
            case ACTIVE:
                optionData = new EnumOptionData(PGSClientStatus.ACTIVE.getValue().longValue(), PGSClientStatus.ACTIVE.getCode(), "Active");
            break;
            case CLOSED:
                optionData = new EnumOptionData(PGSClientStatus.CLOSED.getValue().longValue(), PGSClientStatus.CLOSED.getCode(), "Closed");
            break;
            case TRANSFER_IN_PROGRESS:
                optionData = new EnumOptionData(PGSClientStatus.TRANSFER_IN_PROGRESS.getValue().longValue(),
                        PGSClientStatus.TRANSFER_IN_PROGRESS.getCode(), "Transfer in progress");
            break;
            case TRANSFER_ON_HOLD:
                optionData = new EnumOptionData(PGSClientStatus.TRANSFER_ON_HOLD.getValue().longValue(),
                        PGSClientStatus.TRANSFER_ON_HOLD.getCode(), "Transfer on hold");
            break;
            default:
            break;
        }

        return optionData;
    }
}