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


package Bulk;

import java.io.File;
import java.io.IOException;

import ncsa.im2learn.core.datatype.ImageException;
import transforms.GaborTransformation;
import transforms.Transformations;
import adapter.IJImagePlusAdapter;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import exceptions.IncompatibleBitDepthException;

public class GaborTransformBulk {

	public static void main(String[] args) throws Exception {

		// Scanner scanner = new Scanner(System.in);
		// System.out.print("> ");
		// String name = scanner.nextLine();
		// scanner.close();
		String name = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_v2\\Simulated_Data";
		System.out.println("Started...");
		File f = new File(name);
		recurse(f, name);

		System.out.println("Done.");
	}

	public static void recurse(File f, String name) throws IOException,
			VersusException, IncompatibleBitDepthException, ImageException {
		if (f.isFile()) {
			IJImagePlusAdapter img = new IJImagePlusAdapter();
			img.load(f);
			Transformations trans = new GaborTransformation(img);
			ImageObjectAdapter result = (ImageObjectAdapter) trans.transform();

			int firstIndex = (name.lastIndexOf("Simulated_Data") + "Simulated_Data"
					.length());
			String nameFT = name.substring(0, firstIndex) + "_gabor"
					+ name.substring(firstIndex, name.indexOf("."))
					+ "-gabor.tif";

			result.saveImage(nameFT);
			System.out.println("\t" + name + " processed.");
		} else if (f.isDirectory()) {
			File[] contents = f.listFiles();
			for (int i = 0; i < contents.length; i++)
				recurse(contents[i], contents[i].getAbsolutePath());
		}

	}

}
