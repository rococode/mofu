package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.test.Test;
import com.edasaki.misakachan.updates.ChangelogManager;

public class UpdateTests {

	@Test(enabled = false)
	public void testGetChangelog() {
		ChangelogManager um = new ChangelogManager();
		um.getChangelog();
	}
}
