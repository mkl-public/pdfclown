/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library"
  (the Program): see the accompanying README files for more info.

  This Program is free software; you can redistribute it and/or modify it under the terms
  of the GNU Lesser General Public License as published by the Free Software Foundation;
  either version 3 of the License, or (at your option) any later version.

  This Program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  either expressed or implied; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this
  Program (see README files); if not, go to the GNU website (http://www.gnu.org/licenses/).

  Redistribution and use, with or without modification, are permitted provided that such
  redistributions retain the above copyright notice, license and disclaimer, along with
  this list of conditions.
*/

package org.pdfclown.objects;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.pdfclown.bytes.IOutputStream;

/**
  PDF real number object [PDF:1.6:3.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 03/22/11
*/
public final class PdfReal
  extends PdfNumber<Float>
{
  // <class>
  // <static>
  // <fields>
  protected static final DecimalFormat formatter;
  // </fields>

  // <constructors>
  static
  {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    formatter = new DecimalFormat("0.#####",symbols);
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the object equivalent to the given value.
  */
  public static PdfReal get(
    Float value
    )
  {return value == null ? null : new PdfReal(value);}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public PdfReal(
    float value
    )
  {setRawValue(value);}

  public PdfReal(
    double value
    )
  {this((float)value);}
  // </constructors>

  // <iterface>
  // <public>
  @Override
  public Float getValue(
    )
  {return super.getValue().floatValue();}

  @Override
  public void writeTo(
    IOutputStream stream
    )
  {stream.write(formatter.format(getRawValue()));}
  // </public>

  // <protected>
  @Override
  protected void setValue(
    Object value
    )
  {super.setValue(((Number)value).floatValue());}
  // </protected>
  // </interface>
  // </class>
}