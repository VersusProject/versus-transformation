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


package gov.nist.itl.versus.similarity.analysis;

/**
 * Tester and runner of MetricScorer
 * 
 * @author cng1
 */

import java.io.*;

public class MetricScorerRun {

	/*
	 * 	private final static String[] FOLDERS = { "intensity-Blur-FourColorBox",
			"intensity-Gamma-FourColorBox",
			"intensity-NoiseGauss-FourColorBox", "intensity-NoiseGauss-Shaded",
			"intensity-NoiseUnif-Shaded", "intensity-NoiseUnif-FourColorBox",
			"position-NoiseGauss-Center", "position-NoiseGauss-FourColorBox",
			"position-NoiseGauss-Center", "position-NoiseGauss-FourColorBox",
			"position-NoiseUnif-Center", "position-NoiseUnif-FourColorBox",
			"position-Rotation", "position-Translation", "shape-Ellipticity",
			"shape-NoiseGauss-CircleWidth", "shape-NoiseGauss-DiskBnd",
			"shape-NoiseUnif-CircleWidth", "shape-NoiseUnif-DiskBnd",
			"shape-Scaling", "texture-CheckerGranularity",
			"texture-CheckerOrientation", "texture-NoiseGauss-LineOrient",
			"texture-NoiseGauss-LinePos", "texture-NoiseUnif-LineOrient",
			"texture-NoiseUnif-LinePos" };
	 */
	/**
	 * runs MetricScorer with the option of only being interested in 
	 * the first folder, and prints the metric returned. 
	 * 
	 * Refer to MetricScorer for more information
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		String in_dir = "C:\\Users\\Cindy\\Documents\\Dropbox\\School 2013-2014\\Summer Internship 2013\\SimData\\norm_Versus\\pearson";
		String file_out_name = "Blur-only";
		String[] result = null;
		
		int[] options = new int[24];
		options = setAllZero(options);
		options[0] = 1;
		
		result = MetricScorer.find_best(options, in_dir, file_out_name);
		System.out.println(MetricScorer.arrToString(result));
		System.out.println();
		
		
	}
	
	public static int[] setAllZero(int[] arr) {
		int[] zeroes = new int[arr.length];
		
		for (int i = 0; i<arr.length; i++) {
			zeroes[i] = 0;
		}
		
		return zeroes;
	}
}
