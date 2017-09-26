package org.axway.grapes.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromoValidationConfig extends Configuration {

    @Valid
    @JsonProperty
    private List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors == null ? Collections.emptyList() : Collections.unmodifiableList(errors);
    }


    public void purge(List<String> wrongValues) {
        this.errors.removeAll(wrongValues);
    }
}
