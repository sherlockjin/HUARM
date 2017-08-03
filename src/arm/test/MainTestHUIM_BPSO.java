package arm.test;

import arm.algorithm.AlgoHUIM_BPSO;
import arm.algorithm.AlgoHUIM_BPSOGA;
import arm.algorithm.Common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static arm.algorithm.Common.fileToPath;


/**
 * Example of how to use the HUIM-BPSO algorithm 
 * from the source code.
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger, 2016
 */
public class MainTestHUIM_BPSO {

	public static void main(String [] arg) throws IOException{
		
		String output = ".//BPSOoutput.txt";

        AlgoHUIM_BPSO algo = new AlgoHUIM_BPSO();
		algo.runAlgorithm(Common.fileToPath(), output,Common.min_utility_thres);

		String outputga = ".//BPSOGAoutput.txt";

		AlgoHUIM_BPSOGA algoga = new AlgoHUIM_BPSOGA();
		algoga.runAlgorithm(Common.fileToPath(), outputga,Common.min_utility_thres);
	}


}
