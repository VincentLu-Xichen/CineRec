import java.util.Map;
import java.util.HashMap;

public class User{
    private String userName;
    private String password;
    // "Watchlist" is another class, watchlist is an attribute of User
    private Watchlist watchlist;
    // "History" is another class, history is an attribute of User
    private History history;

    // 静态变量：存储所有用户
    private static Map<String, User> allUsers = new HashMap<>();

    public User(){}
    public User(String userName, String password, String watchlist, String history) {
        this.userName = userName;
        this.password = password;
        this.watchlist = new Watchlist();
        this.history = new History();
    }

    public String getUserName() {return userName;}
    // This is a method,which user can change their username
    public void setUserName(String userName) {this.userName = userName;}

    public String getPassword() {return password;}
    // This is a method,which user can change their username
    public void setPassword(String password) {this.password = password;}

    public Watchlist getWatchlist() {return watchlist;}
    // This is a method that user can change their watchlist.
    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public History getHistory() {return history;}
    // This is a method that user can change their watchlist.
    public void setHistory(History history) {
        this.history = history;
    }

    /**
     * Loading all users from User.CSV
     */
    public static void loadUsersFromCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // 跳过标题行
            if (line == null) throw new IOException("The file is null");

            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",", -1); // -1 Keep the blank field
                if (parts.length < 4) {continue;}

                String username = parts[0].trim();
                String password = parts[1].trim();
                Set<String> watchlist = parseToSet(parts[2]);
                List<WatchedEntry> history = parseHistoryToList(parts[3]);

                allUsers.put(username, new User(username, password, watchlist, history));
            }
        }
    }

    /**
     * 解析 Watchlist: "M008;M015" → Set<String>
     */
    private static Set<String> parseToSet(String csv) {
        Set<String> result = new HashSet<>();
        if (csv == null || csv.trim().isEmpty()) return result;

        for (String item : csv.split(";")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 解析 History: "M001@2025-07-12;M011@2025-08-10" → List<WatchedEntry>
     */
    private static List<WatchedEntry> parseHistoryToList(String csv) {
        List<WatchedEntry> result = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return result;

        for (String item : csv.split(";")) {
            String trimmed = item.trim();
            if (trimmed.isEmpty()) continue;

            String[] parts = trimmed.split("@", 2); // 最多分两部分
            if (parts.length == 2) {
                result.add(new WatchedEntry(parts[0], parts[1]));
            } else {
                result.add(new WatchedEntry(trimmed, "unknown")); // 容错
            }
        }
        return result;
    }

    /**
     * 保存回 CSV（可选）
     */
    public static void saveUsersToCSV(String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("username,password,watchlist,history");

            for (User user : allUsers.values()) {
                String watchlistStr = String.join(";", user.watchlist);

                List<String> historyStrs = new ArrayList<>();
                for (WatchedEntry e : user.history) {
                    historyStrs.add(e.getMovieId() + "@" + e.getDate());
                }
                String historyStr = String.join(";", historyStrs);

                pw.printf("%s,%s,%s,%s%n",
                        user.username,
                        user.password,
                        watchlistStr,
                        historyStr);
            }
        }
    }

    // --- toString 用于调试 ---
    @Override
    public String toString() {
        return String.format("User{username='%s', watchlist=%s, history=%s}",
                username, watchlist, history);
    }
}





