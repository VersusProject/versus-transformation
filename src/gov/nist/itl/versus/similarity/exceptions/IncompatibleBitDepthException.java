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


package gov.nist.itl.versus.similarity.exceptions;

/**
 * An exception for if an gov.nist.itl.versus.similarity.adapter is of a bit depth unsupported by an operation
 *  
 * @author Cynthia Gan (cng1)
 *
 */


public class IncompatibleBitDepthException extends Exception {

	private static final long serialVersionUID = -3234780407264830203L;

	public IncompatibleBitDepthException() {
		super();
	}

	public IncompatibleBitDepthException(String message) {
		super(message);
	}

	public IncompatibleBitDepthException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncompatibleBitDepthException(Throwable cause) {
		super(cause);
	}

}
