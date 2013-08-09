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
Used after: regression_analysis_functions_run
    THAT IMPORTS: regression_analysis

Part of the workflow that analyzed the fitness of different types of 
regression for each transform-feature-measure combination.

A Python program combines all the spreadsheets (e.g. ft, glcm, etc)
 for each subfolder (e.g. intensity-NoiseGauss-FourColorBox) into 
 one spreadsheet and to keep only the best regression function for 
 each transform-feature-measure triplet.
"""

from regression_multireg import dir_find 
import os
import re

out_folder_name = "analysis"
    
def main(*argv):
        dir = 'C:\Users\cng1\Documents\TestImages\Simulated_Data_v2'
        rank_dir = 'C:\Users\cng1\Documents\TestImages\Simulated_Data_rank'
        best_analysis(dir)
        
        
        
        

# input: path to the directory that contains the directories with the transforms regression analysis
def best_analysis(main_dir):
    # All dirs with the desired csv files end with "Versus_graphs"
    # graph_dirs is a list of names of the sub directories
    graph_dirs = dir_find(main_dir, "Versus_graphs")
    # All csv have the same name in each graph dir.
    # reg_files is a list of file names
    reg_files = dir_find(main_dir + os.sep + graph_dirs[0], "csv")
    out_dir = main_dir + os.sep + out_folder_name
    if not os.path.exists(out_dir): 
        os.makedirs(out_dir)
        print "madedir"
    
    for file in reg_files:
        out_file = open(out_dir + os.sep + file[:-9] + "-best.csv", 'w')
        write_header(out_file)
        for dir in graph_dirs:
            path = main_dir + os.sep + dir + os.sep + file
            read_file = open(path)
            count = 0
            names = []
            for line in read_file:
                if count == 0:
                    line = line.split(",")
                    names = line
                else:
                    reg_info = find_best(line, count, names)
                    write_line(out_file, path, reg_info)
                count += 1
            # Commented out to remove blank lines between transform groups
            # out_file.write("\n")
        
        
# Writes the header line for each csv file            
def write_header(out_file):
    out_file.write("Transform,Descriptor,Measure,Best Regression Name,Regression Parameters,Regression Goodness of Fit,Best Fit Linear Slope\n")

# Find name, parameters and fit of the best fitting regression 
def find_best(line, count, names):
    max_col = 0
    max_fit = 0
    line = line.split(",")
    
    lin_slope = get_lin_slope(line, names)
    
    # Find the column with the highest fitness
    for i in range(2, len(line), 2):
        if (float(line[i]) > max_fit):
            max_fit = float(line[i])
            max_col = i

    # If the column found than 1%
    # more accurate than the linear fit, use linear instead
        for i in range(2, len(line), 2):
            if "Linear" in names[i]:
                if (max_fit - float(line[i]) < 0.01):
                    max_col = i
                    max_fit = float(line[max_col])
                    
                    
    # If the column found is quadratic, if it is less than 1%
    # more accurate than another fit, use the highest other fit
    difference = 10  # Arbitrary large number
    if "Quadratic" in names[max_col]:
        for i in range(2, len(line), 2):
            if (max_fit - float(line[i]) < 0.01) and ("Quadratic" not in names[i]):
                if (max_fit - float(line[i]) < difference):
                    max_col = i
                    difference = max_fit - float(line[i])
            
            
            
    if (max_col > 0):
        # Regular Expression to get the name of the regression and it's equation from the header of the column of the max fitness
        name_extractor = re.compile('(.*) Fitness')
        reg_name = name_extractor.search(names[max_col]).groups()[0]

        reg_params = line[max_col - 1]
        reg_fit = str(line[max_col])
        reg_fit = re.sub('[\r\n]', '', reg_fit)
    else:
        reg_name = "No Regression"
        reg_params = "None"
        reg_fit = "None"
    descriptor_measure = line[0]
    # Descriptor is the first word in the descriptor-measure chunk
    descriptor = descriptor_measure.split(" ")[0]
    # Measure is everything else
    measure = " ".join(descriptor_measure.split(" ")[1:])
    return (reg_name, reg_params, reg_fit, descriptor, measure, lin_slope)

def get_lin_slope(line, names):
    # Get the parameters for the linear fit slope
    for i in range(2, len(line), 2):
        if "Linear" in names[i]:
            params = line[i - 1]
            slope_ex = re.compile('\A\S*?=([0-9Ee.\-]*);\S*?=')
            slope = slope_ex.search(params).groups()[0]
            # slope_num = float(slope)
            
    return slope

# Write a line of the resulting csv file, see write_header for contents
def write_line(out_file, in_file, reg_info):    
    # Regular Expressions to find the name of the transform from the folder the csv is in
    trans_extractor = re.compile('Simulated_Data_([a-zA-Z]*)_Versus')
    trans = trans_extractor.search(in_file).groups()[0]

    # Writing all the information to a row in the output csv
    reg_name, reg_params, reg_fit, descriptor, measure, lin_slope = reg_info
    
    
    out_file.write(trans + "," + descriptor + "," + measure + "," + reg_name + "," + reg_params + "," + reg_fit + "," + lin_slope + "\n")
    
    
    

if __name__ == '__main__':
	main()


        
        
        
