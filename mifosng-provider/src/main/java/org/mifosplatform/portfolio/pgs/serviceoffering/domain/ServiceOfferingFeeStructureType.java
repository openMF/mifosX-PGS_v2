package org.mifosplatform.portfolio.pgs.serviceoffering.domain;

public enum ServiceOfferingFeeStructureType {

    ASYOUGO(0, "serviceOfferingFeeStructureType.asyougo"), //
    UPFRONT(100, "serviceOfferingFeeStructureType.upfront");

    private final Integer value;
    private final String code;

    public static ServiceOfferingFeeStructureType fromInt(final Integer statusValue) {

        ServiceOfferingFeeStructureType enumeration = ServiceOfferingFeeStructureType.ASYOUGO;
        switch (statusValue) {
            case 100:
                enumeration = ServiceOfferingFeeStructureType.UPFRONT;
            break;
        }
        return enumeration;
    }

    private ServiceOfferingFeeStructureType(final Integer value, final String code) {
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