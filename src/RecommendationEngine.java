import java.util.*;
import java.util.stream.Collectors;


public class RecommendationEngine {//算法如下：(该方法为默认方法）根据用户观看历史当中的比例来决定推荐给他什么类型的电影，并且去除掉watchlist以及history当中的电影。若最终不足10部电影，就把剩下的电影全部推荐给他
    public static ArrayList<String> getUserHistorygerne(User user){//这个方法用于接收用户观看过的电影种类
        ArrayList<Movie> historymovies=new ArrayList<>(Historyviewed(user));
        ArrayList<String> historygerne=new ArrayList<>();
        for (Movie m: historymovies){
            historygerne.add(m.getType());
        }
        return historygerne;
    }
    public static ArrayList<Movie> Historyviewed(User user){//这个方法用来接收用户的Arraylist

        History h =new History();
        h= user.getHistory();
        if (h==null){}
        ArrayList<Movie> history=new ArrayList<>(h.getWatchedMovies());
        return history;
    }//这个方法用来接收用户的观看记录

    public static ArrayList<Movie> getWatchlist(User user){
        ArrayList<Movie> watchlist=new ArrayList<>();
        Watchlist w =new Watchlist();
        w= user.getWatchlist();
        if (w==null){
            w=new Watchlist();//防止空指针异常
        }
        watchlist = new ArrayList<>(w.getWatchlist());
        return watchlist;
    }//这个方法用来接收Watchlist

    public static ArrayList<Movie> allMovie(){
        return Movie.loadFromCsv("data/movies.csv");

    }//这个方法用来接收所有的movie

    public static Map<String,Integer> getEachnum(User user) {//这个方法记录用户看过的电影中每个电影种类的次数

        ArrayList<String> historygerne = getUserHistorygerne(user);//接收第一个方法的返回值,储存每一种电影的数量到historygerne里面
        Map<String, Integer> typenum = new HashMap<>();//定义一个map typenum来储存用户观看过的每种电影的个数
        for (Movie movie:allMovie()){//将map初始化
            typenum.put(movie.getType(),0);
        }
        for (String type : historygerne) {

                int i = typenum.get(type);
                i++;
                typenum.put(type, i);

        }
        return typenum;
    }



    public static Map<String,Integer> findmovienum(User user){ //该方法用于找到每个电影应该寻找的（最大）次数
        int sum=0;//初始化总和num
        int max=0;
        String maxstring=null;
        Map<String,Integer> Typenum=new HashMap<>(getEachnum(user));//接受上个方法返回的用于储存各个电影观看次数的map Typenum
        for (String eachtype:Typenum.keySet()) {//算出总数sum,以及观看次数最多的电影类型 String maxstring，还有其对应的观看次数int max
            sum+=Typenum.get(eachtype);
          if(Typenum.get(eachtype)>=max){max=Typenum.get(eachtype);
          maxstring=eachtype;
          }


        }
        if (sum==0){//防空,如果用户没有观看记录，就随机推荐十种电影
            int i=0;//记录循环次数，不超过十
            for(Movie movie:allMovie()){
                if(Typenum.get(movie.getType())==0){
                    i++;
                    Typenum.put(movie.getType(),1);
                }
                if (i==10) break;
            }
            return Typenum;
        }
        int secondsum=0;//sub就是要补的值
        int sub=0;
        for(String eachtype:Typenum.keySet()){//算出每部电影应该推荐的数量，返回到Typenum里面
            int i=(int)Math.floor(Typenum.get(eachtype)/sum*10);
            Typenum.put(eachtype,i);
            secondsum+=i;
        }
        sub=10-secondsum;
       if(maxstring!=null) {Typenum.put(maxstring,Typenum.get(maxstring)+sub);

            }
       return Typenum;
        }

    public static ArrayList<Movie> TOGhisANDview(User user){//面相对象，把history 与 watchlist合并起来
        ArrayList<Movie> history=new ArrayList<>();
        ArrayList<Movie> watchlist=new ArrayList<>();
        ArrayList<Movie> historyandview=new ArrayList<>();
        history= Historyviewed(user);
        watchlist= getWatchlist(user);
        historyandview.addAll(history);
        historyandview.addAll(watchlist);
        return historyandview;

    }

    public static ArrayList<Movie> GroupByG(User user){//分组并且按照评分排序
        Map<String,Integer>typenum=new HashMap<>(findmovienum(user));//把要推荐的电影种类对应的数量的map typenum接过来
        ArrayList<Movie> result = new ArrayList<>();//最终要返回的排好序的数组为result
        List<Movie> allmovies=new ArrayList<>(allMovie());
        Set<String> repeat=new HashSet<>();//去重用的set repeat
        for (String typenumkey:typenum.keySet()) {//控制循环次数
            int max=0;
            String maxstring=null;
            for (String eachtype : typenum.keySet()) {//算出总数sum,以及观看次数最多的电影类型 String maxstring，还有其对应的观看次数int max
                if (typenum.get(eachtype) >= max&&!repeat.contains(eachtype)) {
                    max = typenum.get(eachtype);
                    maxstring = eachtype;
                }
            }//得到这个map的最大值以及他所对应的键（电影类型）
            List<Movie> innersequence=new ArrayList<>();//innersequnece用于存储总电影列表里面的一类电影
            for (String key:typenum.keySet()){//如果这个电影的类型和最大的电影类型一样，就把这一类电影加入到里面去innersequence里面去
           if(maxstring.equals(key)) {
               for (Movie movie : allmovies) {
                   if (movie.getType().equals(key)) {
                       innersequence.add(movie);
                   }

               }//把那一类电影加入到innersequence里面

               innersequence.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));//把innersequence进行排序
               result.addAll(innersequence);//排好序后加入到result里面

               repeat.add(key);
           }
           }
        }
        return result;
    }
   public static ArrayList<Movie> FilteredMovies(User user) {//过滤掉已经看过和watchlist中的电影
       ArrayList<Movie> allmovies = GroupByG(user);
       ArrayList<Movie> historyandview = TOGhisANDview (user);
       ArrayList<Movie> filteredMovies = new ArrayList<>();
       ArrayList<Movie> remove=new ArrayList<>();
       for (Movie all : allmovies) {//遍历，并且把history和view中有的电影在大电影名单当中去除掉
           boolean inhw=true;
           for (Movie HandW : historyandview) {
               if (all.getId().equals(HandW.getId())) {
                   inhw=false;
               }
           }
           if (inhw) {filteredMovies.add(all);}

           }
       return filteredMovies;
       }





    public static ArrayList<Movie> Top_10(User user) {//最终要返回的值
        ArrayList<Movie> getmovies = new ArrayList<>();
        getmovies = FilteredMovies(user);//得到一个过滤过后，且初步按照电影种类与评分进行排序的电影列表 getmovies
        Map<String,Integer> typenum=new HashMap<>(findmovienum(user));//把每部电影要推荐的个数接过来
        if (getmovies.size()<=10){return getmovies;}//如果要推荐的电影不足或只有十部，那就剩多少推多少
        getmovies=GroupByG(user);//把没有过滤之前的电影接过来

        ArrayList<String> generall =new ArrayList<>();//先找到电影种类的排序是什么样的，首先把所有电影的种类存在一个数组generall里面

        for (Movie m : getmovies ){
            generall.add(m.getType());

        }
        ArrayList<String> gener= new ArrayList<>();//去掉重复的,并把电影种类存在新的数组gener里面
        gener.add(generall.getFirst());//把第一个电影种类加入gener
        for(int i=0;i<getmovies.size()-1;i++) {//如果前一个电影种类和后一个电影种类不一样，就把后一个电影种类加入到gener里面
            if (!generall.get(i).equals(generall.get(i + 1))) {
                gener.add(generall.get(i + 1));
            }//最终获得一个存着电影种类的数组gener
        }
        ArrayList<Movie> top_10 = new ArrayList<>();//Top_10就是最终储存推荐电影的数组
            double[] genum= new double[15];//接收储存着要推荐的电影数量的数组genum
        int p=0;//写入genum
         for(String typenumkey:typenum.keySet()){
             genum[p]=typenum.get(typenumkey);
             p++;
         }
            boolean se=true;//定义一个布尔值se为true,se为true就要进入下面的while循环
            while(se) {
                for (int i = 0; i < 14; i++) {
                    if (genum[i] < genum[i + 1]) {
                        double m = genum[i];
                        genum[i] = genum[i + 1];
                        genum[i + 1] = m;//如果左边的数比右边的数字小，就调换他们的顺序
                    }
                }
                se = false;//把se设置为false，
                for (int n = 0; n < 14; n++) {//利用for循环检查一遍是不是已经排好了，如果没有，把se设置为true，再次进入while循环
                    if (genum[n] < genum[n + 1]) {
                        se = true;
                        break;
                    }

                }//到这里，genum已经变成了一个从大到小排列的数组
            }
                ArrayList<Movie> allmovies = new ArrayList<>();
                allmovies=FilteredMovies(user);//generall:存着按顺序排好的电影种类 genum：存着要推荐的电影数量 allmovies:存着过滤后的电影列表

                int num=0;//记录总循环次数，如果等于十则跳出循环
                int n=0;//记录每个种类实际循环了几次，等于应该推荐的最大数量则跳出循环
                for (int k=0;k<15;k++){//循环15次，即遍历每一个电影种类
                    for (Movie movie: allmovies){//以每一个种类为基点，再遍历过滤后的电影列表，如果正好是那个种类，就把它加入最后的电影列表
                        if (n==genum[k]){n=0;break;}//如果一个种类加入了n次，那么就跳出循环，换一个电影电影种类加入最终的电影列表

                        if(gener.get(k).equals(movie.getType())){
                            top_10.add(movie);//加入Top10
                            num++;//记录一共循环了几次
                            n++;

                        }
                        if (num==10) break;
                    }
                }

                int sup=10-num;//需要再补sup个电影到Top_10中
        for(int i=0;i<num;i++){
            allmovies.remove(i);
        }
                for (int i=0;i<sup;i++){
                    top_10.add(allmovies.get(i));
                }





        return top_10;
    }
    }










