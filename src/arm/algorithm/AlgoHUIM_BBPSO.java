package arm.algorithm;

import arm.entity.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static arm.algorithm.Common.*;


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

public class AlgoHUIM_BBPSO {
	// variable for statistics
	double maxMemory = 0; // the maximum memory usage
	long totalTime = 0; //the total time
	long genPopTime = 0; // the time of generate population
	long updateTime = 0;
	long velTime = 0;
	long particleTime = 0;

	//final double w = 1, c1 = 0.6, c2 = 0.4;//the parameter used in BPSO algorithm

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
	public AlgoHUIM_BBPSO() {
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
//			long updateStartTime = System.currentTimeMillis();

			//direction表示搜索的方向
			for(int direction = 0; direction < 1; direction++) {

				population.clear();
				pBest.clear();
//				generatePop(minUtility);
//				saveParticle(population,pBest,gBest);
				inintPop();
				gBestList.add(gBest.getFitness());
				pBestList.add(Common.calAvg(pBest));
				pBestSim.add(Common.calSim(pBest));
				popSim.add(Common.calSim(population));
				numOfHUI.add(huiSets.size());
				for (int i = 0; i < Common.iterations; i++) {
					// update population and HUIset
					update(minUtility, i);
					float pbestSim = Common.calSim(pBest);
					float pbestAvg = Common.calAvg(pBest);

					gBestList.add(gBest.getFitness());
					pBestList.add(pbestAvg);
					pBestSim.add(pbestSim);
					popSim.add(Common.calSim(population));
					numOfHUI.add(huiSets.size());

				}
				System.out.println(direction + "-update end. HUIs No. is "
						+ huiSets.size());
                gBests.add(gBest);


			}
//			long updateEndTime = System.currentTimeMillis();
//			updateTime = updateEndTime -  updateStartTime;
			
		}

		writer = new BufferedWriter(new FileWriter(output));
		gBestWriter = new BufferedWriter(new FileWriter(".//GBest"+output.substring(3)));
		//pBestWriter = new BufferedWriter(new FileWriter(".//PBest"+output.substring(3)));
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
		Common.printStatsOFBPSO(Common.pop_size,Common.iterations,gBest.getFitness(),totalTime,
				genPopTime,updateTime,velTime,particleTime,maxMemory,huiSets.size());
	}
	private void inintPop(){
		gBest.copyParticle(cgbest);
		for(int i = 0; i < pop_size; i++){
			Particle particleForPop = new Particle();
			particleForPop.copyParticle(cpop.get(i));
			population.add(i,particleForPop);

			Particle particleForPbest = new Particle();
			particleForPbest.copyParticle(cpBest.get(i));
			pBest.add(i,particleForPbest);

			List<Double> tempV = new ArrayList<Double>();
			for (int j = 0; j < cgbest.getX().size(); j++) {
				tempV.add(j, cV.get(i).get(j));
			}
			V.add(i, tempV);

		}

	}
	/**
	 * This is the method to initial population
	 *
	 * @param minUtility
	 *            minimum utility threshold
	 */
	private void generatePop(int minUtility)//
	{
		long genPopStartTimestamp = System.currentTimeMillis();
		int i, j, k, temp;
		// initial percentage according to the twu value of 1-HTWUIs
		percentage = Common.roulettePercent(twuPattern,mapItemToTWU);
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
				temp = Common.rouletteSelect(percentage);
				if (particleForPop.getX().get(temp) == 0) {
					j++;
					particleForPop.getX().set(temp, 1);
				}

			}
			// calculate the fitness of each particle
			fitCalculate(particleForPop,database,twuPattern,gBests);
			// insert particle into population
			population.add(i, particleForPop);
			// initial pBest
			particleForPbest.copyParticle(particleForPop);
			pBest.add(i, particleForPbest);
			// update huiSets
			if (minUtility != 0 && population.get(i).getUtility() >= minUtility) {
				insert(population.get(i));
			}
			// update gBest
			if (i == 0) {
				gBest.copyParticle(pBest.get(i));
				
			} else {
				if (pBest.get(i).getFitness() > gBest.getFitness()) {
					gBest.copyParticle(pBest.get(i));
				}
			}
			// update velocity
			List<Double> tempV = new ArrayList<Double>();
			for (j = 0; j < twuPattern.size(); j++) {
				tempV.add(j, Math.random());
			}
			V.add(i, tempV);
		}
		
		long genPopEndTimestamp = System.currentTimeMillis();
		genPopTime = genPopEndTimestamp - genPopStartTimestamp;
	}

	/**
	 * Methos to update particle, velocity, pBest and gBest
	 *
	 * @param minUtility
	 */
	private void update(int minUtility, int iter) {
		int i, j, k;
		double r1, r2, temp1, temp2;
		double c1 = 0.4 ,c2 = 0.6;
		for (i = 0; i < Common.pop_size; i++) {
			k = 0;// record the count of 1 in particle
			r1 = Math.random();
			r2 = Math.random();
            c2 = 0.2+0.6*iter/iterations;
            c1 = 1 - c2;
			long velStartTime = System.currentTimeMillis();
			// update velocity
			for (j = 0; j < twuPattern.size(); j++) {
				double temp = V.get(i).get(j) + r1*1
						* (pBest.get(i).getX().get(j) - population.get(i).getX().get(j))
						+ r2 *1* (gBest.getX().get(j) - population.get(i).getX().get(j));
				V.get(i).set(j, temp);
				if (V.get(i).get(j) < -4.0)
					V.get(i).set(j, -4.0);
				else if (V.get(i).get(j) > 4.0)
					V.get(i).set(j, 4.0);
			}
			long velEndTime = System.currentTimeMillis();
			velTime += velEndTime - velStartTime;
			long parStartTime = System.currentTimeMillis();

			// update particle
			for (j = 0; j < twuPattern.size(); j++) {
				temp1 = Math.random();
				temp2 = 1 / (1.0 + Math.exp(-V.get(i).get(j)));
				if (temp1 < temp2) {
					population.get(i).getX().set(j, 1);
					k++;
				} else {
					population.get(i).getX().set(j, 0);
				}
			}
			long parEndTime = System.currentTimeMillis();
			particleTime += parEndTime - parStartTime;

			//BPSO 没有交叉
			population.get(i).setNumOfOne(k);

			//GABPSO2 始终进行交叉
//			crossover(i, pBest.get(i));
//			crossover(i, gBest);

			//GABPSO4 按一定概率进行交叉

//			if(r1 > c2){
//				crossover(i, pBest.get(i));
//			}
//			if(r2 < c2){
//				crossover(i, gBest);
//			}
//			if( r1 <= c2 && r2 >= c2) {
//				population.get(i).setNumOfOne(k);
//			}

			// calculate fitness
			fitCalculate(population.get(i),database,twuPattern, gBests);
			if (minUtility != 0 && population.get(i).getUtility() >= minUtility){
				insert(population.get(i));
			}
			// update pBest & gBest
			if (population.get(i).getFitness() > pBest.get(i).getFitness()) {
				pBest.get(i).copyParticle(population.get(i));
				if (pBest.get(i).getFitness() > gBest.getFitness()) {
					gBest.copyParticle(pBest.get(i));
				}
			}
			// update huiSets
			//if (population.get(i).fitness >= minUtility) {
			//insert(population.get(i));
			//}
		}
	}
	/**
	 * Method to crossover population[temp1] and temp2
	 * @param temp1
	 *            the number of particle to crossover
	 * @param temp2
	 *            the pbest particle or the gbest particle
	 *
	 */
	private void crossover(int temp1, Particle temp2) {
		int i = 0;
		int tempA = 0, tempB = 0;// record the number of 1 in chromosomes

		Particle temp1Particle = new Particle();
		Particle temp2Particle = new Particle();
		int position = (int) (Math.random() * twuPattern.size());// this is the
		// position
		// to
		// crossover

		/*temp1Particle.X = temp1.X.subList(0, position);
		temp1Particle.X.addAll(temp2.X.subList(position,twuPattern.size()));

		temp2Particle.X = temp2.X.subList(0, position);
		temp2Particle.X.addAll(temp1.X.subList(position,twuPattern.size()));*/

		for (i = 0; i < twuPattern.size(); i++) {// i<=position, crossover
			if (i <= position) {
				Integer temp = temp2.getX().get(i);
				temp1Particle.getX().add(temp);
				if (temp.equals(1)){
					tempA++;
				}
				temp = population.get(temp1).getX().get(i);
				temp2Particle.getX().add(temp);
				if (temp.equals(1)){
					tempB++;
				}
			} else {// i>position, not crossover
				Integer temp = population.get(temp1).getX().get(i);
				temp1Particle.getX().add(temp);
				if (temp.equals(1)){
					tempA++;
				}
				temp = temp2.getX().get(i);
				temp2Particle.getX().add(temp);
				if (temp.equals(1)){
					tempB++;
				}
			}
		}
		// get the particle after crossover
		temp1Particle.setNumOfOne(tempA);
		//temp1Particle.fitness = fitCalculate(temp1Particle);
		population.get(temp1).copyParticle(temp1Particle);



		//temp2Particle.setNumOfOne(tempB);
		//temp2Particle.fitness = fitCalculate(temp2Particle);


		/*if(temp1Particle.fitness > temp2Particle.fitness){
			population.get(temp1).setX(temp1Particle.getX());
			population.get(temp1).setFitness(temp1Particle.getFitness());
			population.get(temp1).setNumOfOne(tempA);
		}else{
			population.get(temp1).setX(temp2Particle.getX());
			population.get(temp1).setFitness(temp2Particle.getFitness());
			population.get(temp1).setNumOfOne(tempB);
		}*/
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
		//huiSets.add(new HUI(temp.toString(), tempParticle.getFitness()));
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
