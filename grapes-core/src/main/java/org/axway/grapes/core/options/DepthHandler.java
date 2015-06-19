package org.axway.grapes.core.options;

import org.axway.grapes.model.api.ServerAPI;

import java.util.List;
import java.util.Map;

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

    private void setFullRecursive(final List<String> recursif) {
        if(recursif != null){
            setFullRecursive(Boolean.valueOf(recursif.get(0)));
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

    private void setDepth(final List<String> depth) {
        if(depth != null){
            setDepth(Integer.valueOf(depth.get(0)));
        }
    }


    public void init(final Map<String, List<String>> queryParameters) {
        setFullRecursive(queryParameters.get(ServerAPI.RECURSIVE_PARAM));
        setDepth(queryParameters.get(ServerAPI.DEPTH_PARAM));
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
