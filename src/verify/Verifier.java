package verify;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to verify a code image to real character. Code images are compared to data of each
 * number, and the number with highest score is regarded as the real character.
 */
public class Verifier {

    private static class Singleton {
        private static final Map<Integer, List<BooleanImage>> DATA;

        static {
            DATA = new HashMap<>(10);
            File[] files;
            List<BooleanImage> list;
            for (int i = 0; i < 10; i++) {
                files = new File("data/codes/" + i).listFiles();
                if (files == null) DATA.put(i, new ArrayList<>(1));
                list = new ArrayList<>(files.length);
                DATA.put(i, list);
                for (File file : files)
                    list.add(new BooleanImage(file, '1'));
            }
        }

        private static Map<Integer, List<BooleanImage>> getData() { return DATA; }
    }

    /**
     * Verify a list of code images. Each image should contains only one code. The number is return as a
     * <tt>String</tt> object.
     * @param images a list of code images
     * @return the number which has the highest possibility
     */
    public static String verify(List<BooleanImage> images) {
        if (images == null) return null;
        StringBuilder verifyCode = new StringBuilder();
        for (BooleanImage img : images)
            verifyCode.append(verify(img));
        return verifyCode.toString();
    }

    /**
     * Verify a code image. This image should contains only one code. The number is return as a
     * <tt>String</tt> object.
     * @param image code image
     * @return the number which has the highest possibility
     */
    public static String verify(BooleanImage image) {
        int maxN = -1;
        double maxScore = 0.0, score;
        for (int n = 0; n < 10; n++) {
            score = verify(image, n);
            if (score > maxScore) {
                maxN = n;
                maxScore = score;
            }
        }
        return Integer.toString(maxN);
    }

    /**
     * Compare an image to data of a number and get the score of the similarity.
     * @param img argument image
     * @param num argument number
     * @return the score of the similarity
     */
    public static double verify(BooleanImage img, int num) {
        List<Integer> scores = Singleton.getData().get(num).stream().map(d -> verify(img, d)).collect
                (Collectors.toList());
        Collections.sort(scores);
        int size = scores.size();
        if (size <= 3) return scores.stream().mapToInt(i -> i).average().getAsDouble();
        return (double) (scores.get(size - 1) + scores.get(size - 2) + scores.get(size - 3)) / 3;
    }

    /**
     * Compare two images and get the score of there similarity.
     * @param img1 an image
     * @param img2 another image
     * @return the score of its similarity
     */
    public static int verify(BooleanImage img1, BooleanImage img2) {
        int score = 0, x, y;
        boolean value;
        for (int i = 0; i < img1.array.length; i++) {
            x = Math.round((float) img1.x(i) * (img2.width - 1) / img1.width);
            y = Math.round((float) img1.y(i) * (img2.height - 1) / img1.height);
            value = img2.get(x, y);
            if (img1.array[i] == value) score++;
        }
        return score;
    }
}
