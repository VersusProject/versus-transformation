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
This program generates a scatter plot with regression lines and their coefficients of determination 
for every versus formatted input csv file in a subfolder, and then one csv file per entire subfolder containing
information about each regression performed on each input csv file.
   
Identical to residuals_analysis except it calculates the coefficient 
of determination instead of the sum of squared residuals.

Written by Cynthia Gan, with significant contributions by
Anson Rosenthal, David Nimorwicz
"""

from sys import argv
import re
import gc
import os

from pylab import *
import matplotlib.pyplot as plt

#Main Entry Point    
def run_analysis(funcs, csv_dir, output_path):
    data = dir_find(csv_dir, '.csv')
    if not os.path.exists(output_path): 
        os.makedirs(output_path)
    out_file = open(output_path + "-regs.csv", 'w')
    write_header(funcs, out_file)
    for filename in data:
        in_file = open(csv_dir + os.sep + filename)
        line = in_file.readline()
        in_file.seek(0)
        if (len(line.split(",")) >= 3):
            type = is_similarity_measure(in_file)
            plot_options = plot_opts(csv_dir, filename, output_path, type)
            reg_vals = single_regression(funcs, csv_dir + os.sep + filename, plot_options)
            write_row(reg_vals, filename, out_file)
    out_file.close()
    in_file.close()


#get a mapping (reference, comparison) => (x,y) from parsed csv file
#runs a all regression functions on a single csv file and creates graphs for each, returns a mapping 
#   func name -> (parameter names, parameter values, sum of squared residuals)            
def single_regression(funcs, file, plot_opts):
    data = open(file)
    pairs = get_pairs(parse_csv(data))
    data.close()
    
#Creates a matplotlib plot and sets the axes and title appropriately
    fig = plt.figure()
    ax1 = fig.add_subplot(111)
    plot_labels = plot_opts[0]
    save_options = plot_opts[1]
    ax1.set_xlabel(plot_labels['xLabel'],fontsize=13)
    ax1.set_ylabel(plot_labels['yLabel'],fontsize=13)
    ax1.set_title(plot_labels['title'],fontsize=16, horizontalalignment='center', x = 0.8, y = 0.985)
    
    #plot the x and y values in a scatter plot on the plot just created
    series = pairs.values()
    series = sorted(series, key=lambda pair: pair[0])
    ax1.scatter([x for x,y in series], [y for x,y in series], s=30, c='b', marker="s", label="Similarity Results")
    
    results = {}
    #The possible colors of the regression lines, will be cycled through,
    #all dotted lines, respectively: blue, green, red, cyan, magenta, yellow, black, white
    color = ['b--', 'g--', 'r--', 'c--', 'm--', 'y--', 'k--', 'w--']
    index = 0
    # Heading for text box
    r_str = "Coefficient of Determination (R2): \n\n"
    
    for name, param_ids, func in funcs:
        param_vals, fit_data = func(series)
        #plot each regression line on the same graph as the scatter plot
        plt.plot([x for x,y in series], fit_data, color[index], label = name)
        
        #calculate the coefficient of determination for each regression and include it in the textbox
        corr = coef_of_det(fit_data, [y for x,y in series])
        short_name = name.split("(")[0]
        r_str += '%s:  %e\n' % (short_name, corr) 
        angle = math.atan2(param_vals[0], 1)
        results[name] = (param_ids, param_vals, corr)
        #Go to the next color
        index += 1
              
    
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

    
    #Print progress to console.
    print str(file.split(os.sep)[-3].split("_")[-2])  + "\t" + str(file.split(os.sep)[-2])
    print str(file.split(os.sep)[-1]) + " done.\n"
    
    return results            
            
            
            
#regex to get the number from filename
extractor = re.compile('(\d+)')




# checks whether the feature-measure combination is a similarity or dissimilarity metric
# If it is a similarity metric, it returns "SIMILARITY", else it returns "DISSIMILARITY"
def is_similarity_measure(file):
    answer = ""
    for n1, n2, num in [line.split(',') for line in file]:
        if (n1 == n2):
            if(float(num) == 0 or float(num) == -999):
                answer = "DISSIMILARITY"
            else:
                answer = "SIMILARITY"
    return answer
        

#create a tuple containing necessary label and save information for a plot
# (path to dir with the csv to plot, name of the csv to plot, path to the superdirectory with the folder that will contain the graph) -> (dict of plot labels, dict of plot save options)  
def plot_opts(dir_path, file_name, out_path, type):
   
    labels = {}
    dir = dir_path.split(os.sep)[-1].split("-")
    trans = dir_path.split(os.sep)[-2].split("_")
    file_title = reformat_filename(file_name)
    labels['title']  = "Varying " + str.capitalize(dir[0]) + " (" + " ".join(dir[1:]) + ")"  +   " with " + str.capitalize(trans[-2]) + " transform:\n" + file_title + "\n\n"
    labels['xLabel'] = "Changing " + dir[0]
    if type == "SIMILARITY":
        labels['yLabel'] = "Similarity Values"
    else:
        labels['yLabel'] = "Dissimilarity Values"
    
    save_options = {}
    save_options["dir"] = out_path
    save_options["filename"] = file_name.split(".")[0]
    save_options["file_format"] = ".png"
    
    return (labels, save_options)

    
    # Reformat descriptor and measure part of the plot title
def reformat_filename(file_name):
    file_title = file_name.split(".")[0]
    
    if "Histogram" in file_title or "Pixel" in file_title:
        file_title = file_title.split("_")
        file_title[1:1] = ['Based'] # Insert after "Histogram" or "Pixel" in title
        file_title = ' '.join(file_title)
    return file_title
    

#find all csv files in directory
def dir_find(root, suffix):
	return [file for file in os.listdir(root) if suffix in file]

#write first line of csv file    
def write_header(funcs, out_file):
    sorted_funcs = sorted(funcs, key=lambda func:func[0]) #sort results by function name to line up with data below
    out_file.write('Feature,Measure,%s\n' % ",".join([name + " Params," + name + " GoF" for name, params, f in sorted_funcs]))
			
#write a row of the csv file using result from a full regression analysis of one csv data file            
def write_row(single_result, filename, out_file):
    sorted_result = sorted(single_result.items(), key=lambda item:item[0]) #sort results by function name to line up with header
    body = ",".join(['%s,%s' % (make_param_string(param_ids, param_vals), corr) for name, (param_ids, param_vals, corr) in sorted_result])
    #Descriptor is the first word in the descriptor-measure chunk
    descriptor = filename.split("_")[0]
    #Measure is everything else
    measure = " ".join(filename.split("_")[1:])
    out_file.write(descriptor + "," + measure +',' + body + "\n")
		
#make the parameters of function nice for display        
def make_param_string(ids, vals):
	return ";".join(['%s=%s'%(id, val) for id, val in zip(ids, vals)])
    
#calculate the coefficient of determination for the 
#regression using the predicted and observed y values
def coef_of_det(fit_data, y_data):
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
    reg = extractor.findall(img_name)
    #print('found: %s' % reg)
    return int(reg[-1]) if reg else 0.0
	
#get a mapping (reference, comparison) => (x,y) from parsed csv file
def get_pairs(parsed):
    return {(n1,n2):(get_x(n2), y) for n1, n2, y in parsed}
