package org.psl.fidouaf.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.dao.DeviceDao;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceDaoImpl implements DeviceDao {

	/**
	 * Method that adds new device details to DB.
	 */
	public boolean addDevice(Device device) {
		boolean isDuplicateDevice = false;

		Session session = ConnectionUtil.OpenSession();
		Device deviceDetailsFromDB = (Device) session.get(Device.class,
				device.getDeviceid());

		if (deviceDetailsFromDB != null) {
			System.out
					.println("Device with give device Id already exists in DB.");
			isDuplicateDevice = true;
		} else {
			Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				session.save(device);
				transaction.commit();
			} catch (Exception exception) {
				if (transaction != null) {
					transaction.rollback();
					// log the exception
				}
			} finally {
				session.close();
			}
		}
		return isDuplicateDevice;
	}

	/**
	 * Method that finds a Device record based on given Device Id.
	 */
	public Device findDevice(String deviceId) {
		Session session = ConnectionUtil.OpenSession();
		Device device = (Device) session.get(Device.class, deviceId);
		return device;
	}

	/**
	 * Method that prints all data present in deviceDetails table in DB.
	 */
	@SuppressWarnings("unchecked")
	public void showAllDeviceData() {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			List<Device> devices = (List<Device>) session.createQuery(
					"FROM devicedetails").list();
			for (Iterator<Device> iterator = devices.iterator(); iterator
					.hasNext();) {
				Device device = iterator.next();
				System.out.print("Device ID: " + device.getDeviceid());
				System.out.print("Device Token: " + device.getDevicetoken()
						+ "\n");
			}
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}
