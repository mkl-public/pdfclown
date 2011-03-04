package org.pdfclown.samples.cli;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ITextString;
import org.pdfclown.files.File;
import org.pdfclown.tools.TextExtractor;

/**
  This sample demonstrates how to <b>retrieve text content along with its graphic attributes</b>
  (font, font size, text color, text rendering mode, text bounding box, and so on) from a PDF document;
  text is <i>automatically sorted and aggregated</i>.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
public class AdvancedTextExtractionSample
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
    catch(Exception e)
    {throw new RuntimeException(filePath + " file access error.",e);}

    Document document = file.getDocument();

    // 2. Text extraction from the document pages.
    TextExtractor extractor = new TextExtractor();
    for(Page page : document.getPages())
    {
      if(!promptNextPage(page, false))
        return false;

      List<ITextString> textStrings = extractor.extract(page).get(null);
      for(ITextString textString : textStrings)
      {
        Rectangle2D textStringBox = textString.getBox();
        System.out.println(
          "Text ["
            + "x:" + Math.round(textStringBox.getX()) + ","
            + "y:" + Math.round(textStringBox.getY()) + ","
            + "w:" + Math.round(textStringBox.getWidth()) + ","
            + "h:" + Math.round(textStringBox.getHeight())
            + "]: " + textString.getText()
            );
      }
    }

    return true;
  }
}