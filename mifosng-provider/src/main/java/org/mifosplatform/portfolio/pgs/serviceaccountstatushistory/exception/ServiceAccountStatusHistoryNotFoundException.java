/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class ServiceAccountStatusHistoryNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ServiceAccountStatusHistoryNotFoundException(final Long resourceId) {
        super("error.msg.service.account.status.history.not.found", "Service Account Status History with identifier `" + resourceId + 
        		"` does not exist", resourceId);
    }
}