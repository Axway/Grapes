package org.axway.grapes.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.axway.grapes.commons.datamodel.Tag;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class PromoValidationConfig extends Configuration {

    @Valid
    @JsonProperty
    private List<String> errors = new ArrayList<>();

    @Valid
    @JsonProperty(value = "tags")
    private TagsConfig tagConfig = new TagsConfig();

    public TagsConfig getTagConfig() {
        return tagConfig;
    }

    public void setTagConfig(TagsConfig tagConfig) {
        this.tagConfig = tagConfig;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void purge(List<String> wrongValues) {
        this.errors.removeAll(wrongValues);
    }

    public void purgeFromTag(final Tag tag, final List<String> invalidValues) {
        tagConfig.purge(tag, invalidValues);
    }
}