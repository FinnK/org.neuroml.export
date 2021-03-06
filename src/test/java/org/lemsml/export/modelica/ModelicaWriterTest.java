package org.lemsml.export.modelica;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.lemsml.export.matlab.MatlabWriter;
import org.lemsml.jlems.core.expression.ParseError;
import org.lemsml.jlems.core.run.ConnectionError;
import org.lemsml.jlems.core.run.RuntimeError;
import org.lemsml.jlems.core.sim.ContentError;
import org.lemsml.jlems.core.sim.ParseException;
import org.lemsml.jlems.core.type.BuildException;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.core.xml.XMLException;
import org.lemsml.jlems.io.util.FileUtil;
import org.neuroml.export.AppTest;

public class ModelicaWriterTest extends TestCase {


	public void testFN() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError {

    	String exampleFilename = "LEMS_NML2_Ex9_FN.xml";
    	generateMainScript(exampleFilename);
	}
	
	public void testHH() throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError {

    	String exampleFilename = "LEMS_NML2_Ex1_HH.xml";
    	generateMainScript(exampleFilename);
	}
	
	public void generateMainScript(String exampleFilename) throws ContentError, ParseError, ParseException, BuildException, XMLException, IOException, ConnectionError, RuntimeError {


    	Lems lems = AppTest.readLemsFileFromExamples(exampleFilename);
        
        System.out.println("Loaded: "+exampleFilename);

        ModelicaWriter mw = new ModelicaWriter(lems);

        String mod = mw.generateMainScriptAndCompFiles(AppTest.getTempDir());
        
        for (File genFile: mw.allGeneratedFiles)
        {
        	assertTrue(genFile.exists());
            System.out.println("------------------"+genFile.getAbsolutePath()+"------------------------------------");
            System.out.println(FileUtil.readStringFromFile(genFile));
        }
	}

}
