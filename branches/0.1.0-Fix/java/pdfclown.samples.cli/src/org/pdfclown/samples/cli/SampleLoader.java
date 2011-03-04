package org.pdfclown.samples.cli;

import it.stefanochizzolini.reflex.Package;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
  Command-line sample loader.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.1
  @version 0.1.0
*/
public class SampleLoader
{
  // <static>
  // <fields>
  private static final String ClassName = SampleLoader.class.getName();

  private static final String PropertiesFilePath = "pdfclown-samples-cli.properties";

  private static final String Properties_InputPath = ClassName + ".inputPath";
  private static final String Properties_OutputPath = ClassName + ".outputPath";

  private static final String QuitChoiceSymbol = "Q";
  // </fields>

  // <interface>
  // <public>
  public static void main(
    String[] args
    )
  {
    System.out.println("\nSampleLoader running...\n");

    try
    {
      java.lang.Package pdfClownPackage = Class.forName("org.pdfclown.Engine").getPackage();
      System.out.println(pdfClownPackage.getSpecificationTitle() + " version " + pdfClownPackage.getImplementationVersion());
    }
    catch(Exception e)
    {throw new RuntimeException("Unable to detect PDF Clown library version.",e);}

    Properties properties = new Properties();
    try
    {properties.load(new FileInputStream(PropertiesFilePath));}
    catch(Exception e)
    {throw new RuntimeException("An exception occurred while loading the properties file (\"" + PropertiesFilePath + "\").",e);}

    run(
      new java.io.File(properties.getProperty(Properties_InputPath)).getAbsolutePath(),
      new java.io.File(properties.getProperty(Properties_OutputPath)).getAbsolutePath()
      );

    System.out.println("\nSampleLoader finished.\n");
  }
  // </public>

  // <private>
  private static void run(
    String inputPath,
    String outputPath
    )
  {
    File outputDir = new File(outputPath);
    if(!outputDir.exists())
    {outputDir.mkdirs();}

    Scanner in = new Scanner(System.in);
    String samplePackageName = SampleLoader.class.getPackage().getName();
    while(true)
    {
      // Get the classes belonging to the current package!
      List<Class<?>> samplePackageClasses = Package.getClasses(
        samplePackageName,
        new String[]{it.stefanochizzolini.reflex.Class.getLocation(ClassName)} // Locations: current deployment unit only.
        );
      Collections.sort(
        samplePackageClasses,
        new Comparator<Class<?>>()
        {
          @Override
          public int compare(Class<?> arg0, Class<?> arg1)
          {return arg0.getSimpleName().compareTo(arg1.getSimpleName());}
        }
        );

      // Picking available samples...
      System.out.println("\nAvailable samples:");
      List<java.lang.Class<?>> sampleClasses = new ArrayList<java.lang.Class<?>>();
      for(Class<?> samplePackageClass : samplePackageClasses)
      {
        if(Sample.class.isAssignableFrom(samplePackageClass)
            && !Modifier.isAbstract(samplePackageClass.getModifiers()))
        {
          sampleClasses.add(samplePackageClass);
          System.out.println("[" + sampleClasses.indexOf(samplePackageClass) + "] " + samplePackageClass.getSimpleName());
        }
      }
      System.out.println("[" + QuitChoiceSymbol + "] (Quit)");

      // Getting the user's choice...
      Class<?> sampleClass = null;
      do
      {
        System.out.print("Please select a sample: ");
        try
        {
          String choice = in.nextLine();
          if(choice.toUpperCase().equals(QuitChoiceSymbol)) // Quit.
            return;

          sampleClass = sampleClasses.get(Integer.parseInt(choice));
        }
        catch(Exception e)
        {/* NOOP */}
      } while(sampleClass == null);

      // Instantiate the sample!
      System.out.println("\nInstantiating " + sampleClass.getSimpleName() + " sample...");
      Sample sample;
      try
      {
        sample = (Sample)sampleClass.newInstance();
        sample.initialize(inputPath, outputPath);
      }
      catch(Exception e)
      {throw new RuntimeException(sampleClass.getName() + " sample class has failed to instantiate.",e);}

      // Run the sample!
      System.out.println("Running " + sampleClass.getSimpleName() + " sample...");
      try
      {
        if(sample.run())
        {Utils.prompt("Sample finished.");}
      }
      catch(Exception e)
      {
        System.out.println("An exception happened while running the sample:");
        e.printStackTrace();
      }
    }
  }
  // </private>
  // </interface>
  // </static>
}
