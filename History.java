import java.util.ArrayList;

public class History {
    private ArrayList<String> items = new ArrayList<String>();

    public History() {
    }

    public void fromCsv(String csv) {
        items.clear();
        if (csv == null || csv.isEmpty()) return;
        String[] parts = csv.split(";");
        for (String p : parts) {
            String v = p.trim();
            if (!v.isEmpty()) items.add(v);
        }
    }

    public String toCsv() {
        return String.join(";", items);
    }
}