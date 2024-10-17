-- Clear all data from the transactions table
DELETE FROM transactions;

-- Reset the sequence for the id column if you're using a sequence
-- Uncomment the following line if you're using a sequence for the id column
-- ALTER SEQUENCE transactions_id_seq RESTART WITH 1;

-- Clear all data from the users table
DELETE FROM users;

-- Reset the sequence for the id column if you're using a sequence
-- Uncomment the following line if you're using a sequence for the id column
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;