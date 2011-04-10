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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Chunk;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.util.NotImplementedException;

/**
  PDF array object [PDF:1.6:3.2.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.0
  @version 0.1.1, 04/10/11
*/
public final class PdfArray
  extends PdfDirectObject
  implements List<PdfDirectObject>
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginArrayChunk = Encoding.encode(Keyword.BeginArray);
  private static final byte[] EndArrayChunk = Encoding.encode(Keyword.EndArray);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private ArrayList<PdfDirectObject> items;

  private PdfObject parent;
  private boolean updated;
  // </fields>

  // <constructors>
  public PdfArray(
    )
  {this(10);}

  public PdfArray(
    int capacity
    )
  {items = new ArrayList<PdfDirectObject>(capacity);}

  public PdfArray(
    PdfDirectObject... items
    )
  {
    this(items.length);

    for(PdfDirectObject item : items)
    {add(item);}
    ready();
  }

  public PdfArray(
    List<? extends PdfDirectObject> items
    )
  {
    this(items.size());

    addAll(items);
    ready();
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfArray clone(
    File context
    )
  {
    PdfArray clone = (PdfArray)super.clone(context);
    {
      clone.items = new ArrayList<PdfDirectObject>(items.size());
      for(PdfDirectObject item : items)
      {clone.add((PdfDirectObject)PdfObject.clone(item, context));}
    }
    return clone;
  }

  @Override
  public int compareTo(
    PdfDirectObject obj
    )
  {throw new NotImplementedException();}

  @Override
  public PdfIndirectObject getContainer(
    )
  {return getRoot();}

  @Override
  public PdfObject getParent(
    )
  {return parent;}

  @Override
  public PdfIndirectObject getRoot(
    )
  {return parent != null ? parent.getRoot() : null;}

  /**
    Gets the dereferenced value corresponding to the given index.
    <h3>Remarks</h3>
    <p>This method takes care to resolve the value returned by {@link #get(int)}.</p>

    @param index Index of element to return.

    @since 0.0.8
   */
  public PdfDataObject resolve(
    int index
    )
  {return File.resolve(get(index));}

  @Override
  public String toString(
    )
  {
    StringBuilder buffer = new StringBuilder();
    {
      // Begin.
      buffer.append("[ ");
      // Elements.
      for(PdfDirectObject item : items)
      {buffer.append(PdfDirectObject.toString(item)).append(" ");}
      // End.
      buffer.append("]");
    }
    return buffer.toString();
  }

  @Override
  public void writeTo(
    IOutputStream stream
    )
  {
    // Begin.
    stream.write(BeginArrayChunk);
    // Items.
    for(PdfDirectObject item : items)
    {PdfDirectObject.writeTo(stream,item); stream.write(Chunk.Space);}
    // End.
    stream.write(EndArrayChunk);
  }

  // <List>
  @Override
  public void add(
    int index,
    PdfDirectObject item
    )
  {
    items.add(index, (PdfDirectObject)include(item));
    update();
  }

  @Override
  public boolean addAll(
    int index,
    Collection<? extends PdfDirectObject> items
    )
  {
    for(PdfDirectObject item : items)
    {add(index++, item);}
    return true;
  }

  @Override
  public PdfDirectObject get(
    int index
    )
  {return items.get(index);}

  @Override
  public int indexOf(
    Object item
    )
  {return items.indexOf(item);}

  @Override
  public int lastIndexOf(
    Object item
    )
  {return items.lastIndexOf(item);}

  @Override
  public ListIterator<PdfDirectObject> listIterator(
    )
  {return items.listIterator();}

  @Override
  public ListIterator<PdfDirectObject> listIterator(
    int index
    )
  {return items.listIterator(index);}

  @Override
  public PdfDirectObject remove(
    int index
    )
  {
    PdfDirectObject oldItem = items.remove(index);
    exclude(oldItem);
    update();
    return oldItem;
  }

  @Override
  public PdfDirectObject set(
    int index,
    PdfDirectObject item
    )
  {
    PdfDirectObject oldItem = items.set(index, item = (PdfDirectObject)include(item));
    exclude(oldItem);
    update();
    return oldItem;
  }

  @Override
  public List<PdfDirectObject> subList(
    int fromIndex,
    int toIndex
    )
  {return items.subList(fromIndex,toIndex);}

  // <Collection>
  @Override
  public boolean add(
    PdfDirectObject item
    )
  {
    items.add(item = (PdfDirectObject)include(item));
    update();
    return true;
  }

  @Override
  public boolean addAll(
    Collection<? extends PdfDirectObject> items
    )
  {
    for(PdfDirectObject item : items)
    {add(item);}
    return true;
  }

  @Override
  public void clear(
    )
  {removeAll(items);}

  @Override
  public boolean contains(
    Object item
    )
  {return items.contains(item);}

  @Override
  public boolean containsAll(
    Collection<?> items
    )
  {return this.items.containsAll(items);}

  @Override
  public boolean equals(
    Object object
    )
  {
    return object != null
      && object.getClass().equals(getClass())
      && ((PdfArray)object).items.equals(items);
  }

  @Override
  public int hashCode(
    )
  {return items.hashCode();}

  @Override
  public boolean isEmpty(
    )
  {return items.isEmpty();}

  @Override
  public boolean remove(
    Object item
    )
  {
    if(!items.remove(item))
      return false;

    exclude((PdfDirectObject)item);
    update();
    return true;
  }

  @Override
  public boolean removeAll(
    Collection<?> items
    )
  {
    for(Object item : items)
    {remove(item);}
    return true;
  }

  @Override
  public boolean retainAll(
    Collection<?> items
    )
  {
    int index = 0;
    while(index < this.items.size())
    {
      if(!items.contains(get(index)))
      {remove(index);}
      else
      {index++;}
    }
    return true;
  }

  @Override
  public int size(
    )
  {return items.size();}

  @Override
  public Object[] toArray(
    )
  {return items.toArray();}

  @Override
  public <T> T[] toArray(
    T[] items
    )
  {return this.items.toArray(items);}

  // <Iterable>
  @Override
  public Iterator<PdfDirectObject> iterator(
    )
  {return items.iterator();}
  // </Iterable>
  // </Collection>
  // </List>
  // </public>

  // <protected>
  @Override
  protected boolean isUpdated(
    )
  {return updated;}

  @Override
  protected void setUpdated(
    boolean value
    )
  {updated = value;}
  // </protected>

  // <internal>
  @Override
  void setParent(
    PdfObject value
    )
  {parent = value;}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}