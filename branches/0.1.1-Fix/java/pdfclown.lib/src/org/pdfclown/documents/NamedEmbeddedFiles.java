/*
  Copyright 2008-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.fileSpecs.FileSpec;
import org.pdfclown.objects.NameTree;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.NotImplementedException;

/**
  Named embedded files [PDF:1.6:3.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 04/10/11
*/
@PDF(VersionEnum.PDF14)
public final class NamedEmbeddedFiles
  extends NameTree<FileSpec>
{
  // <class>
  // <dynamic>
  // <constructors>
  public NamedEmbeddedFiles(
    Document context
    )
  {super(context);}

  public NamedEmbeddedFiles(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public NamedEmbeddedFiles clone(
    Document context
    )
  {throw new NotImplementedException();}
  // </public>

  // <protected>
  @Override
  protected FileSpec wrap(
    PdfDirectObject baseObject,
    PdfString name
    )
  {return new FileSpec(baseObject, name);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}