package verify;

/**
 * This is a class used to analyze the color and rgb value.
 */
public class Color {

    private Color() {}

    /**
     * Get the r value of a given color which type is <tt>int</tt>.
     * @param rgb argument rgb
     * @return r value
     */
    public static int r(int rgb) {
        return (rgb & 0xff0000) >>> 16;
    }

    /**
     * Get the g value of a given color which type is <tt>int</tt>.
     * @param rgb argument rgb
     * @return g value
     */
    public static int g(int rgb) {
        return (rgb & 0xff00) >>> 8;
    }


    /**
     * Get the b value of a given color which type is <tt>int</tt>.
     * @param rgb argument rgb
     * @return b value
     */
    public static int b(int rgb) {
        return rgb & 0xff;
    }

    /**
     * Get the difference value of two given color which type is <tt>int</tt>. The difference value is
     * defined as the sum of difference of r, g, and b.
     * @param rgb1 argument rgb
     * @param rgb2 another rgb
     * @return difference value.
     */
    public static int difference(int rgb1, int rgb2) {
        return Math.abs(r(rgb1) - r(rgb2)) + Math.abs(g(rgb1) - g(rgb2)) + Math.abs(b(rgb1) - b(rgb2));
    }
}
