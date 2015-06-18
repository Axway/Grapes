package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbArtifact;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jennifer on 4/14/15.
 */
public class DbArtifactIT extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbArtifact)")
    Crud<DbArtifact, String> artifactsCrud;

    @Test
    public void testArtifacts() {
        Long count =artifactsCrud.count();
        Date date = new Date();
        String aid = "UidTest"+date.getTime();
        // Create one
        DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("com.axway.test");
        dbArtifact.setArtifactId(aid);
        dbArtifact.setVersion("2.0.1-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("jar");
        dbArtifact.setExtension("jar");
        dbArtifact = artifactsCrud.save(dbArtifact);
        assertThat(artifactsCrud.count()).isEqualTo(count +1);

        // find org.axway.grapes.jongo.it
        assertThat(artifactsCrud.findAll()).hasSize((int)(count+1));
        DbArtifact actual = artifactsCrud.findOne(dbArtifact.getGavc());
        assertThat(actual).isNotNull();
        assertThat(actual.getGavc()).isEqualTo(dbArtifact.getGavc());
        assertThat(actual.getDoNotUse()).isFalse();


        // update org.axway.grapes.jongo.it
        actual.setDoNotUse(true);
        artifactsCrud.save(actual);

        assertThat(artifactsCrud.findAll()).hasSize((int)(count+1));
        actual = artifactsCrud.findOne(dbArtifact.getGavc());
        assertThat(actual).isNotNull();
        assertThat(actual.getGavc()).isEqualTo(dbArtifact.getGavc());
        assertThat(actual.getDoNotUse()).isTrue();

        // dispose org.axway.grapes.jongo.it
        artifactsCrud.delete(actual);
        actual = artifactsCrud.findOne(dbArtifact.getGavc());
        assertThat(actual).isNull();
        assertThat(artifactsCrud.count()).isEqualTo(count);

    }

}
