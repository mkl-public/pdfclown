/*
  Copyright 2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.files.File;
import org.pdfclown.objects.IPdfObjectWrapper;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;

/**
  Object that can be inserted into a hierarchical layer structure.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.1, 06/08/11
*/
public interface ILayerNode
  extends IPdfObjectWrapper
{
  /**
    Gets the sublayers.
  */
  Layers getLayers(
    );

  /**
    Gets the text label.
  */
  String getTitle(
    );

  /**
    @see #getTitle()
  */
  void setTitle(
    String value
    );
}

final class LayerNode
{
  public static ILayerNode wrap(
    PdfDirectObject baseObject
    )
  {
    PdfDataObject baseDataObject = File.resolve(baseObject);
    if(baseDataObject instanceof PdfDictionary)
      return new Layer(baseObject);
    else if(baseDataObject instanceof PdfArray)
      return new Layers(baseObject);
    else if(baseDataObject == null)
      return null;
    else
      throw new IllegalArgumentException(baseDataObject.getClass().getSimpleName() + " is NOT a valid layer node.");
  }

  private LayerNode(
    )
  {}
}