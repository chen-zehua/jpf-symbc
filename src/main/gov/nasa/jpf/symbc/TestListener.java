package gov.nasa.jpf.symbc;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.util.Left;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;

public class TestListener extends PropertyListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo currentThread, 
			Instruction nextInstruction, Instruction executedInstruction) {
		String src = executedInstruction.getSourceLine();
		if (src != null) {
			System.out.println("-------------------bytecode executed-------------------");
			System.out.printf("%s : %s : %s\n",executedInstruction.getSourceLine().trim(), 
					executedInstruction, executedInstruction.getSourceLocation());
			System.out.println("---------------------------------------------------------");
		}
	}
	
	@Override
	public void stateProcessed(Search search) {
		System.out.println("============== State Processed =================");
		ChoiceGenerator<?> curCG = search.getVM().getSystemState().getChoiceGenerator();
		PCChoiceGenerator pcCG;
		if (curCG instanceof PCChoiceGenerator) {
			pcCG = (PCChoiceGenerator) curCG;		
		} else {
			pcCG = (PCChoiceGenerator) curCG.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
		}
		PathCondition pc = null;
		if (pcCG != null) {
			pc = pcCG.getCurrentPC();
		}
		if (pc != null) {
			Constraint constraint = pc.header;
			Transition trail;
			while (constraint != null ) {
				System.out.println("==================Constraint====================");
				System.out.println(constraint);
				System.out.println("==================Transition====================");
				trail = constraint.getTrail();
				if (trail != null) {
					printTrail(trail);					
				}
				constraint = constraint.and;
			}			
		}
	}
	
	public void printStateTrail(SystemState ss){
		//get last transition
		Transition trail = ss.getTrail();

		String lastLine = null;

		//mark a transition started
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		//iteratively process each step in the transition
		for (Step s : trail) 
		{
			String line = s.getLineString();
			//if this step has a corresponding SUT source line
			if (line != null) 
			{
				//if this step's corresponding SUT source line is the same as last one
				if (!line.equals(lastLine)) 
				{
					System.out.print("  ");
					System.out.print(Left.format(s.getLocationString(), 30));
					System.out.print(" : ");
					System.out.println(line.trim());
					lastLine = line;
				}
			}

		}
	}
	
	public static void printTrail(Transition trail){
		
		String lastLine = null;

		//mark a transition started
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		//iteratively process each step in the transition
		for (Step s : trail) 
		{
			String line = s.getLineString();
			//if this step has a corresponding SUT source line
			if (line != null) 
			{
				//if this step's corresponding SUT source line is the same as last one
				if (!line.equals(lastLine)) 
				{
					System.out.print("  ");
					System.out.print(Left.format(s.getLocationString(), 30));
					System.out.print(" : ");
					System.out.println(line.trim());
					lastLine = line;
				}
			}

		}
	}
}
