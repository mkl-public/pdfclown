package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.objects.CompositeObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.Operation;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.tokens.FileFormatException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  This sample demonstrates <b>how to inspect the structure of a PDF document</b>.
  <h3>Remarks</h3>
  <p>This sample is just a limited exercise: see the API documentation
  to exploit all the available access functionalities.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public class ParsingSample
  extends Sample
{
  @Override
  public boolean run(
    )
  {
    String filePath = promptPdfFileChoice("Please select a PDF file");

    // 1. Open the PDF file!
    File file;
    try
    {file = new File(filePath);}
    catch(FileFormatException e)
    {throw new RuntimeException(filePath + " file has a bad file format.",e);}
    catch(Exception e)
    {throw new RuntimeException(filePath + " file access error.",e);}

    Document document = file.getDocument();

    // 2. Document parsing.
    // 2.1. Showing basic metadata...
    System.out.println("\nDocument information:");
    Information info = document.getInformation();
    if(info == null)
    {System.out.println("No information available (Info dictionary doesn't exist).");}
    else
    {
      System.out.println("Author: " + info.getAuthor());
      System.out.println("Title: " + info.getTitle());
      System.out.println("Subject: " + info.getSubject());
      System.out.println("CreationDate: " + info.getCreationDate());
    }

    System.out.println("\nIterating through the indirect-object collection (please wait)...");

    // 2.2. Counting the indirect objects, grouping them by type...
    HashMap<String,Integer> objCounters = new HashMap<String,Integer>();
    objCounters.put("xref free entry",0);
    for(PdfIndirectObject object : file.getIndirectObjects())
    {
      if(object.isInUse()) // In-use entry.
      {
        String typeName = object.getDataObject().getClass().getSimpleName();
        if(objCounters.containsKey(typeName))
        {objCounters.put(typeName, objCounters.get(typeName) + 1);}
        else
        {objCounters.put(typeName, 1);}
      }
      else // Free entry.
      {objCounters.put("xref free entry", objCounters.get("xref free entry") + 1);}
    }
    System.out.println("\nIndirect objects partial counts (grouped by PDF object type):");
    for(Map.Entry<String,Integer> entry : objCounters.entrySet())
    {System.out.println(" " + entry.getKey() + ": " + entry.getValue());}
    System.out.println("Indirect objects total count: " + file.getIndirectObjects().size());

    // 2.3. Showing some page information...
    Pages pages = document.getPages();
    int pageCount = pages.size();
    System.out.println("\nPage count: " + pageCount);

    int pageIndex = (int)Math.floor((float)pageCount / 2);
    System.out.println("Mid page:");
    printPageInfo(pages.get(pageIndex),pageIndex);

    pageIndex++;
    if(pageIndex < pageCount)
    {
      System.out.println("Next page:");
      printPageInfo(pages.get(pageIndex),pageIndex);
    }
    
    return true;
  }

  private void printPageInfo(
    Page page,
    int index
    )
  {
    // 1. Showing basic page information...
    System.out.println(" Index (calculated): " + page.getIndex() + " (should be " + index + ")");
    System.out.println(" ID: " + ((PdfReference)page.getBaseObject()).getId());
    PdfDictionary pageDictionary = page.getBaseDataObject();
    System.out.println(" Dictionary entries:");
    for(PdfName key : pageDictionary.keySet())
    {System.out.println("  " + key.getValue());}

    // 2. Showing page contents information...
    Contents contents = page.getContents();
    System.out.println(" Content objects count: " + contents.size());
    System.out.println(" Content head:");
    printContentObjects(contents,0,0);

    // 3. Showing page resources information...
    {
      Resources resources = page.getResources();
      System.out.println(" Resources:");
      Map<PdfName, ? extends PdfObjectWrapper<?>> subResources;
      
      subResources = resources.getFonts();
      if(subResources != null)
      {System.out.println("  Font count: " + subResources.size());}

      subResources = resources.getXObjects();
      if(subResources != null)
      {System.out.println("  XObjects count: " + subResources.size());}

      subResources = resources.getColorSpaces();
      if(subResources != null)
      {System.out.println("  ColorSpaces count: " + subResources.size());}
    }
  }

  private int printContentObjects(
    List<ContentObject> objects,
    int index,
    int level
    )
  {
    String indentation;
    {
      StringBuffer buffer = new StringBuffer();
      for(int i = 0; i < level; i++)
      {buffer.append(' ');}
      indentation = buffer.toString();
    }

    for(ContentObject object : objects)
    {
      /*
        NOTE: Contents are expressed through both simple operations and composite objects.
      */
      if(object instanceof Operation)
      {System.out.println("   " + indentation + (++index) + ": " + object);}
      else if(object instanceof CompositeObject)
      {
        System.out.println(
          "   " + indentation + object.getClass().getSimpleName()
            + "\n   " + indentation + "{"
          );
        index = printContentObjects(((CompositeObject)object).getObjects(),index,level+1);
        System.out.println("   " + indentation + "}");
      }
      if(index > 9)
        break;
    }
    return index;
  }
}