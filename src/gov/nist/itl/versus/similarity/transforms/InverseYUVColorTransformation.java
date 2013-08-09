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
 * A Colorspace Transformation from YUV to RGB for an gov.nist.itl.versus.similarity.adapter 
 * using the im2learn project 
 * 
 * @author Cynthia Gan (cng1)
 *
 */

public class InverseYUVColorTransformation extends ColorTransformation {

	/**
	 * constructs an empty instance of YUVColorTransformation
	 * by calling the ColorTransformation super class and changing the settings
	 */
	public InverseYUVColorTransformation() {
		super();
		cmTrans.setColorModelFrom("YUV");
		cmTrans.setColorModelTo("RGB");
	}

	/**
	 * constructs and loads an gov.nist.itl.versus.similarity.adapter into an instance of InverseYUVColorTransformation
	 * by calling the ColorTransformation super class and changing the settings
	 */
	public InverseYUVColorTransformation(Adapter img) {
		super(img);
		this.img = img;
		cmTrans.setColorModelFrom("YUV");
		cmTrans.setColorModelTo("RGB");
	}

	@Override
	public String getName() {
		return "A Transformer that changes the color space of one YUV image to RGB";
	}

}
