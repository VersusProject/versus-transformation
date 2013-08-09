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

import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;
import gov.nist.itl.versus.similarity.transforms.PCATransformation;
import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;

import mpicbg.imglib.type.numeric.complex.ComplexDoubleType;
import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import ncsa.im2learn.core.io.tiff.TIFFLoader;

import org.junit.Test;

import registration3d.Fast_FourierTransform;
import registration3d.Fast_FourierTransform.FloatArray2D;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;

public class PCATransformTest {

	@Test
	public void testTransform() throws IOException, VersusException, ImageException {

		TIFFLoader readerwriter = new TIFFLoader();
		ImageObject imgObj = readerwriter.readImage(
				"C:\\Users\\cng1\\Documents\\Test Images\\fakeheart.tif",
				null, 1);
		ImageObjectAdapter img = new ImageObjectAdapter(imgObj);
		PCATransformation PCATrans = new PCATransformation(img);
		Adapter result = PCATrans.transform();
		ImageObject imgObjRes = ((ImageObjectAdapter) result).getImageObject();
		readerwriter
				.writeImage(
						"C:\\Users\\cng1\\Documents\\Test Images\\fakeheart pca.tif",
						imgObjRes);

	}
}
