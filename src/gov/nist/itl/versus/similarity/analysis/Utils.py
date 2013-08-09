"""
 This software was developed at the National Institute of Standards and
 Technology by employees of the Federal Government in the course of
 their official duties. Pursuant to title 17 Section 105 of the United
 States Code this software is not subject to copyright protection and is
 in the public domain. This software is an experimental system. NIST assumes
 no responsibility whatsoever for its use by other parties, and makes no
 guarantees, expressed or implied, about its quality, reliability, or
 any other characteristic. We would appreciate acknowledgment if the
 software is used.
 
 
    Date: 08-09-2013
"""

"""
Copied from the NIST isg-forensic-science project
Used by PlotLineOfBestFit
"""

'''
Created on Mar 15, 2013

@author: dan1
'''
import os
from pylab import *


#===============================================================================
# I/O helpers
#===============================================================================
def getFilesInDirectory(directory):
	filepaths = []
	filenames = os.listdir(directory)
	for filename in filenames:
		if os.path.isdir(os.path.join(directory, filename)):
			continue
		else:
			if filename.endswith(".csv"):
				filepaths.append(os.path.join(directory, filename))
	return filepaths

def getFilesOfTypeInDir(directory, file_ext_filter):
	filepaths = []
	for root, dirs, files in os.walk(directory):
		for f in files:
		 	if f.endswith(file_ext_filter):
		 		filepath = os.path.join(root, f)
		 		filepaths.append(filepath)
	return filepaths

def getTitleFromFilename(filepath):
	filename = os.path.basename(filepath)
	root = os.path.splitext(filename) # strip file extension
	f = root[0].replace("_", " ")
	return f

def getFilename(filepath):
	filename = os.path.basename(filepath)
	root = os.path.splitext(filename) # strip file extension
	return root[0]

def getBaseMetricName(string):
	string = string.replace("_", " ")
	if ("Histogram" in string):
		string = string.replace("Histogram", "")
	if ("Measure" in string):
		string = string.replace("Measure", "")
	return string



#===============================================================================
# Arguments:
# filepath = String
# column_num = int // the column where the numbers reside
#===============================================================================
def getResultData(filepath, column_num):
	results = []

	with open(filepath, "rb") as f:
		for line in f:
			result = line.split(",")
			try:				
				if math.isnan(float(result[column_num])):
					continue
				if math.isinf(float(result[column_num])): #or float(result[column_num]) > 1.7e+200:
					continue
				results.append(float(result[column_num]))
			except:
				continue
		f.close()

#    floats = map(float, results)
#    'string %d number' % 1
#    print 'SUM: %f' % sum(floats)
#    print 'AVG: %f' % numpy.average(floats)
#    print 'STD: %f' % numpy.std(floats)
#    print 'Max: %f' % numpy.max(floats)
#    print 'Min: %f' % numpy.min(floats)
#    print '\n'

	return map(float, results)



#===============================================================================
# Original to Original Simlarity Result Lookup Table
#===============================================================================
orig_to_orig_results = {
						"Histogram_Additive_Sym_Chi_Sqrd_Measure" : 0.000,
"Histogram_Average_Distance_Measure" : 0.000,
"Histogram_Bhattacharyya_Distance_Measure" : 0.000,
"Histogram_Canberra_Measure" : 0.000,
"Histogram_ChebyshevLInf_Measure" : 0.000,
"Histogram_City_Block_Measure" : 0.000,
"Histogram_Clark_Measure" : 0.000,
"Histogram_Cosine_Measure" : 1.000,
"Histogram_Czekanowski_D_Measure" : 0.000,
"Histogram_Czekanowski_Measure" : 1.000,
"Histogram_Dice_D_Measure" : 0.000,
"Histogram_Dice_Measure" : 1.000,
"Histogram_Divergence_Measure" : 0.000,
"Histogram_EuclideanL2_Distance" : 0.000,
"Histogram_Fidelity_Measure" : 1.000,
"Histogram_Gower_Measure" : 0.000,
"Histogram_Harmonic_Mean_Measure" : 1.000,
"Histogram_Hellinger_Measure" : 0.000,
"Histogram_Inner_Product_Measure" : 0.050,
"Histogram_Intersection_D_Measure" : 0.000,
"Histogram_Intersection_Measure" : 1.000,
"Histogram_Jaccard_D_Measure" : 0.000,
"Histogram_Jaccard_Measure" : 1.000,
"Histogram_Jeffreys_Measure" : 0.000,
"Histogram_Jensen_Difference_Measure" : 0.000,
"Histogram_Jensen_Shannon_Measure" : 0.000,
"Histogram_Kulczynski_Recip_Measure" : 0.000,
"Histogram_Kulczynski_S_Measure" : 1.797693e+308,
"Histogram_Kullback_Leibler_Measure" : 0.000,
"Histogram_Kumar_Johnson_Distance_Measure" : 0.000,
"Histogram_K_Divergence_Measure" : 0.000,
"Histogram_Lorentzian_Measure" : 0.000,
"Histogram_Matusita_I1_Measure" : 0.000,
"Histogram_Matusita_I2_Measure" : 0.000,
"Histogram_Minkowski_P3_Measure" : 0.000,
"Histogram_Motyka_D_Measure" : 0.500,
"Histogram_Motyka_Measure" : 0.500,
"Histogram_Neyman_ChiSquared_Measure" : 0.000,
"Histogram_Pearson_ChiSquared_Measure" : 0.000,
"Histogram_Probabilistic_Symmetric_ChiSquared_Measure" : 0.000,
"Histogram_Ruzicka_Measure" : 1.000,
"Histogram_Soergel_Measure" : 0.000,
"Histogram_Sorensen_Measure" : 0.000,
"Histogram_Squared_Chi_Squared_Measure" : 0.000,
"Histogram_Squared_Chord_D_Measure" : 0.000,
"Histogram_Squared_Chord_Measure" : 1.000,
"Histogram_Squared_Euclidean_Measure" : 0.000,
"Histogram_Taneja_Diff_Measure" : 0.000,
"Histogram_Tanimoto_Measure" : 0.000,
"Histogram_Topsoe_Measure" : 0.000,
"Histogram_Wave_Hedges_Measure" : 0.000,
"Pixel_Adjusted_Rand_Index_Measure" : 1.000,
"Pixel_Chessboard_Distance_Measure" : 0.000,
"Pixel_Dice_Measure" : 1.000,
"Pixel_Jaccard_Measure" : 1.000,
"Pixel_Manhattan_Distance_Measure" : 0.000,
"Pixel_Rand_Index_Measure" : 1.000,
"Pixel_Structural_Similarity_Measure" : 1.000,
"Pixel_Total_Error_Eval_Measure" : 1.000,
"Pixel_Total_Error_Test_Measure" : 1.000
					}

#===============================================================================
# Relative Standard Deviation of Simlarity Results - Lookup Table
# Computed from 127 image comaprisons from training dataset and used to
# compute good and bad metrics from synthetic eroded, dilated, and threshold
# images. Bad metric indications are where synthetic data results are not close
# to the relative standard deviation results.
#===============================================================================
rel_std_dev_results = {
"Histogram_Additive_Sym_Chi_Sqrd_Measure" : 1.797693e+308,
"Histogram_Average_Distance_Measure" : 2.408664e+01,
"Histogram_Bhattacharyya_Distance_Measure" : 1.475478e+00,
"Histogram_Canberra_Measure" : 1.689122e+02,
"Histogram_ChebyshevLInf_Measure" : 1.840926e-01,
"Histogram_City_Block_Measure" : 1.452159e+00,
"Histogram_Clark_Measure" : 1.185608e+01,
"Histogram_Cosine_Measure" : 7.535944e-01,
"Histogram_Czekanowski_D_Measure" : 7.260794e-01,
"Histogram_Czekanowski_Measure" : 7.260794e-01,
"Histogram_Dice_D_Measure" : 7.655797e-01,
"Histogram_Dice_Measure" : 7.655797e-01,
"Histogram_Divergence_Measure" : 3.042210e+02,
"Histogram_EuclideanL2_Distance" : 2.788220e-01,
"Histogram_Fidelity_Measure" : 5.732159e-01,
"Histogram_Gower_Measure" : 5.672495e-03,
"Histogram_Harmonic_Mean_Measure" : 6.547354e-01,
"Histogram_Hellinger_Measure" : 1.407533e+00,
"Histogram_Inner_Product_Measure" : 9.554816e-02,
"Histogram_Intersection_D_Measure" : 7.260794e-01,
"Histogram_Intersection_Measure" : 7.260794e-01,
"Histogram_Jaccard_D_Measure" : 8.230321e-01,
"Histogram_Jaccard_Measure" : 8.230321e-01,
"Histogram_Jeffreys_Measure" : 2.356198e+02,
"Histogram_Jensen_Difference_Measure" : 4.318149e-01,
"Histogram_Jensen_Shannon_Measure" : 4.318149e-01,
"Histogram_Kulczynski_Recip_Measure" : 1.609185e+03,
"Histogram_Kulczynski_S_Measure" : 1.797693e+308,
"Histogram_Kullback_Leibler_Measure" : 2.340118e+02,
"Histogram_Kumar_Johnson_Distance_Measure" : 1.797693e+308,
"Histogram_K_Divergence_Measure" : 4.341384e-01,
"Histogram_Lorentzian_Measure" : 1.412336e+00,
"Histogram_Matusita_I1_Measure" : 9.952761e-01,
"Histogram_Matusita_I2_Measure" : 9.952761e-01,
"Histogram_Minkowski_P3_Measure" : 3.902280e-01,
"Histogram_Motyka_D_Measure" : 3.630397e-01,
"Histogram_Motyka_Measure" : 3.630397e-01,
"Histogram_Neyman_ChiSquared_Measure" : 1.797693e+308,
"Histogram_Pearson_ChiSquared_Measure" : 1.797693e+308,
"Histogram_Probabilistic_Symmetric_ChiSquared_Measure" : 2.618942e+00,
"Histogram_Ruzicka_Measure" : 8.054942e-01,
"Histogram_Soergel_Measure" : 8.054942e-01,
"Histogram_Sorensen_Measure" : 7.260794e-01,
"Histogram_Squared_Chi_Squared_Measure" : 1.309471e+00,
"Histogram_Squared_Chord_D_Measure" : 1.146432e+00,
"Histogram_Squared_Chord_Measure" : 8.127688e-01,
"Histogram_Squared_Euclidean_Measure" : 1.321227e-01,
"Histogram_Taneja_Diff_Measure" : 1.949315e+02,
"Histogram_Tanimoto_Measure" : 8.054942e-01,
"Histogram_Topsoe_Measure" : 8.636298e-01,
"Histogram_Wave_Hedges_Measure" : 1.808916e+02,
"Pixel_Adjusted_Rand_Index_Measure" : 9.064838e-01,
"Pixel_Chessboard_Distance_Measure" : 1.990499e+02,
"Pixel_Dice_Measure" : 2.273380e-02,
"Pixel_Jaccard_Measure" : 4.141596e-02,
"Pixel_Manhattan_Distance_Measure" : 8.526090e+04,
"Pixel_Rand_Index_Measure" : 1.113197e-01,
"Pixel_Structural_Similarity_Measure" : 7.315386e-01,
"Pixel_Total_Error_Eval_Measure" : 7.621861e-03,
"Pixel_Total_Error_Test_Measure" : 3.911135e-02

}


rel_std_for_synthetic = {
"Histogram_Additive_Sym_Chi_Sqrd_Measure" : 1.797693e+308,
"Histogram_Average_Distance_Measure" : 2.408664e+01,
"Histogram_Bhattacharyya_Distance_Measure" : 1.475478e+00,
"Histogram_Canberra_Measure" : 1.689122e+02,
"Histogram_ChebyshevLInf_Measure" : 1.840926e-01,
"Histogram_City_Block_Measure" : 1.452159e+00,
"Histogram_Clark_Measure" : 1.185608e+01,
"Histogram_Cosine_Measure" : 2.464056e-01,
"Histogram_Czekanowski_D_Measure" : 7.260794e-01,
"Histogram_Czekanowski_Measure" : 2.739206e-01,
"Histogram_Dice_D_Measure" : 7.655797e-01,
"Histogram_Dice_Measure" : 2.344203e-01,
"Histogram_Divergence_Measure" : 3.042210e+02,
"Histogram_EuclideanL2_Distance" : 2.788220e-01,
"Histogram_Fidelity_Measure" : 4.267841e-01,
"Histogram_Gower_Measure" : 5.672495e-03,
"Histogram_Harmonic_Mean_Measure" : 3.452646e-01,
"Histogram_Hellinger_Measure" : 1.407533e+00,
"Histogram_Inner_Product_Measure" : 1.455482e-01,
"Histogram_Intersection_D_Measure" : 7.260794e-01,
"Histogram_Intersection_Measure" : 2.739206e-01,
"Histogram_Jaccard_D_Measure" : 8.230321e-01,
"Histogram_Jaccard_Measure" : 1.769679e-01,
"Histogram_Jeffreys_Measure" : 1.299342e+02,
"Histogram_Jensen_Difference_Measure" : 4.318149e-01,
"Histogram_Jensen_Shannon_Measure" : 4.318149e-01,
"Histogram_Kulczynski_Recip_Measure" : 1.609185e+03,
"Histogram_Kulczynski_S_Measure" : 0.000000e+00,
"Histogram_Kullback_Leibler_Measure" : 2.340118e+02,
"Histogram_Kumar_Johnson_Distance_Measure" : 1.797693e+308,
"Histogram_K_Divergence_Measure" : 4.341384e-01,
"Histogram_Lorentzian_Measure" : 1.412336e+00,
"Histogram_Matusita_I1_Measure" : 9.952761e-01,
"Histogram_Matusita_I2_Measure" : 9.952761e-01,
"Histogram_Minkowski_P3_Measure" : 3.902280e-01,
"Histogram_Motyka_D_Measure" : 8.630397e-01,
"Histogram_Motyka_Measure" : 1.369603e-01,
"Histogram_Neyman_ChiSquared_Measure" : 1.797693e+308,
"Histogram_Pearson_ChiSquared_Measure" : 1.797693e+308,
"Histogram_Probabilistic_Symmetric_ChiSquared_Measure" : 2.618942e+00,
"Histogram_Ruzicka_Measure" : 1.945058e-01,
"Histogram_Soergel_Measure" : 8.054942e-01,
"Histogram_Sorensen_Measure" : 7.260794e-01,
"Histogram_Squared_Chi_Squared_Measure" : 1.309471e+00,
"Histogram_Squared_Chord_D_Measure" : 1.146432e+00,
"Histogram_Squared_Chord_Measure" : 1.872312e-01,
"Histogram_Squared_Euclidean_Measure" : 1.321227e-01,
"Histogram_Taneja_Diff_Measure" : 1.949315e+02,
"Histogram_Tanimoto_Measure" : 8.054942e-01,
"Histogram_Topsoe_Measure" : 8.636298e-01,
"Histogram_Wave_Hedges_Measure" : 1.808916e+02,
"Pixel_Adjusted_Rand_Index_Measure" : 9.351620e-02,
"Pixel_Chessboard_Distance_Measure" : 1.990499e+02,
"Pixel_Dice_Measure" : 9.772662e-01,
"Pixel_Jaccard_Measure" : 9.585840e-01,
"Pixel_Manhattan_Distance_Measure" : 8.526090e+04,
"Pixel_Rand_Index_Measure" : 8.886803e-01,
"Pixel_Structural_Similarity_Measure" : 2.684614e-01,
"Pixel_Total_Error_Eval_Measure" : 9.923781e-01,
"Pixel_Total_Error_Test_Measure" : 9.608887e-01
}


#===============================================================================
# MATH
#===============================================================================

# Standard Deviation Relative to Metric Result Calculation
def std_relative(dataset, relative):
	total = len(dataset)

	sq_results = []
	for data in dataset:
		if (relative > 1e+308 and data > 1e+308):
			d_sq = 0
		elif (data > 1e+308 and not relative > 1e+308 or relative > 1e+308 and not data > 1e+308):
			# print "INFO: Relative Std Dev Cases"
			return 1.797693e+308
		else:
			d_sq = (data - relative) * (data - relative)
			if (math.isinf(d_sq) ):
				# print "INFO: data %e" % data
				pass

		sq_results.append(d_sq)

	average = (sum(sq_results) / total)
	return math.sqrt( average )
