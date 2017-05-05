package org.axway.grapes.server.reports.impl;

import org.reflections.Reflections;

import java.util.*;
// import java.util.stream.Collectors;

public class ReportsLoader {

    private static final Set<Report> reports = new HashSet<>();

    public static void init() {
        Reflections reflections = new Reflections("org.axway.grapes.server.reports");
        final Set<Class<? extends Report>> reportClasses = reflections.getSubTypesOf(Report.class);

        for(Class<? extends Report> c : reportClasses) {
            System.out.println("Report class: " + c.getName());
            try {
                reports.add(c.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Found reports: " + reports.size());
    }

    public static Optional<Report> findById(int id) throws NoSuchElementException {
        for(Report r : reports) {
            System.out.println(r.getId());
            if(r.getId().getId() == id) {
                return Optional.of(r);
            }
        }
        //final List<Report> collected = reports.stream().filter(r -> r.getId().getId() == id).collect(Collectors.toList());

//        if(collected.isEmpty()) {
//            throw new NoSuchElementException(String.format("Cannot find report %s", id));
//        }
//
//        return collected.get(0);
        return Optional.empty();
    }
}
