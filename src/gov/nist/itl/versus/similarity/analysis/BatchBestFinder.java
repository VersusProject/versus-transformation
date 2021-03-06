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
 *  DEPRECATED
 *  	Use MetricScorerRun
 *  
 *  Writes the best metric of each type of change by itself into a csv 
 *  "outfile" from the folder "main_dir" which is assumed to have an "gov.nist.itl.versus.similarity.analysis"
 *  subfolder generated by the "find_best_analysis[multireg]" python program. 
 *  Calls the BestFinder class' findbest method (which returns a ragged string array 
 *  formatted as below) giving it an array with that specifies each type of change once. 
 *  
 *   Return format = { {"transform"}, {"feature"}, 
 *   {"measure"}, {"folder 1", "folder 2", "..."}, {"regression type 1", 
 *   "regression type 2", "..."}, {"regression parameters 1", 
 *   "regression parameters 2", "..."}, {"regression graph file path 1", 
 *   "regression graph file path 2", "..." } };	
 *   
 *   @author cng1
 *   		
 */
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

@Deprecated
public class BatchBestFinder {

	// The outfile path and the directory that contains the "gov.nist.itl.versus.similarity.analysis" folder of interest
	static String out_file = "C:\\Users\\cng1\\Documents\\Presentations\\bests.csv";
	static String main_dir = "C:\\Users\\cng1\\Documents\\TestImages\\Simulated_Data_v2";
	
	/**
	 * Refer to class purpose
	 * 
	 * @param args 
	 * 		parameter not used 
	 */
	public static void main(String[] args) {
		boolean[] options = new boolean[24];

		BufferedWriter writer = null;       
		// Create a file writer
			try {
				writer = new BufferedWriter(new FileWriter(out_file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Print the header to the outfile
			String header = "Transform,Feature,Measure,Type of Change, Type of Regression,"
					+ "Regression Params, Graph Path"; 
			try {
				writer.write(header);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Iterate through each type of change, find the best metric, 
			// and write a row for it in the outfile 
			for (int i = 0; i < options.length; i++) {
				// Reset the options array
				options = set_false(options);
				// Set one type of change to be of interest
				options[i] = true;
				// Get the best metric
				String[][] recArr = BestFinder.find_best(options, main_dir);
				// Convert the string array to a string
				String rec = arrToString(recArr);
				// Write the reccomended metric as a row in the outfile
				try {
					writer.write(rec);
					writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 *  return a boolean array filled with "false" of the same length as the boolean array passed into it
	 * @param arr
	 * 		length of this array becomes the length of the returned array 
	 * @return boolean array filled with "false" of the same length as the boolean array passed into it
	 * 
	 */
	private static boolean[] set_false(boolean arr[]) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = false;
		return arr;			
	}
	
	/**
	 *  Converts a metric recommendation in a ragged string array to a string 
	 *  
	 * @param recs
	 * 		the metric recommendation in ragged string array form
	 * @return
	 * 		the metric recommendation in string form
	 */
	private static String arrToString(String[][] recs){
		String str = "";
		for (int i = 0; i < recs.length; i++) {
			for (int j = 0; j < recs[i].length; j++)
				str += recs[i][j] + ",";
			if (recs[i].length > 1)
				str += ",";
		}
		return str;
	}

}
