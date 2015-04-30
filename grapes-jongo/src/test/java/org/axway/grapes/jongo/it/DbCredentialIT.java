package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbCredential;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DbCredentialIT  extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbCredential)")
    Crud<DbCredential, String> credentialCrud;

    @Test
    public void testArtifacts() {
        Long count = credentialCrud.count();
        Date date = new Date();
        String userid = "user"+date.getTime();

        // Create one
        DbCredential dbCredential = new DbCredential();
        dbCredential.setUser(userid);

        dbCredential = credentialCrud.save(dbCredential);
        assertThat(credentialCrud.count()).isEqualTo(count +1);

        // find it
        assertThat(credentialCrud.findAll()).hasSize((int)(count + 1));
        DbCredential actual = credentialCrud.findOne(dbCredential.getUser());

        assertThat(actual).isNotNull();
        assertThat(actual.getUser()).isEqualTo(dbCredential.getUser());
        assertThat(actual.getPassword()).isEmpty();


        // update it
        actual.setPassword("123456");
        credentialCrud.save(actual);

       assertThat(credentialCrud.findAll()).hasSize((int)(count+1));
        actual = credentialCrud.findOne(dbCredential.getUser());
        assertThat(actual).isNotNull();
        assertThat(actual.getUser()).isEqualTo(dbCredential.getUser());
        assertThat(actual.getPassword()).isEqualTo("123456");

        // dispose it
        credentialCrud.delete(actual);
        actual = credentialCrud.findOne(dbCredential.getUser());
        assertThat(actual).isNull();
        assertThat(credentialCrud.count()).isEqualTo(count);

    }

}