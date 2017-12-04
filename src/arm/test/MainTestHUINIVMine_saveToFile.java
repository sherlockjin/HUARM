package arm.test;

import arm.algorithm.AlgoHUINIVMine;
import arm.algorithm.ItemsetsTP;
import arm.algorithm.UtilityTransactionDatabaseTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;



/**
 * Example of how to use the HUINIVMine Algorithm in source code.
 * @author Philippe Fournier-Viger, 2010
 */
public class MainTestHUINIVMine_saveToFile {

	public static void main(String [] arg) throws IOException{
		
//		String input = fileToPath("DB_NegativeUtility.txt");
		String input = fileToPath("chess_negative.txt");
		String output = ".//HUINIVMineoutput.txt";

		int min_utility = 169000;  //

		// Loading the database into memory
		UtilityTransactionDatabaseTP database = new UtilityTransactionDatabaseTP();
		database.loadFile(input);
		
		// Applying the Two-Phase algorithm
		AlgoHUINIVMine algo = new AlgoHUINIVMine();
		ItemsetsTP highUtilityItemsets = algo.runAlgorithm(database, min_utility);
		
		highUtilityItemsets.saveResultsToFile(output, database.getTransactions().size());

		algo.printStats();

	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestHUINIVMine_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
