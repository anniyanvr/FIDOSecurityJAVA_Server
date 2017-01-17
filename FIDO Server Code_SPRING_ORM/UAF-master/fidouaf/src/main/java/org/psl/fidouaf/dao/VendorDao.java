package org.psl.fidouaf.dao;

import org.psl.fidouaf.entity.VendorDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorDao {
	public boolean saveVendor(VendorDetails vendorDetails);

	public VendorDetails getVendorDetails(int accountId);

	public void updateVendorRegistrationStatus(VendorDetails vendorDetails);
}
