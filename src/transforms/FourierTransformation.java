/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgment if the
 * software is used.
 *
 *
 *    Date: 08-09-2013
 */


package transforms;

import ij.IJ;
import ij.ImagePlus;

import java.util.HashSet;
import java.util.Set;

import converters.AdapterConverter;

import registration3d.Fast_FourierTransform;
import registration3d.Fast_FourierTransform.FloatArray;
import registration3d.Fast_FourierTransform.FloatArray2D;

import adapter.IJImagePlusAdapter;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAndMaskAdapter;
import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;
import exceptions.IncompatibleBitDepthException;

/**
 * 
 * Implements a Fourier Transformation from Fiji for an adapter
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class FourierTransformation implements Transformations {

	Adapter img;
	Fast_FourierTransform ft = new Fast_FourierTransform();

	/**
	 * constructs an empty instance of FourierTransformation
	 */
	public FourierTransformation() {
	}

	/**
	 * constructs an instance of FourierTransformation containing an adapter to
	 * be processed
	 * 
	 * @param adapter
	 *            an adapter containing an image to be processed
	 */
	public FourierTransformation(Adapter adapter) {
		img = adapter;
	}

	/**
	 * loads an instance of FourierTransformation with an adapter to be
	 * processed
	 * 
	 * @param adapter
	 *            an adapter containing an image to be processed
	 */
	public void load(Adapter adapter) {
		img = adapter;
	}

	/**
	 * returns a string with the name of this class
	 */
	@Override
	public String getName() {
		return "A Fourier transformer that implements a windowed fast fourier transform"
				+ " from ImageJ, returning the power as in image on a natural log scale";
	}

	/**
	 * returns a set of supported Adapter classes
	 */
	@Override
	public Set<Class<?>> supportedAdapters() {
		Set<Class<?>> supported = new HashSet<Class<?>>();
		supported.add(IJImagePlusAdapter.class);
		supported.add(ImageObjectAdapter.class);
		supported.add(ImageObjectAndMaskAdapter.class);
		supported.add(BufferedImageAdapter.class);
		return supported;
	}

	/**
	 * This function takes the Fast Fourier Transform of the image in the
	 * adapter pass to it and returns another adapter with the image of the
	 * resulting power spectrum. The phase, while calculated, is not returned.
	 * 
	 * @return the power spectrum of the Fourier transform of the image passed
	 *         to this function
	 * @throws IncompatibleBitDepthException
	 *             if an image storage type is too much bit depth, this Fourier
	 *             Transform from the Fiji Library can't handle it.
	 */
	public Adapter transform() throws IncompatibleBitDepthException {
		// Although the phase component of the Fourier Transform is calculated,
		// nothing is done with it
		ImagePlus fftpower, fftphase;
		IJImagePlusAdapter result;
		float[] power, phase;
		FloatArray powerSpectrum, phaseSpectrum;
		if (img instanceof HasRGBPixels) {
			if (!isCompatibleImageType((HasRGBPixels) img))
				throw new IncompatibleBitDepthException();

			FloatArray image = AdapterConverter
					.adapterToFloatArray2D((HasRGBPixels) img);

			int imgW, imgH;
			imgW = ((FloatArray2D) image).width;
			imgH = ((FloatArray2D) image).height;
			int extW = imgW / 4;
			int extH = imgH / 4;
			// add an even number so that both sides extend equally
			if (extW % 2 != 0)
				extW++;
			if (extH % 2 != 0)
				extH++;
			image = extendImageMirror((FloatArray2D) image, imgW + extW, imgH
					+ extH);

			exponentialWindow((FloatArray2D) image);

			image = zeroPadImage((FloatArray2D) image);

			image = computeFFT((FloatArray2D) image);
			power = computePowerSpectrum(image.data);
			phase = computePhaseSpectrum(image.data);

			powerSpectrum = ft.new FloatArray2D(power,
					((FloatArray2D) image).width / 2,
					((FloatArray2D) image).height);

			phaseSpectrum = ft.new FloatArray2D(phase,
					((FloatArray2D) image).width / 2,
					((FloatArray2D) image).height);
			for (int i = 0; i < powerSpectrum.data.length; i++)
				powerSpectrum.data[i] = (float) Math
						.log(1 + powerSpectrum.data[i]);

			rearrangeFFT((FloatArray2D) powerSpectrum);
			rearrangeFFT((FloatArray2D) phaseSpectrum);

			fftpower = AdapterConverter.floatArrayToImagePlus(
					(FloatArray2D) powerSpectrum, "Power", 0, 0);
			fftphase = AdapterConverter.floatArrayToImagePlus(
					(FloatArray2D) phaseSpectrum, "Phase", 0, 0);

			result = new IJImagePlusAdapter(fftpower);
			return result;
		} else {
			System.out.println("Adapter given does not implement HasPixels");
			return null;
		}
	}

	/* ************************************************************************
	 * ************************************************************************
	 * **************** -------------------------------------- ****************
	 * **************** ----- Code to convert Versus --------- ****************
	 * **************** ----- adapters to the ImagePlus ------ ****************
	 * **************** ----- class via the FloatArray ------- ****************
	 * **************** ----- class and FloatProcessor ------- ****************
	 * **************** ----- class and a function to -------- ****************
	 * **************** ----- check adapter compatibility. --- ****************
	 * **************** -------------------------------------- ****************
	 * ************************************************************************
	 * ************************************************************************
	 */

	private boolean isCompatibleImageType(HasRGBPixels image) {
		boolean verdict = true;
		if (image.getNumBands() == 3 && image.getBitsPerPixel() != 24)
			verdict = false;
		if (image.getNumBands() == 1 && image.getBitsPerPixel() == 64)
			verdict = false;
		return verdict;
	}

	/* ************************************************************************
	 * ************************************************************************
	 * **************** -------------------------------------- ****************
	 * **************** ------ Code pulled from ImageJ ------- ****************
	 * **************** ------ to implement transform() ------ ****************
	 * **************** ------ which goes through the -------- ****************
	 * **************** ------ process of computing a -------- ****************
	 * **************** ------ Fast Fourier Transform. ------- ****************
	 * **************** -------------------------------------- ****************
	 * ************************************************************************
	 * ************************************************************************
	 */
	
	// The majority of this code is contained with the AdapterConverter class as static methods

	private FloatArray2D zeroPadImage(FloatArray2D img) {
		int widthFFT = FftReal.nfftFast(img.width);
		int heightFFT = FftComplex.nfftFast(img.height);

		FloatArray2D result = zeroPad(img, widthFFT, heightFFT);
		img.data = null;
		img = null;

		return result;
	}

	private FloatArray2D zeroPad(FloatArray2D ip, int width, int height) {
		FloatArray2D image = ft.new FloatArray2D(width, height);

		int offsetX = (width - ip.width) / 2;
		int offsetY = (height - ip.height) / 2;

		if (offsetX < 0) {
			System.err
					.println("Fast_FourierTransform.ZeroPad(): Zero-Padding size in X smaller than image! "
							+ width + " < " + ip.width);
			return null;
		}

		if (offsetY < 0) {
			System.err
					.println("Fast_FourierTransform.ZeroPad(): Zero-Padding size in Y smaller than image! "
							+ height + " < " + ip.height);
			return null;
		}

		int count = 0;

		for (int y = 0; y < ip.height; y++)
			for (int x = 0; x < ip.width; x++)
				image.set(ip.data[count++], x + offsetX, y + offsetY);

		return image;
	}

	private FloatArray2D computeFFT(FloatArray2D img) {
		FloatArray2D fft = pffft2D(img, false);
		img.data = null;
		img = null;

		return fft;
	}

	private FloatArray2D pffft2D(FloatArray2D values, boolean scale) {
		int height = values.height;
		int width = values.width;
		int complexWidth = (width / 2 + 1) * 2;

		FloatArray2D result = ft.new FloatArray2D(complexWidth, height);

		// do fft's in x direction
		float[] tempIn = new float[width];
		float[] tempOut;

		FftReal fft = new FftReal(width);

		for (int y = 0; y < height; y++) {
			tempOut = new float[complexWidth];

			for (int x = 0; x < width; x++)
				tempIn[x] = values.get(x, y);

			fft.realToComplex(-1, tempIn, tempOut);

			if (scale)
				fft.scale(width, tempOut);

			for (int x = 0; x < complexWidth; x++)
				result.set(tempOut[x], x, y);
		}

		// do fft's in y-direction on the complex numbers
		tempIn = new float[height * 2];

		FftComplex fftc = new FftComplex(height);

		for (int x = 0; x < complexWidth / 2; x++) {
			tempOut = new float[height * 2];

			for (int y = 0; y < height; y++) {
				tempIn[y * 2] = result.get(x * 2, y);
				tempIn[y * 2 + 1] = result.get(x * 2 + 1, y);
			}

			fftc.complexToComplex(-1, tempIn, tempOut);

			for (int y = 0; y < height; y++) {
				result.set(tempOut[y * 2], x * 2, y);
				result.set(tempOut[y * 2 + 1], x * 2 + 1, y);
			}
		}

		return result;
	}

	private float[] computePhaseSpectrum(float[] complex) {
		int wComplex = complex.length / 2;

		float[] phaseSpectrum = new float[wComplex];
		float a, b;

		for (int pos = 0; pos < phaseSpectrum.length; pos++) {
			a = complex[pos * 2];
			b = complex[pos * 2 + 1];

			if (a != 0.0 || b != 0)
				phaseSpectrum[pos] = (float) Math.atan2(b, a);
			else
				phaseSpectrum[pos] = 0;
		}
		return phaseSpectrum;
	}

	private float[] computePowerSpectrum(float[] complex) {
		int wComplex = complex.length / 2;

		float[] powerSpectrum = new float[wComplex];

		for (int pos = 0; pos < wComplex; pos++)
			powerSpectrum[pos] = (float) Math.sqrt(Math
					.pow(complex[pos * 2], 2)
					+ Math.pow(complex[pos * 2 + 1], 2));

		return powerSpectrum;
	}

	private void rearrangeFFT(FloatArray2D values) {
		float[] fft = values.data;
		int w = values.width;
		int h = values.height;

		int halfDimYRounded = (int) (h / 2);

		float buffer[] = new float[w];
		int pos1, pos2;

		for (int y = 0; y < halfDimYRounded; y++) {
			// copy upper line
			pos1 = y * w;
			for (int x = 0; x < w; x++)
				buffer[x] = fft[pos1++];

			// copy lower line to upper line
			pos1 = y * w;
			pos2 = (y + halfDimYRounded) * w;
			for (int x = 0; x < w; x++)
				fft[pos1++] = fft[pos2++];

			// copy buffer to lower line
			pos1 = (y + halfDimYRounded) * w;
			for (int x = 0; x < w; x++)
				fft[pos1++] = buffer[x];
		}
	}

	private FloatArray2D extendImageMirror(FloatArray2D ip, int width,
			int height) {
		FloatArray2D image = ft.new FloatArray2D(width, height);

		int offsetX = (width - ip.width) / 2;
		int offsetY = (height - ip.height) / 2;

		if (offsetX < 0) {
			IJ.error("Fast_FourierTransform.extendImageMirror(): Extended size in X smaller than image! "
					+ width + " < " + ip.width);
			return null;
		}

		if (offsetY < 0) {
			IJ.error("Fast_FourierTransform.extendImageMirror(): Extended size in Y smaller than image! "
					+ height + " < " + ip.height);
			return null;
		}

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				image.set(ip.getMirror(x - offsetX, y - offsetY), x, y);

		return image;
	}

	private void exponentialWindow(FloatArray2D img) {
		double a = 1000;

		// create lookup table
		double weightsX[] = new double[img.width];
		double weightsY[] = new double[img.height];

		for (int x = 0; x < img.width; x++) {
			double relPos = (double) x / (double) (img.width - 1);

			if (relPos <= 0.5)
				weightsX[x] = 1.0 - (1.0 / (Math.pow(a, (relPos * 2))));
			else
				weightsX[x] = 1.0 - (1.0 / (Math.pow(a, ((1 - relPos) * 2))));
		}

		for (int y = 0; y < img.height; y++) {
			double relPos = (double) y / (double) (img.height - 1);

			if (relPos <= 0.5)
				weightsY[y] = 1.0 - (1.0 / (Math.pow(a, (relPos * 2))));
			else
				weightsY[y] = 1.0 - (1.0 / (Math.pow(a, ((1 - relPos) * 2))));
		}

		for (int y = 0; y < img.height; y++)
			for (int x = 0; x < img.width; x++)
				img.set((float) (img.get(x, y) * weightsX[x] * weightsY[y]), x,
						y);
	}

}
