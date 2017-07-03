package arm.entity;

import java.io.*;
import java.util.*;


public class TWU {

	public int totalTransactionUtility = 0;
	public Map<Integer, Integer> mapItemToTWU;

	public int getTotalTransactionUtility() {
		return totalTransactionUtility;
	}

	public void setTotalTransactionUtility(int totalTransactionUtility) {
		this.totalTransactionUtility = totalTransactionUtility;
	}

	public Map<Integer, Integer> getMapItemToTWU() {
		return mapItemToTWU;
	}

	public void setMapItemToTWU(Map<Integer, Integer> mapItemToTWU) {
		this.mapItemToTWU = mapItemToTWU;
	}

	/**
	 * Default constructor
	 */
	public TWU() {
	}

	public void calItemToTwu(String input)
			throws IOException {

		// We create a map to store the TWU of each item
		mapItemToTWU = new HashMap<Integer, Integer>();

		// We scan the database a first time to calculate the TWU of each item.
		BufferedReader myInput = null;
		String thisLine;
		try {
			// prepare the object for reading the file
			myInput = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(input))));
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is a comment, is empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}

				// split the transaction according to the : separator
				String split[] = thisLine.split(":");
				// the first part is the list of items
				String items[] = split[0].split(" ");
				// the second part is the transaction utility
				int transactionUtility = Integer.parseInt(split[1]);
				totalTransactionUtility += transactionUtility;
				
				// for each item, we add the transaction utility to its TWU
				for (int i = 0; i < items.length; i++) {
					// convert item to integer
					Integer item = Integer.parseInt(items[i]);
					// get the current TWU of that item
					Integer twu = mapItemToTWU.get(item);
					// add the utility of the item in the current transaction to
					// its twu
					twu = (twu == null) ? transactionUtility : twu
							+ transactionUtility;
					mapItemToTWU.put(item, twu);
				}
			}
		} catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
	}



}
