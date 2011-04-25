/*
  Copyright 2011 Stefano Chizzolini. http://www.pdfclown.org

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
using System.IO;
using System.Xml;

namespace org.pdfclown.documents.interchange.metadata
{
  /**
    <summary>Metadata stream [PDF:1.6:10.2.2].</summary>
  */
  [PDF(VersionEnum.PDF14)]
  public sealed class Metadata
    : PdfObjectWrapper<PdfStream>
  {
    #region dynamic
    #region constructors
    public Metadata(
      Document context
      ) : base(
        context.File,
        new PdfStream(
          new PdfDictionary(
            new PdfName[]
            {
              PdfName.Type,
              PdfName.Subtype
            },
            new PdfDirectObject[]
            {
              PdfName.Metadata,
              PdfName.XML
            }
            ))
        )
    {}

    public Metadata(
      PdfDirectObject baseObject
      ) : base(baseObject)
    {}
    #endregion

    #region interface
    #region public
    public override object Clone(
      Document context
      )
    {throw new NotImplementedException();}

    /**
      <summary>Gets/Sets the metadata contents.</summary>
    */
    public XmlDocument Content
    {
      get
      {
        // 1. Get the document contents!
        MemoryStream contentStream = new MemoryStream(BaseDataObject.Body.ToByteArray());
        // 2. Parse the document contents!
        XmlDocument content = new XmlDocument();
        try
        {content.Load(contentStream);}
        finally
        {contentStream.Close();}
        return content;
      }
      set
      {
        // 1. Get the document contents!
        MemoryStream contentStream = new MemoryStream();
        value.Save(contentStream);

        // 2. Store the document contents into the stream body!
        IBuffer body = BaseDataObject.Body;
        body.SetLength(0);
        body.Write(contentStream.ToArray());
        contentStream.Close();
      }
    }
    #endregion
    #endregion
    #endregion
  }
}