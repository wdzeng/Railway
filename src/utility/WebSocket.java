package utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebSocket {

    public static HtmlContent get(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        conn.connect();
        StringBuilder html = new StringBuilder();
        String readed;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while ((readed = in.readLine()) != null) html.append(readed);
        }
        return new HtmlContent(html.toString());
    }

    public static HtmlContent post(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Accept", "/*/");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();
        StringBuilder html = new StringBuilder();
        String readed;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while ((readed = in.readLine()) != null) html.append(readed);
        }
        return new HtmlContent(html.toString());
    }
}
