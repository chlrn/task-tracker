CREATE TABLE IF NOT EXISTS task_status (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(255) UNIQUE NOT NULL,
                             team_id BIGINT NOT NULL,
                             CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES team(id)
);
