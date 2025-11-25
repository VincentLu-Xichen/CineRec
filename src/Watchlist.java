import java.util.ArrayList;
import java.util.List;

public class Watchlist {
    //store the movie id
    private List<String> movieIds;
    private List<Movie> movies = new ArrayList<>();
    public Watchlist() {
        this.movieIds = new ArrayList<>();
        this.movies =new ArrayList<>();
    }

    public Watchlist(List<Movie> movies){
        this.movies=movies;
        movieIds=new ArrayList<>();
        for (Movie movie:movies){
            movieIds.add(movie.getId());
        }
    }
    //add movie id
    public boolean addMovie(String movieId) {
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

    //obtain the list of id
    public List<String> getMovieIds() {
        return new ArrayList<>(movieIds);
    }
    public List<Movie> getWatchlist(){
        return new ArrayList<>(movies);
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }

    //csv conversion
    public String toCsvString() {
        String s="";
        for (int i = 0; i < movieIds.size(); i++) {
            s=s+movieIds.get(i)+";";
        }
        if (!s.isEmpty()) s=s.substring(0,s.length()-1);
        return s;
    }

    public void mergeFromCsv(String csvStr) {
        if (csvStr != null && !csvStr.trim().isEmpty()) {
            String[] ids = csvStr.split(";");
            for (String id : ids) {
                String trimmedId = id.trim();
                if (!trimmedId.isEmpty() && !movieIds.contains(trimmedId)) {
                    movieIds.add(trimmedId);
                }
            }
        }
    }

}