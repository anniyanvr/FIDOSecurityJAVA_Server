package org.psl.fidouaf.dao;

import org.psl.fidouaf.entity.Device;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDao {
	public boolean addDevice(Device device);

	public Device findDevice(String deviceId);

	public void showAllDeviceData();
}
