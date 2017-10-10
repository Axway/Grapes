package org.axway.grapes.server.webapp;

import java.io.IOException;
import java.util.Collections;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.reports.ReportsHandler;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
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
    public void validateLicenseRegex(){

        //Added license
        final License license = DataModelFactory.createLicense("TestLicense",
                "TestLicense", "TestLicense",
                "", "TestLicense");
        WebApplicationException exception = null;

        //Db license
        final DbLicense dbLicense = new DbLicense();
        dbLicense.setName("TestLicense2");
        dbLicense.setRegexp("((.*)(academic)(.*)|(AFL)+(.*))(3)(.*)");

        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getAllLicenses()).thenReturn(Collections.singletonList(dbLicense));

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
    
    
}
