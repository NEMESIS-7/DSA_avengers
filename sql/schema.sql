-- ============================================================
-- Ghana Smart Service Operations Optimizer
-- District Hospital context — schema.sql (PostgreSQL)
-- Owner: A3 (Data & Database Owner)
-- ============================================================

-- ------------------------------------------------------------
-- 1. LOCATIONS
-- Every node in both layers. Exactly ONE row should have
-- type = 'gateway' (the ambulance bay) — that's the single
-- join point between the external and internal networks.
-- ------------------------------------------------------------
CREATE TABLE locations (
    location_id     TEXT PRIMARY KEY,      -- e.g. 'EXT-C01', 'INT-14', 'GATE-01'
    name            TEXT NOT NULL,          -- e.g. 'Ayikuma CHPS Compound'
    layer           TEXT NOT NULL CHECK (layer IN ('EXTERNAL', 'INTERNAL')),
    type            TEXT NOT NULL,          -- community, chps, health_centre, mission_hospital,
                                             -- regional_hospital, gateway, ward, theatre,
                                             -- lab, pharmacy, store, opd, admin, support
    area            TEXT,                   -- e.g. 'Shai-Osudoku District'
    latitude        REAL,
    longitude       REAL
);

-- ------------------------------------------------------------
-- 2. ROADS
-- Every edge, in either layer. Effective traversal cost for
-- every algorithm (DFS doesn't need weight, but BFS/Dijkstra/
-- Kruskal/Prim do) = travel_time_s * road_condition_weight.
-- ------------------------------------------------------------
CREATE TABLE roads (
    road_id                 TEXT PRIMARY KEY,
    from_location_id        TEXT NOT NULL REFERENCES locations(location_id),
    to_location_id          TEXT NOT NULL REFERENCES locations(location_id),
    road_name               TEXT,                            -- e.g. 'Agomeda Township to Agomeda Health Centre Road' — display/reporting only, no algorithm reads this
    distance_m              REAL NOT NULL,
    travel_time_s           REAL NOT NULL,
    road_condition_weight   REAL NOT NULL DEFAULT 1.0,   -- 1.0 = good/flat, up to 2.5-3.0
    is_closed               BOOLEAN NOT NULL DEFAULT FALSE,  -- used by your DFS reachability check
    CHECK (from_location_id <> to_location_id)
);

CREATE INDEX idx_roads_from ON roads(from_location_id);
CREATE INDEX idx_roads_to   ON roads(to_location_id);

-- ------------------------------------------------------------
-- 3. RESOURCES
-- Porters, ambulances, lab runners, etc.
-- ------------------------------------------------------------
CREATE TABLE resources (
    resource_id       TEXT PRIMARY KEY,
    type              TEXT NOT NULL,   -- porter, wheelchair, trolley, ambulance,
                                        -- lab_runner, biomedical_technician, pharmacy_rider
    capacity          INTEGER NOT NULL DEFAULT 1,
    is_available      BOOLEAN NOT NULL DEFAULT TRUE,
    home_location_id  TEXT REFERENCES locations(location_id)
);

-- ------------------------------------------------------------
-- 4. SERVICE REQUESTS
-- The actual jobs being dispatched.
-- ------------------------------------------------------------
CREATE TABLE service_requests (
    request_id              TEXT PRIMARY KEY,
    category                TEXT NOT NULL CHECK (category IN (
                                'REFERRAL_IN', 'REFERRAL_OUT', 'PATIENT_TRANSFER',
                                'SPECIMEN', 'DRUG_DELIVERY', 'BLOOD', 'STERILE_SUPPLY',
                                'EQUIPMENT', 'MEALS', 'LINEN', 'MAINTENANCE',
                                'MORTUARY_TRANSFER')),
    patient_ref              TEXT,                          -- synthetic patient reference (e.g. 'PAT-0011'); NULL for non-patient-specific categories: STERILE_SUPPLY, EQUIPMENT, LINEN, MAINTENANCE — this is what B4's hash table keys on
    source_location_id      TEXT NOT NULL REFERENCES locations(location_id),
    destination_location_id TEXT NOT NULL REFERENCES locations(location_id),
    urgency                 INTEGER NOT NULL CHECK (urgency BETWEEN 1 AND 5),
    status                  TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN (
                                'PENDING', 'ASSIGNED', 'IN_TRANSIT', 'COMPLETED', 'CANCELLED')),
    submitted_at             TIMESTAMP NOT NULL,
    deadline_at              TIMESTAMP,
    assigned_resource_id     TEXT REFERENCES resources(resource_id)
);

CREATE INDEX idx_requests_status  ON service_requests(status);
CREATE INDEX idx_requests_urgency ON service_requests(urgency);
CREATE INDEX idx_requests_patient ON service_requests(patient_ref);

-- ------------------------------------------------------------
-- 5. AUDIT EVENTS  (your table — feeds your Stack/undo-dispatch)
-- Every status change or dispatch action gets logged here.
-- Your Stack pops the most recent event to "undo" it.
-- ------------------------------------------------------------
CREATE TABLE audit_events (
    event_id          SERIAL PRIMARY KEY,
    request_id        TEXT REFERENCES service_requests(request_id),
    action             TEXT NOT NULL,   -- e.g. CREATED, ASSIGNED, STATUS_CHANGE, UNDONE
    previous_status    TEXT,
    new_status         TEXT,
    performed_by       TEXT,            -- who/what triggered it
    event_time         TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_request ON audit_events(request_id);

-- ------------------------------------------------------------
-- 6. ALGORITHM RUNS
-- Log table for the timed performance experiments (B5 owns
-- the experiments; every slot's algorithm logs here).
-- ------------------------------------------------------------
CREATE TABLE algorithm_runs (
    run_id           SERIAL PRIMARY KEY,
    algorithm_name   TEXT NOT NULL,    -- e.g. 'DFS', 'Dijkstra', 'MergeSort'
    input_size       INTEGER NOT NULL,
    run_number       INTEGER NOT NULL, -- 1, 2, or 3 (each experiment runs 3x)
    elapsed_ms       REAL NOT NULL,
    run_at           TIMESTAMP NOT NULL,
    notes            TEXT
);
