import java.util.*;
import java.time.*;
    public class Main {
        private static List<Movie> allMovies;
        private static HashMap<String, User> users;
        private static User currentUser = null;

        // 定义文件路径
        private static final String MOVIE_FILE = "data/movies.csv";
        private static final String USER_FILE = "data/users.csv";

        //启动程序时先加载电影
        public static void main(String[] args) {
            System.out.println("Loading movies...");
            allMovies = Movie.loadFromCsv(MOVIE_FILE);
            System.out.println("Loaded " + allMovies.size() + " movies.");

            //加载用户数据
            System.out.println("Loading users...");
            users = User.loadUsers(USER_FILE, allMovies);//这个方法用于加载用户数据，但是User.loadUser出了问题
            System.out.println("Loaded " + users.size() + " users.");

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            // 主循环
            while (running) {
                if (currentUser == null) {
                    running = showMainMenu(scanner);
                } else {
                    showUserMenu(scanner);
                }
            }

            // 退出前保存数据
            User.saveUsers(users, USER_FILE);
            System.out.println("Data saved. Goodbye!");
            scanner.close();
        }

        // 显示未登录的主菜单
        private static boolean showMainMenu(Scanner scanner) {
            System.out.println("\n=== Welcome to Movie Tracker ===");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                login(scanner);
                return true;
            } else if (choice.equals("2")) {
                return false; // 退出程序
            } else {
                System.out.println("Invalid option.");
                return true;
            }
        }

        // 登录逻辑
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

        // 显示已登录的用户菜单
        private static void showUserMenu(Scanner scanner) {
            System.out.println("\n--- User Menu (" + currentUser.getUserName() + ") ---");
            System.out.println("1. Browse Movies");
            System.out.println("2. View Watchlist");
            System.out.println("3. Add Movie to Watchlist");
            System.out.println("4. Remove Movie from Watchlist");
            System.out.println("5. View History");
            System.out.println("6. Mark Movie as Watched");
            System.out.println("7. Get Recommendations");
            System.out.println("8. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    browseMovies();
                    break;
                case "2":
                    viewWatchlist();
                    break;
                case "3":
                    addToWatchlist(scanner);
                    break;
                case "4":
                    removeFromWatchlist(scanner);
                    break;
                case "5":
                    viewHistory();
                    break;
                case "6":
                    markAsWatched(scanner);
                    break;
                case "7":
                    chooserecommendation(scanner);
                    break;
                case "8":
                    currentUser = null;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        // 浏览所有电影
        private static void browseMovies() {
            System.out.println("\n--- Movie Library ---");
            for (Movie m : allMovies) {
                System.out.println(m.toString());
            }
        }

        // 查看待看列表
        private static void viewWatchlist() {
            System.out.println("\n--- My Watchlist ---");
            List<Movie> list = currentUser.getWatchlist().getWatchlist();
            if (list.isEmpty()) {
                System.out.println("Watchlist is empty.");
            } else {
                for (Movie m : list) {
                    System.out.println(m.toString());
                }
            }
        }

        // 添加电影到待看列表
        private static void addToWatchlist(Scanner scanner) {
            System.out.print("Enter Movie ID (e.g., M001): ");
            String id = scanner.nextLine().trim();

            Movie m = findMovieById(id);
            if (m == null) {
                System.out.println("Movie not found.");
                return;
            }

            // 调用修改后的 Watchlist 方法
            if (currentUser.getWatchlist().addMovie(id)) {
                System.out.println("Added " + m.getTitle() + " to watchlist.");
            } else {
                System.out.println("Movie is already in your watchlist.");
            }
        }

        // 从待看列表移除
        private static void removeFromWatchlist(Scanner scanner) {
            System.out.print("Enter Movie ID to remove: ");
            String id = scanner.nextLine().trim();

            if (currentUser.getWatchlist().removeMovie(id)) {
                System.out.println("Removed successfully.");
            } else {
                System.out.println("Movie not found in your watchlist.");
            }
        }

        // 查看历史记录
        private static void viewHistory() {
            System.out.println("\n--- Viewing History ---");
            List<Movie> list = currentUser.getHistory().getWatchedMovies();//加载并初始化user的时候出了问题
            Map<String,String> map=currentUser.getHistory().getWatchedmap();
            if (list.isEmpty()) {
                System.out.println("No history found.");
            } else {
                Movie thismovie=new Movie();
                for (String id:map.keySet()){
                    System.out.println("Watched: " +findMovieById(id).getTitle()+" on "+map.get(id));
                }

            }
        }

        // 标记为已看???
        private static void markAsWatched(Scanner scanner) {
            LocalDate today=LocalDate.now();
            String stoday=today.toString();
            System.out.print("Enter Movie ID you watched: ");
            String id = scanner.nextLine().trim();

            Movie m = findMovieById(id);
            if (m == null) {
                System.out.println("Movie not found.");
                return;
            }

            // 1. 加入历史
            currentUser.getHistory().addWatchedMovie(m.getId(),stoday);
            System.out.println("Added " + m.getTitle() + " to history on "+stoday);

            // 2. 如果在 Watchlist 中，询问是否移除
            // 自动移除
            if (currentUser.getWatchlist().removeMovie(id)) {
                System.out.println("Also removed from your watchlist.");
            }
        }
        //选择推荐方式
        public static void chooserecommendation(Scanner scanner){
            System.out.println("-----please select the way you prefer for movie recommendation-----");
            System.out.println("1.By Year");
            System.out.println("2.By Rating");
            System.out.println("3.By Genre");
            String choice=scanner.nextLine().trim();
            switch (choice){
                case "1":
                    boolean validInput=false;
                    int year=0;
                    while(!validInput) {//如果用户不输入正确的话，那就一直循环直到输入正确为止
                        validInput=true;
                    System.out.println("-----please choose the year, and we will recommend movies around the year. eg:1999-----");
                          System.out.print("year:");
                              try {//规范用户输入，如果输入非法字符或者非正确年份就让用户重新输入
                                  year = Integer.parseInt(scanner.nextLine().trim());
                                  if(year<=1900||year>=2026) {throw new IllegalArgumentException();}
                              } catch ( NumberFormatException n) {
                                  System.out.println("!!!Invalid year format!!!");
                                  validInput=false;
                              }
                              catch ( IllegalArgumentException i) {
                                  System.out.println("The year you entered is either too early or too late.");
                                  validInput=false;
                              }

                          }
                    getRecommendationsbyyear(year);
                break;
                case "2":getRecommendationsbyrating();
                break;
                case "3":getRecommendationsbygerne();
                break;
            }
        }
        // 获取推荐
        private static void getRecommendationsbygerne() {
            System.out.println("\n--- Recommendations ---");
            try {
                // 调用推荐引擎
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
                // e.printStackTrace(); // 调试用
            }
        }

        private static void getRecommendationsbyrating() {
            System.out.println("\n--- Recommendations ---");
            try {
                // 调用推荐引擎
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
                // e.printStackTrace(); // 调试用
            }
        }

        private static void getRecommendationsbyyear(int year) {
            System.out.println("\n--- Recommendations ---");
            try {
                // 调用推荐引擎
                ArrayList<Movie> recs = RecommendByYear.Top_10(currentUser,year);
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
                // e.printStackTrace(); // 调试用
            }
        }

        // 辅助方法：根据ID查找电影
        private static Movie findMovieById(String id) {
            for (Movie m : allMovies) {
                if (m.getId().equalsIgnoreCase(id)) {
                    return m;
                }
            }
            return null;
        }

    }

