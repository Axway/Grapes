package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.webapp.views.LicenseView;

import java.io.IOException;

/**
 * Serialize a license view in Json
 *
 * @author jdcoffre
 */

public class LicenseSerializer extends JsonSerializer<LicenseView> {

    @Override
    public void serialize(final LicenseView licenseView, final JsonGenerator json,	final SerializerProvider serializer) throws IOException {
        final DbLicense dbLicense = licenseView.getLicense();
        final License license = DataModelFactory.createLicense(dbLicense.getName(), dbLicense.getLongName(), dbLicense.getComments(), dbLicense.getRegexp(), dbLicense.getUrl());

        if(dbLicense.isApproved() != null){
            license.setApproved(dbLicense.isApproved());
        }

        json.writeObject(license);
        json.flush();

    }

}
