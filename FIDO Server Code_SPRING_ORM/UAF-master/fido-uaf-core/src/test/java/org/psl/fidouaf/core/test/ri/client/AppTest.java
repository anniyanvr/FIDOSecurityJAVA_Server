package org.psl.fidouaf.core.test.ri.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.psl.fidouaf.core.ri.client.App;

public class AppTest {
	App app = new App();

	@Test
	public void end2end() throws Exception {
		app.startRegistration();
		String accessToken = app.uafAuthentication();
		assertNotNull(accessToken);
	}
}
