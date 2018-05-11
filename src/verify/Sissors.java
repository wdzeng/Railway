package verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is a tool used to cut out the space or margin of the verify codes image (exactly a {@link
 * BooleanImage object}). This class provides only a static method {@link #cut(BooleanImage)} of which
 * arguments is always a <tt>BooleanImage</tt> object.
 */
public class Sissors {

    private Sissors() {}

    /**
     * This method cuts a {@link BooleanImage} object into pieces. Each of pieces is also
     * <tt>BooleanImage</tt> object represents a character image.
     * @param img a BooleanImage object cut
     * @return pieces which represents a character image.
     */
    public static List<BooleanImage> cut(BooleanImage img) {
        final int newH = img.height - 6;
        final int newW = img.width - 6;
        Boolean[] newArray = new Boolean[newW * newH];
        for (int h = 0; h < newH; h++)
            System.arraycopy(img.array, img.width * h + 2, newArray, newW * h, newW);
        img.array = newArray;
        img.width = newW;
        img.height = newH;

        List<Horizontal> horizontals = new ArrayList<>(6);
        Horizontal added = null;
        boolean left = true, emptyColumn;
        for (int i = 0; i < img.width; i++) {
            emptyColumn = isColomnEmpty(img, i);
            if (left && !emptyColumn) {
                //數字已滿，不要再加新的碎片了，直接宣告錯誤結束
                if (horizontals.size() == 6) return null;
                added = new Horizontal();
                added.left = i;
                left = false;
            }
            else if (!left && emptyColumn) {
                added.right = i;
                //若碎片寬度足夠才加入
                if (added.width() >= 8) horizontals.add(added);
                left = true;
            }
        }
        int hSize = horizontals.size();
        if (!left) {
            //碎片不構，回傳失敗
            if (hSize < 4) return null;
            //碎片仍未完成，補齊
            added.right = img.width - 1;
            horizontals.add(added);
        }
        //碎片不構，回傳失敗
        else if (hSize < 5) return null;

        List<BooleanImage> list = new ArrayList<>(hSize);
        Vertical ver, tmp = null;
        boolean emptyRow, top;
        int verHei, garbage = 0, y;
        for (Horizontal hor : horizontals) {
            top = true;
            ver = new Vertical();
            for (y = 0; y < img.height; y++) {
                //若剩餘Row不夠足以形成更大的碎片，則直接結束
                if (img.height - y <= ver.height()) break;
                emptyRow = isRowEmpty(img, y, hor.left, hor.width());
                if (top && !emptyRow) {
                    tmp = new Vertical();
                    tmp.top = y;
                    top = false;
                }
                else if (!top && emptyRow) {
                    tmp.bottom = y;
                    verHei = tmp.height();
                    if (verHei > ver.height()) ver = tmp;
                    top = true;
                }
            }
            //碎片高度不足則丟棄
            if (ver.height() < 8) {
                //若丟棄致使數字不夠，則回傳失敗
                if (hSize - (++garbage) < 5) return null;
            }
            else list.add(img.cut(hor.left, ver.top, hor.width(), ver.height()));
        }

        return list;
    }

    private static boolean isRowEmpty(BooleanImage img, int y, int x, int width) {
        return Arrays.stream(img.array).skip(img.width * y + x).limit(width).noneMatch(b -> b);
    }

    private static boolean isColomnEmpty(BooleanImage img, int x) {
        int len = img.length();
        for (int index = x; index < len; index += img.width)
            if (img.array[index]) return false;
        return true;
    }

    private static class Horizontal {
        int left, right;

        public int width() {
            return right - left;
        }
    }

    private static class Vertical {
        int top, bottom;

        public int height() {
            return bottom - top;
        }
    }
}


