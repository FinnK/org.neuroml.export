package org.lemsml.export.vhdl.writer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.lemsml.export.vhdl.edlems.EDComponent;
import org.lemsml.export.vhdl.edlems.EDConditionalDerivedVariable;
import org.lemsml.export.vhdl.edlems.EDDerivedVariable;
import org.lemsml.export.vhdl.edlems.EDEventConnection;
import org.lemsml.export.vhdl.edlems.EDEventPort;
import org.lemsml.export.vhdl.edlems.EDParameter;
import org.lemsml.export.vhdl.edlems.EDRequirement;
import org.lemsml.export.vhdl.edlems.EDSimulation;
import org.lemsml.export.vhdl.edlems.EDState;

public class NeuronCoreTop {

	static int NPcurrentBit = -1;//5119;
	static int NPcurrentLength = 0;
	static int NPInteger = 0;
	static int NScurrentBit = -1;//5119;
	static int NScurrentLength = 0;
	static int NSInteger = 0;
	static int SPcurrentBit = -1;//5119;
	static int SPcurrentLength = 0;
	static int SPInteger = 0;
	static int SScurrentBit = -1;//5119;
	static int SScurrentLength = 0;
	static int SSInteger = 0;

	static int NPMax = 0;
	static int NSMax = 0;
	static int SPMax = 0;
	static int SSMax = 0;
	
	public static void writeSignal(String name, int bottom, int top, 
			int integer, int fractional,String value, JsonGenerator g) throws JsonGenerationException, IOException
	{
		writeSignal(name,bottom,top,integer,fractional,Float.parseFloat(value),g);
	}
	
	public static void writeSignal(String name, int bottom, int top, 
			int integer, int fractional,float value, JsonGenerator g) throws JsonGenerationException, IOException
	{
		g.writeStartObject();
		g.writeStringField("name",name);
		g.writeNumberField("bottom", bottom);
		g.writeNumberField("top", top);
		g.writeNumberField("integer", integer);
		g.writeNumberField("fractional", fractional);
		g.writeNumberField("value",value);
		g.writeEndObject();
	}
	
	public static void writeNeuronCoreTop(EDSimulation sim, StringBuilder sb
			, JsonGenerator g, Map<String,Float> initialStateValues, String neuronName) throws JsonGenerationException, IOException
	{
		 NPcurrentBit = -1;//5119;
		 NPcurrentLength = 0;
		 NPInteger = 0;
		 NScurrentBit = -1;//5119;
		 NScurrentLength = 0;
		 NSInteger = 0;
		 SPcurrentBit = -1;//5119;
		 SPcurrentLength = 0;
		 SPInteger = 0;
		 SScurrentBit = -1;//5119;
		 SScurrentLength = 0;
		 SSInteger = 0;
		sb.append("\r\n" + 
				"library ieee;\r\n" + 
				"use ieee.std_logic_1164.all;\r\n" + 
				"use ieee.std_logic_unsigned.all;\r\n" + 
				"-- For Modelsim\r\n" + 
				"--use ieee.fixed_pkg.all;\r\n" + 
				"--use ieee.fixed_float_types.ALL;\r\n" + 
				"\r\n" + 
				"-- For ISE\r\n"
				+ "library ieee_proposed;\r\n" + 
				"use ieee_proposed.fixed_pkg.all;\r\n" + 
				"use ieee_proposed.fixed_float_types.ALL;\r\n" + 
				"use std.textio.all;\r\n" + 
				"use ieee.std_logic_textio.all; -- if you're saving this type of signal\r\n" + 
				"use IEEE.numeric_std.all;\r\n" + 
				"");
		
		sb.append("\r\n" + 
				"entity neuroncore_top is\r\n" + 
				"   generic (\r\n" + 
				"		NP_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		SP_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		NS_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		SS_WORDSWIDTH	: integer	:= 1\r\n" + 
				"		);\r\n" + 
				"Port ( clk : in STD_LOGIC; --SYSTEM CLOCK, THIS ITSELF DOES NOT SIGNIFY TIME STEPS - AKA A SINGLE TIMESTEP MAY TAKE MANY CLOCK CYCLES\r\n" + 
				"  rst : in STD_LOGIC; \r\n" + 
				"  sendSpike : out STD_LOGIC; \r\n" + 
				"  receiveSpike : in STD_LOGIC; \r\n" + 
				"  runNeuronStep : in STD_LOGIC; \r\n" + 
				"  runSynapseStep : in STD_LOGIC; \r\n" + 
				"  busy : out STD_LOGIC; \r\n" + 
				"  busy_synapse : out STD_LOGIC; \r\n" + 				
				"  timeStep : in std_logic_vector(31 downto 0);\r\n" + 
				"  timeStamp : in std_logic_vector(31 downto 0);\r\n" + 
				"  currentTime : in std_logic_vector(31 downto 0);\r\n" + 
				"  synapseType : in std_logic_vector(7 downto 0);\r\n" + 
				"  step_once_clearCurrent : in std_logic;\r\n" + 				
				"  NP_IN : in std_logic_vector(((NP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"  SP_IN : in std_logic_vector(((SP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"  NS_IN : in std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"  SS_IN : in std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"  NS_OUT : out std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0):= (others => '0');\r\n" + 
				"  SS_OUT : out std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0):= (others => '0')\r\n" + 
				");" + 
				"end neuroncore_top;\r\n" + 
				"\r\n" + 
				"---------------------------------------------------------------------\r\n" + 
				"\r\n" + 
				"architecture top of neuroncore_top is\r\n");
		

		EDComponent neuron = sim.neuronComponents.get(0);
		for (int i = 0; i < sim.neuronComponents.size(); i++)
		{
			if (sim.neuronComponents.get(i).name.matches(neuronName))
			{
				neuron = sim.neuronComponents.get(i);
				break;
			}
		}
		

		sb.append("\r\n" + 
				"component "+neuron.name+"\r\n" + 
				"    Port ( clk : in STD_LOGIC; --SYSTEM CLOCK, THIS ITSELF DOES NOT SIGNIFY TIME STEPS - AKA A SINGLE TIMESTEP MAY TAKE MANY CLOCK CYCLES\r\n" + 
				"           init_model: in STD_LOGIC; --SYNCHRONOUS RESET\r\n" + 
				"		   step_once_go : in STD_LOGIC; --signals to the neuron from the core that a time step is to be simulated\r\n" + 
				"		   step_once_complete : out STD_LOGIC; --signals to the core that a time step has finished\r\n" + 
				"		   eventport_in_spike : in STD_LOGIC;\r\n" + 
				"          step_once_clearCurrent : in STD_LOGIC;\r\n" +
				//"		   SelectSpikesIn :  in STD_LOGIC_VECTOR(4607 downto 0);\r\n" + 
				"		   ");
		String name = "";
		for(Iterator<EDEventPort> i = neuron.eventports.iterator(); i.hasNext(); ) {
		    EDEventPort item = i.next();
		    sb.append("			eventport_" + item.direction +  "_" + item.name + " : " + item.direction + " STD_LOGIC;\r\n"  );
		}

		if (neuron.regimes.size() > 0)
			sb.append("			current_regime_in_stdlv : in STD_LOGIC_VECTOR(1 downto 0);\r\n" + 
					  "			current_regime_out_stdlv : out STD_LOGIC_VECTOR(1 downto 0);\r\n");
		Entity.writeEntitySignals(neuron,sb,name,"",true);
		sb.append("\r\n" + 
				"           sysparam_time_timestep : sfixed (-6 downto -22);\r\n" + 
				"           sysparam_time_simtime : sfixed (6 downto -22)\r\n" + 
				"		   );\r\n" + 
				"end component;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	signal eventport_out_spike_out : STD_LOGIC := '0';\r\n" + 
				"	");
		

		for(Iterator<EDEventPort> i = neuron.eventports.iterator(); i.hasNext(); ) {
		    EDEventPort item = i.next();
		    sb.append("signal " + "" + "eventport_" + item.direction + "_" + item.name + "_internal : std_logic;\r\n"  );
		}
		if (neuron.regimes.size() > 0)
		{  
			sb.append("signal " + "" + "current_regime_out_stdlv_int :  STD_LOGIC_VECTOR(1 downto 0);\r\n");
		}
		writeStateSignals(neuron,sb,"","");


		//g.writeObjectFieldStart(neuron.name);
		g.writeArrayFieldStart("NeuronVariablesIn"); 

		sb.append("signal step_once_complete_int : STD_LOGIC;\r\n" + 
		"signal seven_steps_done : STD_LOGIC;\r\n" + 
		"signal step_once_go_int : STD_LOGIC := '0';\r\n" + 
		"signal seven_steps_done_shot_done : STD_LOGIC := '1';\r\n" + 
		"signal seven_steps_done_shot : STD_LOGIC;\r\n" + 
		"signal seven_steps_done_shot2 : STD_LOGIC;\r\n" + 					
		"signal COUNT : unsigned(2 downto 0) := \"110\";\r\n" + 
		"signal seven_steps_done_next : STD_LOGIC;\r\n" + 
		"signal COUNT_next : unsigned(2 downto 0) := \"110\";\r\n" + 
		"signal step_once_go_int_next : STD_LOGIC := '0';\r\n" + 
		"");

		sb.append("signal step_once_complete_syn_int : STD_LOGIC;\r\n" + 
		"signal seven_steps_done_syn : STD_LOGIC;\r\n" + 
		"signal step_once_go_syn_int : STD_LOGIC := '0';\r\n" + 
		"signal seven_steps_done_syn_shot_done : STD_LOGIC;\r\n" + 
		"signal seven_steps_done_syn_shot : STD_LOGIC;\r\n" + 
		"signal seven_steps_done_syn_shot2 : STD_LOGIC;\r\n" + 					
		"signal COUNT_syn : unsigned(2 downto 0) := \"110\";\r\n" + 
		"signal seven_steps_done_syn_next : STD_LOGIC;\r\n" + 
		"signal COUNT_syn_next : unsigned(2 downto 0) := \"110\";\r\n" + 
		"signal step_once_go_syn_int_next : STD_LOGIC := '0';\r\n" + 
		"signal step_once_go_syn_addCurrent_int : STD_LOGIC := '0';\r\n" +
		"signal step_once_go_syn_addCurrent_int_next : STD_LOGIC := '0';\r\n"
		+ "signal busy_synapse_internal : std_logic := '0';\r\n" +
		"");
		
		
		
		
		sb.append("begin\r\n"
				+ "\r\n"
				+ "neuron_model_uut : " + neuron.name + " \r\n" + 
				"    port map (	clk => clk,\r\n" + 
				"				init_model => rst, \r\n" + 
				"		   step_once_go  => step_once_go_int,\r\n" + 
				"		   step_once_complete  => step_once_complete_int,\r\n" + 
				"		   eventport_in_spike =>  receiveSpike,\r\n" + 
				"          step_once_clearCurrent => step_once_clearCurrent,\r\n" +
				//"		   SelectSpikesIn => mega_bus_in(5119 downto 512),\r\n" + 
				"		   ");
		

		for(Iterator<EDEventPort> i = neuron.eventports.iterator(); i.hasNext(); ) {
		    EDEventPort item = i.next();
		    sb.append("			" + "" + "eventport_" + item.direction +  "_" + item.name + " => sendSpike ,\r\n"   );
		}
		


		if (neuron.regimes.size() > 0)
		{
			NScurrentBit = NScurrentBit + NScurrentLength + 1;
			while (NScurrentBit%8 != 0)
				NScurrentBit++;
			NScurrentLength = 1;
			int currentTop = NScurrentBit + NScurrentLength;
			writeSignal("regime",NScurrentBit,currentTop,0,0,0,g);
			sb.append("" + "" + "current_regime_in_stdlv =>  NS_IN(" + currentTop + " downto " + 
					NScurrentBit + "),\r\n");
			sb.append("" + "" + "current_regime_out_stdlv => " + "" + "current_regime_out_stdlv_int,\r\n");
		}
		writeConnectivityMapVar(neuron,sb,"","", initialStateValues,g,false);
		
		g.writeEndArray();
		g.writeArrayFieldStart("NeuronParameters");
		writeConnectivityMapPar(neuron,sb,"","",g,false);
		
		sb.append("sysparam_time_timestep => to_sfixed (timeStep(16 downto 0),-6 , -22),\r\n" + 
				"sysparam_time_simtime => to_sfixed (currentTime(28 downto 0),6, -22)");
		sb.append("\r\n" + 
				"		   );\r\n");
		g.writeEndArray();

		
		

		sb.append("process (synapseType,step_once_go_syn_int,step_once_go_syn_addCurrent_int)\r\n" +
		"begin\r\n");
		
		int j = 0;
		for(Iterator<EDComponent> i = neuron.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (item.isSynapse) {
				SPcurrentBit = -1;
				SPcurrentLength = 0;
				SPInteger = 0;
				String newName = item.name + "_step_once_go_int";
				String newName2 = item.name + "_step_once_addCurrent_int";
				

				sb.append("if (to_integer(unsigned(synapseType)) = (" + j + ")) then\r\n" + 
					"  " + newName + " <= step_once_go_syn_int;\r\n" + 
					"  " + newName2 + " <= step_once_go_syn_addCurrent_int;\r\n" + 
				"else\r\n" +
					"  " + newName + " <= '0';\r\n" + 		
					"  " + newName2 + " <= '0';\r\n" + 				
				"end if;\r\n"
						); 
				j++;

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
				
				
			}
		}
		

		sb.append("end process;\r\n");
		
		

		sb.append("\r\n" +  
				"\r\n" + 
				"count_proc_comb:process(rst,step_once_complete_int,COUNT,runNeuronStep)\r\n" + 
				"  	begin \r\n" + 
				"		seven_steps_done_next <= '0';\r\n" + 
				"		COUNT_next <= COUNT;\r\n" + 
				"		step_once_go_int_next <= '0';\r\n" + 
				"			if (rst='1') then \r\n" + 
				"				seven_steps_done_next <= '0';\r\n" + 
				"				COUNT_next <= \"110\";\r\n" + 
				"		        step_once_go_int_next <= '0';\r\n" + 
				"			else\r\n" + 
				"				if step_once_complete_int = '1'	then\r\n" + 
				"					if (COUNT = \"110\") then\r\n" + 
				"						seven_steps_done_next <= '1';\r\n" + 
				"					    COUNT_next <= \"110\";\r\n" + 
				"						step_once_go_int_next <= '0';\r\n" + 
				"					else\r\n" + 
				"						seven_steps_done_next <= '0';\r\n" + 
				"						COUNT_next <= COUNT + 1;\r\n" + 
				"						step_once_go_int_next <= '1';\r\n" + 
				"					end if;\r\n" + 
				"				elsif runNeuronStep = '1' then\r\n" + 
				"					seven_steps_done_next <= '0';\r\n" +
				"					COUNT_next <= \"000\";\r\n" +  
				"					step_once_go_int_next <= '1';\r\n" + 
				"				else\r\n" + 
				"		            seven_steps_done_next <= seven_steps_done;\r\n" + 
				"		            COUNT_next <= COUNT;\r\n" + 
				"					step_once_go_int_next <= '0';\r\n" + 
				"				end if;\r\n" + 
				"			end if;\r\n" + 
				"end process count_proc_comb;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"count_proc_syn:process(clk)\r\n" + 
				"  	begin \r\n" + 
				"      if rising_edge(clk) then  \r\n" + 
				"		 if rst = '1' then\n" + 
				"		    COUNT <= \"110\";\n" + 
				"			 seven_steps_done <= '1';\n" + 
				"			 step_once_go_int <= '0';\n" + 
				"		  else\n" + 
				"		    COUNT <= COUNT_next;\n" + 
				"		    seven_steps_done <= seven_steps_done_next;\n" + 
				"		    step_once_go_int <= step_once_go_int_next;\n" + 
				"		  end if;" +
				"		end if;\r\n" + 
				"end process count_proc_syn;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n");
		
		sb.append("busy <= not seven_steps_done;\r\n\r\n");
		

		int k = 0;
		StringBuilder sbDone = new StringBuilder();
		StringBuilder sbDoneSensitivities = new StringBuilder();
		for(Iterator<EDComponent> i = neuron.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (item.isSynapse)
			{
				String name2 = "( " + item.name + "_step_once_complete_int = '1' and to_integer(unsigned(synapseType)) = (" + k + "))";
				if (sbDone.length() > 0){ 
					sbDone.append(" or "+ name2);
					sbDoneSensitivities.append("," + item.name + "_step_once_complete_int");
				}
				else {
					sbDone.append(name2);
					sbDoneSensitivities.append(item.name + "_step_once_complete_int");
				}
				k++;

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}
		

/*		if (sbDone.length() == 0){
			sbDone.append(" seven_steps_done_syn = '0' ");
			sbDoneSensitivities.append(",seven_steps_done_syn");
			
		}*/
		
		if (sbDone.length() > 0) {
		sb.append("\r\n" + 
				"---------------------------------------------------------------------\r\n" + 
				"-- Control the synapse done signal\r\n" + 
				"---------------------------------------------------------------------\r\n" + 
				"\r\n" + 
				"step_once_complete_syn_comb:process(" + sbDoneSensitivities.toString() + ",synapseType)\r\n" + 
				"begin \r\n" + 
				"if (" + sbDone.toString() + ") then\r\n" +
				  "  step_once_complete_syn_int <= '1';\r\n"
				+ "else\r\n"
				+ "  step_once_complete_syn_int <= '0';\r\n"
				+ "end if;\r\n" +  
				"end process step_once_complete_syn_comb;\r\n" + 
				"---------------------------------------------------------------------\r\n");
		

		sb.append("\r\n" + 
				"\r\n" + 
				"count_proc_comb_syn:process(rst,step_once_complete_syn_int,COUNT,runSynapseStep)\r\n" + 
				"  	begin \r\n" + 
				"		seven_steps_done_syn_next <= '0';\r\n" + 
				"		COUNT_syn_next <= COUNT_syn;\r\n" + 
				"		step_once_go_syn_int_next <= '0';\r\n" + 
				"			if (rst='1') then \r\n" + 
				"				seven_steps_done_syn_next <= '1';\r\n" + 
				"				COUNT_syn_next <= \"111\";\r\n" + 
				"		        step_once_go_syn_int_next <= '0';\r\n" + 
			"					step_once_go_syn_addCurrent_int_next <= '0';\r\n" + 
				"			else\r\n" + 
				"				if step_once_complete_syn_int = '1'	then\r\n" + 
				"					if (COUNT_syn = \"110\") then\r\n" + 
				"						seven_steps_done_syn_next <= '0';\r\n" + 
				"					    COUNT_syn_next <= \"111\";\r\n" + 
				"						step_once_go_syn_int_next <= '0';\r\n" + 
				"						step_once_go_syn_addCurrent_int_next <= '1';\r\n" + 
				"					else\r\n" + 
				"						seven_steps_done_syn_next <= '0';\r\n" + 
				"						COUNT_syn_next <= COUNT_syn + 1;\r\n" + 
				"						step_once_go_syn_int_next <= '1';\r\n" + 
				"						step_once_go_syn_addCurrent_int_next <= '0';\r\n" + 
				"					end if;\r\n" + 
				"				elsif runSynapseStep = '1' then\r\n" + 
				"					seven_steps_done_syn_next <= '0';\r\n" +
				"					COUNT_syn_next <= \"000\";\r\n" +  
				"					step_once_go_syn_int_next <= '1';\r\n" + 
				"					step_once_go_syn_addCurrent_int_next <= '0';\r\n" + 
				"				elsif (COUNT_syn = \"111\") then\r\n" + 
				"					seven_steps_done_syn_next <= '1';\r\n" + 
				"					COUNT_syn_next <= \"111\";\r\n" + 
				"					step_once_go_syn_int_next <= '0';\r\n" + 
				"					step_once_go_syn_addCurrent_int_next <= '0';\r\n" + 
				"				else\r\n" + 
				"		            seven_steps_done_syn_next <= seven_steps_done_syn;\r\n" + 
				"		            COUNT_syn_next <= COUNT_syn;\r\n" + 
				"					step_once_go_syn_int_next <= '0';\r\n" +
				"					step_once_go_syn_addCurrent_int_next <= '0';\r\n" + 
				"				end if;\r\n" + 
				"			end if;\r\n" + 
				"end process count_proc_comb_syn;\r\n" + 
				"\r\n" + 
				"\r\n" +  
				"\r\n" + 
				"count_proc_syn_syn:process(clk)\r\n" + 
				"  	begin \r\n" + 
				"      if rising_edge(clk) then  \r\n" + 
				"		 if rst = '1' then\n" + 
				"		    COUNT_syn <= \"110\";\n" + 
				"			 seven_steps_done_syn <= '1';\n" + 
				"			 step_once_go_syn_int <= '0';\n" + 
				"		  else\n" + 
				"		    COUNT_syn <= COUNT_syn_next;\n" + 
				"		    seven_steps_done_syn <= seven_steps_done_syn_next;\n" + 
				"		    step_once_go_syn_int <= step_once_go_syn_int_next;\n" + 
				"		    step_once_go_syn_addCurrent_int <= step_once_go_syn_addCurrent_int_next;\n" + 				
				"		  end if;" +
				"		end if;\r\n" + 
				"end process count_proc_syn_syn;\r\n" + 
				"\r\n" + 
				"busy_synapse <= not seven_steps_done_syn;\r\n" + 
				"\r\n" );

		}		else {
			sb.append("\r\n"
					+ "process(clk,rst)\r\n" + 
					"begin\r\n" + 
					"	if (rst = '1') then\r\n" + 
					"	busy_synapse_internal <= '0'; \r\n" + 
					"	elsif rising_edge(CLK) then \r\n" + 
					"		if runSynapseStep = '1' then\r\n" + 
					"			busy_synapse_internal <= '1';\r\n" + 
					"		else\r\n" + 
					"			busy_synapse_internal <= '0';\r\n" + 
					"		end if;\r\n" + 
					"	end if;\r\n" + 
					"end process;\r\n" + 
					"\r\n" + 
					"busy_synapse <= busy_synapse_internal;\r\n");
			
		}
		
		
		
		g.writeArrayFieldStart("NeuronVariablesOut");

		NScurrentBit = -1;
		NScurrentLength = 0;
		NSInteger = 0;
		

		
		sb.append("process (synapseType)\r\nbegin\r\n\r\n" + 
				" NS_OUT <= (others => '0');\r\n" + 
				" SS_OUT <= (others => '0');\r\n");

		if (neuron.regimes.size() > 0)
		{
			NScurrentBit = NScurrentBit + NScurrentLength + 1;
			while (NScurrentBit%8 != 0)
				NScurrentBit++;
			NScurrentLength = 1;
			int currentTop = NScurrentBit + NScurrentLength;
			writeSignal("regime",NScurrentBit,currentTop,0,0,0,g);
			sb.append(" NS_OUT(" + currentTop + " downto " + NScurrentBit + 
					") <= current_regime_out_stdlv_int;\r\n");
		}
		writeStateToBusSignals(neuron,sb,"","", initialStateValues,g);
		sb.append("end process;\r\n");

		
		/*for(Iterator<EDEventPort> i = neuron.eventports.iterator(); i.hasNext(); ) {
		    EDEventPort item = i.next();
		    NScurrentBit = NScurrentBit + NScurrentLength + 1;
			while (NScurrentBit%8 != 0)
				NScurrentBit++;
		    sb.append(" mega_bus_out("  + NScurrentBit + ") <= " + neuron.name + "_eventport_" + item.direction + "_" + item.name + "_internal;\r\n"  );
		}*/
		sb.append("\r\n" + 
				"end top;\r\n" + 
				"");
		
		g.writeEndArray();
		//g.writeEndObject();

		NPMax = NPcurrentBit + 1;
		NSMax = NScurrentBit + 1;
		
		

		g.writeNumberField("NPMax", NPMax);
		g.writeNumberField("NSMax", NSMax);
		g.writeNumberField("SPMax", SPMax);
		g.writeNumberField("SSMax", SSMax);
		
	}
	

	public static void writeStateToBusSignals(EDComponent comp, StringBuilder sb, 
			String name,String parentName,Map<String,Float> initialStateValues, JsonGenerator g) throws JsonGenerationException, IOException
	{
		for(Iterator<EDState> i = comp.state.iterator(); i.hasNext(); ) {
			EDState state = i.next(); 
			if (comp.isSynapse){
				SPcurrentBit = SPcurrentBit + SPcurrentLength + 1;
				while (SPcurrentBit%8 != 0)
					SPcurrentBit++;
				SPcurrentLength = (state.integer) - (state.fraction);
				int currentTop = SPcurrentBit + SPcurrentLength;
				writeSignal("statevariable_" + state.type +  "_" + name + state.name + "_out"
						,SPcurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),
						g);
				if (comp.isInstantiatedSynapse){
				sb.append(" SS_OUT(" + currentTop + " downto " + SPcurrentBit + 
						") <= to_slv(statevariable_" + state.type +  "_" + name + state.name + "_out_int);\r\n");
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {

				NScurrentBit = NScurrentBit + NScurrentLength + 1;
				while (NScurrentBit%8 != 0)
					NScurrentBit++;
				NScurrentLength = (state.integer) - (state.fraction);
				int currentTop = NScurrentBit + NScurrentLength;
				writeSignal("statevariable_" + state.type +  "_" + name + state.name + "_out"
						,NScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append(" NS_OUT(" + currentTop + " downto " + NScurrentBit + 
						") <= to_slv(statevariable_" + state.type +  "_" + name + state.name + "_out_int);\r\n");
				}
			}
		}
		for(Iterator<EDDerivedVariable> i = comp.derivedvariables.iterator(); i.hasNext(); ) {
			EDDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			if (comp.isSynapse){
				SPcurrentBit = SPcurrentBit + SPcurrentLength + 1;
				while (SPcurrentBit%8 != 0)
					SPcurrentBit++;
				SPcurrentLength = (state.integer) - (state.fraction);
				int currentTop = SPcurrentBit + SPcurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_" + 
						name + state.name + "_out",SPcurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
					sb.append(" SS_OUT(" + currentTop + " downto " + SPcurrentBit + 
							") <= to_slv(" + parentName + "derivedvariable_" + state.type +  "_" + 
							name + state.name + "_out_int);\r\n");
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {
				NScurrentBit = NScurrentBit + NScurrentLength + 1;
				while (NScurrentBit%8 != 0)
					NScurrentBit++;
				NScurrentLength = (state.integer) - (state.fraction);
				int currentTop = NScurrentBit + NScurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_" + 
						name + state.name + "_out",NScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
					sb.append(" NS_OUT(" + currentTop + " downto " + NScurrentBit + 
							") <= to_slv(" + parentName + "derivedvariable_" + state.type +  "_" + 
							name + state.name + "_out_int);\r\n");
				}
			}
		}
		for(Iterator<EDConditionalDerivedVariable> i = comp.conditionalderivedvariables.iterator(); i.hasNext(); ) {
			EDConditionalDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			if (comp.isSynapse){
				SPcurrentBit = SPcurrentBit + SPcurrentLength + 1;
				while (SPcurrentBit%8 != 0)
					SPcurrentBit++;
				SPcurrentLength = (state.integer) - (state.fraction);
				int currentTop = SPcurrentBit + SPcurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_" + name + state.name + "_out"
						,SPcurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append(" SS_OUT(" + currentTop + " downto " + SPcurrentBit + 
						") <= to_slv(" + parentName + "derivedvariable_" + state.type +  "_" + name + state.name + "_out_int);\r\n");
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {
				NScurrentBit = NScurrentBit + NScurrentLength + 1;
				while (NScurrentBit%8 != 0)
					NScurrentBit++;
				NScurrentLength = (state.integer) - (state.fraction);
				int currentTop = NScurrentBit + NScurrentLength;
				writeSignal("statevariable_" + state.type +  "_" + name + state.name + "_out"
						,NScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
					sb.append(" NS_OUT(" + currentTop + " downto " + NScurrentBit + 
							") <= to_slv(" + parentName + "derivedvariable_" + state.type +  "_" + name + state.name + "_out_int);\r\n");
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}

		for(Iterator<EDComponent> i = comp.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (!item.isSynapse) {
				String newName = name + item.name + "_";
				writeStateToBusSignals(item, sb, newName,parentName, initialStateValues,g);

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}
		
		boolean hasSynapses = false;
		for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (item.isSynapse) {
				hasSynapses = true;
				
			}
		}
		
		if (hasSynapses == true) {
			int j = 0;
			for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
				EDComponent item = i.next(); 
				if (item.isSynapse) {
					SPcurrentBit = -1;
					SPcurrentLength = 0;
					SPInteger = 0;
					g.writeEndArray();
					g.writeArrayFieldStart("SynapseVariablesOut_" + item.name);
					String newName = name + item.name + "_";
					

					if (item.isInstantiatedSynapse){
						if (0 == j)
							sb.append("if (to_integer(unsigned(synapseType)) = (" + j + ")) then\r\n"); 
						else
							sb.append("elsif (to_integer(unsigned(synapseType)) = (" + j + ")) then\r\n");
						j++;
						if (comp.Children.size() == j)
							sb.append("end if;\r\n");  
					}
					writeStateToBusSignals(item, sb, newName,parentName, initialStateValues, g);


					if (SScurrentBit >= SSMax)
						SSMax = SScurrentBit + 1;

					if (SPcurrentBit >= SPMax)
						SPMax = SPcurrentBit + 1;
				}
			}
		}
	}

	public static void writeStateSignals(EDComponent comp, StringBuilder sb, String name,String parentName)
	{
		if (comp.isSynapse){
			sb.append("  signal "+name+"step_once_go_int : std_logic := '0';\r\n");
			sb.append("  signal "+name+"step_once_addCurrent_int : std_logic := '0';\r\n");
			sb.append("  signal "+name+"step_once_complete_int : std_logic := '0';\r\n");		
		}
		
		for(Iterator<EDState> i = comp.state.iterator(); i.hasNext(); ) {
			EDState state = i.next(); 
			sb.append("signal statevariable_" + state.type +  "_" +  name+state.name + "_out_int : sfixed (" + 
					state.integer + " downto " + state.fraction + ");\r\n");
		}
		for(Iterator<EDDerivedVariable> i = comp.derivedvariables.iterator(); i.hasNext(); ) {
			EDDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			sb.append("signal " + parentName + "derivedvariable_" + state.type +  "_" +  name+state.name + "_out_int : sfixed (" + 
					state.integer + " downto " + state.fraction + ");\r\n");
		}
		for(Iterator<EDConditionalDerivedVariable> i = comp.conditionalderivedvariables.iterator(); i.hasNext(); ) {
			EDConditionalDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			sb.append("signal " + parentName + "derivedvariable_" + state.type +  "_" +  name+state.name + "_out_int : sfixed (" + 
					state.integer + " downto " + state.fraction + ");\r\n");
		}

		for(Iterator<EDComponent> i = comp.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			String newName = name + item.name + "_";
			writeStateSignals(item, sb, newName,parentName);
		}
	
	}
	
	static boolean useVirtualSynapses = true;
	public static void writeConnectivityMapVar(EDComponent comp, StringBuilder sb, String name,
			String parentName, Map<String,Float> initialStateValues,JsonGenerator g, boolean isSynapse) throws JsonGenerationException, IOException
	{

		if (name.length() > 0 && useVirtualSynapses && comp.isSynapse && comp.isInstantiatedSynapse)
		{
			sb.append("  "+name+"step_once_go => "+name+"step_once_go_int,\r\n");
			sb.append("  "+name+"step_once_addCurrent => "+name+"step_once_addCurrent_int,\r\n");
			sb.append("  "+name+"step_once_complete => "+name+"step_once_complete_int,\r\n");
		}
		for(Iterator<EDState> i = comp.state.iterator(); i.hasNext(); ) {
			EDState state = i.next(); 
			if (isSynapse){
				SScurrentBit = SScurrentBit + SScurrentLength + 1;
				while (SScurrentBit%8 != 0)
					SScurrentBit++;
				SScurrentLength = (state.integer) - (state.fraction);
				int currentTop = SScurrentBit + SScurrentLength;
				writeSignal("statevariable_" + state.type +  "_"+name + state.name + 
						"_in",SScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append("statevariable_" + state.type +  "_"+name + state.name + 
						"_out => statevariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append("statevariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (SS_IN(" + currentTop + " downto " + SScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}else{
				NScurrentBit = NScurrentBit + NScurrentLength + 1;
				while (NScurrentBit%8 != 0)
					NScurrentBit++;
				NScurrentLength = (state.integer) - (state.fraction);
				int currentTop = NScurrentBit + NScurrentLength;
				writeSignal("statevariable_" + state.type +  "_"+name + state.name + 
						"_in",NScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append("statevariable_" + state.type +  "_"+name + state.name + 
						"_out => statevariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append("statevariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (NS_IN(" + currentTop + " downto " + NScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}
			}
			
		}
		for(Iterator<EDDerivedVariable> i = comp.derivedvariables.iterator(); i.hasNext(); ) {
			EDDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			if (isSynapse){
				SScurrentBit = SScurrentBit + SScurrentLength + 1;
				while (SScurrentBit%8 != 0)
					SScurrentBit++;
				SScurrentLength = (state.integer) - (state.fraction);
				int currentTop = SScurrentBit + SScurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in",SScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_out => " + parentName + "derivedvariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (SS_IN(" + currentTop + " downto " + SScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {
				NScurrentBit = NScurrentBit + NScurrentLength + 1;
				while (NScurrentBit%8 != 0)
					NScurrentBit++;
				NScurrentLength = (state.integer) - (state.fraction);
				int currentTop = NScurrentBit + NScurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in",NScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append("derivedvariable_" + state.type +  "_"+name + state.name + 
						"_out => " + parentName + "derivedvariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (NS_IN(" + currentTop + " downto " + NScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}
			}
		}
		for(Iterator<EDConditionalDerivedVariable> i = comp.conditionalderivedvariables.iterator(); i.hasNext(); ) {
			EDConditionalDerivedVariable state = i.next(); 
			if (!state.ExposureIsUsed)
				continue;
			if (isSynapse){
				SScurrentBit = SScurrentBit + SScurrentLength + 1;
				while (SScurrentBit%8 != 0)
					SScurrentBit++;
				SScurrentLength = (state.integer) - (state.fraction);
				int currentTop = SScurrentBit + SScurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in",SScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_out => " + parentName + "derivedvariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (SS_IN(" + currentTop + " downto " + SScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}
				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {
				SScurrentBit = SScurrentBit + SScurrentLength + 1;
				while (SScurrentBit%8 != 0)
					SScurrentBit++;
				SScurrentLength = (state.integer) - (state.fraction);
				int currentTop = SScurrentBit + SScurrentLength;
				writeSignal(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in",SScurrentBit,currentTop,state.integer,state.fraction,
						initialStateValues.get("neuron_model_stateCURRENT_" + state.type + "_" + name + 
								state.name).toString(),g);
				if (comp.isInstantiatedSynapse){
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_out => " + parentName + "derivedvariable_" + state.type+   "_"+name + state.name + "_out_int,\r\n" );
				sb.append(parentName + "derivedvariable_" + state.type +  "_"+name + state.name + 
						"_in => to_sfixed (NS_IN(" + currentTop + " downto " + SScurrentBit + ")," + state.integer + " , " + state.fraction + "),\r\n" );
				}
			
			}
		}

		for(Iterator<EDComponent> i = comp.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (!item.isSynapse) {
				String newName = name + item.name + "_";
				writeConnectivityMapVar(item, sb, newName,parentName,initialStateValues, g,isSynapse);

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}

		boolean hasSynapses = false;
		for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (item.isSynapse) {
				hasSynapses = true;
			}
		}
		if (hasSynapses == true) {
			for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
				EDComponent item = i.next(); 
				if (item.isSynapse) {
					SScurrentBit = -1;
					SScurrentLength = 0;
					SSInteger = 0;
					g.writeEndArray();
					g.writeArrayFieldStart("SynapseVariablesIn_" + item.name);
					String newName = name + item.name + "_";
					writeConnectivityMapVar(item, sb, newName,parentName, initialStateValues, g,true);
					
					if (SScurrentBit >= SSMax)
						SSMax = SScurrentBit + 1;

					if (SPcurrentBit >= SPMax)
						SPMax = SPcurrentBit + 1;
				}
			}
		}
	
	}
	

	public static void writeConnectivityMapPar(EDComponent comp, StringBuilder sb, String name,String parentName, JsonGenerator g,boolean isSynapse) throws JsonGenerationException, IOException
	{
		for(Iterator<EDParameter> i = comp.parameters.iterator(); i.hasNext(); ) {
			EDParameter item = i.next(); 
			if (isSynapse) {
				SPcurrentBit = SPcurrentBit + SPcurrentLength + 1;
				while (SPcurrentBit%8 != 0)
					SPcurrentBit++;
				SPcurrentLength = (item.integer) - (item.fraction);
				int currentTop = SPcurrentBit + SPcurrentLength;
				writeSignal(parentName + "param_" + item.type +  "_"+name + item.name,SPcurrentBit,currentTop,item.integer,item.fraction,
						item.value,g);
				if (comp.isInstantiatedSynapse){
				sb.append(parentName + "param_" + item.type +  "_"+name + item.name + " => to_sfixed ( SP_IN(" + currentTop + " downto " + 
						SPcurrentBit + ")," + item.integer + " , " + item.fraction + "),\r\n" );
				}

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			} else {
				NPcurrentBit = NPcurrentBit + NPcurrentLength + 1;
				while (NPcurrentBit%8 != 0)
					NPcurrentBit++;
				NPcurrentLength = (item.integer) - (item.fraction);
				int currentTop = NPcurrentBit + NPcurrentLength;
				writeSignal(parentName + "param_" + item.type +  "_"+name + item.name,NPcurrentBit,currentTop,item.integer,item.fraction,item.value,g);
				sb.append(parentName + "param_" + item.type +  "_"+name + item.name + " => to_sfixed ( NP_IN(" + currentTop + " downto " + 
						NPcurrentBit + ")," + item.integer + " , " + item.fraction + "),\r\n" );

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}
		
		for(Iterator<EDComponent> i = comp.Children.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (!item.isSynapse) {
				String newName = name + item.name + "_";
				writeConnectivityMapPar(item, sb, newName,parentName, g,isSynapse);

				if (SScurrentBit >= SSMax)
					SSMax = SScurrentBit + 1;

				if (SPcurrentBit >= SPMax)
					SPMax = SPcurrentBit + 1;
			}
		}
		

		boolean hasSynapses = false;
		for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
			EDComponent item = i.next(); 
			if (item.isSynapse) {
				hasSynapses = true;
			}
		}
		if (hasSynapses == true) {
			for(Iterator<EDComponent> i = comp.VirtualChildren.iterator(); i.hasNext(); ) {
				EDComponent item = i.next(); 
				if (item.isSynapse) {
					SPcurrentBit = -1;//5119;
					SPcurrentLength = 0;
					SPInteger = 0;
					g.writeEndArray();
					g.writeArrayFieldStart("SynapseParameters_" + item.name);
					String newName = name + item.name + "_";
					writeConnectivityMapPar(item, sb, newName,parentName, g,true);
					 
					g.writeStartObject();
					g.writeStringField("name","synapseType");
					g.writeNumberField("value",item.synapseID);
					g.writeEndObject();
					

					if (SScurrentBit >= SSMax)
						SSMax = SScurrentBit + 1;

					if (SPcurrentBit >= SPMax)
						SPMax = SPcurrentBit + 1;
				}
			}
		}
	
	}
	
}