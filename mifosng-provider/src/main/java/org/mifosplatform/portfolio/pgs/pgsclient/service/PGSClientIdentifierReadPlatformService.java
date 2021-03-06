/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.service;

import java.util.Collection;

import org.mifosplatform.portfolio.pgs.pgsclient.data.PGSClientIdentifierData;

public interface PGSClientIdentifierReadPlatformService {

    Collection<PGSClientIdentifierData> retrieveClientIdentifiers(Long clientId);

    PGSClientIdentifierData retrieveClientIdentifier(Long clientId, Long clientIdentifierId);

}