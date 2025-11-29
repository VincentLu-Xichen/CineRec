import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History {
    //use ArrayList to store the user's viewing history
    private Map<String,String> movieAndtime;//将每一部看过的电影与观看时间配对
    private List<Movie> watchedMovies;
    public History() {//构造方法1：无参构造
        this.movieAndtime=new HashMap<>();
        this.watchedMovies=new ArrayList<>();
    }

    public void addWatchedMovie(String id,String time) {
        movieAndtime.put(id,time);
    }

    // remove the movie history from the list
    public void removeWatchedMovie(String id) {
        movieAndtime.remove(id);
    }

    private boolean containsMovie(String movieId) {//这个方法通过输入一个movie的ID来查询电影是否存在于history里
        for (String id : movieAndtime.keySet()) {
            if (id.equals(movieId)) {
                return true;
            }
        }
        return false;
    }

    public List<Movie> getWatchedMovies() {

        return watchedMovies;
    }
    public Map<String,String> getWatchedmap(){
        return movieAndtime;
    }

    // Transfer to the CSV format
    public String toCsvString() {
        String id="";//初始化一个字符串id
        for (int i = 0; i < getWatchedMovies().size(); i++) {
            id=id+getWatchedMovies().get(i).getId()+"@"+movieAndtime.get(getWatchedMovies().get(i).getId())+";";
        }
        if(id.equals("")){id=";";}

            id=id.substring(0,id.length()-1);

        return id;
    }


    public void mergeFromCsvWithDate(String csv, List<Movie> allMovies) {
        if (csv == null || csv.trim().isEmpty() || allMovies == null) return;//防特殊情况

        String[] records = csv.split(";");//把csv文件变成字符串数组record，即拆分开
        for (String record : records) {//这个for循环就是对record进行一番加工之后，存入list
            String trimmedRecord = record.trim();//去掉首位空格，得到trimmedRecord，如 "M001@2025-07-12"
            if (!trimmedRecord.isEmpty()) {//防空
                // analysis "M001@2025-07-12" format
                String[] parts = trimmedRecord.split("@");//分开trimmedRecord，得到"M001"  "2025-07-12"
                if (parts.length == 2) {
                    String movieId = parts[0].trim();//MovieId:"M001"
                    String date = parts[1].trim();//date:"2025-07-12"

                    // check if the same movie id already exists(without considering the date)
                    boolean exists = false;//防止已经存在
                    for (Movie movie : getWatchedMovies()) {
                        if (movie.getId().equals(movieId)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        // search for the corresponding Movie object from all the movie lists
                        Movie foundMovie = null;
                        for (Movie movie : allMovies) {
                            if (movie.getId().equals(movieId)) {
                                foundMovie = movie;
                                break;
                            }
                        }

                        if (foundMovie != null) {
                            movieAndtime.put(foundMovie.getId(),date);
                            watchedMovies.add(foundMovie);
                        }
                    }//用无参之后的添加把新的对象进行数值化
                }
            }
        }
    }

    //obtain the genre that users watch most frequently
    public String getMostWatchedGenre() {
        if (getWatchedMovies().isEmpty()) {
            return null;
        }
        java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();
        for (Movie movie : getWatchedMovies()) {
            String genre = movie.getType();
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
        }
        String mostWatched = null;
        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : genreCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostWatched = entry.getKey();
            }
        }
        return mostWatched;
    }
}