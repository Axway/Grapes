package org.axway.grapes.core.options;
//todo remove souts

import org.axway.grapes.model.api.ServerAPI;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Scope;

import java.util.List;
import java.util.Map;

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
    //    public void init(final MultivaluedMap<String, String> queryParameters) {
        public void init(final Map<String, List<String>> queryParameters) {
            System.out.println("inside scope");
            final List<String> scopeCompileParam = queryParameters.get(ServerAPI.SCOPE_COMPILE_PARAM);
            System.out.println("inside scope1");
            if(scopeCompileParam != null){
                this.scopeComp = Boolean.valueOf(scopeCompileParam.get(0));
            }
            System.out.println("inside scope2");
            final List<String> scopeProvidedParam = queryParameters.get(ServerAPI.SCOPE_PROVIDED_PARAM);
            if(scopeProvidedParam != null){
                this.scopePro = Boolean.valueOf(scopeProvidedParam.get(0));
            }
            System.out.println("inside scope3");
            final List<String> scopeRuntimeParam = queryParameters.get(ServerAPI.SCOPE_RUNTIME_PARAM);
            if(scopeRuntimeParam != null){
                this.scopeRun = Boolean.valueOf(scopeRuntimeParam.get(0));
            }
            System.out.println("inside scope4");
            final List<String> scopeTestParam = queryParameters.get(ServerAPI.SCOPE_TEST_PARAM);
            if(scopeTestParam != null){
                this.scopeTest = Boolean.valueOf(scopeTestParam.get(0));
            }
        }


    public boolean filter(final Dependency datamodelObj) {
        if(!scopeComp && ((Dependency)datamodelObj).getScope().equals(Scope.COMPILE)){
            return false;
        }
        if(!scopePro && ((Dependency)datamodelObj).getScope().equals(Scope.PROVIDED)){
            return false;
        }
        if(!scopeRun && ((Dependency)datamodelObj).getScope().equals(Scope.RUNTIME)){
            return false;
        }
        if(!scopeTest && ((Dependency)datamodelObj).getScope().equals(Scope.TEST)){
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
