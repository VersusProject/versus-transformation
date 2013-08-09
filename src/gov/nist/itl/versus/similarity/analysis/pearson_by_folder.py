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



from sys import argv
from sys import exit
import re
import gc
import os


from pylab import *
import matplotlib.pyplot as plt

def compare_trans():
    root = "C:\\Users\\Cindy\\Documents\\Dropbox\\School 2013-2014\\Summer Internship 2013\\SimData\\norm_Versus"
    output_folder = "pearson"
    FILE_SUFFIX = "-pearson"
    output_path = root + os.sep + output_folder
    if not os.path.exists(output_path): 
        os.makedirs(output_path)

    #List of all names of transformed directories
    trans_list = dir_find(root, "_Versus")
    #List of all names of change folders, assuming all are the same as the first transform folder
    change_list = dir_find(root + os.sep + trans_list[0], "")
    #List of all metric Versus csv files, assuming all are the same as the first transform folder
    metric_list = dir_find(root + os.sep + trans_list[0] + os.sep + change_list[0], ".csv")

    for change_type in change_list:
        out_file = open(output_path + os.sep + change_type + FILE_SUFFIX + ".csv", 'w')
        write_header(trans_list, out_file)
        results = {}
        for csv_file in metric_list:

            reader_list = []    #List of open files to the csv files for each kind of transform for a set metric
            for trans in trans_list:
                path = os.sep.join([root, trans, change_type, csv_file])
                reader_list.append(open(path))
                
            for trans, reader in zip(trans_list, reader_list):
                pairs = get_pairs(parse_csv(reader))
                series = pairs.values()
                series = sorted(series, key=lambda pair: pair[0])
                trans_name = str.capitalize(re.match('Simulated_Data_(.*)_Versus', trans).groups()[0])

                #plot a linear regression line
                corr = calc_pearson(series)
                result = (trans_name, corr)

                write_row(csv_file, result, out_file)
            print change_type + "\n\t" + csv_file
        out_file.close()
                

def write_row(csv_file, result, out_file):
    #Sort the result dictionary by key, which is the transform name
    trans_name, corr = result 

    #Descriptor is the first word in the descriptor-measure chunk
    descriptor = csv_file.split("_")[0]
    #Measure is everything else before the period
    measure = " ".join((csv_file.split(".")[0]).split("_")[1:])
    out_file.write(",".join([trans_name, descriptor, measure, str(corr)]) + "\n")
    

# write the header for the output file
def write_header(trans_list, out_file):
    out_file.write("Transform,Feature,Measure,Corr\n")

def calc_pearson(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        corr = np.corrcoef(x,y)[0,1]

        return corr

def is_valid(n):
    if (n != float('nan')) and (n != float('inf')):
        return True
    else:
        return False
        
def empty_filter(num_list):
    if (len(num_list) <= 0):
        return [float('nan')]
    else:
        return num_list

def round(coef):
    if ('nan' not in coef and 'inf' not in coef):
        rounded_coef = [num if abs(num) > 10E-15 else 0 for num in coef]
    return rounded_coef
        


#create a tuple containing necessary label and save information for a plot
# (name of change, name of metric csv, path to the superdirectory with the folder that will contain the graph) -> (dict of plot labels, dict of plot save options)  
def get_plot_opts(change_type, metric, out_dir, file):
    labels = {}
    if (len(change_type.split("-")) < 3):
        processed_change = "Varying " + str.capitalize(change_type.replace("-", " "))
    else:
        processed_change = "Varying " + str.capitalize(" ".join(change_type.split("-")[:-2])) + " (" + change_type.split("-")[-1] + ")\n"
    processed_metric = metric.split("_")[0] + "-based " + " ".join((metric.split(".")[0]).split("_")[1:])
    labels['title'] =  processed_change + processed_metric
    labels['xLabel'] = processed_change

    #check whether the metric is a similarity metric or a dissimilarity metric
    for n1, n2, num in [line.split(',') for line in file]:
        if (n1 == n2):
            if(float(num) == 0 or float(num) == -999):
                labels['yLabel'] = "Dissimilarity Value"
            else:
                labels['yLabel'] = "Similarity Value "
        
    save_options = {}
    save_options["dir"] = out_dir
    if not os.path.exists(out_dir): 
        os.makedirs(out_dir)
    save_options["filename"] = metric.split(".")[0] + "-comp_trans"
    save_options["file_format"] = ".png"
    
    return (labels, save_options)


#calculate the coefficient of determination for the 
#regression using the predicted and observed y values
def coef_of_det(fit_data, y_data):
    if ("nan" in fit_data or "nan" in y_data or "inf" in fit_data or "inf" in y_data):
        print fit_data
        print y_data
        exit(0)
    vals = zip(fit_data, y_data)
    mean = sum(y_data)/len(y_data)
    ssr = sum([(a - mean)**2 for a in fit_data])
    sse = sum([(a - b)**2 for a, b in vals])
    if ((ssr + sse) != 0):
        r_2 = ssr / (ssr + sse)
    else:
        r_2 = 1
    return r_2
    
#parse the csv      
def parse_csv(file):
    return [(n1, n2, float(num)) if float(num) < 10E10 else (n1, n2, float('inf')) for n1, n2, num in [line.split(',') for line in file] if (n1 != n2)]

#get the x value of the data observation from file name    
def get_x(img_name):
    extractor = re.compile('(\d+)')
    reg = extractor.findall(img_name)
    #print('found: %s' % reg)
    return int(reg[-1]) if reg else 0.0
    
#get a mapping (reference, comparison) => (x,y) from parsed csv file
def get_pairs(parsed):
    return {(n1,n2):(get_x(n2), y) for n1, n2, y in parsed}


#find all csv files in directory
def dir_find(root, suffix):
    return [file for file in os.listdir(root) if re.match('.*%s$'%suffix, file) is not None]

if __name__ == '__main__':
    compare_trans()