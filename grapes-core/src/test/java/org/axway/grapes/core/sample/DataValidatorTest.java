package org.axway.grapes.core.sample;

public class DataValidatorTest {


   /* public void testValidate() throws Exception {
        ModuleComplete module = new ModuleComplete();
        Artifact artifact = new Artifact();
        License license = new License();
        List<String> licenseList = new ArrayList<>();
        Set<ModuleComplete> moduleSet = new HashSet<>();
        licenseList.add("Copyright 2013 by Axway Software");

        Set<String> artifactSet = new HashSet<>();
        Set<String> artifactSet1 = new HashSet<>();
        Set<String> artifactSet2 = new HashSet<>();
        Set<String> artifactSet3 = new HashSet<>();

        artifact.setArtifactId("sampleForAlloMrclean");
        artifact.setGroupId("com.axway.sampleForAlloMrclean");
        artifact.setVersion("1.0.0-3");
        artifact.setType("pom");
        artifact.setExtension("pom");
        artifact.setPromoted(false);
        artifact.setSize(null);
        artifact.setDownloadUrl(null);
        artifact.setProvider(null);
        artifact.setLicenses(licenseList);

        artifactSet.add(artifact.getGavc());



        Artifact artifact1 = new Artifact();
        artifact1.setArtifactId("sampleForAlloMrclean-module1");
        artifact1.setGroupId("com.axway.sampleForAlloMrclean");
        artifact1.setVersion("1.0.0-3");
        artifact1.setType("jar");
        artifact1.setExtension("jar");
        artifact1.setPromoted(false);
        artifact1.setSize("2798");
        artifact1.setDownloadUrl(null);
        artifact1.setProvider(null);
        artifact1.setLicenses(licenseList);

        artifactSet1.add(artifact1.getGavc());

        Artifact artifact2 = new Artifact();
        artifact2.setArtifactId("sampleForAlloMrclean-module1");
        artifact2.setGroupId("com.axway.sampleForAlloMrclean");
        artifact2.setVersion("1.0.0-3");
        artifact2.setType("pom");
        artifact2.setExtension("xml");
        artifact2.setPromoted(false);
        artifact2.setSize("668");
        artifact2.setDownloadUrl(null);
        artifact2.setProvider(null);
        artifact2.setLicenses(licenseList);

        artifactSet1.add(artifact2.getGavc());


        Artifact artifact3 = new Artifact();
        artifact3.setArtifactId("sampleForAlloMrclean-module2");
        artifact3.setGroupId("com.axway.sampleForAlloMrclean");
        artifact3.setVersion("1.0.0-3");
        artifact3.setType("pom");
        artifact3.setExtension("xml");
        artifact3.setPromoted(false);
        artifact3.setSize("1357");
        artifact3.setDownloadUrl(null);
        artifact3.setProvider(null);
        artifact3.setLicenses(licenseList);

        artifactSet2.add(artifact3.getGavc());

        Artifact artifact4 = new Artifact();
        artifact4.setArtifactId("sampleForAlloMrclean-module2");
        artifact4.setGroupId("com.axway.sampleForAlloMrclean");
        artifact4.setVersion("1.0.0-3");
        artifact4.setType("jar");
        artifact4.setExtension("jar");
        artifact4.setPromoted(false);
        artifact4.setSize("2977");
        artifact4.setDownloadUrl(null);
        artifact4.setProvider(null);
        artifact4.setLicenses(licenseList);

        artifactSet2.add(artifact4.getGavc());

        Artifact artifact5 = new Artifact();
        artifact5.setArtifactId("junit");
        artifact5.setGroupId("junit");
        artifact5.setVersion("3.8.1");
        artifact5.setType("jar");
        artifact5.setExtension("jar");
        artifact5.setPromoted(false);
        artifact5.setSize("121070");
        artifact5.setDownloadUrl(null);
        artifact5.setProvider(null);
        artifact5.setLicenses(licenseList);

        artifactSet3.add(artifact5.getGavc());
        DependencyComplete dependency = new Dependency(":",artifact5, Scope.COMPILE);
        DependencyComplete dependency2 = new Dependency(":",artifact1, Scope.COMPILE);

        Set<DependencyComplete> dependencySet = new HashSet<>();
        dependencySet.add(dependency);
        dependencySet.add(dependency2);



        ModuleComplete subModule1 = new ModuleComplete();
        subModule1.setName( "com.axway.sampleForAlloMrclean:sampleForAlloMrclean-module1");
        subModule1.setVersion("1.0.0-3");
        subModule1.setPromoted(false);
        subModule1.setArtifacts(artifactSet1);
        subModule1.setIsSubmodule(true);

        ModuleComplete subModule2 = new ModuleComplete();
        subModule2.setName( "com.axway.sampleForAlloMrclean:sampleForAlloMrclean-module2");
        subModule2.setVersion("1.0.0-3");
        subModule2.setPromoted(false);
        subModule2.setArtifacts(artifactSet2);
        subModule2.setDependencies(dependencySet);
        subModule2.setIsSubmodule(true);

        module.setName("com.axway.sampleForAlloMrclean:sampleForAlloMrclean");
        module.setVersion("1.0.0-3");
        module.setPromoted(false);
        module.setArtifacts(artifactSet);
        module.setIsSubmodule(false);

        moduleSet.add(subModule1);
        moduleSet.add(subModule2);
        module.setSubmodules(moduleSet);

     //   DataValidator.validate(module);



    }

    @Test
    public void testValidate1() throws Exception {

    }

    @Test
    public void testValidate2() throws Exception {
    }

    @Test
    public void testValidate3() throws Exception {
    }*/
}