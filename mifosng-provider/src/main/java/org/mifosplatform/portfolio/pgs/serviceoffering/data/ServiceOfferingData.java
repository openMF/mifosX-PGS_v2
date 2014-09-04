/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceoffering.data;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a PGS Service Offering.
 */
public class ServiceOfferingData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final String description;
    @SuppressWarnings("unused")
    private final EnumOptionData feeStructure;

    public static ServiceOfferingData instance(final Long id, final String name, final String description, final EnumOptionData feeStructure) {
        return new ServiceOfferingData(id, name, description, feeStructure);
    }

    private ServiceOfferingData(final Long id, final String name, final String description, final EnumOptionData feeStructure) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.feeStructure = feeStructure;
    }

}