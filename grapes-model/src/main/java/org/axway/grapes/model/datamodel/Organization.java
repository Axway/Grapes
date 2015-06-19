package org.axway.grapes.model.datamodel;
//todo used yb the plugin neds to stay the same

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Organization Model Class
 *
 *
 * <P> Model Objects are used in the communication with the Grapes server.
 * These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class Organization {

    private String name;

    private List<String> corporateGroupIdPrefixes = new ArrayList<String>();

    public Organization() {

    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getCorporateGroupIdPrefixes() {
        return corporateGroupIdPrefixes;
    }

    public void setCorporateGroupIdPrefixes(final List<String> corporateGroupIdPrefixes) {
        this.corporateGroupIdPrefixes = corporateGroupIdPrefixes;
    }

    /**
     * Checks if the organization is the same than an other one.
     *
     * @param obj Object
     * @return <tt>true</tt> only if grId/arId/classifier/version are the same in both.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Organization){
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
    //todo why is this null?
        sb.append("name: ");
        sb.append(name);

        Collections.sort(corporateGroupIdPrefixes);
        sb.append(", corporateGroupIds:");
        for(String corporateGroupId : corporateGroupIdPrefixes){
            sb.append(" ");
            sb.append(corporateGroupId);
        }

        return sb.toString();
    }
}
