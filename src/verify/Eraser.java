package verify;

import java.util.Arrays;

/**
 * This class is used to erase the lines or dots in a image that are not a part of codes needed to be
 * verify. This class provides only a static method {@link #erase(BooleanImage)}.
 */
public class Eraser {

    private Eraser() {}

    /**
     * Erase the lines or dots in a image that are not a part of codes needed to be verify.
     * @param img the image needed to be fixed.
     */
    public static void erase(BooleanImage img) {
        int len = img.length(), x, y, index;
        for (index = 0; index < len; index++) {
            if (img.array[index] && shouldEraseLine(toBlock(img, index))) {
                img.array[index] = false;
                if (shouldEraseDot(toLargeBlock(img, index))) {
                    x = img.x(index);
                    y = img.y(index);
                    img.set(x - 1, y - 1, false);
                    img.set(x, y - 1, false);
                    img.set(x + 1, y - 1, false);
                    img.set(x - 1, y, false);
                    img.set(x, y, false);
                    img.set(x + 1, y, false);
                    img.set(x - 1, y + 1, false);
                    img.set(x, y + 1, false);
                    img.set(x + 1, y + 1, false);
                }
            }
        }
    }

    private static boolean shouldEraseDot(Boolean[] block) {
        if (block == null) return false;
        return Arrays.stream(block).anyMatch(b -> b);
    }

    private static boolean shouldEraseLine(Boolean[] blocks) {
        if (blocks == null) return false;
        int n = (int) Arrays.stream(blocks).filter(b -> b).count();
        if (n == 2) {
            return blocks[1] && blocks[6] || blocks[3] && blocks[4];
        }
        return n < 2;
    }

    private static Boolean[] toBlock(BooleanImage img, int index) {
        int x = img.x(index);
        if (x == 0 || x == img.width - 1) return null;
        int y = img.y(index);
        if (y == 0 || y == img.height - 1) return null;
        return new Boolean[]{img.get(x - 1, y - 1), img.get(x, y - 1), img.get(x + 1, y - 1), img.get
                (x - 1, y), img.get(x + 1, y), img.get(x - 1, y + 1), img.get(x, y + 1), img.get(x +
                1, y + 1)};
    }

    private static Boolean[] toLargeBlock(BooleanImage img, int index) {
        int x = img.x(index);
        if (x < 3 || x > img.width - 4) return null;
        int y = img.y(index);
        if (y < 3 || y > img.height - 4) return null;

        Boolean[] block = new Boolean[49 - 9];
        int counter = 0, i, j;
        for (i = -3; i <= 3; i++) {
            for (j = -3; j <= 3; j++) {
                if (Math.abs(i) <= 1 && Math.abs(j) <= 1) continue;
                block[counter++] = img.get(x + i, y + j);
            }
        }
        return block;
    }
}
