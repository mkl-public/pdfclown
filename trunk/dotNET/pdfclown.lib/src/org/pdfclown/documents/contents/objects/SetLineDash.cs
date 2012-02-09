/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.bytes;
using org.pdfclown.objects;

using System.Collections.Generic;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>'Set the line dash pattern' operation [PDF:1.6:4.3.3].</summary>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class SetLineDash
    : Operation
  {
    #region static
    #region fields
    public static readonly string OperatorKeyword = "d";
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public SetLineDash(
      double phase,
      double unitsOn,
      double unitsOff
      ) : base(
        OperatorKeyword,
        new PdfArray(
          PdfReal.Get(unitsOn),
          PdfReal.Get(unitsOff)
          ),
        PdfReal.Get(phase)
        )
    {}

    public SetLineDash(
      IList<PdfDirectObject> operands
      ) : base(OperatorKeyword, operands)
    {}
    #endregion

    #region interface
    #region public
    public override void Scan(
      ContentScanner.GraphicsState state
      )
    {state.LineDash = Value;}

    public LineDash Value
    {
      get
      {
        // 1. Dash array.
        PdfArray baseDashArray = (PdfArray)operands[0];
        double[] dashArray = new double[baseDashArray.Count];
        for(
          int index = 0,
            length = dashArray.Length;
          index < length;
          index++
          )
        {dashArray[index] = ((IPdfNumber)baseDashArray[index]).RawValue;}
        // 2. Dash phase.
        double dashPhase = ((IPdfNumber)operands[1]).RawValue;

        return new LineDash(dashArray, dashPhase);
      }
      set
      {
        // 1. Dash array.
        double[] dashArray = value.DashArray;
        PdfArray baseDashArray = new PdfArray(dashArray.Length);
        for(
          int index = 0,
            length = dashArray.Length;
          index < length;
          index++
          )
        {baseDashArray[index] = PdfReal.Get(dashArray[index]);}
        operands[0] = baseDashArray;
        // 2. Dash phase.
        operands[1] = PdfReal.Get(value.DashPhase);
      }
    }
    #endregion
    #endregion
    #endregion
  }
}