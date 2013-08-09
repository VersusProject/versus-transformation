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


package gov.nist.itl.versus.similarity.converters;

import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;
import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import registration3d.Fast_FourierTransform;
import registration3d.Fast_FourierTransform.FloatArray2D;
import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;

/**
 * 
 * A class of static methods that convert Adapters into other image classes (e.g. ImageObject from im2learn)
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class AdapterConverter {

	/* A list of the acceptable String values for imageObject Image type:
	 * { "BYTE", "SHORT", "USHORT", "INT", "LONG", "FLOAT", "DOUBLE", "UNKNOWN" }
	 */
	/**
	 * Converts an adapter object into an ImageObject from im2learn
	 * @return an ImageObject that has the same pixel values as the adapter
	 * @throws ImageException
	 */
	public static ImageObject adapterToImageObject(Adapter image) {
		ImageObject result = null;
		if (image instanceof ImageObjectAdapter) {
			result = ((ImageObjectAdapter) image).getImageObject();
		} else if (image instanceof HasRGBPixels) {
			HasRGBPixels temp = (HasRGBPixels) image;
			int width, height, bands, bits;
			width = temp.getWidth();
			height = temp.getHeight();
			bands = temp.getNumBands();
			bits = temp.getBitsPerPixel();
			String type = null;
			
			// create image with the right bit depth
			switch (bits){
				case 64:
					type = "DOUBLE"; break;
				case 32:
					type = "FLOAT"; break;
				case 24: 
					type = "INT"; break;
				case 16:
					type = "SHORT"; break;
				case 8:
					type = "BYTE"; break;
				default:
					type = "DOUBLE"; break;
			}
			
			try {
				result = ImageObject
						.createImage(height, width, bands, type);
			} catch (ImageException e) {
				System.out.println("Error creating image");
				e.printStackTrace();
			}
			
			// Copy all the pixels over
			int index = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					for (int i = 0; i < bands; i++) {			
						result.set(index, temp.getRGBPixel(y, x, i));
						index++;
					}
				}
			}
		}
		return result;
	}

	

	/**
	 * Converts an adapter object into an FloatArray2D Object from 
	 * the Fast_FourierTransform class of im2lib from Fiji
	 * @return a FloatArray2D with the same pixels as the adapter if the adapter was greyscale, or a
	 * 				greyscale version of the image if the adapter was color
	 * @throws IncompatibleBitDepthException
	 */
	private static Fast_FourierTransform ft = new Fast_FourierTransform();
	public static FloatArray2D adapterToFloatArray2D(HasRGBPixels image)
			throws IncompatibleBitDepthException {
		FloatArray2D floatArr;
		double[][][] pixelArray3D = image.getRGBPixels();
		int numRows = pixelArray3D.length;
		int numCols = pixelArray3D[0].length;
		int count = 0;

		//check for bit depth so each pixel is casted into the appropriate type
		if (image.getBitsPerPixel() == 8) {
			floatArr = ft.new FloatArray2D(image.getWidth(), image.getHeight());
			for (int row = 0; row < numRows; row++)
				for (int col = 0; col < numCols; col++)
					floatArr.data[count++] = ((byte) pixelArray3D[row][col][0]) & 0xff;

		} else if (image.getBitsPerPixel() == 16) {
			floatArr = ft.new FloatArray2D(image.getWidth(), image.getHeight());
			for (int row = 0; row < numRows; row++)
				for (int col = 0; col < numCols; col++)
					floatArr.data[count++] = ((short) pixelArray3D[row][col][0]) & 0xffff;

		} else if (image.getNumBands() == 1 && image.getBitsPerPixel() == 32) {
			floatArr = ft.new FloatArray2D(image.getWidth(), image.getHeight());
			for (int row = 0; row < numRows; row++)
				for (int col = 0; col < numCols; col++)
					floatArr.data[count++] = (float) pixelArray3D[row][col][0];

		} else if (image.getNumBands() == 3 && image.getBitsPerPixel() == 24) {
			floatArr = ft.new FloatArray2D(image.getWidth(), image.getHeight());
			for (int row = 0; row < numRows; row++)
				for (int col = 0; col < numCols; col++) {
					double r = pixelArray3D[row][col][0];
					double g = pixelArray3D[row][col][1];
					double b = pixelArray3D[row][col][2];

					floatArr.data[count++] = (float) ((r + g + b) / 3.0f);
				}
		} else {
			throw new IncompatibleBitDepthException();
		}

		return floatArr;
	}
	

	/**
	 * Converts a FloatArray object into an ImagePlus Object from im2lib from Fiji
	 * @return an ImagePlus containing a FloatProcessor with the FloatArray passed 
	 * 			into it, with name name. 
	 * 			min and max default seems to be 0 and 0. 
	 */
	public static ImagePlus floatArrayToImagePlus(FloatArray2D image, String name,
			float min, float max) {
		ImagePlus imp = IJ.createImage(name, "32-Bit Black", image.width,
				image.height, 1);
		FloatProcessor ip = (FloatProcessor) imp.getProcessor();
		floatArrayToFloatProcessor(ip, image);

		if (min == max)
			imp.getProcessor().resetMinAndMax();
		else
			imp.getProcessor().setMinAndMax(min, max);

		return imp;
	}

	/**
	 * Used to convert a FloatArray to a FloatProcessor to insert into an ImagePlus object
	 * @param ip		the ImageProcessor or FloatProcessor the FloatArray will be loaded into 
	 * @param pixels	the FloatArray that will be loaded into the ImageProcessor
	 */
	private static void floatArrayToFloatProcessor(ImageProcessor ip,
			FloatArray2D pixels) {
		float[] img = new float[pixels.width * pixels.height];

		int count = 0;
		for (int y = 0; y < pixels.height; y++)
			for (int x = 0; x < pixels.width; x++)
				img[count] = pixels.data[count++];

		ip.setPixels(img);
		ip.resetMinAndMax();
	}
	
	
	/**
	 * 
	 * takes a 3D image pixel array and returns a 1D image pixel array containing the same pixels in grayscale
	 * @returns 1D version of the pixel array of the adapter passed into it
	 * 
	 */
	public static int[] AdaptertoFlatIntArr(Adapter image){
		int[] flatArr = null;
		if (image instanceof HasRGBPixels) {
			HasRGBPixels img = (HasRGBPixels) image;
			double[][][] adapPixels = img.getRGBPixels();
			int width, height, bands, index = 0;
			width = adapPixels.length;
			height = adapPixels[0].length;
			bands = adapPixels[0][0].length;
			flatArr = new int[width * height];
			
			for(int i = 0; i < width; i++)
				for (int j = 0; j < height; j++) {
					// conversion into greyscale is just the average intensity across all bands
					int greyscale = 0;
					for (int k = 0; k < bands; k++) 
						greyscale += (int) adapPixels[i][j][k];
					flatArr[index++] = greyscale / bands;
				}
		}
		return flatArr;
	}
	
}
