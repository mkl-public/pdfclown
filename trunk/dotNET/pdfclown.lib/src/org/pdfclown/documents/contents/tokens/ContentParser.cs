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

using bytes = org.pdfclown.bytes;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.objects;
using org.pdfclown.tokens;
using org.pdfclown.util.io;

using System;
using System.Collections.Generic;
using System.Globalization;
using io = System.IO;
using System.Text;

namespace org.pdfclown.documents.contents.tokens
{
  /**
    <summary>Content stream parser [PDF:1.6:3.7.1].</summary>
  */
  public sealed class ContentParser
    : BaseParser
  {
    #region types
    public class ContentStream
      : bytes::IInputStream
    {
      private readonly PdfDataObject baseDataObject;

      private long basePosition;
      private bytes::IInputStream stream;
      private int streamIndex = -1;

      public ContentStream(
        PdfDataObject baseDataObject
        )
      {
        this.baseDataObject = baseDataObject;
        MoveNextStream();
      }

      public PdfDataObject BaseDataObject
      {
        get
        {return baseDataObject;}
      }

      public ByteOrderEnum ByteOrder
      {
        get
        {return stream.ByteOrder;}
        set
        {throw new NotSupportedException();}
      }

      public void Dispose(
        )
      {/* NOOP */}

      public long Length
      {
        get
        {
          if(baseDataObject is PdfStream) // Single stream.
            return ((PdfStream)baseDataObject).Body.Length;
          else // Array of streams.
          {
            long length = 0;
            foreach(PdfDirectObject stream in (PdfArray)baseDataObject)
            {length += ((PdfStream)((PdfReference)stream).DataObject).Body.Length;}
            return length;
          }
        }
      }

      public long Position
      {
        get
        {return basePosition + stream.Position;}
        set
        {Seek(value);}
      }

      public void Read(
        byte[] data
        )
      {throw new NotImplementedException();}

      public void Read(
        byte[] data,
        int offset,
        int length
        )
      {throw new NotImplementedException();}

      public int ReadByte(
        )
      {
        while(true)
        {
          int c = (stream != null ? stream.ReadByte() : -1);
          if(c != -1
            || !MoveNextStream())
            return c;
        }
      }

      public int ReadInt(
        )
      {throw new NotImplementedException();}

      public int ReadInt(
        int length
        )
      {throw new NotImplementedException();}

      public string ReadLine(
        )
      {throw new NotImplementedException();}

      public short ReadShort(
        )
      {throw new NotImplementedException();}

      public sbyte ReadSignedByte(
        )
      {throw new NotImplementedException();}

      public string ReadString(
        int length
        )
      {throw new NotImplementedException();}

      public ushort ReadUnsignedShort(
        )
      {throw new NotImplementedException();}

      public void Seek(
        long position
        )
      {
        while(true)
        {
          if(position < basePosition) //Before current stream.
          {
            if(!MovePreviousStream())
              throw new ArgumentException("Lower than acceptable.","position");
          }
          else if(position > basePosition + stream.Length) // After current stream.
          {
            if(!MoveNextStream())
              throw new ArgumentException("Higher than acceptable.","position");
          }
          else // At current stream.
          {
            stream.Seek(position - basePosition);
            break;
          }
        }
      }

      public void Skip(
        long offset
        )
      {
        while(true)
        {
          long position = stream.Position + offset;
          if(position < 0) //Before current stream.
          {
            offset += stream.Position;
            if(!MovePreviousStream())
              throw new ArgumentException("Lower than acceptable.","offset");
  
            stream.Position = stream.Length;
          }
          else if(position > stream.Length) // After current stream.
          {
            offset -= (stream.Length - stream.Position);
            if(!MoveNextStream())
              throw new ArgumentException("Higher than acceptable.","offset");
          }
          else // At current stream.
          {
            stream.Seek(position);
            break;
          }
        }
      }

      public byte[] ToByteArray(
        )
      {throw new NotImplementedException();}

      private bool MoveNextStream(
        )
      {
        // Is the content stream just a single stream?
        /*
          NOTE: A content stream may be made up of multiple streams [PDF:1.6:3.6.2].
        */
        if(baseDataObject is PdfStream) // Single stream.
        {
          if(streamIndex < 1)
          {
            streamIndex++;
  
            basePosition = (streamIndex == 0
              ? 0
              : basePosition + stream.Length);
  
            stream = (streamIndex < 1
              ? ((PdfStream)baseDataObject).Body
              : null);
          }
        }
        else // Multiple streams.
        {
          PdfArray streams = (PdfArray)baseDataObject;
          if(streamIndex < streams.Count)
          {
            streamIndex++;
  
            basePosition = (streamIndex == 0
              ? 0
              : basePosition + stream.Length);
  
            stream = (streamIndex < streams.Count
              ? ((PdfStream)streams.Resolve(streamIndex)).Body
              : null);
          }
        }
        if(stream == null)
          return false;
  
        stream.Position = 0;
        return true;
      }
  
      private bool MovePreviousStream(
        )
      {
        if(streamIndex == 0)
        {
          streamIndex--;
          stream = null;
        }
        if(streamIndex == -1)
          return false;
  
        streamIndex--;
        /* NOTE: A content stream may be made up of multiple streams [PDF:1.6:3.6.2]. */
        // Is the content stream just a single stream?
        if(baseDataObject is PdfStream) // Single stream.
        {
          stream = ((PdfStream)baseDataObject).Body;
          basePosition = 0;
        }
        else // Array of streams.
        {
          PdfArray streams = (PdfArray)baseDataObject;
  
          stream = ((PdfStream)((PdfReference)streams[streamIndex]).DataObject).Body;
          basePosition -= stream.Length;
        }
  
        return true;
      }
    }
    #endregion

    #region dynamic
    #region constructors
    internal ContentParser(
      PdfDataObject contentStreamObject
      ) : base(new ContentStream(contentStreamObject))
    {}
    #endregion

    #region interface
    #region public
    /**
      <summary>Parses the next content object [PDF:1.6:4.1].</summary>
    */
    public ContentObject ParseContentObject(
      )
    {
      Operation operation = ParseOperation();
      if(operation is PaintXObject) // External object.
        return new XObject((PaintXObject)operation);
      else if(operation is PaintShading) // Shading.
        return new Shading((PaintShading)operation);
      else if(operation is BeginSubpath
        || operation is DrawRectangle) // Path.
        return ParsePath(operation);
      else if(operation is BeginText) // Text.
        return new Text(
          ParseContentObjects()
          );
      else if(operation is SaveGraphicsState) // Local graphics state.
        return new LocalGraphicsState(
          ParseContentObjects()
          );
      else if(operation is BeginMarkedContent) // Marked-content sequence.
        return new MarkedContent(
          (BeginMarkedContent)operation,
          ParseContentObjects()
          );
      else if(operation is BeginInlineImage) // Inline image.
        return ParseInlineImage();
      else // Single operation.
        return operation;
    }

    /**
      <summary>Parses the next content objects.</summary>
    */
    public IList<ContentObject> ParseContentObjects(
      )
    {
      List<ContentObject> contentObjects = new List<ContentObject>();
      while(MoveNext())
      {
        ContentObject contentObject = ParseContentObject();
        // Multiple-operation graphics object end?
        if(contentObject is EndText // Text.
          || contentObject is RestoreGraphicsState // Local graphics state.
          || contentObject is EndMarkedContent // End marked-content sequence.
          || contentObject is EndInlineImage) // Inline image.
          return contentObjects;

        contentObjects.Add(contentObject);
      }
      return contentObjects;
    }

    /**
      <summary>Parses the next operation.</summary>
    */
    public Operation ParseOperation(
      )
    {
      string operator_ = null;
      List<PdfDirectObject> operands = new List<PdfDirectObject>();
      // Parsing the operation parts...
      while(true)
      {
        // Did we reach the operator keyword?
        if(TokenType == TokenTypeEnum.Keyword)
        {
          operator_ = (string)Token;
          break;
        }

        operands.Add((PdfDirectObject)ParsePdfObject()); MoveNext();
      }
      return Operation.Get(operator_,operands);
    }

    public override PdfDataObject ParsePdfObject(
      )
    {
      switch(TokenType)
      {
        case TokenTypeEnum.Literal:
          if(Token is string)
            return new PdfString(
              org.pdfclown.tokens.Encoding.Encode((string)Token),
              PdfString.SerializationModeEnum.Literal
              );
          break;
        case TokenTypeEnum.Hex:
          return new PdfString(
            (string)Token,
            PdfString.SerializationModeEnum.Hex
            );
      }
      return base.ParsePdfObject();
    }
    #endregion

    #region private
    private InlineImage ParseInlineImage(
      )
    {
      /*
        NOTE: Inline images use a peculiar syntax that's an exception to the usual rule
        that the data in a content stream is interpreted according to the standard PDF syntax
        for objects.
      */
      InlineImageHeader header;
      {
        List<PdfDirectObject> operands = new List<PdfDirectObject>();
        // Parsing the image entries...
        while(MoveNext()
          && TokenType != TokenTypeEnum.Keyword) // Not keyword (i.e. end at image data beginning (ID operator)).
        {operands.Add((PdfDirectObject)ParsePdfObject());}
        header = new InlineImageHeader(operands);
      }

      InlineImageBody body;
      {
        bytes::IInputStream stream = Stream;
        MoveNext();
        bytes::Buffer data = new bytes::Buffer();
        while(true)
        {
          byte c1 = (byte)stream.ReadByte();
          byte c2 = (byte)stream.ReadByte();
          if(c1 == 'E' && c2 == 'I')
            break;

          data.Append(c1);
          data.Append(c2);
        }
        body = new InlineImageBody(data);
      }

      return new InlineImage(
        header,
        body
        );
    }

    private Path ParsePath(
      Operation beginOperation
      )
    {
      /*
        NOTE: Paths do not have an explicit end operation, so we must infer it
        looking for the first non-painting operation.
      */
      IList<ContentObject> operations = new List<ContentObject>();
      {
        operations.Add(beginOperation);
        long position = Position;
        bool closeable = false;
        while(MoveNext())
        {
          Operation operation = ParseOperation();
          // Multiple-operation graphics object closeable?
          if(operation is PaintPath) // Painting operation.
          {closeable = true;}
          else if(closeable) // Past end (first non-painting operation).
          {
            Seek(position); // Rolls back to the last path-related operation.

            break;
          }

          operations.Add(operation);
          position = Position;
        }
      }
      return new Path(operations);
    }
    #endregion
    #endregion
    #endregion
  }
}