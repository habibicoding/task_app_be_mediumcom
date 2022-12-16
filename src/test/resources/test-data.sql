INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `priority`)
VALUES (111, 'first test todo', false, false, CURRENT_TIME(), 'LOW');

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `priority`)
VALUES (112, 'second test todo', true, false, CURRENT_TIME(), 'MEDIUM');

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `priority`)
VALUES (113, 'third test todo', true, true, CURRENT_TIME(), 'HIGH');
