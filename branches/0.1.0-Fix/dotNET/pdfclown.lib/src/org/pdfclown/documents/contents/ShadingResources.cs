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
using org.pdfclown.documents.contents.colorSpaces;
using org.pdfclown.objects;

using System;
using System.Collections;
using System.Collections.Generic;

namespace org.pdfclown.documents.contents
{
  /**
    <summary>Shading resources collection [PDF:1.6:3.7.2].</summary>
  */
  [PDF(VersionEnum.PDF13)]
  public sealed class ShadingResources
    : ResourceItems<Shading>
  {
    #region static
    #region interface
    internal static ShadingResources Wrap(
      PdfDirectObject baseObject,
      PdfIndirectObject container
      )
    {
      return baseObject == null
        ? null
        : new ShadingResources(baseObject, container);
    }
    #endregion
    #endregion

    #region dynamic
    #region constructors
    public ShadingResources(
      Document context
      ) : base(context)
    {}

    private ShadingResources(
      PdfDirectObject baseObject,
      PdfIndirectObject container
      ) : base(baseObject, container)
    {}
    #endregion

    #region interface
    #region protected
    protected override Shading Wrap(
      PdfDirectObject baseObject
      )
    {return Shading.Wrap(baseObject, Container);}
    #endregion
    #endregion
    #endregion
  }
}