package org.axway.grapes.server.core.options;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.db.datamodel.DbDependency;

import javax.ws.rs.core.MultivaluedMap;

public class ScopeHandler {

    /** Value - {@value}, boolean query parameter used to filter dependencies.
     * Default value: true. */
    private Boolean scopeComp = true;

    /** Value - {@value}, boolean query parameter used to filter dependencies.
     * Default value: false. */
    private Boolean scopeRun = false;

    /** Value - {@value}, boolean query parameter used to filter dependencies.
     * Default value: true. */
    private Boolean scopePro = true;

    /** Value - {@value}, boolean query parameter used to filter dependencies.
     * Default value: false. */
    private Boolean scopeTest = false;

    public ScopeHandler(){
        // Default value init
    }

    /**
     * The parameter must never be null
     *
     * @param queryParameters
     */
    public void init(final MultivaluedMap<String, String> queryParameters) {
        final String scopeCompileParam = queryParameters.getFirst(ServerAPI.SCOPE_COMPILE_PARAM);
        if(scopeCompileParam != null){
            this.scopeComp = Boolean.valueOf(scopeCompileParam);
        }
        final String scopeProvidedParam = queryParameters.getFirst(ServerAPI.SCOPE_PROVIDED_PARAM);
        if(scopeProvidedParam != null){
            this.scopePro = Boolean.valueOf(scopeProvidedParam);
        }
        final String scopeRuntimeParam = queryParameters.getFirst(ServerAPI.SCOPE_RUNTIME_PARAM);
        if(scopeRuntimeParam != null){
            this.scopeRun = Boolean.valueOf(scopeRuntimeParam);
        }
        final String scopeTestParam = queryParameters.getFirst(ServerAPI.SCOPE_TEST_PARAM);
        if(scopeTestParam != null){
            this.scopeTest = Boolean.valueOf(scopeTestParam);
        }
    }

    public boolean filter(final DbDependency datamodelObj) {
        if(!scopeComp && ((DbDependency)datamodelObj).getScope().equals(Scope.COMPILE)){
            return false;
        }
        if(!scopePro && ((DbDependency)datamodelObj).getScope().equals(Scope.PROVIDED)){
            return false;
        }
        if(!scopeRun && ((DbDependency)datamodelObj).getScope().equals(Scope.RUNTIME)){
            return false;
        }
        if(!scopeTest && ((DbDependency)datamodelObj).getScope().equals(Scope.TEST)){
            return false;
        }

        return true;
    }

    public Boolean getScopeComp() {
        return scopeComp;
    }

    public void setScopeComp(final Boolean scopeComp) {
        this.scopeComp = scopeComp;
    }

    public Boolean getScopeRun() {
        return scopeRun;
    }

    public void setScopeRun(final Boolean scopeRun) {
        this.scopeRun = scopeRun;
    }

    public Boolean getScopePro() {
        return scopePro;
    }

    public void setScopePro(final Boolean scopePro) {
        this.scopePro = scopePro;
    }

    public Boolean getScopeTest() {
        return scopeTest;
    }

    public void setScopeTest(final Boolean scopeTest) {
        this.scopeTest = scopeTest;
    }
}
