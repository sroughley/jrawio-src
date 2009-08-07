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
package it.tidalwave.imageio.rawprocessor.raf;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.awt.Insets;
import it.tidalwave.imageio.rawprocessor.RAWImage;
import it.tidalwave.imageio.rawprocessor.raw.SizeOperation;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class RAFSizeOperation extends SizeOperation
  {
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override
    public void init (final @Nonnull RAWImage image)
      throws Exception
      {
        // TODO
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Insets getCrop (final @Nonnull RAWImage image)
      {
        final int left = 4; // FIXME
        final int top = 4;
        final Dimension size = getSize(image);
        return new Insets(top, 
                          left, 
                          image.getImage().getHeight() - top - size.height,
                          image.getImage().getWidth() - left - size.width);
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize (final @Nonnull RAWImage image)
      {
        return new Dimension(2848, 2136); // FIXME
      }
  }
