package transforms;

/**
 * Copied from im2Learn for the Gabor Transformation
 */

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;

/**
 * 
 * <p>
 * Title: FourierTransform
 * </p>
 * <p>
 * Description: Computes 1D and 2D fast fourier transforms, and convolutions
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: NCSA
 * </p>
 * 
 * @author Martin Urban
 * @version 1.0
 */
public class FourierTransform {

    /**
     * Fast fourier transform routine if the input array length is not a power
     * of two, it will be zero padded The array size is [L][2], where each
     * sample is complex; array[n][0] is the real part, array[n][1] is the
     * imaginary part of sample n.
     * 
     * @param array
     *            Array[L][2] to be FFT'ed
     * @return FFT of this array
     */
    public static double[][] fft(double[][] array) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        // sanity check to make sure it's power of 2, and pad with 0's
        // accordingly
        int nextPow2 = (int) Math.ceil(Math.log(array.length) / Math.log(2));
        padArray(array, (int) Math.pow(2, nextPow2), 0, 0);

        double uReal;
        double uImag;
        double wReal;
        double wImag;
        double tReal;
        double tImag;
        int size = array.length;
        int sizeHalf;
        int ln, k, l, le, le1, j, ip, i;

        ln = (int) (Math.log((double) size) / Math.log(2) + 0.5);
        sizeHalf = size / 2;
        j = 1;
        for (i = 1; i < size; i++) {
            if (i < j) {
                tReal = array[i - 1][0];
                tImag = array[i - 1][1];
                array[i - 1][0] = array[j - 1][0];
                array[i - 1][1] = array[j - 1][1];
                array[j - 1][0] = tReal;
                array[j - 1][1] = tImag;
            }
            k = sizeHalf;
            while (k < j) {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }

        // loops thru stages
        for (l = 1; l <= ln; l++) {
            le = (int) (Math.exp((double) l * Math.log(2)) + 0.5);
            le1 = le / 2;
            uReal = 1.0;
            uImag = 0.0;
            wReal = Math.cos(Math.PI / (double) le1);
            wImag = -Math.sin(Math.PI / (double) le1);
            // loops thru 1/2 twiddle values per stage
            for (j = 1; j <= le1; j++) {
                // loops thru points per 1/2 twiddle
                for (i = j; i <= size; i += le) {
                    ip = i + le1;
                    tReal = array[ip - 1][0] * uReal - uImag * array[ip - 1][1];
                    tImag = array[ip - 1][1] * uReal + uImag * array[ip - 1][0];
                    if (Math.abs(tReal) < 1e-13)
                        tReal = 0;
                    if (Math.abs(tImag) < 1e-13)
                        tImag = 0;

                    array[ip - 1][0] = array[i - 1][0] - tReal;
                    array[ip - 1][1] = array[i - 1][1] - tImag;

                    array[i - 1][0] = array[i - 1][0] + tReal;
                    array[i - 1][1] = array[i - 1][1] + tImag;
                }
                tReal = uReal * wReal - wImag * uImag;
                uImag = wReal * uImag + wImag * uReal;
                uReal = tReal;
            }
        }
        return array;
    }

    /**
     * Inverse Fast Fourier Transform routine, the array length must be a power
     * of two. The array size is [L][2], where each sample is complex;
     * array[n][0] is the real part, array[n][1] is the imaginary part of sample
     * n.
     * 
     * @param array
     *            Array[L][2] to be inverse FFT'ed (L must be power of 2)
     * @return Inverse FFT of this array
     */
    public static double[][] ifft(double[][] array) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        // sanity check to make sure it's power of 2, and pad with 0's
        // accordingly
        int nextPow2 = (int) Math.ceil(Math.log(array.length) / Math.log(2));
        padArray(array, (int) Math.pow(2, nextPow2), 0, 0);

        double uReal;
        double uImag;
        double wReal;
        double wImag;
        double tReal;
        double tImag;
        int size;
        int sizeHalf;
        int ln, k, l, le, le1, j, ip, i;

        size = array.length;
        ln = (int) (Math.log((double) size) / Math.log(2) + 0.5);
        sizeHalf = size / 2;
        j = 1;
        for (i = 1; i < size; i++) {
            if (i < j) {
                tReal = array[i - 1][0];
                tImag = array[i - 1][1];
                array[i - 1][0] = array[j - 1][0];
                array[i - 1][1] = array[j - 1][1];
                array[j - 1][0] = tReal;
                array[j - 1][1] = tImag;
            }
            k = sizeHalf;
            while (k < j) {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }

        // loops thru stages
        for (l = 1; l <= ln; l++) {
            le = (int) (Math.exp((double) l * Math.log(2)) + 0.5);
            le1 = le / 2;
            uReal = 1.0;
            uImag = 0.0;
            wReal = Math.cos(Math.PI / (double) le1);
            wImag = Math.sin(Math.PI / (double) le1);
            // loops thru 1/2 twiddle values per stage
            for (j = 1; j <= le1; j++) {
                // loops thru points per 1/2 twiddle
                for (i = j; i <= size; i += le) {
                    ip = i + le1;
                    tReal = array[ip - 1][0] * uReal - uImag * array[ip - 1][1];
                    tImag = array[ip - 1][1] * uReal + uImag * array[ip - 1][0];
                    if (Math.abs(tReal) < 1e-13)
                        tReal = 0;
                    if (Math.abs(tImag) < 1e-13)
                        tImag = 0;

                    array[ip - 1][0] = array[i - 1][0] - tReal;
                    array[ip - 1][1] = array[i - 1][1] - tImag;

                    array[i - 1][0] = array[i - 1][0] + tReal;
                    array[i - 1][1] = array[i - 1][1] + tImag;
                }
                tReal = uReal * wReal - wImag * uImag;
                uImag = wReal * uImag + wImag * uReal;
                uReal = tReal;
            }
        }
        return array;
    }

    /**
     * Pads an array with given number of entries of the given amplitude
     * 
     * @param array
     *            Array to be padded
     * @param newLength
     *            Length of array after padding (must be greater than old
     *            length)
     * @param padding
     *            Value that the new entries should hold
     * @return New padded array
     */
    public static double[] padArray(double[] array, int newLength, double padding) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        if (newLength <= array.length)
            return array;

        int size = array.length;
        double[] arrayBar = new double[newLength];

        for (int n = 0; n < size; n++)
            arrayBar[n] = array[n];
        for (int n = size; n < newLength; n++)
            arrayBar[n] = padding;
        return arrayBar;
    }

    /**
     * Pad a complex Array with new entries
     * 
     * @param array
     *            Complex array to pad
     * @param newLength
     *            Length of array after padding (must be greater than old
     *            length)
     * @param padReal
     *            Real part of the value to set each new entry to
     * @param padImag
     *            Imaginary part of the value to set each new entry to
     * @return Array padded with the given complex values
     */
    public static double[][] padArray(double[][] array, int newLength, double padReal, double padImag) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        if (newLength <= array.length)
            return array;

        int size = array.length;
        double[][] arrayBar = new double[newLength][2];

        for (int n = 0; n < size; n++) {
            arrayBar[n][0] = array[n][0];
            arrayBar[n][1] = array[n][1];
        }
        for (int n = size; n < newLength; n++) {
            arrayBar[n][0] = padReal;
            arrayBar[n][1] = padImag;
        }
        return arrayBar;
    }

    /**
     * Pads an image with zeros
     * 
     * @param inImage
     *            ImageObject Image to pad
     * @param newCols
     *            int Width of image after padding (must be greater than old
     *            width)
     * @param newRows
     *            int Height of image after padding (must be greater than old
     *            height)
     * @return ImageObject
     */
    public static ImageObject padImageObject(ImageObject inImage, int newCols, int newRows) throws ImageException {
        if (inImage == null) {
            System.out.println("ERROR: Input Image is null");
            return null;
        }

        if (!((newCols > inImage.getNumCols()) || (newRows > inImage.getNumRows())))
            return inImage;

        newCols = Math.max(newCols, inImage.getNumCols());
        newRows = Math.max(newRows, inImage.getNumRows());

        ImageObject outImage = ImageObject.createImage(newRows, newCols, inImage.getNumBands(), inImage.getType());
        int bands = inImage.getNumBands();

        // if (inImage.sampType.equalsIgnoreCase("BYTE")){
        for (int i = 0; i < inImage.getNumRows(); i++) {
            for (int j = 0; j < inImage.getNumCols(); j++) {
                for (int k = 0; k < bands; k++) {
                    outImage.set(bands * (i * newCols + j) + k, inImage.getDouble(bands * (i * inImage.getNumCols() + j) + k));
                }
            }
        }
        /*
         * if (inImage.sampType.equalsIgnoreCase("SHORT")){ for (int i=0; i<inImage.numrows;
         * i++) for (int j=0; j<inImage.numcols; j++) for (int k=0; k<bands;
         * k++) outImage.imageShort[bands*(i*newCols + j) + k] =
         * inImage.imageShort[bands*(i*inImage.numcols + j) + k]; } if
         * (inImage.sampType.equalsIgnoreCase("INT")){ for (int i=0; i<inImage.numrows;
         * i++) for (int j=0; j<inImage.numcols; j++) for (int k=0; k<bands;
         * k++) outImage.imageInt[bands*(i*newCols + j) + k] =
         * inImage.imageInt[bands*(i*inImage.numcols + j) + k]; } if
         * (inImage.sampType.equalsIgnoreCase("LONG")){ for (int i=0; i<inImage.numrows;
         * i++) for (int j=0; j<inImage.numcols; j++) for (int k=0; k<bands;
         * k++) outImage.imageLong[bands*(i*newCols + j) + k] =
         * inImage.imageLong[bands*(i*inImage.numcols + j) + k]; } if
         * (inImage.sampType.equalsIgnoreCase("FLOAT")){ for (int i=0; i<inImage.numrows;
         * i++) for (int j=0; j<inImage.numcols; j++) for (int k=0; k<bands;
         * k++) outImage.imageFloat[bands*(i*newCols + j) + k] =
         * inImage.imageFloat[bands*(i*inImage.numcols + j) + k]; } if
         * (inImage.sampType.equalsIgnoreCase("DOUBLE")){ for (int i=0; i<inImage.numrows;
         * i++) for (int j=0; j<inImage.numcols; j++) for (int k=0; k<bands;
         * k++) outImage.imageDouble[bands*(i*newCols + j) + k] =
         * inImage.imageDouble[bands*(i*inImage.numcols + j) + k]; }
         */
        return outImage;
    }

    /**
     * Turns an array of real doubles into an array of complex doubles;
     * array[x][0] is real, array[x][1] is imaginary
     * 
     * @param array
     *            Array of real double values
     * @return Double array holding complex values
     */
    public static double[][] toComplex(double[] array) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        int size = array.length;
        double[][] complexArray = new double[size][2];
        for (int i = 0; i < size; i++) {
            complexArray[i][0] = array[i];
            complexArray[i][1] = 0;
        }
        return complexArray;
    }

    /**
     * Turns an array of real ints into an array of complex doubles; array[x][0]
     * is real, array[x][1] is imaginary
     * 
     * @param array
     *            Array of real int values
     * @return Double array holding complex values
     */
    public static double[][] toComplex(int[] array) {
        if (array == null) {
            System.out.println("ERROR: Input array is null");
            return null;
        }

        int size = array.length;
        double[][] complexArray = new double[size][2];
        for (int i = 0; i < size; i++) {
            complexArray[i][0] = array[i];
            complexArray[i][1] = 0;
        }
        return complexArray;
    }

    /**
     * Multiplies all entries in passed array by given factor, usefull to scale
     * after taking ifft
     * 
     * @param array
     *            Array to rescale
     * @param factor
     *            Scaling factor, this should be length of array if scaling
     *            after ifft
     * @return Scaled array
     */
    public static double[][] scale(double[][] array, double factor) {
        if (array == null) {
            System.out.println("ERROR: input array is null");
            return null;
        }
        int h = array.length;
        int w = array[0].length;
        double[][] arrayNew = new double[h][w];

        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                arrayNew[i][j] = factor * array[i][j];

        return arrayNew;
    }

    /**
     * Calculates 2D fourier transform of input image
     * 
     * @param image
     *            Image to take 2D fft of size = [h*w][2], where 1st row is
     *            real, 2nd is imaginary
     * @param w
     *            Width of the input image, should be power of 2
     * @param h
     *            height of the input image, should be power of 2
     * @return Fourier transfomr of image
     */
    public static double[][] fft2D(double[][] image, int w, int h) {
        if (!isPowerOf2(w))
            return null;
        if (!isPowerOf2(h))
            return null;
        if (image == null) {
            System.out.println("ERROR: Input image is null");
            return null;
        }

        // 1st band real, 2nd imaginary
        double[][] newImage = new double[w * h][2];
        int i;
        int j;
        double[][] arr;

        // copy old image into new
        for (i = 0; i < h * w; i++) {
            newImage[i][0] = image[i][0];
            newImage[i][1] = image[i][1];
        }

        arr = new double[w][2];
        // loop over rows
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                arr[j][0] = newImage[i * w + j][0];
                arr[j][1] = newImage[i * w + j][1];
            }
            arr = fft(arr);
            for (j = 0; j < w; j++) {
                newImage[(i * w + j)][0] = arr[j][0];
                newImage[(i * w + j)][1] = arr[j][1];
            }
        }

        arr = new double[h][2];
        // loop over columns
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                arr[j][0] = newImage[j * w + i][0];
                arr[j][1] = newImage[j * w + i][1];
            }
            arr = fft(arr);
            for (j = 0; j < h; j++) {
                newImage[j * w + i][0] = arr[j][0];
                newImage[j * w + i][1] = arr[j][1];
            }
        }

        return newImage;
    }

    /**
     * Calculates 2D inverse fourier transform of input image
     * 
     * @param image
     *            Image to take 2D ifft of size = [h*w][2], where 1st row is
     *            real, 2nd is imaginary
     * @param w
     *            Width of the input image, should be power of 2
     * @param h
     *            height of the input image, should be power of 2
     * @return Inverse fourier transform of image
     */
    public static double[][] ifft2D(double[][] image, int w, int h) {
        if (image == null) {
            System.out.println("ERROR: Input image is null");
            return null;
        }

        if (!isPowerOf2(w))
            return null;
        if (!isPowerOf2(h))
            return null;

        // 1st band real, 2nd imaginary
        double[][] newImage = new double[w * h][2];
        int i;
        int j;
        double[][] arr;

        for (i = 0; i < h * w; i++) {
            newImage[i][0] = image[i][0];
            newImage[i][1] = image[i][1];
        }

        arr = new double[w][2];
        // loop over rows
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                arr[j][0] = newImage[i * w + j][0];
                arr[j][1] = newImage[i * w + j][1];
            }
            arr = ifft(arr);
            arr = scale(arr, 1.0 / arr.length);
            for (j = 0; j < w; j++) {
                newImage[(i * w + j)][0] = arr[j][0];
                newImage[(i * w + j)][1] = arr[j][1];
            }
        }

        arr = new double[h][2];
        // loop over columns
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                arr[j][0] = newImage[j * w + i][0];
                arr[j][1] = newImage[j * w + i][1];
            }
            arr = ifft(arr);
            arr = scale(arr, 1.0 / arr.length);
            for (j = 0; j < h; j++) {
                newImage[j * w + i][0] = arr[j][0];
                newImage[j * w + i][1] = arr[j][1];
            }
        }

        return newImage;
    }

    /**
     * Performs a 2D FFT on an Image Object
     * 
     * @param inImage
     *            ImageObject Input 1 (real) or 2(complex) band ImageObject
     * @return 2-band (complex) Image Object that is the resulting FFT
     */
    public static ImageObject fft2D(ImageObject inImage) throws ImageException {
        if (inImage == null) {
            System.out.print("ERROR: Input image of fft2D is null");
            return null;
        }
        // if (!inImage.getType.equalsIgnoreCase("DOUBLE") &&
        // !inImage.sampType.equalsIgnoreCase("BYTE")){
        // System.out.print("ERROR: Input image of fft2D is not type BYTE or
        // DOUBLE");
        // return null;
        // }
        if (inImage.getNumBands() > 2) {
            System.out.print("ERROR: Input image of fft2D has more than 2 bands");
            return null;
        }

        int h = inImage.getNumRows();
        int w = inImage.getNumCols();
        int i;
        int j;

        // 1st band real, 2nd imaginary
        ImageObject outImage = ImageObject.createImage(h, w, 2, "DOUBLE");

        // copy input image into outImage
        // if (inImage.getTypeString().equalsIgnoreCase("BYTE")){
        if (inImage.getNumBands() == 1)
            for (i = 0; i < h * w; i++)
                outImage.set(2 * i, inImage.getDouble(i));
        else if (inImage.getNumBands() == 2)
            for (i = 0; i < h * w; i++) {
                outImage.set(2 * i, inImage.getDouble(2 * i));
                outImage.set(2 * i + 1, inImage.getDouble(2 * i + 1));
            }
        // }
        /*
         * else if (inImage.sampType.equalsIgnoreCase("DOUBLE")){ if
         * (inImage.sampPerPixel == 1) for (i=0; i<h*w; i++)
         * outImage.imageDouble[2 * i] = inImage.imageDouble[i]; else if
         * (inImage.sampPerPixel == 2) for (i=0; i<h*w; i++){
         * outImage.imageDouble[2 * i] = inImage.imageDouble[2 * i];
         * outImage.imageDouble[2 * i + 1] = inImage.imageDouble[2 * i + 1]; } }
         */
        // resize image to be power of 2
        w = (int) Math.pow(2, (int) Math.ceil(Math.log(outImage.getNumCols()) / Math.log(2)));
        h = (int) Math.pow(2, (int) Math.ceil(Math.log(outImage.getNumRows()) / Math.log(2)));
        outImage = padImageObject(outImage, w, h);
        if ((w != outImage.getNumCols()) || (h != outImage.getNumRows())) {
            System.out.print("ERROR: resized image sizes are not a power of 2");
            return null;
        }

        double[][] arr = new double[w][2];
        // loop over rows
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                arr[j][0] = outImage.getDouble((i * w + j) * 2);
                arr[j][1] = outImage.getDouble((i * w + j) * 2 + 1);
            }
            arr = fft(arr);
            for (j = 0; j < w; j++) {
                outImage.set((i * w + j) * 2, arr[j][0]);
                outImage.set((i * w + j) * 2 + 1, arr[j][1]);
            }
        }

        // loop over columns
        arr = new double[h][2];
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                arr[j][0] = outImage.getDouble((j * w + i) * 2);
                arr[j][1] = outImage.getDouble((j * w + i) * 2 + 1);
            }
            arr = fft(arr);
            for (j = 0; j < h; j++) {
                outImage.set((j * w + i) * 2, arr[j][0]);
                outImage.set((j * w + i) * 2 + 1, arr[j][1]);
            }
        }

        return outImage;
    }

    /**
     * Calculates 2D inverse fourier transform of input image
     * 
     * @param inImage
     *            Image to take 2D ifft of should be size 2^m * 2^n, with 2
     *            bands (real, imaginary);
     * @return 2-band (complex) Image Object that is the resulting IFFT
     */
    public static ImageObject ifft2D(ImageObject inImage) throws ImageException {
        if (inImage == null) {
            System.out.print("ERROR: Input image of ifft2D is null");
            return null;
        }
        // if (!inImage.sampType.equalsIgnoreCase("DOUBLE") &&
        // !inImage.sampType.equalsIgnoreCase("BYTE")){
        // System.out.print("ERROR: Input image of ifft2D is not type BYTE or
        // DOUBLE");
        // return null;
        // }
        if (inImage.getNumBands() > 2) {
            System.out.print("ERROR: Input image of ifft2D has more than 2 bands");
            return null;
        }

        int h = inImage.getNumRows();
        int w = inImage.getNumCols();
        int i;
        int j;

        // 1st band real, 2nd imaginary
        ImageObject outImage = ImageObject.createImage(h, w, 2, "DOUBLE");

        // copy input image into outImage
        /*
         * if (inImage.sampType.equalsIgnoreCase("BYTE")){ if
         * (inImage.sampPerPixel == 1) for (i=0; i<h*w; i++)
         * outImage.imageDouble[2 * i] = inImage.image[i]; else if
         * (inImage.sampPerPixel == 2) for (i=0; i<h*w; i++){
         * outImage.imageDouble[2 * i] = inImage.image[2 * i];
         * outImage.imageDouble[2 * i + 1] = inImage.image[2 * i + 1]; } }
         */
        // else if (inImage.sampType.equalsIgnoreCase("DOUBLE")){
        if (inImage.getNumBands() == 1)
            for (i = 0; i < h * w; i++)
                outImage.set(2 * i, inImage.getDouble(i));
        else if (inImage.getNumBands() == 2)
            for (i = 0; i < h * w; i++) {
                outImage.set(2 * i, inImage.getDouble(2 * i));
                outImage.set(2 * i + 1, inImage.getDouble(2 * i + 1));
            }
        // }

        // resize image to be power of 2
        w = (int) Math.pow(2, (int) Math.ceil(Math.log(outImage.getNumCols()) / Math.log(2)));
        h = (int) Math.pow(2, (int) Math.ceil(Math.log(outImage.getNumRows()) / Math.log(2)));
        outImage = padImageObject(outImage, w, h);

        double[][] arr;

        arr = new double[w][2];
        // loop over rows
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                arr[j][0] = outImage.getDouble((i * w + j) * 2);
                arr[j][1] = outImage.getDouble((i * w + j) * 2 + 1);
            }
            arr = ifft(arr);
            arr = scale(arr, 1.0 / arr.length);
            for (j = 0; j < w; j++) {
                outImage.set((i * w + j) * 2, arr[j][0]);
                outImage.set((i * w + j) * 2 + 1, arr[j][1]);
            }
        }

        arr = new double[h][2];
        // loop over columns
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                arr[j][0] = outImage.getDouble((j * w + i) * 2);
                arr[j][1] = outImage.getDouble((j * w + i) * 2 + 1);
            }
            arr = ifft(arr);
            arr = scale(arr, 1.0 / arr.length);
            for (j = 0; j < h; j++) {
                outImage.set((j * w + i) * 2, arr[j][0]);
                outImage.set((j * w + i) * 2 + 1, arr[j][1]);
            }
        }

        return outImage;
    }

    public static ImageObject fftShift(ImageObject inImage) throws ImageException {
        if (inImage == null) {
            System.out.println("ERROR: Input Image is null");
            return null;
        }

        ImageObject outImage = ImageObject.createImage(inImage.getNumRows(), inImage.getNumCols(), inImage.getNumBands(), "DOUBLE");

        int rowHalf = inImage.getNumRows() / 2;
        int colHalf = inImage.getNumCols() / 2;

        int index1, index2;
        int i2, j2;

        int bands = inImage.getNumBands();
        for (int i = 0; i < inImage.getNumRows(); i++)
            for (int j = 0; j < inImage.getNumCols(); j++) {
                i2 = (i + rowHalf) % inImage.getNumRows();
                j2 = (j + colHalf) % inImage.getNumCols();
                index1 = bands * (i2 * inImage.getNumCols() + j2);
                index2 = bands * (i * inImage.getNumCols() + j);
                for (int k = 0; k < bands; k++)
                    outImage.set(index1 + k, inImage.getDouble(index2 + k));
            }

        return outImage;
    }

    public static ImageObject ifftShift(ImageObject inImage) throws ImageException {
        if (inImage == null) {
            System.out.println("ERROR: Input Image is null");
            return null;
        }

        ImageObject outImage = ImageObject.createImage(inImage.getNumRows(), inImage.getNumCols(), inImage.getNumBands(), "DOUBLE");

        int rowHalf = inImage.getNumRows() / 2;
        int colHalf = inImage.getNumCols() / 2;

        int index1, index2;
        int i2, j2;

        int bands = inImage.getNumBands();
        for (int i = 0; i < inImage.getNumRows(); i++)
            for (int j = 0; j < inImage.getNumCols(); j++) {
                i2 = (i + rowHalf) % inImage.getNumRows();
                j2 = (j + colHalf) % inImage.getNumCols();
                index1 = bands * (i * inImage.getNumCols() + j);
                index2 = bands * (i2 * inImage.getNumCols() + j2);
                for (int k = 0; k < bands; k++)
                    outImage.set(index1 + k, inImage.getDouble(index2 + k));
            }

        return outImage;
    }

    /**
     * Lines up centers of FFT images and multiplies corresponding frequencies
     * together
     * 
     * @param inImage1
     *            Input image
     * @param inImage2
     *            Other input image
     * @param x1
     *            X coordinate of origin of img
     * @param y1
     *            Y coordinate of origin of img
     * @param x2
     *            X coordinate of origin of filter
     * @param y2
     *            Y coordinate of origin of filter
     * @return Image of size same as inImage1
     */
    public static ImageObject multiplyFFTs(ImageObject inImage1, ImageObject inImage2, int x1, int y1, int x2, int y2) throws ImageException {
        // sanity check
        // include the following
        // check if center is outside of image
        if ((inImage1 == null) || (inImage2 == null)) {
            System.out.println("ERROR: One or both of the input images is/are null");
            return null;
        }
        // if (!(inImage1.sampType.equalsIgnoreCase("DOUBLE")) ||
        // !(inImage1.sampType.equalsIgnoreCase("DOUBLE"))){
        // System.out.println("ERROR: Both images must be type DOUBLE");
        // return null;
        // }

        ImageObject outImage = ImageObject.createImage(inImage1.getNumRows(), inImage1.getNumCols(), 2, "DOUBLE");

        int sizeX1 = inImage1.getNumRows();
        int sizeY1 = inImage1.getNumCols();
        int sizeX2 = inImage2.getNumRows();
        int sizeY2 = inImage2.getNumCols();

        int xFilter;
        int yFilter;

        int xOffset = x2 - x1;
        int yOffset = y2 - y1;

        if ((inImage1.getNumBands() == 1) && (inImage2.getNumBands() == 1))
            for (int i = 0; i < sizeX1; i++)
                for (int j = 0; j < sizeY1; j++) {
                    xFilter = i + xOffset;
                    yFilter = j + yOffset;
                    if ((xFilter >= 0) && (xFilter < sizeX2) && (yFilter >= 0) && (yFilter < sizeY2))
                        outImage.set(2 * (i * sizeY1 + j), inImage1.getDouble(i * sizeY1 + j) * inImage2.getDouble(xFilter * sizeY2 + yFilter));
                    else
                        outImage.set(2 * (i * sizeY1 + j), 0);
                }
        else if ((inImage1.getNumBands() == 2) && (inImage2.getNumBands() == 1))
            for (int i = 0; i < sizeX1; i++)
                for (int j = 0; j < sizeY1; j++) {
                    xFilter = i + xOffset;
                    yFilter = j + yOffset;
                    if ((xFilter >= 0) && (xFilter < sizeX2) && (yFilter >= 0) && (yFilter < sizeY2)) {
                        outImage.set(2 * (i * sizeY1 + j), inImage1.getDouble(2 * (i * sizeY1 + j)) * inImage2.getDouble(xFilter * sizeY2 + yFilter));
                        outImage.set(2 * (i * sizeY1 + j) + 1, inImage1.getDouble(2 * (i * sizeY1 + j) + 1) * inImage2.getDouble(xFilter * sizeY2 + yFilter));
                    } else {
                        outImage.set(2 * (i * sizeY1 + j), 0);
                        outImage.set(2 * (i * sizeY1 + j) + 1, 0);
                    }
                }
        else if ((inImage1.getNumBands() == 1) && (inImage2.getNumBands() == 2))
            for (int i = 0; i < sizeX1; i++)
                for (int j = 0; j < sizeY1; j++) {
                    xFilter = i + xOffset;
                    yFilter = j + yOffset;
                    if ((xFilter >= 0) && (xFilter < sizeX2) && (yFilter >= 0) && (yFilter < sizeY2)) {
                        outImage.set(2 * (i * sizeY1 + j), inImage1.getDouble(i * sizeY1 + j) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter)));
                        outImage.set(2 * (i * sizeY1 + j) + 1, inImage1.getDouble(i * sizeY1 + j) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter) + 1));
                    } else {
                        outImage.set(2 * (i * sizeY1 + j), 0);
                        outImage.set(2 * (i * sizeY1 + j) + 1, 0);
                    }
                }
        else if ((inImage1.getNumBands() == 2) && (inImage2.getNumBands() == 2))
            for (int i = 0; i < sizeX1; i++)
                for (int j = 0; j < sizeY1; j++) {
                    xFilter = i + xOffset;
                    yFilter = j + yOffset;
                    if ((xFilter >= 0) && (xFilter < sizeX2) && (yFilter >= 0) && (yFilter < sizeY2)) {
                        outImage.set(2 * (i * sizeY1 + j), inImage1.getDouble(2 * (i * sizeY1 + j)) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter))
                                - inImage1.getDouble(2 * (i * sizeY1 + j) + 1) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter) + 1));
                        outImage.set(2 * (i * sizeY1 + j) + 1, inImage1.getDouble(2 * (i * sizeY1 + j)) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter) + 1)
                                + inImage1.getDouble(2 * (i * sizeY1 + j) + 1) * inImage2.getDouble(2 * (xFilter * sizeY2 + yFilter)));
                    } else {
                        outImage.set(2 * (i * sizeY1 + j), 0);
                        outImage.set(2 * (i * sizeY1 + j) + 1, 0);
                    }
                }

        return outImage;
    }

    /**
     * Converts real/imaginary values 2-band image into magnitude/phase values
     * 2-band image
     * 
     * @param inImage
     *            Input image
     * @return Modified input image
     */
    public static ImageObject toMagnitudePhase(ImageObject inImage) {
        if (inImage == null) {
            System.out.println("ERROR: Input image is null");
            return inImage;
        }
        if (inImage.getNumBands() != 2) {
            System.out.println("ERROR: Input image must contain 2 bands");
            return inImage;
        }

        double real, imag;

        for (int i = 0; i < inImage.getNumRows() * inImage.getNumCols(); i++) {
            real = inImage.getDouble(2 * i);
            imag = inImage.getDouble(2 * i + 1);
            inImage.set(2 * i, Math.sqrt(real * real + imag * imag));
            inImage.set(2 * i + 1, Math.tan(imag / real));
        }

        return inImage;
    }

    /**
     * Converts magnitude/phase values 2-band image into real/imaginary values
     * 2-band image
     * 
     * @param inImage
     *            Input image
     * @return Modified input image
     */
    public static ImageObject toRealImaginary(ImageObject inImage) {
        if (inImage == null) {
            System.out.println("ERROR: Input image is null");
            return inImage;
        }
        if (inImage.getNumBands() != 2) {
            System.out.println("ERROR: Input image must contain 2 bands");
            return inImage;
        }

        double mag, phase;

        for (int i = 0; i < inImage.getNumRows() * inImage.getNumCols(); i++) {
            mag = inImage.getDouble(2 * i);
            phase = inImage.getDouble(2 * i + 1);
            inImage.set(2 * i, mag * Math.cos(phase));
            inImage.set(2 * i + 1, mag * Math.sin(phase));
        }

        return inImage;
    }

    public static ImageObject RealtoRealImag(ImageObject inImage) throws ImageException {
        if (inImage == null) {
            return null;
        }

        if (inImage.getNumBands() != 1) {
            System.out.println("ERROR: Input image must have exactly 1 band");
            return null;
        }
        // 1st band real, 2nd imaginary
        ImageObject outImage = ImageObject.createImage(inImage.getNumRows(), inImage.getNumCols(), 2, "DOUBLE");

        if (inImage.getNumBands() == 1)
            for (int i = 0; i < inImage.getNumRows() * inImage.getNumCols(); i++)
                outImage.set(2 * i, inImage.getDouble(i));

        return outImage;
    }

    /**
     * Regular 1D convolution of two Real signals, convolves array1 with array2
     * 
     * @param array1
     *            Array of data samples
     * @param array2
     *            Array of filter samples
     * @return Array of convoluted signals of size |array1| + |array2| - 1
     */
    public static double[] convolve(double[] array1, double[] array2) {
        if ((array1 == null) || (array2 == null)) {
            System.out.println("ERROR: One of the input arrays is null");
            return null;
        }

        double sum;
        int size1 = array1.length;
        int size2 = array2.length;

        int size = size1 + size2 - 1;
        double[] y = new double[size];
        // Convolves from beginning until the head of array2 has passed the tail
        // of array1
        for (int n = 0; n < size1; n++) {
            sum = 0;
            for (int m = 0; (m <= n) && (m < size2); m++)
                sum = sum + array2[m] * array1[n - m];
            y[n] = sum;
        }
        // finishes the convolution
        for (int n = size1; n < size; n++) {
            sum = 0;
            for (int m = n - size1 + 1; m < size2; m++)
                sum = sum + array2[m] * array1[n - m];
            y[n] = sum;
        }
        return y;
    }

    /**
     * Convolves inImage with kernel
     * 
     * @param inImage
     *            ImageObject
     * @param kernel
     *            ImageObject
     * @return ImageObject
     */
    public static ImageObject convolve2D(ImageObject inImage, ImageObject kernel) throws ImageException {
        if ((inImage == null) || (kernel == null)) {
            System.out.println("ERROR: One of the input images is null");
            return null;
        }
        // TODO sanity check
        // filter must be 1-band
        // image should be 1-band for now

        int rows1 = inImage.getNumRows();
        int cols1 = inImage.getNumCols();
        int rows2 = kernel.getNumRows();
        int cols2 = kernel.getNumCols();
        int bands = 1;

        if ((inImage.getNumBands() == 2) || (kernel.getNumBands() == 2))
            bands = 2;

        ImageObject outImage = ImageObject.createImage(rows1 + rows2 - 1, cols1 + cols2 - 1, bands, "DOUBLE");

        // for different types
        // fill in with inImage

        for (int i = 0; i < rows1; i++)
            for (int j = 0; j < cols1; j++)
                outImage.set((i + rows2 - 1) * outImage.getNumCols() + j + cols2 - 1, inImage.getDouble(i * inImage.getNumCols() + j));
        /*
         * if (inImage.sampType == "SHORT") for (int i=0; i<rows1; i++) for
         * (int j=0; j<cols1; j++) outImage.imageDouble[(i + rows2 -
         * 1)*outImage.numcols + j + cols2 - 1] =
         * inImage.imageShort[i*inImage.numcols + j]; if (inImage.sampType ==
         * "INT") for (int i=0; i<rows1; i++) for (int j=0; j<cols1; j++)
         * outImage.imageDouble[(i + rows2 - 1)*outImage.numcols + j + cols2 -
         * 1] = inImage.imageInt[i*inImage.numcols + j]; if (inImage.sampType ==
         * "LONG") for (int i=0; i<rows1; i++) for (int j=0; j<cols1; j++)
         * outImage.imageDouble[(i + rows2 - 1)*outImage.numcols + j + cols2 -
         * 1] = inImage.imageLong[i*inImage.numcols + j]; if (inImage.sampType ==
         * "FLOAT") for (int i=0; i<rows1; i++) for (int j=0; j<cols1; j++)
         * outImage.imageDouble[(i + rows2 - 1)*outImage.numcols + j + cols2 -
         * 1] = inImage.imageFloat[i*inImage.numcols + j]; else if
         * (inImage.sampType == "DOUBLE") for (int i=0; i<rows1; i++) for (int
         * j=0; j<cols1; j++) outImage.imageDouble[(i + rows2 -
         * 1)*outImage.numcols + j + cols2 - 1] =
         * inImage.imageDouble[i*inImage.numcols + j];
         */
        double sum = 0;

        for (int i = 0; i < outImage.getNumRows(); i++)
            for (int j = 0; j < outImage.getNumCols(); j++) {
                sum = 0;
                // loop over all things in kernel
                for (int x = 0; x < rows2; x++)
                    for (int y = 0; y < cols2; y++)
                        if ((i + x < outImage.getNumRows()) && (j + y < outImage.getNumCols()))
                            sum = sum + outImage.getDouble((i + x) * outImage.getNumCols() + (j + y)) * kernel.getDouble((rows2 - x - 1) * cols2 + (cols2 - y - 1));
                outImage.set(i * outImage.getNumCols() + j, sum);
            }

        return outImage;
    }

    public static boolean isPowerOf2(int num) {
        float a = num;
        while (a > 1)
            a = a / 2;
        if (a == 1)
            return true;
        return false;
    }
}
