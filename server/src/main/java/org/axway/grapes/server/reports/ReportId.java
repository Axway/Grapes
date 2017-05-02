package org.axway.grapes.server.reports;

/**
 * Created by mganuci on 5/2/17.
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
