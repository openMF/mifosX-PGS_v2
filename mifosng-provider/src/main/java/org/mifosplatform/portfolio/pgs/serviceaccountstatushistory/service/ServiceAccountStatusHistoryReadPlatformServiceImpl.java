/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.data.ServiceAccountStatusHistoryData;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.exception.ServiceAccountStatusHistoryNotFoundException;
import org.mifosplatform.portfolio.pgs.usagerecord.exception.UsageRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceAccountStatusHistoryReadPlatformServiceImpl implements ServiceAccountStatusHistoryReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceAccountStatusHistoryMapper serviceAccountStatusHistoryRowMapper;

    @Autowired
    public ServiceAccountStatusHistoryReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.serviceAccountStatusHistoryRowMapper = new ServiceAccountStatusHistoryMapper();
    }

    private static final class ServiceAccountStatusHistoryMapper implements RowMapper<ServiceAccountStatusHistoryData> {

        final String schema;

        public ServiceAccountStatusHistoryMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("sasho.id as id, ");
            sql.append("sasho.service_account_id as serviceAccountId, ");
            sql.append("sasho.date as date, ");
            sql.append("sasho.change_enum as changeEnum ");
            sql.append("from m_service_account_status_history sasho");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ServiceAccountStatusHistoryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long serviceAccountId = rs.getLong("serviceAccountId");
            final Date date = rs.getDate("date");
            
            final Integer change = JdbcSupport.getInteger(rs, "changeEnum");
            //final EnumOptionData status = ServiceAccountStatusHistoryFeeStructureEnumerations.feeStructure(feeStructureId);
            
            return ServiceAccountStatusHistoryData.instance(id, serviceAccountId, date, change);
        }
    }

    @Override
    public Collection<ServiceAccountStatusHistoryData> retrieveAll(Long serviceAccountId) {
    	try{
    		final String sql = "select " + this.serviceAccountStatusHistoryRowMapper.schema() + " where service_account_id = ?";
    		return this.jdbcTemplate.query(sql, this.serviceAccountStatusHistoryRowMapper, new Object[] { serviceAccountId });
    	} catch (final EmptyResultDataAccessException e) {
            throw new UsageRecordNotFoundException(serviceAccountId);
        }	
    }

    @Override
    public ServiceAccountStatusHistoryData retrieveOne(final Long serviceAccountId, final Long resourceId) {
        try {
            final String sql = "select " + this.serviceAccountStatusHistoryRowMapper.schema() + " where service_account_id = ? " +
            		" and sasho.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.serviceAccountStatusHistoryRowMapper, new Object[] { serviceAccountId, resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ServiceAccountStatusHistoryNotFoundException(resourceId);
        }
    }
}