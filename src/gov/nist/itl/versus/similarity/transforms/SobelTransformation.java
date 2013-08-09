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
import ncsa.im2learn.core.datatype.LimitValues;
import ncsa.im2learn.ext.math.GeomOper;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAndMaskAdapter;
import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;
import gov.nist.itl.versus.similarity.converters.AdapterConverter;
import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;


/**
 * 
 * Implements a Sobel Transformation from im2learn for a Versus gov.nist.itl.versus.similarity.adapter
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class SobelTransformation implements Transformations {

	private Adapter img;

	public SobelTransformation() {
	}

	public SobelTransformation(Adapter img) {
		this.img = img;
	}

	@Override
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
		return "A Sobel transformer.";
	}

	/*
	 * Code copy-pasted from im2learn's Edge class 
	 */
	@Override
	public Adapter transform() throws IncompatibleBitDepthException,
			ImageException {
		
		float _edgeMaxVal, _edgeMinVal;

		// classes
		GeomOper _myGeom = new GeomOper();
		
			ImageObject _res = null;
			ImageObject im = AdapterConverter.adapterToImageObject(img);
			// sanity check
			if ((im == null) || (im.getNumRows() <= 0) || (im.getNumCols() <= 0)) {
				System.out.println("Error: no image \n");
				throw new ImageException("Image non-existent");
			}

			// init the output edge image
			try {
				_res = ImageObject.createImage(im.getNumRows(), im.getNumCols(), 2,
						ImageObject.TYPE_FLOAT);
			} catch (ImageException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int index, indexIm;
			int offset = (im.getNumCols() + 1) * im.getNumBands();
			int offset1 = (im.getNumCols() - 1) * im.getNumBands();
			int offsetCenter = im.getNumCols() * im.getNumBands();

			int offsetIndex = _res.getNumBands() << 1;
			int offsetIndexIm = im.getNumBands() << 1;
			double val, D1, D2;

			// define offsets

			ImageObject vec1 = null;
			try {
				vec1 = ImageObject.createImage(1, 1, im.getNumBands(),
						ImageObject.TYPE_FLOAT);
			} catch (ImageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ImageObject vec2 = null;
			try {
				vec2 = ImageObject.createImage(1, 1, im.getNumBands(),
						ImageObject.TYPE_FLOAT);
			} catch (ImageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			index = (_res.getNumCols() + 1) * _res.getNumBands();
			indexIm = (im.getNumCols() + 1) * im.getNumBands();
			if (im.getNumBands() == 1) {
				for (int i = 1; i < im.getNumRows() - 1; i++) {
					for (int j = 1; j < im.getNumCols() - 1; j++) {
						val = im.getDouble(indexIm - offset)
								+ (im.getDouble(indexIm - 1) * 2)
								+ im.getDouble(indexIm + offset1);
						D1 = im.getDouble(indexIm - offset1)
								+ (im.getDouble(indexIm + 1) * 2)
								+ im.getDouble(indexIm + offset);
						D1 -= val;
						val = im.getDouble(indexIm - offset)
								+ (im.getDouble(indexIm - im.getNumCols()) * 2)
								+ im.getDouble(indexIm - offset1);
						D2 = im.getDouble(indexIm + offset1)
								+ (im.getDouble(indexIm + im.getNumCols()) * 2)
								+ im.getDouble(indexIm + offset);
						D2 -= val;
						indexIm++;
						// magnitude
						val = Math.sqrt(D1 * D1 + D2 * D2);
						_res.set(index, (float) val);
						index++;
						// direction
						_res.set(index, (float) Math.atan2(D2, D1));
						index++;
					}
					index += offsetIndex;
					indexIm += offsetIndexIm;
				}
				// end of if(sampPerPixel == 1)
			} else {

				vec1.setData(0);
				vec2.setData(0);

				for (int i = 1; i < im.getNumRows() - 1; i++) {
					for (int j = 1; j < im.getNumCols() - 1; j++) {
						for (int k = 0; k < im.getNumBands(); k++) {
							vec1.set(
									k,
									(float) ((im.getDouble(indexIm + k - offset)
											+ (im.getDouble(indexIm + k - 1) * 2) + im
											.getDouble(indexIm + k + offset1))));
							vec2.set(
									k,
									(float) ((im.getDouble(indexIm + k - offset1)
											+ (im.getDouble(indexIm + k + 1) * 2) + im
											.getDouble(indexIm + k + offset))));
						}
						D1 = _myGeom.euclidDist(im.getNumBands(),
								(float[]) vec1.getData(), 0,
								(float[]) vec2.getData(), 0);
						for (int k = 0; k < im.getNumBands(); k++) {
							vec2.set(
									k,
									(float) ((im.getDouble(indexIm + k - offset)
											+ (im.getDouble(indexIm + k
													- offsetCenter) * 2) + im
											.getDouble(indexIm + k - offset1))));
							vec2.set(
									k,
									(float) ((im.getDouble(indexIm + k + offset1)
											+ (im.getDouble(indexIm + k
													+ offsetCenter) * 2) + im
											.getDouble(indexIm + k + offset))));
						}
						D2 = _myGeom.euclidDist(im.getNumBands(),
								(float[]) vec1.getData(), 0,
								(float[]) vec2.getData(), 0);
						indexIm += im.getNumBands();
						// magnitude
						val = Math.sqrt(D1 * D1 + D2 * D2);
						_res.set(index, (float) val);
						index++;
						// direction
						_res.set(index, (float) Math.atan2(D2, D1));
						index++;
					}
					index += offsetIndex;
					indexIm += offsetIndexIm;
				}
				// end of high dimensional data
			}
			// take care of borders
			_edgeMaxVal = LimitValues.MIN_FLOAT;
			_edgeMinVal = LimitValues.MAX_FLOAT;
			float valFloat;
			// find magnitude max and min values
			for (index = 0; index < _res.getSize(); index += 2) {
				valFloat = _res.getFloat(index);
				if (valFloat > _edgeMaxVal) {
					_edgeMaxVal = valFloat;
				}
				if (valFloat < _edgeMinVal) {
					_edgeMinVal = valFloat;
				}
			}

			int oneRow = _res.getNumCols() * _res.getNumBands();
			// first row
			for (index = 0; index < oneRow; index += _res.getNumBands()) {
				_res.set(index, _edgeMinVal);
				_res.set(index + 1, 0);
			}
			// last row
			for (index = _res.getSize() - oneRow; index < _res.getSize(); index += _res
					.getNumBands()) {
				_res.set(index, _edgeMinVal);
				_res.set(index + 1, 0);
			}
			// first col
			for (index = oneRow; index < _res.getSize() - oneRow; index += oneRow) {
				_res.set(index, _edgeMinVal);
				_res.set(index + 1, 0);
			}
			// last col
			for (index = oneRow - 1; index < _res.getSize() - oneRow; index += oneRow) {
				_res.set(index, _edgeMinVal);
				_res.set(index + 1, 0);
			}

			// Return only the magnitude information from the sobel transform. 
			// Band 2, which contains the direction information, is ignored 
			return new ImageObjectAdapter(_res.extractBand(0));
	}


	
	
}
