-- Create users table
CREATE TABLE users
(
    id      UUID PRIMARY KEY NOT NULL,
    name    VARCHAR(255)     NOT NULL,
    balance DECIMAL(19, 2)   NOT NULL
);

-- Create partitioned transactions table
CREATE TABLE transactions
(
    id         UUID           NOT NULL,
    user_id    UUID           NOT NULL,
    amount     DECIMAL(19, 2) NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Create initial partition
CREATE TABLE transactions_y2024m01 PARTITION OF transactions
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Create function to create new partitions
CREATE OR REPLACE FUNCTION create_partition_and_insert()
    RETURNS TRIGGER AS
$$
DECLARE
    partition_date TEXT;
    partition_name TEXT;
BEGIN
    partition_date := TO_CHAR(NEW.created_at, 'YYYY_MM');
    partition_name := 'transactions_y' || partition_date;

    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = partition_name) THEN
        EXECUTE format('CREATE TABLE %I PARTITION OF transactions
                        FOR VALUES FROM (%L) TO (%L)',
                       partition_name,
                       DATE_TRUNC('month', NEW.created_at),
                       DATE_TRUNC('month', NEW.created_at) + INTERVAL '1 month');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically create new partitions
CREATE TRIGGER insert_transaction_trigger
    BEFORE INSERT
    ON transactions
    FOR EACH ROW
EXECUTE FUNCTION create_partition_and_insert();

-- Create index on user_id and created_at for each partition
CREATE INDEX ON transactions (user_id, created_at);

-- Create index on created_at for partitioning performance
CREATE INDEX ON transactions (created_at);

-- Add foreign key constraint
ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id) REFERENCES users (id);