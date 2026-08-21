import csv


def esc(value):
    if value is None or value == "":
        return "NULL"
    return "'" + value.replace("'", "''") + "'"


def esc_num(value):
    return "NULL" if value is None or value == "" else value


def esc_bool(value):
    return "TRUE" if value == "1" else "FALSE"


def gen_locations():
    with open("experiments/csv/locations_template.csv", newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    lines = [
        "-- Locations seed data (real dataset: 50 rows)",
        "INSERT INTO locations (location_id, name, layer, type, area, latitude, longitude)",
        "VALUES",
    ]
    values = []
    for r in rows:
        values.append("({}, {}, {}, {}, {}, {}, {})".format(
            esc(r["location_id"]), esc(r["name"]), esc(r["layer"]), esc(r["type"]),
            esc(r["area"]), esc_num(r["latitude"]), esc_num(r["longitude"])
        ))
    lines.append(",\n".join(values) + ";")
    with open("sql/seed/locations_seed.sql", "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def gen_roads():
    with open("experiments/csv/roads_template.csv", newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    lines = [
        "-- Roads seed data (real dataset: 100 rows). road_name included directly,",
        "-- so add_road_name_migration.sql is not needed on a fresh load.",
        "INSERT INTO roads (road_id, from_location_id, to_location_id, road_name,",
        "                   distance_m, travel_time_s, road_condition_weight, is_closed)",
        "VALUES",
    ]
    values = []
    for r in rows:
        values.append("({}, {}, {}, {}, {}, {}, {}, {})".format(
            esc(r["road_id"]), esc(r["from_location_id"]), esc(r["to_location_id"]),
            esc(r["road_name"]), esc_num(r["distance_m"]), esc_num(r["travel_time_s"]),
            esc_num(r["road_condition_weight"]), esc_bool(r["is_closed"])
        ))
    lines.append(",\n".join(values) + ";")
    with open("sql/seed/roads_seed.sql", "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def gen_resources():
    with open("experiments/csv/resources_template.csv", newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    lines = [
        "-- Resources seed data (real dataset: 30 rows)",
        "INSERT INTO resources (resource_id, type, capacity, is_available, home_location_id)",
        "VALUES",
    ]
    values = []
    for r in rows:
        values.append("({}, {}, {}, {}, {})".format(
            esc(r["resource_id"]), esc(r["type"]), esc_num(r["capacity"]),
            esc_bool(r["is_available"]), esc(r["home_location_id"])
        ))
    lines.append(",\n".join(values) + ";")
    with open("sql/seed/resources_seed.sql", "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


gen_locations()
gen_roads()
gen_resources()
print("done")
