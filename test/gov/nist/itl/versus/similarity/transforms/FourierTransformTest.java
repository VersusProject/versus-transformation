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
import gov.nist.itl.versus.similarity.transforms.FourierTransformation;
import ij.ImagePlus;

import java.io.File;




public class FourierTransformTest {

	public static void main(String[] args) throws Exception {
		
		File f = new File("C:\\Users\\cng1\\Documents\\Test Images\\fakeheart.png");
		IJImagePlusAdapter img = new IJImagePlusAdapter();
		img.load(f);
		//img.getIJImage().show();
		FourierTransformation ftrans = new FourierTransformation(img);
		IJImagePlusAdapter result = (IJImagePlusAdapter) ftrans.transform();
		ImagePlus imgpRes = result.getIJImage();	
		imgpRes.show();
		result.saveImage("C:\\Users\\cng1\\Documents\\Test Images\\fakeheart ft fake8bit.tif");
		
//		
//		File f2 = new File("C:\\Users\\cng1\\Documents\\Test Images\\Simulated_Data_v2\\Simulated_Data\\shape-Ellipticity\\ellipse50-45.png");
//		IJImagePlusAdapter img2 = new IJImagePlusAdapter();
//		img2.load(f2);
//		FourierTransformation ftrans2 = new FourierTransformation(img2);
//		IJImagePlusAdapter result2 = (IJImagePlusAdapter) ftrans2.transform();
//		ImagePlus imgpRes2 = result2.getIJImage();	
//		imgpRes2.show();
//		result2.saveImage("C:\\Users\\cng1\\Documents\\Test Images\\Simulated_Data_v2\\Simulated_Data\\shape-Ellipticity\\ellipse50-45 ft fromg png.tif");
//		
//		double[][][] pixels1 = img.getRGBPixels();
//		double[][][] pixels2 = img2.getRGBPixels();
//		
//		System.out.println("Tif dims: " + pixels1.length + " " 
//				+ pixels1[0].length + " " + pixels1[0][0].length);
//		System.out.println("png dims: " + pixels2.length + " " 
//				+ pixels2[0].length + " " + pixels2[0][0].length);
//		
//		boolean value = true;
//		for (int i = 0; i < pixels1.length; i++)
//			for (int j = 0; j < pixels1[0].length; j++)
//				for (int k = 0; k < pixels1[0][0].length; k++)
//					if (pixels1[i][j][k] != pixels2[i][j][k]){
////						System.out.print(i + " " + j + " " + k + " == ");
////						System.out.println(pixels1[i][j][k] + " " + pixels2[i][j][k]);
//						value = false;						
//					}
//		System.out.println(value);
//		int[] lastPixel = img.getIJImage().getPixel(255, 255);
//		for (int i = 0; i < lastPixel.length; i++)
//			System.out.print(lastPixel[i]+ " ");
//		System.out.println("\n" + img2.getRGBPixel(255, 255, 0) + " " + img2.getRGBPixel(255, 255, 1) + " " + img2.getRGBPixel(255, 255, 2));
	}

}
