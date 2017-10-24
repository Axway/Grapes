package org.axway.grapes.server.config;

import org.axway.grapes.commons.datamodel.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.*;

public class TagsConfig {

    private static final Logger LOG = LoggerFactory.getLogger(TagsConfig.class);


    @Valid
    private List<String> critical = new ArrayList<>();

    @Valid
    private List<String> major = new ArrayList<>();

    @Valid
    private List<String> minor = new ArrayList<>();

    private Map<Tag, List<String>> tagLists = new EnumMap<>(Tag.class);


    public TagsConfig() {
    }

    public List<String> getCritical() {
        return critical;
    }

    public List<String> getMajor() {
        return major;
    }

    public List<String> getMinor() {
        return minor;
    }

    public Tag getTag(final String v) {
        refreshTagLists();
        for(final Map.Entry<Tag, List<String>> entry : tagLists.entrySet()) {
            if(entry.getValue().contains(v)) {
                return entry.getKey();
            }
        }

        return Tag.MINOR;
    }

    public List<String> getListByTag(Tag t) {
        refreshTagLists();
        return tagLists.get(t);
    }

    public void purge(final Tag tag, final List<String> invalidValues) {
        if(tagLists.containsKey(tag)) {
            final List<String> list = tagLists.get(tag);
            list.removeAll(invalidValues);
        } else {
            if(LOG.isWarnEnabled()) {
                LOG.warn(String.format("Invalid tag %s", tag.toString()));
            }
        }
    }

    @Override
    public String toString() {
        return "TagsConfig { " +
                "critical=" + critical +
                ", major=" + major +
                ", minor=" + minor +
                " }";
    }

    private void refreshTagLists() {
        tagLists.put(Tag.MINOR, minor);
        tagLists.put(Tag.MAJOR, major);
        tagLists.put(Tag.CRITICAL, critical);
    }
}
