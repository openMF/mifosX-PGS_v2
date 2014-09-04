/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.serviceaccount.data.ServiceAccountData;
import org.mifosplatform.portfolio.pgs.serviceaccount.exception.ServiceAccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceAccountReadPlatformServiceImpl implements ServiceAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceAccountMapper serviceAccountRowMapper;

    @Autowired
    public ServiceAccountReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.serviceAccountRowMapper = new ServiceAccountMapper();
    }

    private static final class ServiceAccountMapper implements RowMapper<ServiceAccountData> {

        final String schema;

        public ServiceAccountMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("sao.id as id, ");
            sql.append("sao.pgs_client as pgsClientId, ");
            sql.append("sao.current_account_information as currentAccountInformationId, ");
            sql.append("sao.usage_record as usageRecordId, ");
            sql.append("sao.transaction_record as transactionRecordId, ");
            sql.append("sao.amount as amount, ");
            sql.append("sao.is_activated as isActivated, ");
            sql.append("sao.service_account_status_history as serviceAccountStatusHistoryId, ");
            sql.append("sao.service_offering as serviceAccountId ");
            sql.append("from m_service_account sao");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ServiceAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long pgsClientId = rs.getLong("pgsClientId");
            final Long currentAccountInformationId = rs.getLong("currentAccountInformationId");
            final Long usageRecordId = rs.getLong("usageRecordId");
            final Long transactionRecordId = rs.getLong("transactionRecordId");
            final Double amount = rs.getDouble("amount");
            final Boolean isActivated = rs.getBoolean("isActivated");
            final Long serviceAccountStatusHistoryId = rs.getLong("serviceAccountStatusHistoryId");
            final Long serviceAccountId = rs.getLong("serviceAccountId");
            
            return ServiceAccountData.instance(id, pgsClientId, currentAccountInformationId, usageRecordId, transactionRecordId,
            		amount, isActivated, serviceAccountStatusHistoryId, serviceAccountId);
        }
    }

    @Override
    public Collection<ServiceAccountData> retrieveAll() {

        final String sql = "select " + this.serviceAccountRowMapper.schema();
        //TODO figure out what is wrong with this SQL statement!
        return this.jdbcTemplate.query(sql, this.serviceAccountRowMapper, new Object[] {});
    }

    @Override
    public ServiceAccountData retrieveOne(final Long resourceId) {
        try {
            final String sql = "select " + this.serviceAccountRowMapper.schema() + " where sao.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.serviceAccountRowMapper, new Object[] { resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ServiceAccountNotFoundException(resourceId);
        }
    }
}