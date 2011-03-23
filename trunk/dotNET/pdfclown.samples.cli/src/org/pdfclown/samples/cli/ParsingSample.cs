using org.pdfclown.documents;
using org.pdfclown.documents.contents;
using org.pdfclown.documents.contents.objects;
using org.pdfclown.documents.interchange.metadata;
using org.pdfclown.files;
using org.pdfclown.objects;

using System;
using System.Collections.Generic;

namespace org.pdfclown.samples.cli
{
  /**
    <summary>This sample demonstrates how to inspect the structure of a PDF document.</summary>
    <remarks>This sample is just a limited exercise: see the API documentation
    to exploit all the available access functionalities.</remarks>
  */
  public class ParsingSample
    : Sample
  {
    public override bool Run(
      )
    {
      string filePath = PromptPdfFileChoice("Please select a PDF file");

      // 1. Open the PDF file!
      File file = new File(filePath);
      Document document = file.Document;

      // 2. Document parsing.
      // 2.1. Showing basic metadata...
      Console.WriteLine("\nDocument information:");
      Information info = document.Information;
      if(info == null)
      {Console.WriteLine("No information available (Info dictionary doesn't exist).");}
      else
      {
        Console.WriteLine("Author: " + info.Author);
        Console.WriteLine("Title: " + info.Title);
        Console.WriteLine("Subject: " + info.Subject);
        Console.WriteLine("CreationDate: " + info.CreationDate);
      }

      Console.WriteLine("\nIterating through the indirect-object collection (please wait)...");

      // 2.2. Counting the indirect objects, grouping them by type...
      Dictionary<string,int> objCounters = new Dictionary<string,int>();
      objCounters["xref free entry"] = 0;
      foreach(PdfIndirectObject obj in file.IndirectObjects)
      {
        if(obj.IsInUse()) // In-use entry.
        {
          string typeName = obj.DataObject.GetType().Name;
          if(objCounters.ContainsKey(typeName))
          {objCounters[typeName]++;}
          else
          {objCounters[typeName] = 1;}
        }
        else // Free entry.
        {objCounters["xref free entry"]++;}
      }
      Console.WriteLine("\nIndirect objects partial counts (grouped by PDF object type):");
      foreach(KeyValuePair<string,int> keyValuePair in objCounters)
      {Console.WriteLine(" " + keyValuePair.Key + ": " + keyValuePair.Value);}
      Console.WriteLine("Indirect objects total count: " + file.IndirectObjects.Count);

      // 2.3. Showing some page information...
      Pages pages = document.Pages;
      int pageCount = pages.Count;
      Console.WriteLine("\nPage count: " + pageCount);
      int pageIndex = (int)Math.Round((float)pageCount / 2);
      Console.WriteLine("Mid page:");
      PrintPageInfo(pages[pageIndex],pageIndex);

      pageIndex++;
      if(pageIndex < pageCount)
      {
        Console.WriteLine("Next page:");
        PrintPageInfo(pages[pageIndex],pageIndex);
      }

      file.Dispose();

      return true;
    }

    private void PrintPageInfo(
      Page page,
      int index
      )
    {
      // 1. Showing basic page information...
      Console.WriteLine(" Index (calculated): " + page.Index + " (should be " + index + ")");
      Console.WriteLine(" ID: " + ((PdfReference)page.BaseObject).Id);
      PdfDictionary pageDictionary = page.BaseDataObject;
      Console.WriteLine(" Dictionary entries:");
      foreach(PdfName key in pageDictionary.Keys)
      {Console.WriteLine("  " + key.Value);}

      // 2. Showing page contents information...
      Contents contents = page.Contents;
      Console.WriteLine(" Content objects count: " + contents.Count);
      Console.WriteLine(" Content head:");
      PrintContentObjects(contents,0,0);

      // 3. Showing page resources information...
      {
        Resources resources = page.Resources;
        Console.WriteLine(" Resources:");
        try{Console.WriteLine("  Font count: " + resources.Fonts.Count);}catch{}
        try{Console.WriteLine("  XObjects count: " + resources.XObjects.Count);}catch{}
        try{Console.WriteLine("  ColorSpaces count: " + resources.ColorSpaces.Count);}catch{}
      }
    }

    private int PrintContentObjects(
      IList<ContentObject> objects,
      int index,
      int level
      )
    {
      string indentation = new String(' ',level);

      foreach(ContentObject obj in objects)
      {
        /*
          NOTE: Contents are expressed through both simple operations and composite objects.
        */
        if(obj is Operation)
        {Console.WriteLine("   " + indentation + (++index) + ": " + obj);}
        else if(obj is CompositeObject)
        {
          Console.WriteLine(
            "   " + indentation + obj.GetType().Name
              + "\n   " + indentation + "{"
            );
          index = PrintContentObjects(((CompositeObject)obj).Objects,index,level+1);
          Console.WriteLine("   " + indentation + "}");
        }
        if(index > 9)
          break;
      }
      return index;
    }
  }
}