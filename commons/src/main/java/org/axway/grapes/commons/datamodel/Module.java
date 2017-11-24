package org.axway.grapes.commons.datamodel;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Module Model Class
 *
 *
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class Module {

    private String name;
    private String version;

    private boolean promoted = false;
    private boolean isSubmodule = false;

    private Set<Artifact> artifacts = new HashSet<Artifact>();
    private Set<Dependency> dependencies = new HashSet<Dependency>();
    private Set<Module> submodules = new HashSet<Module>();

    private Date createdDateTime = null;
    private Date updatedDateTime = null;

    // productLogicalName and deliveries are used in case of commercial delivery
    private String productLogicalName;
    private Delivery deliveries;
    
    protected Module() {
        // Must be instantiated via the DataModelObjectFactory
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public Set<Module> getSubmodules() {
        return submodules;
    }

    public boolean isSubmodule() {
        return isSubmodule;
    }

    public void setSubmodule(final boolean isSubmodule) {
        this.isSubmodule = isSubmodule;
    }

    public Date getCreatedDateTime() { return createdDateTime; }

    public void setCreatedDateTime(Date createdDateTime) { this.createdDateTime = createdDateTime; }

    public Date getUpdatedDateTime() { return updatedDateTime; }

    public void setUpdatedDateTime(Date updatedDateTime) { this.updatedDateTime = updatedDateTime; }

    /**
     * Sets the promotion state.
     *
     * <P>INFO: This method updates automatically all the contained artifacts.
     *
     * @param promoted boolean
     */
    public void setPromoted(final boolean promoted) {
        this.promoted = promoted;

        for (final Artifact artifact : artifacts) {
            artifact.setPromoted(promoted);
        }

        for (final Module suModule : submodules) {
            suModule.setPromoted(promoted);
        }
    }

    /**
     * Add a dependency to the module.
     *
     * @param dependency Dependency
     */
    public void addDependency(final Dependency dependency) {
        if(dependency != null && !dependencies.contains(dependency)){
            this.dependencies.add(dependency);
        }
    }

    /**
     * Adds a submodule to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added submodule will be promoted.
     *
     * @param submodule Module
     */
    public void addSubmodule(final Module submodule) {
        if (!submodules.contains(submodule)) {
            submodule.setSubmodule(true);

            if (promoted) {
                submodule.setPromoted(promoted);
            }

            submodules.add(submodule);
        }
    }

    /**
     * Adds an artifact to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added artifacts will be promoted.
     *
     * @param artifact Artifact
     */
    public void addArtifact(final Artifact artifact) {
        if (!artifacts.contains(artifact)) {
            if (promoted) {
                artifact.setPromoted(promoted);
            }

            artifacts.add(artifact);
        }
    }

    /**
     * Adds a set of artifact to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added artifacts will be promoted.
     *
     * @param artifacts Listof Artifact
     */
    public void addAllArtifacts(final List<Artifact> artifacts) {
        for (final Artifact artifact : artifacts) {
            addArtifact(artifact);
        }
    }

    /**
     * Checks if the module is the same than an other one.
     *
     * @param obj Object
     * @return <tt>true</tt> only if name/version are the same in both.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Module) {
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(name);
        sb.append(version);

        return sb.toString().hashCode();
    }
    

    /**
     * 
     * @return <tt>0</tt> if delivery info doesn't exist.
     * @return <tt>1</tt> if wrong delivery info entered.
     * @return <tt>2</tt> if delivery info exist.
     */
    
	public int deliveryStatusCount() {
		if(this.deliveries==null){
			return 0;
		}
		int count = 0;
		if(this.deliveries.getCommercialName()!=null && !this.deliveries.getCommercialName().isEmpty()){
			count++;
        }
        if(this.deliveries.getCommercialVersion()!=null && !this.deliveries.getCommercialVersion().isEmpty()){
        	count++;
        }        
	    return count;
	    
	 }

	public String getProductLogicalName() {
		return productLogicalName;
	}
	public void setProductLogicalName(String productLogicalName) {
		this.productLogicalName = productLogicalName;
	}
	
	public Delivery getDeliveries() {
		return deliveries;
	}
	public void setDeliveries(Delivery deliveries) {
		this.deliveries = deliveries;
	}

}