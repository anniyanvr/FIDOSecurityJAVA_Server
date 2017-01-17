package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.exceptions.DuplicateVendorException;
import org.springframework.stereotype.Service;

@Service
public interface VendorService {
	public void saveVendorDetails(VendorDetails vendorDetails)
			throws DuplicateVendorException;

	public VendorDetails getVendorDetails(int accountId)
			throws DataRecordNotFoundException;

	public void updateVendorRegStatusToTrue(VendorDetails vendorDetails);
}
