package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain;

public enum ServiceAccountStatusHistoryChangeType {

    ACTIVATION(0, "serviceaccountstatushistorychange.activation"), //
    DEACTIVATION(100, "serviceaccountstatushistorychange.deactivation");

    private final Integer value;
    private final String code;

    public static ServiceAccountStatusHistoryChangeType fromInt(final Integer statusValue) {

        ServiceAccountStatusHistoryChangeType enumeration = ServiceAccountStatusHistoryChangeType.ACTIVATION;
        switch (statusValue) {
            case 100:
                enumeration = ServiceAccountStatusHistoryChangeType.DEACTIVATION;
            break;
        }
        return enumeration;
    }

    private ServiceAccountStatusHistoryChangeType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}