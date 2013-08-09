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
import java.io.File;
import java.io.IOException;

import ncsa.im2learn.core.datatype.ImageObject;
import ncsa.im2learn.core.io.tiff.TIFFLoader;

import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;
import gov.nist.itl.versus.similarity.transforms.GLCMTransformation;

import org.junit.Test;


public class GLCMTest {

	/**
	 * @param args
	 * @throws VersusException 
	 * @throws IOException 
	 */
	@Test
	public static void main(String[] args) throws IOException, VersusException {
		File f = new File("C:\\Users\\cng1\\Documents\\Test Images\\white triangle on black square small.png");
		IJImagePlusAdapter img = new IJImagePlusAdapter();
		img.load(f);
		GLCMTransformation gcTrans = new GLCMTransformation(img);
		Adapter result = gcTrans.transform();
		TIFFLoader io = new TIFFLoader();		
		ImageObject imgObj = ((ImageObjectAdapter) result).getImageObject();
		System.out.println(result);
		System.out.println(imgObj);
		io.writeImage("C:\\Users\\cng1\\Documents\\Test Images\\white triangle on black square small glcm normal.tif", imgObj);
	}
}
