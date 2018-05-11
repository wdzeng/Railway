package verify;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

/**
 * This class is used to get a verify code image, but it does not contains the procedure to verify codes.
 * These functions are implemented in class {@link Verifier}. This class contains a method {@link
 * #verify(String)} which calls {@link Verifier} and returns the final result of codes.
 * @author Parabola
 * @see Verifier
 */
public class VerifyCode {

    private static final String VERIFY_CODE_IMG_URL = "http://railway1.hinet.net/ImageOut.jsp?";

    /**
     * Call this method to get the verify code string of a code image. This method opens a connection to
     * TRA's code image generator website and thus cookies may be needed.
     * @param cookies cookies used to connect with TRA's verify code generator website or null if cookies
     *                are not needed.
     * @return a string represents the context of verify code image.
     */
    public static String verify(String cookies) throws IOException {
        //將圖檔轉換成布林陣列
        BooleanImage img = new BooleanImage(getImage(cookies));
        //清除躁點
        Eraser.erase(img);
        //切割數字區塊
        List<BooleanImage> codes = Sissors.cut(img);
        //取得驗證碼值
        return Verifier.verify(codes);
    }

    /**
     * Get a <tt>BufferedImage</tt> object of verify codes. This method opens a connection to TRA's
     * verify codes generator website and download a verify code image.
     * @param cookies cookies used to connect to TRA's verify code generator website or null if cookies
     *                are not needed.
     * @return a <tt>BufferedImage</tt> object of verify codes.
     */
    public static BufferedImage getImage(String cookies) throws IOException {
        URLConnection conn = new URL(VERIFY_CODE_IMG_URL + new Random().nextDouble()).openConnection();
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Leep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        if (cookies != null) conn.setRequestProperty("cookie", cookies);
        conn.setConnectTimeout(5000);
        conn.connect();
        return ImageIO.read(conn.getInputStream());
    }
}

