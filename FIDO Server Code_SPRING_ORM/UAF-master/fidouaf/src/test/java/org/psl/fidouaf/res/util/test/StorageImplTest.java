package org.psl.fidouaf.res.util.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.psl.fidouaf.res.util.StorageImpl;

public class StorageImplTest {

	@Test
	public void basic() {
		assertNotNull(StorageImpl.getInstance());
	}

}
