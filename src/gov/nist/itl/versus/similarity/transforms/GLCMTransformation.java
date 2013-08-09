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


package gov.nist.itl.versus.similarity.transforms;

import java.util.HashSet;
import java.util.Set;

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;


import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAndMaskAdapter;
import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;

/**
 * 
 * Implements a Grey Level Co-Occurrence Matrix Transformation from the NIST
 * Big-Data-Project for an gov.nist.itl.versus.similarity.adapter
 * 
 * @author Cynthia Gan (cng1)
 * 
 */

public class GLCMTransformation implements Transformations {

	Adapter img;

	// Hard-coded options for the GLCM Transform
	static int DX = 1, DY = 1;
	static boolean SYMMETRIC = false, NORMALIZE = true;
	static int LEVELS = 10;

	/**
	 * constructor of an empty instance of GLCMTransformation
	 */
	public GLCMTransformation() {
	}

	/**
	 * constructor that loads the parameter img as the image to be transformed
	 * 
	 * @param img
	 *            Adapter to be transformed into la Grey Level Co-occurance
	 *            Matrix
	 */
	public GLCMTransformation(Adapter img) {
		this.img = img;
	}

	public void load(Adapter img) {
		this.img = img;
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
	 * returns a string with the name of this class
	 */
	@Override
	public String getName() {
		return "A Grey Level Co-Occurrence Matrix transformer, unsymmetric, normalized,"
				+ "searching for pixels in the (1, 1) direction, scaled to 10 image levels.";
	}

	@Override
	public Adapter transform() {
		double[][][] result = null;
		// a medium to turn the double array into an ImageObjectAdapter, 
		// then an Adapter
		ImageObject temp = null;

		if (img instanceof HasRGBPixels) {
			HasRGBPixels pixelImg = (HasRGBPixels) img;
			double[][][] pixelDoubles = pixelImg.getRGBPixels();
			int width, height, bands;
			width = pixelImg.getWidth();
			height = pixelImg.getHeight();
			bands = pixelImg.getNumBands();
			//get minimum and maximum intensities of the image
			double min = pixelImg.getMinimumPixel(), max = pixelImg
					.getMaximumPixel();
			double pixels[][] = new double[width][height];
			double bandResult[][] = new double[LEVELS][LEVELS];
			result = new double[LEVELS][LEVELS][bands];

			for (int i = 0; i < bands; i++) {
				// copy one band of the image into a temporary array
				for (int x = 0; x < height; x++)
					for (int y = 0; y < width; y++)
						pixels[x][y] = pixelDoubles[x][y][i];
				
				// calculate the glcm of the band
				GLCM transformer = GLCM.createGLCM(pixels, DX, DY, LEVELS,
						SYMMETRIC, NORMALIZE, min, max);
				bandResult = transformer.computeGLCM();
				
				// copy the glcm into the right band of the result array
				result = copyIntoResult(result, bandResult, i);
			}

			//Create an ImageObject to create an ImageObjectAdapter
			try {
				temp = ImageObject.createImage(LEVELS, LEVELS, bands, "DOUBLE");
			} catch (ImageException e) {
				System.out.println("Error creating image");
				e.printStackTrace();
			}

			//Copy all the pixels over
			int index = 0;
			for (int y = 0; y < LEVELS; y++) {
				for (int x = 0; x < LEVELS; x++) {
					for (int i = 0; i < bands; i++) {
						temp.set(index, (float) result[y][x][i]);
						index++;
					}
				}
			}

		}

		//Create and return gov.nist.itl.versus.similarity.adapter
		return new ImageObjectAdapter(temp);
	}

	/**
	 * copies the glcm for one band of an image and copies it into one band for
	 * the result image
	 * 
	 * @param result
	 *            the result image, each band is a glcm for an input image band
	 * @param arr
	 *            the glcm for one input image band
	 * @param band
	 *            which input image band the glcm is for
	 * @return the result image, the collection of co-occurrence matrices with
	 *         the addition of the matrix just passed in
	 */
	private double[][][] copyIntoResult(double[][][] result, double[][] arr,
			int band) {
		int size = arr.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				result[x][y][band] = arr[x][y];
			}
		}
		return result;
	}

}
