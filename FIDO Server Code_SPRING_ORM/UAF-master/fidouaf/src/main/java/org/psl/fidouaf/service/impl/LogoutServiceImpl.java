package org.psl.fidouaf.service.impl;

import org.psl.fidouaf.dao.LogoutDao;
import org.psl.fidouaf.service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {

	@Autowired
	private LogoutDao logoutServicesDao;

	/**
	 * Removes all records from all tables in Database.
	 * 
	 * @return true or false.
	 */
	public boolean removeAllRecords() {
		boolean logoutStatus = logoutServicesDao.removeAllRecords();
		return logoutStatus;
	}

}
