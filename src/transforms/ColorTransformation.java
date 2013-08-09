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

import adapter.IJImagePlusAdapter;

import converters.AdapterConverter;

import ncsa.im2learn.core.datatype.ImageException;
import ncsa.im2learn.core.datatype.ImageObject;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.HasRGBPixels;
import edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAdapter;
import edu.illinois.ncsa.versus.adapter.impl.ImageObjectAndMaskAdapter;

/**
 * 
 * Abstract class for Colorspace Transformations for an adapter 
 * from RGB space to other spaces using the im2learn project 
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public abstract class ColorTransformation implements Transformations {

	Adapter img;
	protected ColorModels cmTrans = new ColorModels();
	{
		cmTrans.setColorModelFrom("RGB");
	}

	/**
	 * Constructs an empty ColorTransformation instance
	 */
	public ColorTransformation() {
	}

	/**
	 * constructor that loads the parameter img as the image to be transformed
	 * 
	 * @param img
	 *            Adapter to be transformed into a different color space
	 */
	public ColorTransformation(Adapter img) {
		this.img = img;
	}

	public void load(Adapter img) {
		this.img = img;
	}

	@Override
	public String getName() {
		return "A transformer that changes the color space of an image from RGB to another, depending on what subclass it is invoked from, default is HSV";
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


	@Override
	public Adapter transform() throws ImageException {
		ImageObject imgObj;
		ImageObjectAdapter result = null;
		if (img instanceof HasRGBPixels) {
			imgObj = AdapterConverter.adapterToImageObject((HasRGBPixels) img);
			imgObj = imgObj.convert(ImageObject.TYPE_FLOAT, false);
			cmTrans.convert(imgObj);
			result = new ImageObjectAdapter(cmTrans.getConvertedIm());
		}
		return (Adapter) result;
	}

}
