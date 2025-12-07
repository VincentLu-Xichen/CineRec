import java.util.ArrayList;
public class RecommendByRating {
    public static ArrayList<Movie> Regroup(User user) {
        ArrayList<Movie> Filtered = RecommendationEngine.FilteredMovies(user); //get the filtered movies

        ArrayList<Movie> Grouped= new ArrayList<>(RecommendationEngine.sortMoviesByRating(Filtered));//group by rating

        return Grouped; // Return new ArrayList to match return type
    }

    public static ArrayList<Movie> Top_10(User user) {
        ArrayList<Movie> receive = new ArrayList<>(Regroup(user));// Receive the filtered and reordered movies from above method (method interface)
        ArrayList<Movie> Top10 = new ArrayList<>();//top10 is the result of the method

        //if the filtered movie<=10,return all the filtered movies
        if (receive.size() <= 10) {
            return receive;
        }


        for (int i = 0; i < 10; i++) {
            Top10.add(receive.get(i));
        }
        return Top10;

    }



}

