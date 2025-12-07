import java.util.ArrayList;

public class RecommendByYear {
    public static ArrayList<Movie> Top_10(User user, int year){
        ArrayList<Movie> FilteredMovies= RecommendationEngine.FilteredMovies(user);//load the filtered movies;
        ArrayList<Movie> Top10= new ArrayList<>();//Top10 is the result of this method

        if (FilteredMovies.size()<=10) {return FilteredMovies;}//prevent this special case

        int loopnum=0;//record the loopnum(loopnum<=10)

        //search the movie whose year is same to the user input year
        for(Movie movie:FilteredMovies){
            if(year==movie.getYear()){
                Top10.add(movie);
                loopnum++;
            }
            if(loopnum>=10) break;
        }

        //search the movie whose year is near the user input year
        if(loopnum<10) {
            for (int i = 1; i < 85; i++) {//i= movie's year-input year,from 1 to 85, 85 is the rough span of years of the films
               // search the movies
                for (Movie movie : FilteredMovies) {
                    if (year + i == movie.getYear()) {
                        Top10.add(movie);
                        loopnum++;
                    }
                    if (loopnum >= 10) break;
                    if (year - i == movie.getYear()) {
                        Top10.add(movie);
                        loopnum++;
                    }

                }

                if (loopnum >= 10) break;// make sure that recommend no more than 10 movies
            }
        }
       return Top10;
    }
}
