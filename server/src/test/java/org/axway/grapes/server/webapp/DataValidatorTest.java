package org.axway.grapes.server.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.reports.ReportsHandler;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataValidatorTest {

    @Test
    public void validateArtifact(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void artifactWithGroupIdNullIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact(null, "artifactId", "version", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void artifactWithGroupIdEmptyIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("", "artifactId", "version", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void artifactWithArtifactIdNullIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", null, "version", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void artifactWithArtifactIdEmptyIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "", "version", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void artifactWithVersionNullIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", null, null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        
        assertNotNull(exception);
    }

    @Test
    public void artifactWithVersionEmptyIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void artifactWithExtensionNullIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier" , null , null);
        WebApplicationException exception = null;

        try{
            DataValidator.validatePostArtifact(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }
    
    @Test
    public void artifactWithExtensionEmptyIsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier" , null , "");
        WebApplicationException exception = null;

        try{
            DataValidator.validatePostArtifact(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }
    
    @Test
    public void artifactWithNoSha256IsNotValid(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier" , "type" , "extension");
        WebApplicationException exception = null;

        try{
            DataValidator.validatePostArtifact(artifact);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void validateLicense(){
        final License license = DataModelFactory.createLicense("name", "longName", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void licenseWithNameNullIsNotValid(){
        final License license = DataModelFactory.createLicense(null, "longName", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void licenseWithNameEmptyIsNotValid(){
        final License license = DataModelFactory.createLicense("", "longName", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void licenseWithLongNameNullIsNotValid(){
        final License license = DataModelFactory.createLicense("name", null, null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void licenseWithLongNameEmptyIsNotValid(){
        final License license = DataModelFactory.createLicense("name", "", null, null, null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void licenseWithRegexpThatCompileIsValid(){
        final License license = DataModelFactory.createLicense("name", "longName", null, ".*", null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void licenseWithRegexpThatDoesNotCompileIsNotValid(){
        final License license = DataModelFactory.createLicense("name", "longName", null, "*", null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(license);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void validateModule(){
        final Module module = DataModelFactory.createModule("name", "version");
        module.addArtifact(DataModelFactory.createArtifact("groupId", "artifactId", "version", null, null, null));
        module.addSubmodule(DataModelFactory.createModule("sub-module", "version"));
        WebApplicationException exception = null;

        try{
            DataValidator.validate(module);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void moduleWithNameEmptyIsNotValid(){
        final Module module = DataModelFactory.createModule("", "version");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(module);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void moduleWithNameNullIsNotValid(){
        final Module module = DataModelFactory.createModule(null, "version");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(module);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void moduleWithVersionEmptyIsNotValid(){
        final Module module = DataModelFactory.createModule("name", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(module);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void moduleWithVersionNullIsNotValid(){
        final Module module = DataModelFactory.createModule("name", null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(module);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void validateAnOrganization(){
        final Organization organization = DataModelFactory.createOrganization("test");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(organization);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void organizationWithNameEmptyIsNotValid(){
        final Organization organization = DataModelFactory.createOrganization("");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(organization);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void organizationWithNameNullIsNotValid(){
        final Organization organization = DataModelFactory.createOrganization(null);
        WebApplicationException exception = null;

        try{
            DataValidator.validate(organization);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }
    @Test
    public void validateArtifactQuery() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", 0 , "file.jar", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNull(exception);
    }
    
    @Test
    public void artifactQueryWithMissingUser() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery(null, 0 , "file.jar", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }
    
    @Test
    public void artifactQueryWithInvalidStage() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", -1 , "file.jar", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }
    
    @Test
    public void artifactQueryWithMissingChecksum() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", 0 , "file.jar", null, "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }
    
    @Test
    public void artifactQueryWithMissingFilename() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", 0 , null, "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }
    
    @Test
    public void artifactQueryWithInvalidChecksum() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", 0 , "file.jar", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821Y", "ServicePack", "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }
    
    @Test
    public void artifactQueryWithMissingFiletype() throws IOException{
        final ArtifactQuery artifactQuery = DataModelFactory.createArtifactQuery("toto", 0 , "file.jar", "6554ed3d1ab007bd81d3d57ee27027510753d905277d5b5b8813e5bd516e821c", null, "");
        WebApplicationException exception = null;

        try{
            DataValidator.validate(artifactQuery);
        }
        catch (WebApplicationException e){
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void conflictingLicenseRegex(){

        //Added license
        final License license = DataModelFactory.createLicense("AFL3",
                "AFL3", "",
                "((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)", "");
        WebApplicationException exception = null;

        //Db license
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("AFL-3.0");
        dbLicense.setRegexp("((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)");

        //fake artifact
        final DbArtifact fakeArtifact = new DbArtifact();
        fakeArtifact.setGroupId("Fake artifact");
        fakeArtifact.setArtifactId("Fake artifact");
        fakeArtifact.setVersion("1");
        fakeArtifact.setLicenses(Arrays.asList("AFL-3.0"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(dbLicense));

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(fakeArtifact);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));


        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        try{
            ReportsRegistry.init();
            DataValidator.validateLicensePattern(license, licenseHandler);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void validLicenseRegex(){

        //Added license
        final License license = DataModelFactory.createLicense("TEST",
                "TEST", "",
                "((.*)(TESTING)(.*)|(TEST)+(.*))(3)(.*)", "");
        WebApplicationException exception = null;

        //Db license
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("AFL-3.0");
        dbLicense.setRegexp("((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)");

        //fake artifact
        final DbArtifact fakeArtifact = new DbArtifact();
        fakeArtifact.setGroupId("Fake artifact");
        fakeArtifact.setArtifactId("Fake artifact");
        fakeArtifact.setVersion("1");
        fakeArtifact.setLicenses(Arrays.asList("AFL-3.0"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(dbLicense));

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(fakeArtifact);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));


        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        try{
            ReportsRegistry.init();
            DataValidator.validateLicensePattern(license, licenseHandler);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }


    @Test
    public void validLicenseRegexOnEdit(){

        //Added license
        final License license = DataModelFactory.createLicense("TEST",
                "TEST", "",
                "((.*)(TESTING)(.*)|(TEST)+(.*))(3)(.*)", "");
        WebApplicationException exception = null;

        //Db license
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("TEST");
        dbLicense.setLongName("TEST");
        dbLicense.setRegexp("((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)");

        //fake artifact
        final DbArtifact fakeArtifact = new DbArtifact();
        fakeArtifact.setGroupId("Fake artifact");
        fakeArtifact.setArtifactId("Fake artifact");
        fakeArtifact.setVersion("1");
        fakeArtifact.setLicenses(Arrays.asList("AFL-3.0"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(dbLicense));
        when(repoHandler.getLicense(anyString())).thenReturn(dbLicense);

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(fakeArtifact);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));


        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        try{
            ReportsRegistry.init();
            DataValidator.validateLicensePattern(license, licenseHandler);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNull(exception);
    }

    @Test
    public void invalidLicenseRegexOnEdit(){

        //Edit license
        final License license = DataModelFactory.createLicense("TEST",
                "TEST", "",
                "((.*)(TEST)(.*)|(TEST)+(.*))(3)(.*)", "");
        WebApplicationException exception = null;

        //Db license
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("TEST");
        dbLicense.setLongName("TEST");
        dbLicense.setRegexp("((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)");

        final DbLicense dbLicense2 = new DbLicense();
        dbLicense2.setName("TEST2");
        dbLicense2.setLongName("TEST2");
        dbLicense2.setRegexp("((.*)(TEST)(.*)|(TEST)+(.*))(3)(.*)");


        //fake artifact
        final DbArtifact fakeArtifact = new DbArtifact();
        fakeArtifact.setGroupId("Fake artifact");
        fakeArtifact.setArtifactId("Fake artifact");
        fakeArtifact.setVersion("1");
        fakeArtifact.setLicenses(Arrays.asList("AFL-3.0", "TEST-3.0"));

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Arrays.asList(dbLicense,dbLicense2));
        when(repoHandler.getLicense(anyString())).thenReturn(dbLicense);

        doAnswer(invocation -> {
            Consumer<DbArtifact> fakeConsumer = (Consumer<DbArtifact>) invocation.getArguments()[3];
            fakeConsumer.accept(fakeArtifact);
            return null;
        }).when(repoHandler).consumeByQuery(anyString(), anyString(), any(Class.class), any(Consumer.class));


        final LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        try{
            ReportsRegistry.init();
            DataValidator.validateLicensePattern(license, licenseHandler);
        }
        catch (WebApplicationException e){
            exception = e;
        }

        assertNotNull(exception);
    }



}
