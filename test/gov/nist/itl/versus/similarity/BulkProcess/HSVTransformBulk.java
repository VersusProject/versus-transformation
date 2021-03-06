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

import java.io.File;
import java.io.IOException;

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import edu.illinois.ncsa.versus.VersusException;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import gov.nist.itl.versus.similarity.adapter.IJImagePlusAdapter;
import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;
import gov.nist.itl.versus.similarity.transforms.HSVColorTransformation;
import gov.nist.itl.versus.similarity.transforms.PCATransformation;
import gov.nist.itl.versus.similarity.transforms.SobelTransformation;
import gov.nist.itl.versus.similarity.transforms.Transformations;

public class HSVTransformBulk {


	public static void main(String[] args) throws Exception {

		// Scanner scanner = new Scanner(System.in);
		// System.out.print("> ");
		// String name = scanner.nextLine();
		// scanner.close();
		String name = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_color\\Simulated_Data_color";
		System.out.println("Started...");
		File f = new File(name);
		recurse(f, name);

		System.out.println("Done.");
	}
	

    public static final int           TYPE_BYTE        = 0;
    public static final int           TYPE_SHORT       = 1;
    public static final int           TYPE_USHORT      = 2;
    public static final int           TYPE_INT         = 3;
    public static final int           TYPE_LONG        = 4;
    public static final int           TYPE_FLOAT       = 5;
    public static final int           TYPE_DOUBLE      = 6;
    public static final int           TYPE_UNKNOWN     = 7;

	public static void recurse(File f, String name) throws IOException,
			VersusException, IncompatibleBitDepthException, ImageException {
		if (f.isFile()) {
			ImageObjectAdapter img = new ImageObjectAdapter();
			img.load(f);
			Transformations trans = new HSVColorTransformation(img);
			ImageObjectAdapter result = (ImageObjectAdapter) trans.transform();
//			ImageObject resImgObj = result.getImageObject();
//			resImgObj = resImgObj.convert(TYPE_INT, true);
//			result = new ImageObjectAdapter(resImgObj);

			int firstIndex = (name.lastIndexOf("Simulated_Data_color") + "Simulated_Data_color"
					.length());
			String nameFT = name.substring(0, firstIndex) + "_hsv"
					+ name.substring(firstIndex, name.indexOf("."))
					+ "-hsv.tif";

			result.saveImage(nameFT);
			System.out.println("\t" + name + " processed.");
		} else if (f.isDirectory()) {
			File[] contents = f.listFiles();
			for (int i = 0; i < contents.length; i++)
				recurse(contents[i], contents[i].getAbsolutePath());
		}

	}

}
