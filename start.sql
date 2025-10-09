CREATE DATABASE bank_db;

CREATE TABLE IF NOT EXISTS transaction (
    transaction_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id INT,
    recipient_id INT,
    sum DECIMAL(7, 2),
    time TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES account (account_id),
    FOREIGN KEY (recipient_id) REFERENCES account (account_id)
);

CREATE TABLE IF NOT EXISTS account (
    account_id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    balance DECIMAL(7, 2)
);

INSERT INTO accout (balance) VALUES (10000);
INSERT INTO accout (balance) VALUES (10000);
INSERT INTO accout (balance) VALUES (10000);
INSERT INTO accout (balance) VALUES (10000);
INSERT INTO accout (balance) VALUES (10000);