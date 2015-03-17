package com.dcp.setupmaker;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.dcp.sm.App;

public class AppTest {

    @Ignore
	@Test
	public void testCL() {
		String[] args = {"saves/dcp.dcp"};
		App.main(args);
		assertTrue(new File("DCPSetupMaker-1.2.1.jar").exists());
	}

	@Test
	public void test() {
		System.out.println("TEST OK");
		assertTrue(true);
	}
	
}
