package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "devicedetails")
public class Device implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 95228057801929353L;

	@Id
	@Column(name = "deviceid")
	String deviceid;

	@Column(name = "devicetoken")
	String devicetoken;

	public Device() {
		super();
	}

	public Device(String deviceid, String devicetoken) {
		super();
		this.deviceid = deviceid;
		this.devicetoken = devicetoken;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDevicetoken() {
		return devicetoken;
	}

	public void setDevicetoken(String devicetoken) {
		this.devicetoken = devicetoken;
	}

	@Override
	public String toString() {
		return "\nDevice Id: " + this.deviceid + "\n Device Token: "
				+ this.devicetoken;
	}
}
