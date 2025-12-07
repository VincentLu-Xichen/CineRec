import java.util.*;



public class RecommendationEngine {//Algorithm: Based on the proportion of user's viewing history to determine what type of movies to recommend, and remove movies from watchlist and history. If there are less than 10 movies, recommend all remaining movies
    public static ArrayList<String> getUserHistorygerne(User user){//This method is used to receive the genres of movies watched by the user
        ArrayList<Movie> historymovies=new ArrayList<>(Historyviewed(user));
        ArrayList<String> historygerne=new ArrayList<>();
        for (Movie m: historymovies){
            historygerne.add(m.getType());
        }
        return historygerne;
    }
    public static ArrayList<Movie> Historyviewed(User user){//This method is used to receive the user's ArrayList

        History h =new History();
        h= user.getHistory();
        if (h==null){}
        ArrayList<Movie> history=new ArrayList<>(h.getWatchedMovies());
        return history;
    }//This method is used to receive the user's viewing history

    public static ArrayList<Movie> getWatchlist(User user){
        ArrayList<Movie> watchlist=new ArrayList<>();
        Watchlist w =new Watchlist();
        w= user.getWatchlist();
        if (w==null){
            w=new Watchlist();//Prevent NullPointerException
        }
        watchlist = new ArrayList<>(w.getWatchlist());
        return watchlist;
    }//This method is used to receive the Watchlist

    public static ArrayList<Movie> allMovie(){
        return Movie.loadFromCsv("data/movies.csv");

    }//This method is used to receive all movies

    public static Map<String,Integer> getEachnum(User user) {//This method records the number of each movie genre the user has watched

        ArrayList<String> historygerne = getUserHistorygerne(user);//Receive the return value of the first method, store the quantity of each movie genre in historygerne
        Map<String, Integer> typenum = new HashMap<>();//Define a map typenum to store the count of each movie genre watched by the user
        for (Movie movie:allMovie()){//Initialize the map
            typenum.put(movie.getType(),0);
        }
        for (String type : historygerne) {

                int i = typenum.get(type);
                i++;
                typenum.put(type, i);

        }
        return typenum;
    }



    public static Map<String,Integer> findmovienum(User user){ //This method is used to find the maximum number of each movie type to search for
        int sum=0;//Initialize total sum
        int max=0;
        String maxstring=null;
        Map<String,Integer> Typenum=new HashMap<>(getEachnum(user));//Receive the map storing the count of each movie genre from the previous method
        for (String eachtype:Typenum.keySet()) {//Calculate the total sum, and find the most watched movie genre String maxstring, and its corresponding count int max
            sum+=Typenum.get(eachtype);
          if(Typenum.get(eachtype)>=max){max=Typenum.get(eachtype);
          maxstring=eachtype;
          }


        }
        if (sum==0){//Null check, if user has no viewing history, randomly recommend ten types of movies
            int i=0;//Record loop count, not exceeding ten
            for(Movie movie:allMovie()){
                if(Typenum.get(movie.getType())==0){
                    i++;
                    Typenum.put(movie.getType(),1);
                }
                if (i==10) break;
            }
            return Typenum;
        }
        int secondsum=0;//sub is the value to补
        int sub=0;
        for(String eachtype:Typenum.keySet()){//Calculate how many movies of each type should be recommended, return to Typenum
            int i=(int)Math.floor(Typenum.get(eachtype)/sum*10);
            Typenum.put(eachtype,i);
            secondsum+=i;
        }
        sub=10-secondsum;
       if(maxstring!=null) {Typenum.put(maxstring,Typenum.get(maxstring)+sub);

            }
       return Typenum;
        }

    public static ArrayList<Movie> TOGhisANDview(User user){//Object-oriented, combine history and watchlist
        ArrayList<Movie> history=new ArrayList<>();
        ArrayList<Movie> watchlist=new ArrayList<>();
        ArrayList<Movie> historyandview=new ArrayList<>();
        history= Historyviewed(user);
        watchlist= getWatchlist(user);
        historyandview.addAll(history);
        historyandview.addAll(watchlist);
        return historyandview;

    }

    public static ArrayList<Movie> GroupByG(User user){//Group and sort by rating
        Map<String,Integer>typenum=new HashMap<>(findmovienum(user));//Receive the map with recommended movie genres and their quantities
        ArrayList<Movie> result = new ArrayList<>();//The final sorted array to be returned as result
        List<Movie> allmovies=new ArrayList<>(allMovie());
        Set<String> repeat=new HashSet<>();//Set for deduplication
        for (int loop = 0; loop < typenum.size(); loop++) {//Control loop count
            int max=0;
            String maxstring=null;
            for (String eachtype : typenum.keySet()) {//Calculate total sum, and find the most watched movie genre String maxstring, and its corresponding count int max
                if (typenum.get(eachtype) >= max&&!repeat.contains(eachtype)) {
                    max = typenum.get(eachtype);
                    maxstring = eachtype;
                }
            }//Get the maximum value of this map and its corresponding key (movie genre)
            ArrayList<Movie> innersequence=new ArrayList<>();//innersequence is used to store one category of movies from the total movie list
            for (String key:typenum.keySet()){//If this movie genre matches the maximum movie genre, add this category of movies to innersequence
           if(maxstring.equals(key)) {
               for (Movie movie : allmovies) {
                   if (movie.getType().equals(key)) {
                       innersequence.add(movie);
                   }

               }//Add that category of movies to innersequence


               result.addAll(sortMoviesByRating(innersequence));//Add sorted results to result

               repeat.add(key);
           }
           }
        }
        return result;
    }
   public static ArrayList<Movie> FilteredMovies(User user) {//Filter out movies already watched and in watchlist
       ArrayList<Movie> allmovies = GroupByG(user);
       ArrayList<Movie> historyandview = TOGhisANDview (user);
       ArrayList<Movie> filteredMovies = new ArrayList<>();
       for (Movie all : allmovies) {//Iterate, and remove movies that exist in history and view from the main movie list
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





    public static ArrayList<Movie> Top_10(User user) {//Final value to be returned
        ArrayList<Movie> getmovies = new ArrayList<>();
        getmovies = FilteredMovies(user);//Get a filtered movie list initially sorted by movie genre and rating
        Map<String,Integer> typenum=new HashMap<>(findmovienum(user));//Receive the number of movies to recommend for each type
        if (getmovies.size()<=10){return getmovies;}//If there are 10 or fewer movies to recommend, recommend all remaining
        getmovies=GroupByG(user);//Receive unfiltered movies

        ArrayList<String> generall =new ArrayList<>();//First find the sorting order of movie genres, first store all movie genres in array generall

        for (Movie m : getmovies ){
            generall.add(m.getType());

        }
        ArrayList<String> gener= new ArrayList<>();//Remove duplicates, and store movie genres in new array gener
        gener.add(generall.getFirst());//Add the first movie genre to gener
        for(int i=0;i<getmovies.size()-1;i++) {//If the previous movie genre is different from the next movie genre, add the next movie genre to gener
            if (!generall.get(i).equals(generall.get(i + 1))) {
                gener.add(generall.get(i + 1));
            }//Finally obtain an array gener storing movie genres
        }
        ArrayList<Movie> top_10 = new ArrayList<>();//Top_10 is the final array storing recommended movies
            double[] genum= new double[15];//Receive array genum storing the number of movies to recommend
        int p=0;//Write to genum
         for(String typenumkey:typenum.keySet()){
             genum[p]=typenum.get(typenumkey);
             p++;
         }
            boolean se=true;//Define a boolean value se as true, if se is true, enter the following while loop
            while(se) {
                for (int i = 0; i < 14; i++) {
                    if (genum[i] < genum[i + 1]) {
                        double m = genum[i];
                        genum[i] = genum[i + 1];
                        genum[i + 1] = m;//If the left number is smaller than the right number, swap their positions
                    }
                }
                se = false;//Set se to false
                for (int n = 0; n < 14; n++) {//Use for loop to check if it's already sorted, if not, set se to true and re-enter while loop
                    if (genum[n] < genum[n + 1]) {
                        se = true;
                        break;
                    }

                }//At this point, genum has become a descending array
            }
                ArrayList<Movie> allmovies = new ArrayList<>();
                allmovies=FilteredMovies(user);//generall: stores movie genres in sorted order genum: stores number of movies to recommend allmovies: stores filtered movie list

                int num=0;//Record total loop count, break if equals ten
                int n=0;//Record how many times each genre actually looped, break if equals maximum recommended quantity
                for (int k=0;k<15;k++){//Loop 15 times, i.e., iterate through each movie genre
                    for (Movie movie: allmovies){//Using each genre as base, iterate through filtered movie list, if it matches the genre, add it to final movie list
                        if (n==genum[k]){n=0;break;}//If a genre has added n times, break the loop and switch to another movie genre to add to final movie list

                        if(gener.get(k).equals(movie.getType())){
                            top_10.add(movie);//Add to Top10
                            num++;//Record total loop count
                            n++;

                        }
                        if (num==10) break;
                    }
                }

                int sup=10-num;//Need to supplement sup movies to Top_10
        for(int i=0;i<num;i++){
            allmovies.remove(i);
        }
                for (int i=0;i<sup;i++){
                    top_10.add(allmovies.get(i));
                }





        return top_10;
    }


    public static ArrayList<Movie> sortMoviesByRating(ArrayList<Movie> movies) {//Helper method (sort movies by rating from high to low)
        ArrayList<Movie> sortedMovies = new ArrayList<>(movies); // Create a copy to avoid modifying the original list


        for (int i = 0; i < sortedMovies.size() - 1; i++) {
            for (int j = i+1; j < sortedMovies.size() ; j++) {
                if (sortedMovies.get(i).getRating() < sortedMovies.get(j).getRating()) {
                    // Swap element positions
                    Movie temp = sortedMovies.get(j);
                    sortedMovies.set(j, sortedMovies.get(i));
                    sortedMovies.set(i, temp);
                }
            }
        }

        return sortedMovies;
    }
    }
