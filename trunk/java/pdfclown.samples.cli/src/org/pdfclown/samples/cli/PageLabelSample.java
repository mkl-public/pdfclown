package org.pdfclown.samples.cli;

import java.io.IOException;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.PageLabels;
import org.pdfclown.documents.interaction.navigation.page.PageLabel;
import org.pdfclown.documents.interaction.navigation.page.PageLabel.NumberStyleEnum;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfInteger;

/**
  This sample demonstrates <b>how to define, read and modify page labels</b>.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 09/24/12
*/
public class PageLabelSample
  extends Sample
{
  @Override
  public void run(
    )
  {
    String outputFilePath;
    {
      File file = null;
      try
      {
        // 1. Opening the PDF file...
        {
          String filePath = promptFileChoice("Please select a PDF file");
          try
          {file = new File(filePath);}
          catch(Exception e)
          {throw new RuntimeException(filePath + " file access error.",e);}
        }
        Document document = file.getDocument();

        // 2. Defining the page labels...
        PageLabels pageLabels = document.getPageLabels();
        if(pageLabels != null)
        {pageLabels.clear();}
        else
        {document.setPageLabels(pageLabels = new PageLabels(document));}
        /*
          NOTE: This sample applies labels to arbitrary page ranges: no sensible connection with their
          actual content has therefore to be expected.
        */
        int pageCount = document.getPages().size();
        pageLabels.put(new PdfInteger(0), new PageLabel(document, "Introduction ", NumberStyleEnum.UCaseRomanNumber, 5));
        if(pageCount > 3)
        {pageLabels.put(new PdfInteger(3), new PageLabel(document, NumberStyleEnum.UCaseLetter));}
        if(pageCount > 6)
        {pageLabels.put(new PdfInteger(6), new PageLabel(document, "Contents ", NumberStyleEnum.ArabicNumber, 0));}

        // 3. Serialize the PDF file!
        outputFilePath = serialize(file, "Page labelling", "labelling a document's pages");
      }
      finally
      {
        // 4. Closing the PDF file...
        if(file != null)
        {
          try
          {file.close();}
          catch(IOException e)
          {/* NOOP */}
        }
      }
    }

    {
      File file = null;
      try
      {
        // 1. Opening the PDF file...
        try
        {file = new File(outputFilePath);}
        catch(Exception e)
        {throw new RuntimeException(outputFilePath + " file access error.",e);}

        // 2. Reading the page labels...
        for(Map.Entry<PdfInteger,PageLabel> entry : file.getDocument().getPageLabels().entrySet())
        {
          System.out.println("Page label " + entry.getValue().getBaseObject());
          System.out.println("    Initial page: " + (entry.getKey().getValue() + 1));
          System.out.println("    Prefix: " + (entry.getValue().getPrefix()));
          System.out.println("    Number style: " + (entry.getValue().getNumberStyle()));
          System.out.println("    Number base: " + (entry.getValue().getNumberBase()));
        }
      }
      finally
      {
        // 3. Closing the PDF file...
        if(file != null)
        {
          try
          {file.close();}
          catch(IOException e)
          {/* NOOP */}
        }
      }
    }
  }
}