-- liquibase formatted sql

-- changeset erick:240620251800
CREATE TABLE BOARDS (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(150)
);
--rollback DROP TABLE BOARDS