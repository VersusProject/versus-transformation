/*******************************************************************************
 * University of Illinois/NCSA
 * Open Source License according to
 * http://www.otm.uiuc.edu/faculty/forms/opensource.asp
 * 
 * Copyright (c) 2006,    NCSA/UIUC.  All rights reserved.
 * 
 * Developed by:
 * 
 * Name of Development Groups:
 * Image Spatial Data Analysis Group (ISDA Group)
 * http://isda.ncsa.uiuc.edu/
 * 
 * Name of Institutions:
 * National Center for Supercomputing Applications (NCSA)
 * http://www.ncsa.uiuc.edu/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal with the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimers.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimers in the
 *   documentation and/or other materials provided with the distribution.
 *   Neither the names of University of Illinois/NCSA, nor the names
 *   of its contributors may be used to endorse or promote products
 *   derived from this Software without specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
 *******************************************************************************/
/*
 * Created on Sep 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package transforms;

/**
 * Copied into the Versus-transformations project for Color Space Transformations
 */

import ncsa.im2learn.core.datatype.ImageObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yjlee
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ColorModels {
    private static Log logger = LogFactory.getLog(ColorModels.class);
    protected String _colorModelFrom = "RGB";

    protected String _colorModelTo = "HSV";

    private ImageObject _convertedIm = null;

    protected float _MinVal, _MaxVal;

    protected int _howRGB2GRAY = 1; // this value is used for selecting a

    // methodd for RGB to Grayscale conversion

    public ColorModels() {
        _convertedIm = null;
    }

    public ColorModels(String colorModelFrom, String colorModelTo) {
        setColorModelFrom(colorModelFrom);
        setColorModelTo(colorModelTo);
        _convertedIm = null;
    }

    //setters and getters

    public boolean setColorModelFrom(String colorModel) {
        String val = null;

        if ((val = checkColorModelValue(colorModel)) == null) {
            return false;
        } else {
            _colorModelFrom = val;
            return true;
        }
    }

    public boolean setColorModelTo(String colorModel) {
        String val = null;

        if ((val = checkColorModelValue(colorModel)) == null) {
            return false;
        } else {
            _colorModelTo = val;
            return true;
        }
    }

    public String checkColorModelValue(String colorModel) {
        String val = null;

        //sanity check
        if (colorModel == null) {
            logger.debug("ERROR: colorModel = null");

            return val;
        }

        if (colorModel.equalsIgnoreCase("GRAY")) {
            val = new String();
            val = "GRAY";

            return val;
        }

        // red, green, blue
        if (colorModel.equalsIgnoreCase("RGB")) {
            val = new String();
            val = "RGB";

            return val;
        }

        // hue, saturation, value
        if (colorModel.equalsIgnoreCase("HSV")) {
            val = new String();
            val = "HSV";

            return val;
        }
        if (colorModel.equalsIgnoreCase("YUV")) {
            val = new String();
            val = "YUV";

            return val;
        }

        
        return val;
    }

    public String getColorModelFrom() {
        return _colorModelFrom;
    }

    public String getColorModelTo() {
        return _colorModelTo;
    }

    public ImageObject getConvertedIm() {
        return _convertedIm;
    }

    public boolean setHowRGB2GRAY(int val) {
        if (val < 1 || val > 4)
            return false;

        _howRGB2GRAY = val;

        return true;
    }

    public int getHowRGB2GRAY() {
        return _howRGB2GRAY;
    }

    public boolean convert(ImageObject imObject) {
        if (_colorModelFrom.equalsIgnoreCase("RGB")
            && _colorModelTo.equalsIgnoreCase("HSV")) {
            return convertRGB2HSV(imObject);
        }

        if (_colorModelFrom.equalsIgnoreCase("HSV")
            && _colorModelTo.equalsIgnoreCase("RGB")) {
            return convertHSV2RGB(imObject);
        }
        if (_colorModelFrom.equalsIgnoreCase("RGB")
                && _colorModelTo.equalsIgnoreCase("YUV")) {
                return convertRGB2YUV(imObject);
            }

            if (_colorModelFrom.equalsIgnoreCase("YUV")
                && _colorModelTo.equalsIgnoreCase("RGB")) {
                return convertYUV2RGB(imObject);
            }

        if (_colorModelFrom.equalsIgnoreCase("GRAY")
            && _colorModelTo.equalsIgnoreCase("RGB")) {
            return convertGRAY2RGB(imObject);
        }

        if (_colorModelFrom.equalsIgnoreCase("RGB")
            && _colorModelTo.equalsIgnoreCase("GRAY")) {
            return convertRGB2GRAY(imObject);
        }

        logger.debug("ERROR: conversion is not implemented yet");

        return false;
    }

    public boolean convertRGB2HSV(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");
            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != ImageObject.TYPE_FLOAT
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()) {
            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(),
                                                       imObject.getNumCols(), imObject.getNumBands(), ImageObject.TYPE_FLOAT);
            } catch (Exception e) {
                logger.error("Error in converting RGB2HSV", e);
            }
        }

        return convertRGB2HSVOut(imObject, _convertedIm);
    }

    // RGB are in signed byte, output H is in [0,360] (invalid = -1), S in [0,1]
    // and V in [0,256] (unsigned byte)
    public boolean convertRGB2HSVOut(ImageObject imObject, ImageObject newIm) {
        if (!sanityCheck(imObject, newIm))
            return false;

        //		if (!imObject.sampType.equalsIgnoreCase("BYTE")) {
        //
        //			System.out
        //					.println("Error: other than BYTE type input image is not supported");
        //
        //			return false;
        //
        //		}

        //		if (imObject.sampType.equalsIgnoreCase("BYTE")
        //				&& imObject.image == null) {
        //
        //			System.out.println("Error: no BYTE image to convert");
        //
        //			return false;
        //
        //		}
        //
        //		if (!newIm.sampType.equalsIgnoreCase("FLOAT")) {
        //
        //			System.out.println("Error: output image is not in FLOAT type");
        //
        //			return false;
        //
        //		}

        int idx, red, green, blue;
        int max, min, delta;

        for (idx = 0; idx < imObject.getSize(); idx += imObject.getNumBands()) {
            // convert byte to int
            red = imObject.getInt(idx);
            green = imObject.getInt(idx + 1);
            blue = imObject.getInt(idx + 2);

            // find min and max of r,g,b
            max = min = red;
            if (max < green) {
                max = green;
            }

            if (max < blue) {
                max = blue;
            }

            if (min > green) {
                min = green;
            }

            if (min > blue) {
                min = blue;
            }

            // v is max
            newIm.set(idx + 2, max);

            // calculate saturation
            if (max != 0) {
                newIm.set(idx + 1, (float) (max - min) / max);
            } else {
                newIm.set(idx + 1, 0.0f);
            }

            if (newIm.getFloat(idx + 1) == 0.0f) {
                newIm.set(idx, -1.0f);
            } else {
                delta = max - min;

                if (red == max) {
                    newIm.set(idx, (float) (green - blue) / delta);
                } else if (green == max) {
                    newIm.set(idx, 2.0F + (float) (blue - red) / delta);
                } else if (blue == max) {
                    newIm.set(idx, 4.0F + (float) (red - green) / delta);
                }

                newIm.set(idx, 60 * newIm.getFloat(idx));

                if (newIm.getFloat(idx) < 0.0f)
                    newIm.setFloat(idx, 360.0f + newIm.getFloat(idx));

            }// end chromatic case
        }

        return true;
    }

    private boolean sanityCheck(ImageObject imObject, ImageObject newIm) {
        //sanity check
        if (imObject == null) {
            logger.debug("Error: no image to convert");

            return false;
        }

        if (imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");

            return false;
        }

        if (newIm == null) {
            logger.debug("Error: no output image to convert to");

            return false;
        }

        if (newIm.getNumBands() != 3) {
            logger.debug("Error: output image does not have three bands");

            return false;
        }

        if (newIm.getNumRows() != imObject.getNumRows()
            || newIm.getNumCols() != imObject.getNumCols()) {
            logger.debug("Error: output image does not match the size of input image");

            return false;
        }

        return true;
    }

    public boolean convertHSV2RGB(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");

            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != ImageObject.TYPE_BYTE
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()) {
            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(), imObject.getNumCols(),
                                                       imObject.getNumBands(), ImageObject.TYPE_BYTE);
            } catch (Exception e) {
                logger.error("Error in converting HSV2RGB", e);
            }
        }

        return convertHSV2RGBOut(imObject, _convertedIm);
    }

    // input H is in [0,360] (invalid = -1), S in [0,1] and V in [0,256]
    // (unsigned byte)
    // output RGB are in signed byte
    public boolean convertHSV2RGBOut(ImageObject imObject, ImageObject newIm) {
        if (!sanityCheck(newIm, imObject))
            return false;

        if (imObject.getType() != ImageObject.TYPE_FLOAT) {
            logger.debug("Error: other than FLOAT type input image is not supported");

            return false;
        }

        if ( imObject.isHeaderOnly() ) {
            logger.debug("Error: no FLOAT image to convert");

            return false;
        }

        if (newIm.getType() != ImageObject.TYPE_BYTE) {
            logger.debug("Error: output image is not in BYTE type");

            return false;
        }

        int idx, i, val, t, p, q;
        float f, h;

        for (idx = 0; idx < imObject.getSize(); idx += imObject.getNumBands()) {
            if (imObject.getFloat(idx + 1) == 0.0f) {
                if (imObject.getFloat(idx) == -1.0F) {
                    val = (int) imObject.getFloat(idx + 2) & 0xff; // value
                    newIm.setByte(idx, (byte) val);
                    newIm.setByte(idx + 1, (byte) val);
                    newIm.setByte(idx + 2, (byte) val);
                } else {
                    logger.debug("ERROR: unacceptable values: (Hue="
                                 + imObject.getFloat(idx) + ", Saturation="
                                 + imObject.getFloat(idx + 1) + ", value="
                                 + imObject.getFloat(idx + 2) + ")");

                    newIm.setByte(idx, (byte) 0);
                    newIm.setByte(idx + 1, (byte) 0);
                    newIm.setByte(idx + 2, (byte) 0);
                }
            } else {
                h = imObject.getFloat(idx);
                if (h == 360.0F) {
                    h = 0.0F;
                }
                h /= 60.0F;

                i = (int) Math.floor(h);
                f = h - i;
                p = (int) (imObject.getFloat(idx + 2)
                           * (1.0F - imObject.getFloat(idx + 1)) + 0.5F) & 0xff;
                q = (int) (imObject.getFloat(idx + 2)
                           * (1.0F - imObject.getFloat(idx + 1) * f) + 0.5F) & 0xff;
                t = (int) (imObject.getFloat(idx + 2)
                           * (1.0F - imObject.getFloat(idx + 1) * (1.0F - f)) + 0.5F) & 0xff;
                val = (int) imObject.getFloat(idx + 2) & 0xff;// value

                switch (i) {
                    case 0:
                        newIm.setByte(idx, (byte) val);
                        newIm.setByte(idx + 1, (byte) t);
                        newIm.setByte(idx + 2, (byte) p);

                        break;

                    case 1:
                        newIm.setByte(idx, (byte) q);
                        newIm.setByte(idx + 1, (byte) val);
                        newIm.setByte(idx + 2, (byte) p);

                        break;

                    case 2:
                        newIm.setByte(idx, (byte) p);
                        newIm.setByte(idx + 1, (byte) val);
                        newIm.setByte(idx + 2, (byte) t);

                        break;

                    case 3:
                        newIm.setByte(idx, (byte) p);
                        newIm.setByte(idx + 1, (byte) q);
                        newIm.setByte(idx + 2, (byte) val);

                        break;

                    case 4:
                        newIm.setByte(idx, (byte) t);
                        newIm.setByte(idx + 1, (byte) p);
                        newIm.setByte(idx + 2, (byte) val);

                        break;

                    case 5:
                        newIm.setByte(idx, (byte) val);
                        newIm.setByte(idx + 1, (byte) p);
                        newIm.setByte(idx + 2, (byte) q);

                        break;

                    default:
                        logger.debug("ERROR: unacceptable value of i = " + i);

                        newIm.setByte(idx, (byte) 0);
                        newIm.setByte(idx + 1, (byte) 0);
                        newIm.setByte(idx + 2, (byte) 0);

                        break;
                }// end switch
            } // end chromatic case
        }// end of for idx loop

        return true;
    }

/////////////////////////////////////
//////Convert From RGB to YUV and vice versa
////////////////////////
    
    public boolean convertRGB2YUV(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");

            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != ImageObject.TYPE_DOUBLE
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()) {
            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(),
                                                       imObject.getNumCols(), imObject.getNumBands(), ImageObject.TYPE_DOUBLE);
            } catch (Exception e) {
                logger.error("Error in converting RGB2YUV", e);
            }
        }

        return convertRGB2YUVOut(imObject, _convertedIm);
    }

   
    public boolean convertRGB2YUVOut(ImageObject imObject, ImageObject newIm) {
        if (!sanityCheck(imObject, newIm))
            return false;

        
        int idx;
        double red, green, blue;
        double Y,U,V;

        for (idx = 0; idx < imObject.getSize(); idx += imObject.getNumBands()){
        	
        	red=imObject.getDouble(idx);
        	green=imObject.getDouble(idx+1);
        	blue=imObject.getDouble(idx+2);
        		
        	Y =  0.299*red + 0.587*green + 0.114*blue;
            U = -0.147*red - 0.289*green + 0.436*blue;
            V =  0.615*red - 0.515*green - 0.100*blue;
            
            newIm.setDouble(idx, Y);
            newIm.setDouble(idx + 1, U);
            newIm.setDouble(idx + 2, V);
        	
        }
          

        return true;
    }
    
    public boolean convertYUV2RGB(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");

            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != ImageObject.TYPE_DOUBLE
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()) {
            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(),
                                                       imObject.getNumCols(), imObject.getNumBands(), ImageObject.TYPE_DOUBLE);
            } catch (Exception e) {
                logger.error("Error in converting YUV2RGB", e);
            }
        }

        return convertYUV2RGBOut(imObject, _convertedIm);
    }

    // RGB are in signed byte, output H is in [0,360] (invalid = -1), S in [0,1]
    // and V in [0,256] (unsigned byte)
    public boolean convertYUV2RGBOut(ImageObject imObject, ImageObject newIm) {
        if (!sanityCheck(imObject, newIm))
            return false;

        
        int idx;
        double red, green, blue;
        double Y,U,V;

        for (idx = 0; idx < imObject.getSize(); idx += imObject.getNumBands()){
        	
        	Y=imObject.getDouble(idx);
        	U=imObject.getDouble(idx+1);
        	V=imObject.getDouble(idx+2);
        		
        	red = Y + 1.140*V;
        	green = Y - 0.395*U - 0.581*V;
        	blue = Y + 2.032*U;

            newIm.setDouble(idx, red);
            newIm.setDouble(idx + 1, green);
            newIm.setDouble(idx + 2, blue);
        	
        }
          

        return true;
    }

    
    ////////////////////////////////////////////////////////
    // Convert color (PPM) format to Gray scale and scales the value
    // into desired range
    /////////////////////////////////////////////////////////////////////
    public boolean convertRGB2GRAY(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 3) {
            logger.debug("Error: input image does not have three bands");
            System.out.println("Error");

            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != imObject.getType()
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()||_convertedIm.getNumBands() != 1) {
            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(), imObject.getNumCols(),
                                                       1, imObject.getType());

                } catch (Exception e) {
                logger.error("Error in converting rgb to gray", e);
                System.out.println("Error");
            }
        }

        return convertRGB2GRAYOut(imObject, _convertedIm, _howRGB2GRAY);
    }

    ////////////////////////////////////////////////////////
    //public ImageObject PPMtoGrayScale(ImageObject image,int howtoscale) {
    public boolean convertRGB2GRAYOut(ImageObject image, ImageObject newIm,
                                      int howtoscale) {

        if (image == null) {
            logger.debug("ERROR: no input data");
            System.out.println("Error");
            return false;
        }

        if (image.getNumBands() != 3) {
            logger.debug("ERROR: input data should have sampPerPixel = 3");
            System.out.println("Error");
            return false;
        }

        //
        if (newIm == null || newIm.getNumCols() != image.getNumCols()
            || newIm.getNumRows() != image.getNumRows()) {
            logger.debug("ERROR: no image or mismatch of size \n");
            System.out.println("newIm=null");
            return false;
        }

        if (newIm.getNumBands() != 1) {
            logger.debug("ERROR: input images should have sampPerPixel = 1");
            System.out.println("newIm.numbands");
            return false;
        }

        if (image.getType() != newIm.getType() ||
            newIm.getType() != ImageObject.TYPE_FLOAT) {
            logger.debug("ERROR: image and outImage types are inconsistent or output image is not FLOAT type");
            System.out.println("Error");
            return false;
        }

        float scale;

        int index, indexColor;

        //ImageObject newIm = null;

        if (howtoscale == 3) {
            if (image.getType() != newIm.getType()) {
                logger.debug("ERROR: image and outImage types have inconsistent types");
                System.out.println("Error");
                return false;
            }

            // one band output
            // first component of PPM becomes PGM value
            int[] band = new int[1];

            band[0] = 1;

            try {
                if (image.extractBand(band) == null) {
                    logger.debug("ERROR: could not copy the first band to gray scale image");
                    System.out.println("Error");

                    return false;
                }
            } catch (Exception e) {
                logger.error("Error in extracing band.", e);
                System.out.println("Error");
            }

            return true;
        }

        if (newIm.getType() != ImageObject.TYPE_FLOAT) {
            logger.debug("ERROR: outImage type should be FLOAT");
            System.out.println("Error");

            return false;
        }

        // convert to a float data
        //ImageObject euclidIm = null;
        if (!convertRGBToEuclidDistImageOut(image, newIm)) {
            logger.debug("ERROR: could not compute Euclidean dist image");
            System.out.println("Error");
            return false;
        }

        // scale everything
        if (howtoscale == 0) {
            scale = (float) (Math.sqrt(3) * 255.0); // PPM max value to (0,1)
            // range

            for (index = 0; index < newIm.getSize(); index++)
                newIm.setFloat(index, newIm.getFloat(index) / scale);
        }

        if (howtoscale == 1) { // range to PGM max value
            if (Math.abs(_MaxVal - _MinVal - 255) > 0.1) {
                scale = (float) (255.0 / (_MaxVal - _MinVal));

                for (index = 0; index < newIm.getSize(); index++)
                    newIm.setFloat(index, (newIm.getFloat(index) - _MinVal) * scale);
            }
        }

        if (howtoscale == 2) {
            scale = (float) Math.sqrt(3); // PPM max to PGM max value

            for (index = 0; index < newIm.getSize(); index++)
                newIm.setFloat(index, newIm.getFloat(index) / scale);
        }

        return true;//(newIm);
    }

    ////////////////////////////////////////////////////////
    // Convert RGB (PPM) format to Euclidean distance (float output)
    ////////////////////////////////////////////////////////
    public ImageObject convertRGBToEuclidDistImage(ImageObject image) {
        if (image == null || image.getNumCols() <= 0 || image.getNumRows() <= 0) {
            logger.debug("ERROR: no image \n");
            System.out.println("Error");

            return null;
        }

        int numrows, numcols;
        numrows = image.getNumRows();
        numcols = image.getNumCols();

        ImageObject imageGray = null;
        try {
            imageGray = ImageObject.createImage(image.getNumRows(), image.getNumCols(), 1, ImageObject.TYPE_FLOAT);

            long size = image.getSize()/image.getNumBands();
            double sum;
            _MaxVal = 0.0F; //norm = 4072.0234; // this is sqrt(255^3)
            _MinVal = Float.MAX_VALUE; //5000
            int index, indexColor;
            boolean signal = true;
            int num_bands=image.getNumBands();
            for (index = 0, indexColor = 0; index < size; index++, indexColor += num_bands) {    	
            		
                sum = image.getFloat(indexColor) * image.getFloat(indexColor);
                sum += image.getFloat(indexColor + 1) * image.getFloat(indexColor + 1);
                sum += image.getFloat(indexColor + 2) * image.getFloat(indexColor + 2);
                sum = Math.sqrt(sum);

                imageGray.setFloat(index, (float) sum);

                if (sum > _MaxVal)
                    _MaxVal = (float) sum;

                if (sum < _MinVal)
                    _MinVal = (float) sum;
            }

            signal = false;

            logger.debug("Max dist in 3D = " + _MaxVal);
            logger.debug("Min dist in 3D = " + _MinVal);

            if (signal)
                return null;
        } catch (Exception e) {
            logger.error("Error in ConvertRGBToEuclidDistImage()", e);
            System.out.println("Error");
        }

        return (imageGray);
    }

    //////////////////////////////////////////////////////
    //public ImageObject PPMtoGrayScaleEuclidDist(ImageObject image) {
    public boolean convertRGBToEuclidDistImageOut(ImageObject image,
                                                  ImageObject newIm) {

        if (image == null || image.getNumCols() <= 0 || image.getNumRows() <= 0) {
            logger.debug("ERROR: no image \n");

            return false;
        }

        if (newIm == null || newIm.getNumCols() != image.getNumCols()
            || newIm.getNumRows() != image.getNumRows()) {

            logger.debug("ERROR: no image or mismatch of size \n");
            return false;
        }

        if (newIm.getNumBands() != 1) {
            logger.debug("ERROR: input images should have sampPerPixel = 1");

            return false;
        }

        if (newIm.getType() != ImageObject.TYPE_FLOAT) {
            logger.debug("ERROR: outImage type is not FLOAT type");

            return false;
        }

        int numrows, numcols;
        numrows = image.getNumRows();
        numcols = image.getNumCols();

        long size = image.getSize()/image.getNumBands();
        double sum;
        _MaxVal = 0.0F; //norm = 4072.0234; // this is sqrt(255^3)
        _MinVal = Float.MAX_VALUE; //5000
        int index, indexColor;
        boolean signal = true;
        int num_bands=image.getNumBands();
        for (index = 0, indexColor = 0; index < size; index++, indexColor += num_bands) {
        	
        
            sum = image.getFloat(indexColor) * image.getFloat(indexColor);
            sum += image.getFloat(indexColor + 1) * image.getFloat(indexColor + 1);
            sum += image.getFloat(indexColor + 2) * image.getFloat(indexColor + 2);
            sum = Math.sqrt(sum);

            newIm.setFloat(index, (float) sum);

            if (sum > _MaxVal)
                _MaxVal = (float) sum;

            if (sum < _MinVal)
                _MinVal = (float) sum;
        }

        signal = false;

        logger.debug("Max dist in 3D = " + _MaxVal);
        logger.debug("Min dist in 3D = " + _MinVal);

        if (signal)
            return false;

        return true;
    }

    /////////////////////////////////////////////////////////////////////
    //direct utility to convert on gray scale image to RGB
    /////////////////////////////////////////////////////////////////////
    public boolean convertGRAY2RGB(ImageObject imObject) {
        if (imObject == null || imObject.getNumBands() != 1) {
            logger.debug("Error: input image does not have one band");

            return false;
        }

        // save memory if possible
        if (_convertedIm == null
            || _convertedIm.getType() != imObject.getType()
            || _convertedIm.getNumRows() != imObject.getNumRows()
            || _convertedIm.getNumCols() != imObject.getNumCols()|| _convertedIm.getNumBands() !=3) {

            try {
                _convertedIm = ImageObject.createImage(imObject.getNumRows(), imObject.getNumCols(),
                                                       3, imObject.getType());
            } catch (Exception e) {
                logger.error("Error in ConvertGRAY2RGB()", e);
            }
        }

        return convertGRAY2RGBOut(imObject, _convertedIm);
    }

   
    public boolean convertGRAY2RGBOut(ImageObject image, ImageObject newIm) {
        if (image == null || image.getNumCols() <= 0 || image.getNumRows() <= 0) {
            logger.debug("ERROR: no image \n");

            return false;
        }

        if (image.getNumBands() != 1) {
            logger.debug("ERROR: input images should have sampPerPixel = 1");

            return false;
        }

        if (image.getType() != ImageObject.TYPE_BYTE &&
            image.getType() != ImageObject.TYPE_FLOAT) {
            logger.debug("ERROR: image type is not supported");
         
            return false;
        }

        ///
        if (newIm == null || newIm.getNumCols() != image.getNumCols()
            || newIm.getNumRows() != image.getNumRows()) {

            logger.debug("ERROR: no image or mismatch of size \n");
         
            return false;
         
        }

        if (newIm.getNumBands() != 3) {
            logger.debug("ERROR: input images should have sampPerPixel = 3");
         
            return false;
        }

        if (image.getType() != newIm.getType()) {
            logger.debug("ERROR: image and outImage types are inconsistent");
         
            return false;
        }

        int numrows, numcols;
        numrows = image.getNumRows();
        numcols = image.getNumCols();

        //long size = newIm.size;
        _MaxVal = -Float.MAX_VALUE;//-5000; // int values
        _MinVal = Float.MAX_VALUE;//5000;
        int index, indexColor;
        boolean signal = true;

        float val;

        for (index = 0, indexColor = 0; index < image.getSize(); index++, indexColor += 3) {
            val = image.getFloat(index);
            newIm.setFloat(indexColor, val);
            newIm.setFloat(indexColor + 1, val);
            newIm.setFloat(indexColor + 2, val);

            if (val > _MaxVal)
                _MaxVal = val;

            if (val < _MinVal)
                _MinVal = val;
        }

        signal = false;

        if (signal)
            return false;

        logger.debug("Max val in PGM = " + _MaxVal);
        logger.debug("Min val in PGM = " + _MinVal);

        return true;//(newIm);
    }

    //display values

    public void printColorModels() {
        logger.debug("ColorModels:From=" + _colorModelFrom + " To="
                     + _colorModelTo);
    }
}
