package arm.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import arm.algorithm.*;

import static arm.algorithm.Common.fileToPath;


/**
 * Example of how to use the HUIM-BPSO algorithm 
 * from the source code.
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger, 2016
 */
public class MainTestHUIM_BPSOGA {

	public static void main(String [] arg) throws IOException{
		
		String output = ".//BPSOGAoutput.txt";

        AlgoHUIM_BPSOGA algo = new AlgoHUIM_BPSOGA();
		algo.runAlgorithm(Common.fileToPath(), output,Common.min_utility_thres);

	}



}
