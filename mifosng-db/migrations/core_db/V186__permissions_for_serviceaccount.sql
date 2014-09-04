INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'CREATE_SERVICEACCOUNT', 'GROUP', 'CREATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio_group', 'READ_SERVICEACCOUNT', 'GROUP', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'UPDATE_SERVICEACCOUNT', 'CLIENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'DELETE_SERVICEACCOUNT', 'CLIENT', 'DELETE', 0);

