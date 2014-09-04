/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.data;

import java.util.Collection;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;

/**
 * Immutable data object represent PGSClient identity data.
 */
public class PGSClientIdentifierData {

    private final Long id;
    private final Long clientId;
    private final CodeValueData documentType;
    private final String documentKey;
    private final String description;
    @SuppressWarnings("unused")
    private final Collection<CodeValueData> allowedDocumentTypes;

    public static PGSClientIdentifierData singleItem(final Long id, final Long clientId, final CodeValueData documentType,
            final String documentKey, final String description) {
        return new PGSClientIdentifierData(id, clientId, documentType, documentKey, description, null);
    }

    public static PGSClientIdentifierData template(final Collection<CodeValueData> codeValues) {
        return new PGSClientIdentifierData(null, null, null, null, null, codeValues);
    }

    public static PGSClientIdentifierData template(final PGSClientIdentifierData data, final Collection<CodeValueData> codeValues) {
        return new PGSClientIdentifierData(data.id, data.clientId, data.documentType, data.documentKey, data.description, codeValues);
    }

    public PGSClientIdentifierData(final Long id, final Long clientId, final CodeValueData documentType, final String documentKey,
            final String description, final Collection<CodeValueData> allowedDocumentTypes) {
        this.id = id;
        this.clientId = clientId;
        this.documentType = documentType;
        this.documentKey = documentKey;
        this.description = description;
        this.allowedDocumentTypes = allowedDocumentTypes;
    }
}