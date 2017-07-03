package arm.test;

import arm.algorithm.AlgoHUIM_Random;
import arm.algorithm.Common;

import java.io.IOException;



/**
 * Example of how to use the HUIM-BPSO algorithm 
 * from the source code.
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger, 2016
 */
public class MainTestHUIM_RAND {

	public static void main(String [] arg) throws IOException{
		
		String output = ".//RANDoutput.txt";

        AlgoHUIM_Random algo = new AlgoHUIM_Random();
		algo.runAlgorithm(Common.fileToPath(), output, Common.min_utility_thres);

	}


}
