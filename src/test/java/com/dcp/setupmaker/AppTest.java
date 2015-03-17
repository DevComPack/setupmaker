package com.dcp.setupmaker;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.dcp.sm.App;

public class AppTest {

	@Test
	public void testCL() {
		String[] args = {"saves/dcp.dcp"};
		App.main(args);
		assertTrue(new File("DCPSetupMaker-1.2.1.jar").exists());
	}

}
