
-- Q1: 52 Weeks of sales data

\echo '\nQ1: Checking order count per week...'
\echo '\nExpected: One row per week'
SELECT 
    DATE_TRUNC('week', created_at) AS week_start, COUNT(*) AS order_count
FROM orders
GROUP BY DATE_TRUNC('week', created_at)
ORDER BY week_start;
\echo 'Q1 end'

-----------------------------------------------

-- Q2: Orders by hour
\echo '\nQ2: Check order quantity and totals by hour...'
\echo '\nExpected: 11 rows representing hours from 10am up to but not beyond 9pm'
SELECT 
    EXTRACT(HOUR FROM o.created_at)::INT AS hour_of_day,
    COUNT(*) AS total_orders,
    ROUND(SUM(o.order_total)::NUMERIC, 2) AS total_sales
FROM orders AS o
GROUP BY EXTRACT(HOUR FROM o.created_at)
ORDER BY hour_of_day;

\echo 'Q2 end'

-----------------------------------------------

-- Q3: 2 highest sales days

\echo '\nQ3: Checking for 2 peak sales days...'
\echo '\nExpected: 2024-10-04 and 2025-11-26 should be the top 2 days'
SELECT
    created_at::date AS day,
    COUNT(*) AS order_count,
    ROUND(SUM(order_total)::NUMERIC, 2) AS total_sold
FROM orders
GROUP BY created_at::date
ORDER BY total_sold DESC
LIMIT 10;
\echo 'Q3 end'

-----------------------------------------------

-- Q4: Inventory items for 20 menu items
\echo '\nQ4: Checking inventory items for 20 items...'
\echo '\nExpected: 20 rows that match the ingredient mapping'
SELECT 
    mi.menu_item_id, 
    mi.menu_item_name, 
    COUNT(i.ingredient_id) AS ingredient_count
FROM menu_items AS mi
JOIN menu_item_details AS mid ON mi.menu_item_id = mid.menu_item_id
JOIN ingredients AS i ON i.ingredient_id = mid.ingredient_id
GROUP BY mi.menu_item_id, mi.menu_item_name
ORDER BY mi.menu_item_id ASC
LIMIT 20;
\echo 'Q4 end'

-----------------------------------------------

-- Q5: best of the worst

\echo '\nQ5: Checking best selling item on worst sales day...'
\echo '\nExpected: 1 row with the best item on the worst sales day, to be checked according to random data'
WITH worst_day AS (
    SELECT DATE_TRUNC('day', created_at) AS day_start, COUNT(*) AS order_count
    FROM orders
    GROUP BY DATE_TRUNC('day', created_at)
    ORDER BY order_count ASC
    LIMIT 1
)
SELECT
    mi.menu_item_id,
    mi.menu_item_name,
    SUM(od.order_qty) AS sold_total,
    wd.day_start
FROM orders o
JOIN worst_day wd ON DATE_TRUNC('day', o.created_at) = wd.day_start
JOIN order_details od ON od.order_id = o.order_id
JOIN combo_details AS cd on cd.order_detail_id = od.order_detail_id
JOIN menu_items AS mi ON mi.menu_item_id = cd.menu_item_id
GROUP BY mi.menu_item_id, mi.menu_item_name, wd.day_start
ORDER BY sold_total DESC
LIMIT 1;
\echo 'Q5 end'

-----------------------------------------------

-- Q6: Check ingredients below restock level

\echo '\nQ6: Checking which items need to be restocked...'
\echo '\nExpected: Always at least 6 items (sesame seeds, egg roll wrappers, spring roll wrappers, wonton wrappers, eggs, and black pepper)'
SELECT i.ingredient_id, i.ingredient_name, i.quantity_in_stock, i.min_qty_on_hand, i.measurement_units
FROM ingredients AS i
WHERE i.quantity_in_stock < i.min_qty_on_hand;

\echo 'Q6 end'

-----------------------------------------------

-- Q7: Orders are only after 10am, before 9pm, and weekdays only

\echo '\nQ7: Checking orders between valid times...'
\echo '\nExpected: No output since it means the script found no errors'
\echo 'ck error is expected on initial validation run'

ALTER TABLE orders DROP CONSTRAINT IF EXISTS ck_operating_hours;
ALTER TABLE orders
ADD CONSTRAINT ck_operating_hours
CHECK (
    CAST(created_at AS time) >= '10:00:00'
    AND CAST(created_at AS time) <= '21:00:00'
    AND EXTRACT(ISODOW FROM created_at) < 6
);

\echo 'Q7 end'

-----------------------------------------------

-- Q8: Breakdown of Menu Items by storage type of ingredients

\echo '\nQ8: Checking how many storage type ingredients go into each menu item...'
\echo '\nExpected: Rows all match the ingredient storage mapping ex: 1: 1/3/0 and 15: 1/4/0'
SELECT m.menu_item_id, m.menu_item_name, 
    COUNT(*) FILTER (WHERE i.storage_type = 'pantry') AS pantry_ingredients,
    COUNT(*) FILTER (WHERE i.storage_type = 'refrigerator') AS refrigerator_ingredients,
    COUNT(*) FILTER (WHERE i.storage_type = 'freezer') AS freezer_ingredients
FROM menu_items AS m
JOIN menu_item_details AS md ON md.menu_item_id = m.menu_item_id
JOIN ingredients as i ON i.ingredient_id = md.ingredient_id
GROUP BY m.menu_item_id, m.menu_item_name
ORDER BY m.menu_item_id ASC;


\echo 'Q8 end'

-----------------------------------------------

-- Q9: 

\echo '\nQ9: Checking all menu items that have rice...'
\echo '\nExpected: 3 rows containing fried rice, white rice, and super greens'
SELECT m.menu_item_id, m.menu_item_name FROM menu_items AS m
JOIN menu_item_details AS md ON m.menu_item_id = md.menu_item_id
JOIN ingredients AS i ON i.ingredient_id = md.ingredient_id
WHERE i.ingredient_name = 'Rice';

-----------------------------------------------