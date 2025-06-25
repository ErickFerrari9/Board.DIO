-- liquibase formatted sql

-- changeset erick:202506241820
--comment: boards table create
CREATE TABLE BOARDS
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150)
)ENGINE=InnoDB;
--rollback DROP TABLE BOARDS