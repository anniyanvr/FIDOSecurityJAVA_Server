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

package org.ebayopensource.fido.uaf.storage;

import java.io.IOException;
import java.security.PublicKey;
import java.sql.Connection;

public interface StorageInterface {

	public void storeServerDataString(String username, String serverDataString);

	public String getUsername(String serverDataString);

	public void store(RegistrationRecord[] records)
			throws DuplicateKeyException, SystemErrorException;

	public RegistrationRecord readRegistrationRecord(String key);

	public void update(RegistrationRecord[] records);

	//Read reg record from DB for auth flow
	public RegistrationRecord readRegistrationRecordfromDB(String string) throws IOException;

	/**
	 * ========================================================================
	 */
	// Database support for mySQL server Database
	//public Connection getConnection();

	//public void displayAllRecords();

	//public void storeInDatabase(String username, String pubKey);
}
