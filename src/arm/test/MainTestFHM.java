package arm.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import arm.algorithm.AlgoFHM;
import arm.algorithm.Common;

import static arm.algorithm.Common.fileToPath;


/**
 * Example of how to use the FHM algorithm 
 * from the source code.
 * @author Philippe Fournier-Viger, 2014
 */
public class MainTestFHM {

	public static void main(String [] arg) throws IOException{

		String output = ".//FHMoutput.txt";
		//String input = fileToPath("contextHUIM.txt");
		//String input = fileToPath("chess_utility.txt");
		//int min_utility = 300000;  //

		int min_utility = 301331;  //


		// Applying the HUIMiner algorithm
		AlgoFHM fhm = new AlgoFHM();

		fhm.runAlgorithm(Common.fileToPath(), output, min_utility);
		fhm.printStats();

	}


}
