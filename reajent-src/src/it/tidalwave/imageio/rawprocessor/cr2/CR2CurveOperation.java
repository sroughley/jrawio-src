/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 *
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 *******************************************************************************
 *
 * $Id: CR2CurveOperation.java,v 1.1 2006/02/17 15:32:08 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor.cr2;

import java.util.logging.Logger;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.CurveOperation;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: CR2CurveOperation.java,v 1.1 2006/02/17 15:32:08 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class CR2CurveOperation extends CurveOperation  
  {
    private final static String CLASS = "it.tidalwave.imageio.rawprocessor.cr2.CR2CurveOperation";
    
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected double getWhiteLevel (RAWImage image)
      {
        logger.fine("getWhiteLevel()");
        return (1 << 12) - 1; // FIXME
      }
  }
