package org.psl.fidouaf.service.impl;

import org.psl.fidouaf.dao.VendorDao;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.exceptions.DuplicateVendorException;
import org.psl.fidouaf.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorServiceImpl implements VendorService {

	@Autowired
	private VendorDao vendorDao;

	@Override
	public void saveVendorDetails(VendorDetails vendorDetails)
			throws DuplicateVendorException {
		boolean isDuplicateVendor = vendorDao.saveVendor(vendorDetails);
		if (isDuplicateVendor == true) {
			throw new DuplicateVendorException(
					"DUPLICATE RECORD: Vendor with given RP Account Id already exists in DB.");
		} else {
			System.out
					.println("New Vendor Registration details inserted in DB successfully.");
		}
	}

	/**
	 * Method that gets All Important Vendor Details for given RP Account Id.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public VendorDetails getVendorDetails(int accountId)
			throws DataRecordNotFoundException {
		VendorDetails vendorDetails = vendorDao.getVendorDetails(accountId);
		if (vendorDetails == null) {
			throw new DataRecordNotFoundException(
					"Vendor with given RP account Id is not present in Database.");
		} else {
			return vendorDetails;
		}
	}

	/**
	 * Method that marks the Vendor Registration Status to True in Vendor
	 * Details Table.
	 */
	public void updateVendorRegStatusToTrue(VendorDetails vendorDetails) {
		vendorDao.updateVendorRegistrationStatus(vendorDetails);
	}
}
