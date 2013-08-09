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
Uses: graph_compare_regression

Summary: 
    -make plot pngs feature-measure combination with respect to each type of change
    -each plot has multiple lines, with each line representing a transformation
    -Generates a csv file for each type of change, with the linear regressions 
    and correlation coefficient for each transform for each feature and measure
    
Details:
    Assumes a folder structure that goes
    [] mark fields, easily converted to inputs.
    () naming conventions 
    [root] -> folders organized by transform with [in_suffix] 
        (Simulated_Data_*_[in_suffix]) where * is the transformation name 
    -> exclusively folders organized by type of change
    -> csv's (feature_measure_separated_by_underscores.csv) with Versus values
        
    
    as many transforms/types of change/feature-measures as 
    there are files and folders, who's names are parsed to label 
    the spreadsheets and graphs    
    

by: Cynthia Gan (cng1)

"""

from sys import argv
from sys import exit
import re
import gc
import os

do_graphs = True #Boolean that turns graph generation on or off
#file that contains the data folders organized by transform with the in_suffix
root = "C:\\Users\\Cindy\\Documents\\Dropbox\\School 2013-2014\\Summer Internship 2013\\SimData\\norm_Versus"
output_folder = "comp_trans" #puts graphs and csvs in this folder in root
in_suffix = "_Versus" #Searchs root for folders ending in this for data
out_suffix = "-comp.csv" #appends to the types of change as output csv file names


from pylab import *
import matplotlib.pyplot as plt

def compare_trans():
    
    #Create the output folder if it doesn't exist
    output_path = root + os.sep + output_folder
    if not os.path.exists(output_path): 
        os.makedirs(output_path)

    #List of all names of transformed directories
    trans_list = dir_find(root, in_suffix)
    #List of all names of change folders names, assuming all are the same as the first transform folder
    change_list = dir_find(root + os.sep + trans_list[0], "")
    #List of all metric Versus csv files names, assuming all are the same as the first transform folder
    metric_list = dir_find(root + os.sep + trans_list[0] + os.sep + change_list[0], ".csv")

    for change_type in change_list:
        #Create csv for the type of change, overwrite if exist and write header
        out_file = open(output_path + os.sep + change_type + out_suffix, 'w')
        write_header(trans_list, out_file)
        results = {}
        
        #Iterate over all feature-measure combinations
        for csv_file in metric_list:
            if (do_graphs):
                #Start figure
                fig = plt.figure()
                ax1 = fig.add_subplot(111)
                path = os.sep.join([root, trans_list[0], change_type, csv_file])
                #Open a file temporarily to get whether the metric measures similarity or dissimilarity 
                temp_read = open(path)
                #Parse out plot information
                plot_opts = get_plot_opts(change_type, csv_file, output_path, temp_read, out_suffix)
                temp_read.close()
                plot_labels = plot_opts[0]
                save_options = plot_opts[1]
                ax1.set_xlabel(plot_labels['xLabel'],fontsize=16)
                ax1.set_ylabel(plot_labels['yLabel'],fontsize=16)
                ax1.set_title(plot_labels['title'],fontsize=18, horizontalalignment='center', x = 0.8, y = 1.02)
            
            #Create a list of opened Versus csv files, one per tansform  
            #given a type of change and feature-measure combo
            reader_list = []    
            for trans in trans_list:
                path = os.sep.join([root, trans, change_type, csv_file])
                reader_list.append(open(path))
                
            #Initialize more things for the graphs
            if (do_graphs):
                index = 0
                scatter_colors = ['b', 'g', 'r', 'c', 'm', 'y', 'k', 'w']
                #The possible colors of the regression lines, will be cycled through,
                #all dotted lines, respectively: blue, green, red, cyan, magenta, yellow, black, white
                line_color = ['b--', 'g--', 'r--', 'c--', 'm--', 'y--', 'k--', 'w--']
                # Heading for text box
                r_str = "Correlation Coefficient (R): \n\n"
                
            #For each of the transforms for the feature-measure, 
            #Calculate the linear regression, correlation coefficient
            #and add points and a line to the plot
            for trans, reader in zip(trans_list, reader_list):
                #Get the x-y pairs, sorted
                pairs = get_pairs(parse_csv(reader))
                series = pairs.values()
                series = sorted(series, key=lambda pair: pair[0])
                
                #Parse out the name of the transform, and capitalize
                trans_name = str.capitalize(re.match('Simulated_Data_(.*)_Versus', trans).groups()[0])

                if (do_graphs):
                    #plot the x and y values in a scatter plot on the plot just created
                    ax1.scatter([x for x,y in series], [y for x,y in series], s=30, c=scatter_colors[index], marker="s", label=trans_name + " Transform Similarity Results")

                #plot a linear regression line
                params, fit_y = lin_reg(series)
                if (do_graphs):
                    plt.plot([x for x,y in series], fit_y, line_color[index], label = trans_name + " Transform Linear Fit")


                #calculate the coefficient of determination for each regression and include it in the textbox
                #gof = coef_of_det(fit_y, [y for x,y in series])
                corr = calc_pearson(series)
                if (do_graphs):
                    r_str += '%s:  %e\n' % (trans_name + " Transform", corr) 
                    #Go to the next color
                    index += 1
                results[trans_name] = (params, corr)

            if (do_graphs):
                #Arranges items on graph (e.g. text box, legend, title, etc.), saves it to a file and clear the memory of it
                #Set location of plot and the y axis range
                box = ax1.get_position()
                ax1.set_position([box.x0-box.width*0.05, box.y0, box.width*0.7, box.height])
                
                #Set locations of legend and textbox
                ax1.legend(loc='center left', bbox_to_anchor=(1.05, 0.6), fancybox=True, shadow=True, ncol=1, fontsize = 13)
                props = dict(boxstyle='round', facecolor='gray', alpha=0.3)
                ax1.text(1.05, 0.4, r_str, transform=ax1.transAxes, fontsize=13, horizontalalignment='left',
                    verticalalignment='top', bbox=props)

                #Save figure
                save_options = plot_opts[1]
                fig.set_size_inches(15,11)
                fig.savefig(save_options["dir"] + os.sep + save_options["filename"] + save_options["file_format"], bbox_inches=0, dpi=gcf().dpi)

                #Close graph and clear memory
                fig.clf()
                plt.close()
                gc.collect()
                       
            write_row(csv_file, results, out_file)
            print change_type + "\n\t" + csv_file
        out_file.close()
                

def write_row(csv_file, results, out_file):
    #Sort the result dictionary by key, which is the transform name
    sorted_results = sorted(results.items(), key = lambda item:item[0]) 
    out_string_body = ""
    for trans, (params, gof) in sorted_results:
        out_string_body += ",".join([make_param_string(params), str(gof)]) + ","

    #Descriptor is the first word in the descriptor-measure chunk
    descriptor = csv_file.split("_")[0]
    #Measure is everything else before the period
    measure = " ".join((csv_file.split(".")[0]).split("_")[1:])
    out_file.write(",".join([descriptor, measure, out_string_body]) + "\n")
    
#Format the parameters for the linear regressions into strings
def make_param_string(params):
    param_string_list = ";".join(["%s=%s" % (name, coef) for name, coef in params])
    return param_string_list

# write the header for the output file
def write_header(trans_list, out_file):
    trans_name_list = [str.capitalize(re.match('Simulated_Data_(.*)_Versus', trans).groups()[0]) for trans in trans_list]
    sorted_trans_name = sorted(trans_name_list) #ensure that all transforms are in alphabetical order so things line up
    trans_string = ",".join([trans_name + " Linear Reg," + trans_name + " Correlation" for trans_name in sorted_trans_name])
    out_file.write("Feature,Measure,%s\n" % trans_string)

#Calculate a linear regression, and return tuples with 
#(name of the parameter, its value)
def lin_reg(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 1))
        fit_y = np.lib.polyval(rounded_coef, [a for a,b in pairs])
        
        #packaging the coefficients as parameters tuples
        param_names = ["a", "b"]
        params = zip(param_names, rounded_coef)
        
        return (params, fit_y)

#Return if a string is a valid float
def is_valid(n):
    if (n != float('nan')) and (n != float('inf')):
        return True
    else:
        return False
        
#replaces empty lists with one element lists containing float('nan')
def empty_filter(num_list):
    if (len(num_list) <= 0):
        return [float('nan')]
    else:
        return num_list

#rounds all the values in an array smaller than 10E-15 to 0  
def round(coef):
    if ('nan' not in coef and 'inf' not in coef):
        rounded_coef = [num if abs(num) > 10E-15 else 0 for num in coef]
    return rounded_coef
        


#create a tuple containing necessary label and save information for a plot
# (name of change, name of metric csv, path to the superdirectory with the folder that will contain the graph) -> (dict of plot labels, dict of plot save options)  
def get_plot_opts(change_type, metric, output_path, file, out_suffix):
    labels = {}
    #parse the type of change and metric for the title and x-axis
    if (len(change_type.split("-")) < 3):
        processed_change = "Varying " + change_type.replace("-", " ").title() + "\n"
    else:
        processed_change = "Varying " + change_type.split("-")[0].title() + " (" + ", ".join(change_type.split("-")[1:]).title() + ")\n"
    processed_metric = metric.split("_")[0] + "-based " + " ".join((metric.split(".")[0]).split("_")[1:])
    labels['title'] =  processed_change + processed_metric
    labels['xLabel'] = processed_change

    #check whether the metric is a similarity metric or a dissimilarity metric
    #Use as y axis
    for n1, n2, num in [line.split(',') for line in file]:
        if (n1 == n2):
            if(float(num) == 0 or float(num) == -999):
                labels['yLabel'] = "Dissimilarity Value"
            else:
                labels['yLabel'] = "Similarity Value "
        
    save_options = {}
    #Organize each plot into subfolders named by type of change
    #Make them if they do not exist
    out_dir = output_path + os.sep + change_type
    save_options["dir"] = out_dir
    if not os.path.exists(out_dir): 
        os.makedirs(out_dir)
    save_options["filename"] = metric.split(".")[0] + out_suffix
    save_options["file_format"] = ".png"
    
    return (labels, save_options)


#Calculate the correlation coefficient for a list of tuples
def calc_pearson(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        corr = np.corrcoef(x,y)[0,1]

        return corr

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