package org.axway.grapes.commons.datamodel;

public enum Tag {
    CRITICAL("critical"),
    MAJOR("major"),
    MINOR("minor");

    private final String value;

    Tag(String tag) {
        this.value = tag;
    }

    public static Tag byName(final String name) {
        //
        // TODO: Enhance this to Java 8 Optional once the compilation
        // level is raised. Currently is kept at 7 to match Jenkins 1
        // for shared library grapes-commons
        //
        for(Tag t : values()) {
            if(t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
