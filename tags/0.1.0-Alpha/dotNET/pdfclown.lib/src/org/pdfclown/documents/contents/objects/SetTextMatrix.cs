/*
  Copyright 2007-2010 Stefano Chizzolini. http://www.pdfclown.org

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

using System;
using System.Collections.Generic;
using System.Drawing.Drawing2D;

namespace org.pdfclown.documents.contents.objects
{
  /**
    <summary>'Set the text matrix' operation [PDF:1.6:5.3.1].</summary>
    <remarks>The specified matrix is not concatenated onto the current text matrix,
    but replaces it.</remarks>
  */
  [PDF(VersionEnum.PDF10)]
  public sealed class SetTextMatrix
    : Operation
  {
    #region static
    #region fields
    public static readonly string OperatorKeyword = "Tm";
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public SetTextMatrix(
      Matrix value
      ) : this(
        value.Elements[0],
        value.Elements[1],
        value.Elements[2],
        value.Elements[3],
        value.Elements[4],
        value.Elements[5]
        )
    {}

    public SetTextMatrix(
      double a,
      double b,
      double c,
      double d,
      double e,
      double f
      ) : base(
        OperatorKeyword,
        new PdfReal(a),
        new PdfReal(b),
        new PdfReal(c),
        new PdfReal(d),
        new PdfReal(e),
        new PdfReal(f)
        )
    {}

    public SetTextMatrix(
      IList<PdfDirectObject> operands
      ) : base(OperatorKeyword, operands)
    {}
    #endregion

    #region interface
    #region public
    public override void Scan(
      ContentScanner.GraphicsState state
      )
    {
      state.Tlm = Value;
      state.Tm = state.Tlm.Clone();
    }

    public Matrix Value
    {
      get
      {
        return new Matrix(
          ((IPdfNumber)operands[0]).RawValue,
          ((IPdfNumber)operands[1]).RawValue,
          ((IPdfNumber)operands[2]).RawValue,
          ((IPdfNumber)operands[3]).RawValue,
          ((IPdfNumber)operands[4]).RawValue,
          ((IPdfNumber)operands[5]).RawValue
          );
      }
    }
    #endregion
    #endregion
    #endregion
  }
}