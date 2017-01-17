package org.psl.fidouaf.service.impl;

import org.psl.fidouaf.dao.DeviceDao;
import org.psl.fidouaf.dao.VendorDao;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.service.QRVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QRVerificationServiceImpl implements QRVerificationService {
	@Autowired
	private VendorDao vendorDao;

	@Autowired
	private DeviceDao deviceDao;

	/**
	 * Method that verifies the QR code details scanned for the vendor
	 * registration.
	 */
	public boolean isQRVerified(VendorDetails vendorDetails) {
		boolean isQRVerified = false;

		String rpdisplayName = vendorDetails.getVendorName();
		String displayname = vendorDetails.getUserName();
		String email = vendorDetails.getEmail();
		String otp = vendorDetails.getOtp();
		int accountId = vendorDetails.getAccountid();

		VendorDetails vendorDetailsFromDB = vendorDao
				.getVendorDetails(accountId);
		if (vendorDetailsFromDB != null
				&& rpdisplayName.equalsIgnoreCase(vendorDetailsFromDB
						.getVendorName())
				&& displayname.equalsIgnoreCase(vendorDetailsFromDB
						.getUserName())
				&& email.equalsIgnoreCase(vendorDetailsFromDB.getEmail())
				&& otp.equalsIgnoreCase(vendorDetailsFromDB.getOtp())
				&& accountId == vendorDetailsFromDB
						.getAccountid()) {
			System.out.println("\nQR Data Verified successfully.");

			isQRVerified = true;
		}
		return isQRVerified;
	}
}