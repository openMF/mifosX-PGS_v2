/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.transactionrecord.data.TransactionRecordData;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecordTypeEnumerations;
import org.mifosplatform.portfolio.pgs.transactionrecord.exception.TransactionRecordNotFoundException;
import org.mifosplatform.portfolio.pgs.usagerecord.exception.UsageRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TransactionRecordReadPlatformServiceImpl implements TransactionRecordReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionRecordMapper transactionRecordRowMapper;

    @Autowired
    public TransactionRecordReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionRecordRowMapper = new TransactionRecordMapper();
    }

    private static final class TransactionRecordMapper implements RowMapper<TransactionRecordData> {

        final String schema;

        public TransactionRecordMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("tro.id as id, ");
            sql.append("tro.service_account_id as serviceAccountId, ");
            sql.append("tro.description as description, ");
            sql.append("tro.date as date, ");
            sql.append("tro.type_enum as type, ");
            sql.append("tro.amount as amount ");
            sql.append("from m_transaction_record tro");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public TransactionRecordData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long serviceAccountId = rs.getLong("serviceAccountId");
            final String description = rs.getString("description");
            final Date date = rs.getDate("date");
            final Integer typeInteger = JdbcSupport.getInteger(rs, "type");
            final EnumOptionData type = TransactionRecordTypeEnumerations.type(typeInteger);
            final Double amount = rs.getDouble("amount");
            return TransactionRecordData.instance(id, serviceAccountId, description, date, type, amount);
        }
    }

    @Override
    public Collection<TransactionRecordData> retrieveAll(Long serviceAccountId) {
    	try{
    		final String sql = "select " + this.transactionRecordRowMapper.schema() + " where service_account_id = ?";
    		return this.jdbcTemplate.query(sql, this.transactionRecordRowMapper, new Object[] { serviceAccountId });
    	} catch (final EmptyResultDataAccessException e) {
            throw new UsageRecordNotFoundException(serviceAccountId);
        }
    }

    @Override
    public TransactionRecordData retrieveOne(final Long serviceAccountId, final Long resourceId) {
        try {
            final String sql = "select " + this.transactionRecordRowMapper.schema() + " where service_account_id = ? " + 
            		" and tro.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.transactionRecordRowMapper, new Object[] { serviceAccountId, resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new TransactionRecordNotFoundException(resourceId);
        }
    }
}