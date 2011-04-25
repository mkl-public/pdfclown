/*
  Copyright 2009-2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfIndirectObject;

/**
  Graphics state parameter resources collection [PDF:1.6:3.7.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
@PDF(VersionEnum.PDF12)
public final class ExtGStateResources
  extends ResourceItems<ExtGState>
{
  // <class>
  // <static>
  // <interface>
  static ExtGStateResources wrap(
    PdfDirectObject baseObject,
    PdfIndirectObject container
    )
  {
    return baseObject == null
      ? null
      : new ExtGStateResources(baseObject, container);
  }
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ExtGStateResources(
    Document context
    )
  {super(context);}

  private ExtGStateResources(
    PdfDirectObject baseObject,
    PdfIndirectObject container
    )
  {super(baseObject, container);}
  // </constructors>

  // <interface>
  // <protected>
  @Override
  protected ExtGState wrap(
    PdfDirectObject baseObject
    )
  {return ExtGState.wrap(baseObject, getContainer());}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}