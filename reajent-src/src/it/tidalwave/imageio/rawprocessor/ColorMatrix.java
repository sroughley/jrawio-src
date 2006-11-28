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
 * $Id: ColorMatrix.java,v 1.1 2006/02/17 15:32:00 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.util.logging.Logger;
import java.text.DecimalFormat;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: ColorMatrix.java,v 1.1 2006/02/17 15:32:00 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class ColorMatrix
  {
    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.rawprocessor.ColorMatrix");

    private final static DecimalFormat FORMAT = new DecimalFormat("+0.0000;-0.0000");

    private double[] c;

    private static final long SHORT_MASK = 0xffff;

    public final static ColorMatrix XYZ_TO_RGB = new ColorMatrix(new double[] 
      {
        0.412453, 0.357580, 0.180423, 0.212671, 0.715160, 0.072169, 0.019334, 0.119193, 0.950227 
      });

    /*******************************************************************************
     * 
     * @param coefficients
     * 
     *******************************************************************************/
    public ColorMatrix (double[] coefficients)
      {
        this.c = coefficients;
      }

    /*******************************************************************************
     * 
     * FIXME: move to EditableImage
     * 
     * @param data
     * @param index
     * 
     *******************************************************************************/
    public void process (short[] data,
                         int index)
      {
        short d0 = compute(0, data, index);
        short d1 = compute(1, data, index);
        short d2 = compute(2, data, index);
        data[index + 0] = d0;
        data[index + 1] = d1;
        data[index + 2] = d2;
      }

    /*******************************************************************************
     * 
     * @param m
     * @return
     * 
     * 012
     * 345
     * 678
     * 
     *******************************************************************************/
    public ColorMatrix product (ColorMatrix m)
      {
        double[] r = new double[9];

        r[2] = c[0] * m.c[2] + c[1] * m.c[5] + c[2] * m.c[8];
        r[5] = c[3] * m.c[2] + c[4] * m.c[5] + c[5] * m.c[8];
        r[8] = c[6] * m.c[2] + c[7] * m.c[5] + c[8] * m.c[8];

        r[1] = c[0] * m.c[1] + c[1] * m.c[4] + c[2] * m.c[7];
        r[4] = c[3] * m.c[1] + c[4] * m.c[4] + c[5] * m.c[7];
        r[7] = c[6] * m.c[1] + c[7] * m.c[4] + c[8] * m.c[7];

        r[0] = c[0] * m.c[0] + c[1] * m.c[3] + c[2] * m.c[6];
        r[3] = c[3] * m.c[0] + c[4] * m.c[3] + c[5] * m.c[6];
        r[6] = c[6] * m.c[0] + c[7] * m.c[3] + c[8] * m.c[6];

        return new ColorMatrix(r);
      }

    /*******************************************************************************
     * 
     * 
     * 
     *******************************************************************************/
    public void normalizeRows()
      {
        double sum = c[0] + c[1] + c[2];
        c[0] /= sum;
        c[1] /= sum;
        c[2] /= sum;
        sum = c[3] + c[4] + c[5];
        c[3] /= sum;
        c[4] /= sum;
        c[5] /= sum;
        sum = c[6] + c[7] + c[8];
        c[6] /= sum;
        c[7] /= sum;
        c[8] /= sum;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public ColorMatrix inverse()
      {
        double cof0 = +(c[4] * c[8] - c[7] * c[5]);
        double cof1 = -(c[3] * c[8] - c[6] * c[5]);
        double cof2 = +(c[3] * c[7] - c[6] * c[4]);
        double cof3 = -(c[1] * c[8] - c[7] * c[2]);
        double cof4 = +(c[0] * c[8] - c[6] * c[2]);
        double cof5 = -(c[0] * c[7] - c[6] * c[1]);
        double cof6 = +(c[1] * c[5] - c[4] * c[2]);
        double cof7 = -(c[0] * c[5] - c[3] * c[2]);
        double cof8 = +(c[0] * c[4] - c[3] * c[1]);

        double det = c[0] * cof0 + c[1] * cof1 + c[2] * cof2;

        double nc[] = new double[9];
        nc[0] = cof0 / det;
        nc[1] = cof3 / det;
        nc[2] = cof6 / det;
        nc[3] = cof1 / det;
        nc[4] = cof4 / det;
        nc[5] = cof7 / det;
        nc[6] = cof2 / det;
        nc[7] = cof5 / det;
        nc[8] = cof8 / det;
        ColorMatrix result = new ColorMatrix(nc);

        logger.finest("inverse(" + this + ") = " + result);
        //        logger.finest(">>>> det = " + det);

        return result;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String toString()
      {
        StringBuffer buffer = new StringBuffer("[");

        for (int i = 0; i < c.length; i++)
          {
            if (i > 0)
              buffer.append(",");

            buffer.append(FORMAT.format(c[i]));
          }

        buffer.append("]");

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * @param data
     * @return
     * 
     *******************************************************************************/
    private short compute (int channel,
                           short[] data,
                           int index)
      {
        double c0 = c[channel * 3 + 0];
        double c1 = c[channel * 3 + 1];
        double c2 = c[channel * 3 + 2];

        double f = c0 * (data[index + 0] & SHORT_MASK) 
                 + c1 * (data[index + 1] & SHORT_MASK) 
                 + c2 * (data[index + 2] & SHORT_MASK);
        int x = (int)f;

        if (x < 0)
          return 0;

        return (short)((x <= SHORT_MASK) ? x : SHORT_MASK);
      }
  }
