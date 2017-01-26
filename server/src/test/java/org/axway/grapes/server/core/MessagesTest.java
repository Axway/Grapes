package org.axway.grapes.server.core;

import static org.junit.Assert.*;

import org.axway.grapes.server.GrapesTestUtils;
import static org.axway.grapes.server.config.Messages.*;
import static org.axway.grapes.server.core.services.email.MessageKey.*;
import org.junit.Test;

public class MessagesTest {

	@Test
	public void simpleMessageTest() {
		final String templatePath = GrapesTestUtils.class.getResource("all-messages.txt").getPath();
		init(templatePath);
		assertEquals("msg_4", get(ARTIFACT_IS_PROMOTED));
	}

	@Test
	public void keyNotFoundTest() {
		final String templatePath = GrapesTestUtils.class.getResource("messages-missing-subject.txt").getPath();
		init(templatePath);
		assertEquals(ARTIFACT_VALIDATION_EMAIL_SUBJECT.toString(), get(ARTIFACT_VALIDATION_EMAIL_SUBJECT));
	}
}
