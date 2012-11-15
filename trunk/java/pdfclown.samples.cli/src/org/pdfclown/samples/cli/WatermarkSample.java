package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.files.File;
import org.pdfclown.tools.PageStamper;

/**
  This sample demonstrates <b>how to insert watermark text</b> into an existing PDF document.
  <p>This implementation uses a Form XObject [PDF:1.6:4.9] to conveniently achieve a consistent page
  background. Form XObjects provide context independence encapsulating their contents (and resources)
  in a single stream: such an approach allows content reuse.</p>
  <p>The watermark is seamlessly inserted under each page content using the PageStamper class.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 09/24/12
*/
public class WatermarkSample
  extends Sample
{
  @Override
  public void run(
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

    // 2. Create a watermark!
    FormXObject watermark = createWatermark(document);

    // 3. Apply the watermark to the pages of the document!
    applyWatermark(watermark);

    // 4. Serialize the PDF file!
    serialize(file, "Watermark", "how to place some content behind existing pages");
  }

  private void applyWatermark(
    FormXObject watermark
    )
  {
    // 1. Instantiate the stamper!
    /* NOTE: The PageStamper is optimized for dealing with pages. */
    PageStamper stamper = new PageStamper();

    // 2. Inserting the watermark into each page of the document...
    for(Page page : watermark.getDocument().getPages())
    {
      // 2.1. Associate the page to the stamper!
      stamper.setPage(page);

      // 2.2. Stamping the watermark on the background...
      // Get the background 'layer' of the page!
      PrimitiveComposer background = stamper.getBackground();
      // Show the watermark into the page background!
      background.showXObject(watermark);

      // 2.3. End the stamping!
      stamper.flush();
    }
  }

  private FormXObject createWatermark(
    Document document
    )
  {
    Dimension2D size = document.getSize();

    // 1. Create a new external form object to represent the watermark!
    FormXObject watermark = new FormXObject(document, size);

    // 2. Inserting the contents of the watermark...
    // 2.1. Create a content composer for the watermark!
    PrimitiveComposer composer = new PrimitiveComposer(watermark);
    // 2.2. Inserting the contents...
    // Set the font to use!
    composer.setFont(
      new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Times,
        true,
        false
        ),
      120
      );
    // Set the color to fill the text characters!
    composer.setFillColor(
      new DeviceRGBColor(115 / 255d, 164 / 255d, 232 / 255d)
      );
    // Show the text!
    composer.showText(
      "PDFClown", // Text to show.
      new Point2D.Double(size.getWidth() / 2d, size.getHeight() / 2d), // Anchor location: page center.
      XAlignmentEnum.Center, // Horizontal placement (relative to the anchor): center.
      YAlignmentEnum.Middle, // Vertical placement (relative to the anchor): middle.
      50 // Rotation: 50-degree-counterclockwise.
      );
    // 2.3. Flush the contents into the watermark!
    composer.flush();

    return watermark;
  }
}