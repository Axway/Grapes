package org.axway.grapes.server.reports;

/**
 * Enumeration created for type safety of the report id identification
 */
public enum ReportId {
    LICENSES_PER_PRODUCT_RELEASE(1);

    private int id;
    ReportId(int reportId) {
        this.id = reportId;
    }

    public int getId() {
        return this.id;
    }
}
