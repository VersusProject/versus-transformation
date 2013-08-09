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


package transform;

import java.io.IOException;

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import ncsa.im2learn.core.io.tiff.TIFFLoader;

import org.junit.Test;

import transforms.Transformations;
import transforms.YUVColorTransformation;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import exceptions.IncompatibleBitDepthException;

public class YUVColorTransformationTest {

	@Test
	public void testTransform() throws IOException, VersusException, ImageException, IncompatibleBitDepthException {

		TIFFLoader readerwriter = new TIFFLoader();
		ImageObject imgObj = readerwriter.readImage(
				"C:\\Users\\cng1\\Documents\\Test Images\\R is GplusB.tif",
				null, 1);
//		readerwriter
//				.writeImage(
//						"C:\\Users\\cng1\\Documents\\Test Images\\20130605_105824 clone.tif",
//						imgObj);
		Adapter img = (Adapter) new ImageObjectAdapter(imgObj);
		Transformations trans = (Transformations) new YUVColorTransformation(img);
		Adapter result = trans.transform();
		ImageObject imgObjRes = ((ImageObjectAdapter) result).getImageObject();
		readerwriter
				.writeImage(
						"C:\\Users\\cng1\\Documents\\Test Images\\R is GplusB yuv.tif",
						imgObjRes);

	}
}