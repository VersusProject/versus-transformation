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

import Utils
from pylab import *

def printOriginalToOriginalResultsToConsole(data_dir, col_data):
	""" Print the mean of the original to original 
		image comparison results to the console in a python
		dictionary format for use in computing the relative 
		standard deviations

		Args:
			data_dir: directory containing csv files
			col_data: the column within csv files containing data
	"""
	# np.seterr(all ='ignore')
	for file in data_dir:
		results = Utils.getResultData(file, col_data)
		key = Utils.getFilename(file)
		mu = mean(results)
		rounded_mu = round(mu * 10000.0) / 10000

		print "\"%s\" : %.3f," % (key, rounded_mu)

def printRelativeStdDevToConsole(data_dir, col_data):
	""" Print only the relative standard deviation results to
		the console. For similarity and disimilarity metrics the
		results should be the same.

		Args:
			data_dir: directory containing csv files
			col_data: the column within csv files containing data
	"""
	
	for file in data_dir:
		results = Utils.getResultData(file, col_data)
		key = Utils.getFilename(file)
		relative = Utils.orig_to_orig_results.get(key)
		rel_std_dev = Utils.std_relative(results, relative)

		print "%s, %f" % (key, rel_std_dev)

def printTotalNumOfComparisonsToConsole(data_dir, col_data):
	""" Print only the relative standard deviation results to
		the console. For similarity and disimilarity metrics the
		results should be the same.

		Args:
			data_dir: directory containing csv files
			col_data: the column within csv files containing data
	"""
	
	for file in data_dir:
		results = Utils.getResultData(file, col_data)
		key = Utils.getFilename(file)

		total_num_of_comparisons = len(results)

		print "%s, %f" % (key, total_num_of_comparisons)



def printRelativeStdDevPlotValuesToConsole(data_dir, col_data):
	""" Print relative standard deviation to console

		Disimilarity and similarity metrics should have the same value

		Args:
			data_dir: directory containing csv files
			col_data: the column within csv files containing data
	"""
	
	for file in data_dir:
		results = Utils.getResultData(file, col_data)
		key = Utils.getFilename(file)
		relative = Utils.orig_to_orig_results.get(key)
		rel_std_dev = Utils.std_relative(results, relative)

		print "\"%s\" : %e," % (key, rel_std_dev)

def printRelativeStdDevForSyntheticDataToConsole(data_dir, col_data):
	""" Print relative standard deviation with plot check
		results to console for a sanity check

		Check is used during plotting of the red line in synthetic graphs
		to correctly calculate the position of the red line within
		the synthetic metric results.

		Args:
			data_dir: directory containing csv files
			col_data: the column within csv files containing data
	"""
	
	for file in data_dir:
		results = Utils.getResultData(file, col_data)
		key = Utils.getFilename(file)
		relative = Utils.orig_to_orig_results.get(key)
		rel_std_dev = Utils.std_relative(results, relative)

		min_val = min(results)
		max_val = max(results)

		# Plotting check for relative standard deviation
		v1 = relative + rel_std_dev
		v2 = relative - rel_std_dev

		# Check to make sure the rel std dev is within the metric results
		if (v1 <= max_val and v1 >= min_val):
			v = v1
		else:
			v = v2

		print "\"%s\" : %e," % (key, v)