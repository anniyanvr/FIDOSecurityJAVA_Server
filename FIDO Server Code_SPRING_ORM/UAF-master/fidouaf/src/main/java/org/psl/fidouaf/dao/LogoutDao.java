package org.psl.fidouaf.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface LogoutDao {
	public boolean removeAllRecords();
}
