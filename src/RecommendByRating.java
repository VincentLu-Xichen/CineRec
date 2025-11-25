import java.util.*;
import java.util.stream.Collectors;
public class RecommendByRating {//这个方法单纯按照电影评分排列,列出Top10的电影






        public static ArrayList<Movie> Regroup(User user) {
    List<Movie> Filtered = RecommendationEngine.FilteredMovies(user); // 获取过滤后的电影名单Filtered
    List<Movie> Grouped = Filtered.stream()
            .sorted(Comparator.comparingDouble(Movie::getRating).reversed()) // 按评分从高到低排序
            .collect(Collectors.toList()); // 收集到Grouped列表中

    return new ArrayList<>(Grouped); // 返回新的ArrayList以符合返回类型
}
       public static ArrayList<Movie> Top_10(User user){
            ArrayList<Movie> receive= new ArrayList<>();//把上面方法中的过滤且重排后的电影接受过来（方法接口）
            receive=Regroup(user);
            ArrayList<Movie> Top10= new ArrayList<>();
            if (receive.size()<=10){return receive;}
            for (int i=0;i<10;i++){
                Top10.add(receive.get(i));
            }
            return  Top10;

       }


    }

