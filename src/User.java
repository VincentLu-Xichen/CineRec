import java.io.*;
import java.util.HashMap;
import java.util.List;

public class User {
    private String userName;
    private String password;
    // "Watchlist" is another class, watchlist is an attribute of User
    private Watchlist watchlist;
    // "History" is another class, history is an attribute of User
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

    // This is a method,which user can change their username
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    // This is a method,which user can change their username
    public void setPassword(String password) {
        this.password = password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    // This is a method that user can change their watchlist.
    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public History getHistory() {
        return history;
    }

    // This is a method that user can change their watchlist.
    public void setHistory(History history) {
        this.history = history;
    }

    // List<Movie> allMovies is used for a method that in History.
    public static HashMap<String, User> loadUsers(String path,List<Movie> allMovies) {
        HashMap<String, User> users = new HashMap<>(); // Create a new map to store all users
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("The file is not found.");
            return users;
        }
        BufferedReader br = null; // Declare a "Reader" variable,it will be initialized in "try"
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            boolean first = true; // this is used to skip "username,password,watchlist,history"
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;  /* If it is the first row"username,password,watchlist,history",
                                  do not habdle.*/
                }
                String[] parts = line.split(",", 4);
                if (parts.length < 4) {continue;}
                String username = parts[0].trim();
                String password = parts[1].trim();
                String watchlistCsv = parts[2].trim();
                String historyCsv = parts[3].trim();//把文件里面的内容读成四个String

                User user = new User(username, password);// Create User's object

                Watchlist wl = new Watchlist();
                wl.mergeFromCsv(watchlistCsv); // eg: Add "M008;M015;M071;M048;M056" to the list，把watchlistCsv里面的内容加入到新的Watchlist对象wl里面（涉及到一个加入的方法）


                History his = new History();//要该这里的初始化，这里相当于是使用了无参构造，在进行后续添加！！！
                his.mergeFromCsvWithDate(historyCsv,allMovies);//添加内容到这个用户的History对象his里面

                user.setWatchlist(wl);//修改Watchlist
                user.setHistory(his);//修改setHistory

                users.put(username, user); // Store in the map."Username" is key. 修改这个map，即完成数据的更新
            }
        } catch (IOException e) {
            System.out.println("[WARN] Failed to read users: " + e.getMessage());
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
            System.out.println("[WARN] Failed to save users: " + e.getMessage());
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
            System.out.println("[WARN] Username already exists: " + username);
            return false;
        }
        // check the password
        if(password == null || password.equals("")) {
            System.out.println("[WARN] Password cannot be empty");
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
            System.out.println("[WARN] Old password is incorrect!");
            return false;}
        if(newPassword == null || newPassword.equals("")) {
            System.out.println("[WARN] New password cannot be empty");
            return false;
        }
        this.password = newPassword;
        return true;
    }
    // The overloading of the method "changePassword"
    public boolean changePassword(String oldPassword, String newPassword, String confirmPassword) {
        if(!confirmPassword.equals(newPassword)){
            System.out.println("[WARN] Two passwords don't match!");
            return false;
        }
        return changePassword(oldPassword, newPassword);
    }
}