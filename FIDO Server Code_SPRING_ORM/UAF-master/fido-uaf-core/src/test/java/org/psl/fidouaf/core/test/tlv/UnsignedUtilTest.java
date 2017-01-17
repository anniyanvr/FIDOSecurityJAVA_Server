package org.psl.fidouaf.core.test.tlv;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.psl.fidouaf.core.tlv.ByteInputStream;
import org.psl.fidouaf.core.tlv.TagsEnum;
import org.psl.fidouaf.core.tlv.UnsignedUtil;

public class UnsignedUtilTest {

	@Test
	public void test() throws IOException {
		TagsEnum t = TagsEnum.TAG_ASSERTION_INFO;
		int checkId = UnsignedUtil.read_UAFV1_UINT16(new ByteInputStream(
				UnsignedUtil.encodeInt(t.id)));
		if (checkId != t.id) {
			fail("Conversion error");
		}
	}
}
