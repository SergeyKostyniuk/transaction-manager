-- Insert test transactions with different statuses
INSERT INTO transactions (id, user_id, amount, status, created_at)
VALUES
    (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 100.00, 'PENDING', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 200.00, 'PENDING', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 300.00, 'APPROVED', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 400.00, 'REJECTED', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 500.00, 'AWAITING_APPROVAL', CURRENT_TIMESTAMP);