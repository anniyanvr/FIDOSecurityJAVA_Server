package org.psl.fidouaf.service;


import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface DeviceService {
	public void showAllDeviceData();
	
	public Device getDeviceFordeviceId(String deviceId)  throws DataRecordNotFoundException;
	
	public boolean addDevice(Device device);

}
