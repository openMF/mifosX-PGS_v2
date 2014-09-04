/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.domain;

import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link PGSClientRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class PGSClientRepositoryWrapper {

    private final PGSClientRepository repository;

    @Autowired
    public PGSClientRepositoryWrapper(final PGSClientRepository repository) {
        this.repository = repository;
    }

    public PGSClient findOneWithNotFoundDetection(final Long id) {
        final PGSClient client = this.repository.findOne(id);
        if (client == null) { throw new ClientNotFoundException(id); }
        return client;
    }

    public void save(final PGSClient client) {
        this.repository.save(client);
    }

    public void saveAndFlush(final PGSClient client) {
        this.repository.saveAndFlush(client);
    }

    public void delete(final PGSClient client) {
        this.repository.delete(client);
    }
}