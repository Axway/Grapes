package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OriginFilter implements Filter {

	private String origin;

	/**
	 * The parameter must never be null
	 *
	 * @param origin
	 */
	public OriginFilter(final String origin) {
		this.origin = origin;
	}

	@Override
	public boolean filter(final Object datamodelObj) {
		if(datamodelObj instanceof DbArtifact){
			return origin.equals( ((DbArtifact)datamodelObj).getOrigin());
		}

		return false;
	}

	@Override
	public Map<String, Object> moduleFilterFields() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Object> artifactFilterFields() {
		final Map<String, Object> fields = new HashMap<String, Object>();
		fields.put(DbArtifact.ORIGIN_DB_FIELD, origin);
		return fields;
	}
}
