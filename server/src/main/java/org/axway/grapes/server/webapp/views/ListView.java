package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.webapp.views.serialization.ListSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List View
 * 
 * <p>List view that can either be serialized in HTML or in JSON.</p>
 * 
 * @author jdcoffre
 */
@JsonSerialize(using=ListSerializer.class)
public class ListView extends View{

	private final String title;
	private final String itemName;
	private final List<String> list = new ArrayList<String>();

	public ListView(final String title, final String itemName) {
		super("ListView.ftl");
		this.title = title;
		this.itemName = itemName;
	}

	public String getTitle() {
		return title;
	}

	public void addAll(final List<String> list) {
        for(final String element: list){
            add(element);
        }
	}

	public List<String> getItems() {
		Collections.sort(list);
		return list;
	}

	public String getItemName() {
		return itemName;
	}

	public void add(final String element) {
		if(!list.contains(element)){
            list.add(element);
        }
	}

}
