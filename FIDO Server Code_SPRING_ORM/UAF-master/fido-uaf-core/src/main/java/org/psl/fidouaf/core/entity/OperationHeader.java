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

package org.psl.fidouaf.core.entity;

public class OperationHeader {
	public Version upv;
	public Operation op;
	public String appID;
	public String serverData;
	
	public OperationHeader(){
		
	}

	public OperationHeader(Version upv, Operation op, String appID,
			String serverData) {
		super();
		this.upv = upv;
		this.op = op;
		this.appID = appID;
		this.serverData = serverData;
	}

	public Version getUpv() {
		return upv;
	}

	public void setUpv(Version upv) {
		this.upv = upv;
	}

	public Operation getOp() {
		return op;
	}

	public void setOp(Operation op) {
		this.op = op;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getServerData() {
		return serverData;
	}

	public void setServerData(String serverData) {
		this.serverData = serverData;
	}

	// public Extension[] exts;
}
