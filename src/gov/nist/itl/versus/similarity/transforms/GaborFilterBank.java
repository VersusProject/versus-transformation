package gov.nist.itl.versus.similarity.transforms; 

/**
 * Copied from im2Learn into Versus-transformations for the Gabor Transformation
 */

import gov.nist.itl.versus.similarity.transforms.FourierTransform;
import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;

/**
 * <B> The class GaborFilterBank provides a tool for image filtering and texture
 * feature detection using a theoretical model of Gabor filters. </B> <BR>
 * <BR>
 * <B>Description:</B> Due to our lack of texture understanding, Gabor filter
 * extracted features are one out of many other texture representations, such
 * as, co-ocurrence matrix, Gauss-Markov random fields or wavelet
 * representations. Gabor filter features represent spatial and frequency
 * (repetition) characteristics of 2D image textures that can be used for
 * texture discrimination and object recognition purposes. The original Gabor
 * filter design is described in the paper: Gabor D., "Theory of Communication,"
 * Journal of IEE, 93, 429-457, 1946. The implementation of this tool is based
 * on the paper: <BR>
 * Palm, C; Keysers, D; Lehmann, TM; Spitzer, K: Gabor Filtering of Complex
 * Hue/Saturation Images for Color Texture Classification, In: Wang, PP (ed.):
 * Procs. 5th Joint Conference on Information Science (JCIS) 2, The Association
 * for Intelligent Machinery, Atlantic City, NJ, 45-49, 2000, URL:
 * http://www.fz-juelich.de/ime/datapool/Palm/JCIS_2000-45-49.pdf. <BR>
 * <BR>
 * 
 * <p>
 * <B>Setup:</B> First, an image file is loaded in the main frame window and
 * the GaborFilter dialog is invoked from the "Feature" drop-down menu of the
 * main window. The GaborFilter dialog is shown below. <BR>
 * <BR>
 * <img src="../../../../../../images/imGaborFilterDialog.jpg" width="580"
 * height="220"> <BR>
 * Gabor Filter dialog.
 * 
 * <BR>
 * <BR>
 * <p>
 * Next, a user has to specify the number of frequencies, orientations and
 * angular offset before obtaining Gabor features. An example of a setup and a
 * run is provided below. <BR>
 * <B>1)</B> Specify the number of frequencies in the "Frequencies" text field.
 * This must be a positive integer (recommended 4 or 5). <BR>
 * <B>2)</B> Specify the number of orientations in the "Orientations" text
 * field. This must be an integer greater than or equal to 3 (recommended 5-8).
 * <BR>
 * <B>3)</B> Specify the angle offset in radians in the "Offset" text field.
 * This must be a decimal value (recommended 0). <BR>
 * <B>4a)</B> Depending on the number of image bands (grayscale with one band
 * or color with three bands), select a feature type in the "FeatureType" combo
 * box. <BR>
 * <B>4b)</B> Press the "Get Features" button. The computation might take some
 * time. After completing the computation, a user will be presented with a list
 * of numberical feature values in one window and an image of the used filter
 * bank in another window. <BR>
 * <B>4c)</B> To apply a single filter to the image and see the result, specify
 * the frequency number for single filter, specify freq num and orientation also
 * select feature type then press the "Show" button to display the chosen
 * results. <BR>
 * <B>5)</B> Optional: Press the "Save" button to save resulting images to
 * individual files. <BR>
 * 
 * <BR>
 * <B>INPUT FIELDS</B> <BR>
 * <B>Frequencies:</B> Number of central frequencies (R) to use in filtering
 * the image. This must be a positive integer (recommended 4 or 5). The filter
 * central frequencies will range from sqrt(2) to 2^(R-1)*sqrt(2). <BR>
 * <B>Orientations:</B> Number of orientations of filters to use. This must be
 * an integer greater than or equal to 3 (recommended 5-8). The filters will be
 * oriented like the faces of a regular polygon with this many sides. <BR>
 * <B>Offset:</B> Angular counter-clockwise rotation of the filter orientations
 * in radians (recommended 0). If offset=0 the 0th orientation filter is the
 * horizontal bottom face of the filter bank polygon. <BR>
 * <B>Features:</B> Selection of which feature type to extract with "Get
 * Features" <BR>
 * <p>
 * <U>Grayscale</U>: Find grayscale features in a single-band image. <BR>
 * <U>R, G, B</U>: Find features for the one chosen band, only works for 3-band
 * (preferably RGB) images. <BR>
 * <U>RGB</U>: Find features for all 3 bands in an RGB image. <BR>
 * <U>Opponent</U>: Find opponent features in a 3-band RGB image, these are RG,
 * RB, and GB combined features for same frequency, and RG', RB', GR', GB', BR',
 * and BG' where ' indicates half (previous) frequency. <BR>
 * <U>HS Complex</U>: Finds complex Hue/Saturation features in a 3-band RGB
 * image. <BR>
 * </p>
 * When using "Show" button:
 * <p>
 * <U>Grayscale</U>: Filters all bands and shows image with same number of
 * bands as input. <BR>
 * <U>R, G, B</U>: Filters only the 1st, 2nd or 3rd band in an image that has
 * at least that many bands, and shows only a single-band image. <BR>
 * <U>RGB</U>: Same as grayscale, filters all bands in an image. <BR>
 * <U>Opponent</U>: Same as grayscale, filters all bands in an image. <BR>
 * <U>HS Complex</U>: Converts a 3-band RGB image to HS-complex space, and
 * displayes the filtered converted image. <BR>
 * </p>
 * 
 * <BR>
 * <B>Frequency:</B> Number of the frequency to use in single scan(show).
 * Ranges from 0 to (number of frequencies - 1). <BR>
 * <B>Orientation:</B> Number of the orientation to use in a single scan
 * (show). Ranges from 0 to (number of orientations - 1). <BR>
 * 
 * <BR>
 * <B>BUTTONS</B> <BR>
 * <B>Get Features:</B> Filters the input image with numerous (# of frequencies
 * X # of orientations) filters and calculates feature values from these
 * filterings. The feature values are displayed in a dialog box.<BR>
 * The feature values are displayed in sections of increasing frequency.<BR>
 * Within each frequency's section they are ordered in increasing orientation
 * starting at 0.<BR>
 * In case of RGB features red is shown first followed by green and blue.<BR>
 * In case of opponent features, the current band and current-previous band
 * opponents are shown. The order is RG, RB, GB for same band, and RG, RB, GR,
 * GB, BR, BG for current-previous band.<BR>
 * Also shows the filter bank (image of all filters) used in this process. <BR>
 * <B>Show:</B> Shows 2 images. One is the filter with the specified
 * parameters, the other is the image resulting from filtering the input image
 * with this filter. <BR>
 * <B>Save:</B> Opens a save file dialog box for each image created with this
 * tool You can then save each image to a different file, or press cancel to not
 * save a given image. <BR>
 * <B>Done:</B> Closes the GaborFilter dialog box, as well as all images
 * created while using it. <BR>
 * 
 * </p>
 * 
 * </p>
 * Release notes: </B> <BR>
 * 
 * @author Martin Urban
 * @version 1.0
 * 
 */

public class GaborFilterBank {
    public static int   FEATURE_GRAY      = 0;
    public static int   FEATURE_RED       = 1;
    public static int   FEATURE_GREEN     = 2;
    public static int   FEATURE_BLUE      = 3;
    public static int   FEATURE_RGB       = 4;
    public static int   FEATURE_OPPONENT  = 5;
    public static int   FEATURE_HSCOMPLEX = 6;

    private ImageObject _filter           = null;
    private int         _rows             = 256;
    private int         _cols             = 256;
    private int         _numFreqs         = 1;
    private int         _numOrientations  = 3;
    private double      _offset           = 0;
    private int         _featureType      = FEATURE_GRAY;

    /**
     * Constructor
     * 
     * @param rows
     *            Number of rows in filter
     * @param cols
     *            Number of columns in filter
     * @param numOrientations
     *            Number of filter orientations in filter bank
     * @param numFrequencies
     *            Number of filter central frequencies in filter bank
     * @param offset
     * @param featureType
     *            Type of features to extract
     */
    public GaborFilterBank(int rows, int cols, int numOrientations, int numFrequencies, double offset, int featureType) {
        _rows = rows;
        _cols = cols;
        _numOrientations = numOrientations;
        _numFreqs = numFrequencies;
        _offset = offset;
        _featureType = featureType;
    }

    public GaborFilterBank() {

    }

    // setters and getters
    public ImageObject getFilter() {
        return _filter;
    }

    public void setRows(int rows) {
        _rows = rows;
    }

    public int getRows() {
        return _rows;
    }

    public void setCols(int cols) {
        _cols = cols;
    }

    public int getCols() {
        return _cols;
    }

    public void setNumFrequencies(int numFrequencies) {
        _numFreqs = numFrequencies;
    }

    public int getNumFrequencies() {
        return _numFreqs;
    }

    public void setNumOrientations(int omega) {
        _numOrientations = omega;
    }

    public int getNumOrientations() {
        return _numOrientations;
    }

    public void setOffset(double offset) {
        _offset = offset;
    }

    public double getOffset() {
        return _offset;
    }

    public void setFeatureType(int type) {
        _featureType = type;
    }

    public int getFeatureType() {
        return _featureType;
    }

    public void resetVals() {
        _filter = null;
        _numFreqs = 1;
        _numOrientations = 3;
        _rows = 256;
        _cols = 256;
        _featureType = FEATURE_GRAY;
        _offset = 0;
    }

    /**
     * Creates a spatial domain version of filter with parameters set with
     * constructor or setters
     * 
     * @param frequency
     *            double Central frequency of this filter
     * @param angle
     *            double Orientation angle in radians of this filter
     * @param sampleRate
     *            double Sampling rate of pixels of this filter
     */
    public void makeSpatialFilter(double frequency, double angle, double sampleRate) throws ImageException {
        double B = 1;
        double Omega = 2 * Math.PI / _numOrientations;
        double[] sl = findSigmaLambda(frequency, B, Omega);
        makeSpatialFilter(sl[0], sl[1], frequency, angle, sampleRate);
    }

    /**
     * creates spatial domain version of filter
     * 
     * @param sigma
     *            Variance of gaussian
     * @param lambda
     *            Aspect ratio
     * @param frequency
     *            double Central frequency of this filter
     * @param angle
     *            double Orientation angle in radians of this filter
     * @param sampleRate
     *            double Sampling rate of pixels of this filter
     */
    private void makeSpatialFilter(double sigma, double lambda, double frequency, double angle, double sampleRate) throws ImageException {
        int rowsHalf = _rows / 2;
        int colsHalf = _cols / 2;

        _filter = ImageObject.createImage(_rows, _cols, 2, "DOUBLE");

        double cosPhi = Math.cos(angle);
        double sinPhi = Math.sin(angle);
        double lambda2 = lambda * lambda;
        double sigma2 = sigma * sigma;

        int i, j;
        double x, y;
        double x1, y1;
        double A, B, C;

        A = 1 / (2 * Math.PI * sigma2 * lambda);

        for (i = 0; i < _rows; i++) {
            x = (i - rowsHalf) / sampleRate;
            for (j = 0; j < _cols; j++) {
                y = (j - colsHalf) / sampleRate;
                x1 = x * cosPhi + y * sinPhi;
                y1 = -x * sinPhi + y * cosPhi;
                B = Math.exp(-(x1 * x1 / (lambda2) + y1 * y1) / (2 * sigma2));
                C = 2 * Math.PI * frequency * x1;

                _filter.set(2 * (i * _cols + j), A * B * Math.cos(C));
                _filter.set(2 * (i * _cols + j) + 1, A * B * Math.sin(C));
            }
        }
    }

    /**
     * Creates a frequency domain version of filter with parameters set with
     * constructor or setters
     * 
     * @param frequency
     *            double Central frequency of this filter
     * @param angle
     *            double Orientation angle in radians of this filter
     * @param sampleRate
     *            double Sampling rate of pixels of this filter
     */
    public void makeFrequencyFilter(double frequency, double angle, double sampleRate) throws ImageException {
        double B = 1;
        double Omega = 2 * Math.PI / _numOrientations;

        double[] sl = findSigmaLambda(frequency, B, Omega);

        makeFrequencyFilter(sl[0], sl[1], frequency, angle, sampleRate);
    }

    /**
     * creates frequency domain version of filter
     * 
     * @param sigma
     *            Variance of gaussian
     * @param lambda
     *            Aspect ratio
     * @param frequency
     *            double Central frequency of this filter
     * @param angle
     *            double Orientation angle in radians of this filter
     * @param sampleRate
     *            double Sampling rate of pixels of this filter
     */
    private void makeFrequencyFilter(double sigma, double lambda, double frequency, double angle, double sampleRate) throws ImageException {
        int rowsHalf = _rows / 2;
        int colsHalf = _cols / 2;

        _filter = ImageObject.createImage(_rows, _cols, 1, "DOUBLE");

        double cosPhi = Math.cos(angle);
        double sinPhi = Math.sin(angle);
        double lambda2 = lambda * lambda;

        int i, j;
        double u, v;
        double u1, v1;
        double A = -2 * Math.PI * Math.PI * sigma * sigma;

        for (i = 0; i < _rows; i++) {
            u = (i - rowsHalf) / sampleRate;
            for (j = 0; j < _cols; j++) {
                v = (j - colsHalf) / sampleRate;
                u1 = u * cosPhi + v * sinPhi;
                v1 = -u * sinPhi + v * cosPhi;
                _filter.set(i * _cols + j, Math.exp(A * ((u1 - frequency) * (u1 - frequency) * lambda2 + v1 * v1)));
            }
        }
    }

    /**
     * Finds B (half-peak bandwidth) as on page 3 of paper
     * 
     * @param F
     *            Center frequency
     * @param sigma
     *            Variance of gaussian
     * @param lambda
     *            Aspect ratio
     * @return Half-peak bandwidth
     */
    private double findB(double F, double sigma, double lambda) {
        double c1, c2;

        c1 = Math.PI * F * sigma * lambda;
        c2 = Math.sqrt(Math.log(2) / 2);

        return Math.log((c1 + c2) / (c1 - c2)) / Math.log(2);
    }

    /**
     * Finds Omega (orientation bandwidth) as on page 3 of paper
     * 
     * @param sigma
     *            Variance of gaussian
     * @param lambda
     *            Aspect ratio
     * @return Orientation bandwidth
     */
    private double findOmega(double sigma, double lambda) {
        return 2 * Math.atan(Math.sqrt(Math.log(2) / 2) / (Math.PI * sigma * lambda));
    }

    /**
     * Finds sigma and lambda from F, B, Omega using equations on page 3 of
     * paper
     * 
     * @param F
     *            Center frequency
     * @param B
     *            Half-peak bandwidth in octaves, (usually 1)
     * @param Omega
     *            Orientation Bandwidth (2*pi/ #orientations)
     * @return Array [sigma lambda]
     */
    private double[] findSigmaLambda(double F, double B, double Omega) {
        double c1 = Math.pow(2, B);
        double c2 = Math.sqrt(Math.log(2) / 2);
        double sigma = c2 / (Math.tan(0.5 * Omega) * Math.PI * F);
        double lambda = (c2 * (c1 + 1) / (c1 - 1)) / (Math.PI * F * sigma);

        return new double[] { sigma, lambda };
    }

    /**
     * Converts an RGB image into complex type HS image
     * 
     * @param imageRGB
     *            Image with 3 bands: Red, Green, Blue
     * @return Image with 2 bands: real, imaginary values of S*exp(H)
     */
    public ImageObject RGB2ComplexHS(ImageObject imageRGB) throws ImageException {
        if (imageRGB.getNumBands() != 3) {
            System.out.println("ERROR: Input image must have 3 bands (RGB)");
            return null;
        }

        if (!imageRGB.getTypeString().equalsIgnoreCase("BYTE")) {
            System.out.println("ERROR: Input image must be of type BYTE");
            return null;
        }

        /*
         * ColorModels cm = new ColorModels(); ImageObject imageHSV = new
         * ImageObject(); cm.ConvertRGB2HSVOut(imageRGB, imageHSV);
         */
        // in outputImage 1st band is real, 2nd band is imaginary
        ImageObject outputImage = ImageObject.createImage(imageRGB.getNumRows(), imageRGB.getNumCols(), 2, "DOUBLE");
        int i;
        int R, G, B;
        double H, S, V;
        double delta;

        int size = imageRGB.getNumRows() * imageRGB.getNumCols();
        for (i = 0; i < size; i++) {
            R = imageRGB.getByte(i * 3) & 0xff;
            G = imageRGB.getByte(i * 3 + 1) & 0xff;
            B = imageRGB.getByte(i * 3 + 2) & 0xff;

            // method from paper
            /*
             * H = Math.atan2(Math.sqrt(3)*(G-B), R - G + R - B); V = (R + G +
             * B) / 3; S = 1 - Math.min(Math.min(R, G), B) / V;
             */

            // common conversion method
            V = Math.max(Math.max(R, G), B);
            delta = V - Math.min(Math.min(R, G), B);
            if (V == 0)
                S = 0;
            else
                S = delta / V;
            if (S == 0)
                H = 0;
            else if (R == V)
                H = 60.0 * (G - B) / delta;
            else if (G == V)
                H = 120 + 60.0 * (B - R) / delta;
            else
                H = 240 + 60.0 * (R - G) / delta;
            if (H < 0)
                H = H + 360;
            V = V / 255.0;

            // S is magnitude, H is phase
            outputImage.set(i * 2, S * Math.cos(H));
            outputImage.set(i * 2 + 1, S * Math.sin(H));
        }

        return outputImage;
    }

    public double[] discreteDecomposition(ImageObject inputImage, ImageObject filterBank) throws ImageException {
        ImageObject filteredImage;
        double phi;
        double F;
        double sampleRate = 1.0 / (5 * Math.pow(2, _numFreqs - 1) * Math.sqrt(2));
        double[] features = null;

        if (_featureType == FEATURE_GRAY) {
            if (inputImage.getNumBands() != 1)
                System.out.println("ERROR: To extract grayscale features input image must have 1 band");
            else {
                features = new double[_numFreqs * _numOrientations];
                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        // filter the input image
                        filteredImage = filterImage(inputImage);

                        // get desired feature
                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_RED) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract R features input image must have 3 bands");
            else {
                features = new double[_numFreqs * _numOrientations];
                ImageObject RBand = inputImage.extractBand(new int[] { 0 });

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        // filter the input image
                        filteredImage = filterImage(RBand);

                        // get desired feature
                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_GREEN) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract G features input image must have 3 bands");
            else {
                features = new double[_numFreqs * _numOrientations];

                ImageObject GBand = inputImage.extractBand(new int[] { 1 });

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        // filter the input image
                        filteredImage = filterImage(GBand);

                        // get desired feature
                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_BLUE) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract B features input image must have 3 bands");
            else {
                features = new double[_numFreqs * _numOrientations];

                ImageObject BBand = inputImage.extractBand(new int[] { 2 });

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        // filter the input image
                        filteredImage = filterImage(BBand);

                        // get desired feature
                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_RGB) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract RGB features input image must have 3 bands");
            else {
                features = new double[3 * _numFreqs * _numOrientations];

                ImageObject RBand = inputImage.extractBand(new int[] { 0 });
                ImageObject GBand = inputImage.extractBand(new int[] { 1 });
                ImageObject BBand = inputImage.extractBand(new int[] { 2 });

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        filteredImage = filterImage(RBand);
                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        filteredImage = filterImage(GBand);
                        features[_numFreqs * _numOrientations + i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        filteredImage = filterImage(BBand);
                        features[2 * _numFreqs * _numOrientations + i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_OPPONENT) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract opponent features input image must have 3 bands");
            else {
                ImageObject prevImg1 = null;
                ImageObject prevImg2 = null;
                ImageObject prevImg3 = null;

                features = new double[9 * _numFreqs * _numOrientations];
                ImageObject filteredImage2;
                ImageObject filteredImage3;

                ImageObject RBand = inputImage.extractBand(new int[] { 0 });
                ImageObject GBand = inputImage.extractBand(new int[] { 1 });
                ImageObject BBand = inputImage.extractBand(new int[] { 2 });

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        // filter all 3 bands
                        filteredImage = filterImage(RBand);
                        filteredImage2 = filterImage(GBand);
                        filteredImage3 = filterImage(BBand);

                        double[] feat9 = findOpponentFeature(filteredImage, filteredImage2, filteredImage3, prevImg1, prevImg2, prevImg3);

                        for (int k = 0; k < 9; k++)
                            features[k * _numFreqs * _numOrientations + i * _numOrientations + j] = feat9[k];

                        prevImg1 = filteredImage;
                        prevImg2 = filteredImage2;
                        prevImg3 = filteredImage3;

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        } else if (_featureType == FEATURE_HSCOMPLEX) {
            if (inputImage.getNumBands() != 3)
                System.out.println("ERROR: To extract HS features input image must have 3 bands (RGB)");
            else {
                features = new double[_numFreqs * _numOrientations];
                // convert to HS complex
                ImageObject imageHS = RGB2ComplexHS(inputImage);

                // loop over all filters in filter bank
                for (int i = 0; i < _numFreqs; i++) {
                    F = Math.pow(2, i) * Math.sqrt(2) * inputImage.getNumCols();
                    for (int j = 0; j < _numOrientations; j++) {
                        phi = j * 2 * Math.PI / _numOrientations + _offset;

                        // create filter with these parameters
                        makeFrequencyFilter(F, phi, sampleRate);

                        filteredImage = filterImage(imageHS);

                        features[i * _numOrientations + j] = findUnichromeFeature(filteredImage);

                        for (int k = 0; k < filterBank.getSize(); k++)
                            filterBank.set(k, filterBank.getDouble(k) + _filter.getDouble(k));
                    }
                }
            }
        }

        return features;
    }

    /**
     * Filters inputImage with a given filter
     * 
     * @param inputImage
     *            ImageObject
     * @return ImageObject
     */
    public ImageObject filterImage(ImageObject inputImage) throws ImageException {
        // sanity check
        if (inputImage == null) {
            System.out.println("ERROR: Input Image is null");
            return null;
        }
        if (_filter == null) {
            System.out.println("ERROR: Filter is null, create a filter first");
            return null;
        }

        ImageObject img1 = FourierTransform.fft2D(inputImage);

        // do fft shift?
        img1 = FourierTransform.fftShift(img1);

        ImageObject img2 = FourierTransform.multiplyFFTs(img1, _filter, img1.getNumRows() / 2, img1.getNumCols() / 2, _filter.getNumRows() / 2, _filter.getNumCols() / 2);

        // take IFFT2
        ImageObject img3 = FourierTransform.ifft2D(img2);

        return img3;
    }

    /**
     * Input image should be 2 band (real, imaginary) There should be a separate
     * image for each color band of original image
     * 
     * @param inputImage
     *            Image with 2 bands (real, imaginary)
     * @return Feature of this image
     */
    private double findUnichromeFeature(ImageObject inputImage) {
        if (inputImage == null) {
            System.out.println("ERROR: Input Image is null");
            return 0;
        }
        if (inputImage.getNumBands() != 2) {
            System.out.println("ERROR: Input Image must have 2 bands");
            return 0;
        }

        double U = 0;

        // if (inputImage.getTypeString().equalsIgnoreCase("BYTE"))
        for (int i = 0; i < inputImage.getNumRows() * inputImage.getNumCols(); i++)
            U = U + inputImage.getDouble(2 * i) * inputImage.getDouble(2 * i) + inputImage.getDouble(2 * i + 1) * inputImage.getDouble(2 * i + 1);
        /*
         * else if (inputImage.sampType.equalsIgnoreCase("SHORT")) for (int i=0;
         * i<inputImage.numrows*inputImage.numcols; i++) U = U +
         * inputImage.imageShort[2*i]*inputImage.imageShort[2*i]
         * +inputImage.imageShort[2*i + 1]*inputImage.imageShort[2*i + 1]; else
         * if (inputImage.sampType.equalsIgnoreCase("INT")) for (int i=0; i<inputImage.numrows*inputImage.numcols;
         * i++) U = U + inputImage.imageInt[2*i]*inputImage.imageInt[2*i]
         * +inputImage.imageInt[2*i + 1]*inputImage.imageInt[2*i + 1]; else if
         * (inputImage.sampType.equalsIgnoreCase("LONG")) for (int i=0; i<inputImage.numrows*inputImage.numcols;
         * i++) U = U + inputImage.imageLong[2*i]*inputImage.imageLong[2*i]
         * +inputImage.imageLong[2*i + 1]*inputImage.imageLong[2*i + 1]; else if
         * (inputImage.sampType.equalsIgnoreCase("FLOAT")) for (int i=0; i<inputImage.numrows*inputImage.numcols;
         * i++) U = U + inputImage.imageFloat[2*i]*inputImage.imageFloat[2*i]
         * +inputImage.imageFloat[2*i + 1]*inputImage.imageFloat[2*i + 1]; else
         * if (inputImage.sampType.equalsIgnoreCase("DOUBLE")) for (int i=0; i<inputImage.numrows*inputImage.numcols;
         * i++) U = U + inputImage.imageDouble[2*i]*inputImage.imageDouble[2*i]
         * +inputImage.imageDouble[2*i + 1]*inputImage.imageDouble[2*i + 1];
         */
        return U;
    }

    /**
     * Finds 3 opponent features from these images, (input images must be R, G,
     * B bands filtered with same filter)
     * 
     * @param imageR
     *            Filtered Red band of an image, must be 2-band (complex value)
     * @param imageG
     *            Filtered Green band of an image, must be 2-band (complex
     *            value)
     * @param imageB
     *            Filtered Blue band of an image, must be 2-band (complex value)
     * @param prevImageR
     *            Filtered Red band of an image at previous frequency, must be
     *            2-band (complex value)
     * @param prevImageG
     *            Filtered Green band of an image at previous frequency, must be
     *            2-band (complex value)
     * @param prevImageB
     *            Filtered Blue band of an image at previous frequency, must be
     *            2-band (complex value)
     * @return double[] RG, RB, GB opponent features
     */
    private double[] findOpponentFeature(ImageObject imageR, ImageObject imageG, ImageObject imageB, ImageObject prevImageR, ImageObject prevImageG, ImageObject prevImageB) {
        // sanity check
        // check first 3 null, 2nd 3 null
        if ((imageR == null) || (imageG == null) || (imageB == null)) {
            System.out.println("ERROR: some input images are null");
            return null;
        }
        if ((prevImageR == null) || (prevImageG == null) || (prevImageB == null)) {
            if (!(imageR.getTypeString().equalsIgnoreCase(imageG.getTypeString()) && imageR.getTypeString().equalsIgnoreCase(imageB.getTypeString()))) {
                System.out.println("ERROR: All input images must be of the same sample type");
                return null;
            }
            if ((imageR.getNumBands() != 2) || (imageG.getNumBands() != 2) || (imageB.getNumBands() != 2)) {
                System.out.println("ERROR: All input images must have 2 bands");
                return null;
            }
            if ((imageR.getNumCols() != imageG.getNumCols()) || (imageR.getNumCols() != imageB.getNumCols())) {
                System.out.println("ERROR: All input images must have same number of columns");
                return null;
            }
            if ((imageR.getNumRows() != imageG.getNumRows()) || (imageR.getNumRows() != imageB.getNumRows())) {
                System.out.println("ERROR: All input images must have same number of rows");
                return null;
            }
        } else {
            if (!(imageR.getTypeString().equalsIgnoreCase(imageG.getTypeString()) && imageR.getTypeString().equalsIgnoreCase(imageB.getTypeString())
                    && imageR.getTypeString().equalsIgnoreCase(prevImageR.getTypeString()) && imageR.getTypeString().equalsIgnoreCase(prevImageG.getTypeString()) && imageR.getTypeString()
                    .equalsIgnoreCase(prevImageB.getTypeString()))) {
                System.out.println("ERROR: All input images must be of the same sample type");
                return null;
            }
            if ((imageR.getNumBands() != 2) || (imageG.getNumBands() != 2) || (imageB.getNumBands() != 2) || (prevImageR.getNumBands() != 2) || (prevImageG.getNumBands() != 2)
                    || (prevImageB.getNumBands() != 2)) {
                System.out.println("ERROR: All input images must have 2 bands");
                return null;
            }
            if ((imageR.getNumCols() != imageG.getNumCols()) || (imageR.getNumCols() != imageB.getNumCols()) || (imageR.getNumCols() != prevImageR.getNumCols())
                    || (imageR.getNumCols() != prevImageG.getNumCols()) || (imageR.getNumCols() != prevImageB.getNumCols())) {
                System.out.println("ERROR: All input images must have same number of columns");
                return null;
            }
            if ((imageR.getNumRows() != imageG.getNumRows()) || (imageR.getNumRows() != imageB.getNumRows()) || (imageR.getNumRows() != prevImageR.getNumRows())
                    || (imageR.getNumRows() != prevImageG.getNumRows()) || (imageR.getNumRows() != prevImageB.getNumRows())) {
                System.out.println("ERROR: All input images must have same number of rows");
                return null;
            }
        }

        double[] features = new double[9];

        double Ur, Ug, Ub;
        double Grg, Grb, Ggb;
        double R1, R2, G1, G2, B1, B2;

        if (imageR.getTypeString().equalsIgnoreCase("DOUBLE")) {
            Grg = 0;
            Grb = 0;
            Ggb = 0;
            Ur = findUnichromeFeature(imageR);
            Ug = findUnichromeFeature(imageG);
            Ub = findUnichromeFeature(imageB);
            for (int i = 0; i < imageR.getNumRows() * imageR.getNumCols(); i++) {
                R1 = imageR.getDouble(2 * i);
                R2 = imageR.getDouble(2 * i + 1);
                G1 = imageG.getDouble(2 * i);
                G2 = imageG.getDouble(2 * i + 1);
                B1 = imageB.getDouble(2 * i);
                B2 = imageB.getDouble(2 * i + 1);
                // do magnitudes first (choose either first or last, but not
                // both)
                Grg = Grg + Math.sqrt((R1 * R1 + R2 * R2) * (G1 * G1 + G2 * G2));
                Grb = Grb + Math.sqrt((R1 * R1 + R2 * R2) * (B1 * B1 + B2 * B2));
                Ggb = Ggb + Math.sqrt((G1 * G1 + G2 * G2) * (B1 * B1 + B2 * B2));

                // do magnitudes last
                // Urg = Urg + Math.sqrt((R1*G1 - R2*G2)*(R1*G1 - R2*G2) +
                // (R2*G1 + R1*G2)*(R2*G1 + R1*G2));
                // Urb = Urb + Math.sqrt((R1*B1 - R2*B2)*(R1*B1 - R2*B2) +
                // (R2*B1 + R1*B2)*(R2*B1 + R1*B2));
                // Ugb = Ugb + Math.sqrt((G1*B1 - G2*B2)*(G1*B1 - G2*B2) +
                // (G2*B1 + G1*B2)*(G2*B1 + G1*B2));
            }
            Grg = Grg / (Ur * Ug);
            Grb = Grb / (Ur * Ub);
            Ggb = Ggb / (Ug * Ub);

            features[0] = Grg;
            features[1] = Grb;
            features[2] = Ggb;
            features[3] = 0;
            features[4] = 0;
            features[5] = 0;
            features[6] = 0;
            features[7] = 0;
            features[8] = 0;
        }

        // if this isn't the first band previous images should not be null, so
        // get f = 2f' features also
        if ((prevImageR != null) && (prevImageG != null) && (prevImageB != null)) {
            if (imageR.getTypeString().equalsIgnoreCase("DOUBLE")) {
                Grg = 0;
                Grb = 0;
                Ur = findUnichromeFeature(imageR);
                Ug = findUnichromeFeature(prevImageG);
                Ub = findUnichromeFeature(prevImageB);
                for (int i = 0; i < imageR.getNumRows() * imageR.getNumCols(); i++) {
                    R1 = imageR.getDouble(2 * i);
                    R2 = imageR.getDouble(2 * i + 1);
                    G1 = prevImageG.getDouble(2 * i);
                    G2 = prevImageG.getDouble(2 * i + 1);
                    B1 = prevImageB.getDouble(2 * i);
                    B2 = prevImageB.getDouble(2 * i + 1);
                    // do magnitudes first (choose either first or last, but not
                    // both)
                    Grg = Grg + Math.sqrt((R1 * R1 + R2 * R2) * (G1 * G1 + G2 * G2));
                    Grb = Grb + Math.sqrt((R1 * R1 + R2 * R2) * (B1 * B1 + B2 * B2));

                    // do magnitudes last
                    // Grg = Grg + Math.sqrt((R1*G1 - R2*G2)*(R1*G1 - R2*G2) +
                    // (R2*G1 + R1*G2)*(R2*G1 + R1*G2));
                    // Grb = Grb + Math.sqrt((R1*B1 - R2*B2)*(R1*B1 - R2*B2) +
                    // (R2*B1 + R1*B2)*(R2*B1 + R1*B2));
                }
                Grg = Grg / (Ur * Ug);
                Grb = Grb / (Ur * Ub);

                features[3] = Grg;
                features[4] = Grb;
            }
            if (imageG.getTypeString().equalsIgnoreCase("DOUBLE")) {
                Grg = 0;
                Ggb = 0;
                Ur = findUnichromeFeature(prevImageR);
                Ug = findUnichromeFeature(imageG);
                Ub = findUnichromeFeature(prevImageB);
                for (int i = 0; i < imageR.getNumRows() * imageR.getNumCols(); i++) {
                    R1 = prevImageR.getDouble(2 * i);
                    R2 = prevImageR.getDouble(2 * i + 1);
                    G1 = imageG.getDouble(2 * i);
                    G2 = imageG.getDouble(2 * i + 1);
                    B1 = prevImageB.getDouble(2 * i);
                    B2 = prevImageB.getDouble(2 * i + 1);
                    // do magnitudes first (choose either first or last, but not
                    // both)
                    Grg = Grg + Math.sqrt((G1 * G1 + G2 * G2) * (R1 * R1 + R2 * R2));
                    Ggb = Ggb + Math.sqrt((G1 * G1 + G2 * G2) * (B1 * B1 + B2 * B2));

                    // do magnitudes last
                    // Grg = Grg + Math.sqrt((G1*R1 - G2*R2)*(G1*R1 - G2*R2) +
                    // (G2*R1 + G1*R2)*(G2*R1 + G1*R2));
                    // Ggb = Ggb + Math.sqrt((G1*B1 - G2*B2)*(G1*B1 - G2*B2) +
                    // (G2*B1 + G1*B2)*(G2*B1 + G1*B2));
                }
                Grg = Grg / (Ug * Ur);
                Ggb = Ggb / (Ug * Ub);

                features[5] = Grg;
                features[6] = Ggb;
            }
            if (imageB.getTypeString().equalsIgnoreCase("DOUBLE")) {
                Grb = 0;
                Ggb = 0;
                Ur = findUnichromeFeature(prevImageR);
                Ug = findUnichromeFeature(prevImageG);
                Ub = findUnichromeFeature(imageB);
                for (int i = 0; i < imageR.getNumRows() * imageR.getNumCols(); i++) {
                    R1 = prevImageR.getDouble(2 * i);
                    R2 = prevImageR.getDouble(2 * i + 1);
                    G1 = prevImageG.getDouble(2 * i);
                    G2 = prevImageG.getDouble(2 * i + 1);
                    B1 = imageB.getDouble(2 * i);
                    B2 = imageB.getDouble(2 * i + 1);
                    // do magnitudes first (choose either first or last, but not
                    // both)
                    Grb = Grb + Math.sqrt((B1 * B1 + B2 * B2) * (R1 * R1 + R2 * R2));
                    Ggb = Ggb + Math.sqrt((B1 * B1 + B2 * B2) * (G1 * G1 + G2 * G2));

                    // do magnitudes last
                    // Grb = Grb + Math.sqrt((B1*R1 - B2*R2)*(B1*R1 - B2*R2) +
                    // (B2*R1 + B1*R2)*(B2*R1 + B1*R2));
                    // Ggb = Ggb + Math.sqrt((B1*G1 - B2*G2)*(B1*G1 - B2*G2) +
                    // (B2*G1 + B1*G2)*(B2*G1 + B1*G2));
                }
                Grb = Grb / (Ub * Ur);
                Ggb = Ggb / (Ub * Ug);

                features[7] = Grb;
                features[8] = Ggb;
            }

        }

        return features;
    }
}
