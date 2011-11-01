package org.pdfclown.samples.cli;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.EnumSet;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.AlignmentXEnum;
import org.pdfclown.documents.contents.composition.AlignmentYEnum;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;

/**
  This sample demonstrates <b>how to use of standard Type 1 fonts</b>, which are the14 built-in fonts
  prescribed by the PDF specification to be shipped along with any conformant PDF viewer.
  <p>In particular, this sample displays the complete glyphset of each standard font, iterating through
  character codes and glyph styles (regular, italic, bold).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 11/01/11
*/
public class StandardFontSample
  extends Sample
{
  private static final int FontBaseSize = 20;
  private static final int Margin = 50;

  @Override
  public boolean run(
    )
  {
    // 1. PDF file instantiation.
    File file = new File();
    Document document = file.getDocument();

    // 2. Content creation.
    populate(document);

    // 3. Serialize the PDF file!
    serialize(file, false, "Standard Type 1 fonts", "applying standard Type 1 fonts");

    return true;
  }

  private void populate(
    Document document
    )
  {
    Page page = new Page(document);
    document.getPages().add(page);
    Dimension2D pageSize = page.getSize();

    PrimitiveComposer composer = new PrimitiveComposer(page);

    int x = Margin, y = Margin;
    StandardType1Font titleFont = new StandardType1Font(
      document,
      StandardType1Font.FamilyEnum.Times,
      true,
      true
      );
    StandardType1Font font = null;
    // Iterating through the standard Type 1 fonts...
    for(StandardType1Font.FamilyEnum fontFamily
      : EnumSet.allOf(StandardType1Font.FamilyEnum.class))
    {
      // Iterating through the font styles...
      for(int styleIndex = 0; styleIndex < 4; styleIndex++)
      {
        /*
          NOTE: Symbol and Zapf Dingbats are available just as regular fonts (no italic or bold variant).
        */
        if(styleIndex > 0
          && (fontFamily == StandardType1Font.FamilyEnum.Symbol
            || fontFamily == StandardType1Font.FamilyEnum.ZapfDingbats))
            break;

        boolean bold, italic;
        switch(styleIndex)
        {
          case 0: // Regular.
            bold = false;
            italic = false;
            break;
          case 1: // Bold.
            bold = true;
            italic = false;
            break;
          case 2: // Italic.
            bold = false;
            italic = true;
            break;
          case 3: // Bold italic.
            bold = true;
            italic = true;
            break;
          default:
            throw new RuntimeException("styleIndex " + styleIndex + " not supported.");
        }
        // Define the font used to show its character set!
        font = new StandardType1Font(
          document,
          fontFamily,
          bold,
          italic
          );

        if(y > pageSize.getHeight() - Margin)
        {
          composer.flush();

          page = new Page(document);
          document.getPages().add(page);
          pageSize = page.getSize();
          composer = new PrimitiveComposer(page);
          x = Margin; y = Margin;
        }

        if(styleIndex == 0)
        {
          composer.drawLine(
            new Point2D.Double(x,y),
            new Point2D.Double(pageSize.getWidth() - Margin,y)
            );
          composer.stroke();
          y += 5;
        }

        composer.setFont(
          titleFont,
          FontBaseSize * (styleIndex == 0 ? 1.5f : 1)
          );
        composer.showText(
          fontFamily.name() + (bold ? " bold" : "") + (italic ? " italic" : ""),
          new Point2D.Double(x,y)
          );

        y += 40;
        // Set the font used to show its character set!
        composer.setFont(font,FontBaseSize);
        // Iterating through the font characters...
        for(int charCode = 32; charCode < 256; charCode++)
        {
          if(y > pageSize.getHeight() - Margin)
          {
            composer.flush();

            page = new Page(document);
            document.getPages().add(page);
            pageSize = page.getSize();
            composer = new PrimitiveComposer(page);
            x = Margin; y = Margin;

            composer.setFont(titleFont,FontBaseSize);
            composer.showText(
              fontFamily.name() + " (continued)",
              new Point2D.Double(pageSize.getWidth() - Margin, y),
              AlignmentXEnum.Right,
              AlignmentYEnum.Top,
              0
              );
            composer.setFont(font,FontBaseSize);
            y += FontBaseSize * 2;
          }

          try
          {
            // Show the current character (using the current standard Type 1 font)!
            composer.showText(
              new String(new char[]{(char)charCode}),
              new Point2D.Double(x,y)
              );
            x += FontBaseSize;
            if(x > pageSize.getWidth() - Margin)
            {x = Margin; y += 30;}
          }
          catch(Exception e)
          { /* Ignore */ }
        }

        x = Margin; y += Margin;
      }
    }
    composer.flush();
  }
}