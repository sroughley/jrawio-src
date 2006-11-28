/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: HeaderProcessor.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: HeaderProcessor.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class HeaderProcessor
  {
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public void process (RAWImageInputStream iis) throws IOException
      {        
      }
    
    /*******************************************************************************
     * 
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    public int getBaseOffset()
      {
        return 0;    
      }

    /*******************************************************************************
     * 
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    public int getOffset () 
      {
        return 0;    
      }
  }
