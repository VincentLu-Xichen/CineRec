import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History {
    //use ArrayList to store the user's viewing history
    private Map<String, String> movieAndtime;// use map to match the History and the corresponding time
    private List<Movie> watchedMovies;

    public History() {//constructor without parameter
        this.movieAndtime = new HashMap<>();
        this.watchedMovies = new ArrayList<>();
    }

    //getter
    public List<Movie> getWatchedMovies() {

        return watchedMovies;
    }

    public Map<String, String> getWatchedmap() {
        return movieAndtime;
    }

    // Transfer to the CSV format
    public String toCsvString() {
        String id = "";//initialize a string id
        //add all the history and transform to the Csvformat
        for (int i = 0; i < getWatchedMovies().size(); i++) {
            id = id + getWatchedMovies().get(i).getId() + "@" + movieAndtime.get(getWatchedMovies().get(i).getId()) + ";";
        }
        if (id.equals("")) {
            id = ";";
        }//prevent id.length-1 is negative

        id = id.substring(0, id.length() - 1);

        return id;
    }

    //this method id to load the data from the file,and add the history attribute to the user object
    public void mergeFromCsvWithDate(String csv, List<Movie> allMovies) {
        if (csv == null || csv.trim().isEmpty() || allMovies == null) return;//prevent empty case

        String[] records = csv.split(";");//split the csv string by ";"


        for (String record : records) {
            String trimmedRecord = record.trim();
            //get the string like: "M001@2025-07-12"
            if (!trimmedRecord.isEmpty()) {//prevent empty case
                // analysis "M001@2025-07-12" format
                String[] parts = trimmedRecord.split("@");//split the movieId and time, store them in "parts"
                if (parts.length == 2) {
                    String movieId = parts[0].trim();//eg:part[0]:MovieId:"M001"
                    String date = parts[1].trim();//eg:part[1]:date:"2025-07-12"

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
                        // add the data to the two attributes
                        if (foundMovie != null) {
                            movieAndtime.put(foundMovie.getId(), date);
                            watchedMovies.add(foundMovie);
                        }
                    }
                }
            }
        }
    }
}
