package org.axway.grapes.server.webapp.tasks.migrate;

import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.datamodel.*;
import org.jongo.FindOne;
import org.jongo.Jongo;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Migration220 {

    private static final String DB_CORPORATE_GID_COLLECTION_NAME = "DbCorporateGroupIds";


    private Migration220(){
        // hide utility Class constructor
    }

    public static void perform(final Jongo db, final PrintWriter printer) {
        try {

            printer.println("Migration to data-model version 2.2.0 started ...");
            printer.println("The following operations are going to be done on Grapes db: ");
            printer.println("  - removing DbCorporateGroupIds collection");
            printer.println("  - data-model version updated to 2.2.0 on all DB object");
            printer.println("  - sub-module artifact duplication is being fixed ");
            printer.println("  - organization field will be added to modules");
            printer.println("  - create DbGrapesInfo collection");
            printer.flush();

            // Migrate Modules;
            printer.println("Starting the migration of Modules ...");
            final int nbModules = getModuleCount(db);

            final FindOne query = db.getCollection(DbCollections.DB_MODULES).findOne("{" + DbModule.DATA_MODEL_VERSION + " : {$not: #}}", Pattern.compile(DbCollections.DATAMODEL_VERSION));
            DbModule oldModule = query.as(DbModule.class);
            int migrated = 0;

            while (oldModule != null) {
                final DbModule newModule = getNewModule(oldModule, printer);

                db.getCollection(DbCollections.DB_MODULES).update("{ _id : \"" + oldModule.getId() + "\"}").with(newModule);
                migrated++;

                oldModule = query.as(DbModule.class);
            }

            printer.println(migrated + "/" + nbModules + " Modules migrated.");

            if(migrated == nbModules){
                printer.println("Migration completed and successful");
            }
            else{
                final int remaining = nbModules-migrated;
                printer.println("There are " + remaining + " remaining elements to migrate.");
            }

            printer.println("");

            // Migrate Artifacts;
            printer.println("Starting the migration of Artifacts ...");
            db.getCollection(DbCollections.DB_ARTIFACTS).update("{_id : #}", Pattern.compile(".*")).with("{$set: {"+DbArtifact.DATA_MODEL_VERSION+": # }}", DbCollections.DATAMODEL_VERSION);
            printer.println("Artifact migration ended.");
            printer.println("");

            // Migrate Licenses;
            printer.println("Starting the migration of Licenses ...");
            db.getCollection(DbCollections.DB_LICENSES).update("{_id : #}", Pattern.compile(".*")).with("{$set: {"+DbArtifact.DATA_MODEL_VERSION+": # }}", DbCollections.DATAMODEL_VERSION);
            printer.println("Licenses migration ended.");
            printer.println("");

            // Migrate Credentials;
            printer.println("Starting the migration of Credentials ...");
            db.getCollection(DbCollections.DB_CREDENTIALS).update("{_id : #}", Pattern.compile(".*")).with("{$set: {"+DbCredential.DATA_MODEL_VERSION+": # }}", DbCollections.DATAMODEL_VERSION);
            printer.println("Credentials migration ended.");
            printer.println("");

            // Removing Corporate GroupIds;
            printer.println("Removing Corporate groupIds ...");
            db.getCollection(DB_CORPORATE_GID_COLLECTION_NAME).drop();
            printer.println("Deletion ended.");
            printer.println("");

            // Adding Corporate DbGrapesInfo;
            printer.println("Adding DbGrapesInfo ...");
            final DbGrapesInfo info = new DbGrapesInfo();
            info.setDatamodelVersion(DbCollections.DATAMODEL_VERSION);
            db.getCollection(DbCollections.DB_GRAPES_INFO).save(info);
            printer.println("DbGrapesInfo ok.");
            printer.println("");

            printer.println("End of the migration.");
        } catch (Exception e) {
            printer.print(e);
            printer.println("Migration failed");
            printer.flush();
        }

        printer.flush();
    }

    private static int getModuleCount(final Jongo db) {
        return db.getDatabase().getCollection(DbCollections.DB_MODULES).find().count();
    }

    private static DbModule getNewModule(final DbModule module, final PrintWriter printer) {
        printer.println("\nStart Working on " + module.getId());
        final DbModule newModule = copyModule(module);

        // Fix artifact issue
        final List<String> submoduleArtifacts = getSubmoduleArtifacts(newModule);
        final boolean changes = newModule.getArtifacts().removeAll(submoduleArtifacts);

        if(changes){
            printer.println("\nFixing artifact duplication on " + module.getId());
        }

        // Update Has & Use to take modifications in account
        newModule.updateHasAndUse();

        return newModule;
    }

    private static DbModule copyModule(final DbModule module) {
        final DbModule newModule = new DbModule();
        newModule.setName(module.getName());
        newModule.setVersion(module.getVersion());
        newModule.setPromoted(module.isPromoted());
        newModule.setSubmodule(module.isSubmodule());

        newModule.setArtifacts(module.getArtifacts());
        newModule.setDependencies(module.getDependencies());
        for(final DbModule subModule: module.getSubmodules()){
            newModule.addSubmodule(copyModule(subModule));
        }

        return newModule;
    }

    private static List<String> getSubmoduleArtifacts(final DbModule newModule) {
        final List<String> submoduleArtifacts = new ArrayList<String>();

        for(final DbModule submodule: DataUtils.getAllSubmodules(newModule)){
            submoduleArtifacts.addAll(submodule.getArtifacts());
        }

        return submoduleArtifacts;
    }
}
