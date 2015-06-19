package org.axway.grapes.jongo.it;

import org.axway.grapes.jongo.datamodel.DbModule;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DbModuleIT  extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbModule)")
    Crud<DbModule, String> moduleCrud;
    @Test
    public void testModules() {

        Long count = moduleCrud.count();
        Date date = new Date();
        String name = "org.test.module-2.0"+date.getTime();

        //create one
        DbModule dbModule = new DbModule();
        dbModule.setName(name);
        dbModule.setVersion("2.0");
        dbModule.setPromoted(false);

        dbModule= moduleCrud.save(dbModule);
        assertThat(moduleCrud.count()).isEqualTo(count +1);

        //find it
        DbModule actual = moduleCrud.findOne(dbModule.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(dbModule.getId());
        assertThat(actual.getVersion()).isEqualTo("2.0");

        //update it
        actual.setPromoted(true);
        DbModule actual2= moduleCrud.save(actual);
        assertThat(moduleCrud.count()).isEqualTo(count +1);
        actual = moduleCrud.findOne(dbModule.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(dbModule.getId());
        assertThat(actual.isPromoted()).isTrue();

        //todo corg.axway.grapes.jongo.itate a new one should it?
        actual2.setVersion("3.0");
        actual2= moduleCrud.save(actual2);
        assertThat(moduleCrud.count()).isEqualTo(count +2);
        assertThat(actual2).isNotNull();
        assertThat(actual2.getId()).isNotEqualTo(dbModule.getId());
        assertThat(actual2.getVersion()).isEqualTo("3.0");


        //dispose of both
        moduleCrud.delete(actual);
        actual = moduleCrud.findOne(dbModule.getId());
        assertThat(actual).isNull();
        assertThat(moduleCrud.count()).isEqualTo(count+1);

        moduleCrud.delete(actual2);
        actual2 = moduleCrud.findOne(actual2.getId());
        assertThat(actual2).isNull();
        assertThat(moduleCrud.count()).isEqualTo(count);




    }

}