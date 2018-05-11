package verify;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains a <tt>Boolean[]</tt> field which represents a verify code image. If
 * <tt>Boolean[location]</tt> is <<t>true</t>, that dot is foreground or otherwise background. Field
 * <tt>Boolean[]</tt> is not visible but can be read by using method {@link #get(int, int)} and can be
 * modified by method {@link #set(int, int, boolean)}.
 */
public class BooleanImage {

	private static final int TOLERANCE = 270;

	private static int getBackground(BufferedImage img, int width, int height) {
		Map<Integer, Integer> map = new HashMap<>();
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				Integer rgb = img.getRGB(x, y);
				Integer n = map.get(rgb);
				map.put(rgb, n == null ? 1 : n + 1);
			}
		}
		return map.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
	}

	Boolean[] array;
	int width, height;

	private BooleanImage() {
	}

	/**
	 * Create a <tt>BooleanImage</tt> object by given <tt>BufferedImage</tt> object.
	 * @param image argument image
	 */
	public BooleanImage(BufferedImage image) {
		height = image.getHeight();
		width = image.getWidth();
		array = new Boolean[height * width];
		int bg = getBackground(image, width, height);
		for (int y = 0 ; y < height ; y++) {
			for (int x = 0 ; x < width ; x++) {
				array[width * y + x] = Color.difference(bg, image.getRGB(x, y)) > TOLERANCE;
			}
		}
	}

	/**
	 * Create a <tt>BooleanImage</tt> object by reading a file context.
	 * @param file argument file
	 */
	public BooleanImage(File file, char foreground) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			String readed;
			int rlen;
			while ((readed = in.readLine()) != null) {
				rlen = readed.length();
				if (width == 0) {
					width = rlen;
				}
				else if (width != rlen && rlen != 0) {
					throw new IllegalArgumentException();
				}
				sb.append(readed);
				height++;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		array = new Boolean[width * height];
		for (int i = 0 ; i < sb.length() ; i++) {
			array[i] = sb.charAt(i) == foreground;
		}
	}

	int x(int index) {
		return index % width;
	}

	int y(int index) {
		return index / width;
	}

	/**
	 * Get a piece of this object in given rectangle range. This method does not change this object.
	 * @param x      the x coordinate of rectangle
	 * @param y      the y coordinate of rectangle
	 * @param width  the width of rectangle
	 * @param height the height of rectangle
	 * @return a piece of this object in given rectangle range
	 * @throws IndexOutOfBoundsException if any argument is improper
	 */
	public BooleanImage cut(final int x, final int y, final int width, final int height) {
		Boolean[] copied = new Boolean[width * height];
		final int fh = y + height;
		for (int h = y ; h < fh ; h++) {
			System.arraycopy(array, this.width * h + x, copied, width * (h - y), width);
		}
		BooleanImage img = new BooleanImage();
		img.width = width;
		img.height = height;
		img.array = copied;
		return img;
	}

	/**
	 * Get the dot value of given coordinate.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the dot value of given coordinate
	 * @throws IndexOutOfBoundsException if x or y is not in proper range
	 */
	public boolean get(int x, int y) {
		return array[width * y + x];
	}

	/**
	 * Get a copy of array of this object.
	 * @return a copy of array of this object
	 */
	public Boolean[] getArray() {
		Boolean[] copied = new Boolean[array.length];
		System.arraycopy(array, 0, copied, 0, array.length);
		return copied;
	}

	/**
	 * Get the height of this image.
	 * @return the height of this image
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width of this image.
	 * @return the width of this image
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the length of array of this object.
	 * @return the length of array of this object
	 */
	public int length() {
		return width * height;
	}

	/**
	 * Set the specific dot value
	 * @param x     x coordinate
	 * @param y     y coordinate
	 * @param value argument value
	 */
	public void set(int x, int y, boolean value) {
		array[width * y + x] = value;
	}

	@Override
	public String toString() {
		return toString('0', ' ');
	}

	/**
	 * Get the imaginable text of this image.
	 * @param foreground a char representing foreground
	 * @param background a char representing background
	 * @return the imaginable text of this image
	 */
	public String toString(char foreground, char background) {
		StringBuilder sb = new StringBuilder();
		int x;
		int index = 0;
		int length = width * height;
		while (index < length) {
			for (x = 0; x < width ; x++, index++) {
				sb.append(array[index] ? foreground : background);
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
