package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.db.datamodel.DbSearch;
import org.axway.grapes.server.webapp.views.serialization.SearchSerializer;

@JsonSerialize(using = SearchSerializer.class)
public class SearchView extends View {

    private DbSearch searchOj;

    public SearchView() {
        super("Search.ftl");
    }

    public void setSearchOj(DbSearch searchOj) {
       this.searchOj = searchOj;
    }

    public DbSearch getSearchOj() {
        return searchOj;
    }
}
