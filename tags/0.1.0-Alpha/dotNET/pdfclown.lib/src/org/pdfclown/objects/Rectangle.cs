/*
  Copyright 2010 Stefano Chizzolini. http://www.pdfclown.org

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

using org.pdfclown.documents;
using org.pdfclown.files;

using System;
using System.Drawing;

namespace org.pdfclown.objects
{
  /**
    <summary>PDF rectangle object [PDF:1.6:3.8.4].</summary>
    <remarks>
      <para>Rectangles are described by two diagonally-opposite corners. Corner pairs which don't respect
      the canonical form (lower-left and upper-right) are automatically normalized to provide a consistent
      representation.</para>
      <para>Coordinates are expressed within the PDF coordinate space (lower-left origin and positively-oriented
      axes).</para>
    </remarks>
  */
  public sealed class Rectangle
    : PdfObjectWrapper<PdfArray>
  {
    #region static
    #region interface
    #region private
    private static PdfArray Normalize(
      PdfArray rectangle
      )
    {
      if(rectangle[0].CompareTo(rectangle[2]) > 0)
      {
        PdfDirectObject leftCoordinate = rectangle[2];
        rectangle[2] = rectangle[0];
        rectangle[0] = leftCoordinate;
      }
      if(rectangle[1].CompareTo(rectangle[3]) > 0)
      {
        PdfDirectObject bottomCoordinate = rectangle[3];
        rectangle[3] = rectangle[1];
        rectangle[1] = bottomCoordinate;
      }
      return rectangle;
    }
    #endregion
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public Rectangle(
      RectangleF rectangle
      ) : this(
        rectangle.Left,
        rectangle.Bottom,
        rectangle.Width,
        rectangle.Height
        )
    {}

    public Rectangle(
      PointF lowerLeft,
      PointF upperRight
      ) : this(
        lowerLeft.X,
        upperRight.Y,
        upperRight.X-lowerLeft.X,
        upperRight.Y-lowerLeft.Y
        )
    {}

    public Rectangle(
      double left,
      double top,
      double width,
      double height
      ) : this(
        new PdfArray(
          new PdfDirectObject[]
          {
            new PdfReal(left), // Left (X).
            new PdfReal(top - height), // Bottom (Y).
            new PdfReal(left + width), // Right.
            new PdfReal(top) // Top.
          }
          )
        )
    {}
    //TODO:integrate with the container update infrastructure (see other PdfObjectWrapper subclass implementations)!!
    public Rectangle(
      PdfDirectObject baseObject
      ) : base(Normalize((PdfArray)File.Resolve(baseObject)), null)
    {}
    #endregion

    #region interface
    #region public
    public float Bottom
    {
      get
      {return ((IPdfNumber)BaseDataObject[1]).RawValue;}
      set
      {((IPdfNumber)BaseDataObject[1]).RawValue = value;}
    }

    public override object Clone(
      Document context
      )
    {throw new NotImplementedException();}

    public float Height
    {
      get
      {return (Top - Bottom);}
      set
      {Bottom = Top - value;}
    }

    public float Left
    {
      get
      {return ((IPdfNumber)BaseDataObject[0]).RawValue;}
      set
      {((IPdfNumber)BaseDataObject[0]).RawValue = value;}
    }

    public float Right
    {
      get
      {return ((IPdfNumber)BaseDataObject[2]).RawValue;}
      set
      {((IPdfNumber)BaseDataObject[2]).RawValue = value;}
    }

    public float Top
    {
      get
      {return ((IPdfNumber)BaseDataObject[3]).RawValue;}
      set
      {((IPdfNumber)BaseDataObject[3]).RawValue = value;}
    }

    public RectangleF ToRectangleF(
      )
    {return new RectangleF(X, Y, Width, Height);}

    public float Width
    {
      get
      {return Right - Left;}
      set
      {Right = Left + value;}
    }

    public float X
    {
      get
      {return Left;}
      set
      {Left = value;}
    }

    public float Y
    {
      get
      {return Bottom;}
      set
      {Bottom = value;}
    }
    #endregion
    #endregion
    #endregion
  }
}