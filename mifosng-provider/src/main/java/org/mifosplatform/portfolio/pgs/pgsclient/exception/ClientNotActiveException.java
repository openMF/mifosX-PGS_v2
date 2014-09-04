package org.mifosplatform.portfolio.pgs.pgsclient.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ClientNotActiveException extends AbstractPlatformDomainRuleException {

    public ClientNotActiveException(final Long clientId) {
        super("error.msg.client.not.active.exception", "The Client with id `" + clientId + "` is not active", clientId);
    }

}
