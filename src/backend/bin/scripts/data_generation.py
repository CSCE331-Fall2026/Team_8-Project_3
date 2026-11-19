import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(42)
PEAK_DAYS = [datetime(2024, 10, 4), datetime(2025, 11, 26)]
PEAK_COPIES = 2

# menu_item_id, menu_item_name, premium_charge, menu_item_type
MENU_ITEMS = [
    (1, "The Original Orange Chicken", 0.0, "entree"),
    (2, "Honey Walnut Shrimp (Premium)", 2.50, "entree"),
    (3, "Beijing Beef", 0.0, "entree"),
    (4, "Broccoli Beef", 0.0, "entree"),
    (5, "Grilled Teriyaki Chicken", 0.0, "entree"),
    (6, "Kung Pao Chicken", 0.0, "entree"),
    (7, "Honey Sesame Chicken Breast", 0.0, "entree"),
    (8, "Black Pepper Chicken", 0.0, "entree"),
    (9, "Mushroom Chicken", 0.0, "entree"),
    (10, "String Bean Chicken Breast", 0.0, "entree"),
    (11, "SweetFire Chicken Breast", 0.0, "entree"),
    (12, "Black Pepper Sirloin Steak (Premium)", 2.50, "entree"),
    (13, "Chow Mein", 0.0, "side"),
    (14, "Fried Rice", 0.0, "side"),
    (15, "Super Greens", 0.0, "side"),
    (16, "White Steamed Rice", 0.0, "side"),
    (17, "Brown Steamed Rice", 0.0, "side"),
    (18, "Chicken Egg Roll", 2.00, "appetizer"),
    (19, "Veggie Spring Roll", 1.75, "appetizer"),
    (20, "Cream Cheese Rangoon", 2.35, "appetizer"),
    (21, "Chicken Potstickers", 4.95, "appetizer"),
    (22, "Apple Pie Roll", 2.25, "appetizer"),
    (23, "Soda", 2.19, "drink"),
]

# ingredient_id, ingredient_name, qty_in_stock, storage_type, ingredient_cost, min_qty_on_hand, measurement_units, shelf_life_days
INGREDIENTS = [
    (1, "Rice", 180.0, "pantry", 0.55, 40.0, "lb", 21),
    (2, "Brown Rice", 170.0, "pantry", 0.60, 40.0, "lb", 21),
    (3, "Chicken", 75.0, "refrigerator", 2.25, 50.0, "lb", 14),
    (4, "Beef", 42.0, "freezer", 3.30, 40.0, "lb", 31),
    (5, "Shrimp", 32.0, "freezer", 3.80, 20.0, "lb", 31),
    (6, "Broccoli", 80.0, "refrigerator", 0.70, 35.0, "lb", 21),
    (7, "Mushrooms", 48.0, "refrigerator", 0.95, 20.0, "lb", 21),
    (8, "Green Beans", 60.0, "refrigerator", 0.85, 20.0, "lb", 21),
    (9, "Bell Pepper", 70.0, "refrigerator", 0.60, 20.0, "lb", 21),
    (10, "Onion", 85.0, "pantry", 0.30, 30.0, "lb", 14),
    (11, "Carrots", 95.0, "refrigerator", 0.45, 30.0, "lb", 21),
    (12, "Pineapple", 45.0, "refrigerator", 0.90, 20.0, "lb", 14),
    (13, "Orange Sauce", 90.0, "pantry", 0.75, 50.0, "bottle", 31),
    (14, "Walnuts", 35.0, "pantry", 1.10, 20.0, "lb", 31),
    (15, "Honey", 50.0, "pantry", 0.90, 30.0, "bottle", 365),
    (16, "Sesame Seeds", 25.0, "pantry", 0.50, 72.0, "oz", 62),
    (17, "Soy Sauce", 95.0, "pantry", 0.35, 40.0, "bottle", 180),
    (18, "Chow Mein Noodles", 110.0, "refrigerator", 0.80, 75.0, "lb", 21),
    (19, "Egg Roll Wrapper", 70.0, "pantry", 0.25, 100.0, "each", 365),
    (20, "Spring Roll Wrapper", 70.0, "pantry", 0.40, 100.0, "each", 365),
    (21, "Cream Cheese", 30.0, "refrigerator", 1.20, 25.0, "lb", 21),
    (22, "Wonton Wrapper", 75.0, "pantry", 0.30, 100.0, "each", 365),
    (23, "Apple Pie Filling", 40.0, "refrigerator", 0.85, 20.0, "lb", 31),
    (24, "Cucumber", 60.0, "refrigerator", 0.40, 40.0, "lb", 14),
    (25, "Cabbage", 65.0, "refrigerator", 0.35, 50.0, "lb", 14),
    (26, "Tomato", 55.0, "refrigerator", 0.50, 50.0, "lb", 14),
    (27, "Garlic", 45.0, "pantry", 0.20, 35.0, "head", 21),
    (28, "Ginger", 35.0, "refrigerator", 0.25, 20.0, "lb", 14),
    (29, "Egg", 80.0, "refrigerator", 0.35, 120.0, "each", 21),
    (30, "Sweet Chili Sauce", 55.0, "pantry", 0.70, 50.0, "bottle", 180),
    (31, "Teriyaki Sauce", 60.0, "pantry", 0.65, 50.0, "bottle", 180),
    (32, "Black Pepper", 28.0, "pantry", 0.22, 48.0, "oz", 180),
    (33, "Carbonated Water", 200.0, "pantry", 0.08, 150.0, "liter", 365),
    (34, "Soda Syrup", 120.0, "pantry", 0.90, 100.0, "liter", 180),
    (35, "Cup", 250.0, "pantry", 0.08, 100.0, "each", 1000),
    (36, "Bowl", 200.0, "pantry", 0.12, 80.0, "each", 1000),
    (37, "Plate", 200.0, "pantry", 0.15, 80.0, "each", 1000),
]

MENU_INGREDIENT_MAP = {
    "The Original Orange Chicken": ["Chicken", "Broccoli", "Carrots", "Orange Sauce"],
    "Honey Walnut Shrimp (Premium)": ["Shrimp", "Walnuts", "Honey"],
    "Beijing Beef": ["Beef", "Bell Pepper", "Onion", "Soy Sauce"],
    "Broccoli Beef": ["Beef", "Broccoli", "Onion", "Soy Sauce"],
    "Grilled Teriyaki Chicken": ["Chicken", "Broccoli", "Carrots", "Teriyaki Sauce"],
    "Kung Pao Chicken": ["Chicken", "Bell Pepper", "Onion", "Soy Sauce"],
    "Honey Sesame Chicken Breast": ["Chicken", "Broccoli", "Honey", "Sesame Seeds"],
    "Black Pepper Chicken": ["Chicken", "Bell Pepper", "Onion", "Black Pepper"],
    "Mushroom Chicken": ["Chicken", "Mushrooms", "Onion", "Broccoli"],
    "String Bean Chicken Breast": ["Chicken", "Green Beans", "Onion", "Soy Sauce"],
    "SweetFire Chicken Breast": ["Chicken", "Bell Pepper", "Pineapple", "Onion", "Sweet Chili Sauce"],
    "Black Pepper Sirloin Steak (Premium)": ["Beef", "Bell Pepper", "Onion", "Black Pepper"],
    "Chow Mein": ["Chow Mein Noodles", "Cabbage", "Carrots", "Onion", "Soy Sauce"],
    "Fried Rice": ["Rice", "Egg", "Carrots", "Onion", "Soy Sauce"],
    "Super Greens": ["Broccoli", "Green Beans", "Carrots", "Mushrooms"],
    "White Steamed Rice": ["Rice"],
    "Brown Steamed Rice": ["Brown Rice"],
    "Chicken Egg Roll": ["Chicken", "Cabbage", "Carrots", "Egg Roll Wrapper", "Soy Sauce"],
    "Veggie Spring Roll": ["Spring Roll Wrapper", "Cabbage", "Carrots", "Cucumber", "Onion"],
    "Cream Cheese Rangoon": ["Cream Cheese", "Wonton Wrapper", "Egg"],
    "Chicken Potstickers": ["Chicken", "Wonton Wrapper", "Cabbage", "Ginger", "Garlic"],
    "Apple Pie Roll": ["Apple Pie Filling", "Wonton Wrapper", "Egg"],
    "Soda": ["Carbonated Water", "Soda Syrup", "Cup"],
}

# Combo options for the code to pick from
COMBOS = [
    (1, "Bowl", 8.99),
    (2, "Plate", 10.99),
    (3, "Bigger Plate", 12.99),
    (4, "A la carte", 4.99),
    (5, "Drink", 2.99),
    (6, "Bowl Combo", 10.99),
    (7, "Plate Combo", 12.99),
    (8, "Bigger Plate Combo", 14.99),
    (9, "Appetizer", 0.00)
] 

# Categorizing items
ENTREES = list(range(1, 13))
SIDES   = [13, 14, 15, 16, 17]
APPS    = [18, 19, 20, 21, 22]
DRINKS  = [23]

# Categorizing what each combo has
COMBO_ELIGIBLE = {
    "Bowl":        ENTREES + SIDES,
    "Plate":       ENTREES + SIDES,
    "Bigger Plate": ENTREES + SIDES,
    "A la carte":  ENTREES + SIDES + APPS + DRINKS,
    "Drink":       DRINKS,
    "Bowl Combo":  ENTREES + SIDES,
    "Plate Combo": ENTREES + SIDES,
    "Bigger Plate Combo": ENTREES + SIDES,
    "Appetizer":   APPS,
}


def iso_timestamp(value: datetime) -> str:
    return value.isoformat(timespec="seconds")


def random_datetime(start_date: datetime, end_date: datetime) -> datetime:
    delta_seconds = int((end_date - start_date).total_seconds())
    random_seconds = random.randint(0, max(delta_seconds, 1))
    return start_date + timedelta(seconds=random_seconds)


def format_sql_value(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, datetime):
        return "'" + iso_timestamp(value).replace("T", " ") + "'"
    return "'" + str(value).replace("'", "''") + "'"


def build_insert_sql(table_name, columns, rows):
    if not rows:
        return ""
    value_rows = []
    for row in rows:
        value_rows.append("(" + ", ".join(format_sql_value(v) for v in row) + ")")
    return f"INSERT INTO {table_name} ({', '.join(columns)}) VALUES\n" + ",\n".join(value_rows) + ";\n\n"

def build_sequence_resets(tables):
    sql = ""
    for table, pk in tables:
        sql += f"SELECT setval(pg_get_serial_sequence('{table}', '{pk}'), (SELECT MAX({pk}) FROM {table}));\n"
    return sql + "\n"

def generate_menu_items():
    return [
        {
            "menu_item_id": item_id,
            "menu_item_name": name,
            "premium_charge": premium_charge,
            "menu_item_type": menu_item_type,
        }
        for item_id, name, premium_charge, menu_item_type in MENU_ITEMS
    ]


def generate_static_rows():
    menu_items = generate_menu_items()
    combos = generate_combos()
    menu_item_details_rows = generate_menu_item_details_rows(menu_items)
    ingredient_rows = build_ingredient_rows()

    return {
        "menu_items": menu_items,
        "combos": combos,
        "menu_item_details_rows": menu_item_details_rows,
        "ingredient_rows": ingredient_rows,
    }


def build_ingredient_rows():
    now = datetime.now()
    ingredient_rows = []
    for ingredient_id, name, quantity_in_stock, storage_type, ingredient_cost, min_qty_on_hand, measurement_units, shelf_life_days in INGREDIENTS:
        ingredient_rows.append(
            {
                "ingredient_id": ingredient_id,
                "ingredient_name": name,
                "quantity_in_stock": round(random.uniform(quantity_in_stock * 0.85, quantity_in_stock * 1.15), 0),
                "storage_type": storage_type,
                "ingredient_cost": round(ingredient_cost + random.uniform(-0.15, 0.35), 2),
                "expiry_date": now + timedelta(days=random.randint(-5, shelf_life_days)),
                "min_qty_on_hand": min_qty_on_hand,
                "measurement_units": measurement_units,
                "shelf_life_days": shelf_life_days,
            }
        )
    return ingredient_rows


def generate_dynamic_rows(order_count, combos):
    orders = generate_orders(order_count)
    order_detail_rows = generate_order_details(orders, combos)
    combo_detail_rows = generate_combo_details(order_detail_rows, combos)

    return {
        "orders": orders,
        "order_detail_rows": order_detail_rows,
        "combo_detail_rows": combo_detail_rows,
    }


def generate_menu_item_details_rows(menu_items):
    ingredient_lookup = {item[1]: item[0] for item in INGREDIENTS}
    rows = []
    menu_item_detail_id = 1

    for menu_item in menu_items:
        menu_name = menu_item["menu_item_name"]
        ingredient_names = MENU_INGREDIENT_MAP.get(menu_name, [random.choice([x[1] for x in INGREDIENTS])])
        for ingredient_name in ingredient_names:
            ingredient_id = ingredient_lookup[ingredient_name]
            ingredient_quantity = 1
            rows.append(
                {
                    "menu_item_detail_id": menu_item_detail_id,
                    "menu_item_id": menu_item["menu_item_id"],
                    "ingredient_id": ingredient_id,
                    "ingredient_quantity": ingredient_quantity,
                }
            )
            menu_item_detail_id += 1

    return rows


def generate_combos():
    return [
        {"combo_id": combo_id, "combo_name": name, "combo_price": round(price, 2)}
        for combo_id, name, price in COMBOS
    ]


def generate_combo_details(order_details, combos):
    rows = []
    combo_detail_id = 1

    combo_lookup = {combo["combo_id"]: combo["combo_name"] for combo in combos}

    for order_detail in order_details:
        combo_name = combo_lookup[order_detail["combo_id"]]
        for menu_item_id in COMBO_ELIGIBLE[combo_name]:
            rows.append(
                {
                    "combo_detail_id": combo_detail_id,
                    "menu_item_id": menu_item_id,
                    "order_detail_id": order_detail["order_detail_id"],
                    "servings": round(random.uniform(0.5, 2.0), 2),
                }
            )
            combo_detail_id += 1

    return rows


def generate_orders(order_count=10):
    start_date = datetime(2024, 1, 1, 10, 0, 0)
    end_date = datetime(2025, 12, 31, 21, 0, 0)
    rows = []
    for order_id in range(1, order_count + 1):
        created_at = random_datetime(start_date, end_date)
        while created_at.weekday() >= 5 or created_at.hour < 10 or created_at.hour >= 21:
            created_at = random_datetime(start_date, end_date)

        status = random.choices(["paid", "canceled", "refunded"], weights=[75, 10, 15], k=1)[0]
        payment_method = random.choice(["credit_card", "debit_card", "cash", "gift_card", "meal_pass"])
        updated_at = None
        if status != "paid" or random.random() < 0.2:
            updated_at = created_at + timedelta(hours=random.randint(1, 72))

        rows.append(
            {
                "order_id": order_id,
                "order_status": status,
                "created_at": created_at,
                "updated_at": updated_at,
                "payment_method": payment_method,
                "order_total": 0.0,
            }
        )

    for peak in PEAK_DAYS:
        for r in [r for r in rows if r["created_at"].date() == peak.date()]:
            for _ in range(PEAK_COPIES):
                rows.append({**r, "order_id": len(rows) + 1})

    return rows


def generate_order_details(orders, combos):
    rows = []
    order_detail_id = 1

    for order in orders:
        selected_combos = random.sample(combos, k=random.randint(1, min(3, len(combos))))
        order_total = 0.0

        for combo in selected_combos:
            qty = random.randint(1, 3)
            order_total = round(order_total + combo["combo_price"] * qty, 2)
            rows.append(
                {
                    "order_detail_id": order_detail_id,
                    "combo_id": combo["combo_id"],
                    "order_id": order["order_id"],
                    "order_qty": qty,
                }
            )
            order_detail_id += 1

        order["order_total"] = order_total

    return rows


def build_sql_script(mode="static", order_count=10):
    combos = generate_combos()
    sql_script = ""

    if mode == "static":
        static_rows = generate_static_rows()

        sql_script += build_insert_sql(
            "menu_items",
            ["menu_item_id", "menu_item_name", "premium_charge", "menu_item_type"],
            [
                (row["menu_item_id"], row["menu_item_name"], row["premium_charge"], row["menu_item_type"])
                for row in static_rows["menu_items"]
            ],
        )
        sql_script += build_insert_sql(
            "ingredients",
            ["ingredient_id", "ingredient_name", "quantity_in_stock", "storage_type", "ingredient_cost", "expiry_date",
             "min_qty_on_hand", "measurement_units", "shelf_life_days"],
            [
                (
                    row["ingredient_id"],
                    row["ingredient_name"],
                    row["quantity_in_stock"],
                    row["storage_type"],
                    row["ingredient_cost"],
                    row["expiry_date"],
                    row["min_qty_on_hand"],
                    row["measurement_units"],
                    row["shelf_life_days"],
                )
                for row in static_rows["ingredient_rows"]
            ],
        )
        sql_script += build_insert_sql(
            "menu_item_details",
            ["menu_item_detail_id", "menu_item_id", "ingredient_id", "ingredient_quantity"],
            [
                (
                    row["menu_item_detail_id"],
                    row["menu_item_id"],
                    row["ingredient_id"],
                    row["ingredient_quantity"],
                )
                for row in static_rows["menu_item_details_rows"]
            ],
        )
        sql_script += build_insert_sql(
            "combos",
            ["combo_id", "combo_name", "combo_price"],
            [
                (row["combo_id"], row["combo_name"], row["combo_price"])
                for row in static_rows["combos"]
            ],
        )

        sql_script += build_sequence_resets([
            ("menu_items", "menu_item_id"),
            ("ingredients", "ingredient_id"),
            ("menu_item_details", "menu_item_detail_id"),
            ("combos", "combo_id"),
        ])

    if mode == "orders":
        dynamic_rows = generate_dynamic_rows(order_count, combos)

        sql_script += build_insert_sql(
            "orders",
            ["order_id", "order_status", "created_at", "updated_at", "payment_method", "order_total"],
            [
                (
                    row["order_id"],
                    row["order_status"],
                    row["created_at"],
                    row["updated_at"],
                    row["payment_method"],
                    row["order_total"],
                )
                for row in dynamic_rows["orders"]
            ],
        )
        sql_script += build_insert_sql(
            "order_details",
            ["order_detail_id", "combo_id", "order_id", "order_qty"],
            [
                (row["order_detail_id"], row["combo_id"], row["order_id"], row["order_qty"])
                for row in dynamic_rows["order_detail_rows"]
            ],
        )
        sql_script += build_insert_sql(
            "combo_details",
            ["combo_detail_id", "menu_item_id", "order_detail_id", "servings"],
            [
                (
                    row["combo_detail_id"],
                    row["menu_item_id"],
                    row["order_detail_id"],
                    row["servings"],
                )
                for row in dynamic_rows["combo_detail_rows"]
            ],
        )

        sql_script += build_sequence_resets([
            ("orders", "order_id"),
            ("order_details", "order_detail_id"),
            ("combo_details", "combo_detail_id"),
        ])

    return sql_script


def main():
    mode = input("Type of data to generate: static, orders, or quit: ")
    while mode not in ("static", "orders", "quit"):
        mode = input("Retry input - Type of data to generate: static, orders, or quit: ")

    if mode == "quit":
        return

    order_count = 0
    if mode == "orders":
        order_count = int(input("how many orders to generate? "))

    script = build_sql_script(mode=mode, order_count=order_count)

    if mode == "orders":
        output_path = Path(__file__).with_name("generated_orders.sql")
        output_path.write_text(script, encoding="utf-8")
        print(f"Generated SQL script with {order_count} orders written to: {output_path}")
        print(f"Rows generated: {script.count('INSERT INTO')}")

    else:
        output_path = Path(__file__).with_name("generated_static_data.sql")
        output_path.write_text(script, encoding="utf-8")
        print(f"Generated SQL script with {order_count} orders written to: {output_path}")
        print(f"Rows generated: {script.count('INSERT INTO')}")


if __name__ == "__main__":
    main()