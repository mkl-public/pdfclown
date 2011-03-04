/*
  Copyright 2006-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.ObjectStream;
import org.pdfclown.tokens.Parser;
import org.pdfclown.tokens.Symbol;
import org.pdfclown.tokens.XRefEntry;
import org.pdfclown.tokens.XRefEntry.UsageEnum;

/**
  PDF indirect object [PDF:1.6:3.2.9].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public final class PdfIndirectObject
  extends PdfObject
  implements IPdfIndirectObject
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginIndirectObjectChunk = Encoding.encode(Symbol.Space + Keyword.BeginIndirectObject + Symbol.LineFeed);
  private static final byte[] EndIndirectObjectChunk = Encoding.encode(Symbol.LineFeed + Keyword.EndIndirectObject + Symbol.LineFeed);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private PdfDataObject dataObject;
  private File file;
  private boolean original;
  private PdfReference reference;
  private XRefEntry xrefEntry;
  // </fields>

  // <constructors>
  /**
    For internal use only.

    @param file Associated file.
    @param dataObject Data object associated to the indirect object.
      It MUST be null if the indirect object is original (i.e. coming from an existing file) or
      free.
      It MUST be NOT null if the indirect object is new and in-use.
    @param xrefEntry Cross-reference entry associated to the indirect object.
      If the indirect object is new, its offset field MUST be set to 0 (zero).
  */
  public PdfIndirectObject(
    File file,
    PdfDataObject dataObject,
    XRefEntry xrefEntry
    )
  {
    this.file = file;
    this.dataObject = dataObject;
    this.xrefEntry = xrefEntry;

    this.original = (xrefEntry.getOffset() != 0);
    this.reference = new PdfReference(
      this,
      xrefEntry.getNumber(),
      xrefEntry.getGeneration()
      );
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Adds the {@link #getDataObject() data object} to the specified object stream [PDF:1.6:3.4.6].

    @param objectStreamIndirectObject Target object stream.
   */
  public void compress(
    PdfIndirectObject objectStreamIndirectObject
    )
  {
    if(objectStreamIndirectObject == null)
    {uncompress();}
    else
    {
      PdfDataObject objectStreamDataObject = objectStreamIndirectObject.getDataObject();
      if(!(objectStreamDataObject instanceof ObjectStream))
        throw new IllegalArgumentException("'objectStreamIndirectObject' argument MUST contain an ObjectStream instance.");

      // Ensure removal from previous object stream!
      uncompress();

      // Add to the object stream!
      ObjectStream objectStream = (ObjectStream)objectStreamDataObject;
      objectStream.put(xrefEntry.getNumber(),getDataObject());
      // Update its xref entry!
      xrefEntry.setUsage(UsageEnum.InUseCompressed);
      xrefEntry.setStreamNumber(objectStreamIndirectObject.getReference().getObjectNumber());
      xrefEntry.setOffset(-1); // Internal object index unknown (to set on object stream serialization -- see ObjectStream).
    }
  }

  public File getFile(
    )
  {return file;}

  public XRefEntry getXrefEntry(
    )
  {return xrefEntry;}

  @Override
  public int hashCode(
    )
  {
    /*
      NOTE: Uniqueness should be achieved XORring the (local) reference hashcode
      with the (global) file hashcode.
      NOTE: Do NOT directly invoke reference.hashCode() method here as
      it would trigger an infinite loop, as it conversely relies on this method.
    */
    return reference.getId().hashCode() ^ file.hashCode();
  }

  /**
    Gets whether it's compressed within an object stream [PDF:1.6:3.4.6].
  */
  public boolean isCompressed(
    )
  {return xrefEntry.getUsage() == UsageEnum.InUseCompressed;}

  /**
    Gets whether it contains a data object.
  */
  public boolean isInUse(
    )
  {return xrefEntry.getUsage() != UsageEnum.Free;}

  /**
    Gets whether it hasn't been modified.
  */
  public boolean isOriginal(
    )
  {return original;}

  /**
    Removes the {@link #getDataObject() data object} from its object stream [PDF:1.6:3.4.6].
  */
  public void uncompress(
    )
  {
    if(!isCompressed())
      return;

    // Remove from its object stream!
    ObjectStream oldObjectStream = (ObjectStream)file.getIndirectObjects().get(xrefEntry.getStreamNumber()).getDataObject();
    oldObjectStream.remove(xrefEntry.getNumber());
    // Update its xref entry!
    xrefEntry.setUsage(UsageEnum.InUse);
    xrefEntry.setStreamNumber(-1); // No object stream.
    xrefEntry.setOffset(-1); // Offset unknown (to set on file serialization -- see CompressedWriter).
  }

  public void update(
    )
  {
    if(original)
    {
      /*
        NOTE: It's expected that dropOriginal() is invoked by IndirectObjects set() method;
        such an action is delegated because clients may invoke directly set() method, skipping
        this method.
      */
      file.getIndirectObjects().update(this);
    }
  }

  @Override
  public void writeTo(
    IOutputStream stream
    )
  {
    // Header.
    stream.write(reference.getId()); stream.write(BeginIndirectObjectChunk);
    // Body.
    getDataObject().writeTo(stream);
    // Tail.
    stream.write(EndIndirectObjectChunk);
  }

  // <IPdfIndirectObject>
  @Override
  public PdfIndirectObject clone(
    File context
    )
  {return context.getIndirectObjects().addExternal(this);}

  @Override
  public void delete(
    )
  {
    if(file == null)
      return;

    /*
      NOTE: It's expected that dropFile() is invoked by IndirectObjects remove() method;
      such an action is delegated because clients may invoke directly remove() method,
      skipping this method.
    */
    file.getIndirectObjects().remove(xrefEntry.getNumber());
  }

  @Override
  public PdfDataObject getDataObject(
    )
  {
    if(dataObject == null)
    {
      try
      {
        switch (xrefEntry.getUsage())
        {
          // Free entry (no data object at all).
          case Free:
            break;
          // In-use entry (late-bound data object).
          case InUse:
          {
            Parser parser = file.getReader().getParser();
            // Retrieve the associated data object among the original objects!
            parser.seek(xrefEntry.getOffset());
            // Get the indirect data object!
            dataObject = parser.parsePdfObject(4); // NOTE: Skips the indirect-object header.
            break;
          }
          case InUseCompressed:
          {
            // Get the object stream where its data object is stored!
            ObjectStream objectStream = (ObjectStream)file.getIndirectObjects().get(xrefEntry.getStreamNumber()).getDataObject();
            // Get the indirect data object!
            dataObject = objectStream.get(xrefEntry.getNumber());
            break;
          }
        }
      }
      catch(Exception e)
      {throw new RuntimeException("Data object resolution failed.",e);}
    }
    return dataObject;
  }

  @Override
  public PdfIndirectObject getIndirectObject(
    )
  {return this;}

  @Override
  public PdfReference getReference(
    )
  {return reference;}

  @Override
  public void setDataObject(
    PdfDataObject value
    )
  {
    if(xrefEntry.getGeneration() == XRefEntry.GenerationUnreusable)
      throw new RuntimeException("Unreusable entry.");

    dataObject = value;
    xrefEntry.setUsage(UsageEnum.InUse);
    update();
  }
  // </IPdfIndirectObject>
  // </public>

  // <internal>
  /**
    For internal use only.
  */
  public void dropFile(
    )
  {
    uncompress();
    file = null;
  }

  /**
    For internal use only.
  */
  public void dropOriginal(
    )
  {original = false;}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}