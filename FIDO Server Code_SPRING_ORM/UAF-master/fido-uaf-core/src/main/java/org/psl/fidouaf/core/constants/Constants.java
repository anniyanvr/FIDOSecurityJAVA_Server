/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.psl.fidouaf.core.constants;

public interface Constants {

	public static final String APP_ID = "https://www.head2toes.org/fidouaf/v1/public/uaf/facets";
	public static final String FACET_ID = "https://www.head2toes.org";
	public static final String AAID = "EBA0#0101";

	public static final String SUCCESS_RESPONSE_STATUS = "SUCCESS";
	public static final String FAILURE_RESPONSE_STATUS = "FAILURE";
	public static final String NOTIFIED = "NOTIFIED";

	public static final String DUPLICATE_PUBLIC_KEY = "DUPLICATE PUBLIC KEY";
	public static final String DUPLICATE_AAID_KEYID = "DUPLICATE AAID KEYID";
	public static final String INSERT_SUCCESSFULL = "INSERT SUCCESSFULL";

	public static final int SERVER_DATA_EXPIRY_IN_MS = 5 * 60 * 1000;

	public static final String API_KEY = "AIzaSyAKxN6RAkSXjvpLK2j8zASsKo6Yub_wSH8";
	public static final String URL = "https://fcm.googleapis.com/fcm/send";

	public static final String LAST_REG_REQ = "LAST_REG_REQ";
	public static final String LAST_REG_RES = "LAST_REG_RES";
	public static final String LAST_AUTH_REQ = "LAST_AUTH_REQ";
	public static final String LAST_AUTH_RES = "LAST_AUTH_RES";
	public static final String LAST_AUTH_RES_REPLY = "LAST_AUTH_RES_REPLY";
	public static final String LAST_DEREG_REQ = "LAST_DEREG_REQ";

	public static final String SOME_OTHER_STRING = "SomeOtherString";
	public static final String SOME_STRING = "SomeString";

	public static final String TEST_PRIV_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgzqOJl-rC0FFMMFM7w7sqp99jsBxgMx_fqwuaUc4CVv-gCgYIKoZIzj0DAQehRANCAAQokXIHgAc20GWpznnnIX9eD2btK-R-uWUFgOKt8l27RcrrOrqJ66uCMfOuG4I1usUUOa7f_A19v74FC-HuSB50";
	public static final String TEST_PUB_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEKJFyB4AHNtBlqc555yF_Xg9m7SvkfrllBYDirfJdu0XK6zq6ieurgjHzrhuCNbrFFDmu3_wNfb--BQvh7kgedA==";

	public static final String TEST_SIGNATURE = "test_signature";
	public static final String TEST_USERNAME = "testUsername";
}
