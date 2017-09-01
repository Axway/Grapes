package org.axway.grapes.server.db;


import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.datamodel.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModelMapperTest {

    @Test
    public void testGetDbLicense() throws Exception {
        final License license = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url");
        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final DbLicense dbLicense = modelMapper.getDbLicense(license);

        assertEquals(license.getName(), dbLicense.getName());
        assertEquals(license.getLongName(), dbLicense.getLongName());
        assertEquals(license.getComments(), dbLicense.getComments());
        assertEquals(license.getRegexp(), dbLicense.getRegexp());
        assertEquals(license.getUrl(), dbLicense.getUrl());

    }

    @Test
    public void testGetLicense() throws Exception {
        final DbLicense dbLicense = new DbLicense() ;
        dbLicense.setName("name");
        dbLicense.setLongName("long name");
        dbLicense.setComments("comment");
        dbLicense.setRegexp("regexp");
        dbLicense.setUrl("url");

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final License license = modelMapper.getLicense(dbLicense);

        assertEquals(dbLicense.getName(), license.getName());
        assertEquals(dbLicense.getLongName(), license.getLongName());
        assertEquals(dbLicense.getComments(), license.getComments());
        assertEquals(dbLicense.getRegexp(), license.getRegexp());
        assertEquals(dbLicense.getUrl(), license.getUrl());

    }

    @Test
    public void testGetUnknownLicense() throws Exception {
        final DbLicense dbLicense = new DbLicense() ;
        dbLicense.setName("name");
        dbLicense.setLongName("");
        dbLicense.setComments("");
        dbLicense.setRegexp("");
        dbLicense.setUrl("");

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final License license = modelMapper.getLicense(dbLicense);

        assertEquals(true, license.isUnknown());

    }

    @Test
    public void testGetDbArtifact(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        artifact.setSize("10Mo");
        artifact.setDownloadUrl("http://www.nowhere.com");
        artifact.setProvider("http://www.nowhere.com/provider");

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final DbArtifact dbArtifact = modelMapper.getDbArtifact(artifact);

        assertEquals(artifact.getGroupId(), dbArtifact.getGroupId());
        assertEquals(artifact.getArtifactId(), dbArtifact.getArtifactId());
        assertEquals(artifact.getVersion(), dbArtifact.getVersion());
        assertEquals(artifact.getClassifier(), dbArtifact.getClassifier());
        assertEquals(artifact.getType(), dbArtifact.getType());
        assertEquals(artifact.getExtension(), dbArtifact.getExtension());
        assertEquals(artifact.getSize(), dbArtifact.getSize());
        assertEquals(artifact.getDownloadUrl(), dbArtifact.getDownloadUrl());
        assertEquals(artifact.getProvider(), dbArtifact.getProvider());

    }

    @Test
    public void testGetArtifact(){
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("groupId");
        dbArtifact.setArtifactId("artifactId");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("component");
        dbArtifact.setExtension("jar");
        dbArtifact.setDownloadUrl("nowhere");
        dbArtifact.setSize("10Mo");
        dbArtifact.setProvider("provider");

        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        dbArtifact.addLicense(license);

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final Artifact artifact = modelMapper.getArtifact(dbArtifact);

        assertEquals(dbArtifact.getGroupId(), artifact.getGroupId());
        assertEquals(dbArtifact.getArtifactId(), artifact.getArtifactId());
        assertEquals(dbArtifact.getVersion(), artifact.getVersion());
        assertEquals(dbArtifact.getClassifier(), artifact.getClassifier());
        assertEquals(dbArtifact.getType(), artifact.getType());
        assertEquals(dbArtifact.getExtension(), artifact.getExtension());
        assertEquals(dbArtifact.getSize(), artifact.getSize());
        assertEquals(dbArtifact.getDownloadUrl(), artifact.getDownloadUrl());
        assertEquals(dbArtifact.getProvider(), artifact.getProvider());
        assertEquals(1, artifact.getLicenses().size());
        assertEquals("licenseId", artifact.getLicenses().get(0));
    }

    @Test
    public void getDbModule(){
        final Module module = DataModelFactory.createModule("root", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("com.axway.root", "artifact1", "1.0.0-SNAPSHOT", "win", "component", "jar");
        module.addArtifact(artifact);

        final Artifact thirdparty = DataModelFactory.createArtifact("org.apache", "all", "6.8.0-5426", "", "", "jar");
        final Dependency dependency = DataModelFactory.createDependency(thirdparty, Scope.COMPILE);
        module.addDependency(dependency);

        final Module submodule = DataModelFactory.createModule("sub1", "1.0.0-SNAPSHOT");
        final Artifact artifact2 = DataModelFactory.createArtifact("com.axway.root.sub1", "artifactSub1", "1.0.0-SNAPSHOT", "", "", "jar");
        submodule.addArtifact(artifact2);
        final Artifact thirdparty2 = DataModelFactory.createArtifact("org.lol", "all", "1.2.3-4", "", "", "jar");
        final Dependency dependency2 = DataModelFactory.createDependency(thirdparty2, Scope.PROVIDED);
        submodule.addDependency(dependency2);
        module.addSubmodule(submodule);

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final DbModule dbModule = modelMapper.getDbModule(module);
        assertEquals(module.getName(), dbModule.getName());
        assertEquals(module.getVersion(), dbModule.getVersion());
        assertEquals(1, dbModule.getArtifacts().size());
        assertEquals(artifact.getGavc(), dbModule.getArtifacts().get(0));
        assertEquals(1, dbModule.getDependencies().size());
        assertEquals(thirdparty.getGavc(), dbModule.getDependencies().get(0).getTarget());
        assertEquals(DbModule.generateID(module.getName(), module.getVersion()), dbModule.getDependencies().get(0).getSource());
        assertEquals(dependency.getScope(), dbModule.getDependencies().get(0).getScope());
        assertEquals(1, dbModule.getSubmodules().size());

        final DbModule dbSubmodule = dbModule.getSubmodules().get(0);
        assertEquals(submodule.getName() , dbSubmodule.getName());
        assertEquals(submodule.getVersion(), dbSubmodule.getVersion());
        assertEquals(1, dbSubmodule.getArtifacts().size());
        assertEquals(artifact2.getGavc(), dbSubmodule.getArtifacts().get(0));
        assertEquals(1, dbSubmodule.getDependencies().size());
        assertEquals(thirdparty2.getGavc(), dbSubmodule.getDependencies().get(0).getTarget());
        assertEquals(DbModule.generateID(submodule.getName(), submodule.getVersion()), dbSubmodule.getDependencies().get(0).getSource());
        assertEquals(dependency2.getScope(), dbSubmodule.getDependencies().get(0).getScope());

    }

    @Test
    public void getModule(){
        final DbModule dbModule = new DbModule();
        dbModule.setName("root");
        dbModule.setVersion("1.0.0-SNAPSHOT");

        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("com.axway.root");
        dbArtifact.setArtifactId("artifact1");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("component");
        dbArtifact.setExtension("jar");
        dbModule.addArtifact(dbArtifact);

        final DbArtifact dbThirdParty = new DbArtifact();
        dbThirdParty.setGroupId("org.apache");
        dbThirdParty.setArtifactId("all");
        dbThirdParty.setVersion("6.8.0-5426");
        dbThirdParty.setType("jar");
        dbThirdParty.setExtension("jar");

        dbModule.addDependency(dbThirdParty.getGavc(), Scope.COMPILE);

        final DbModule dbSubmodule = new DbModule();
        dbSubmodule.setSubmodule(true);
        dbSubmodule.setName("sub1");
        dbSubmodule.setVersion("1.0.0-SNAPSHOT");

        final DbArtifact dbArtifact2 = new DbArtifact();
        dbArtifact2.setGroupId("com.axway.root.sub1");
        dbArtifact2.setArtifactId("artifactSub1");
        dbArtifact2.setVersion("1.0.0-SNAPSHOT");
        dbArtifact2.setExtension("jar");
        dbSubmodule.addArtifact(dbArtifact2);

        final DbArtifact dbThirdParty2 = new DbArtifact();
        dbThirdParty2.setGroupId("org.lol");
        dbThirdParty2.setArtifactId("all");
        dbThirdParty2.setVersion("1.2.3-4");
        dbThirdParty2.setExtension("jar");

        dbSubmodule.addDependency(dbThirdParty2.getGavc(), Scope.PROVIDED);
        dbModule.addSubmodule(dbSubmodule);

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);
        when(repositoryHandler.getArtifact(dbArtifact2.getGavc())).thenReturn(dbArtifact2);
        when(repositoryHandler.getArtifact(dbThirdParty.getGavc())).thenReturn(dbThirdParty);
        when(repositoryHandler.getArtifact(dbThirdParty2.getGavc())).thenReturn(dbThirdParty2);

        final ModelMapper modelMapper = new ModelMapper(repositoryHandler);
        final Module module = modelMapper.getModule(dbModule);

        assertEquals(dbModule.getName(), module.getName());
        assertEquals(dbModule.getVersion(), module.getVersion());
        assertEquals(1, module.getArtifacts().size());
        assertEquals(dbArtifact.getGavc(), module.getArtifacts().iterator().next().getGavc());
        assertEquals(1, module.getDependencies().size());

        final Dependency thirdParty = module.getDependencies().iterator().next();
        assertEquals(dbThirdParty.getGavc(), thirdParty.getTarget().getGavc());
        assertEquals(Scope.COMPILE, thirdParty.getScope());
        assertEquals(dbModule.getName(), thirdParty.getSourceName());
        assertEquals(dbModule.getVersion(), thirdParty.getSourceVersion());

        assertEquals(1, module.getSubmodules().size());
        final Module submodule = module.getSubmodules().iterator().next();

        assertEquals(dbSubmodule.getName() , submodule.getName());
        assertEquals(dbSubmodule.getVersion(), submodule.getVersion());
        assertEquals(1, submodule.getArtifacts().size());
        assertEquals(dbArtifact2.getGavc(), submodule.getArtifacts().iterator().next().getGavc());
        assertEquals(1, submodule.getDependencies().size());
        assertEquals(dbThirdParty2.getGavc(), submodule.getDependencies().iterator().next().getTarget().getGavc());
        assertEquals(dbSubmodule.getName(), submodule.getDependencies().iterator().next().getSourceName());
        assertEquals(dbSubmodule.getVersion(), submodule.getDependencies().iterator().next().getSourceVersion());
        assertEquals(Scope.PROVIDED, submodule.getDependencies().iterator().next().getScope());

    }

    @Test
    public void getDbOrganizationFromOrganization(){
        final Organization organization = DataModelFactory.createOrganization("test");
        organization.getCorporateGroupIdPrefixes().add("com.test");

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final DbOrganization dbOrganization = modelMapper.getDbOrganization(organization);

        assertEquals(organization.getName(), dbOrganization.getName());
        assertEquals(1, dbOrganization.getCorporateGroupIdPrefixes().size());
        assertEquals(organization.getCorporateGroupIdPrefixes().get(0), dbOrganization.getCorporateGroupIdPrefixes().get(0));
    }

    @Test
    public void getOrganizationFromDbOrganization(){
        final DbOrganization dbOrganization = new DbOrganization();
        dbOrganization.setName("test");
        dbOrganization.getCorporateGroupIdPrefixes().add("com.test");

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final Organization organization = modelMapper.getOrganization(dbOrganization);

        assertEquals(dbOrganization.getName(), organization.getName());
        assertEquals(1, organization.getCorporateGroupIdPrefixes().size());
        assertEquals(dbOrganization.getCorporateGroupIdPrefixes().get(0), organization.getCorporateGroupIdPrefixes().get(0));
    }

    @Test
    public void getDependencyFromDbDependency(){
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("org.axway.grapes");
        dbArtifact.setArtifactId("artifact1");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setExtension("jar");

        final DbDependency dbDependency = new DbDependency();
        dbDependency.setScope(Scope.RUNTIME);
        dbDependency.setTarget(dbArtifact.getGavc());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getArtifact(dbArtifact.getGavc())).thenReturn(dbArtifact);

        final ModelMapper modelMapper = new ModelMapper(repositoryHandler);
        final Dependency dependency = modelMapper.getDependency(dbDependency, "sourceName", "123456");

        assertEquals(dbDependency.getTarget(), dependency.getTarget().getGavc());
        assertEquals(dbDependency.getScope(), dependency.getScope());
        assertEquals("sourceName", dependency.getSourceName());
        assertEquals("123456", dependency.getSourceVersion());
    }

    @Test
    public void getCommentFromDbComment() throws Exception {
        final String entityId = "com.axway.test:1.0.0::jar";
        final String entityType = "DbArtifact";
        DbComment dbComment = new DbComment();
        dbComment.setEntityId(entityId);
        dbComment.setEntityType(entityType);
        dbComment.setDbCommentText("test comment");
        dbComment.setDbCommentedBy("testUser");
        dbComment.setDbCreatedDateTime(new Date());

        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final ModelMapper modelMapper = new ModelMapper(repositoryHandler);

        final Comment comment = modelMapper.getComment(dbComment);

        assertEquals(entityId, comment.getEntityId());
        assertEquals(entityType, comment.getEntityType());
        assertEquals("test comment", comment.getCommentText());
        assertEquals("testUser", comment.getCommentedBy());

    }

    @Test
    public void getDbCommentFromComment() throws Exception {
        final String entityId = "com.axway.test:1.0.0::jar";
        final String entityType = "DbArtifact";
        final Comment comment = DataModelFactory.createComment(entityId, entityType,
                "test action","test comment", "testUser", new Date());

        final ModelMapper modelMapper = new ModelMapper(mock(RepositoryHandler.class));
        final DbComment dbComment = modelMapper.getDbComment(comment);

        assertEquals(entityId, dbComment.getEntityId());
        assertEquals(entityType, dbComment.getEntityType());
        assertEquals("test comment", dbComment.getDbCommentText());
        assertEquals("test action", dbComment.getAction());
        assertEquals("testUser", dbComment.getDbCommentedBy());
    }
}
