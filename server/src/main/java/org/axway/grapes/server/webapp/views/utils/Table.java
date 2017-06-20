package org.axway.grapes.server.webapp.views.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Table
 *
 * <p>Utility class that displays a table of element</p>
 *
 * @author jdcoffre
 */
public class Table {

    private final Row headers;
    private final List<Row> rows = new ArrayList<>();

    public Table(final String... headers){
        this.headers = new Row((Object[]) headers);
    }

    public Row getHeaders(){
        return headers;
    }

    /**
     * Add a row to the table if it does not already exist
     *
     * @param cells String...
     */
    public void addRow(final String... cells){
        final Row row = new Row((Object[]) cells);

        if(!rows.contains(row)){
            rows.add(row);
        }
    }

    public List<Row> getRows(){
        return rows;
    }

    public int size(){
        return rows.size();
    }

}
