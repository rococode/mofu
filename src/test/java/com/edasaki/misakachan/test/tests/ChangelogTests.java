package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.changelog.ChangelogManager;
import com.edasaki.misakachan.test.Test;

public class ChangelogTests {

	@Test(enabled = true)
	public void testGetChangelog() {
		ChangelogManager um = new ChangelogManager();
		um.getChangelog();
	}
}
