package org.psl.fidouaf.service.impl;

import org.psl.fidouaf.dao.DeviceDao;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceDao deviceDao;

	/**
	 * Method that prints all data present in deviceDetails table in DB.
	 */
	public void showAllDeviceData() {
		deviceDao.showAllDeviceData();

	}

	/**
	 * Method that gets Device details For Given DeviceId.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public Device getDeviceFordeviceId(String deviceId)
			throws DataRecordNotFoundException {
		Device device = deviceDao.findDevice(deviceId);
		if (device.equals(null)) {
			throw new DataRecordNotFoundException(
					"Device is not found due to deviceId not present in DB");
		} else {
			return device;
		}
	}

	/**
	 * Method that adds new device details to DB.
	 */
	public boolean addDevice(Device device) {
		boolean isDeviceAdded = false;
		boolean isDuplicate = deviceDao.addDevice(device);

		if (!isDuplicate) {
			isDeviceAdded = true;
		}
		return isDeviceAdded;
	}
}
