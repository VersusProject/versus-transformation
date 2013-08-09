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
 * DEPRECATED
 * 		Use MetricScorer (Or MetricScorerRun for "stand alone") 
 * 
 * Used after: find_best_analysis.py
 * 
 * Static class that returns a recommendations for transform-feature-measure, 
 * with the corresponding best regressions for each folder of interest given
 * a array of booleans saying whether a folder is of interest and the
 * directory containing the "gov.nist.itl.versus.similarity.analysis" subfolder with the best regression files. 
 * 
 * Scoring Method: 
 *     -Each metric/type-of-change combination is represented by its 
 *     Coefficient of Determination. 
 *     -A metric's final score is the sum of the representative fitnesses
 *     for only the types-of-change with a boolean value of true
 *     -The metric with the highest score is returned.  
 *     		the transform-feature-measure is returned as well as 
 *     		for each type of change specified, 
 *     		-the type of regression
 *     		-the parameters of the resulting regression
 *     		-the path to the graphs that plot the t-f-m vs change with 
 *     			all the regressions fitted to it.  
 * 
 * @author Cynthia Gan (cng1)
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Deprecated
public class BestFinder {
	// Index starts from 0, column the transformation applied is in
	private final static int TRANS_COL = 0;
	// Index starts from 0, column the descriptor is in
	private final static int DESCRIPTOR_COL = 1;
	// Index starts from 0, column the measure is in
	private final static int MEASURE_COL = 2;
	// Index starts from 0, column the regression name is in
	private final static int REG_TYPE_COL = 3;
	// Index starts from 0, column the regression parameters are in
	private final static int REG_PARAMS_COL = 4;
	// Index starts from 0, column the regression fitness is in
	private final static int FITNESS_COL = 5;
	// Index starts from 0, column the slope of the linear fit is in
	private final static int LIN_COL = 6;
	// what character separates the cols, (e,g, a comma in a .csv file)
	private final static String SPLIT_CHAR = ",";
	// what suffix all the spreadsheets have
	private final static String FILE_SUFFIX = "-best.csv";
	// what subfolders the spreadsheets are in
	private final static String FILE_SUBFOLDER = "gov.nist.itl.versus.similarity.analysis";

	// Guide to what folders the booleans should refer to:
	private final static String[] FOLDERS = { "intensity-Blur-FourColorBox",
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

	/**
	 * 
	 * @param options
	 *            an array of booleans that state whether the corresponding
	 *            folder is of interest
	 * @param main_dir
	 *            the main folder that contains the "gov.nist.itl.versus.similarity.analysis" folder of
	 *            regression gov.nist.itl.versus.similarity.analysis of all the folders.
	 * @return recommendations for transform-feature-measure, with the
	 *         corresponding best regressions for each folder of interest, The
	 *         result will be returned as: { {"transform"}, {"feature"},
	 *         {"measure"}, {"folder 1", "folder 2", ...}, {"regression type 1",
	 *         "regression type 2", ...}, {"regression parameters 1",
	 *         "regression parameters 2", ...}, {"regression graph file path 1",
	 *         "regression graph file path 2", ... } }
	 */
	public static String[][] find_best(boolean[] options, String main_dir) {
		// Number of folders selected
		int counts = countOptions(options);
		String[][] result = new String[7][];
		String[] max_row = null;
		String[] folderNames = getFolderNames(options);
		String[] maxRegTypes = new String[counts];
		String[] maxRegParams = new String[counts];
		String[] maxRegPaths = new String[counts];

		// Get paths to the relevant files based off the boolean options and a
		// root directory
		String[] filePaths = getFiles(options, main_dir);

		// Create a file reader for each file, store it in an array
		BufferedReader[] readers = new BufferedReader[filePaths.length];
		for (int i = 0; i < filePaths.length;i++)
			try {
				readers[i] = new BufferedReader(new FileReader(filePaths[i]));
				readers[i].readLine(); // Skip first line which is just a header
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		double sum = 0;
		double max = 0;
		String line = "";

		try {
			// Search through all the lines of a file
			while ((line = readers[0].readLine()) != null) {
				String[] regPaths = getRegressionPaths(options, main_dir, line);
				String[] regTypes = new String[counts];
				String[] regParams = new String[counts];

				// Pull a row's worth of information from the first file with
				// the first reader and save it temporarily
				sum = 0;
				String[] row = line.split(SPLIT_CHAR);
				regTypes[0] = row[REG_TYPE_COL];
				regParams[0] = row[REG_PARAMS_COL];

				// Save the names of the comparison options to ensure the right
				// row is being summed in the other files
				String[] prevRow = row;
				if (row.length != 0) {
					sum += filterFitness(row[FITNESS_COL], row[LIN_COL]);

					// Sum all the fitnesses for a cumulative fitness value
					for (int i = 1; i < readers.length; i++) {
						// Only add them if they are the corresponding row
						if (properlyPaired(prevRow, row)) {

							// Pull a row's worth of information, and save it
							// temporarily
							line = readers[i].readLine();
							row = line.split(SPLIT_CHAR);
							regTypes[i] = row[REG_TYPE_COL];
							regParams[i] = row[REG_PARAMS_COL];

							if (row.length != 0)
								sum += filterFitness(row[FITNESS_COL], row[LIN_COL]);
						}
					}
						// If the current row the a highest sum thus far
						// save everything about the row
						if (sum > max) {
							max = sum;
							max_row = row;
							maxRegTypes = regTypes;
							maxRegParams = regParams;
							maxRegPaths = regPaths;
						}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Package the results into the desired array structure
		result[0] = new String[] { max_row[TRANS_COL] };
		result[1] = new String[] { max_row[DESCRIPTOR_COL] };
		result[2] = new String[] { max_row[MEASURE_COL] };
		result[3] = folderNames;
		result[4] = maxRegTypes;
		result[5] = maxRegParams;
		result[6] = maxRegPaths;
		return result;
	}

	/**
	 * returns an array of strings containing the list of names of the folders
	 * of interest in order
	 * 
	 * @param options
	 *            array of booleans stating whether a folder is of interest or
	 *            not
	 * @return an array of strings containing the list of names of the folders
	 *         of interest in order
	 */
	private static String[] getFolderNames(boolean[] options) {
		int count = countOptions(options);

		String[] names = new String[count];
		int index = 0;
		for (int i = 0; i < options.length; i++) {
			if (options[i] == true) {
				names[index++] = FOLDERS[i];
			}
		}
		return names;
	}

	/**
	 * Get paths to the relevant files based off the boolean options and a root
	 * directory
	 * 
	 * @param options
	 *            an array of booleans that state whether the corresponding
	 *            folder is of interest
	 * @param mainDir
	 *            the main folder that contains the "gov.nist.itl.versus.similarity.analysis" folder of
	 *            regression gov.nist.itl.versus.similarity.analysis of all the folders.
	 * @return an array that contains the paths to each of the files containing
	 *         the gov.nist.itl.versus.similarity.analysis of the folders specified in the options array
	 */
	private static String[] getFiles(boolean[] options, String mainDir) {
		int count = countOptions(options);

		String[] paths = new String[count];
		int index = 0;
		for (int i = 0; i < options.length; i++) {
			if (options[i] == true) {
				String file = mainDir + "\\" + FILE_SUBFOLDER + "\\"
						+ FOLDERS[i] + FILE_SUFFIX;
				paths[index++] = file;
			}
		}
		return paths;
	}

	/**
	 * Generate a String array with file paths to the relevant regression graphs
	 * for the row being processed
	 * 
	 * @param options
	 *            an array of booleans that state whether the corresponding
	 *            folder is of interest
	 * @param rootDir
	 *            the main folder that contains the "gov.nist.itl.versus.similarity.analysis" folder of
	 *            regression gov.nist.itl.versus.similarity.analysis of all the folders.
	 * @param line
	 *            The string containing the line being added to the sum, which
	 *            contains which transform, feature and measure it analyzes.
	 * @return a list of file paths to the relevant regression graphs for the
	 *         row being processed (folder(change-type), transform, feature,
	 *         measure)
	 */
	private static String[] getRegressionPaths(boolean[] options,
			String rootDir, String line) {
		int count = countOptions(options);
		String[] regPaths = new String[count];

		String[] row = line.split(",");

		int index = 0;
		for (int i = 0; i < options.length; i++) {
			if (options[i] == true) {
				String transformFolder = "Simulated_Data_" + row[TRANS_COL]
						+ "_Versus_graph";
				String imageName = row[DESCRIPTOR_COL] + "_"
						+ row[MEASURE_COL].replace(' ', '_') + ".png";
				regPaths[index++] = rootDir + "\\" + transformFolder + "\\"
						+ FOLDERS[i] + "\\" + imageName;
			}
		}
		return regPaths;
	}

	/**
	 * Return the number of options that are true, that is, the number of
	 * folders of interest
	 * 
	 * @param options
	 *            an array of booleans that state whether the corresponding
	 *            folder is of interest
	 * 
	 * @return the number of options containing true, that is, the number of
	 *         folders of interest for array declaration use in other methods
	 */
	private static int countOptions(boolean[] options) {
		int count = 0;
		for (int i = 0; i < options.length; i++)
			if (options[i] == true)
				count++;
		return count;
	}

	/**
	 * Filters out measures without valid regression by replacing the 
	 * fitness value with -999, effectively knocking those measures out of the
	 * running for best fit. They filter out cells filled with "None" and regressions
	 * with a nearly flat slope. 
	 * 
	 * @param cell
	 *            the string containing the value in the Fitness column of the
	 *            line of the csv being processed
	 * @return a double containing the fitness value of the line being processed
	 *         or -999 if no regression was possible
	 */
	private static double filterFitness(String cell, String lin_slope) {
		double filtered = 0;
		if (cell.equalsIgnoreCase("None") || lin_slope.equalsIgnoreCase("nan") 
				|| Math.abs(Double.parseDouble(lin_slope)) < 0.0000000001) {
			filtered = -999;
		} else {
			filtered = Double.parseDouble(cell);
		}
		return filtered;
	}

	/**
	 * Checks whether a row corresponds to the reference row (e.g. the row most
	 * recently read from the first file)
	 * 
	 * @param refRow
	 *            String array that is the row being compared of the first file
	 *            read, split into columns, used as a reference
	 * @param row
	 *            String array containing the row being compared of the file
	 *            currently being read, split into columns
	 * @return a boolean value whether the rows are of the same
	 *         transform-descriptor-measure
	 */
	private static boolean properlyPaired(String[] refRow, String[] row) {
		if (refRow == null
				|| (row[TRANS_COL].equalsIgnoreCase(refRow[TRANS_COL])
						&& row[MEASURE_COL]
								.equalsIgnoreCase(refRow[MEASURE_COL]) && row[DESCRIPTOR_COL]
							.equalsIgnoreCase(refRow[DESCRIPTOR_COL])))
			return true;
		else {

			System.out
					.print("Spreadsheet rows are not identical across spreadsheets.");
			return false;
		}
	}

	// Debugging tool that converts a string array to a string
	public static String arrToString(String[] arr) {
		String res = "";
		for (int i = 0; i < arr.length; i++) {
			res = res + " " + arr[i];
		}
		return res;
	}

}
