package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbLicense;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DbLicenseIT  extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbLicense)")
    Crud<DbLicense, String> licenseCrud;

    @Test
    public void testLicenses() {
        Long count = licenseCrud.count();
        Date date = new Date();
        String name = "Artistic-License-2.0"+date.getTime();
        //create one
        DbLicense dbLicense = new DbLicense();
        dbLicense.setName(name);
        dbLicense.setLongName("Artistic License 2.0");
        dbLicense.setComments("comments");
        dbLicense.setRegexp("(((.*)(artistic)+(.*)))+(.*)(2)(.*)");
        dbLicense.setUrl("http://www.opensource.org/licenses/artistic-license-2.0.php");
        dbLicense = licenseCrud.save(dbLicense);
        assertThat(licenseCrud.count()).isEqualTo(count +1);

        //find it
        DbLicense actual = licenseCrud.findOne(dbLicense.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbLicense.getName());
        assertThat(actual.getRegexp()).isEqualTo("(((.*)(artistic)+(.*)))+(.*)(2)(.*)");

        //update it
        actual.setApproved(true);
        licenseCrud.save(actual);
        assertThat(licenseCrud.count()).isEqualTo(count +1);
        actual = licenseCrud.findOne(dbLicense.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbLicense.getName());
        assertThat(actual.isApproved()).isTrue();

        //dispose it
        licenseCrud.delete(actual);
        actual = licenseCrud.findOne(dbLicense.getName());
        assertThat(actual).isNull();
        assertThat(licenseCrud.count()).isEqualTo(count);
    }

}