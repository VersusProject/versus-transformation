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
import gov.nist.itl.versus.similarity.converters.AdapterConverter;
import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;

/**
 * 
 * Implements a Gabor Transformation from im2learn for an gov.nist.itl.versus.similarity.adapter
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class GaborTransformation implements Transformations {

	private Adapter img;

	private GaborFilterBank _gfb = new GaborFilterBank();

	private final int numFreqs = 4;
	private final int numOrients = 5;
	private final double offset = Math.PI / 4;
	private int numRows, numCols, type;

	// Row and col parameters depend on image size
	// Type parameter depends on the number of image bands.
	// Types:
	// Gray, R, G, B, RGB, Opponent, HS Complex

	/**
	 * constructs an empty instance of FourierTransformation
	 */
	public GaborTransformation() {
	}

	/**
	 * constructs an instance of FourierTransformation containing an gov.nist.itl.versus.similarity.adapter to
	 * be processed
	 * 
	 * @param gov.nist.itl.versus.similarity.adapter
	 *            an gov.nist.itl.versus.similarity.adapter containing an image to be processed
	 */
	public GaborTransformation(Adapter img) {
		this.img = img;
	}


	@Override
	public void load(Adapter img) {
		this.img = img;
	}

	@Override
	public String getName() {
		return "A Gabor Transformation that applies a Gabor Filter Bank from im2learn" +
				" with 4 frequencies, 5 orientations, and a 45 degree offset, looking for " +
				"either RGB  or greyscale features."; 
	}

	@Override
	public Set<Class<?>> supportedAdapters() {
		Set<Class<?>> supported = new HashSet<Class<?>>();
		supported.add(IJImagePlusAdapter.class);
		supported.add(ImageObjectAdapter.class);
		supported.add(ImageObjectAndMaskAdapter.class);
		supported.add(BufferedImageAdapter.class);
		return supported;
	}
	
	@Override
	public Adapter transform() throws IncompatibleBitDepthException,
			ImageException {
		ImageObject image = AdapterConverter.adapterToImageObject(img);
		ImageObject outImage = null;

		if (image == null)
			return null;
		if (img instanceof HasRGBPixels) {
			numRows = ((HasRGBPixels) img).getHeight();
			numCols = ((HasRGBPixels) img).getWidth();
			if (((HasRGBPixels) img).getNumBands() == 1)
				type = 0;
			else if (((HasRGBPixels) img).getNumBands() == 3)
				type = 4;
			else
				throw new IncompatibleBitDepthException();
		}

		_gfb.setNumFrequencies(numFreqs);
		_gfb.setNumOrientations(numOrients);
		_gfb.setRows(numRows);
		_gfb.setCols(numCols);
		_gfb.setOffset(offset);
		_gfb.setFeatureType(type);

		// showAllActivated(event);
		int imgRows = image.getNumRows();
		int imgCols = image.getNumCols();
		int newRows = (int) Math.ceil(Math.log(imgRows) / Math.log(2));
		int newCols = (int) Math.ceil(Math.log(imgCols) / Math.log(2));


		ImageObject filterBankImg = null;

		filterBankImg = ImageObject.createImage(_gfb.getRows(), _gfb.getCols(),
				1, "DOUBLE");

		_gfb.discreteDecomposition(image, filterBankImg);

		
		int k = newRows;
		newRows = 1;
		for (int i = 0; i < k; i++)
			newRows = newRows * 2;
		k = newCols;
		newCols = 1;
		for (int i = 0; i < k; i++)
			newCols = newCols * 2;

		ImageObject temp = null;
		ImageObject oneBand = null;
		ImageObject imgFiltered = null;

		if (type != 6)
			outImage = ImageObject.createImage(image.getNumRows(),
					image.getNumCols(), image.getNumBands(), "DOUBLE");
		else
			outImage = ImageObject.createImage(image.getNumRows(),
					image.getNumCols(), 1, "DOUBLE");

		// loop over all bands in image
		for (int band = 0; band < image.getNumBands(); band++) {

			if (type != 6) {

				oneBand = ImageObject.createImage(image.getNumRows(),
						image.getNumCols(), 1, image.getType());
				oneBand = image.extractBand(new int[] { band });
			} else
				oneBand = image;

			temp = FourierTransform.padImageObject(oneBand, newRows, newCols);

			imgFiltered = _gfb.filterImage(temp);

			imgFiltered = FourierTransform.toMagnitudePhase(imgFiltered);

			if (type != 6)
				for (int i = 0; i < imgRows; i++)
					for (int j = 0; j < imgCols; j++)
						outImage.set(
								(i * outImage.getNumCols() + j)
										* outImage.getNumBands() + band,
								imgFiltered.getDouble((i
										* imgFiltered.getNumCols() + j) * 2));
			else {
				for (int i = 0; i < imgRows; i++)
					for (int j = 0; j < imgCols; j++)
						outImage.set(
								i * outImage.getNumCols() + j,
								imgFiltered.getDouble((i
										* imgFiltered.getNumCols() + j) * 2));
				band = 2;
			}
		}
		return new ImageObjectAdapter(outImage);
	}


}
