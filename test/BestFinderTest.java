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


import org.junit.Test;

import analysis.BestFinder;


public class BestFinderTest {

	/*
	private final static String[] FOLDERS = {"intensity-Blur-FourColorBox", "intensity-Gamma-FourColorBox", 
        "intensity-NoiseGauss-FourColorBox", "intensity-NoiseGauss-Shaded",
        "intensity-NoiseUnif_Shaded", "intensity-NoiseUnif-Center", 
        "position-NoiseGauss-Center", "position-NoiseGauss-FourColorBox",
        "position-NoiseGauss-Center", "NoiseGauss-FourColorBox",
        "position-NoiseUnif-Center", "position-NoiseUnif-FourColorBox",
        "position-Rotation", "position-Translation", "shape-Ellipticity",
        "shape-NoiseGauss-CircleWidth", "shape-NoiseGauss-DiskBnd",
        "shape-NoiseUnif-CircleWidth", "shape-NoiseUnif-DiskBnd",
        "shape-Scaling", "texture-CheckerGranularity", "texture-CheckerOrientation",
        "texture-NoiseGauss-LineOrient", "texture-NoiseGauss-LinePos",
        "texture-NoiseUnif-LineOrient", "texture-NoiseUnif-LinePos"};
        */
	
	@Test
	public void test() {
		String dir = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_v2";
		boolean[] values = {false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
		System.out.println(values.length);
		
		String[][] recs = BestFinder.find_best(values, dir);
		for (int i = 0; i < recs.length; i++) {
			for (int j = 0; j < recs[i].length; j++)
				System.out.print(recs[i][j] + "\t\t\t");
			System.out.println();
		}
		
	}

}
