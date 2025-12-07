import java.util.ArrayList;
import java.util.List;

public class Watchlist {
    //store the movie id
    private List<String> movieIds;
    private List<Movie> movies = new ArrayList<>();
    public Watchlist() {//constructor without parameter
        this.movieIds = new ArrayList<>();
        this.movies =new ArrayList<>();
    }

    public Watchlist(List<Movie> movies){//constructor according to the List<Movie>
        this.movies=movies;
        movieIds=new ArrayList<>();
        for (Movie movie:movies){//get the List of movieID
            movieIds.add(movie.getId());
        }
    }

    public boolean addMovie(String movieId) {//add movie to movieID list
        if (!movieIds.contains(movieId)) {
            movieIds.add(movieId);
            return true;
        }
        return false;
    }

    //delete the movie id
    public boolean removeMovie(String movieId) {
        return movieIds.remove(movieId);
    }

    //getter
    public List<String> getMovieIds() {
        return new ArrayList<>(movieIds);
    }
    public List<Movie> getWatchlist(){
        return new ArrayList<>(movies);
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }


    public String toCsvString(){//csv conversion___ Example:M003;M009
        String id="";
        for (int i = 0; i < movieIds.size(); i++) {
            id=id+movieIds.get(i)+";";
        }
        if(id.isEmpty()){id=";";}//prevent id.length()-1 is negative

        id=id.substring(0,id.length()-1);//remove the last ";"

        return id;//finally,the id is same to the form of csv file

    }

    public void mergeFromCsv(String csvStr) {//use the csvString to initialize the watchlist object
        //if csvString(watchlist)is not empty,than spilt each id into String[] ids
        if (csvStr != null && !csvStr.trim().isEmpty()) {
            String[] ids = csvStr.split(";");
            //add all the movies id from the watchlist of the user to the movieId
            for (String id : ids) {
                String trimmedId = id.trim();
                if (!trimmedId.isEmpty() && !movieIds.contains(trimmedId)) {
                    movieIds.add(trimmedId);
                }
            }
        }
    }

}