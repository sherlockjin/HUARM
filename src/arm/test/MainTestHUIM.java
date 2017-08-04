package arm.test;

import arm.algorithm.*;

import java.io.IOException;


/**
 * Example of how to use the HUIM-BPSO algorithm 
 * from the source code.
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger, 2016
 */
public class MainTestHUIM {

	public static void main(String [] arg) throws IOException{

		String dbpsooutput = ".//DBPSOoutput.txt";
		AlgoHUIM_DBPSO algoHUIM_dbpso = new AlgoHUIM_DBPSO();
		algoHUIM_dbpso.runAlgorithm(Common.fileToPath(), dbpsooutput,Common.min_utility_thres);

		String bbpsooutput = ".//BBPSOoutput.txt";
		AlgoHUIM_BBPSO algoHUIM_bbpso = new AlgoHUIM_BBPSO();
		algoHUIM_bbpso.runAlgorithm(Common.fileToPath(), bbpsooutput,Common.min_utility_thres);

		String gabpsooutput = ".//GABPSOoutput.txt";
		AlgoHUIM_GABPSO algoHUIM_gabpso = new AlgoHUIM_GABPSO();
		algoHUIM_gabpso.runAlgorithm(Common.fileToPath(), gabpsooutput,Common.min_utility_thres);
	}


}
