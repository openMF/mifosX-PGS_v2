/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.usagerecord.data.UsageRecordData;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecordMetricEnumerations;
import org.mifosplatform.portfolio.pgs.usagerecord.exception.UsageRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.sun.istack.FinalArrayList;

@Service
public class UsageRecordReadPlatformServiceImpl implements UsageRecordReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final UsageRecordMapper usageRecordRowMapper;

    @Autowired
    public UsageRecordReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.usageRecordRowMapper = new UsageRecordMapper();
    }

    private static final class UsageRecordMapper implements RowMapper<UsageRecordData> {

        final String schema;

        public UsageRecordMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("uro.id as id, ");
            sql.append("uro.service_account_id as serviceAccountId, ");
            sql.append("uro.date as date, ");
            sql.append("uro.metric_type_enum as metricType, ");
            sql.append("uro.number_of_units as noOfUnits, ");
            sql.append("uro.start_date as startDate, ");
            sql.append("uro.end_date as endDate ");
            sql.append("from m_usage_record uro");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public UsageRecordData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long serviceAccountId = rs.getLong("serviceAccountId");
            final Date date = rs.getDate("date");
            final Integer metricTypeId = rs.getInt("metricType");
            final EnumOptionData metricType = UsageRecordMetricEnumerations.metricType(metricTypeId);
            final Double noOfUnits = rs.getDouble("noOfUnits");
            final Date startDate = rs.getDate("startDate");
            final Date endDate = rs.getDate("endDate");
            final DateTime startDateTime = new DateTime(startDate);
            final DateTime endDateTime = new DateTime(endDate);
            return UsageRecordData.instance(id, serviceAccountId, date, metricType, noOfUnits, startDateTime, endDateTime);
        }
    }
    
    @Override
	public Collection<UsageRecordData> retrieveAll(Long serviceAccountId) {
    	try {
    			final String sql = "select " + this.usageRecordRowMapper.schema() + " where service_account_id = ?";
    			return this.jdbcTemplate.query(sql, this.usageRecordRowMapper, new Object[] { serviceAccountId });
	        } catch (final EmptyResultDataAccessException e) {
	            throw new UsageRecordNotFoundException(serviceAccountId);
	        }
	}
    @Override
    public UsageRecordData retrieveOne(final Long serviceAccountId, final Long resourceId) {
        try {
            final String sql = "select " + this.usageRecordRowMapper.schema() + " where service_account_id = ? " + 
            		" and uro.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.usageRecordRowMapper, new Object[] { serviceAccountId, resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new UsageRecordNotFoundException(resourceId);
        }
    }
}