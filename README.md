# RailSync — Eclipse Java Project

RailSync is a Java console project demonstrating railway-style record workflows and six DSA algorithms.

## Included features
- Train/service records: list, add, search by ID, update status
- Sample timetable, passenger, maintenance/incident, and alert views
- CSV persistence in `data/trains.csv`
- DSA Lab:
  1. KMP string matching
  2. Suffix Array + LCP (Kasai algorithm)
  3. Levenshtein Distance
  4. Edmonds–Karp maximum flow using BFS
  5. Vertex-Cover 2-approximation
  6. Randomized QuickSort

## Open in Eclipse
1. Extract `RailSync_Eclipse.zip` to a folder.
2. Open Eclipse.
3. Choose **File → Import → General → Existing Projects into Workspace**.
4. Select **Select root directory**, browse to the extracted `RailSync_Eclipse` folder, and select **Finish**.
5. Open `src/railsync/Main.java`.
6. Right-click `Main.java` → **Run As → Java Application**.

No external libraries or database are required. Java 8 or later is recommended.

## Data storage
The sample train records are in `data/trains.csv`. The application reads/writes this file relative to the project working directory. If running from Eclipse, the working directory is normally the project root. Use menu option 9 to save.

## Notes
The railway data is illustrative sample data, not live railway information. The project demonstrates algorithms in a console application; it is not connected to an actual railway system.
