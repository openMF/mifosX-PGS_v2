/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandProcessingService;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.sms.domain.SmsMessage;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageRepository;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGateway;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGatewayRepository;
import org.mifosplatform.infrastructure.smsgateway.domain.frontlinesms.FrontlineSMSMessage;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.client.domain.AccountNumberGenerator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGeneratorFactory;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.group.exception.GroupNotFoundException;
import org.mifosplatform.portfolio.note.domain.Note;
import org.mifosplatform.portfolio.note.domain.NoteRepository;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformation;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformationRepository;
import org.mifosplatform.portfolio.pgs.pgsclient.api.PGSClientApiConstants;
import org.mifosplatform.portfolio.pgs.pgsclient.data.PGSClientDataValidator;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientRepositoryWrapper;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientStatus;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.mifosclient.MifosClient;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.mifospgsaccount.MifosPGSAccount;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.ClientActiveForUpdateException;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.ClientHasNoStaffException;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.ClientMustBePendingToBeDeletedException;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.InvalidClientSavingProductException;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.InvalidClientStateTransitionException;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOffering;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOfferingRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsProduct;
import org.mifosplatform.portfolio.savings.domain.SavingsProductRepository;
import org.mifosplatform.portfolio.savings.exception.SavingsAccountNotFoundException;
import org.mifosplatform.portfolio.savings.exception.SavingsProductNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class PGSClientWritePlatformServiceJpaRepositoryImpl implements PGSClientWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(PGSClientWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final PGSClientRepositoryWrapper clientRepository;
    private final OfficeRepository officeRepository;
    private final NoteRepository noteRepository;
    private final GroupRepository groupRepository;
    private final PGSClientDataValidator fromApiJsonDeserializer;
    private final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory;
    private final StaffRepositoryWrapper staffRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final SavingsAccountRepository savingsRepository;
    private final SavingsProductRepository savingsProductRepository;
    private final CommandProcessingService commandProcessingService;
    private final CurrentAccountInformationRepository currentAccountInformationRepository;
    private final ServiceAccountRepository serviceAccountRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final SmsGatewayRepository smsGatewayRepository;
    private String mifosPGSClientResponse;
    private String mifosPGSAccountResponse;
    private long mifosPGSClientId;
    
    @Autowired
    public PGSClientWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final PGSClientRepositoryWrapper clientRepository, final OfficeRepository officeRepository, final NoteRepository noteRepository,
            final PGSClientDataValidator fromApiJsonDeserializer, final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory,
            final GroupRepository groupRepository, final StaffRepositoryWrapper staffRepository,
            final CodeValueRepositoryWrapper codeValueRepository,
            final SavingsAccountRepository savingsRepository, final SavingsProductRepository savingsProductRepository,
            final CommandProcessingService commandProcessingService, final CurrentAccountInformationRepository currentAccountInformationRepository,
            final ServiceAccountRepository serviceAccountRepository, final ServiceOfferingRepository serviceOfferingRepository, 
            final SmsMessageRepository smsMessageRepository, final SmsGatewayRepository smsGatewayRepository) {
        this.context = context;
        this.clientRepository = clientRepository;
        this.officeRepository = officeRepository;
        this.noteRepository = noteRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.accountIdentifierGeneratorFactory = accountIdentifierGeneratorFactory;
        this.groupRepository = groupRepository;
        this.staffRepository = staffRepository;
        this.codeValueRepository = codeValueRepository;
        this.savingsRepository = savingsRepository;
        this.savingsProductRepository = savingsProductRepository;
        this.commandProcessingService = commandProcessingService;
        this.currentAccountInformationRepository = currentAccountInformationRepository;
        this.serviceAccountRepository = serviceAccountRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.smsMessageRepository = smsMessageRepository;
        this.smsGatewayRepository = smsGatewayRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteClient(final Long clientId) {

        final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        if (client.isNotPending()) { throw new ClientMustBePendingToBeDeletedException(clientId); }

        final List<Note> relatedNotes = this.noteRepository.findByClientId(clientId);
        this.noteRepository.deleteInBatch(relatedNotes);

        this.clientRepository.delete(client);

        return new CommandProcessingResultBuilder() //
                .withOfficeId(client.officeId()) //
                .withClientId(clientId) //
                .withEntityId(clientId) //
                .build();
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("pgs_client_account_no_UNIQUE")) {
            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo", "Client with accountNo `" + accountNo
                    + "` already exists", "accountNo", accountNo);
        } else if (realCause.getMessage().contains("mobile_no")) {
            final String mobileNo = command.stringValueOfParameterNamed("mobileNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.mobileNo", "Client with mobileNo `" + mobileNo
                    + "` already exists", "mobileNo", mobileNo);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Transactional
    @Override
    public CommandProcessingResult createClient(final JsonCommand command) {
    	//TODO amidst all of the object creation and DB writes, there needs to be some messages 
    	// returned if/when things go wrong
        try {
            final AppUser currentUser = this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final Long officeId = command.longValueOfParameterNamed(PGSClientApiConstants.officeIdParamName);

            final Office clientOffice = this.officeRepository.findOne(officeId);
            if (clientOffice == null) { throw new OfficeNotFoundException(officeId); }

            final Long groupId = command.longValueOfParameterNamed(PGSClientApiConstants.groupIdParamName);

            Group clientParentGroup = null;
            if (groupId != null) {
                clientParentGroup = this.groupRepository.findOne(groupId);
                if (clientParentGroup == null) { throw new GroupNotFoundException(groupId); }
            }

            String firstName = null;
            firstName = command.stringValueOfParameterNamed(PGSClientApiConstants.firstnameParamName);
            
            String lastName = null;
            lastName = command.stringValueOfParameterNamed(PGSClientApiConstants.lastnameParamName);
            
            Staff staff = null;
            final Long staffId = command.longValueOfParameterNamed(PGSClientApiConstants.staffIdParamName);
            if (staffId != null) {
                staff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(staffId, clientOffice.getHierarchy());
            }

            CodeValue gender = null;
            final Long genderId = command.longValueOfParameterNamed(PGSClientApiConstants.genderIdParamName);
            if (genderId != null) {
                gender = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(PGSClientApiConstants.GENDER, genderId);
            }

            SavingsProduct savingsProduct = null;
            final Long savingsProductId = command.longValueOfParameterNamed(PGSClientApiConstants.savingsProductIdParamName);
            if (savingsProductId != null) {
                savingsProduct = this.savingsProductRepository.findOne(savingsProductId);
                if (savingsProduct == null) { throw new SavingsProductNotFoundException(savingsProductId); }
            }
            
            String mobileNo = command.stringValueOfParameterNamed("mobileNo"); 
            
            // Create new client in PGS backend
            final PGSClient newClient = PGSClient.createNew(currentUser, clientOffice, clientParentGroup, staff, 
            		savingsProduct, gender, command);
            
            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            //TODO make this dynamic
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy"); 
            final Date date = new Date();
            final String activationDate = sdfDate.format(date);
            
            // Create new client in Mifos backend
            MifosClient mifosClient = new MifosClient(clientOffice.getId(), 
    				firstName, lastName, newClient.isActive(), savingsProductId, 
    				command.locale(),command.dateFormat(), activationDate);
    		try {
    			mifosPGSClientResponse = mifosClient.createInMifosInstance();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		if (mifosPGSClientResponse != null){
    			JsonObject mifosResponseJSON  = new JsonParser().parse(mifosPGSClientResponse).getAsJsonObject();
    			newClient.updateStatus(PGSClientStatus.ACTIVE.getValue());
    			mifosPGSClientId = mifosResponseJSON.get("clientId").getAsLong();
    			newClient.updateMifosClientId(mifosPGSClientId);
    		
    		} else {
    			// TODO Alert user of issue 
    		}
            
    		this.clientRepository.save(newClient);
    		
            if (newClient.isAccountNumberRequiresAutoGeneration()) {
                final AccountNumberGenerator accountNoGenerator = this.accountIdentifierGeneratorFactory
                        .determineClientAccountNoGenerator(newClient.getId());
                newClient.updateAccountNo(accountNoGenerator.generate());
                this.clientRepository.save(newClient);
            }

            
    		//TODO better error handling, incase this returns an error message
    		//Creating PGS account in Mifos backend
            MifosPGSAccount mifosPGSAccount = new MifosPGSAccount(command.locale(), command.dateFormat(), activationDate, 
					mifosPGSClientId);
			mifosPGSAccountResponse = mifosPGSAccount.createinMifosInstance();
    		
			if (mifosPGSAccountResponse != null){
				JsonObject mifosResponseJSON  = new JsonParser().parse(mifosPGSAccountResponse).getAsJsonObject();
				
				// Create new blank ServiceAccount
				ServiceAccount serviceAccount = ServiceAccount.createNew(newClient, null, null, null, 
						null, null, 0.0, true);
				
				mifosPGSClientId = mifosResponseJSON.get("clientId").getAsLong();
				long mifosPGSAccountId = mifosResponseJSON.get("savingsId").getAsLong();
				//Now that we have the savingsId, we can approve and activate the new account
				mifosPGSAccount.approve(mifosResponseJSON.get("savingsId").getAsString());
				mifosPGSAccount.activate(mifosResponseJSON.get("savingsId").getAsString());
				double mifosPGSAccountBalance = 0.0;
				// Create new CurrentAccountInformation
				CurrentAccountInformation currentAccountInformation = 
					CurrentAccountInformation.CurrentAccountInformation(serviceAccount, mifosPGSClientId, mifosPGSAccountId, mifosPGSAccountBalance);
				currentAccountInformationRepository.save(currentAccountInformation);
						
				// Create new ServiceOffering
				//TODO Allow for flexible creation of service offerings
				ServiceOffering serviceOffering = ServiceOffering.ServiceOffering(serviceAccount, "Name TBD", "Test Description", 100);
				serviceOfferingRepository.save(serviceOffering);
				
				// set new CurrentAccountInformation and Service Offering to ServiceAccount and save
				serviceAccount.setCurrentAccountInformation(currentAccountInformation);
				serviceAccount.setServiceOffering(serviceOffering);				
				serviceAccountRepository.save(serviceAccount);
				// Save new ServiceAccount to newClient
				newClient.setServiceAccount(serviceAccount);
			}
			
			boolean rollbackTransaction = false;
            if (newClient.isActive()) {
                final CommandWrapper commandWrapper = new CommandWrapperBuilder().activateClient(null).build();
                rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
            }
    		
            this.clientRepository.save(newClient);
            
            // TODO make this message dynamic
            String message = "Hello " + firstName + " welcome to PayGoSol solar payments services. Your "
            		+ "account number is " + newClient.getId().toString() + ".";
            
            // TODO make this all dynamic
            // TODO improve this code
            long gatewayId = 1;
            SmsMessage smsMessage = SmsMessage.pendingSms(newClient, staff, message, mobileNo, gatewayId);
            final SmsGateway smsGateway = this.smsGatewayRepository.findOne(smsMessage.gatewayId());	
            final FrontlineSMSMessage frontlineSMSMessage = new FrontlineSMSMessage(smsGateway.authToken(), smsMessage.message(), smsMessage.mobileNo(), smsGateway.url());
            this.smsMessageRepository.save(smsMessage);
            
            try {
				frontlineSMSMessage.sendPostRequest();
			} catch (IOException e) {
				// TODO return some kind of messasge to user, letting them know why this has failed. 
				return CommandProcessingResult.empty();
			}
            
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(clientOffice.getId()) //
                    .withClientId(newClient.getId()) //
                    .withGroupId(groupId) //
                    .withEntityId(newClient.getId()) //
                    //.withSavingsId(result.getSavingsId())//
                    .setRollbackTransaction(rollbackTransaction)//
                    //.setRollbackTransaction(result.isRollbackTransaction())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateClient(final Long clientId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            final PGSClient clientForUpdate = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final String clientHierarchy = clientForUpdate.getOffice().getHierarchy();

            this.context.validateAccessRights(clientHierarchy);

            final Map<String, Object> changes = clientForUpdate.update(command);

            if (changes.containsKey(PGSClientApiConstants.staffIdParamName)) {

                final Long newValue = command.longValueOfParameterNamed(PGSClientApiConstants.staffIdParamName);
                Staff newStaff = null;
                if (newValue != null) {
                    newStaff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(newValue, clientForUpdate.getOffice()
                            .getHierarchy());
                }
                clientForUpdate.updateStaff(newStaff);
            }
            
            if (changes.containsKey(PGSClientApiConstants.genderIdParamName)) {

                final Long newValue = command.longValueOfParameterNamed(PGSClientApiConstants.genderIdParamName);
                CodeValue gender = null;
                if (newValue != null) {
                    gender = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(PGSClientApiConstants.GENDER, newValue);
                }
                clientForUpdate.updateGender(gender);
            }

            if (changes.containsKey(PGSClientApiConstants.savingsProductIdParamName)) {
                if (clientForUpdate.isActive()) { throw new ClientActiveForUpdateException(clientId,
                        PGSClientApiConstants.savingsProductIdParamName); }
                SavingsProduct savingsProduct = null;
                final Long savingsProductId = command.longValueOfParameterNamed(PGSClientApiConstants.savingsProductIdParamName);
                if (savingsProductId != null) {
                    savingsProduct = this.savingsProductRepository.findOne(savingsProductId);
                    if (savingsProduct == null) { throw new SavingsProductNotFoundException(savingsProductId); }
                }
                clientForUpdate.updateSavingsProduct(savingsProduct);
            }

            if (!changes.isEmpty()) {
                this.clientRepository.saveAndFlush(clientForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(clientForUpdate.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult activateClient(final Long clientId, final JsonCommand command) {
        try {
            this.fromApiJsonDeserializer.validateActivation(command);

            final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

            final AppUser currentUser = this.context.authenticatedUser();
            client.activate(currentUser, fmt, activationDate);
            //CommandProcessingResult result = openSavingsAccount(client, fmt);
            this.clientRepository.saveAndFlush(client);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientId) //
                    //.withSavingsId(result.getSavingsId())//
                    //.setRollbackTransaction(result.isRollbackTransaction())//
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    //TODO fix this
    /*
    private CommandProcessingResult openSavingsAccount(final PGSClient client, final DateTimeFormatter fmt) {
        CommandProcessingResult commandProcessingResult = CommandProcessingResult.empty();
        if (client.isActive() && client.SavingsProduct() != null) {
            SavingsAccountDataDTO savingsAccountDataDTO = new SavingsAccountDataDTO(client, null, client.SavingsProduct(),
                    client.getActivationLocalDate(), client.activatedBy(), fmt);
            commandProcessingResult = this.savingsApplicationProcessWritePlatformService.createActiveApplication(savingsAccountDataDTO);
            if (commandProcessingResult.getSavingsId() != null) {
                SavingsAccount savingsAccount = this.savingsRepository.findOne(commandProcessingResult.getSavingsId());
                client.updateSavingsAccount(savingsAccount);
                client.updateSavingsProduct(null);
            }
        }
        return commandProcessingResult;
    }
	
	*/

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Transactional
    @Override
    public CommandProcessingResult unassignClientStaff(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(5);

        this.fromApiJsonDeserializer.validateForUnassignStaff(command.json());

        final PGSClient clientForUpdate = this.clientRepository.findOneWithNotFoundDetection(clientId);

        final Staff presentStaff = clientForUpdate.getStaff();
        Long presentStaffId = null;
        if (presentStaff == null) { throw new ClientHasNoStaffException(clientId); }
        presentStaffId = presentStaff.getId();
        final String staffIdParamName = PGSClientApiConstants.staffIdParamName;
        if (!command.isChangeInLongParameterNamed(staffIdParamName, presentStaffId)) {
            clientForUpdate.unassignStaff();
        }
        this.clientRepository.saveAndFlush(clientForUpdate);

        actualChanges.put(staffIdParamName, presentStaffId);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(clientForUpdate.officeId()) //
                .withEntityId(clientForUpdate.getId()) //
                .withClientId(clientId) //
                .with(actualChanges) //
                .build();
    }

    @Override
    public CommandProcessingResult assignClientStaff(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(5);

        this.fromApiJsonDeserializer.validateForAssignStaff(command.json());

        final PGSClient clientForUpdate = this.clientRepository.findOneWithNotFoundDetection(clientId);

        Staff staff = null;
        final Long staffId = command.longValueOfParameterNamed(PGSClientApiConstants.staffIdParamName);
        if (staffId != null) {
            staff = this.staffRepository.findByOfficeHierarchyWithNotFoundDetection(staffId, clientForUpdate.getOffice().getHierarchy());
            /**
             * TODO Vishwas: We maintain history of chage of loan officer w.r.t
             * loan in a history table, should we do the same for a client?
             * Especially useful when the change happens due to a transfer etc
             **/
            clientForUpdate.assignStaff(staff);
        }

        this.clientRepository.saveAndFlush(clientForUpdate);

        actualChanges.put(PGSClientApiConstants.staffIdParamName, staffId);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(clientForUpdate.officeId()) //
                .withEntityId(clientForUpdate.getId()) //
                .withClientId(clientId) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult closeClient(final Long clientId, final JsonCommand command) {
        try {

            final AppUser currentUser = this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateClose(command);

            final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final LocalDate closureDate = command.localDateValueOfParameterNamed(PGSClientApiConstants.closureDateParamName);
            final Long closureReasonId = command.longValueOfParameterNamed(PGSClientApiConstants.closureReasonIdParamName);

            final CodeValue closureReason = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    PGSClientApiConstants.CLIENT_CLOSURE_REASON, closureReasonId);

            if (PGSClientStatus.fromInt(client.getStatus()).isClosed()) {
                final String errorMessage = "Client is already closed.";
                throw new InvalidClientStateTransitionException("close", "is.already.closed", errorMessage);
            } else if (PGSClientStatus.fromInt(client.getStatus()).isUnderTransfer()) {
                final String errorMessage = "Cannot Close a Client under Transfer";
                throw new InvalidClientStateTransitionException("close", "is.under.transfer", errorMessage);
            }

            if (client.isNotPending() && client.getActivationLocalDate().isAfter(closureDate)) {
                final String errorMessage = "The client closureDate cannot be before the client ActivationDate.";
                throw new InvalidClientStateTransitionException("close", "date.cannot.before.client.actvation.date", errorMessage,
                        closureDate, client.getActivationLocalDate());
            }

            final List<SavingsAccount> clientSavingAccounts = this.savingsRepository.findSavingAccountByClientId(clientId);

            for (final SavingsAccount saving : clientSavingAccounts) {
                if (saving.isActive() || saving.isSubmittedAndPendingApproval() || saving.isApproved()) {
                    final String errorMessage = "Client cannot be closed because of non-closed savings account.";
                    throw new InvalidClientStateTransitionException("close", "non-closed.savings.account", errorMessage);
                }
            }

            client.close(currentUser, closureReason, closureDate.toDate());
            this.clientRepository.saveAndFlush(client);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public CommandProcessingResult updateDefaultSavingsAccount(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(5);

        this.fromApiJsonDeserializer.validateForSavingsAccount(command.json());

        final PGSClient clientForUpdate = this.clientRepository.findOneWithNotFoundDetection(clientId);

        SavingsAccount savingsAccount = null;
        final Long savingsId = command.longValueOfParameterNamed(PGSClientApiConstants.savingsAccountIdParamName);
        if (savingsId != null) {
            savingsAccount = this.savingsRepository.findOne(savingsId);
            if (savingsAccount == null) { throw new SavingsAccountNotFoundException(savingsId); }
            if (!savingsAccount.getClient().identifiedBy(clientId)) {
                String defaultUserMessage = "saving account must belongs to client";
                throw new InvalidClientSavingProductException("saving.account", "must.belongs.to.client", defaultUserMessage, savingsId,
                        clientForUpdate.getId());
            }
            clientForUpdate.updateSavingsAccount(savingsAccount);
        }

        this.clientRepository.saveAndFlush(clientForUpdate);

        actualChanges.put(PGSClientApiConstants.savingsAccountIdParamName, savingsId);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(clientForUpdate.officeId()) //
                .withEntityId(clientForUpdate.getId()) //
                .withClientId(clientId) //
                .with(actualChanges) //
                .build();
    }
}