package arm.algorithm;

import arm.entity.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static arm.algorithm.Common.checkMemory;
import static arm.algorithm.Common.fitCalculate;


/**
 * * * * This is an implementation of the high utility itemset mining algorithm
 * based on Binary Particle Swarm Optimization Algorithm.
 * 
 * Copyright (c) 2016 Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE *
 * (http://www.philippe-fournier-viger.com/spmf).
 * 
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the *
 * terms of the GNU General Public License as published by the Free Software *
 * Foundation, either version 3 of the License, or (at your option) any later *
 * version. *
 * 
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * *
 * 
 * You should have received a copy of the GNU General Public License along with
 * * SPMF. If not, see .
 * 
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 */

public class AlgoHUIM_Random {
	// variable for statistics
	double maxMemory = 0; // the maximum memory usage
	long totalTime = 0; //the total time
	long genPopTime = 0; // the time of generate population
	long updateTime = 0;
	long mutationTime = 0; // the time of mutation
	long crossPBestTime = 0; // the time of crossover with pbest
	long crossGBestTime = 0; //the time of crossover with gbest

	final double w = 1, c1 = 0.6, c2 = 0.4;//the parameter used in BPSO algorithm

	// Create a list to store database
	List<List<Pair>> database = new ArrayList<List<Pair>>();
	Map<Integer, Integer> mapItemToTWU;
	List<Integer> twuPattern;// the items which has twu value more than minUtil

	BufferedWriter writer = null; // writer to write the output file
	BufferedWriter gBestWriter = null;
	BufferedWriter pBestWriter = null;
	// this class represent an item and its utility in a transaction

	List<Float> gBestList =  new ArrayList<Float>(); //store gBest's fitness every iteration
	List<Float> pBestList =  new ArrayList<Float>(); //store pBest's average fitness every iteration
	List<Float> pBestSim =  new ArrayList<Float>();
	List<Float> popSim =  new ArrayList<Float>();
	List<Integer> numOfHUI =  new ArrayList<Integer>(); //store HUI's num every iteration
	Particle gBest = new Particle();// the gBest particle in populations
	List<Particle> gBests = new ArrayList<Particle>();
	List<Particle> pBest = new ArrayList<Particle>();// each pBest particle in	populations,
	List<Particle> population = new ArrayList<Particle>();// populations
	List<List<Double>> V = new ArrayList<List<Double>>();// the velocity of each
	List<HUI> huiSets = new ArrayList<HUI>();// the set of HUIs

	List<Double> percentage = new ArrayList<Double>();// the portation of twu
	// value of each
	// 1-HTWUIs in sum of
	// twu value


	/**
	 * Default constructor
	 */
	public AlgoHUIM_Random() {
	}

	/**
	 * Run the algorithm
	 * 
	 * @param input
	 *            the input file path
	 * @param output
	 *            the output file path
	 * @param minUtilityThres
	 *            the minimum utility threshold
	 * @throws IOException
	 *             exception if error while writing the file
	 */
	public void runAlgorithm(String input, String output, double minUtilityThres)
			throws IOException {
		long startTimestamp = System.currentTimeMillis();

		//calculate the TWU of each item
		TWU twu = new TWU();
		twu.calItemToTwu(input);
		mapItemToTWU = twu.getMapItemToTWU();

		int minUtility = (int)(twu.getTotalTransactionUtility()*minUtilityThres);
		System.out.println(" TotalTransactionUtility ~ " + twu.getTotalTransactionUtility());
		System.out.println(" minUtility ~ " + minUtility);
		// revise the database
		DatabaseRevised databaseRevised = new DatabaseRevised();
		databaseRevised.reviseData(input,mapItemToTWU,minUtility);
		database = databaseRevised.getDatabase();
		twuPattern = databaseRevised.getTwuPattern();

		// check the memory usage
		maxMemory = checkMemory(maxMemory);

		// Mine the database recursively
		if (twuPattern.size() > 0) {
			// initial population
			long genPopStartTimestamp = System.currentTimeMillis();
			generatePop(minUtility);
			long genPopEndTimestamp = System.currentTimeMillis();
			genPopTime = genPopEndTimestamp - genPopStartTimestamp;

			gBestList.add(gBest.getFitness());
			pBestList.add(Common.calAvg(pBest));
			pBestSim.add(Common.calSim(pBest));
			popSim.add(Common.calSim(population));
			numOfHUI.add(huiSets.size());
			long updateStartTime = System.currentTimeMillis();
			for (int i = 0; i < Common.iterations; i++) {
				// update population and HUIset
				update(minUtility);
				gBestList.add(gBest.getFitness());
				pBestList.add(Common.calAvg(pBest));
				pBestSim.add(Common.calSim(pBest));
				popSim.add(Common.calSim(population));
				numOfHUI.add(huiSets.size());
//				System.out.println(i + "-update end. HUIs No. is "
//						+ huiSets.size());
			}
			long updateEndTime = System.currentTimeMillis();
			updateTime = updateEndTime -  updateStartTime;
			
		}
		writer = new BufferedWriter(new FileWriter(output));
		gBestWriter = new BufferedWriter(new FileWriter(".//GBest"+output.substring(3)));
		Common.writeOut(writer,huiSets);
		Common.writeGbest(gBestWriter,gBestList,pBestList,pBestSim,popSim,numOfHUI);
		// check the memory usage again and close the file.
		maxMemory = checkMemory(maxMemory);
		// close output file
		writer.close();
		gBestWriter.close();
		// record end time
		long endTimestamp = System.currentTimeMillis();
		totalTime = endTimestamp - startTimestamp;
		Common.printStatsOFRandom(Common.pop_size,Common.iterations,gBest.getFitness(),totalTime, maxMemory,huiSets.size());
	}

	/**
	 * This is the method to initial population
	 * 
	 * @param minUtility
	 *            minimum utility threshold
	 */
	private void generatePop(int minUtility)//
	{

		int i, j, k, temp;
		// initial percentage according to the twu value of 1-HTWUIs

		for (i = 0; i < Common.pop_size; i++) {
			// initial particles
			Particle particleForPop = new Particle(twuPattern.size());
			Particle particleForPbest = new Particle();
			j = 0;
			// k is the count of 1 in particle
			k = (int) (Math.random() * twuPattern.size());
			particleForPop.setNumOfOne(k);
			while (j < k) {
				// roulette select the position of 1 in population
				temp = (int) (Math.random() * twuPattern.size());
				if (particleForPop.getX().get(temp) == 0) {
					j++;
					particleForPop.getX().set(temp, 1);
				}

			}
			// calculate the fitness of each particle
			fitCalculate(particleForPop,database,twuPattern, gBests);
			// insert particle into population
			population.add(i, particleForPop);
			particleForPbest.copyParticle(particleForPop);
			pBest.add(i, particleForPbest);
			// update huiSets
			if (minUtility != 0 && population.get(i).getFitness() >= minUtility) {
				insert(population.get(i));
			}
			// update gBest
			if (i == 0) {
				gBest.copyParticle(population.get(i));

			} else {
				if (population.get(i).getFitness() > gBest.getFitness()) {
					gBest.copyParticle(population.get(i));
				}
			}
		}


	}

	/**
	 * Methos to update particle, velocity, pBest and gBest
	 *
	 */
	private void update(int minUtility) {
		int i, j, k;
		int temp;

		for (i = 0; i < Common.pop_size; i++) {
			// initial particles
			Particle particleForPop = new Particle(twuPattern.size());
			j = 0;
			// k is the count of 1 in particle
			k = (int) (Math.random() * twuPattern.size());
			particleForPop.setNumOfOne(k);
			while (j < k) {
				// roulette select the position of 1 in population
				temp = (int) (Math.random() * twuPattern.size());
				if (particleForPop.getX().get(temp) == 0) {
					j++;
					particleForPop.getX().set(temp, 1);
				}

			}
			// calculate the fitness of each particle
			fitCalculate(particleForPop,database,twuPattern, gBests);
			// insert particle into population
			population.add(i, particleForPop);
			// update huiSets
			if (minUtility != 0 && population.get(i).getFitness() >= minUtility) {
				insert(population.get(i));
			}
			// update gBest
			if (population.get(i).getFitness() > pBest.get(i).getFitness()) {
				pBest.get(i).copyParticle(population.get(i));
				if (pBest.get(i).getFitness() > gBest.getFitness()) {
					gBest.copyParticle(pBest.get(i));
				}
			}

		}
	}

	/**
	 * Method to inseret tempParticle to huiSets
	 * 
	 * @param tempParticle
	 *            the particle to be inserted
	 */
	private void insert(Particle tempParticle) {
		int i;
		StringBuilder temp = new StringBuilder();
		for (i = 0; i < twuPattern.size(); i++) {
			if (tempParticle.getX().get(i) == 1) {
				temp.append(twuPattern.get(i));
				temp.append(' ');
			}
		}
		// huiSets is null
		if (huiSets.size() == 0) {
			huiSets.add(new HUI(temp.toString(), tempParticle.getUtility()));
		} else {
			// huiSets is not null, judge whether exist an itemset in huiSets
			// same with tempParticle
			for (i = 0; i < huiSets.size(); i++) {
				if (temp.toString().equals(huiSets.get(i).getItemset())) {
					break;
				}
			}
			// if not exist same itemset in huiSets with tempParticle,insert it
			// into huiSets
			if (i == huiSets.size())
				huiSets.add(new HUI(temp.toString(), tempParticle.getUtility()));
		}
	}




}
