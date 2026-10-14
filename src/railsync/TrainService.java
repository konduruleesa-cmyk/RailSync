package railsync;

public class TrainService {
    private String id;
    private String name;
    private String from;
    private String to;
    private String status;
    private int passengers;
    private int capacity;

    public TrainService(String id, String name, String from, String to,
                        String status, int passengers, int capacity) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.status = status;
        this.passengers = passengers;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getStatus() { return status; }
    public int getPassengers() { return passengers; }
    public int getCapacity() { return capacity; }

    public void setStatus(String status) { this.status = status; }

    public String toCsv() {
        return csv(id) + "," + csv(name) + "," + csv(from) + "," + csv(to) + ","
                + csv(status) + "," + passengers + "," + capacity;
    }

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static TrainService fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 7) return null;
        try {
            return new TrainService(unquote(p[0]), unquote(p[1]), unquote(p[2]),
                    unquote(p[3]), unquote(p[4]), Integer.parseInt(p[5].trim()),
                    Integer.parseInt(p[6].trim()));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String unquote(String s) {
        s = s.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    @Override
    public String toString() {
        return String.format("%-7s %-22s %-14s %-14s %-12s %5d/%-5d",
                id, name, from, to, status, passengers, capacity);
    }
}
