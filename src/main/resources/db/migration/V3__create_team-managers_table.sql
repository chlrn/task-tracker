CREATE TABLE IF NOT EXISTS team_managers (
                               team_id BIGINT NOT NULL,
                               manager_id BIGINT NOT NULL,
                               PRIMARY KEY (team_id, manager_id),
                               FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE CASCADE,
                               FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE
);
