INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'CREATE_CURRENTACCOUNTINFORMATION', 'GROUP', 'CREATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio_group', 'READ_CURRENTACCOUNTINFORMATION', 'GROUP', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'UPDATE_CURRENTACCOUNTINFORMATION', 'CLIENT', 'UPDATE', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'DELETE_CURRENTACCOUNTINFORMATION', 'CLIENT', 'DELETE', 0);