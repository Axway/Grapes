package org.axway.grapes.server.webapp.views.utils;

import java.util.ArrayList;

/**
 * Row
 *
 * <p>Row is a utility class that represent a list of element contained in a row of a table.</p>
 *
 * @author jdcoffre
 */
public class Row extends ArrayList<Object>{


    public Row(final Object... cells) {
        for(Object cell: cells){
            add(cell);
        }
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();
        for(Object cell: this){
            sb.append(String.valueOf(cell));
        }

        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Row){
            return this.hashCode() == obj.hashCode();
        }

        return false;
    }
}