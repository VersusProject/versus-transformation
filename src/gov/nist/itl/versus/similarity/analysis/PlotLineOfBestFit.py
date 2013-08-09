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
Creates a scatter plot with a linear line of best fit for each versus created csv in the input folder 
and outputs them into the output folder given, provided it has a subfolder structure identical to the input folder

Written largely by David Nimorwicz
"""

import Utils
import os
import PrintDataToConsole
import os
import os.path
import gc

from pylab import *
import matplotlib.pyplot as plt

def main():
    in_dir = "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_glcm_Versus"
    out_dir = "C:\Users\cng1\Documents\TestImages\Simulated_Data_v2\Simulated_Data_glcm_Versus_graphs"
    sub_dirs = os.listdir(in_dir)
     
    for sub_dir in sub_dirs:
        if os.path.isdir(os.path.join(in_dir, sub_dir)):
            out_path = os.path.join(out_dir, sub_dir)
            data = Utils.getFilesInDirectory(os.path.join(in_dir, sub_dir));
            plotScatter(data, 2, out_path)
            gc.collect()
    



def plotScatter(data_dir, col_data, output_path):
    """ Plot multiple scatter plots

        Wrapper to plot multiple scatter plots from data in CSV files
        within a directory

        Args:
            data_dir: input csv file directory
            col_data: column number which contains data
            output_path: directory to save images of plots
    """
    for file in data_dir:
        results = Utils.getResultData(file, col_data)

        labels = {}
        labels['title']  = Utils.getTitleFromFilename(file)
        labels['xLabel'] = "Index of Image Comparisons"
        labels['yLabel'] = "Distribution of Similarity Results"

        # Reformat Graph Title
        if "Histogram" in labels['title'] or "Pixel" in labels['title']:
            title = labels['title']
            title = title.split(" ")
            title[1:1] = ['Based -'] # Insert after "Histogram" or "Pixel" in title
            labels['title'] = ' '.join(title)

        save_options = {}
        save_options["dir"] = output_path
        save_options["filename"] = Utils.getFilename(file)
        save_options["file_format"] = ".png"

        print labels['title']

        generateSimilarityScatterPlot(results, labels, save_options)

    print "Finished"





def generateSimilarityScatterPlot(dataset, plot_labels, save_options):
    """ Creates a scatter plot

        Args:
            dataset: list of numbers
            plot_labels: dictionary of plot labels, keys are
                         'title', 'xLabel', 'yLabel'
            save_options: dictionary of directory parameters
                          to build a filepath for plot image,
                          keys are 'dir', 'filename', 'file_format'
    """

    total = len(dataset)

    x = range(total)
    fig = plt.figure()
    ax1 = fig.add_subplot(111)

    ax1.set_title(plot_labels['title'],fontsize=16)
    ax1.set_xlabel(plot_labels['xLabel'],fontsize=14)
    ax1.set_ylabel(plot_labels['yLabel'],fontsize=14)


    # dataset changes for plotting purposes
    inf_in_data = False
    m = -1
    for num in dataset:
        if (num > 1e+308):
            inf_in_data = True
        else:
            if (num > m):
                m = num # second maximum of the dataset

    # print "M = %e" % m
    index = 0
    if inf_in_data:
        if m == -1: # all data was inf values and we didn't change m, so set it to a value that we can plot
            m = 9999
        else:
            m = m + 50 # set the infinite value to the second max of the dataset + 10
        for n in dataset:
            if (n > 1e+308):
                dataset[index] = m
                # print dataset[index]
            index = index + 1


    props = dict(boxstyle='round', facecolor='gray', alpha=0.3)


    # set axes range
    plt.xlim(min(x),max(x)+1)
    plt.ylim(min(dataset),max(dataset)+0.05)
    
    coefs = np.lib.polyfit(x, dataset, 1) #4
    fit_y = np.lib.polyval(coefs, x) #5
    plt.plot(x, fit_y, 'b--', label = "Best Fit Line") #6
    print(coefs)

    mu = mean(dataset)
    median = np.median(dataset)
    sigma = std(dataset)
    min_val = min(dataset)
    max_val = max(dataset)

    textstr = '$min=\ %e$\n$max=\ %e$\n$mean=\ %e$\n$median=\ %e$'%( min_val, max_val, mu, median)


    # place a text box in upper right in axes coords
    ax1.text(0.0, -0.05, textstr, transform=ax1.transAxes, fontsize=14, horizontalalignment='left',
        verticalalignment='top', bbox=props)
    
    ax1.scatter(x, dataset, s=30, c='b', marker="s", label="Similarity Results")

    box = ax1.get_position()
    ax1.set_position([box.x0, box.y0 + box.height * 0.1, box.width, box.height * 0.9])
    ax1.legend(loc='upper center', bbox_to_anchor=(0.5, -0.1), fancybox=True, shadow=True, ncol=2)

    #plt.show()
    fig.set_size_inches(15,11)
    fig.savefig(save_options["dir"] + os.sep + save_options["filename"] + save_options["file_format"], bbox_inches=0, dpi=gcf().dpi)

    fig.clf()
    plt.close()
    del x, dataset, fit_y, coefs, mu, median, sigma, min_val, max_val, textstr
    gc.collect()




# Standard boilerplate to call the main() function to begin
# the program.
if __name__ == '__main__':
    main()
