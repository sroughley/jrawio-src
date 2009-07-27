/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id: DNGWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.dng;

import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.rawprocessor.OperationSupport;
import it.tidalwave.imageio.rawprocessor.RAWImage;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: DNGWhiteBalanceOperation.java 153 2008-09-13 15:13:59Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public class DNGWhiteBalanceOperation extends OperationSupport
  {
    private final static Logger logger = getLogger(DNGWhiteBalanceOperation.class);
    
    /*******************************************************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************************************************/
    public void process (RAWImage image)
      {
        TIFFMetadataSupport metadata = (TIFFMetadataSupport)image.getRAWMetadata();
        IFD primaryIFD = metadata.getPrimaryIFD();
        
        if (primaryIFD.isAsShotNeutralAvailable())
          {
            TagRational[] asn = primaryIFD.getAsShotNeutral();
            image.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            image.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            image.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
        
        if (primaryIFD.isAnalogBalanceAvailable())
          {
            TagRational[] asn = primaryIFD.getAnalogBalance();
            image.multiplyRedCoefficient(asn[0].reciprocal().doubleValue());
            image.multiplyGreenCoefficient(asn[1].reciprocal().doubleValue());
            image.multiplyBlueCoefficient(asn[2].reciprocal().doubleValue());
          }
      }    
  }
