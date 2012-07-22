package org.pdfclown.samples.cli;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.files.EmbeddedFile;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.actions.GoToEmbedded;
import org.pdfclown.documents.interaction.actions.GoToURI;
import org.pdfclown.documents.interaction.annotations.Border;
import org.pdfclown.documents.interaction.annotations.FileAttachment;
import org.pdfclown.documents.interaction.annotations.Link;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.RemoteDestination;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to apply links</b> to a PDF document.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 01/29/12
*/
public class LinkCreationSample
  extends Sample
{
  @Override
  public boolean run(
    )
  {
    // 1. Creating the document...
    File file = new File();
    Document document = file.getDocument();

    // 2. Applying links...
    buildLinks(document);

    // 3. Serialize the PDF file!
    serialize(file, "Link annotations", "applying link annotations");

    return true;
  }

  private void buildLinks(
    Document document
    )
  {
    Pages pages = document.getPages();
    Page page = new Page(document);
    pages.add(page);

    StandardType1Font font = null;
    try
    {
      font = new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Courier,
        true,
        false
        );
    }
    catch(Exception e)
    {}

    PrimitiveComposer composer = new PrimitiveComposer(page);
    BlockComposer blockComposer = new BlockComposer(composer);

    /*
      2.1. Goto-URI link.
    */
    {
      blockComposer.begin(new Rectangle2D.Double(30,100,200,50),XAlignmentEnum.Left,YAlignmentEnum.Middle);
      composer.setFont(font,12);
      blockComposer.showText("Go-to-URI link");
      composer.setFont(font,8);
      blockComposer.showText("\nIt allows you to navigate to a network resource.");
      composer.setFont(font,5);
      blockComposer.showText("\n\nClick on the box to go to the project's SourceForge.net repository.");
      blockComposer.end();

      try
      {
        /*
          NOTE: This statement instructs the PDF viewer to navigate to the given URI when the link is clicked.
        */
        Link link = new Link(
          page,
          new Rectangle(240,100,100,50),
          new GoToURI(
            document,
            new URI("http://www.sourceforge.net/projects/clown")
            )
          );
        link.setBorder(
          new Border(
            document,
            3,
            Border.StyleEnum.Beveled,
            null
            )
          );
      }
      catch(Exception exception)
      {throw new RuntimeException(exception);}
    }

    /*
      2.2. Embedded-goto link.
    */
    {
      // Get the path of the PDF file to attach!
      String filePath = promptFileChoice("Please select a PDF file to attach");

      /*
        NOTE: These statements instruct PDF Clown to attach a PDF file to the current document.
        This is necessary in order to test the embedded-goto functionality,
        as you can see in the following link creation (see below).
      */
      int fileAttachmentPageIndex = page.getIndex();
      String fileAttachmentName = "attachedSamplePDF";
      String fileName = new java.io.File(filePath).getName();
      FileAttachment attachment = new FileAttachment(
        page,
        new Rectangle(0, -20, 10, 10),
        FileSpecification.get(
          EmbeddedFile.get(
            document,
            filePath
            ),
          fileName
          )
        );
      attachment.setName(fileAttachmentName);
      attachment.setText("File attachment annotation");
      attachment.setIconType(FileAttachment.IconTypeEnum.PaperClip);

      blockComposer.begin(new Rectangle2D.Double(30,170,200,50),XAlignmentEnum.Left,YAlignmentEnum.Middle);
      composer.setFont(font,12);
      blockComposer.showText("Go-to-embedded link");
      composer.setFont(font,8);
      blockComposer.showText("\nIt allows you to navigate to a destination within an embedded PDF file.");
      composer.setFont(font,5);
      blockComposer.showText("\n\nClick on the button to go to the 2nd page of the attached PDF file (" + fileName + ").");
      blockComposer.end();

      /*
        NOTE: This statement instructs the PDF viewer to navigate to the page 2 of a PDF file
        attached inside the current document as described by the FileAttachment annotation on page 1 of the current document.
      */
      Link link = new Link(
        page,
        new Rectangle(240,170,100,50),
        new GoToEmbedded(
          document,
          new GoToEmbedded.PathElement(
            document,
            fileAttachmentPageIndex, // Page of the current document containing the file attachment annotation of the target document.
            fileAttachmentName, // Name of the file attachment annotation corresponding to the target document.
            null // No sub-target.
            ), // Target represents the document to go to.
          new RemoteDestination(
            document,
            1, // Show the page 2 of the target document.
            Destination.ModeEnum.Fit, // Show the target document page entirely on the screen.
            null,
            null
            ) // The destination must be within the target document.
          )
        );
      link.setBorder(
        new Border(
          document,
          1,
          Border.StyleEnum.Dashed,
          new LineDash(new double[]{8,5,2,5})
          )
        );
    }

    /*
      2.3. Textual link.
    */
    {
      blockComposer.begin(new Rectangle2D.Double(30,240,200,50),XAlignmentEnum.Left,YAlignmentEnum.Middle);
      composer.setFont(font,12);
      blockComposer.showText("Textual link");
      composer.setFont(font,8);
      blockComposer.showText("\nIt allows you to expose any kind of link (including the above-mentioned types) as text.");
      composer.setFont(font,5);
      blockComposer.showText("\n\nClick on the text links to go either to the project's SourceForge.net repository or to the project's home page.");
      blockComposer.end();

      try
      {
        composer.beginLocalState();
        composer.setFont(font,10);
        composer.setFillColor(DeviceRGBColor.get(Color.BLUE));
        composer.showText(
          "PDF Clown Project's repository at SourceForge.net",
          new Point2D.Double(240,265),
          XAlignmentEnum.Left,
          YAlignmentEnum.Middle,
          0,
          new GoToURI(
            document,
            new URI("http://www.sourceforge.net/projects/clown")
            )
          );
        composer.showText(
          "PDF Clown Project's home page",
          new Point2D.Double(240,285),
          XAlignmentEnum.Left,
          YAlignmentEnum.Bottom,
          -90,
          new GoToURI(
            document,
            new URI("http://www.pdfclown.org")
            )
          );
        composer.end();
      }
      catch(Exception e)
      {}
    }

    composer.flush();
  }
}