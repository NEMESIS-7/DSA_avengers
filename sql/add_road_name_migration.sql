-- Migration: add road_name to roads table
-- Run this against your existing database (data already loaded, so ALTER not CREATE)

ALTER TABLE roads ADD COLUMN road_name TEXT;

UPDATE roads
SET road_name = CONCAT(fl.name, ' to ', tl.name, ' Road')
FROM locations fl, locations tl
WHERE roads.from_location_id = fl.location_id
  AND roads.to_location_id = tl.location_id;
