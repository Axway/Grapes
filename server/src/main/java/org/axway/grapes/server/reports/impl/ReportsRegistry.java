package org.axway.grapes.server.reports.impl;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * This class is the locator for all the service implementations
 */
public class ReportsRegistry {

    //
    // All the reports should be included under this package
    //
    private static final String REPORTS_PACKAGE = "org.axway.grapes.server.reports";

    private static final Set<Report> reports = new HashSet<>();
    private static final Logger LOG = LoggerFactory.getLogger(ReportsRegistry.class);

    private ReportsRegistry() {};

    /**
     * Initializes the set of report implementation.
     */
    public static void init() {
        Reflections reflections = new Reflections(REPORTS_PACKAGE);
        final Set<Class<? extends Report>> reportClasses = reflections.getSubTypesOf(Report.class);

        for(Class<? extends Report> c : reportClasses) {
            LOG.info("Report class: " + c.getName());
            try {
                reports.add(c.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                LOG.error("Error while loading report implementation classes", e);
            }
        }

        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Detected %s reports", reports.size()));
        }
    }

    public static Set<Report> allReports() {
        return Collections.unmodifiableSet(reports);
    }

    public static Optional<Report> findById(int id) throws NoSuchElementException {
        for(Report r : reports) {
            if(r.getId().getId() == id) {
                return Optional.of(r);
            }
        }

        return Optional.empty();
    }
}
