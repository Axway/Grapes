package org.axway.grapes.core.handler;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.Filter;
import org.axway.grapes.core.options.filters.ModuleNameFilter;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.DependencyService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.axway.grapes.model.datamodel.Scope;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ModuleHandlerTest extends WisdomTest {
    @Inject
    ModuleService moduleService;

    @Inject
    ArtifactService artifactService;

    @Inject
    LicenseService licenseService;

    @Inject
    OrganizationService organizationService;

    @Inject
    @org.wisdom.test.parents.Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbModule)")
    Crud<Module, String> moduleStringCrud;

    @Inject
  DependencyService dependencyService;

    @Before
    public void clearDBCollection() {
        Iterable<Module> list = moduleStringCrud.findAll();
        moduleStringCrud.delete(list);
    }

    @Test
    public void checkStoreModule() {
        final Module module = new Module();
        module.setName("module1");
        moduleService.store(module);
        Module module1 = moduleService.getModule(module.getId());
        assertThat(module1).isNotNull();
        assertThat(module1.getName()).isEqualTo("module1");
        assertThat(module1.getId()).isEqualTo(module.getId());
    }

    @Test
    public void checkGetAllModuleNames() {
        final Module module = new Module();
        module.setName("module1");
        moduleService.store(module);
        final FiltersHolder filters = mock(FiltersHolder.class);
        List<String> list = moduleService.getModuleNames(filters);
        assertThat(list).hasSize(1);
        assertThat(list).contains("module1");
    }

    @Test
    public void getVersionsOfAModule() {
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");
        moduleService.store(module);
        final FiltersHolder filters = mock(FiltersHolder.class);
        List<String> versions = moduleService.getModuleVersions(module.getName(), filters);
        assertThat(versions).isNotNull();
        assertThat(versions.size()).isEqualTo(1);
        assertThat(versions).contains(module.getVersion());
    }

    @Test(expected = NoSuchElementException.class)
    public void getVersionsOfAModuleWhichDoesNotExist() {
        final FiltersHolder filters = mock(FiltersHolder.class);
        List<String> list = moduleService.getModuleVersions("doesNotExist", filters);
    }

    @Test
    public void getAModule() {
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");
        moduleService.store(module);
        final Module gotModule = moduleService.getModule(module.getId());
        assertThat(gotModule).isNotNull();
        assertThat(module.getId()).isEqualTo(gotModule.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void getAModuleThatDoesNotExist() {
        moduleService.getModule("doesNotExist");
    }

   // @Test
    //todo dependecy stuff
    public void deleteAModule() {
         Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");

         Artifact artifact1 = new Artifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        module.addArtifact(artifact1);

          Artifact dependency = new Artifact();
        dependency.setArtifactId("dependency");
        dependency.setVersion("1.0.0-1");
        module.addDependency(dependency, Scope.COMPILE);
         Module subModule = new Module();
        subModule.setName("sub-module");
        subModule.setVersion("1.0.0-SNAPSHOT");

        module.addSubmodule(subModule);
        moduleService.store(subModule);
        moduleService.store(module);
        artifactService.store(artifact1);



        Artifact artifact2 = new Artifact();
        artifact2.setArtifactId("artifact2");
        artifact2.setVersion("1.0.0-SNAPSHOT");
        subModule.addArtifact(artifact2);
        artifactService.store(artifact2);
        moduleService.store(subModule);

       final Module module1 = moduleService.getModule(module.getId());
        final Module subModule1 =moduleService.getModule(subModule.getId());
        final Artifact artifact1fromdb= artifactService.getArtifact(artifact1.getGavc());
       final Artifact artifact2fromdb = artifactService.getArtifact(artifact2.getGavc());
      //  dependency =artifactService.getArtifact(dependency.getGavc());
      moduleService.deleteModule(module.getId());
//        verify(moduleService, times(1)).deleteModule(module.getId());
//        verify(moduleService, never()).deleteModule(subModule.getId());
//        verify(artifactService, times(1)).deleteArtifact(artifact1.getGavc());
//        verify(artifactService, times(1)).deleteArtifact(artifact2.getGavc());
//        verify(artifactService, never()).deleteArtifact(dependency.getGavc());
        assertThatThrownBy(()->moduleService.getModule(module1.getId())).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(()->moduleService.getModule(subModule1.getId())).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(()->artifactService.getArtifact(artifact1fromdb.getGavc())).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(()->artifactService.getArtifact(artifact2fromdb.getGavc())).isInstanceOf(NoSuchElementException.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteAModuleThatDoesNotExist() {
        moduleService.deleteModule("doesNotExist");
    }

    @Test
    //todo check logig for promoted
    public void promoteAModule() {
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");
         Artifact artifact = new Artifact();
        artifact.setArtifactId("artifact1");
        artifact.setVersion("1.0.0-SNAPSHOT");
        module.addArtifact(artifact);
        moduleService.store(module);
        artifactService.store(artifact);
        Module module1 = moduleService.getModule(module.getId());

        moduleService.promoteModule(module.getId());
        assertThat(moduleService.getModule(module1.getId()).isPromoted()).isTrue();
        artifact = artifactService.getArtifact(artifact.getGavc());
        assertThat(artifact.isPromoted()).isTrue();
    }

    @Test(expected = NoSuchElementException.class)
    public void promoteAModuleThatDoesNotExist() {
        moduleService.promoteModule("doesNotExist");
    }

    @Test
    public void getModuleLicenses() {
        final Module module = new Module();
        module.setName("module");
        module.setVersion("1.0.0-SNAPSHOT");
        final Artifact artifact1 = new Artifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        final License license = new License();
        license.setName("test");
        license.setLongName("License for Grapes tests");
        artifact1.addLicense(license.getName());
        module.addArtifact(artifact1);
        licenseService.store(license);
        artifactService.store(artifact1);
        moduleService.store(module);
        Module module1 = moduleService.getModule(module.getId());
        Artifact artifact11 = artifactService.getArtifact(artifact1.getGavc());
        License license1 = licenseService.getLicense(license.getName());
        final List<License> licenses = moduleService.getModuleLicenses(module.getId());
        assertThat(licenses).isNotNull();
        assertThat(licenses.size()).isEqualTo(1);
        assertThat(license.getName()).isEqualTo(licenses.get(0).getName());
    }

    @Test
    public void getModule() {
        Module module = new Module();
        module.setName("testmoodule");
        module.setVersion("1");
        moduleService.store(module);
        module.setVersion("2");
        moduleService.store(module);
        final FiltersHolder filters = new FiltersHolder();
        Filter filter = new ModuleNameFilter("testmodule");
        List<Module> list = moduleService.getModules(filters);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    //todo finish this
    public void getModuleDependencies(){
        Module module = new Module();
        module.setName("arf2");
        module.setVersion("1.0.0-SNAPSHOT");
        module.setPromoted(false);
        module.setOrganization("happy");

        Artifact artifact1 = new Artifact();
        artifact1.setArtifactId("artifact1");
        artifact1.setVersion("1.0.0-SNAPSHOT");
        artifact1.setGroupId("org.happy");
        module.addArtifact(artifact1);

        Artifact dependency = new Artifact();
        dependency.setArtifactId("dependency");
        dependency.setVersion("1.0.0-1");
        dependency.setGroupId("org.happy");
        module.addDependency(dependency, Scope.COMPILE);
        Module subModule = new Module();
        subModule.setName("sub-module");
        subModule.setVersion("1.0.0-SNAPSHOT");
        Organization organization = new Organization();
        organization.setName("happy");
        List<String> orgs = new ArrayList<>();
        orgs.add("org.happy");
        organization.setCorporateGroupIdPrefixes(orgs);
        organizationService.store(organization);

        module.addSubmodule(subModule);
        moduleService.store(subModule);
        moduleService.store(module);
        artifactService.store(artifact1);
        artifactService.store(dependency);



        Artifact artifact2 = new Artifact();
        artifact2.setArtifactId("artifact2");
        artifact2.setVersion("1.0.0-SNAPSHOT");
        subModule.addArtifact(artifact2);
        artifactService.store(artifact2);
        moduleService.store(subModule);

        final Module module1 = moduleService.getModule(module.getId());
        final Module subModule1 =moduleService.getModule(subModule.getId());
        final Artifact artifact1fromdb= artifactService.getArtifact(artifact1.getGavc());
        final Artifact artifact2fromdb = artifactService.getArtifact(artifact2.getGavc());
        dependency =artifactService.getArtifact(dependency.getGavc());
       List<Dependency> list= dependencyService.getModuleDependencies(module.getId(), new FiltersHolder());
        System.out.println(list.get(0));
    }
}
