package org.mifosplatform.portfolio.pgs.pgsclient.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.pgs.pgsclient.service.PGSClientWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClosePGSClientCommandHandler implements NewCommandSourceHandler {

    private final PGSClientWritePlatformService clientWritePlatformService;

    @Autowired
    public ClosePGSClientCommandHandler(final PGSClientWritePlatformService clientWritePlatformService) {
        this.clientWritePlatformService = clientWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.clientWritePlatformService.closeClient(command.entityId(), command);
    }
}