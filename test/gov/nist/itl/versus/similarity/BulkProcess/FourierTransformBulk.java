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


package gov.nist.itl.versus.similarity.BulkProcess;

import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;
import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;
import gov.nist.itl.versus.similarity.transforms.FourierTransformation;
import ij.ImagePlus;

import java.io.*;
import java.util.Scanner;

import edu.illinois.ncsa.versus.VersusException;


public class FourierTransformBulk {

	public static void main(String[] args) throws Exception {

//		Scanner scanner = new Scanner(System.in);
//		System.out.print("> ");
//		String name = scanner.nextLine();
//      scanner.close();
		String name = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_v2\\Simulated_Data";
		System.out.println("Started...");
		File f = new File(name);
		recurse(f, name);

		System.out.println("Done.");
	}

	public static void recurse(File f, String name) throws IOException,
			VersusException, IncompatibleBitDepthException {
		if (f.isFile()) {
			IJImagePlusAdapter img = new IJImagePlusAdapter();
			img.load(f);
			FourierTransformation ftrans = new FourierTransformation(img);
			IJImagePlusAdapter result = (IJImagePlusAdapter) ftrans.transform();
			ImagePlus imgpRes = result.getIJImage();
			// imgpRes.show();
			int firstIndex = (name.indexOf("Simulated_Data", 50) + "Simulated_Data"
					.length());
			String nameFT = name.substring(0, firstIndex) + "_ft"
					+ name.substring(firstIndex, name.indexOf(".")) + "-ft.tif";
			result.saveImage(nameFT);
		} else if (f.isDirectory()) {
			File[] contents = f.listFiles();
			for (int i = 0; i < contents.length; i++) {
				recurse(contents[i], contents[i].getAbsolutePath());
			}
		}

	}

}
