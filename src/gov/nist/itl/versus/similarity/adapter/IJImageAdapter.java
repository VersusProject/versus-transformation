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


package gov.nist.itl.versus.similarity.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import imagescience.image.*;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.FileLoader;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.StreamLoader;
import edu.illinois.ncsa.versus.utility.HasCategory;

/**
 * 
 * Adapter for Image class from ImageJ 
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class IJImageAdapter implements Adapter, HasRGBPixels, FileLoader,
		StreamLoader, HasCategory {

	/** The alpha component (stored in bits 24-31). */
	public final static int ALPHA = 0;

	/** The red component (stored in bits 16-23). */
	public final static int RED = 1;

	/** The green component (stored in bits 8-15). */
	public final static int GREEN = 2;

	/** The blue component (stored in bits 0-7). */
	public final static int BLUE = 3;

	/** The full 32-bit integer value. */
	public final static int FULL = 4;

	private Image img = null;

	/**
	 * Wrap an ImageJ Image object in a Versus gov.nist.itl.versus.similarity.adapter
	 * 
	 * @param img
	 */
	public IJImageAdapter(Image img) {
		this.img = img;
	}

	/**
	 * Create an empty Versus gov.nist.itl.versus.similarity.adapter for an IJImageAdapter
	 */
	public IJImageAdapter() {
	}

	/**
	 * Return the wrapped ImageJ image
	 * @return wrapped ImageJ image 
	 */
	public Image getIJImage() {
		return img;
	}
	
	@Override
	public double[][][] getRGBPixels() {
		if (img != null) {
			Dimensions dims = img.dimensions();
			Coordinates coor = new Coordinates(0, 0);
			double[][][] pixels = new double[dims.x][dims.y][4];
			if (img.type().equals("imagescience.image.ColorImage") != true) {
				pixels = new double[dims.x][dims.y][0];
				for (coor.x = 0; coor.x < dims.x; coor.x++) {
					for (coor.y = 0; coor.y < dims.y; coor.y++) {
						pixels[coor.x][coor.y][0] = img.get(coor);
					}
				}
			} else {
				for (int i = 0; i < 4; i++) {
					for (coor.x = 0; coor.x < dims.x; coor.x++) {
						for (coor.y = 0; coor.y < dims.y; coor.y++) {
							pixels[i][coor.x][coor.y] = img.get(coor);
						}
					}
					((ColorImage) img).component(i);
				}
			}
			return pixels;
		} else
			return null;
	}

	@Override
	public double getRGBPixel(int row, int column, int band) {
		double pixel = 0;
		if (img != null  && inBounds(row, column)) {
			Coordinates coor = new Coordinates(row, column);
			if (img.type().equals("imagescience.image.ColorImage") == true) {
				((ColorImage) img).component(band);
			}
			pixel = img.get(coor);
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
		if (row > 0 && row < getHeight() && col > 0 && col < getWidth())
			return true;
		else
			return false;
	}
	
	@Override
	public int getWidth() {
		int width = 0;
		if (img != null) {
			Dimensions dims = img.dimensions();
			width = dims.x;
		}
		return width;
	}

	@Override
	public int getHeight() {
		int height = 0;
		if (img != null) {
			Dimensions dims = img.dimensions();
			height = dims.y;
		}
		return height;
	}

	@Override
	public int getBitsPerPixel() {
		if (img != null) {
			String str = img.type();
			switch (str) {
			case "imagescience.image.ColorImage":
				return 16;
			case "imagescience.image.ByteImage":
				return 1;
			case "imagescience.image.ShortImage":
				return 2;
			case "imagescience.image.FloatImage":
				return 4;
			}
		}
		return 0;
	}

	@Override
	public int getNumBands() {
		int bands = 1;
		if (img.type().equals("imagescience.image.ColorImage") == true) {
			bands = 4;
		}
		return bands;
	}

	@Override
	public String getName() {
		return "ImageJ 'Image' Superclass Adapter";
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

		ImagePlus imgp = new ImagePlus(path);
		img = Image.wrap(imgp);
	}

	@Override
	public String getCategory() {
		// TODO The ImageJ 'Image' class supports more than 2 dimensions but
		// this gov.nist.itl.versus.similarity.adapter doesn't, should the gov.nist.itl.versus.similarity.adapter be expanded?
		return "2D";
	}

	@Override
	public double getMinimumPixel() {
		return img.minimum();
	}

	@Override
	public double getMaximumPixel() {
		return img.maximum();
	}

}
