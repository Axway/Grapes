package org.axway.grapes.server.core;

import static org.junit.Assert.*;

import java.io.File;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import static org.axway.grapes.server.config.Messages.*;
import org.axway.grapes.server.core.services.email.GrapesEmailSender;
import static org.axway.grapes.server.core.services.email.MessageKey.*;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessagesTest {

	@Test
	public void simpleMessageTest() {
		final String templatePath = GrapesTestUtils.class.getResource("all-messages.txt").getPath();
		init(templatePath);
		assertEquals("msg_4", getMessage(ARTIFACT_IS_PROMOTED));
	}

	@Test
	public void keyNotFoundTest() {
		final String templatePath = GrapesTestUtils.class.getResource("messages-missing-subject.txt").getPath();
		init(templatePath);
		assertEquals(ARTIFACT_VALIDATION_EMAIL_SUBJECT.toString(), getMessage(ARTIFACT_VALIDATION_EMAIL_SUBJECT));
	}
}
