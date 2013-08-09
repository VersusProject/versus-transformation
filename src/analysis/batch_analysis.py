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
Uses: residuals_analysis_functions_run.py
    Depending on what that imports, different csv's and graphs 
    will be generated

Summary: Creates csvs and graphs with multiple regressions on a set of folders

Description:
Calls residuals_analysis_functions_run for each of the subfolders present in the folders in "in_dir_list"
and outputs graphs (similarity of a transform-feature-measure triplet vs amount of change, 
with multiple regression lines)and csv files in a folder with the same name and "_graphs" appended, 
which needs an identical subfolder structure as the input folder  

by: Cynthia Gan (cng1)

"""
import regression_analysis_functions_run as analysis_run
import os
import re

def analyze_subfolders():
    # List of folders to call the analysis function on
    """
    in_dir_list = ["C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_ft_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_glcm_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_no_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_sobel_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_gabor_Versus"]
    
    in_dir_rank_list = ["C:\Users\cng1\Documents\TestImages\Simulated_Data_Rank\Simulated_Data_ft_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_Rank\Simulated_Data_glcm_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_Rank\Simulated_Data_no_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_Rank\Simulated_Data_sobel_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_Rank\Simulated_Data_gabor_Versus"]
    
    color_in_dir_list = [    "C:\Users\cng1\Documents\TestImages\Simulated_Data_color\Simulated_Data_color_gray_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_color\Simulated_Data_color_hsv_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_color\Simulated_Data_color_no_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_color\Simulated_Data_color_pca_Versus",
    "C:\Users\cng1\Documents\TestImages\Simulated_Data_color\Simulated_Data_color_yuv_Versus"
    ]
    """
    
    out_suffix = "_lin"
    
    bulk_dir_list = [
"C:\Users\Cindy\Documents\Dropbox\School 2013-2014\Summer Internship 2013\SimData\\norm_Versus", 

#"C:\Users\Cindy\Documents\Dropbox\School 2013-2014\Summer Internship 2013\SimData\\rank_Versus", 

#"C:\Users\Cindy\Documents\Dropbox\School 2013-2014\Summer Internship 2013\SimData\color_Versus"
]

    for dir in bulk_dir_list:
        dir_list = dir_find(dir, "_Versus")
        
        for in_dir in dir_list:
            sub_dirs = os.listdir(in_dir)
            out_dir = in_dir + out_suffix
            for sub_dir in sub_dirs:
                if os.path.isdir(os.path.join(in_dir, sub_dir)):
                    out_path = os.path.join(out_dir, sub_dir)
                    in_path = os.path.join(in_dir, sub_dir)
                    analysis_run.main(in_path, out_path)
    

#find all csv files in directory
def dir_find(root, suffix):
    return [root + os.sep + file for file in os.listdir(root) if (re.match('(.*%s)$'%suffix, file) is not None)]    
    
if __name__ == '__main__':
    analyze_subfolders()