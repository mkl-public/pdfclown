package org.pdfclown.samples.cli;

import java.awt.geom.Point2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.NamedDestinations;
import org.pdfclown.documents.Names;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to manipulate the named destinations</b> within a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 01/29/12
*/
public class NamedDestinationSample
  extends Sample
{
  @Override
  public boolean run(
    )
  {
    // 1. Opening the PDF file...
    File file;
    {
      String filePath = promptFileChoice("Please select a PDF file");
      try
      {file = new File(filePath);}
      catch(Exception e)
      {throw new RuntimeException(filePath + " file access error.",e);}
    }
    Document document = file.getDocument();
    Pages pages = document.getPages();

    // 2. Inserting page destinations...
    Names names = document.getNames();
    if(names == null)
    {document.setNames(names = new Names(document));}

    NamedDestinations destinations = names.getDestinations();
    if(destinations == null)
    {names.setDestinations(destinations = new NamedDestinations(document));}

    destinations.put("d31e1142", new LocalDestination(pages.get(0)));
    if(pages.size() > 1)
    {
      destinations.put("N84afaba6", new LocalDestination(pages.get(1), Destination.ModeEnum.FitHorizontal, 0, null));
      destinations.put("d38e1142", new LocalDestination(pages.get(1)));
      destinations.put("M38e1142", new LocalDestination(pages.get(1)));
      destinations.put("d3A8e1142", new LocalDestination(pages.get(1)));
      destinations.put("z38e1142", new LocalDestination(pages.get(1)));
      destinations.put("f38e1142", new LocalDestination(pages.get(1)));
      destinations.put("e38e1142", new LocalDestination(pages.get(1)));
      destinations.put("B84afaba6", new LocalDestination(pages.get(1)));
      destinations.put("Z38e1142", new LocalDestination(pages.get(1)));

      if(pages.size() > 2)
      {destinations.put("1845505298", new LocalDestination(pages.get(2), Destination.ModeEnum.XYZ, new Point2D.Double(50, Double.NaN), null));}
    }

    // 3. Serialize the PDF file!
    serialize(file, "Named destinations", "manipulating named destinations");

    return true;
  }
}