/**                                                                                                                                                                               
 * This Source Code Form is subject to the terms of the Mozilla Public                                                                                                            
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,                                                                                                      
 * You can obtain one at http://mozilla.org/MPL/2.0/.                                                                                                                             
 */
package org.mifosplatform.portfolio.pgs.serviceoffering.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.sms.data.SmsData;
import org.mifosplatform.portfolio.pgs.serviceoffering.data.ServiceOfferingData;

public interface ServiceOfferingReadPlatformService {

    Collection<ServiceOfferingData> retrieveAll(Long serviceAccountId);

    ServiceOfferingData retrieveOne(Long serviceAccountId, Long resourceId);
}

