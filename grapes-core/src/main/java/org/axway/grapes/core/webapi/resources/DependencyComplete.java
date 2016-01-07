package org.axway.grapes.core.webapi.resources;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Scope;

/**
 * Created by jennifer on 6/11/15.
 */
public class DependencyComplete {
        private String sourceName;
        private String sourceVersion;
        private Artifact target;
        private Scope scope;

        public DependencyComplete() {

        }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public Artifact getTarget() {
        return target;
    }

    public void setTarget(Artifact target) {
        this.target = target;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /**
         * Checks if the dependency is the same than an other one.
         *
         * @param obj Object
         * @return <tt>true</tt> only if artifact/scope are the same in both.
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof DependencyComplete) {
                return hashCode() == obj.hashCode();
            }

            return false;
        }

        @Override
        public int hashCode() {
            final StringBuilder sb = new StringBuilder();

            sb.append(sourceName);
            sb.append(sourceVersion);
            sb.append(target.getGroupId());
            sb.append(target.getArtifactId());
            sb.append(target.getClassifier());
            sb.append(target.getVersion());
            sb.append(target.getType());
            sb.append(scope.toString());

            return sb.toString().hashCode();
        }
    }



