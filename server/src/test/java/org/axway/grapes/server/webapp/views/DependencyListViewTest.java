package org.axway.grapes.server.webapp.views;


import com.google.common.collect.Lists;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.options.Decorator;
import org.axway.grapes.server.webapp.views.utils.Row;
import org.axway.grapes.server.webapp.views.utils.Table;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class DependencyListViewTest {

    @Test
    public void checkEmptyDependencyList(){
        final DependencyListView depList = new DependencyListView("test", Collections.EMPTY_LIST, new Decorator());
        final Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void checkColumnConfiguration(){
        final Decorator decorator = new Decorator();
        decorator.setShowSources(true);
        decorator.setShowSourcesVersion(false);
        decorator.setShowTargets(false);
        decorator.setShowTargetsDownloadUrl(false);
        decorator.setShowTargetsSize(false);
        decorator.setShowScopes(false);
        decorator.setShowLicenses(false);
        decorator.setShowLicensesComment(false);
        decorator.setShowLicensesLongName(false);
        decorator.setShowLicensesUrl(false);

        final DependencyListView depList = new DependencyListView("test", Collections.EMPTY_LIST, decorator);
        Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(1, results.getHeaders().size());


        decorator.setShowSources(true);
        decorator.setShowSourcesVersion(true);
        decorator.setShowTargets(true);
        decorator.setShowTargetsDownloadUrl(true);
        decorator.setShowTargetsSize(true);
        decorator.setShowScopes(true);
        decorator.setShowLicenses(true);
        decorator.setShowLicensesComment(true);
        decorator.setShowLicensesLongName(true);
        decorator.setShowLicensesUrl(true);

        results = depList.getTable();

        assertNotNull(results);
        assertEquals(10, results.getHeaders().size());
        assertEquals(DependencyListView.SOURCE_FIELD, results.getHeaders().get(0));
        assertEquals(DependencyListView.SOURCE_VERSION_FIELD, results.getHeaders().get(1));
        assertEquals(DependencyListView.TARGET_FIELD, results.getHeaders().get(2));
        assertEquals(DependencyListView.DOWNLOAD_URL_FIELD, results.getHeaders().get(3));
        assertEquals(DependencyListView.SIZE_FIELD, results.getHeaders().get(4));
        assertEquals(DependencyListView.SCOPE_FIELD, results.getHeaders().get(5));
        assertEquals(DependencyListView.LICENSE_FIELD, results.getHeaders().get(6));
        assertEquals(DependencyListView.LICENSE_LONG_NAME__FIELD, results.getHeaders().get(7));
        assertEquals(DependencyListView.LICENSE_URL_FIELD, results.getHeaders().get(8));
        assertEquals(DependencyListView.LICENSE_COMMENT_FIELD, results.getHeaders().get(9));
    }

    @Test
    public void checkDependenciesInformationOfTheTable(){
        final Decorator decorator = new Decorator();
        decorator.setShowSources(true);
        decorator.setShowSourcesVersion(true);
        decorator.setShowTargets(true);
        decorator.setShowTargetsDownloadUrl(true);
        decorator.setShowTargetsSize(true);
        decorator.setShowScopes(true);
        decorator.setShowLicenses(true);
        decorator.setShowLicensesComment(true);
        decorator.setShowLicensesLongName(true);
        decorator.setShowLicensesUrl(true);

        final License license = DataModelFactory.createLicense("name", "long name", "comment", "", "url");
        final DependencyListView depList = new DependencyListView("test", Collections.singletonList(license), decorator);

        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1", null, "jar", "jar");
        artifact.setDownloadUrl("http://");
        artifact.setSize("123456");
        artifact.addLicense(license.getName());

        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);
        dependency.setSourceName("module1");
        dependency.setSourceVersion("1");
        depList.addDependency(dependency);

        final Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1, results.getRows().size());

        final Row result = results.getRows().get(0);
        assertEquals(dependency.getSourceName(), result.get(0));
        assertEquals(dependency.getSourceVersion(), result.get(1));
        assertEquals(dependency.getTarget().getGavc(), result.get(2));
        assertEquals(dependency.getTarget().getDownloadUrl(), result.get(3));
        assertEquals(dependency.getTarget().getSize(), result.get(4));
        assertEquals(dependency.getScope().name(), result.get(5));
        assertEquals(license.getName(), result.get(6));
        assertEquals(license.getLongName(), result.get(7));
        assertEquals(license.getUrl(), result.get(8));
        assertEquals(license.getComments(), result.get(9));
    }

    @Test
    public void checkDependenciesInformationOfTheTableIfDependencyWithNoLicense(){
        final Decorator decorator = new Decorator();
        decorator.setShowSources(true);
        decorator.setShowSourcesVersion(true);
        decorator.setShowTargets(true);
        decorator.setShowTargetsDownloadUrl(true);
        decorator.setShowTargetsSize(true);
        decorator.setShowScopes(true);
        decorator.setShowLicenses(true);
        decorator.setShowLicensesComment(true);
        decorator.setShowLicensesLongName(true);
        decorator.setShowLicensesUrl(true);

        final DependencyListView depList = new DependencyListView("test", Collections.EMPTY_LIST, decorator);

        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1", null, "jar", "jar");
        artifact.setDownloadUrl("http://");
        artifact.setSize("123456");

        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);
        dependency.setSourceName("module1");
        dependency.setSourceVersion("1");
        depList.addDependency(dependency);

        final Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1, results.getRows().size());

        final Row result = results.getRows().get(0);
        assertEquals(dependency.getSourceName(), result.get(0));
        assertEquals(dependency.getSourceVersion(), result.get(1));
        assertEquals(dependency.getTarget().getGavc(), result.get(2));
        assertEquals(dependency.getTarget().getDownloadUrl(), result.get(3));
        assertEquals(dependency.getTarget().getSize(), result.get(4));
        assertEquals(dependency.getScope().name(), result.get(5));
        assertEquals("", result.get(6));
        assertEquals("", result.get(7));
        assertEquals("", result.get(8));
        assertEquals("", result.get(9));
    }

    @Test
    public void checkThatIfALicenseDoesNotExistAFakeOneIsCreated(){
        final Decorator decorator = new Decorator();
        decorator.setShowSources(true);
        decorator.setShowSourcesVersion(true);
        decorator.setShowTargets(true);
        decorator.setShowTargetsDownloadUrl(true);
        decorator.setShowTargetsSize(true);
        decorator.setShowScopes(true);
        decorator.setShowLicenses(true);
        decorator.setShowLicensesComment(true);
        decorator.setShowLicensesLongName(true);
        decorator.setShowLicensesUrl(true);

        final DependencyListView depList = new DependencyListView("test", Collections.EMPTY_LIST, decorator);

        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1", null, "jar", "jar");
        artifact.setDownloadUrl("http://");
        artifact.setSize("123456");
        artifact.addLicense("test");

        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);
        dependency.setSourceName("module1");
        dependency.setSourceVersion("1");
        depList.addDependency(dependency);

        final Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1, results.getRows().size());

        final Row result = results.getRows().get(0);
        assertEquals(dependency.getSourceName(), result.get(0));
        assertEquals(dependency.getSourceVersion(), result.get(1));
        assertEquals(dependency.getTarget().getGavc(), result.get(2));
        assertEquals(dependency.getTarget().getDownloadUrl(), result.get(3));
        assertEquals(dependency.getTarget().getSize(), result.get(4));
        assertEquals(dependency.getScope().name(), result.get(5));
        assertEquals("#test# (to be identified)", result.get(6));
        assertEquals("not identified yet", result.get(7));
        assertEquals("not identified yet", result.get(8));
        assertEquals("not identified yet", result.get(9));

    }

    @Test
    public void checkThatIfAnArtifactHasManyLicensesItAppearsAsManyTimesThatItHasLicense(){
        final License license1 = DataModelFactory.createLicense("name1", "long name1", "comment1", "", "url1");
        final License license2 = DataModelFactory.createLicense("name2", "long name2", "comment2", "", "url2");

        final DependencyListView depList = new DependencyListView("test", Lists.newArrayList(license1, license2), new Decorator());

        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1", null, "jar", "jar");
        artifact.setDownloadUrl("http://");
        artifact.setSize("123456");
        artifact.addLicense(license1.getName());
        artifact.addLicense(license2.getName());

        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);
        dependency.setSourceName("module1");
        dependency.setSourceVersion("1");
        depList.addDependency(dependency);

        final Table results = depList.getTable();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(2, results.getRows().size());

    }

}
