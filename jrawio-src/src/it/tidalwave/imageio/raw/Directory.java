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
 * $Id: Directory.java,v 1.4 2006/02/08 22:32:01 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: Directory.java,v 1.4 2006/02/08 22:32:01 fabriziogiudici Exp $
 *
 ******************************************************************************/
public abstract class Directory implements Serializable
  {
    private final static long serialVersionUID = 7068468438676854749L;

    private final static String CLASS = "it.tidalwave.imageio.raw.Directory";

    private final static Logger logger = Logger.getLogger(CLASS);

    protected long start;
    
    protected long end;
    
    /** The map of tags. */
    private Map tagMapByKey = new HashMap();

    /** The list of tags, in the same order as they were added. */
    private List tagList = new ArrayList();
    
    public Directory nextDirectory; // FIXME
    
    /** The list of sub-directories. */
    private Collection directoryList = new ArrayList();

    /** The map of named directories. */
    private Map directoryMapByName = new HashMap();

    /** The registry the contained tags belongs to. This is transient so that this class can
     * be serialized without carrying along the whole registry! Upon deserialization
     * the link to the registry must be restored by using the registryName.
     */
    protected transient TagRegistry tagRegistry;

    /** The name of the registry this tag belongs to. */
    private String registryName;

    /*******************************************************************************
     *
     * This class models an enumerated value for a tag.
     *
     ******************************************************************************/
    public static class Enumeration implements Serializable
      {
        private final static long serialVersionUID = 4029468438676854749L;

        /** The tag value. */
        private int intValue;
        
        private String stringValue;

        /** The tag name. */
        private String description;

        protected Enumeration (int value, String description)
          {
            this.intValue = value;
            this.description = description;
          }

        protected Enumeration (int[] value, String description)
          {
            this(value[0], description);
          }

        protected Enumeration (String value, String description)
          {
            this.stringValue = value;
            this.description = description;
          }

        protected Enumeration (String[] value, String description)
          {
            this(value[0], description);
          }

        public int intValue ()
          {
            return intValue;
          }

        public String toString ()
          {
            if (stringValue != null)
              {
                return description.equals("reserved") ? (description + "#" + stringValue) : description;
              }
            
            else
              {
                return description.equals("reserved") ? (description + "#" + intValue) : description;
              }
          }

        public int hashCode ()
          {
            if (stringValue != null)
              {
                return stringValue.hashCode();
              }
            
            else
              {
                return intValue;
              }
          }

        public boolean equals (Object object)
          {
            if (this.getClass() != object.getClass())
              {
                return false;
              }

            Enumeration e = (Enumeration)object;

            if (stringValue != null)
              {
                return this.stringValue.equals(e.stringValue);
              }
            
            else
              {
                return this.intValue == e.intValue;
              }
          }
        
        protected static boolean equals (int i1, int i2)
          {
            return i1 == i2;  
          }
        
        protected static boolean equals (int i1[], int i2)
          {
            return i1[0] == i2;  
          }
        
        protected static boolean equals (String s1, String s2)
          {
            return trim(s1).equals(trim(s2));   
          }
        
        private static String trim (String s)
          {
            return s.trim(); // TODO: also remove leading zeros   
          }
      }

    /*******************************************************************************
     * 
     * For de-serialization only.
     * 
     *******************************************************************************/
    protected Directory ()
      {
      }

    /*******************************************************************************
     * 
     * Creates a new <code>Directory</code> whose tags belong to the given registry.
     * 
     * @param tagRegistry  the registry
     * 
     *******************************************************************************/
    protected Directory (TagRegistry tagRegistry)
      {
        this.tagRegistry = tagRegistry;
        registryName = tagRegistry.getName();
      }

    /*******************************************************************************
     * 
     * Returns the registry the contained tags belong to.
     * 
     * @return  the registry
     * 
     *******************************************************************************/
    public TagRegistry getRegistry ()
      {
        return tagRegistry;
      }
    
    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public long getStart()
      {
        return start;
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public long getEnd()
      {
        return end;
      }

    /*******************************************************************************
     * 
     * @param iis
     * @param offset
     * @throws IOException
     * 
     *******************************************************************************/
    public abstract long load (RAWImageInputStream iis, long offset) throws IOException;

    /*******************************************************************************
     * 
     * @param iis
     * @param offset
     * @throws IOException
     * 
     *******************************************************************************/
    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        load(iis, offset);   
      }
     
    /*******************************************************************************
     *
     * Adds a tag to this <code>Directory</code>.
     * 
     * @param  tag  the tag to add
     * 
     ******************************************************************************/
    public void addTag (AbstractTag tag)
      {
        if (tag != null)
          {
            Object key = new Integer(tag.getCode());
            tagMapByKey.put(tagRegistry.getKey(key), tag);
            tagList.add(tagRegistry.getKey(key));
          }
      }

    /*******************************************************************************
     *
     * Retrieves a contained tag given its key.
     * 
     * @param  key  the tag key
     * 
     ******************************************************************************/
    public AbstractTag getTag (Object key)
      {
        return (AbstractTag)tagMapByKey.get(tagRegistry.getKey(key));
      }

    /*******************************************************************************
     *
     * Checks if this <code>Directory</code> contains a given tag.
     * 
     * @param  key  the tag key
     * @return      true if this <code>Directory</code> contains the tag
     * 
     ******************************************************************************/
    public boolean containsTag (Object key)
      {
        return (tagRegistry != null) && tagMapByKey.containsKey(tagRegistry.getKey(key));
      }

    /*******************************************************************************
     *
     * Removes a given tag from this </code>Directory</code>
     * 
     * @param  key  the tag key
     * @return      the remove tag
     * 
     ******************************************************************************/
    public AbstractTag removeTag (Object key)
      {
        tagList.remove(tagRegistry.getKey(key));
        return (AbstractTag)tagMapByKey.remove(tagRegistry.getKey(key));
      }

    /*******************************************************************************
     *
     * Returns the name of the given tag. FIXME: is meaningful here?
     * 
     * @param  key  the tag key
     * @return      the tag name
     * 
     ******************************************************************************/
    public String getTagName (Object key)
      {
        return tagRegistry.getTagName(((Number)key).intValue());
      }

    /*******************************************************************************
     *
     * FIXME: This method is only provided for supporting 
     * it.tidalwave.image.DirectorySupport, but should be replaced with a more
     * decoupled mechanism. This method returns a plain Number when an enumeration
     * could be expected.
     * 
     ******************************************************************************/
    public Object getObject (Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getValue() : null;
      }

    /*******************************************************************************
     *
     * Returns the tag values as bytes.
     * 
     * @param  key  the tag key
     * @return      the bytes or null if the tag is not present
     * 
     ******************************************************************************/
    public byte[] getBytes (Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getByteValues() : null;
      }

    /*******************************************************************************
     *
     * Returns the tag value as a byte.
     * 
     * @param  key  the tag key
     * @return      the byte
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************/
    public int getByte (Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getBytes(key)[0];
      }

    /*******************************************************************************
     *
     * Returns the tag values as rationals.
     * 
     * @param  key  the tag key
     * @return      the rationals or null if the tag is not present
     * 
     ******************************************************************************/
    public TagRational[] getRationals (Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getRationalValues() : null;
      }

    /*******************************************************************************
     *
     * Returns the tag value as a rational.
     * 
     * @param  key  the tag key
     * @return      the rational
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************/
    public TagRational getRational (Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0];
      }

    /*******************************************************************************
     *
     * Returns the tag values as doubles.
     * 
     * @param  key  the tag key
     * @return      the doubles or null if the tag is not present
     * 
     ******************************************************************************/
    public double[] getDoubles (Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? asDoubles(tag.getRationalValues()) : null;
      }

    /*******************************************************************************
     *
     * Returns the tag value as a double.
     * 
     * @param  key  the tag key
     * @return      the double
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************/
    public double getDouble (Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0].doubleValue();
      }

    /*******************************************************************************
     *
     * Returns the tag values as floats.
     * 
     * @param  key  the tag key
     * @return      the floats or null if the tag is not present
     * 
     ******************************************************************************/
    public float[] getFloats (Object key)
      {
        AbstractTag tag = getTag(key);
        return (tag != null) ? tag.getFloatValues() : null;
      }

    /*******************************************************************************
     *
     * Returns the tag value as a float.
     * 
     * @param  key  the tag key
     * @return      the float
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************/
    public float getFloat (Object key)
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        return getRationals(key)[0].floatValue();
      }

    /*******************************************************************************
     *
     * Returns the tag values as integers.
     * 
     * @param  key  the tag key
     * @return      the integers or null if the tag is not present
     * 
     ******************************************************************************/
    public int[] getIntegers (Object key)
      {
        AbstractTag tag = getTag(key);

        if (tag == null)
          {
            return null;
          }

        int[] intValues = tag.getIntValues();

        if (intValues != null)
          {
            return intValues;
          }

        byte[] byteValues = tag.getByteValues();

        if (byteValues != null)
          {
            intValues = new int[byteValues.length];

            for (int i = 0; i < byteValues.length; i++)
              {
                intValues[i] = byteValues[i] & 0xff;
              }

            return intValues;
          }

        return null;
      }

    /*******************************************************************************
     *
     * Returns the tag value as an integer.
     * <br>
     * This method returns 0 if there exists a tag with the given key which contains
     * an empty array of integers. Such a tag is BayerGreenSplit in DNG files.
     * It should be clarified if this is compliant with specs (in which case this
     * behaviour should be extended to other multiple-value getXXX() methods, or if
     * it is a bug of Adobe Converter.
     * 
     * @param  key  the tag key
     * @return      the integer
     * @throws      NoSuchElementException  if the tag is not present
     * 
     ******************************************************************************/
    public int getInteger (Object key) throws NoSuchElementException
      {
        if (!containsTag(key))
          {
            throw new NoSuchElementException("No tags with key = " + key);
          }

        int[] i = getIntegers(key);

        if (i.length == 0) // FIXME: happens with a BayerGreenSplit field in DNG files
          {
            return 0;
          }

        return i[0];
      }

    /*******************************************************************************
     *
     * Returns the tag value as a string.
     * 
     * @param  key  the tag key
     * @return      the string or null if the tag has not been found
     * 
     ******************************************************************************/
    public String getString (Object key)
      {
        AbstractTag tag = getTag(key);

        return (tag != null) ? tag.getASCIIValue() : null;
      }

    /*******************************************************************************
     *
     * 
     ******************************************************************************/
    public Directory getNextDirectory()
      {
        return nextDirectory;
      }
      
    /*******************************************************************************
     *
     * Adds a sub-directory.
     * 
     * @param   subDirectory  the sub-directory
     * 
     ******************************************************************************/
    public void addDirectory (Directory subDirectory)
      {
        directoryList.add(subDirectory);
      }

    /*******************************************************************************
     *
     * Returns an iterator over sub-directories. Note that named directories are 
     * not returned.
     * 
     * @return  the iterator
     * 
     ******************************************************************************/
    public Iterator subDirectories ()
      {
        return Collections.unmodifiableCollection(directoryList).iterator();
      }

    /*******************************************************************************
     *
     * Adds a named sub-directory.
     * 
     * @param  name       the directory name
     * @param  subDirectory  the sub-directory
     * 
     ******************************************************************************/
    public void addNamedDirectory (String name,
                                   Directory subDirectory)
      {
        directoryMapByName.put(name, subDirectory);
      }

    /*******************************************************************************
     *
     * Returns a named sub-directory
     * 
     * @param   name  the sub-directory name
     * @return        the sub-directory
     * 
     ******************************************************************************/
    public Directory getNamedDirectory (String name)
      {
        return (Directory)directoryMapByName.get(name);
      }

    /*******************************************************************************
     * 
     * Returns the names of named sub directories
     * 
     * @return  the subdirectory names
     * 
     *******************************************************************************/
    public String[] getSubDirectoryNames ()
      {
        return (String[])directoryMapByName.keySet().toArray(new String[0]);
      }

    /*******************************************************************************
     *
     * Returns the contained tags in the same order as they were added.
     * 
     * @return  the contained tags
     * 
     ******************************************************************************/
    public Collection tags ()
      {
        List result = new ArrayList();
        
        for (Iterator i = tagList.iterator (); i.hasNext (); )
          {
            result.add(tagMapByKey.get(i.next()));
          }

        return result;
      }

    /*******************************************************************************
     *
     * Convenience method that converts an array of rationals into floats.
     * 
     * @param   rationals
     * @return  the floats
     * 
     ******************************************************************************/
    public static float[] asFloats (TagRational[] rationals)
      {
        if (rationals == null)
          {
            return null;
          }

        float[] floats = new float[rationals.length];

        for (int i = 0; i < rationals.length; i++)
          {
            floats[i] = rationals[i].floatValue();
          }

        return floats;
      }

    /*******************************************************************************
     *
     * Convenience method that converts an array of rationals into doubles.
     * 
     * @param   rationals
     * @return  the doubles
     * 
     ******************************************************************************/
    public static double[] asDoubles (TagRational[] rationals)
      {
        if (rationals == null)
          {
            return null;
          }

        double[] doubles = new double[rationals.length];

        for (int i = 0; i < rationals.length; i++)
          {
            doubles[i] = rationals[i].doubleValue();
          }

        return doubles;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer("\n\t");

        for (Iterator i = tagMapByKey.keySet().iterator(); i.hasNext();)
          {
            Object name = i.next();
            Object value = tagMapByKey.get(name);
            buffer.append(value);
            buffer.append("\n\t");
          }

        return buffer.toString();
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    protected String toString (byte[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " bytes";
          }

        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toHexString(array[i] & 0xff));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    protected String toString (int[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " integers";
          }

        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    protected String toString (double[] array)
      {
        if (array.length > 64)
          {
            return "" + array.length + " doubles";
          }

        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Double.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    protected String toString (TagRational[] array)
      {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(array[i].toString());
          }

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * Customized deserialization code. This method restores the link to the registry
     * this tag belongs to.
     * 
     * @param  is
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     *******************************************************************************/
    private void readObject (ObjectInputStream is) throws IOException, ClassNotFoundException
      {
        is.defaultReadObject();
        tagRegistry = TagRegistry.getRegistry(registryName);
      }
  }
