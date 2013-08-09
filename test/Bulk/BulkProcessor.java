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


/**
 * Calls the main function of each of the bulk processing classes written for
 * each transform. 
 * 
 * Restructuring recommended, so at the very least the directory containing 
 * the images can be passed into each of the classes, and ideally so that
 * the transformation class can be as well, as they all implement the
 * "Transformation" interface.
 * 
 * @author cng1
 * 
 */

package Bulk;

public class BulkProcessor {

//	//static String refPath = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_v2\\Simulated_Data";
//	  static String refPath = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_color\\Simulated_Data_color";
	
	public static void main(String[] args) throws Exception {
		FourierTransformBulk.main(null);
		GaborTransformBulk.main(null);
		GLCMTransformBulk.main(null);
		GrayTransformBulk.main(null);
		PCATransformationBulk.main(null);
		SobelTransformBulk.main(null);
		YUVTransformBulk.main(null);
		

	}

}
