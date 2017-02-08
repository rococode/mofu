package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.changelog.ChangelogManager;
import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;

@TestClass(enabled = true)
public class ChangelogTests {

	@TestMethod(enabled = true)
	public void testGetChangelog() {
		ChangelogManager um = new ChangelogManager();
		um.getChangelog();
	}
}
