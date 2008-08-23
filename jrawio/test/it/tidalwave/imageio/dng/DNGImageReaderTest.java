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
 * $Id: ThumbnailHelper.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.dng;

import java.io.IOException;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.LoadTestSupport;
import org.junit.Test;
import static org.junit.Assert.*;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
 *
 ******************************************************************************/
public class DNGImageReaderTest extends LoadTestSupport
  {
    @Test
    public void testReadPICT0006_MRW() 
      throws IOException 
      {
        final ImageReader ir = getImageReader("esordini/Canon/EOS300D/Adobe/DNG/100_0056.DNG");
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3088, 2055);
        assertThumbnail(ir, 0, 256, 171);
        assertThumbnail(ir, 1, 1024, 683);
        assertLoadImage(ir, 3088, 2055);
        assertLoadThumbnail(ir, 0, 256, 171);
        assertLoadThumbnail(ir, 1, 1024, 683);
        close(ir);
      }
    
    @Test
    public void testReadPICT0006_MRW1() 
      throws IOException 
      {
        final ImageReader ir = getImageReader("esordini/Canon/EOS300D/Adobe/DNG/100_0043.DNG");
        assertEquals(1, ir.getNumImages(false));
        assertEquals(2, ir.getNumThumbnails(0));
        assertImage(ir, 3088, 2055);
        assertThumbnail(ir, 0, 256, 171);
        assertThumbnail(ir, 1, 1024, 683);
        assertLoadImage(ir, 3088, 2055);
        assertLoadThumbnail(ir, 0, 256, 171);
        assertLoadThumbnail(ir, 1, 1024, 683);
        close(ir);
      }
  }
