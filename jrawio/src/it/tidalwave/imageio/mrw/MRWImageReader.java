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
 * $Id: MRWImageReader.java 140 2008-09-07 12:48:37Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.mrw;

import javax.annotation.Nonnull;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RasterReader;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.minolta.MinoltaRawData;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWImageReader.java 140 2008-09-07 12:48:37Z fabriziogiudici $
 *
 ******************************************************************************/
public class MRWImageReader extends TIFFImageReaderSupport
  {
    private final static String CLASS = MRWImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
   
    /***************************************************************************
     *
     **************************************************************************/
    protected MRWImageReader (@Nonnull final ImageReaderSpi originatingProvider,
                              final Object extension)
      {
        super(originatingProvider, MinoltaMakerNote.class, MRWMetadata.class);
        headerProcessor = new MRWHeaderProcessor();
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     * FIXME: merge with super implementation
     * 
     **************************************************************************/
    @Override
    @Nonnull
    protected Directory loadPrimaryDirectory() 
      throws IOException
      {
        logger.info("loadPrimaryDirectory(" + iis + ")");
        headerProcessor.process(iis);
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        iis.seek(headerProcessor.getOffset());
        final long directoryOffset = processHeader(iis, headerProcessor); // FIXME: refactor so that processHeader doe snot use headerSkipper

        final IFD primaryIFD = new IFD();
        primaryIFD.loadAll(iis, directoryOffset);
        iis.setBaseOffset(0);
        
        return primaryIFD;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     * FIXME: merge with super implementation
     * 
     **************************************************************************/
    @Override
    protected void processEXIFAndMakerNote (@Nonnull final Directory directory) 
      throws IOException
      {
        iis.setBaseOffset(headerProcessor.getBaseOffset());
        super.processEXIFAndMakerNote(directory);
        iis.setBaseOffset(0);
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnull
    protected WritableRaster loadRAWRaster() 
      throws IOException
      {
        logger.fine("loadRAWRaster(iis: " + iis + ")");

        final long time = System.currentTimeMillis();
        final MRWRasterReader rasterReader = new MRWRasterReader();
        initializeRasterReader(rasterReader);

        final MRWHeaderProcessor mrwHeaderProcessor = (MRWHeaderProcessor)headerProcessor;
        final MinoltaRawData minoltaRawData = mrwHeaderProcessor.getMinoltaRawData();
        final long rasterOffset = minoltaRawData.getRasterOffset();
        final int sensorWidth = minoltaRawData.getPRD().getCcdSize().width;
        final int sensorHeight = minoltaRawData.getPRD().getCcdSize().height;
        iis.seek(rasterOffset); // FIXME: set prop in rasterReader, seek in the rasterreader
        logger.finest(">>>> imageDataOffset: " + rasterOffset + ", size: " + sensorWidth + " x " + sensorHeight);
        rasterReader.setWidth(sensorWidth);
        rasterReader.setHeight(sensorHeight);
        int rasterDataSize = 0;
        int bitsPerSample = 16;
        rasterReader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        
        MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
        
//        if (minoltaMakerNote.isRasterDataSizeAvailable())
//          {
//            rasterDataSize = minoltaMakerNote.getRasterDataSize();
//            bitsPerSample = (rasterDataSize * 8) / (sensorWidth * sensorHeight);
//          }
//        
//        else
          {
            final IFD primaryIFD = (IFD)primaryDirectory;
            final String model = primaryIFD.getModel();
        
            if ((model != null) && ((model.indexOf("A200") > 0) || (model.indexOf("DYNAX 5D") >= 0)
                || (model.indexOf("A2") > 0) || (model.indexOf("A1") > 0)) || (model.indexOf("7D") >= 0))
              {
                bitsPerSample = 12;  
              }
        
            rasterDataSize = (sensorWidth * sensorHeight) / bitsPerSample;
            logger.finest("MODEL " + model + " BITS " + bitsPerSample);
          }
            
        rasterReader.setBitsPerSample(bitsPerSample);
        rasterReader.setStripByteCount(rasterDataSize);
        
        logger.finest(">>>> using rasterReader: " + rasterReader);
        final WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.finer(">>>> loadRAWRaster() completed ok in " + (System.currentTimeMillis() - time) + " msec.");

        return raster;
      }

    /***************************************************************************
     * 
     * FIXME: merge with superclass
     * 
     * @param rasterReader
     * 
     **************************************************************************/
    protected void initializeRasterReader (@Nonnull final RasterReader rasterReader)
      {
        final IFD primaryIFD = (IFD)primaryDirectory;
        final IFD exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
        
//        rasterReader.setCFAPattern(exifIFD.getEXIFCFAPattern());
        rasterReader.setCFAPattern(new byte[]{0,1,1,2});
        rasterReader.setCompression(primaryIFD.getCompression().intValue());

        final MinoltaMakerNote minoltaMakerNote = (MinoltaMakerNote)makerNote;
        
        if (minoltaMakerNote.isRasterDataSizeAvailable())
          {
            rasterReader.setStripByteCount(minoltaMakerNote.getRasterDataSize());
          }
      }
  }
