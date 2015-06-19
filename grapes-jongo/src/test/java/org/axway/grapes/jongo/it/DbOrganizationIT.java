package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbOrganization;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DbOrganizationIT  extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbOrganization)")
    Crud<DbOrganization, String> organizationCrud;

    @Test
    public void tesOrganizationst() {
         List<String> corporateGroupIdPrefixes = new ArrayList<String>();
        corporateGroupIdPrefixes.add("prefix1");
        corporateGroupIdPrefixes.add("prefix2");

        // create one
        Long count = organizationCrud.count();
        Date date = new Date();
        String name = "TestOrg"+date.getTime();

        //create one
        DbOrganization dbOrganization = new DbOrganization();
        dbOrganization.setName(name);
        organizationCrud.save(dbOrganization);
        assertThat(organizationCrud.count()).isEqualTo(count +1);

        //find it
        DbOrganization actual = organizationCrud.findOne(dbOrganization.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbOrganization.getName());
        assertThat(actual.getCorporateGroupIdPrefixes()).isEmpty();

        //update it
        actual.setCorporateGroupIdPrefixes(corporateGroupIdPrefixes);
        organizationCrud.save(actual);
        assertThat(organizationCrud.count()).isEqualTo(count +1);
        actual = organizationCrud.findOne(dbOrganization.getName());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(dbOrganization.getName());
        assertThat(actual.getCorporateGroupIdPrefixes()).isNotEmpty();
        assertThat(actual.getCorporateGroupIdPrefixes()).hasSize(2);


        //dispose it
        organizationCrud.delete(actual);
        actual = organizationCrud.findOne(dbOrganization.getName());
        assertThat(actual).isNull();
        assertThat(organizationCrud.count()).isEqualTo(count);
    }
}