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
 * $Id: CR2RasterReader.java,v 1.2 2006/02/08 19:46:48 fabriziogiudici Exp $
 *  
 ******************************************************************************/

package it.tidalwave.imageio.cr2;

import java.io.IOException;
import java.nio.ByteOrder;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import it.tidalwave.imageio.decoder.LosslessJPEGDecoder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RasterReader;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: CR2RasterReader.java,v 1.2 2006/02/08 19:46:48 fabriziogiudici Exp $
 *
 ******************************************************************************/

public class CR2RasterReader extends RasterReader
  {
    private static final int BUFFER_SIZE = 128 * 1024;

    private int canonTileWidth;

    private int canonLastTileWidth;

    /*******************************************************************************
     * 
     * @param canonTileWidth The canonTileWidth to set.
     * 
     *******************************************************************************/
    public void setCanonTileWidth (int canonTileWidth)
      {
        this.canonTileWidth = canonTileWidth;
      }

    /*******************************************************************************
     * 
     * @param canonLastTileWidth The canonLastTileWidth to set.
     * 
     *******************************************************************************/
    public void setCanonLastTileWidth (int canonLastTileWidth)
      {
        this.canonLastTileWidth = canonLastTileWidth;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * CR2 files are always compressed.
     * 
     *******************************************************************************/
    protected boolean isCompressedRaster ()
      {
        return true;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    protected void loadCompressedRaster (RAWImageInputStream iis,
                                         WritableRaster raster,
                                         RAWImageReaderSupport ir) throws IOException
      {
        LosslessJPEGDecoder jpegDecoder = new LosslessJPEGDecoder();
        ByteOrder byteOrderSaved = iis.getByteOrder();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        jpegDecoder.reset(iis);
        DataBufferUShort dataBuffer = (DataBufferUShort)raster.getDataBuffer();
        short[] data = dataBuffer.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int pixelStride = 3; // FIXME
        int scanStride = width * pixelStride;
        
        iis.selectBitReader(-1, BUFFER_SIZE); 
        iis.setSkipZeroAfterFF(true);
        int wholeTileCount = (canonTileWidth != 0) ? (width - canonLastTileWidth) / canonTileWidth : 0;
        int odd = height % 2;

        for (int y = 0; y < height; y++)
          {
            short[] rowBuffer = jpegDecoder.loadRow(iis);

            for (int x = 0; x < width; x++)
              {
                int value = rowBuffer[x];

                if (linearizationTable != null)
                  {
                    value = linearizationTable[value & 0xffff];
                  }

                int xx = x;
                int yy = y;

                if (canonTileWidth != 0)
                  {
                    int scan = y * width + x;
                    int tileIndex = scan / (canonTileWidth * height);

                    if (tileIndex < wholeTileCount)
                      {
                        xx = scan % canonTileWidth + tileIndex * canonTileWidth;
                        yy = scan / canonTileWidth % height;
                      }

                    else
                      {
                        scan -= wholeTileCount * canonTileWidth * height;
                        xx = scan % canonLastTileWidth + wholeTileCount * canonTileWidth;
                        yy = scan / canonLastTileWidth;
                      }
                  }

              //
              // This is a very strange thing. EOS 1Ds Mark II has an odd number of rows on the sensor.
              // Looks like it's a GBRG Bayer pattern, but DNG marks it as a RGGB. So we skip the first
              // row and voila', GBRG becomes RGGB.
              //
                yy -= odd;

                if ((xx < width) && (yy >= 0) && (yy < height))
                  {
                    int cfaIndex = (yy % 2) * 2 + (xx % 2);
                    data[yy * scanStride + xx * pixelStride + cfaOffsets[cfaIndex]] = (short)value;
                  }
              }

            ir.processImageProgress((100.0f * y) / height);
          }

        iis.setByteOrder(byteOrderSaved);
      }
  }
