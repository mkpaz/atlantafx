/*
 * Java Color Thief
 * by Sven Woltmann, Fonpit AG
 * https://androidpit.com
 * https://androidpit.de
 *
 * Creative Commons Attribution 2.5 License:
 * http://creativecommons.org/licenses/by/2.5/
 */

package atlantafx.sampler.page.showcase.musicplayer;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"unused", "NarrowingCompoundAssignment"})
final class ColorThief {

    private ColorThief() {
        // Default constructor
    }

    private static final int DEFAULT_QUALITY = 10;
    private static final boolean DEFAULT_IGNORE_WHITE = true;

    public static int[] getColor(BufferedImage source) {
        int[][] palette = getPalette(source, 5);

        if (palette == null) {
            return null;
        }

        return palette[0];
    }

    public static int[][] getPalette(BufferedImage source, int colorCount) {
        MMCQ.ColorMap colorMap = getColorMap(source, colorCount);

        if (colorMap == null) {
            return null;
        }

        return colorMap.palette();
    }

    public static MMCQ.ColorMap getColorMap(BufferedImage source, int colorCount) {
        return getColorMap(source, colorCount, DEFAULT_QUALITY, DEFAULT_IGNORE_WHITE);
    }

    public static MMCQ.ColorMap getColorMap(BufferedImage sourceImage, int colorCount, int quality,
                                            boolean ignoreWhite) {
        if (colorCount < 2 || colorCount > 256) {
            throw new IllegalArgumentException("Specified colorCount must be between 2 and 256.");
        }
        if (quality < 1) {
            throw new IllegalArgumentException("Specified quality should be greater then 0.");
        }

        int[][] pixelArray = switch (sourceImage.getType()) {
            case TYPE_3BYTE_BGR, TYPE_4BYTE_ABGR -> getPixelsFast(sourceImage, quality, ignoreWhite);
            default -> getPixelsSlow(sourceImage, quality, ignoreWhite);
        };

        return MMCQ.quantize(pixelArray, colorCount);
    }

    private static int[][] getPixelsFast(
        BufferedImage sourceImage,
        int quality,
        boolean ignoreWhite) {
        DataBufferByte imageData = (DataBufferByte) sourceImage.getRaster().getDataBuffer();
        byte[] pixels = imageData.getData();
        int pixelCount = sourceImage.getWidth() * sourceImage.getHeight();

        int colorDepth;
        int type = sourceImage.getType();
        colorDepth = switch (type) {
            case TYPE_3BYTE_BGR -> 3;
            case TYPE_4BYTE_ABGR -> 4;
            default -> throw new IllegalArgumentException("Unhandled type: " + type);
        };

        int expectedDataLength = pixelCount * colorDepth;
        if (expectedDataLength != pixels.length) {
            throw new IllegalArgumentException(
                "(expectedDataLength = " + expectedDataLength + ") != (pixels.length = " + pixels.length + ")"
            );
        }

        int numRegardedPixels = (pixelCount + quality - 1) / quality;

        int numUsedPixels = 0;
        int[][] pixelArray = new int[numRegardedPixels][];
        int offset, r, g, b, a;

        switch (type) {
            case TYPE_3BYTE_BGR -> {
                for (int i = 0; i < pixelCount; i += quality) {
                    offset = i * 3;
                    b = pixels[offset] & 0xFF;
                    g = pixels[offset + 1] & 0xFF;
                    r = pixels[offset + 2] & 0xFF;

                    if (!(ignoreWhite && r > 250 && g > 250 && b > 250)) {
                        pixelArray[numUsedPixels] = new int[] {r, g, b};
                        numUsedPixels++;
                    }
                }
            }
            case TYPE_4BYTE_ABGR -> {
                for (int i = 0; i < pixelCount; i += quality) {
                    offset = i * 4;
                    a = pixels[offset] & 0xFF;
                    b = pixels[offset + 1] & 0xFF;
                    g = pixels[offset + 2] & 0xFF;
                    r = pixels[offset + 3] & 0xFF;

                    if (a >= 125 && !(ignoreWhite && r > 250 && g > 250 && b > 250)) {
                        pixelArray[numUsedPixels] = new int[] {r, g, b};
                        numUsedPixels++;
                    }
                }
            }
            default -> throw new IllegalArgumentException("Unhandled type: " + type);
        }

        return Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
    }

    private static int[][] getPixelsSlow(
        BufferedImage sourceImage,
        int quality,
        boolean ignoreWhite) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        int pixelCount = width * height;
        int numRegardedPixels = (pixelCount + quality - 1) / quality;
        int numUsedPixels = 0;

        int[][] res = new int[numRegardedPixels][];
        int r, g, b;

        for (int i = 0; i < pixelCount; i += quality) {
            int row = i / width;
            int col = i % width;
            int rgb = sourceImage.getRGB(col, row);

            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;
            if (!(ignoreWhite && r > 250 && g > 250 && b > 250)) {
                res[numUsedPixels] = new int[] {r, g, b};
                numUsedPixels++;
            }
        }

        return Arrays.copyOfRange(res, 0, numUsedPixels);
    }

    private static class MMCQ {

        private static final int SIGBITS = 5;
        private static final int RSHIFT = 8 - SIGBITS;
        private static final int MULT = 1 << RSHIFT;
        private static final int HISTOSIZE = 1 << (3 * SIGBITS);
        private static final int VBOX_LENGTH = 1 << SIGBITS;
        private static final double FRACT_BY_POPULATION = 0.75;
        private static final int MAX_ITERATIONS = 1000;

        static int getColorIndex(int r, int g, int b) {
            return (r << (2 * SIGBITS)) + (g << SIGBITS) + b;
        }

        public static class VBox {
            int r1;
            int r2;
            int g1;
            int g2;
            int b1;
            int b2;

            private final int[] histo;

            private int[] gAvg;
            private Integer gVolume;
            private Integer gCount;

            public VBox(int r1, int r2, int g1, int g2, int b1, int b2, int[] histo) {
                this.r1 = r1;
                this.r2 = r2;
                this.g1 = g1;
                this.g2 = g2;
                this.b1 = b1;
                this.b2 = b2;
                this.histo = histo;
            }

            @Override
            public String toString() {
                return "r1: " + r1 + " / r2: " + r2
                    + " / g1: " + g1 + " / g2: " + g2
                    + " / b1: " + b1 + " / b2: " + b2;
            }

            public int volume(boolean force) {
                if (gVolume == null || force) {
                    gVolume = ((r2 - r1 + 1) * (g2 - g1 + 1) * (b2 - b1 + 1));
                }
                return gVolume;
            }

            public int count(boolean force) {
                if (gCount == null || force) {
                    int npix = 0;
                    int i, j, k, index;

                    for (i = r1; i <= r2; i++) {
                        for (j = g1; j <= g2; j++) {
                            for (k = b1; k <= b2; k++) {
                                index = getColorIndex(i, j, k);
                                npix += histo[index];
                            }
                        }
                    }

                    gCount = npix;
                }

                return gCount;
            }

            @Override
            @SuppressWarnings("MethodDoesntCallSuperMethod")
            public VBox clone() {
                return new VBox(r1, r2, g1, g2, b1, b2, histo);
            }

            public int[] avg(boolean force) {
                if (gAvg == null || force) {
                    int ntot = 0;
                    int rsum = 0;
                    int gsum = 0;
                    int bsum = 0;
                    int hval, i, j, k, histoindex;

                    for (i = r1; i <= r2; i++) {
                        for (j = g1; j <= g2; j++) {
                            for (k = b1; k <= b2; k++) {
                                histoindex = getColorIndex(i, j, k);
                                hval = histo[histoindex];
                                ntot += hval;
                                rsum += (hval * (i + 0.5) * MULT);
                                gsum += (hval * (j + 0.5) * MULT);
                                bsum += (hval * (k + 0.5) * MULT);
                            }
                        }
                    }

                    if (ntot > 0) {
                        gAvg = new int[] {
                            (rsum / ntot), (gsum / ntot), (bsum / ntot)
                        };
                    } else {
                        gAvg = new int[] {
                            (MULT * (r1 + r2 + 1) / 2),
                            (MULT * (g1 + g2 + 1) / 2),
                            (MULT * (b1 + b2 + 1) / 2)
                        };
                    }
                }

                return gAvg;
            }

            public boolean contains(int[] pixel) {
                int rval = pixel[0] >> RSHIFT;
                int gval = pixel[1] >> RSHIFT;
                int bval = pixel[2] >> RSHIFT;
                return (rval >= r1 && rval <= r2 && gval >= g1 && gval <= g2 && bval >= b1 && bval <= b2);
            }
        }

        public static class ColorMap {

            public final List<VBox> vboxes = new ArrayList<>();

            public void push(VBox box) {
                vboxes.add(box);
            }

            public int[][] palette() {
                int numVBoxes = vboxes.size();
                int[][] palette = new int[numVBoxes][];
                for (int i = 0; i < numVBoxes; i++) {
                    palette[i] = vboxes.get(i).avg(false);
                }
                return palette;
            }

            public int size() {
                return vboxes.size();
            }

            public int[] map(int[] color) {
                int numVBoxes = vboxes.size();
                for (VBox vbox : vboxes) {
                    if (vbox.contains(color)) {
                        return vbox.avg(false);
                    }
                }
                return nearest(color);
            }

            public int[] nearest(int[] color) {
                double d1 = Double.MAX_VALUE;
                double d2;
                int[] pColor = null;

                int numVBoxes = vboxes.size();
                for (VBox vbox : vboxes) {
                    int[] vbColor = vbox.avg(false);
                    d2 = Math.sqrt(Math.pow(color[0] - vbColor[0], 2)
                        + Math.pow(color[1] - vbColor[1], 2)
                        + Math.pow(color[2] - vbColor[2], 2)
                    );
                    if (d2 < d1) {
                        d1 = d2;
                        pColor = vbColor;
                    }
                }
                return pColor;
            }

        }

        private static int[] getHisto(int[][] pixels) {
            int[] histo = new int[HISTOSIZE];
            int index, rval, gval, bval;
            int numPixels = pixels.length;

            for (int[] pixel : pixels) {
                rval = pixel[0] >> RSHIFT;
                gval = pixel[1] >> RSHIFT;
                bval = pixel[2] >> RSHIFT;
                index = getColorIndex(rval, gval, bval);
                histo[index]++;
            }
            return histo;
        }

        private static VBox vboxFromPixels(int[][] pixels, int[] histo) {
            int rmin = 1000000, rmax = 0;
            int gmin = 1000000, gmax = 0;
            int bmin = 1000000, bmax = 0;

            int rval, gval, bval;

            int numPixels = pixels.length;
            for (int[] pixel : pixels) {
                rval = pixel[0] >> RSHIFT;
                gval = pixel[1] >> RSHIFT;
                bval = pixel[2] >> RSHIFT;

                if (rval < rmin) {
                    rmin = rval;
                } else if (rval > rmax) {
                    rmax = rval;
                }

                if (gval < gmin) {
                    gmin = gval;
                } else if (gval > gmax) {
                    gmax = gval;
                }

                if (bval < bmin) {
                    bmin = bval;
                } else if (bval > bmax) {
                    bmax = bval;
                }
            }

            return new VBox(rmin, rmax, gmin, gmax, bmin, bmax, histo);
        }

        private static VBox[] medianCutApply(int[] histo, VBox vbox) {
            if (vbox.count(false) == 0) {
                return null;
            }

            if (vbox.count(false) == 1) {
                return new VBox[] {vbox.clone(), null};
            }

            int rw = vbox.r2 - vbox.r1 + 1;
            int gw = vbox.g2 - vbox.g1 + 1;
            int bw = vbox.b2 - vbox.b1 + 1;
            int maxw = Math.max(Math.max(rw, gw), bw);

            int total = 0;
            int[] partialSum = new int[VBOX_LENGTH];
            Arrays.fill(partialSum, -1);
            int[] lookAheadSum = new int[VBOX_LENGTH];
            Arrays.fill(lookAheadSum, -1);
            int i, j, k, sum, index;

            if (maxw == rw) {
                for (i = vbox.r1; i <= vbox.r2; i++) {
                    sum = 0;
                    for (j = vbox.g1; j <= vbox.g2; j++) {
                        for (k = vbox.b1; k <= vbox.b2; k++) {
                            index = getColorIndex(i, j, k);
                            sum += histo[index];
                        }
                    }
                    total += sum;
                    partialSum[i] = total;
                }
            } else if (maxw == gw) {
                for (i = vbox.g1; i <= vbox.g2; i++) {
                    sum = 0;
                    for (j = vbox.r1; j <= vbox.r2; j++) {
                        for (k = vbox.b1; k <= vbox.b2; k++) {
                            index = getColorIndex(j, i, k);
                            sum += histo[index];
                        }
                    }
                    total += sum;
                    partialSum[i] = total;
                }
            } else {
                for (i = vbox.b1; i <= vbox.b2; i++) {
                    sum = 0;
                    for (j = vbox.r1; j <= vbox.r2; j++) {
                        for (k = vbox.g1; k <= vbox.g2; k++) {
                            index = getColorIndex(j, k, i);
                            sum += histo[index];
                        }
                    }
                    total += sum;
                    partialSum[i] = total;
                }
            }

            for (i = 0; i < VBOX_LENGTH; i++) {
                if (partialSum[i] != -1) {
                    lookAheadSum[i] = total - partialSum[i];
                }
            }

            return maxw == rw ? doCut('r', vbox, partialSum, lookAheadSum, total)
                : maxw == gw ? doCut('g', vbox, partialSum, lookAheadSum, total)
                : doCut('b', vbox, partialSum, lookAheadSum, total);
        }

        private static VBox[] doCut(
            char color,
            VBox vbox,
            int[] partialSum,
            int[] lookAheadSum,
            int total
        ) {
            int vboxDim1;
            int vboxDim2;

            if (color == 'r') {
                vboxDim1 = vbox.r1;
                vboxDim2 = vbox.r2;
            } else if (color == 'g') {
                vboxDim1 = vbox.g1;
                vboxDim2 = vbox.g2;
            } else {
                vboxDim1 = vbox.b1;
                vboxDim2 = vbox.b2;
            }

            int left, right;
            VBox vbox1, vbox2;
            int d2, count2;

            for (int i = vboxDim1; i <= vboxDim2; i++) {
                if (partialSum[i] > total / 2) {
                    vbox1 = vbox.clone();
                    vbox2 = vbox.clone();

                    left = i - vboxDim1;
                    right = vboxDim2 - i;

                    if (left <= right) {
                        d2 = Math.min(vboxDim2 - 1, (i + right / 2));
                    } else {
                        d2 = Math.max(vboxDim1, ((int) (i - 1 - left / 2.0)));
                    }

                    while (d2 < 0 || partialSum[d2] <= 0) {
                        d2++;
                    }
                    count2 = lookAheadSum[d2];
                    while (count2 == 0 && d2 > 0 && partialSum[d2 - 1] > 0) {
                        count2 = lookAheadSum[--d2];
                    }

                    if (color == 'r') {
                        vbox1.r2 = d2;
                        vbox2.r1 = d2 + 1;
                    } else if (color == 'g') {
                        vbox1.g2 = d2;
                        vbox2.g1 = d2 + 1;
                    } else {
                        vbox1.b2 = d2;
                        vbox2.b1 = d2 + 1;
                    }

                    return new VBox[] {vbox1, vbox2};
                }
            }

            throw new RuntimeException("VBox can't be cut");
        }

        public static ColorMap quantize(int[][] pixels, int maxcolors) {
            if (pixels.length == 0 || maxcolors < 2 || maxcolors > 256) {
                return null;
            }

            int[] histo = getHisto(pixels);

            VBox vbox = vboxFromPixels(pixels, histo);
            ArrayList<VBox> pq = new ArrayList<>();
            pq.add(vbox);

            int target = (int) Math.ceil(FRACT_BY_POPULATION * maxcolors);

            iter(pq, COMPARATOR_COUNT, target, histo);
            pq.sort(COMPARATOR_PRODUCT);

            if (maxcolors > pq.size()) {
                iter(pq, COMPARATOR_PRODUCT, maxcolors, histo);
            }

            Collections.reverse(pq);

            ColorMap colorMap = new ColorMap();
            for (VBox vb : pq) {
                colorMap.push(vb);
            }

            return colorMap;
        }

        @SuppressWarnings("ConstantConditions")
        private static void iter(List<VBox> lh, Comparator<VBox> comparator, int target, int[] histo) {
            int niters = 0;
            VBox vbox;

            while (niters < MAX_ITERATIONS) {
                vbox = lh.get(lh.size() - 1);
                if (vbox.count(false) == 0) {
                    lh.sort(comparator);
                    niters++;
                    continue;
                }
                lh.remove(lh.size() - 1);

                VBox[] vboxes = medianCutApply(histo, vbox);
                VBox vbox1 = vboxes[0];
                VBox vbox2 = vboxes[1];

                if (vbox1 == null) {
                    throw new RuntimeException("vbox1 not defined; shouldn't happen!");
                }

                lh.add(vbox1);
                if (vbox2 != null) {
                    lh.add(vbox2);
                }
                lh.sort(comparator);

                if (lh.size() >= target) {
                    return;
                }
                if (niters++ > MAX_ITERATIONS) {
                    return;
                }
            }
        }

        private static final Comparator<VBox> COMPARATOR_COUNT = Comparator.comparingInt(a -> a.count(false));

        private static final Comparator<VBox> COMPARATOR_PRODUCT = (a, b) -> {
            int aCount = a.count(false);
            int bCount = b.count(false);
            int aVolume = a.volume(false);
            int bVolume = b.volume(false);

            if (aCount == bCount) {
                return aVolume - bVolume;
            }

            return Long.compare((long) aCount * aVolume, (long) bCount * bVolume);
        };
    }
}
