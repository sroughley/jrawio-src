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
 * $Id: DCRImageReaderSpi.java,v 1.3 2006/02/14 22:18:08 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.dcr;

import java.util.Locale;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.pef.PEFImageReader;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: DCRImageReaderSpi.java,v 1.3 2006/02/14 22:18:08 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class DCRImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.dcr.DCRImageReaderSpi");
    
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public DCRImageReaderSpi ()
      {
        super("DCR", "dcr", "image/dcr", DCRImageReader.class);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Standard DCR Image Reader";
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public ImageReader createReaderInstance (Object extension) throws IOException
      {
        return new DCRImageReader(this, extension);
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    public boolean canDecodeInput (RAWImageInputStream iis) throws IOException
      {
        iis.seek(0);
        long ifdOffset = TIFFImageReaderSupport.processHeader(iis, null);
        IFD primaryIFD = new IFD();
        primaryIFD.load(iis, ifdOffset);
        
        if (primaryIFD.isDNGVersionAvailable())
          { 
            return false;    
          }
        
        String make = primaryIFD.getMake();
        String model = primaryIFD.getModel();

        if ((make == null) || !make.toUpperCase().startsWith("KODAK"))
          {
            logger.fine("DCRImageReaderSpi giving up on: '" + make + "' / '" + model + "'");
            return false;
          }

        return true;
      }
  }
