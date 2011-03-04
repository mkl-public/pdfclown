/*
  Copyright 2010 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.NotImplementedException;

/**
  Collection of a specific resource type.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public abstract class ResourceItems<TValue extends PdfObjectWrapper<?>>
  extends PdfObjectWrapper<PdfDictionary>
  implements Map<PdfName,TValue>
{
  // <class>
  // <dynamic>
  // <constructors>
  protected ResourceItems(
    Document context
    )
  {super(context.getFile(), new PdfDictionary());}

  ResourceItems(
    PdfDirectObject baseObject,
    PdfIndirectObject container
    )
  {super(baseObject, container);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ResourceItems<TValue> clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Gets the key associated to a given value.
  */
  public PdfName getKey(
    TValue value
    )
  {return getBaseDataObject().getKey(value.getBaseObject());}

  // <Map>
  @Override
  public void clear(
    )
  {getBaseDataObject().clear();}

  @Override
  public boolean containsKey(
    Object key
    )
  {return getBaseDataObject().containsKey(key);}

  @Override
  @SuppressWarnings("unchecked")
  public boolean containsValue(
    Object value
    )
  {return getBaseDataObject().containsValue(((TValue)value).getBaseObject());}

  @Override
  public Set<Map.Entry<PdfName,TValue>> entrySet(
    )
  {throw new NotImplementedException();}

  @Override
  public TValue get(
    Object key
    )
  {return wrap(getBaseDataObject().get(key));}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public Set<PdfName> keySet(
    )
  {return getBaseDataObject().keySet();}

  @Override
  public TValue put(
    PdfName key,
    TValue value
    )
  {return wrap(getBaseDataObject().put(key,value.getBaseObject()));}

  @Override
  public void putAll(
    Map<? extends PdfName,? extends TValue> entries
    )
  {throw new NotImplementedException();}

  @Override
  public TValue remove(
    Object key
    )
  {return wrap(getBaseDataObject().remove(key));}

  @Override
  public int size(
    )
  {return getBaseDataObject().size();}

  @Override
  public Collection<TValue> values(
    )
  {
    Collection<TValue> values;
    {
      // Get the low-level objects!
      Collection<PdfDirectObject> valueObjects = getBaseDataObject().values();
      // Populating the high-level collection...
      values = new ArrayList<TValue>(valueObjects.size());
      for(PdfDirectObject valueObject : valueObjects)
      {values.add(wrap(valueObject));}
    }
    return values;
  }
  // </Map>
  // </public>

  // <protected>
  /**
    Wraps a base object within its corresponding high-level representation.
  */
  protected abstract TValue wrap(
    PdfDirectObject baseObject
    );
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}