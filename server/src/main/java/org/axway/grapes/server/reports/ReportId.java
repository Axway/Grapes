package org.axway.grapes.server.reports;

/**
 * Enumeration created for type safety of the report id identification
 */
public enum ReportId {
    LICENSES_PER_PRODUCT_RELEASE(1),
    DIFFS_PER_PRODUCT_RELEASE(2),
    LICENSE_ON_PRODUCT_RELEASES(3),
    THIRD_PARTY_ARTIFACTS(4),
    MULTIPLE_LICENSE_MATCHING_STRINGS(5),
    EMPTY_LICENSE_ARTIFACTS(6),
    MOST_USED_LICENSES(7);

    private int id;
    ReportId(int reportId) {
        this.id = reportId;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
