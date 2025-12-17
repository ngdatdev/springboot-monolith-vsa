-- ===============================================================================================
-- BIG DATA SEEDING SCRIPT FOR POSTGRESQL
-- Generates:
--   - 10,000 Users
--   - 1,000 Products + Inventory
--   - 50,000 Orders
--   - ~150,000 Order Items
--   - 50,000 Payments
-- ===============================================================================================

BEGIN;

-- 1. SEED USERS (10,000 records)
INSERT INTO users (email, first_name, last_name, status, created_at, updated_at, version)
SELECT 
    'user_' || i || '@example.com', 
    'FirstName' || i, 
    'LastName' || i, 
    CASE WHEN random() < 0.9 THEN 'ACTIVE' ELSE 'INACTIVE' END,
    NOW() - (random() * interval '365 days'),
    NOW(),
    0
FROM generate_series(1, 10000) AS i
ON CONFLICT (email) DO NOTHING;

RAISE NOTICE 'Seeded 10,000 Users';

-- 2. SEED PRODUCTS (1,000 records)
INSERT INTO products (name, description, sku, price, created_at, updated_at, version)
SELECT 
    'Product ' || i,
    'Description for product ' || i || '. This is a realistic description.',
    'SKU-' || lpad(i::text, 6, '0'),
    (random() * 100 + 10)::numeric(10,2), -- Price between 10.00 and 110.00
    NOW() - (random() * interval '365 days'),
    NOW(),
    0
FROM generate_series(1, 1000) AS i;

RAISE NOTICE 'Seeded 1,000 Products';

-- 3. SEED INVENTORY (1 per Product)
INSERT INTO inventory (product_id, available_quantity, reserved_quantity, last_updated, version)
SELECT 
    id,
    (random() * 500)::int, -- Random stock 0-500
    (random() * 50)::int,  -- Random reserved 0-50
    NOW(),
    0
FROM products;

RAISE NOTICE 'Seeded Inventory';

-- 4. SEED ORDERS (50,000 records)
-- Dates are randomized over the last year
INSERT INTO orders (user_id, status, total_amount, created_at, updated_at, version)
SELECT 
    (floor(random() * 10000) + 1)::bigint, -- Random User ID 1-10000
    CASE 
        WHEN random() < 0.1 THEN 'CREATED'
        WHEN random() < 0.2 THEN 'PENDING_PAYMENT'
        WHEN random() < 0.8 THEN 'PAID'
        WHEN random() < 0.9 THEN 'COMPLETED'
        ELSE 'CANCELLED'
    END,
    0, -- Will update later based on items
    NOW() - (random() * interval '365 days'),
    NOW(),
    0
FROM generate_series(1, 50000) AS i;

RAISE NOTICE 'Seeded 50,000 Orders';

-- 5. SEED ORDER ITEMS (~1-5 items per order)
INSERT INTO order_items (order_id, product_id, product_name_snapshot, price_per_unit_snapshot, quantity)
SELECT 
    o.id,
    p.id,
    p.name,
    p.price,
    (floor(random() * 5) + 1)::int -- Quantity 1-5
FROM orders o
CROSS JOIN LATERAL (
    SELECT * FROM products ORDER BY random() LIMIT (floor(random() * 4) + 1)::int -- 1 to 4 random items per order
) p;

RAISE NOTICE 'Seeded Order Items';

-- 6. UPDATE ORDER TOTALS
UPDATE orders o
SET total_amount = (
    SELECT COALESCE(SUM(oi.price_per_unit_snapshot * oi.quantity), 0)
    FROM order_items oi
    WHERE oi.order_id = o.id
);

RAISE NOTICE 'Updated Order Totals';

-- 7. SEED PAYMENTS (1 per Order for 'PAID', 'COMPLETED', 'CANCELLED' orders)
INSERT INTO payments (order_id, transaction_id, status, amount, failure_reason, created_at)
SELECT 
    id,
    md5(random()::text),
    CASE WHEN status IN ('PAID', 'COMPLETED') THEN 'SUCCESS' ELSE 'FAILED' END,
    total_amount,
    CASE WHEN status = 'CANCELLED' THEN 'Payment Failed or User Cancelled' ELSE NULL END,
    created_at + interval '1 minute'
FROM orders
WHERE status IN ('PAID', 'COMPLETED', 'CANCELLED');

RAISE NOTICE 'Seeded Payments';

-- 8. SEED NOTIFICATIONS (1 per Order)
INSERT INTO notifications (user_id, related_order_id, status, payload_json, created_at, sent_at)
SELECT 
    user_id,
    id,
    'SENT',
    '{"event": "ORDER_CREATED", "orderId": ' || id || '}',
    created_at,
    created_at + interval '5 seconds'
FROM orders
LIMIT 20000; -- Just seed 20k notifications

RAISE NOTICE 'Seeded Notifications';

COMMIT;
