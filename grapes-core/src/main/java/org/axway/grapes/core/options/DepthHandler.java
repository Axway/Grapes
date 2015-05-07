package org.axway.grapes.core.options;

import org.axway.grapes.model.api.ServerAPI;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Depth Handler
 *
 * <p>Handle the dependency report depth.</p>
 *
 * <author>jdcoffre</author>
 */
public class DepthHandler {

    /** Value - {@value}, boolean query parameter that is used to get the result of the request going till the end of the dependency depth. Override depth parameter if exist.
     * Default value: false. */
    private Boolean fullRecursive = false;

    /** Value - {@value}, integer query parameter that is used to set a depth into the dependency result.
     * Default value: 1. */
    private Integer depth = 1;

    public Boolean getFullRecursive() {
        return fullRecursive;
    }

    public void setFullRecursive(final Boolean fullRecursive) {
        if(fullRecursive != null){
            this.fullRecursive = fullRecursive;
        }
    }

    private void setFullRecursive(final String recursif) {
        if(recursif != null){
            setFullRecursive(Boolean.valueOf(recursif));
        }
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(final Integer depth) {
        if(depth != null){
            this.depth = depth;
        }
    }

    private void setDepth(final String depth) {
        if(depth != null){
            setDepth(Integer.valueOf(depth));
        }
    }


    public void init(final MultivaluedMap<String, String> queryParameters) {
        setFullRecursive(queryParameters.getFirst(ServerAPI.RECURSIVE_PARAM));
        setDepth(queryParameters.getFirst(ServerAPI.DEPTH_PARAM));
    }


    /**
     * Check if the dependency inspection should go deeper regarding the filters
     *
     * @param depth
     * @return boolean true if the dependency introspection should go deeper
     */
    public boolean shouldGoDeeper(final int depth) {
        return fullRecursive || depth < this.depth;
    }

}
