import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Movie {
    // 电影唯一编号，例如 "M001"
    private String id;
    // 片名
    private String title;
    // 类型，例如 "Drama"、"Action"
    private String type;
    // 上映年份
    private int year;
    // 评分（0.0 ~ 10.0）
    private double rating;

    // 无参构造器（方便初学者与后续扩展）
    public Movie() {}

    // 带参构造器
    public Movie(String id, String title, String type, int year, double rating) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.year = year;
        this.rating = rating;
    }

    // 基本的 getter / setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    /**
     * 从一行 CSV 文本解析 Movie 对象。
     * 格式固定为：id,title,genre,year,rating
     */
    public static Movie fromCsvRow(String row) {
        if (row == null) {
            throw new IllegalArgumentException("CSV 行为空");
        }
        // 按逗号拆分为 5 列
        String[] parts = row.split(",");
        // 简单格式校验：必须正好 5 列
        if (parts.length != 5) {
            throw new IllegalArgumentException("CSV 格式错误，需 5 列");
        }
        // 依次读取各列，并用 trim() 去掉两端空白
        String id = parts[0].trim();
        String title = parts[1].trim();
        String type = parts[2].trim(); // 第三列：类型（去除前后空白）
        int year = Integer.parseInt(parts[3].trim()); // 第四列：年份（转为整数）
        double rating = Double.parseDouble(parts[4].trim()); // 第五列：评分（转为小数）
        return new Movie(id, title, type, year, rating);
    }

    /**
     * 批量从 CSV 文件加载电影列表。自动跳过首行表头（若以 "id," 开头）。
     */
    public static ArrayList<Movie> loadFromCsv(String csvPath) {
        ArrayList<Movie> list = new ArrayList<Movie>(); // 存放所有读取到的电影
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvPath)); // 打开文件并按行读取
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // 去除每行两端空白
                if (line.isEmpty()) continue; // 跳过空行
                if (first) {
                    first = false;
                    if (line.toLowerCase().startsWith("id,")) {
                        continue; // 跳过表头
                    }
                }
                try {
                    Movie m = Movie.fromCsvRow(line); // 解析一行生成 Movie 对象
                    list.add(m);
                } catch (Exception e) {
                    System.err.println("解析失败，跳过: " + line); // 某行数据不合法，打印信息并跳过
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {} // 关闭文件
            }
        }
        return list;
    }

    // 提供默认路径加载方法，方便直接调用
    // 假设 CSV 文件在项目根目录下的 data 文件夹中，文件名为 movies.csv
    public static ArrayList<Movie> loadFromCsv() {
        return loadFromCsv("data/movies.csv");
    }

    @Override
    public String toString() {
        return id + " - " + title + " (" + type + ", " + year + ", " + rating + ")";
    }
}