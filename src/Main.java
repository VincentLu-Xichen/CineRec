import java.time.LocalDate;
import java.util.*;

public class Main {

    private static List<Movie> allMovies;
    private static HashMap<String, User> users;
    private static User currentUser = null;

    //Define file paths
    private static final String MOVIE_FILE = "data/movies.csv";
    private static final String USER_FILE = "data/users.csv";

    //Load movies and users at program startup
    public static void main(String[] args) {
        System.out.println("Loading movies...");
        allMovies = Movie.loadFromCsv(MOVIE_FILE);
        System.out.println("Loaded " + allMovies.size() + " movies.");

        System.out.println("Loading users...");
        users = User.loadUsers(USER_FILE, allMovies);
        System.out.println("Loaded " + users.size() + " users.");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        //Main loop
        while (running) {
            if (currentUser == null) {
                running = showMainMenu(scanner);
            } else {
                showUserMenu(scanner);
            }
        }

        //Save data before exit
        User.saveUsers(users, USER_FILE);
        System.out.println("Data saved. Goodbye!");
        scanner.close();
    }

    //Show main menu for non-logged-in users (have included advanced feature: create new user)
    private static boolean showMainMenu(Scanner scanner) {
        System.out.println("\n=== Welcome to Movie Tracker ===");
        System.out.println("1. Login");
        System.out.println("2. Create New User");
        System.out.println("3. Exit");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            login(scanner);
            return true;
        } else if ("2".equals(choice)) {
            createNewUser(scanner);
            return true;
        } else if ("3".equals(choice)) {
            return false; //Exit program
        } else {
            System.out.println("Invalid option.");
            return true;
        }
    }

    //Login logic
    private static void login(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getUserName());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    //Create a new user account (Advanced feature: call User.createNewUser)
    private static void createNewUser(Scanner scanner) {
        System.out.print("New username: ");
        String newUsername = scanner.nextLine().trim();
        System.out.print("New password: ");
        String newPassword = scanner.nextLine().trim();

        //Use a temporary User object to call the instance method createNewUser
        User helper = new User();
        boolean ok = helper.createNewUser(newUsername, newPassword, users);
        if (ok) {
            System.out.println("User created successfully! You can now login with your new account.");
        } else {
            System.out.println("Failed to create user. Please try again.");
        }
        User.saveUsers(users, USER_FILE);
    }

    //Show menu for logged-in users (have included advanced feature: change password)
    private static void showUserMenu(Scanner scanner) {
        System.out.println("\n--- User Menu (" + currentUser.getUserName() + ") ---");
        System.out.println("1. Browse Movies");
        System.out.println("2. View Watchlist");
        System.out.println("3. Add Movie to Watchlist");
        System.out.println("4. Remove Movie from Watchlist");
        System.out.println("5. View History");
        System.out.println("6. Mark Movie as Watched");
        System.out.println("7. Get Recommendations");
        System.out.println("8. Change Password");
        System.out.println("9. Logout");
        System.out.print("Select option: ");

        String choice = scanner.nextLine().trim();

        //Ensure users' needs and call corresponding methods
        if ("1".equals(choice)) {
            browseMovies();
        } else if ("2".equals(choice)) {
            viewWatchlist();
        } else if ("3".equals(choice)) {
            addToWatchlist(scanner);
        } else if ("4".equals(choice)) {
            removeFromWatchlist(scanner);
        } else if ("5".equals(choice)) {
            viewHistory();
        } else if ("6".equals(choice)) {
            markAsWatched(scanner);
        } else if ("7".equals(choice)) {
            chooserecommendation(scanner);
        } else if ("8".equals(choice)) {
            changePassword(scanner);
        } else if ("9".equals(choice)) {
            currentUser = null;
            System.out.println("Logged out.");
        } else {
            System.out.println("Invalid option.");
        }
    }

    //Browse all movies
    private static void browseMovies() {
        System.out.println("\n--- Movie Library ---");
        for (Movie m : allMovies) {
            System.out.println(m.toString());
        }
    }

    // 查看待看列表：基于 movieIds + findMovieById / View watchlist: based on movieIds + findMovieById
    private static void viewWatchlist() {
        System.out.println("\n--- My Watchlist ---");
        Watchlist wl = currentUser.getWatchlist();
        List<String> ids = wl.getMovieIds();

        if (ids == null || ids.isEmpty()) {
            System.out.println("Watchlist is empty.");
            return;
        }

        for (String id : ids) {
            Movie m = findMovieById(id);
            if (m != null) {
                System.out.println(m.toString());
            } else {
                System.out.println(id + " (movie not found in library)");
            }
        }
    }

    //Add a movie to watchlist
    private static void addToWatchlist(Scanner scanner) {
        System.out.print("Enter Movie ID (e.g., M001): ");
        String id = scanner.nextLine().trim();

        Movie m = findMovieById(id);
        if (m == null) {
            System.out.println("Movie not found.");
            return;
        }

        if (currentUser.getWatchlist().addMovie(id)) {
            System.out.println("Added " + m.getTitle() + " to watchlist.");
        } else {
            System.out.println("Movie is already in your watchlist.");
        }
        User.saveUsers(users, USER_FILE);
    }

    //Remove a movie from watchlist
    private static void removeFromWatchlist(Scanner scanner) {
        System.out.print("Enter Movie ID to remove: ");
        String id = scanner.nextLine().trim();

        if (currentUser.getWatchlist().removeMovie(id)) {
            System.out.println("Removed successfully.");
        } else {
            System.out.println("Movie not found in your watchlist.");
        }

    }

    //View watch history: purely based on History's map
    private static void viewHistory() {
        System.out.println("\n--- Viewing History ---");
        History history = currentUser.getHistory();
        Map<String, String> map = history.getWatchedmap();

        if (map == null || map.isEmpty()) {
            System.out.println("No history now!");
            return;
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String movieId = entry.getKey();
            String date = entry.getValue();
            Movie m = findMovieById(movieId);
            if (m != null) {
                System.out.println("Watched: " + m.getTitle() + " on " + date);
            } else {
                System.out.println("Watched: " + movieId + " on " + date);
            }
        }
    }

    // Mark a movie as watched: use mergeFromCsvWithDate to update both map and watchedMovies
    private static void markAsWatched(Scanner scanner) {
        LocalDate today = LocalDate.now();
        String stoday = today.toString();

        System.out.print("Enter Movie ID you watched: ");
        String id = scanner.nextLine().trim();

        Movie m = findMovieById(id);
        if (m == null) {
            System.out.println("Movie not found.");
            return;
        }

        //Append record using the same format as in CSV
        String record = id + "@" + stoday;
        currentUser.getHistory().mergeFromCsvWithDate(record, allMovies);
        System.out.println("Added " + m.getTitle() + " to history on " + stoday);

        //If in watchlist, remove automatically
        if (currentUser.getWatchlist().removeMovie(id)) {
            System.out.println("Also removed from your watchlist.");
        }

    }

    //Choose recommendation method
    private static void chooserecommendation(Scanner scanner) {
        System.out.println("-----please select the way you prefer for movie recommendation-----");
        System.out.println("1.By Year");
        System.out.println("2.By Rating");
        System.out.println("3.According to your history");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            boolean validInput = false;
            int year = 0;
            while (!validInput) {
                // If the user input is not valid, keep looping until it is valid
                validInput = true;
                System.out.println("-----please choose the year, and we will recommend movies around the year. eg:1999-----");
                System.out.print("year:");
                try {
                    //Validate user input
                    year = Integer.parseInt(scanner.nextLine().trim());
                    if (year <= 1900 || year >= 2026) {
                        throw new IllegalArgumentException();
                    }
                } catch (NumberFormatException n) {
                    System.out.println("!!!Invalid year format!!!");
                    validInput = false;
                } catch (IllegalArgumentException i) {
                    System.out.println("The year you entered is either too early or too late.");
                    validInput = false;
                }
            }
            getRecommendationsbyyear(year);
        } else if ("2".equals(choice)) {
            getRecommendationsbyrating();
        } else if ("3".equals(choice)) {
            getRecommendationsbygerne();
        } else {
            System.out.println("Invalid option.");
        }
    }

    //Recommend by genre
    private static void getRecommendationsbygerne() {
        System.out.println("\n--- Recommendations ---");
        try {
            ArrayList<Movie> recs = RecommendationEngine.Top_10(currentUser);
            if (recs.isEmpty()) {
                System.out.println("No more recommendations available.");
            } else {
                for (int i = 0; i < recs.size(); i++) {
                    Movie m = recs.get(i);
                    System.out.println((i + 1) + ". " + m.getTitle() + " (" + m.getType() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Error generating recommendations: " + e.getMessage());
        }
    }

    //Recommend by rating
    private static void getRecommendationsbyrating() {
        System.out.println("\n--- Recommendations ---");
        try {
            ArrayList<Movie> recs = RecommendByRating.Top_10(currentUser);
            if (recs.isEmpty()) {
                System.out.println("No more recommendations available.");
            } else {
                for (int i = 0; i < recs.size(); i++) {
                    Movie m = recs.get(i);
                    System.out.println((i + 1) + ". " + m.getTitle() + " (" + m.getRating() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Error generating recommendations: " + e.getMessage());
        }
    }

    //Recommend by year
    private static void getRecommendationsbyyear(int year) {
        System.out.println("\n--- Recommendations ---");
        try {
            ArrayList<Movie> recs = RecommendByYear.Top_10(currentUser, year);
            if (recs.isEmpty()) {
                System.out.println("No more recommendations available.");
            } else {
                for (int i = 0; i < recs.size(); i++) {
                    Movie m = recs.get(i);
                    System.out.println((i + 1) + ". " + m.getTitle() + " (" + m.getYear() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Error generating recommendations: " + e.getMessage());
        }
    }

    //Helper method: find movie by ID
    private static Movie findMovieById(String id) {
        for (Movie m : allMovies) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        return null;
    }

    //Change password (advanced feature)
    private static void changePassword(Scanner scanner) {
        System.out.print("Enter your current password: ");
        String oldPwd = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPwd = scanner.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirm = scanner.nextLine().trim();

        boolean ok = currentUser.changePassword(oldPwd, newPwd, confirm);
        if (ok) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password.");
        }

    }
}