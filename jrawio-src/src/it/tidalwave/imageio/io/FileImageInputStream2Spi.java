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
 * $Id: FileImageInputStream2Spi.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.util.Iterator;
import java.util.Locale;
import java.io.File;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.Version;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: FileImageInputStream2Spi.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class FileImageInputStream2Spi extends ImageInputStreamSpi 
  {
    private static final String vendorName = "www.tidalwave.it";

    private static final String version = Version.BUILD;

    private static final Class inputClass = File.class;

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public FileImageInputStream2Spi()
      {
        super(vendorName, version, inputClass);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Service provider that wraps a FileImageInputStream";
      }

    /*******************************************************************************
     * 
     * Upon registration, this method ensures that this SPI is listed at the top
     * of the ImageInputStreamSpi items, so that it will be invoked before the
     * default FileImageInputStreamSpi
     * 
     * @param registry  the registry
     * @param category  the registration category
     * 
     *******************************************************************************/
    public void onRegistration (ServiceRegistry registry, Class category)
      {
        super.onRegistration(registry, category);
        
        for (Iterator i = registry.getServiceProviders(ImageInputStreamSpi.class, true); i.hasNext();)
          {
            Object other = i.next();

            if (this != other)
              {
                registry.setOrdering(ImageInputStreamSpi.class, this, other);
              }
          }
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public ImageInputStream createInputStreamInstance (Object input, boolean useCache, File cacheDir)
      {
        if (input instanceof File)
          {
            try
              {
                return new FileImageInputStream2((File)input);
              }
            catch (Exception e)
              {
                return null;
              }
          }

        throw new IllegalArgumentException();
      }
  }
