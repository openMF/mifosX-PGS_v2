INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'CREATE_TRANSACTIONRECORD', 'GROUP', 'CREATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio_group', 'READ_TRANSACTIONRECORD', 'GROUP', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'UPDATE_TRANSACTIONRECORD', 'CLIENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'DELETE_TRANSACTIONRECORD', 'CLIENT', 'DELETE', 0);

