package arm.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import arm.algorithm.AlgoHUIM_BPSO_tree;
import arm.algorithm.Common;

import static arm.algorithm.Common.fileToPath;


/**
 * Example of how to use the HUIM-BPSO-tree algorithm 
 * from the source code.
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger, 2016
 */
public class MainTestHUIM_BPSO_tree {

	public static void main(String [] arg) throws IOException{
		

		
		String output = ".//output.txt";
		int min_utility = 40;  // 
		
		// Applying the huim_bpso_tree algorithm
		AlgoHUIM_BPSO_tree huim_bpso_tree = new AlgoHUIM_BPSO_tree();
		huim_bpso_tree.runAlgorithm(Common.fileToPath(), output, min_utility);
		huim_bpso_tree.printStats();

	}

}
