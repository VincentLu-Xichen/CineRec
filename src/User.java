import java.io.*;
import java.util.HashMap;
import java.util.List;

public class User {
    private String userName;
    private String password;
    // "Watchlist" is another class; the user's watchlist is stored here
    private Watchlist watchlist;
    // "History" is another class; the user's viewing history is stored here
    private History history;


    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.watchlist = new Watchlist();
        this.history = new History();
    }

    public User(String userName, String password, Watchlist watchlist, History history) {
        this.userName = userName;
        this.password = password;
        this.watchlist = watchlist;
        this.history = history;
    }

    public String getUserName() {
        return userName;
    }

    // Method to change the username
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    // Method to change the password
    public void setPassword(String password) {
        this.password = password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    // Method to update the user's watchlist
    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public History getHistory() {
        return history;
    }

    // Method to update the user's history
    public void setHistory(History history) {
        this.history = history;
    }

    // The parameter allMovies is used by History to resolve movie IDs
    public static HashMap<String, User> loadUsers(String path,List<Movie> allMovies) {
        HashMap<String, User> users = new HashMap<>(); // Create a new map to store all users
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("The file is not found.");
            return users;
        }
        BufferedReader br = null; // Declare the reader; initialize inside try
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            boolean first = true; // this is used to skip "username,password,watchlist,history"
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;  /* If it is the header row "username,password,watchlist,history",
                                  do not handle */
                }
                String[] parts = line.split(",", 4);
                if (parts.length < 4) {continue;}
                String username = parts[0].trim();
                String password = parts[1].trim();
                String watchlistCsv = parts[2].trim();
                String historyCsv = parts[3].trim(); // Read these fields as four strings from the file

                User user = new User(username, password); // Create the User object

                Watchlist wl = new Watchlist();
                wl.mergeFromCsv(watchlistCsv); // Load watchlist IDs from CSV into wl (e.g., M008;M015;...)


                History his = new History(); // Initialize history (no-arg constructor); entries are added below
                his.mergeFromCsvWithDate(historyCsv,allMovies); // Load history entries with dates using allMovies

                user.setWatchlist(wl); // Attach watchlist to user
                user.setHistory(his); // Attach history to user

                users.put(username, user); // Store in the map with username as the key
            }
        } catch (IOException e) {
            System.out.println("Failed to read users: " + e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ignored) {
            }
        }
        return users;
    }

    // save to CSV
    public static boolean saveUsers(HashMap<String, User> users, String path) {
        File file = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write("Username,Password,Watchlist,History");
            bw.newLine();
            for (User u : users.values()) {
                String wl;
                if(u.getWatchlist()!=null){
                    wl = u.getWatchlist().toCsvString();
                }
                else{wl="";}
                String his;
                if(u.getHistory()!=null){
                    his = u.getHistory().toCsvString();
                }
                else{his="";}

                // "toCsv"method (In class "Watchlist" and "History") makes the elements in "Watchlist"and "History" transfer to the String type
                bw.write(u.getUserName() + "," + u.getPassword() + "," + wl + "," + his);
                bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to save users: " + e.getMessage());
            return false;
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (IOException ignored) {

            }
        }
    }
    //Functionality for creating a new user account.(consists of new username and new password)
    public boolean createNewUser(String username, String password,HashMap<String, User> users) {
        // check the new username
        if(username == null||username.equals("")) {return false;}
        if(users.containsKey(username)) {
            System.out.println("Username already exists: " + username);
            return false;
        }
        // check the password
        if(password == null || password.equals("")) {
            System.out.println("Password cannot be empty");
            return false;
        }
        // Create the new user account
        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("User created successfully!");
        return true;
    }

    //Functionality for changing a user's password.
    public boolean changePassword(String oldPassword, String newPassword) {
        if( !this.password.equals(oldPassword)){
            System.out.println("Old password is incorrect!");
            return false;}
        if(newPassword == null || newPassword.equals("")) {
            System.out.println("New password cannot be empty");
            return false;
        }
        this.password = newPassword;
        return true;
    }
    // The overloading of the method "changePassword"
    public boolean changePassword(String oldPassword, String newPassword, String confirmPassword) {
        if(!confirmPassword.equals(newPassword)){
            System.out.println("Two passwords don't match!");
            return false;
        }
        return changePassword(oldPassword, newPassword);
    }
}
