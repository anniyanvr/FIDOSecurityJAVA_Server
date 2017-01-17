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

package org.ebayopensource.fido.uaf.msg;



public class FinalChallengeParams {
	public String appID;
	public String challenge;
	public String facetID;
	// public ChannelBinding channelBinding;
	public String channelBinding;

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getFacetID() {
		return facetID;
	}

	public void setFacetID(String facetID) {
		this.facetID = facetID;
	}

	public String getChannelBinding() {
		return channelBinding;
	}

	public void setChannelBinding(String channelBinding) {
		this.channelBinding = channelBinding;
	}

	public String toString (){
		String ret = "\nAppID: " +this.appID + "\nchallenge: " +this.challenge+ "\nFacetId: " +this.facetID+ "\nChannelBinding: " +this.channelBinding+ "\n" ;
		return ret;
}
}
