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
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.rawprocessor.arw;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWProcessorTest extends NewImageReaderTestSupport
  {
    public ARWProcessorTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // A100
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/kijiro/Sony/A100/ARW/DSC00041.ARW").
                            image(3872, 2592, 3, 8, "0fadabd72e76c535e39216d68a55d4b9").
                            thumbnail(640, 480).
                            thumbnail(160, 120).
                            issues("JRW-127", "JRW-198", "JRW-209"),
            // A200
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a200/RAW_SONY_A200.ARW").
                            image(3880, 2600, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A300
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a300/RAW_SONY_A300.ARW").
                            image(3880, 2600, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A350
            ExpectedResults.create("http://www.rawsamples.ch/raws/sony/a350/RAW_SONY_A350.ARW").
                            image(4600, 3064, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1920, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A700
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A700_35mm_f1.4G_aperture_test_Ninik/DSC01592.ARW").
                            image(4288, 2856, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258"),
            // A900
            ExpectedResults.create("http://raw.fotosite.pl/download-Sony_A900_Carl_Zeiss_85mm_f1.4_by_Ninik/DSC08682.ARW").
                            image(6080, 4048, 3, 8, "90e70a15bb3bd63ea4dacb1ea9841653").
                            thumbnail(1616, 1080).
                            thumbnail(160, 120).
                            issues("JRW-257", "JRW-258")
          );
      }
  }
