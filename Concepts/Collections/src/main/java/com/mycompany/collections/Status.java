package com.mycompany.collections;

public enum Status {

    FAULTY("Faulty"),
    SILENT("Silent"),
    DEGRADED("Degraded"),
    REPORTING("Reporting"),
    UNKNOWN("Unknown");

    String status;

    Status(String status) {
        this.status = status;
    }

}
