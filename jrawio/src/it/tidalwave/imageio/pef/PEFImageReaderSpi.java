/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: PEFImageReaderSpi.java 82 2008-08-24 08:46:20Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.pef;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.logging.Logger;
import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSpiSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: PEFImageReaderSpi.java 82 2008-08-24 08:46:20Z fabriziogiudici $
 *
 ******************************************************************************/
public class PEFImageReaderSpi extends RAWImageReaderSpiSupport
  {
    private final static String CLASS = PEFImageReaderSpi.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    /***************************************************************************
     * 
     **************************************************************************/
    public PEFImageReaderSpi()
      {
        super("PEF", "pef", "image/pef", PEFImageReader.class);
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnull
    public String getDescription (final Locale locale)
      {
        return "Standard PEF Image Reader";
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnull
    public ImageReader createReaderInstance (final Object extension) 
      throws IOException
      {
        return new PEFImageReader(this, extension);
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    public boolean canDecodeInput (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {
        iis.seek(0);
        long ifdOffset = TIFFImageReaderSupport.processHeader(iis, null);
        final IFD primaryIFD = new IFD();
        primaryIFD.load(iis, ifdOffset);
        
        if (primaryIFD.isDNGVersionAvailable())
          { 
            return false;    
          }
        
        final String make = primaryIFD.getMake();
        final String model = primaryIFD.getModel();

        if ((make == null) || !make.toUpperCase().startsWith("PENTAX"))
          {
            logger.fine("PEFImageReaderSpi giving up on: '" + make + "' / '" + model + "'");
            return false;
          }

        return true;
      }
  }
