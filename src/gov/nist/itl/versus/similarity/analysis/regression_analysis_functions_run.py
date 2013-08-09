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
Regression functions for use in any of the following programs:
    -regression_analysis
    -residuals_analysis
    
"""


from sys import argv
import regression_analysis_sans_graphs as analysis
import numpy as np
import math

"""
Contracts for regression function definitions:
	INPUT : list of (x,y) pairs
	OUTPUT: (list of parameter values, list of y predictions, Pearson's Correlation Coefficient) 
"""

#Linear Regression
def lin_reg(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 1))
        fit_y = np.lib.polyval(rounded_coef, [a for a,b in pairs])
        
        return (rounded_coef, fit_y)

#Quadratic Regression
def quad_reg(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 2))
        fit_y = np.lib.polyval(rounded_coef, [a for a,b in pairs])
        return (rounded_coef, fit_y)

#Logarithmic Regression
def log_reg(pairs):
        x = [math.log(a) if (a > 0) else float('nan') for a,b in pairs if is_valid(b)]
        y = [b for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 1))
        fit_y = [float(rounded_coef[0]*math.log(n if (n > 0) else float('nan')) + rounded_coef[1]) for n in [a for a,b in pairs]]
        return (rounded_coef, fit_y)

#Exponential Regression
def exp_reg(pairs):
        x = [a for a,b in pairs if is_valid(b)]
        y = [math.log(b) if (b > 0) else float('nan') for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 1))
        fit_y = [float(math.exp(rounded_coef[0]*n + rounded_coef[1])) for n in [a for a,b in pairs]]
        return (rounded_coef, fit_y)

#Power Regression
def pow_reg(pairs):
        x = [math.log(a) if (a > 0) else float('nan') for a,b in pairs if is_valid(b)]
        y = [math.log(b) if (b > 0) else float('nan') for a,b in pairs if is_valid(b)]
        x = empty_filter(x)
        y = empty_filter(y)
        rounded_coef = round(np.lib.polyfit(x, y, 1))
        fit_y = [float(math.exp(rounded_coef[0]*math.log(n if (n > 0) else float('nan')) + rounded_coef[1])) for n in [a for a,b in pairs]]
        return (rounded_coef, fit_y)

#checks if a string is a valid float
def is_valid(n):
    if (n != float('nan')) and (n != float('inf')):
        return True
    else:
        return False
        
#replaces empty lists with single element lists containing float('nan')
def empty_filter(num_list):
    if (len(num_list) <= 0):
        return [float('nan')]
    else:
        return num_list

#rounds values in a list smaller than 10E-15 to 0
def round(coef):
    if ('nan' not in coef and 'inf' not in coef):
        rounded_coef = [num if abs(num) > 10E-15 else 0 for num in coef]
    return rounded_coef
        
#Construct your regressions list as follows:
#	A list of (function name, list of parameter names, actual function) triples	
regressions = [("Linear Regression (y = a*x + b)", ['a', 'b'], lin_reg),
                ("Quadratic Regression (y = a*x^2 + b*x + c)", ['a', 'b', 'c'], quad_reg),
                ("Logarithmic Regression (y = a*log(x) + b)", ['a', 'b'], log_reg),
                ("Exponential Regression (y = (e^b)*e^(a*x))", ['a','b'], exp_reg),
                ("Power Regression (y = (e^b)*(x^a))", ['a','b'], pow_reg)]

def main(*argv):
        analysis.run_analysis(regressions, argv[0], argv[1])

#run like this:
#	python residuals_analysis_demo.py <csvdir> <outputfile>.csv
if __name__ == '__main__':
	analysis.run_analysis(regressions, argv[1], argv[2])

