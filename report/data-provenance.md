# Data Provenance

This dataset models hospital operations for the Shai-Osudoku District in the Greater Accra Region, Ghana. The internal layer is based on Shai-Osudoku District Hospital in Dodowa, and its ambulance bay is the single gateway node connecting the external referral network to the hospital's internal layout.

For the external layer, I used real, publicly documented facilities from the district instead of just making up names — 8 communities, 5 CHPS compounds, 1 mission hospital (St. Andrew Catholic Hospital, Kordiabe), 4 health centres, and 1 regional referral hospital (Greater Accra Regional Hospital). I checked all of these against Ghana Health Service district records, government budget documents, and local news reporting — sources are listed below.

A couple of corrections along the way, worth mentioning: St. Andrew Catholic Hospital was originally going to be listed as just a CHPS compound, but it turns out it was actually upgraded to licensed hospital status back in 2016-18, so I fixed that once I found the reporting on it. I also went looking for a polyclinic in the district, since that's usually one of the facility tiers, but Shai-Osudoku doesn't actually have one — confirmed that against the district's own facility register — so I didn't force one into the dataset just to fill out a category that doesn't apply here.

The internal locations (wards, theatres, labs, and so on) aren't based on any specific hospital's real floor plan — those are just generic department types.

No real patient, staff, or request data appears anywhere in this system. Everything in `service_requests`, `resources`, `audit_events`, road travel times/conditions, and every ID (`REQ-0001` style, plus the `patient_ref` values added later) is synthetic, generated for this project only. The `patient_ref` values were generated using the project's shared `randomSeed` (6316, from our S calculation), so the assignment is reproducible if anyone needs to check how it was done — not every category gets one either, since things like equipment moves or facility maintenance aren't tied to a specific patient in the first place. Road connections follow the real referral hierarchy Ghana's health system actually uses (community → CHPS/health centre → district hospital), but the actual distances and travel times aren't from surveyed GPS data.

## Sources

- Shai-Osudoku District Assembly — Health Directorate facility register
- Ghana Health Service district records
- MyJoyOnline / GNA reporting on St. Andrew Catholic Hospital, Kordiabe (clinic-to-hospital upgrade)
- Local news and government budget documents referencing district sub-facility names

*Owner: A3 (Data & Database Owner). Last updated Week 1.*
