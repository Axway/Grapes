package org.axway.grapes.server.webapp;

import org.axway.grapes.commons.datamodel.*;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

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

}
