package org.axway.grapes.server.reports.utils;

import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.server.core.DependencyHandler;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.db.mongo.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.axway.grapes.server.db.DataUtils.isFullGAVC;
import static org.axway.grapes.server.db.DataUtils.strip;
import static org.axway.grapes.server.db.mongo.QueryUtils.makeQuery;

/**
 * Class for retrieving commonly used objects
 */
public class DataFetchingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DataFetchingUtils.class);

    public List<DbProduct> getProductWithCommercialDeliveries(final RepositoryHandler repoHandler) {
        return repoHandler.getListByQuery(
                DbCollections.DB_PRODUCT,
                QueryUtils.makeQueryAllDeliveries(),
                DbProduct.class);
    }

    public Optional<Delivery> getCommercialDelivery(final RepositoryHandler repoHandler,
                                                    final String name,
                                                    final String version) {

        final Optional<DbProduct> productOptional = repoHandler.getOneByQuery(DbCollections.DB_PRODUCT,
                makeQuery(name, version),
                DbProduct.class);

        if(!productOptional.isPresent()) {
            return Optional.empty();
        }

        final DbProduct dbProduct = productOptional.get();

        final List<Delivery> filtered = dbProduct.getDeliveries()
                .stream()
                .filter(d -> d.getCommercialName().equals(name) &&
                        d.getCommercialVersion().equals(version)

                )
                .collect(Collectors.toList());

        if(filtered.isEmpty()) {
            return Optional.empty();
        }

        if (filtered.size() > 1 && LOG.isWarnEnabled()) {
            LOG.warn(String.format("Multiple commercial version entries found for [%s] [%s]", name, version));
        }

        return Optional.of(filtered.get(0));
    }

    public Set<String> getDeliveryDependencies(final RepositoryHandler repoHandler,
                                               final DependencyHandler depHandler,
                                               final Delivery delivery) {

        final FiltersHolder filters = new FiltersHolder();
        filters.getDecorator().setShowCorporate(Boolean.FALSE);
        filters.getDecorator().setShowThirdparty(Boolean.TRUE);

        Set<String> deps = new HashSet<>();

        delivery.getDependencies().forEach(d -> {
            final DbModule module = repoHandler.getModule(d);
            if (module != null) {
                final List<Dependency> allDependencies = depHandler.getModuleDependencies(module.getId(), filters);
                allDependencies.forEach(dep -> deps.add(dep.getTarget().getGavc()));
            } else {
                //
                // This stripping occurs because the dep.getTarget().getGavc() returns the
                // "short form" of the artifact group_id:artifact_id:version
                //
                if(isFullGAVC(d)) {
                    deps.add(strip(d, 2));
                } else {
                    deps.add(d);
                }
            }
        });

        return deps;
    }
}
