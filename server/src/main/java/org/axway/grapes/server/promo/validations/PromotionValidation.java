package org.axway.grapes.server.promo.validations;

import java.util.Optional;

public enum PromotionValidation {

        VERSION_IS_SNAPSHOT("Release version is SNAPSHOT"),
        DO_NOT_USE_DEPS("Release contains dependencies marked as DO_NOT_USE"),
        UNPROMOTED_DEPS("Release contains corporate dependencies which are not promoted"),
        DEPS_WITH_NO_LICENSES("Release contains dependencies with no license information"),
        DEPS_UNACCEPTABLE_LICENSE("Release contains dependencies subject of license terms not accepted");

    private String description;

    PromotionValidation(final String n) {
        this.description = n;
    }

    @Override
    public String toString() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    public static Optional<PromotionValidation> byName(final String name) {
        for(PromotionValidation v : values()) {
            if(v.name().equalsIgnoreCase(name)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
