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
import java.util.Set;

import ncsa.im2learn.core.datatype.ImageException;

import edu.illinois.ncsa.versus.adapter.Adapter;
import gov.nist.itl.versus.similarity.exceptions.IncompatibleBitDepthException;

/**
 * Marker interface for transforms. Transformations take an gov.nist.itl.versus.similarity.adapter, apply and transform
 * to and output another gov.nist.itl.versus.similarity.adapter. 
 * 
 * @author Cynthia Gan (cng1)
 */
public abstract interface Transformations {


	/**
	 * transforms the image loaded and returns the result  
	 * @return the transformed image in an ImageObjectAdapter
	 * @throws ImageException 
	 */
	Adapter transform() throws IncompatibleBitDepthException, ImageException;
	
	/**
	 * Loads an gov.nist.itl.versus.similarity.adapter into the transform
	 */ 
	void load(Adapter img);
	
	/**
	 * Returns the name of the transform.
	 * @return name of the transform.
	 */
	String getName();

	/**
	 * Returns the set of adapters supported by the transform.
	 * @return a set of adapters supported by the transform
	 */
	Set<Class<?>> supportedAdapters();


	
}
