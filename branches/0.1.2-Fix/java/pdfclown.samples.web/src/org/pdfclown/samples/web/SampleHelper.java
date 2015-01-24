package org.pdfclown.samples.web;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.documents.contents.xObjects.FormXObject;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;

public class SampleHelper
{
  /**
    Creates a common page template to use when generating sample documents.
    @return Page template.
  */
  public static FormXObject createTemplate(
    Document document
    )
  {
    // Create a template (form)!
    FormXObject template = new FormXObject(document, document.getPageSize());

    // Size.
    Dimension2D templateSize = template.getSize();

    PrimitiveComposer composer = new PrimitiveComposer(template);

    Image image = null;
    try
    {
      // Instantiate a jpeg image object!
      image = Image.get(new java.io.File(((java.net.URL)SampleHelper.class.getResource("mountains.jpg")).toURI()).getPath()); // Abstract image (entity).
    }
    catch(Exception e)
    {/* NOOP. */}
    // Show the image inside the common content stream!
    composer.showXObject(
      image.toXObject(document),
      new Point2D.Double(0,0),
      new Dimension(
        (int)templateSize.getWidth() - 50,
        125
        )
      );

    // Showing the 'PDFClown' label inside the common content stream...
    composer.beginLocalState();
    composer.setFillColor(
      new DeviceRGBColor(115f/255,164f/255,232f/255)
      );
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
    // Show the text!
    composer.showText(
      "PDFClown",
      new Point2D.Double(
        0,
        templateSize.getHeight() - composer.getState().getFont().getAscent(composer.getState().getFontSize())
        )
      );

    // Drawing the side rectangle...
    composer.drawRectangle(
      new Rectangle2D.Double(
        templateSize.getWidth() - 50,
        0,
        50,
        templateSize.getHeight()
        )
      );
    composer.fill();
    composer.end();

    // Begin the graphics state!
    composer.beginLocalState();
    // Set the font to use!
    composer.setFont(
      new StandardType1Font(
        document,
        StandardType1Font.FamilyEnum.Helvetica,
        false,
        false
        ),
      8
      );
    // Set the fill color to use!
    composer.setFillColor(
      DeviceRGBColor.White
      );
    composer.beginLocalState();
    composer.translate((float)templateSize.getWidth() - 20, 20);
    composer.rotate(90);
    composer.showText("Generated by PDF Clown on " + (new Date()));
    composer.translate(0,-8);
    composer.showText("For more info, visit http://www.pdfclown.org");
    composer.end();
    // End the graphics state!
    composer.end();

    composer.flush();

    return template;
  }
}