package org.pdfclown.samples.cli;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.actions.GoToEmbedded;
import org.pdfclown.documents.interaction.actions.GoToEmbedded.PathElement;
import org.pdfclown.documents.interaction.actions.GoToNonLocal;
import org.pdfclown.documents.interaction.actions.GoToURI;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  This sample demonstrates <b>how to inspect the bookmarks</b> of a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 01/29/12
*/
public class BookmarksParsingSample
  extends Sample
{
  @Override
  public boolean run(
    )
  {
    // 1. Opening the PDF file...
    File file;
    {
      String filePath = promptPdfFileChoice("Please select a PDF file");
      try
      {file = new File(filePath);}
      catch(Exception e)
      {throw new RuntimeException(filePath + " file access error.",e);}
    }
    Document document = file.getDocument();

    // 2. Get the bookmarks collection!
    Bookmarks bookmarks = document.getBookmarks();
    if(bookmarks == null)
    {System.out.println("\nNo bookmark available (Outline dictionary not found).");}
    else
    {
      System.out.println("\nIterating through the bookmarks collection (please wait)...\n");
      // 3. Show the bookmarks!
      printBookmarks(bookmarks);
    }

    return true;
  }

  private void printBookmarks(
    Bookmarks bookmarks
    )
  {
    if(bookmarks == null)
      return;

    for(Bookmark bookmark : bookmarks)
    {
      // Show current bookmark!
      System.out.println("Bookmark '" + bookmark.getTitle() + "'");
      System.out.print("    Target: ");
      PdfObjectWrapper<?> target = bookmark.getTarget();
      if(target instanceof Destination)
      {printDestination((Destination)target);}
      else if(target instanceof Action)
      {printAction((Action)target);}
      else if(target == null)
      {System.out.println("[not available]");}
      else
      {System.out.println("[unknown type: " + target.getClass().getSimpleName() + "]");}

      // Show child bookmarks!
      printBookmarks(bookmark.getBookmarks());
    }
  }

  private void printAction(
    Action action
    )
  {
    System.out.println("Action [" + action.getClass().getSimpleName() + "] " + action.getBaseObject());
    if(action instanceof GoToDestination<?>)
    {
      if(action instanceof GoToNonLocal<?>)
      {
        FileSpecification<?> destinationFile = ((GoToNonLocal<?>)action).getDestinationFile();
        if(destinationFile != null)
        {System.out.println("    Filename: " + destinationFile.getPath());}

        if(action instanceof GoToEmbedded)
        {
          PathElement target = ((GoToEmbedded)action).getDestinationPath();
          System.out.println("    EmbeddedFilename: " + target.getEmbeddedFileName() + " Relation: " + target.getRelation());
        }
      }
      System.out.print("    ");
      printDestination(((GoToDestination<?>)action).getDestination());
    }
    else if(action instanceof GoToURI)
    {System.out.println("    URI: " + ((GoToURI)action).getURI());}
  }

  private void printDestination(
    Destination destination
    )
  {
    System.out.println(destination.getClass().getSimpleName() + " " + destination.getBaseObject());
    System.out.print("    Page ");
    Object pageRef = destination.getPage();
    if(pageRef instanceof Page)
    {
      Page page = (Page)pageRef;
      System.out.println((page.getIndex()+1) + " [ID: " + page.getBaseObject() + "]");
    }
    else
    {System.out.println(((Integer)pageRef+1));}
  }
}