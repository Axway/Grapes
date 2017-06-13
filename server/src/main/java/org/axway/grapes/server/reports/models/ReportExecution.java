package org.axway.grapes.server.reports.models;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ReportExecution {

    private static final Logger LOG = LoggerFactory.getLogger(ReportExecution.class);


    private ReportRequest request;
    private int sortingColumn = 0;

    public String[] getResultColumnNames() {
        return resultColumnNames;
    }

    private String[] resultColumnNames;
    private List<String[]> tabularData;

    public ReportExecution(final ReportRequest request, final String[] colNames) {
        this.request = request;
        this.resultColumnNames = colNames;

        tabularData = new ArrayList<>();
    }

    public void setSortingColumn(int index) {
        if(index >= resultColumnNames.length) {
            throw new IllegalArgumentException("Invalid sorting column");
        }

        this.sortingColumn = index;
    }

    public ReportRequest getRequest() {
        return request;
    }

    public List<String[]> getData() {
        sortByColumn(sortingColumn);
        return Collections.unmodifiableList(tabularData);
    }

    public void addResultRow(String[] row) {
        if(row.length < resultColumnNames.length) {
            LOG.warn(String.format("Required column count: %s, got: %s", resultColumnNames.length, row.length));
            throw new IllegalArgumentException("Invalid row data");
        }

        tabularData.add(row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportExecution that = (ReportExecution) o;

        if (request != null ? !request.equals(that.request) : that.request != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(resultColumnNames, that.resultColumnNames)) return false;
        if(tabularData != null && that.tabularData != null) {
            return tabularDataEquals(tabularData, that.tabularData);
        }

        return (tabularData == null && that.tabularData == null);
    }

    private boolean tabularDataEquals(List<String[]> t1, List<String[]> t2) {
        if(t1.size() != t2.size())
            return false;

        final List<String> c1 = t1.stream().map(strArray -> StringUtils.join(strArray, ',')).collect(Collectors.toList());
        final List<String> c2 = t2.stream().map(strArray -> StringUtils.join(strArray, ',')).collect(Collectors.toList());

        c1.removeAll(c2);

        return c1.isEmpty();
    }

    @Override
    public int hashCode() {
        int result = request != null ? request.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(resultColumnNames);
        result = 31 * result + (tabularData != null ? tabularData.hashCode() : 0);
        return result;
    }

    private void sortByColumn(int index) {
        tabularData.sort(Comparator.comparing(t -> t[index]));
    }

}
