package railsync;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final Scanner SC = new Scanner(System.in);
    private static final List<TrainService> TRAINS = new ArrayList<>();
    private static final Path DATA_FILE = Paths.get("data", "trains.csv");

    public static void main(String[] args) {
        loadTrains();
        System.out.println("==============================================");
        System.out.println(" RAILSYNC - INTELLIGENT RAILWAY OPERATIONS");
        System.out.println("==============================================");
        while (true) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Train and service records");
            System.out.println("2. Timetable / platforms (sample)");
            System.out.println("3. Passenger / ticket records (sample)");
            System.out.println("4. Route / passenger flow demo");
            System.out.println("5. Maintenance / incidents (sample)");
            System.out.println("6. Search alerts using KMP");
            System.out.println("7. Reports / analytics");
            System.out.println("8. Advanced DSA Lab");
            System.out.println("9. Save all CSV data");
            System.out.println("0. Exit");
            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1: trainMenu(); break;
                case 2: showTimetable(); break;
                case 3: showPassengers(); break;
                case 4: flowDemo(); break;
                case 5: showIncidents(); break;
                case 6: alertSearch(); break;
                case 7: reports(); break;
                case 8: dsaMenu(); break;
                case 9: saveTrains(); break;
                case 0:
                    saveTrains();
                    System.out.println("Thank you for using RailSync!");
                    return;
                default: System.out.println("Invalid option. Please choose 0-9.");
            }
        }
    }

    private static void trainMenu() {
        while (true) {
            System.out.println("\nTRAIN & SERVICE RECORDS");
            System.out.println("1. List records");
            System.out.println("2. Search by service ID");
            System.out.println("3. Add service");
            System.out.println("4. Update service status");
            System.out.println("0. Return to main menu");
            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1: listTrains(); break;
                case 2: searchTrain(); break;
                case 3: addTrain(); break;
                case 4: updateStatus(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void printHeader() {
        System.out.printf("%-7s %-22s %-14s %-14s %-12s %5s/%-5s%n",
                "ID", "SERVICE", "FROM", "TO", "STATUS", "PAX", "CAPACITY");
        System.out.println("------------------------------------------------------------------------------------");
    }

    private static void listTrains() {
        if (TRAINS.isEmpty()) { System.out.println("No records found."); return; }
        printHeader();
        for (TrainService t : TRAINS) System.out.println(t);
    }

    private static void searchTrain() {
        String id = readLine("Enter service ID (e.g., RS101): ").trim();
        for (TrainService t : TRAINS) {
            if (t.getId().equalsIgnoreCase(id)) {
                printHeader(); System.out.println(t); return;
            }
        }
        System.out.println("No service found with ID " + id);
    }

    private static void addTrain() {
        String id = readLine("Service ID: ").trim();
        for (TrainService t : TRAINS) {
            if (t.getId().equalsIgnoreCase(id)) {
                System.out.println("That ID already exists."); return;
            }
        }
        String name = readLine("Service name: ");
        String from = readLine("From station: ");
        String to = readLine("To station: ");
        String status = readLine("Status (ON TIME/DELAYED/MAINTENANCE): ").trim().toUpperCase();
        int passengers = readInt("Passenger count: ");
        int capacity = readInt("Capacity: ");
        if (passengers < 0 || capacity < 0 || passengers > capacity) {
            System.out.println("Invalid counts. Passengers must be between 0 and capacity."); return;
        }
        TRAINS.add(new TrainService(id, name, from, to, status, passengers, capacity));
        System.out.println("Service added. Choose option 9 from main menu to save.");
    }

    private static void updateStatus() {
        String id = readLine("Enter service ID: ").trim();
        for (TrainService t : TRAINS) {
            if (t.getId().equalsIgnoreCase(id)) {
                t.setStatus(readLine("Enter new status: ").trim().toUpperCase());
                System.out.println("Status updated. Choose option 9 from main menu to save.");
                return;
            }
        }
        System.out.println("Service not found.");
    }

    private static void showTimetable() {
        System.out.println("\nSAMPLE TIMETABLE / PLATFORMS");
        System.out.println("RS101  Vijayawada -> Hyderabad  09:00  Platform 2");
        System.out.println("RS204  Chennai -> Vijayawada    11:30  Platform 4");
        System.out.println("RS318  Hyderabad -> Guntur      14:15  Platform 1");
        System.out.println("These are illustrative records, not live timetable data.");
    }

    private static void showPassengers() {
        System.out.println("\nSAMPLE PASSENGER / TICKET RECORDS");
        System.out.println("TKT501 | Passenger A | RS101 | Confirmed");
        System.out.println("TKT502 | Passenger B | RS204 | Waiting list");
        System.out.println("TKT503 | Passenger C | RS318 | Confirmed");
        System.out.println("These are illustrative records.");
    }

    private static void showIncidents() {
        System.out.println("\nSAMPLE MAINTENANCE / INCIDENTS");
        System.out.println("INC01 | RS422 | Scheduled maintenance inspection");
        System.out.println("INC02 | RS204 | Signal delay reported");
        System.out.println("INC03 | RS318 | Platform information update");
    }

    private static void alertSearch() {
        String pattern = readLine("Enter alert keyword or train ID to search: ");
        Path alertFile = Paths.get("data", "alerts.txt");
        try {
            List<String> lines = Files.readAllLines(alertFile, StandardCharsets.UTF_8);
            boolean found = false;
            for (String line : lines) {
                int at = DsaAlgorithms.kmpSearch(line.toLowerCase(Locale.ROOT), pattern.toLowerCase(Locale.ROOT));
                if (at >= 0) { System.out.println("Match at index " + at + ": " + line); found = true; }
            }
            if (!found) System.out.println("No matching alert found.");
        } catch (IOException e) {
            System.out.println("Could not read " + alertFile + ": " + e.getMessage());
        }
    }

    private static void reports() {
        int totalPassengers = 0, totalCapacity = 0, delayed = 0;
        for (TrainService t : TRAINS) {
            totalPassengers += t.getPassengers();
            totalCapacity += t.getCapacity();
            if (t.getStatus().toUpperCase(Locale.ROOT).contains("DELAY")) delayed++;
        }
        System.out.println("\nSAMPLE REPORT");
        System.out.println("Total services: " + TRAINS.size());
        System.out.println("Total passengers: " + totalPassengers);
        System.out.println("Total capacity: " + totalCapacity);
        System.out.println("Delayed services: " + delayed);
        if (totalCapacity > 0)
            System.out.printf("Overall sample capacity utilization: %.2f%%%n", totalPassengers * 100.0 / totalCapacity);
    }

    private static void flowDemo() {
        // Nodes: 0=Source, 1=Station A, 2=Station B, 3=Destination
        int[][] capacity = {
            {0, 10, 8, 0},
            {0, 0, 5, 5},
            {0, 0, 0, 10},
            {0, 0, 0, 0}
        };
        int max = DsaAlgorithms.edmondsKarp(capacity, 0, 3);
        System.out.println("Sample network: Source -> Station A/B -> Destination");
        System.out.println("Maximum flow = " + max + " capacity units.");
    }

    private static void dsaMenu() {
        while (true) {
            System.out.println("\nADVANCED DSA LAB");
            System.out.println("1. KMP string search");
            System.out.println("2. Suffix Array + LCP/Kasai");
            System.out.println("3. Levenshtein Distance");
            System.out.println("4. Edmonds-Karp maximum flow demo");
            System.out.println("5. Vertex-Cover 2-approximation demo");
            System.out.println("6. Randomized QuickSort");
            System.out.println("0. Return to main menu");
            int choice = readInt("Choose algorithm: ");
            switch (choice) {
                case 1: runKmp(); break;
                case 2: runSuffixArray(); break;
                case 3: runLevenshtein(); break;
                case 4: flowDemo(); break;
                case 5: runVertexCover(); break;
                case 6: runQuickSort(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void runKmp() {
        String text = readLine("Enter text: ");
        String pattern = readLine("Enter pattern: ");
        int index = DsaAlgorithms.kmpSearch(text, pattern);
        System.out.println(index >= 0 ? "Pattern found at index " + index : "Pattern not found.");
    }

    private static void runSuffixArray() {
        String text = readLine("Enter text (short text recommended): ");
        int[] sa = DsaAlgorithms.suffixArray(text);
        int[] lcp = DsaAlgorithms.lcpKasai(text, sa);
        System.out.println("Index | Suffix | LCP with previous suffix");
        for (int i = 0; i < sa.length; i++) {
            System.out.printf("%5d | %-20s | %d%n", sa[i], text.substring(sa[i]), lcp[i]);
        }
    }

    private static void runLevenshtein() {
        String a = readLine("Enter first station/name: ");
        String b = readLine("Enter second station/name: ");
        int distance = DsaAlgorithms.levenshteinDistance(a.toLowerCase(Locale.ROOT), b.toLowerCase(Locale.ROOT));
        System.out.println("Levenshtein distance = " + distance);
        System.out.println(distance == 0 ? "Exact match." : "Smaller values indicate closer spellings.");
    }

    private static void runVertexCover() {
        int vertices = 5;
        int[][] edges = {{0,1},{1,2},{2,3},{3,4},{0,4}};
        Set<Integer> cover = DsaAlgorithms.vertexCover2Approx(vertices, edges);
        System.out.println("Sample graph edges: (0,1), (1,2), (2,3), (3,4), (0,4)");
        System.out.println("Approximate vertex cover: " + cover);
        System.out.println("Vertices are numbered 0 through " + (vertices - 1) + ".");
    }

    private static void runQuickSort() {
        String line = readLine("Enter integers separated by spaces: ").trim();
        if (line.isEmpty()) { System.out.println("No numbers entered."); return; }
        try {
            String[] parts = line.split("\\s+");
            int[] values = new int[parts.length];
            for (int i = 0; i < parts.length; i++) values[i] = Integer.parseInt(parts[i]);
            DsaAlgorithms.randomizedQuickSort(values);
            System.out.println("Sorted: " + Arrays.toString(values));
        } catch (NumberFormatException e) {
            System.out.println("Please enter integers only.");
        }
    }

    private static void loadTrains() {
        try {
            if (!Files.exists(DATA_FILE)) {
                System.out.println("Data file not found; using built-in sample records.");
                TRAINS.add(new TrainService("RS101", "Vande Bharat Express", "Vijayawada", "Hyderabad", "ON TIME", 420, 600));
                TRAINS.add(new TrainService("RS204", "Coastal Link", "Chennai", "Vijayawada", "DELAYED", 310, 500));
                return;
            }
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                TrainService t = TrainService.fromCsv(lines.get(i));
                if (t != null) TRAINS.add(t);
            }
            if (TRAINS.isEmpty()) System.out.println("No valid train rows loaded.");
        } catch (IOException e) {
            System.out.println("Could not load data file; starting with built-in sample records.");
            TRAINS.add(new TrainService("RS101", "Vande Bharat Express", "Vijayawada", "Hyderabad", "ON TIME", 420, 600));
            TRAINS.add(new TrainService("RS204", "Coastal Link", "Chennai", "Vijayawada", "DELAYED", 310, 500));
        }
    }

    private static void saveTrains() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("id,name,from,to,status,passengers,capacity");
            for (TrainService t : TRAINS) lines.add(t.toCsv());
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
            System.out.println("Saved " + TRAINS.size() + " train records to " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SC.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine();
    }
}
