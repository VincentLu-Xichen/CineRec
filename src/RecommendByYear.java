import java.util.ArrayList;

public class RecommendByYear {//用户自己输入一个年份，根据这个年份，寻找其年份附近的十部电影
    public static ArrayList<Movie> Top_10(User user, int year){
        ArrayList<Movie> FilteredMovies= RecommendationEngine.FilteredMovies(user);
        ArrayList<Movie> Top10= new ArrayList<>();
        if (FilteredMovies.size()<=10) {return FilteredMovies;}
        int loopnum=0;//记录循环的次数
        for (int i=0;i<85;i++){
            for (Movie movie: FilteredMovies){
                if(year+i==movie.getYear()){
                    Top10.add(movie);
                    loopnum++;
                }
                if (loopnum>=10) break;
                if(year-i==movie.getYear()){
                    Top10.add(movie);
                    loopnum++;
                }

            }
            if(loopnum>=10) break;
        }
       return Top10;
    }
}
