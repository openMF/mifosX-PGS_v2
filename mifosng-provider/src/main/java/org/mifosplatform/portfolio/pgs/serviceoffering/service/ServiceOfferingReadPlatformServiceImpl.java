/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceoffering.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.serviceoffering.data.ServiceOfferingData;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOfferingFeeStructureEnumerations;
import org.mifosplatform.portfolio.pgs.serviceoffering.exception.ServiceOfferingNotFoundException;
import org.mifosplatform.portfolio.pgs.usagerecord.exception.UsageRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceOfferingReadPlatformServiceImpl implements ServiceOfferingReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceOfferingMapper serviceOfferingRowMapper;

    @Autowired
    public ServiceOfferingReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.serviceOfferingRowMapper = new ServiceOfferingMapper();
    }

    private static final class ServiceOfferingMapper implements RowMapper<ServiceOfferingData> {

        final String schema;

        public ServiceOfferingMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("soo.id as id, ");
            sql.append("soo.name as name, ");
            sql.append("soo.description as description, ");
            sql.append("soo.fee_structure_enum as feeStructureId ");
            sql.append("from m_service_offering soo");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ServiceOfferingData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            
            final Integer feeStructureId = JdbcSupport.getInteger(rs, "feeStructureId");
            final EnumOptionData status = ServiceOfferingFeeStructureEnumerations.feeStructure(feeStructureId);
            
            return ServiceOfferingData.instance(id, name, description, status);
        }
    }

    @Override
    public Collection<ServiceOfferingData> retrieveAll(Long serviceAccountId) {
    	try {
	        final String sql = "select " + this.serviceOfferingRowMapper.schema() + " where service_account_id = ?";
	        return this.jdbcTemplate.query(sql, this.serviceOfferingRowMapper, new Object[] { serviceAccountId });
    	} catch (final EmptyResultDataAccessException e) {
            throw new UsageRecordNotFoundException(serviceAccountId);
        }
    }

    @Override
    public ServiceOfferingData retrieveOne(final Long serviceAccountId, final Long resourceId) {
        try {
            final String sql = "select " + this.serviceOfferingRowMapper.schema() + " where service_account_id = ? " + 
            		" and soo.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.serviceOfferingRowMapper, new Object[] { serviceAccountId, resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ServiceOfferingNotFoundException(resourceId);
        }
    }
}