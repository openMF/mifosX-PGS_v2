package org.mifosplatform.portfolio.pgs.transactionrecord.domain;

public enum TransactionRecordType {

    CREDIT(0, "transactionRecordType.asyougo"), //
    DEBIT(100, "transactionRecordType.upfront");

    private final Integer value;
    private final String code;

    public static TransactionRecordType fromInt(final Integer statusValue) {

        TransactionRecordType enumeration = TransactionRecordType.CREDIT;
        switch (statusValue) {
            case 100:
                enumeration = TransactionRecordType.DEBIT;
            break;
        }
        return enumeration;
    }

    private TransactionRecordType(final Integer value, final String code) {
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