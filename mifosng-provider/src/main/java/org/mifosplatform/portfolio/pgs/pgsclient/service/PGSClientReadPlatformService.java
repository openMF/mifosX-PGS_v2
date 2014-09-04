/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.portfolio.pgs.pgsclient.data.PGSClientData;

public interface PGSClientReadPlatformService {

    PGSClientData retrieveTemplate(Long officeId, boolean staffInSelectedOfficeOnly);

    Page<PGSClientData> retrieveAll(SearchParameters searchParameters);

    PGSClientData retrieveOne(Long clientId);

    Collection<PGSClientData> retrieveAllForLookup(String extraCriteria);

    Collection<PGSClientData> retrieveAllForLookupByOfficeId(Long officeId);

    PGSClientData retrieveClientByIdentifier(Long identifierTypeId, String identifierKey);

    Collection<PGSClientData> retrieveClientMembersOfGroup(Long groupId);

    Collection<PGSClientData> retrieveActiveClientMembersOfGroup(Long groupId);

    Collection<PGSClientData> retrieveActiveClientMembersOfCenter(final Long centerId);

    PGSClientData retrieveAllClosureReasons(String clientClosureReason);
}