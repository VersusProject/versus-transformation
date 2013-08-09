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

import edu.illinois.ncsa.versus.adapter.Adapter;


/**
 * 
 * A Colorspace Transformation from RGB to HSV for an gov.nist.itl.versus.similarity.adapter 
 * using the im2learn project 
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class HSVColorTransformation extends ColorTransformation{
	
	/**
	 * constructs an empty instance of HSVColorTransformation
	 * by calling the ColorTransformation super class and changing the settings
	 */
	public HSVColorTransformation() {
		super();
	}
	
	/**
	 * constructs and loads an gov.nist.itl.versus.similarity.adapter into an instance of HSVColorTransformation
	 * by calling the ColorTransformation super class and changing the settings
	 */
	public HSVColorTransformation(Adapter img) {
		super(img);
		this.img = img;
		cmTrans.setColorModelTo("HSV");
	}

	@Override
	public String getName() {
		return "A Transformer that changes the color space of one RGB image to HSV";
	}

}
