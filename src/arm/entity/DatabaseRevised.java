package arm.entity;

import arm.algorithm.AlgoHUIM_BPSOGA;

import java.io.*;
import java.util.*;




public class DatabaseRevised {
	// Create a list to store database
	List<List<Pair>> database = new ArrayList<List<Pair>>();
	List<Integer> twuPattern;// the items which has twu value more than minUtil

	public List<List<Pair>> getDatabase() {
		return database;
	}

	public void setDatabase(List<List<Pair>> database) {
		this.database = database;
	}

	public List<Integer> getTwuPattern() {
		return twuPattern;
	}

	public void setTwuPattern(List<Integer> twuPattern) {
		this.twuPattern = twuPattern;
	}

	/**
	 * Default constructor
	 */
	public DatabaseRevised() {
	}

	public void reviseData(String input, Map<Integer, Integer> mapItemToTWU, int minUtility)
			throws IOException {
		BufferedReader myInput = null;
		String thisLine;
		if (minUtility == 0) {
			twuPattern = new ArrayList<Integer>(mapItemToTWU.keySet());
		} else {
			twuPattern = new ArrayList<Integer>();
			for (Integer key : mapItemToTWU.keySet()) {
				Integer value = mapItemToTWU.get(key);
				if (value >= minUtility) {
					twuPattern.add(key);
				}
			}
		}
		Collections.sort(twuPattern);

		// SECOND DATABASE PASS TO CONSTRUCT THE DATABASE
		// OF 1-ITEMSETS HAVING TWU >= minutil (promising items)
		try {
			// prepare object for reading the file
			myInput = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(input))));
			// variable to count the number of transaction
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is a comment, is empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}

				// split the line according to the separator
				String split[] = thisLine.split(":");
				// get the list of items
				String items[] = split[0].split(" ");
				// get the list of utility values corresponding to each item
				// for that transaction
				String utilityValues[] = split[2].split(" ");

				// Create a list to store items and its utility
				List<Pair> revisedTransaction = new ArrayList<Pair>();

				// for each item
				for (int i = 0; i < items.length; i++) {
					// / convert values to integers
					Pair pair = new Pair();
					pair.item = Integer.parseInt(items[i]);
					pair.utility = Integer.parseInt(utilityValues[i]);

					if (minUtility == 0) {
						revisedTransaction.add(pair);
					} else {
						if (mapItemToTWU.get(pair.item) >= minUtility) {
							// add it
							revisedTransaction.add(pair);
						}
					}
				}
				// Copy the transaction into database but
				// without items with TWU < minutility
				database.add(revisedTransaction);
			}
		} catch (Exception e) {
			// to catch error while reading the input file
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}


	}




}
