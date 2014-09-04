package org.mifosplatform.portfolio.pgs.usagerecord.domain;

public enum UsageRecordMetricType {

    KWH(0, "usageMetricType.kwh"), //
    ELAPSEDTIME(100, "usageMetricType.elapsedTime");

    private final Integer value;
    private final String code;

    public static UsageRecordMetricType fromInt(final Integer statusValue) {

        UsageRecordMetricType enumeration = UsageRecordMetricType.KWH;
        switch (statusValue) {
            case 100:
                enumeration = UsageRecordMetricType.ELAPSEDTIME;
            break;
        }
        return enumeration;
    }

    private UsageRecordMetricType(final Integer value, final String code) {
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