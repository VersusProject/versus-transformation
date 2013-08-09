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



package adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.FileLoader;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.StreamLoader;
import edu.illinois.ncsa.versus.utility.HasCategory;

/**
 * 
 * Adapter for ImagePlus class from ImageJ 
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class IJImagePlusAdapter implements Adapter, HasRGBPixels, FileLoader,
		StreamLoader, HasCategory {

	/** 8-bit grayscale (unsigned) */
	public static final int GRAY8 = 0;

	/** 16-bit grayscale (unsigned) */
	public static final int GRAY16 = 1;

	/** 32-bit floating-point grayscale */
	public static final int GRAY32 = 2;

	/** 8-bit indexed color */
	public static final int COLOR_256 = 3;

	/** 32-bit RGB color */
	public static final int COLOR_RGB = 4;

	private ImagePlus img = null;

	/**
	 * Wrap an ImageJ ImagePlus object in a Versus adapter
	 * 
	 * @param img
	 */
	public IJImagePlusAdapter(ImagePlus img) {
		this.img = img;
	}

	/**
	 * Create an empty Versus adapter for an IJImageAdapter
	 */
	public IJImagePlusAdapter() {
	}

	/**
	 * Return the wrapped ImageJ ImagePlus image
	 * 
	 * @return wrapped ImageJ ImagePlus image
	 */
	public ImagePlus getIJImage() {
		return img;
	}

	/**
	 * Saves the image with the name and at the path given by the passed string.
	 * 
	 * @param completePath
	 *            the complete and absolute path desired path including the name
	 *            and type of the file.
	 */
	public void saveImage(String completePath) {
		IJ.save(img, completePath);
	}

	@Override
	public double[][][] getRGBPixels() {
		if (img != null) {
			int x = 0, y = 0, type, numRows, numCols, bands;
			type = img.getType();
			numRows = img.getHeight();
			numCols = img.getWidth();
			bands = img.getNChannels();
			double[][][] pixels = new double[numRows][numCols][3];
			if ((type == GRAY8) || (type == GRAY16) || (type == GRAY32)) {
				for (x = 0; x < numRows; x++) {
					for (y = 0; y < numCols; y++) {
						pixels[x][y][0] = getRGBPixel(x, y, 0);
						pixels[x][y][1] = getRGBPixel(x, y, 0);
						pixels[x][y][2] = getRGBPixel(x, y, 0);
					}
				}
			} else {
				for (int i = 0; i < 3; i++) {
					for (x = 0; x < numRows; x++) {
						for (y = 0; y < numCols; y++) {
							pixels[x][y][i] = getRGBPixel(x, y, i);
						}
					}
				}
			}
			return pixels;
		} else
			return null;
	}

	@Override
	public double getRGBPixel(int row, int column, int band) {
		int[] pixel_array = new int[4];
		double pixel = 0;
		if (img != null && inBounds(row, column)) {
			if (img.getType() == GRAY8 || img.getType() == GRAY16
					|| img.getType() == GRAY32) {
				pixel_array = img.getPixel(column, row);
				pixel = (double) pixel_array[0];
			} else if (img.getType() == COLOR_RGB || img.getType() == COLOR_256) {
				pixel_array = img.getPixel(column, row);
				pixel = (double) pixel_array[band];
			}
		}
		return pixel;
	}

	/**
	 * Checks whether a coordinate is valid
	 * 
	 * @param row
	 *            row value of the coordinate
	 * @param col
	 *            column value of the coordinate
	 * @return boolean whether the coordinate is valid (refers to an actual
	 *         pixel in the image)
	 */
	private boolean inBounds(int row, int col) {
		if (row > 0 && row < img.getHeight() && col > 0 && col < img.getWidth())
			return true;
		else
			return false;
	}

	@Override
	public int getWidth() {
		int width = 0;
		if (img != null) {
			width = img.getWidth();
		}
		return width;
	}

	@Override
	public int getHeight() {
		int height = 0;
		if (img != null) {
			height = img.getHeight();
		}
		return height;
	}

	@Override
	public int getBitsPerPixel() {
		int bits = 0;
		if (img != null) {
			bits = img.getBitDepth();
		}
		return bits;
	}

	@Override
	public int getNumBands() {
		int bands = 1;
		if (img.getType() == COLOR_RGB || img.getType() == COLOR_256) {
			bands = 3;
		}
		return bands;
	}

	@Override
	public double getMinimumPixel() {
		return img.getDisplayRangeMin();
	}

	@Override
	public double getMaximumPixel() {
		return img.getDisplayRangeMax();
	}

	@Override
	public String getName() {
		return "ImageJ 'ImagePlus' Class Adapter";
	}

	@Override
	public List<String> getSupportedMediaTypes() {
		List<String> mediaTypes = new ArrayList<String>();
		mediaTypes.add("image/*");
		return mediaTypes;
	}

	@Override
	public void load(InputStream in) throws IOException, VersusException {
		File file = File.createTempFile("IJImageAdapterInput", ".tmp");
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		out.close();
		in.close();

		load(file);
		try {
			file.delete();
		} catch (Exception e) {
			System.out.println("Cannot delete temp file " + file.getName());
		}
	}

	@Override
	public void load(File file) throws IOException, VersusException {
		String path = file.getAbsolutePath();

		img = new ImagePlus(path);
	}

	@Override
	public String getCategory() {
		return "2D";
	}

}
