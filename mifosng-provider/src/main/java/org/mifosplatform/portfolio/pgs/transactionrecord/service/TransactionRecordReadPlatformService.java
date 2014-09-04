/**                                                                                                                                                                               
 * This Source Code Form is subject to the terms of the Mozilla Public                                                                                                            
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,                                                                                                      
 * You can obtain one at http://mozilla.org/MPL/2.0/.                                                                                                                             
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.service;

import java.util.Collection;

import org.mifosplatform.portfolio.pgs.transactionrecord.data.TransactionRecordData;

public interface TransactionRecordReadPlatformService {

    Collection<TransactionRecordData> retrieveAll(Long serviceAccountId);

    TransactionRecordData retrieveOne(Long serviceAccountId, Long resourceId);
}

