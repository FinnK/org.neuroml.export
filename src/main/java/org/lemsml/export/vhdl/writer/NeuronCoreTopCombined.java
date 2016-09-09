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

public class NeuronCoreTopCombined {

	
	public static void writeNeuronCoreTopCombined(StringBuilder sb
			, int numberOfNeurons, int NP_WORDSWIDTH, int NS_WORDSWIDTH, 
			int SP_WORDSWIDTH, int SS_WORDSWIDTH) throws JsonGenerationException, IOException
	{
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
		
		sb.append("\r\n\r\n" + 
				"entity neuroncore_combined is\r\n" + 
				"generic (\r\n" + 
				"		NP_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		SP_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		NS_WORDSWIDTH	: integer	:= 1;\r\n" + 
				"		SS_WORDSWIDTH	: integer	:= 1\r\n" + 
				"		);\r\n" + 
				"Port (    clk : in STD_LOGIC; --SYSTEM CLOCK, THIS ITSELF DOES NOT SIGNIFY TIME STEPS - AKA A SINGLE TIMESTEP MAY TAKE MANY CLOCK CYCLES\r\n" + 
				"          rst : in STD_LOGIC; \r\n" + 
				"          control_reset : in STD_LOGIC; \r\n" + 
				"          sendSpike : out STD_LOGIC; \r\n" + 
				"          receiveSpike : in STD_LOGIC; \r\n" + 
				"          step_once_clearCurrent : in STD_LOGIC;\r\n" + 
				"          runNeuronStep : in STD_LOGIC; --runStep\r\n" + 
				"          runSynapseStep : in STD_LOGIC; --\r\n" + 
				"          busy : out STD_LOGIC; \r\n" + 
				"          busy_synapse : out STD_LOGIC; -- \r\n" + 
				"          timeStep : in std_logic_vector(31 downto 0);\r\n" + 
				"          timeStamp : in std_logic_vector(31 downto 0);\r\n" + 
				"          currentTime : in std_logic_vector(31 downto 0);\r\n" + 
				"          synapseType : in std_logic_vector(7 downto 0); --\r\n" + 
				"          neuronType : in std_logic_vector(7 downto 0); --\r\n" + 
				"          NP_IN : in std_logic_vector(((NP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"          SP_IN : in std_logic_vector(((SP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"          NS_IN : in std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"          SS_IN : in std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"          NS_OUT : out std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"          SS_OUT : out std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0)\r\n" + 
				");\r\n" + 
				"end  neuroncore_combined;\r\n" + 
				"architecture arch_imp of neuroncore_combined is\r\n");
		

		for (int i = 0; i < numberOfNeurons; i ++)
		{
			sb.append("\r\n" + 
					"component neuroncore_top" + i + " is\r\n" + 
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
					"  step_once_clearCurrent : in STD_LOGIC;\r\n" + 
					"  busy : out STD_LOGIC; \r\n" + 
					"  busy_synapse : out STD_LOGIC; \r\n" + 
					"  timeStep : in std_logic_vector(31 downto 0);\r\n" + 
					"  timeStamp : in std_logic_vector(31 downto 0);\r\n" + 
					"  currentTime : in std_logic_vector(31 downto 0);\r\n" + 
					"  synapseType : in std_logic_vector(7 downto 0);\r\n" + 
					"  NP_IN : in std_logic_vector(((NP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
					"  SP_IN : in std_logic_vector(((SP_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
					"  NS_IN : in std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
					"  SS_IN : in std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
					"  NS_OUT : out std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
					"  SS_OUT : out std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0)\r\n" + 
					");end component;\r\n");	
		
		}
		sb.append("\r\n" + 
				"signal clockcount : integer range 0 to 3;\r\n" + 
				"signal clockb : std_logic := '0';\r\n" + 
				"signal step_once_clearCurrentb : std_logic := '0';\r\n" + 
				"signal runNeuronStepb : std_logic := '0';\r\n" + 
				"signal runSynapseStepb : std_logic := '0';");
		for (int i = 0; i < numberOfNeurons; i ++)
		{
		sb.append("\r\n" + 
				"signal sendSpike" + i + " : std_logic;\r\n" + 
				"signal runNeuronStep" + i + " : std_logic;\r\n" + 
				"signal runSynapseStep" + i + " : std_logic;\r\n" + 
				"signal busy" + i + " : std_logic;\r\n" + 
				"signal busy_synapse" + i + " : std_logic;\r\n" + 
				"signal  NS_OUT" + i + " : std_logic_vector(((NS_WORDSWIDTH)*32)-1 downto 0);\r\n" + 
				"signal  SS_OUT" + i + " : std_logic_vector(((SS_WORDSWIDTH)*32)-1 downto 0);\r\n");
		}
		
		sb.append("\r\n" + 
				"begin\r\n" + 
				"\r\n" + 
				"process(clk,rst)\r\n" + 
				"begin\r\n" + 
				"if RST = '1' then\r\n" + 
				"	clockcount<= 0;\r\n" + 
				"	clockb <= '0';\r\n" + 
				"elsif rising_edge(CLK) then \r\n" + 
				"	clockcount <= clockcount + 1;\r\n" + 
				"	if clockcount = 3 then\r\n" + 
				"	   clockb <= not clockb;\r\n" + 
				"       clockcount<= 0;\r\n" + 
				"	 end if;\r\n" + 
				"end if;\r\n" + 
				"end process;\r\n" + 
				"\r\n" + 
				"process(runNeuronStep,clockb)\r\n" + 
				"begin\r\n" + 
				"  runNeuronStepb <= runNeuronStep;\r\n" + 
				"--if runNeuronStep = '1' then\r\n" + 
				"--    runNeuronStepb <= '1';\r\n" + 
				"--elsif rising_edge(clockb) then\r\n" + 
				"--    runNeuronStepb <= '0';\r\n" + 
				"--end if;\r\n" + 
				"end process;\r\n" + 
				"\r\n" + 
				"process(step_once_clearCurrent,clockb)\r\n" + 
				"begin\r\n" + 
				"if step_once_clearCurrent = '1' then\r\n" + 
				"    step_once_clearCurrentb <= '1';\r\n" + 
				"elsif rising_edge(clockb) then\r\n" + 
				"    step_once_clearCurrentb <= '0';\r\n" + 
				"end if;\r\n" + 
				"end process;\r\n" + 
				"\r\n" + 
				"process(runsynapsestep,clockb)\r\n" + 
				"begin\r\n" + 
				"  runsynapsestepb <= runsynapsestep;\r\n" + 
				"--if runsynapsestep = '1' then\r\n" + 
				"--    runsynapsestepb <= '1';\r\n" + 
				"--elsif rising_edge(clockb) then\r\n" + 
				"--    runsynapsestepb <= '0';\r\n" + 
				"--end if;\r\n" + 
				"end process;");
		
		for (int i = 0; i < numberOfNeurons; i ++)
		{
			sb.append("\r\n" + 
					"neuroncore_top" + i + "_inst : neuroncore_top" + i + " \r\n" + 
					"generic map (\r\n" + 
					"		NP_WORDSWIDTH	=> NP_WORDSWIDTH,\r\n" + 
					"		SP_WORDSWIDTH	=> SP_WORDSWIDTH,\r\n" + 
					"		NS_WORDSWIDTH	=> NS_WORDSWIDTH,\r\n" + 
					"		SS_WORDSWIDTH	=> SS_WORDSWIDTH\r\n" + 
					"		)\r\n" + 
					"Port map( \r\n" + 
					"  clk => clockb,\r\n" + 
					"  rst => rst or control_reset,\r\n" + 
					"  sendSpike => sendSpike" + i + ",\r\n" + 
					"  receiveSpike => receiveSpike,\r\n" + 
					"  runNeuronStep => runNeuronStep" + i + ",\r\n" + 
					"  runSynapseStep => runSynapseStep" + i + ",\r\n" + 
					"  step_once_clearCurrent => step_once_clearCurrentb,\r\n" + 
					"  busy => busy" + i + ",\r\n" + 
					"  busy_synapse => busy_synapse" + i + ",\r\n" + 
					"  timeStep => timeStep,\r\n" + 
					"  timeStamp => timeStamp,\r\n" + 
					"  currentTime => currentTime,\r\n" + 
					"  synapseType => synapseType,\r\n" + 
					"  NP_IN =>NP_IN,\r\n" + 
					"  SP_IN =>SP_IN,\r\n" + 
					"  NS_IN =>NS_IN,\r\n" + 
					"  SS_IN =>SS_IN,\r\n" + 
					"  NS_OUT =>NS_OUT" + i + ",\r\n" + 
					"  SS_OUT =>SS_OUT" + i + "\r\n" + 
					"); ");
			
		}
		
		sb.append("\r\n" + 
				"process(neuronType,runNeuronStepb,runSynapseStepb");
		for (int i = 0; i < numberOfNeurons; i ++)
		{
			sb.append(",sendSpike" + i + ",NS_OUT" + i + ",SS_OUT" + i + ",busy" + i + ",busy_synapse" + i + "");
		}
		sb.append(")\r\n" + 
				"begin\r\n" + 
				"busy <= '0';\r\n" + 
				"busy_synapse <= '0';\r\n" + 
				"sendSpike <= '0';\r\n" + 
				"NS_OUT <= (others => '0');\r\n" + 
				"SS_OUT <= (others => '0');");
		for (int i = 0; i < numberOfNeurons; i ++)
		{
			sb.append("runNeuronStep" + i + " <= '0';\r\n" + 
					"runSynapseStep" + i + " <= '0';");
		}
		for (int i = 0; i < numberOfNeurons; i ++)
		{
			if (i > 0)
				sb.append("els");
			
			sb.append("if (to_integer(unsigned(neuronType)) = (" + i + ")) then\r\n" + 
					"sendSpike <= sendSpike" + i + ";\r\n" + 
					"NS_OUT <= NS_OUT" + i + ";\r\n" + 
					"SS_OUT <= SS_OUT" + i + ";\r\n" + 
					"runNeuronStep" + i + " <= runNeuronStepb;\r\n" + 
					"runSynapseStep" + i + " <= runSynapseStepb;\r\n" + 
					"busy <= busy" + i + ";\r\n" + 
					"busy_synapse <= busy_synapse" + i + ";");
			
		}
		sb.append("\r\n" + 
				"\r\n" + 
				"end if;\r\n" + 
				"\r\n" + 
				"end process;\r\n" + 
				"\r\n" + 
				"end arch_imp;");
		
		sb.append("\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"--Package declaration for the above program\r\n" + 
				"library IEEE;\r\n" + 
				"use IEEE.std_logic_1164.all;\r\n" + 
				"use IEEE.std_logic_arith.all;\r\n" + 
				"\r\n" + 
				"package test_pkg is\r\n" + 
				"\r\n" + 
				"constant NP_WORDSWIDTH : integer := " + NP_WORDSWIDTH + ";\r\n" + 
				"constant NS_WORDSWIDTH : integer := " + NS_WORDSWIDTH + ";\r\n" + 
				"constant SP_WORDSWIDTH : integer := " + SP_WORDSWIDTH + ";\r\n" + 
				"constant SS_WORDSWIDTH : integer := " + SS_WORDSWIDTH + ";\r\n" + 
				"constant NP_WORDSWIDTH_PRACTICAL : integer := NP_WORDSWIDTH + 1;\r\n" + 
				"constant NS_WORDSWIDTH_PRACTICAL : integer := NS_WORDSWIDTH + 2;\r\n" + 
				"constant SP_WORDSWIDTH_PRACTICAL : integer := SP_WORDSWIDTH + 3;\r\n" + 
				"constant SS_WORDSWIDTH_PRACTICAL : integer := SS_WORDSWIDTH + 4;\r\n" + 
				"\r\n" + 
				"end test_pkg;   --end of package.\r\n" + 
				"\r\n" + 
				"package body test_pkg is  --start of package body\r\n" + 
				"\r\n" + 
				"end test_pkg;  --end of the package body");
		
	}
	
}