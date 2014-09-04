/**                                                                                                                                                                               
 * This Source Code Form is subject to the terms of the Mozilla Public                                                                                                            
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,                                                                                                      
 * You can obtain one at http://mozilla.org/MPL/2.0/.                                                                                                                             
 */
package org.mifosplatform.portfolio.pgs.currentaccountinformation.service;

import java.util.Collection;

import org.mifosplatform.portfolio.pgs.currentaccountinformation.data.CurrentAccountInformationData;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformation;

public interface CurrentAccountInformationReadPlatformService {

    Collection<CurrentAccountInformationData> retrieveAll(Long serviceAccountId);

    CurrentAccountInformationData retrieveOne(Long serviceAccountId, Long resourceId);
}

