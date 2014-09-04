/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.currentaccountinformation.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.data.CurrentAccountInformationData;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.exception.CurrentAccountInformationNotFoundException;
import org.mifosplatform.portfolio.pgs.usagerecord.exception.UsageRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CurrentAccountInformationReadPlatformServiceImpl implements CurrentAccountInformationReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CurrentAccountInformationMapper currentAccountInformationRowMapper;

    @Autowired
    public CurrentAccountInformationReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currentAccountInformationRowMapper = new CurrentAccountInformationMapper();
    }

    private static final class CurrentAccountInformationMapper implements RowMapper<CurrentAccountInformationData> {

        final String schema;

        public CurrentAccountInformationMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("caio.id as id, ");
            sql.append("caio.pgs_client_id as clientId, ");
            sql.append("caio.savings_id as savingsId, ");
            sql.append("caio.balance as balance ");
            sql.append("from m_current_account_information caio");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public CurrentAccountInformationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final long clientId = rs.getLong("clientId");
            final long savingsId = rs.getLong("savingsId");
            final double balance = rs.getDouble("balance");
            
            return CurrentAccountInformationData.instance(id, clientId, savingsId, balance);
        }
    }

    @Override
    public Collection<CurrentAccountInformationData> retrieveAll(Long serviceAccountId) {
    	
    	try{
        final String sql = "select " + this.currentAccountInformationRowMapper.schema() + " where service_account_id = ?";

        return this.jdbcTemplate.query(sql, this.currentAccountInformationRowMapper, new Object[] { serviceAccountId });
    	} catch (final EmptyResultDataAccessException e) {
            throw new UsageRecordNotFoundException(serviceAccountId);
        }
}

    @Override
    public CurrentAccountInformationData retrieveOne(final Long serviceAccountId, final Long resourceId) {
        try {
            final String sql = "select " + this.currentAccountInformationRowMapper.schema() + " where service_account_id = ? " + 
            		" and caio.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.currentAccountInformationRowMapper, new Object[] { serviceAccountId, resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new CurrentAccountInformationNotFoundException(resourceId);
        }
    }
}