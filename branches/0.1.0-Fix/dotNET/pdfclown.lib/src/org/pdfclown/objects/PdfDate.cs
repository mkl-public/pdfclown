/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

  This file should be part of the source code distribution of "PDF Clown library" (the
  Program): see the accompanying README files for more info.

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

using System;
using System.Globalization;
using System.Text;

namespace org.pdfclown.objects
{
  /**
    <summary>PDF date object [PDF:1.6:3.8.3].</summary>
  */
  public sealed class PdfDate
    : PdfString
  {
    #region static
    #region interface
    #region public
    public static DateTime ToDate(
      string value
      )
    {
      StringBuilder dateBuilder = new StringBuilder();
      {
        int length = value.Length;
        // Year (YYYY).
        dateBuilder.Append(value.Substring(2, 4)); // NOTE: Skips the "D:" prefix; Year is mandatory.
        // Month (MM).
        dateBuilder.Append(length < 7 ? "01" : value.Substring(6, 2));
        // Day (DD).
        dateBuilder.Append(length < 9 ? "01" : value.Substring(8, 2));
        // Hour (HH).
        dateBuilder.Append(length < 11 ? "00" : value.Substring(10, 2));
        // Minute (mm).
        dateBuilder.Append(length < 13 ? "00" : value.Substring(12, 2));
        // Second (SS).
        dateBuilder.Append(length < 15 ? "00" : value.Substring(14, 2));
        // Local time / Universal Time relationship (O).
        dateBuilder.Append(length < 16 || value.Substring(16, 1).Equals("Z") ? "+" : value.Substring(16, 1));
        // UT Hour offset (HH').
        dateBuilder.Append(length < 19 ? "00" : value.Substring(17, 2));
        // UT Minute offset (mm').
        dateBuilder.Append(":").Append(length < 22 ? "00" : value.Substring(20, 2));
      }
      return DateTime.ParseExact(
        dateBuilder.ToString(),
        "yyyyMMddHHmmsszzz",
        new CultureInfo("en-US")
        );
    }
    #endregion

    #region private
    private static string Format(
      DateTime value
      )
    {return ("D:" + value.ToString("yyyyMMddHHmmsszzz").Replace(':','\'') + "'");}
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public PdfDate(
      )
    {}

    public PdfDate(
      DateTime value
      )
    {Value = value;}
    #endregion

    #region interface
    #region public
    public override object Value
    {
      get
      {return ToDate(tokens.Encoding.Decode(RawValue));}
      set
      {RawValue = tokens.Encoding.Encode(Format((DateTime)value));}
    }
    #endregion
    #endregion
    #endregion
  }
}
