/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.documentmanagement.domain.Image;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.interestratechart.data.ClientIncentiveAttributes;
import org.mifosplatform.portfolio.pgs.pgsclient.api.PGSClientApiConstants;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.mifosclient.MifosClient;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsProduct;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_pgsclient", uniqueConstraints = { @UniqueConstraint(columnNames = { "pgs_client_account_no" }, name = "account_no_UNIQUE"), //
        @UniqueConstraint(columnNames = { "mobile_no" }, name = "mobile_no_UNIQUE") })
public final class PGSClient extends AbstractPersistable<Long> {
	
    @Column(name = "pgs_client_account_no", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "transfer_to_office_id", nullable = true)
    private Office transferToOffice;

    @OneToOne(optional = true)
    @JoinColumn(name = "image_id", nullable = true)
    private Image image;

    /**
     * A value from {@link PGSClientStatus}.
     */
    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name = "activation_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @Column(name = "office_joining_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date officeJoiningDate;

    @Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "middlename", length = 50)
    private String middlename;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "fullname", length = 100)
    private String fullname;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @Column(name = "date_of_birth", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_cv_id", nullable = true)
    private CodeValue gender;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "m_group_client", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @Transient
    private boolean accountNumberRequiresAutoGeneration = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_cv_id", nullable = true)
    private CodeValue closureReason;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closeddBy;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "activatedon_userid", nullable = true)
    private AppUser activatedBy;

    @ManyToOne
    @JoinColumn(name = "default_savings_product", nullable = true)
    private SavingsProduct savingsProduct;

    @ManyToOne
    @JoinColumn(name = "default_savings_account", nullable = true)
    private SavingsAccount savingsAccount;

    @Transient
    private ClientIncentiveAttributes incentiveAttributes;
    
    @Column(name = "mifos_client_id", nullable = true)
    private long mifosClientId;
    
    @OneToOne(optional = true)
    @JoinColumn(name = "service_account", nullable = true)
    private ServiceAccount serviceAccount;
    
    public static PGSClient createNew(final AppUser currentUser, final Office clientOffice, final Group clientParentGroup, final Staff staff,
            final SavingsProduct savingsProduct, final CodeValue gender, final JsonCommand command) {

        final String accountNo = command.stringValueOfParameterNamed(PGSClientApiConstants.accountNoParamName);
        final String externalId = command.stringValueOfParameterNamed(PGSClientApiConstants.externalIdParamName);
        final String mobileNo = command.stringValueOfParameterNamed(PGSClientApiConstants.mobileNoParamName);

        final String firstname = command.stringValueOfParameterNamed(PGSClientApiConstants.firstnameParamName);
        final String middlename = command.stringValueOfParameterNamed(PGSClientApiConstants.middlenameParamName);
        final String lastname = command.stringValueOfParameterNamed(PGSClientApiConstants.lastnameParamName);
        final String fullname = command.stringValueOfParameterNamed(PGSClientApiConstants.fullnameParamName);

        final LocalDate dataOfBirth = command.localDateValueOfParameterNamed(PGSClientApiConstants.dateOfBirthParamName);

        PGSClientStatus status = PGSClientStatus.PENDING;
        boolean active = false;
        if (command.hasParameter("active")) {
            active = command.booleanPrimitiveValueOfParameterNamed(PGSClientApiConstants.activeParamName);
        }

        LocalDate activationDate = null;
        LocalDate officeJoiningDate = null;
        if (active) {
            status = PGSClientStatus.ACTIVE;
            activationDate = command.localDateValueOfParameterNamed(PGSClientApiConstants.activationDateParamName);
            officeJoiningDate = activationDate;
        }

        LocalDate submittedOnDate = new LocalDate();
        if (active && submittedOnDate.isAfter(activationDate)) {
            submittedOnDate = activationDate;
        }
        if (command.hasParameter(PGSClientApiConstants.submittedOnDateParamName)) {
            submittedOnDate = command.localDateValueOfParameterNamed(PGSClientApiConstants.submittedOnDateParamName);
        }
        final SavingsAccount account = null;
        return new PGSClient(currentUser, status, clientOffice, clientParentGroup, accountNo, firstname, middlename, lastname, fullname,
                activationDate, officeJoiningDate, externalId, mobileNo, staff, submittedOnDate, savingsProduct, account, dataOfBirth,
                gender);
    }

    protected PGSClient() {
        //
    }

    private PGSClient(final AppUser currentUser, final PGSClientStatus status, final Office office, final Group clientParentGroup,
            final String accountNo, final String firstname, final String middlename, final String lastname, final String fullname,
            final LocalDate activationDate, final LocalDate officeJoiningDate, final String externalId, final String mobileNo,
            final Staff staff, final LocalDate submittedOnDate, final SavingsProduct savingsProduct, final SavingsAccount savingsAccount,
            final LocalDate dateOfBirth, final CodeValue gender) {

        if (StringUtils.isBlank(accountNo)) {
            this.accountNumber = new RandomPasswordGenerator(19).generate();
            this.accountNumberRequiresAutoGeneration = true;
        } else {
            this.accountNumber = accountNo;
        }

        this.submittedOnDate = submittedOnDate.toDate();
        this.submittedBy = currentUser;

        this.status = status.getValue();
        this.office = office;
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }

        if (StringUtils.isNotBlank(mobileNo)) {
            this.mobileNo = mobileNo.trim();
        } else {
            this.mobileNo = null;
        }

        if (activationDate != null) {
            this.activationDate = activationDate.toDateTimeAtStartOfDay().toDate();
            this.activatedBy = currentUser;
        }
        if (officeJoiningDate != null) {
            this.officeJoiningDate = officeJoiningDate.toDateTimeAtStartOfDay().toDate();
        }
        if (StringUtils.isNotBlank(firstname)) {
            this.firstname = firstname.trim();
        } else {
            this.firstname = null;
        }

        if (StringUtils.isNotBlank(middlename)) {
            this.middlename = middlename.trim();
        } else {
            this.middlename = null;
        }

        if (StringUtils.isNotBlank(lastname)) {
            this.lastname = lastname.trim();
        } else {
            this.lastname = null;
        }

        if (StringUtils.isNotBlank(fullname)) {
            this.fullname = fullname.trim();
        } else {
            this.fullname = null;
        }

        if (clientParentGroup != null) {
            this.groups = new HashSet<Group>();
            this.groups.add(clientParentGroup);
        }

        this.staff = staff;
        this.savingsProduct = savingsProduct;
        this.savingsAccount = savingsAccount;

        if (gender != null) {
            this.gender = gender;
        }
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth.toDateTimeAtStartOfDay().toDate();
        }

        deriveDisplayName();
        validate();
    }

    private void validate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        validateNameParts(dataValidationErrors);
        validateActivationDate(dataValidationErrors);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void setAccountNumberRequiresAutoGeneration(final boolean accountNumberRequiresAutoGeneration) {
        this.accountNumberRequiresAutoGeneration = accountNumberRequiresAutoGeneration;
    }

    public boolean identifiedBy(final String identifier) {
        return identifier.equalsIgnoreCase(this.externalId);
    }

    public boolean identifiedBy(final Long clientId) {
        return getId().equals(clientId);
    }

    public void updateAccountNo(final String accountIdentifier) {
        this.accountNumber = accountIdentifier;
        this.accountNumberRequiresAutoGeneration = false;
    }

    public void updateMifosClientId(final long mifosClientId) {
        this.mifosClientId = mifosClientId;
    }
    
    public void activate(final AppUser currentUser, final DateTimeFormatter formatter, final LocalDate activationLocalDate) {

        if (isActive()) {
            final String defaultUserMessage = "Cannot activate PGS client. Client is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.already.active", defaultUserMessage,
                    PGSClientApiConstants.activationDateParamName, activationLocalDate.toString(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        this.activationDate = activationLocalDate.toDate();
        this.activatedBy = currentUser;
        this.officeJoiningDate = this.activationDate;
        this.status = PGSClientStatus.ACTIVE.getValue();

        validate();
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isActive() {
        return PGSClientStatus.fromInt(this.status).isActive();
    }

    public boolean isClosed() {
        return PGSClientStatus.fromInt(this.status).isClosed();
    }

    public boolean isTransferInProgress() {
        return PGSClientStatus.fromInt(this.status).isTransferInProgress();
    }

    public boolean isTransferOnHold() {
        return PGSClientStatus.fromInt(this.status).isTransferOnHold();
    }

    public boolean isTransferInProgressOrOnHold() {
        return isTransferInProgress() || isTransferOnHold();
    }

    public boolean isNotPending() {
        return !isPending();
    }

    public boolean isPending() {
        return PGSClientStatus.fromInt(this.status).isPending();
    }

    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(9);

        if (command.isChangeInIntegerParameterNamed(PGSClientApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(PGSClientApiConstants.statusParamName);
            actualChanges.put(PGSClientApiConstants.statusParamName, PGSClientEnumerations.status(newValue));
            this.status = PGSClientStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.accountNoParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.accountNoParamName);
            actualChanges.put(PGSClientApiConstants.accountNoParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.externalIdParamName);
            actualChanges.put(PGSClientApiConstants.externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.mobileNoParamName);
            actualChanges.put(PGSClientApiConstants.mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.firstnameParamName);
            actualChanges.put(PGSClientApiConstants.firstnameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.middlenameParamName, this.middlename)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.middlenameParamName);
            actualChanges.put(PGSClientApiConstants.middlenameParamName, newValue);
            this.middlename = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.lastnameParamName);
            actualChanges.put(PGSClientApiConstants.lastnameParamName, newValue);
            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(PGSClientApiConstants.fullnameParamName, this.fullname)) {
            final String newValue = command.stringValueOfParameterNamed(PGSClientApiConstants.fullnameParamName);
            actualChanges.put(PGSClientApiConstants.fullnameParamName, newValue);
            this.fullname = newValue;
        }

        if (command.isChangeInLongParameterNamed(PGSClientApiConstants.staffIdParamName, staffId())) {
            final Long newValue = command.longValueOfParameterNamed(PGSClientApiConstants.staffIdParamName);
            actualChanges.put(PGSClientApiConstants.staffIdParamName, newValue);
        }
        
        if (command.isChangeInLongParameterNamed(PGSClientApiConstants.genderIdParamName, genderId())) {
            final Long newValue = command.longValueOfParameterNamed(PGSClientApiConstants.genderIdParamName);
            actualChanges.put(PGSClientApiConstants.genderIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(PGSClientApiConstants.savingsProductIdParamName, savingsProductId())) {
            final Long newValue = command.longValueOfParameterNamed(PGSClientApiConstants.savingsProductIdParamName);
            actualChanges.put(PGSClientApiConstants.savingsProductIdParamName, newValue);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(PGSClientApiConstants.activationDateParamName, getActivationLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(PGSClientApiConstants.activationDateParamName);
            actualChanges.put(PGSClientApiConstants.activationDateParamName, valueAsInput);
            actualChanges.put(PGSClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(PGSClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(PGSClientApiConstants.activationDateParamName);
            this.activationDate = newValue.toDate();
            this.officeJoiningDate = this.activationDate;
        }

        validate();

        deriveDisplayName();

        return actualChanges;
    }

    private void validateNameParts(final List<ApiParameterError> dataValidationErrors) {
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client");

        if (StringUtils.isNotBlank(this.fullname)) {

            baseDataValidator.reset().parameter(PGSClientApiConstants.firstnameParamName).value(this.firstname)
                    .mustBeBlankWhenParameterProvided(PGSClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(PGSClientApiConstants.middlenameParamName).value(this.middlename)
                    .mustBeBlankWhenParameterProvided(PGSClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(PGSClientApiConstants.lastnameParamName).value(this.lastname)
                    .mustBeBlankWhenParameterProvided(PGSClientApiConstants.fullnameParamName, this.fullname);
        } else {

            baseDataValidator.reset().parameter(PGSClientApiConstants.firstnameParamName).value(this.firstname).notBlank()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(PGSClientApiConstants.middlenameParamName).value(this.middlename).ignoreIfNull()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(PGSClientApiConstants.lastnameParamName).value(this.lastname).notBlank()
                    .notExceedingLengthOf(50);
        }
    }

    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors) {

        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.in.the.future",
                    defaultUserMessage, PGSClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.after.activation.date",
                    defaultUserMessage, PGSClientApiConstants.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.activationDate.in.the.future",
                    defaultUserMessage, PGSClientApiConstants.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null) {
            if (this.office.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Client activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.clients.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        PGSClientApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }
    }

    private void deriveDisplayName() {

        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(this.firstname)) {
            nameBuilder.append(this.firstname).append(' ');
        }

        if (StringUtils.isNotBlank(this.middlename)) {
            nameBuilder.append(this.middlename).append(' ');
        }

        if (StringUtils.isNotBlank(this.lastname)) {
            nameBuilder.append(this.lastname);
        }

        if (StringUtils.isNotBlank(this.fullname)) {
            nameBuilder = new StringBuilder(this.fullname);
        }

        this.displayName = nameBuilder.toString();
    }

    public LocalDate getSubmittedOnDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);
    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = LocalDate.fromDateFields(this.activationDate);
        }
        return activationLocalDate;
    }

    public LocalDate getOfficeJoiningLocalDate() {
        LocalDate officeJoiningLocalDate = null;
        if (this.officeJoiningDate != null) {
            officeJoiningLocalDate = LocalDate.fromDateFields(this.officeJoiningDate);
        }
        return officeJoiningLocalDate;
    }

    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.office.identifiedBy(officeId);
    }

    public Long officeId() {
        return this.office.getId();
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

    public String mobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(final String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public Office getOffice() {
        return this.office;
    }

    public Office getTransferToOffice() {
        return this.transferToOffice;
    }

    public void updateOffice(final Office office) {
        this.office = office;
    }

    public void updateTransferToOffice(final Office office) {
        this.transferToOffice = office;
    }
    
    public void updateStatus(final int status) {
        this.status = status;
    }

    public void updateOfficeJoiningDate(final Date date) {
        this.officeJoiningDate = date;
    }

    private Long staffId() {
        Long staffId = null;
        if (this.staff != null) {
            staffId = this.staff.getId();
        }
        return staffId;
    }

    public void updateStaff(final Staff staff) {
        this.staff = staff;
    }

    public Staff getStaff() {
        return this.staff;
    }

    public void unassignStaff() {
        this.staff = null;
    }

    public void assignStaff(final Staff staff) {
        this.staff = staff;
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void close(final AppUser currentUser, final CodeValue closureReason, final Date closureDate) {
        this.closureReason = closureReason;
        this.closureDate = closureDate;
        this.closeddBy = currentUser;
        this.status = PGSClientStatus.CLOSED.getValue();
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public boolean isActivatedAfter(final LocalDate submittedOn) {
        return getActivationLocalDate().isAfter(submittedOn);
    }

    public boolean isChildOfGroup(final Long groupId) {
        if (groupId != null && this.groups != null && !this.groups.isEmpty()) {
            for (final Group group : this.groups) {
                if (group.getId().equals(groupId)) { return true; }
            }
        }
        return false;
    }

    private Long savingsProductId() {
        Long savingsProductId = null;
        if (this.savingsProduct != null) {
            savingsProductId = this.savingsProduct.getId();
        }
        return savingsProductId;
    }

    public SavingsProduct SavingsProduct() {
        return this.savingsProduct;
    }

    public void updateSavingsProduct(SavingsProduct savingsProduct) {
        this.savingsProduct = savingsProduct;
    }

    public AppUser activatedBy() {
        return this.activatedBy;
    }

    public SavingsAccount savingsAccount() {
        return this.savingsAccount;
    }

    public void updateSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

     
    public ClientIncentiveAttributes incentiveAttributes() {
        return this.incentiveAttributes;
    }
	

    public void updateIncentiveAttributes(final Long ageLimitForChildren, final Long ageLimitForSeniorCitizen, final LocalDate compareOnDate) {
        boolean isFemale = false;
        boolean isChild = false;
        boolean isSeniorCitizen = false;

        if (this.gender != null) {
            // FIXME: this needs to be handled in better way
            if (this.gender.label().equalsIgnoreCase("FEMALE")) {
                isFemale = true;
            }
        }

        if (this.dateOfBirth != null) {
            final LocalDate dobLacalDate = LocalDate.fromDateFields(this.dateOfBirth);
            final int age = Years.yearsBetween(dobLacalDate, compareOnDate).getYears();
            
            if (age >= ageLimitForSeniorCitizen.intValue()) {
                isSeniorCitizen = true;
            }

            if (age <= ageLimitForChildren.intValue()) {
                isChild = true;
            }
        }

        this.incentiveAttributes = ClientIncentiveAttributes.instance(isFemale, isChild, isSeniorCitizen);

    }

    public CodeValue gender() {
        return this.gender;
    }
    
    private Long genderId() {
        Long genderId = null;
        if (this.gender != null) {
            genderId = this.gender.getId();
        }
        return genderId;
    }

    public void updateGender(final CodeValue gender) {
        this.gender = gender;
    }

	public String getFirstname() {
		return this.firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public ServiceAccount getServiceAccount() {
		return this.serviceAccount;
	}

	public void setServiceAccount(ServiceAccount serviceAccount) {
		this.serviceAccount = serviceAccount;
	}
}