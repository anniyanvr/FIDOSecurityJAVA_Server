package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.VendorDetails;
import org.springframework.stereotype.Service;

@Service
public interface QRVerificationService {
	public boolean isQRVerified(VendorDetails vendorDetails);
}
