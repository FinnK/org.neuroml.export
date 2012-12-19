/**
 * 
 */
package org.neuroml.export.sbml;


import java.io.File;
import java.util.ArrayList;
import org.neuroml.export.base.BaseWriter;
import org.neuroml.export.base.XMLWriter;
import org.neuroml.export.xpp.XppWriter;

import org.lemsml.jlems.type.dynamics.OnCondition;
import org.lemsml.jlems.type.dynamics.OnStart;
import org.lemsml.jlems.type.dynamics.StateAssignment;
import org.lemsml.jlems.type.dynamics.StateVariable;
import org.lemsml.jlems.type.dynamics.TimeDerivative;
//import org.lemsml.jlems.expression.;
import org.lemsml.jlems.expression.ParseError;
import org.lemsml.jlems.expression.ParseTree;
import org.lemsml.jlems.expression.Parser;
import org.lemsml.jlems.logging.E;
import org.lemsml.jlems.type.Component;
import org.lemsml.jlems.type.ComponentType;
import org.lemsml.jlems.type.Target;
import org.lemsml.jlems.type.FinalParam;
import org.lemsml.jlems.type.Lems;
import org.lemsml.jlems.sim.ContentError;
import org.lemsml.jlems.sim.Sim;
import org.lemsml.jlemsio.reader.FileInclusionReader;
import org.lemsml.jlemsio.util.FileUtil;

public class SBMLWriter extends XMLWriter {

    public static final String GLOBAL_TIME_SBML = "t";
    public static final String GLOBAL_TIME_SBML_MATHML = "<csymbol encoding=\"text\" definitionURL=\"http://www.sbml.org/sbml/symbols/time\"> time </csymbol>";

    public SBMLWriter(Lems l) {
        super(l);
    }

    private String getPopPrefix(Component pop) {
        return pop.getID() + "_";
    }

    public String getMainScript() throws ContentError {

        Parser p = new Parser();

        StringBuilder main = new StringBuilder();
        main.append("<?xml version='1.0' encoding='UTF-8'?>\n");

        String[] attrs = new String[]{"xmlns=http://www.sbml.org/sbml/level2/version2",
            "metaid=metaid_0000001",
            "level=2",
            "version=2",
            "xmlns:xsi=http://www.w3.org/2001/XMLSchema-instance",
            "xsi:schemaLocation=http://www.sbml.org/sbml/level2/version2 http://sbml.org/Special/xml-schemas/sbml-l2v2-schema/sbml.xsd"};
        startElement(main, "sbml", attrs);
        startElement(main, "notes");
        startElement(main, "p", "xmlns=http://www.w3.org/1999/xhtml");
        main.append("\nSBML export for:\n" + lems.textSummary(false, false) + "\n");
        endElement(main, "p");
        endElement(main, "notes");


        Target dr = lems.getTarget();
        
        Component simSetCpt = dr.getComponent();
        E.info("simSetCpt: "+simSetCpt.getAllChildren());
        Component simCpt = null;
        for (Component c : simSetCpt.getAllChildren()) {
        	if (c.getTypeName().equals("Simulation"))
        		simCpt = c;
        }
        E.info("simCpt: "+simCpt);

        String targetId = simCpt.getStringValue("target");

        Component tgtNet = lems.getComponent(targetId);
        addComment(main, "Adding simulation " + simCpt + " of network: " + tgtNet.summary() + "", true);

        String netId = tgtNet.getID();
        startElement(main, "model", "id=" + netId, "name=" + netId);
        main.append("\n");

        //indent="";

        ArrayList<Component> pops = tgtNet.getChildrenAL("populations");

        int initAssNum = 0;
        int onCondNum = 0;

        startElement(main, "listOfCompartments");

        for (Component pop : pops) {
            String compRef = pop.getStringValue("component");
            Component popComp = lems.getComponent(compRef);
            ComponentType type = popComp.getComponentType();
            initAssNum = initAssNum + type.getDynamics().getOnStarts().size();
            onCondNum = onCondNum + type.getDynamics().getOnConditions().size();
            
            int num = Integer.parseInt(pop.getStringValue("size"));
            addComment(main, "Population " + pop.getID() + " contains " + num + " instances of components of: " + popComp, true);

            for (int i = 0; i < num; i++) {
                startEndElement(main, "compartment", "id=" + pop.getID() + "_" + i, "size=1");
            }

        }

        endElement(main, "listOfCompartments");

        main.append("\n");

        startElement(main, "listOfParameters");

        for (Component pop : pops) {
            String compRef = pop.getStringValue("component");
            Component popComp = lems.getComponent(compRef);


            for (FinalParam param : popComp.getComponentType().getFinalParams()) {
                startEndElement(main,
                        "parameter",
                        "id=" + param.getName(),
                        "value=" + (float) popComp.getParamValue(param.getName()).getDoubleValue(),
                        "constant=false");
            }

            for (StateVariable sv : popComp.getComponentType().getDynamics().getStateVariables()) {
                startEndElement(main,
                        "parameter",
                        "id=" + sv.getName(),
                        "constant=false");

            }
            /*
            for(Constant c: popComp.getComponentClass().getConstants())
            {
            startEndElement(main,
            "parameter",
            "id="+c.getName(),
            "value="+(float)popComp.getParamValue(c.getName()).getDoubleValue(),
            "constant=false");

            }*/
        }

        endElement(main, "listOfParameters");
        main.append("\n");

        if (initAssNum>0)
        {
            startElement(main, "listOfInitialAssignments");

            for (Component pop : pops) {
                String compRef = pop.getStringValue("component");
                Component popComp = lems.getComponent(compRef);
                ComponentType type = popComp.getComponentType();

                for (OnStart os : type.getDynamics().getOnStarts()) {
                    for (StateAssignment sa : os.getStateAssignments()) {


                        startElement(main, "initialAssignment", "symbol=" + sa.getStateVariable().getName());
                        processMathML(main, sa.getParseTree());
                        endElement(main, "initialAssignment");



                    }
                }
            }

            endElement(main, "listOfInitialAssignments");
        }
        
        main.append("\n");

        startElement(main, "listOfRules");

        for (Component pop : pops) {
            String compRef = pop.getStringValue("component");
            Component popComp = lems.getComponent(compRef);
            ComponentType type = popComp.getComponentType();

            for (TimeDerivative td : type.getDynamics().getTimeDerivatives()) {
                startElement(main, "rateRule", "variable=" + td.getStateVariable().getName());
                processMathML(main, td.getParseTree());
                endElement(main, "rateRule");

            }
            /*
            for(OnStart os: type.getDynamics().getOnStarts()){
            for(StateAssignment sa: os.getStateAssignments()){
            startElement(main,"assignmentRule", "variable="+sa.getStateVariable().getName());
            processMathML(main, sa.getEvaluable());
            endElement(main,"assignmentRule");

            }
            }*/
        }


        endElement(main, "listOfRules");
        main.append("\n");


        if (onCondNum>0)
        {
            startElement(main, "listOfEvents");

            for (Component pop : pops) {
                String compRef = pop.getStringValue("component");
                Component popComp = lems.getComponent(compRef);
                ComponentType type = popComp.getComponentType();

                for (OnCondition oc : type.getDynamics().getOnConditions()) {

                    String id = "check__" + getSuitableId(oc.test);
                    startElement(main, "event", "id=" + id);
                    startElement(main, "trigger");
                    //String tempTestString = oc.test.replace(".gt.", ">").replace(".lt.", "<");

                    try {
                    	ParseTree testEval = p.parseCondition(oc.test);
                        processMathML(main, testEval);
                    } catch (ParseError ex) {
                        throw new ContentError("Problem parsing string for triggering event: " + oc.test, ex);
                    }

                    endElement(main, "trigger");
                    startElement(main, "listOfEventAssignments");

                    for (StateAssignment sa : oc.getStateAssignments()) {

                        startElement(main, "eventAssignment", "variable=" + sa.getStateVariable().getName());
                        processMathML(main, sa.getParseTree());
                        endElement(main, "eventAssignment");

                    }
                    endElement(main, "listOfEventAssignments");
                    endElement(main, "event");

                }
            }
            endElement(main, "listOfEvents");
        }


        main.append("\n");


        endElement(main, "model");
        endElement(main, "sbml");
        System.out.println(main);
        return main.toString();
    }

    private String getSuitableId(String str) {
        return str.replace(" ", "_").replace(".", "").replace("(", "_").replace(")", "_").replace("*", "mult").replace("+", "plus").replace("/", "div");
    }
    

    public static void main(String[] args) throws Exception
    {
    	File exampleSrc = new File("/home/padraig/NeuroML2/NeuroML2CoreTypes");
    	File nml2DefSrc = new File("/home/padraig/NeuroML2/NeuroML2CoreTypes");
        //Note: only works with this example at the moment!!
    	
        File xml = new File(exampleSrc, "LEMS_NML2_Ex9_FN.xml");
        File sbmlFile = new File("/home/padraig/NeuroML2/NeuroML2CoreTypes/LEMS_NML2_Ex9_FN.sbml");



        FileInclusionReader fir = new FileInclusionReader(xml);
        fir.addSearchPaths(nml2DefSrc.getAbsolutePath());
        Sim sim = new Sim(fir.read());
            
        sim.readModel();
		Lems lems = sim.getLems();

        SBMLWriter sbmlw = new SBMLWriter(lems);
        String sbml = sbmlw.getMainScript();

        System.out.println(sbml);
                    
        FileUtil.writeStringToFile(sbml, sbmlFile);

        System.out.println("Written to: "+sbmlFile.getAbsolutePath());


    }
}