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

import org.junit.Test;

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import ncsa.im2learn.core.io.tiff.TIFFLoader;
import transforms.GrayColorTransformation;
import transforms.Transformations;
import transforms.YUVColorTransformation;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import exceptions.IncompatibleBitDepthException;

public class GreyColorTransformationTest {

	@Test
	public void testTransform() throws IOException, VersusException,
			ImageException, IncompatibleBitDepthException {

		TIFFLoader readerwriter = new TIFFLoader();
		ImageObject imgObj = readerwriter.readImage(
				"C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_color\\Simulated_Data_color\\PeakTranslation\\peakTrans_r0_g105_b100_8.tif",
				null, 1);
		Adapter img = (Adapter) new ImageObjectAdapter(imgObj);
		Transformations trans = (Transformations) new GrayColorTransformation(
				img);
		Adapter result = trans.transform();
		ImageObject imgObjRes = ((ImageObjectAdapter) result).getImageObject();
		readerwriter
				.writeImage(
						"C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_color\\Simulated_Data_color\\PeakTranslation\\peakTrans_r0_g105_b100_8_grey.tif",
						imgObjRes);

	}
}
