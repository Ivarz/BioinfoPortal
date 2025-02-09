CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    given_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE analysis_statuses (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255)
);

-- id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
CREATE TABLE analysis_requests (
    id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    status_id INT NOT NULL,
    fulfilled BOOLEAN NOT NULL,
    failed BOOLEAN NOT NULL,
    details JSONB
);


CREATE TABLE resource_accounts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    balance FLOAT,
    active BOOLEAN,
    CONSTRAINT unique_name UNIQUE (name)
);

CREATE UNIQUE INDEX idx_unique_acc_name ON resource_accounts(name);

CREATE TABLE resource_account_role_mapping (
                                   id SERIAL PRIMARY KEY,
                                   role VARCHAR(255),
                                   resource_account_id INT NOT NULL,
                                   CONSTRAINT fk_acc_id
                                        FOREIGN KEY (resource_account_id)
                                        REFERENCES resource_accounts(id)
                                        ON DELETE CASCADE
);
ALTER TABLE resource_account_role_mapping
    ADD CONSTRAINT unique_role_resource_account UNIQUE (role, resource_account_id);

CREATE TABLE resource_account_permissions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    resource_account_id INT NOT NULL,
    CONSTRAINT fk_user_id_acc_perm
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_resource_account_id_acc_perm
        FOREIGN KEY (resource_account_id)
        REFERENCES resource_accounts(id)
        ON DELETE CASCADE
);
