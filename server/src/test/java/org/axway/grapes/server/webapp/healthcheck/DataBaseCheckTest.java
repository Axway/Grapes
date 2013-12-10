package org.axway.grapes.server.webapp.healthcheck;

import com.yammer.metrics.core.HealthCheck.Result;
import org.axway.grapes.server.config.DataBaseConfig;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author jdcoffre
 */
public class DataBaseCheckTest {
    
    @Test
	public void checkUnhealthy() {
		DataBaseConfig dataBaseConfig = mock(DataBaseConfig.class);
		when(dataBaseConfig.getHost()).thenReturn("localhost");
		when(dataBaseConfig.getPort()).thenReturn(8074);
		
		DataBaseCheck dbCheck = new DataBaseCheck(dataBaseConfig);
		Result result = dbCheck.check();
		
		assertNotNull(result);
		assertFalse(result.isHealthy());
		
	}
    
}
