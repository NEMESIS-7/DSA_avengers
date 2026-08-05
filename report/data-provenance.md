# Data Provenance

The dataset for this project models district hospital operations in the **Shai-Osudoku District, Greater Accra Region, Ghana**, with **Shai-Osudoku District Hospital (Dodowa)** as the internal hospital layer and its ambulance bay as the single gateway node joining the external referral network to the internal hospital layout.

External-layer location names are drawn from real, publicly documented facilities in this district: 8 communities, 5 CHPS compounds, 1 mission hospital (St. Andrew Catholic Hospital, Kordiabe — corrected from an earlier CHPS classification after verifying it was elevated to licensed hospital status), 4 health centres, and 1 regional referral hospital (Greater Accra Regional Hospital). Facility names and classifications were verified against Ghana Health Service district records, government budget documents, and local news reporting; sources are listed in the appendix. Shai-Osudoku District has no polyclinic tier — this was confirmed against the district's own facility register, and the network was adjusted accordingly rather than forcing a category that doesn't exist in reality.

Internal-layer locations (wards, theatres, labs, etc.) are generic hospital departments, not modeled on any specific hospital's real floor plan.

**No real patient, staff, or request data was used anywhere in this system.** All `service_requests`, `resources`, `audit_events`, road travel-time/condition weights, and patient-facing identifiers (`REQ-0001` style) are synthetic, generated for this project only. Road connections reflect the real referral hierarchy used in Ghana's health system (community → CHPS/health centre → district hospital) but are not surveyed GPS data.

## Sources

- Shai-Osudoku District Assembly — Health Directorate facility register
- Ghana Health Service district records
- MyJoyOnline / GNA reporting on St. Andrew Catholic Hospital, Kordiabe (clinic-to-hospital upgrade)
- Local news and government budget documents referencing district sub-facility names

*Owner: A3 (Data & Database Owner). Last updated during Week 1.*
