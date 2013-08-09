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


package transforms;

import java.util.HashSet;
import java.util.Set;

import converters.AdapterConverter;

import adapter.IJImagePlusAdapter;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAndMaskAdapter;
import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;
import ncsa.im2learn.ext.conversion.PCA;

public class PCATransformation implements Transformations {

	Adapter img;
	PCA pca = new PCA();

	/**
	 * Constructor that also loads an Adapter img into the instance  
	 * @param img Adapter that is loaded into the PCATransformation instance
	 */
	public PCATransformation(Adapter img) {
		this.img = img;
	}

	/**
	 * Constructor that creates an empty instance of PCATransformation
	 */
	public PCATransformation() {
	}

	@Override
	public void load(Adapter img) {
		this.img = img;
	}
	
	@Override
	public String getName() {
		return "A Principle Component Analysis transform";
	}

	@Override
	public Set<Class<?>> supportedAdapters() {
		Set<Class<?>> supported = new HashSet<Class<?>>();
		supported.add(IJImagePlusAdapter.class);
		supported.add(ImageObjectAdapter.class);
		supported.add(ImageObjectAndMaskAdapter.class);
		supported.add(BufferedImageAdapter.class);
		return supported;
	}


    public static final int           TYPE_BYTE        = 0;
    public static final int           TYPE_SHORT       = 1;
    public static final int           TYPE_USHORT      = 2;
    public static final int           TYPE_INT         = 3;
    public static final int           TYPE_LONG        = 4;
    public static final int           TYPE_FLOAT       = 5;
    public static final int           TYPE_DOUBLE      = 6;
    public static final int           TYPE_UNKNOWN     = 7;

	
	@Override
	public Adapter transform() throws ImageException {
		ImageObject result = null;
		if (img instanceof HasRGBPixels) {
			ImageObject imgObj = AdapterConverter.adapterToImageObject(img);
			pca.setComponents(((HasRGBPixels) img).getNumBands());
			pca.computeLoadings(imgObj);
			pca.applyPCATransform(imgObj);
			result = pca.getResult();
			result.convert(TYPE_FLOAT, false);
		}  
		return new ImageObjectAdapter(result);
	}

	/**
	 * returns the result of the pca transform
	 * @return the pca transform of the input adapter image
	 */
	public Adapter getResult(){
		return new ImageObjectAdapter(pca.getResult());
	}


}
