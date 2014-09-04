/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class TransactionRecordNotFoundException extends AbstractPlatformResourceNotFoundException {

    public TransactionRecordNotFoundException(final Long resourceId) {
        super("error.msg.transaction.record.identifier.not.found", "Transaction Record with identifier `" + resourceId + "` does not exist", resourceId);
    }
}