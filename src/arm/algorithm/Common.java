package arm.algorithm;

import arm.entity.HUI;
import arm.entity.Pair;
import arm.entity.Particle;
import arm.test.MainTestHUIM_RAND;

import javax.naming.Context;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jinxiaole on 2017/6/29.
 */
public class Common {
    final public static int pop_size = 30;// the size of populations
    final public static int iterations = 500;// the iterations of algorithms
    //final public static String input = "DB_Utility.txt";
    //final public static String input = "mushroom_utility.txt";
    final public static String input = "chess_utility.txt";
    final public static double min_utility_thres = 0;  //
    public static String fileToPath() throws UnsupportedEncodingException{

        URL  url =  Common.class .getResource("../test/"+input);      // 获得当前类所在路径
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
    /**
     * Method to initial percentage
     *
     * @return percentage
     */
    public static List<Double> roulettePercent(List<Integer> twuPattern,Map<Integer, Integer> mapItemToTWU) {
        List<Double> percentage = new ArrayList<Double>();
        int i, sum = 0, tempSum = 0;
        double tempPercent;

        // calculate the sum of twu value of each 1-HTWUIs
        for (i = 0; i < twuPattern.size(); i++) {
            sum = sum + mapItemToTWU.get(twuPattern.get(i));
        }
        // calculate the portation of twu value of each item in sum
        for (i = 0; i < twuPattern.size(); i++) {
            tempSum = tempSum + mapItemToTWU.get(twuPattern.get(i));
            tempPercent = tempSum / (sum + 0.0);
            percentage.add(tempPercent);
        }
        return percentage;
    }

    /**
     * Method to ensure the posotion of 1 in particle use roulette selection
     *
     * @param percentage
     *            the portation of twu value of each 1-HTWUIs in sum of twu
     *            value
     * @return the position of 1
     */
    public static int rouletteSelect(List<Double> percentage) {
        int i, temp = 0;
        double randNum;
        randNum = Math.random();
        for (i = 0; i < percentage.size(); i++) {
            if (i == 0) {
                if ((randNum >= 0) && (randNum <= percentage.get(0))) {
                    temp = 0;
                    break;
                }
            } else if ((randNum > percentage.get(i - 1))
                    && (randNum <= percentage.get(i))) {
                temp = i;
                break;
            }
        }
        return temp;
    }

    /**
     * Method to calculate the fitness of each particle
     *
     * @return fitness
     */
    public static int fitCalculate(Particle particle, List<List<Pair>> database, List<Integer> twuPattern) {
        List<Integer> tempParticle = particle.getX();
        int k = particle.getNumOfOne();
        if (k == 0)
            return 0;
        int i, j, p, q, temp;

        int sum, fitness = 0;
        for (p = 0; p < database.size(); p++) {// p scan the transactions in
            // database
            i = 0;
            j = 0;
            q = 0;
            temp = 0;
            sum = 0;
            // j scan the 1 in particle, q scan each transaction, i scan each
            // particle
            while (j < k && q < database.get(p).size()
                    && i < tempParticle.size()) {
                if (tempParticle.get(i) == 1) {
                    if (database.get(p).get(q).getItem() < twuPattern.get(i))
                        q++;
                    else if (database.get(p).get(q).getItem() == twuPattern.get(i)) {
                        sum = sum + database.get(p).get(q).getUtility();
                        j++;
                        q++;
                        temp++;
                        i++;
                    } else if (database.get(p).get(q).getItem() > twuPattern.get(i)) {
                        j++;
                        i++;
                    }
                } else
                    i++;
            }
            if (temp == k) {
                fitness = fitness + sum;
            }
        }
        return fitness;
    }

    /**
     * Print statistics about the latest execution to System.out.
     */
    public static void printStatsOFBPSOGA(int pop_size,int iterations,int gbestFitness, long totalTime,long genPopTime, long updateTime, long mutationTime, long crossPBestTime,long crossGBestTime,double maxMemory, int huiSetsSize) {
        System.out
                .println("=============  HUIM-BPSOGA ALGORITHM v.1.0 - STATS =============");
        System.out
                .println(" pop_size ~ " + pop_size);
        System.out
                .println(" iterations ~ "+ iterations);
        System.out
                .println(" gbest hui ~ "+ gbestFitness);
        System.out.println(" Total time ~ " + totalTime
                + " ms");
        System.out.println(" Genpop time ~ " + genPopTime
                + " ms");
        System.out.println(" Update time ~ " + updateTime
                + " ms");
        System.out.println(" Mutation time ~ " + mutationTime
                + " ms");
        System.out.println(" Crossover with pbest time ~ " + crossPBestTime
                + " ms");
        System.out.println(" Crossover with gbest time ~ " + crossGBestTime
                + " ms");
        System.out.println(" Memory ~ " + maxMemory + " MB");
        System.out.println(" High-utility itemsets count : " + huiSetsSize);
        System.out
                .println("===================================================");
    }
    /**
     * Print statistics about the latest execution to System.out.
     */
    public static void printStatsOFBPSO(int pop_size,int iterations,int gbestFitness, long totalTime,long genPopTime, long updateTime, long velTime, long particleTime,double maxMemory, int huiSetsSize) {
        System.out
                .println("=============  HUIM-BPSO ALGORITHM v.1.0 - STATS =============");
        System.out
                .println(" pop_size ~ " + pop_size);
        System.out
                .println(" iterations ~ "+ iterations);
        System.out
                .println(" gbest hui ~ "+ gbestFitness);
        System.out.println(" Total time ~ " + totalTime
                + " ms");
        System.out.println(" Genpop time ~ " + genPopTime
                + " ms");
        System.out.println(" Update time ~ " + updateTime
                + " ms");
        System.out.println(" Vel Update time ~ " + velTime
                + " ms");
        System.out.println(" Particle update time ~ " + particleTime
                + " ms");
        System.out.println(" Memory ~ " + maxMemory + " MB");
        System.out.println(" High-utility itemsets count : " + huiSetsSize);
        System.out
                .println("===================================================");
    }

    /**
     * Print statistics about the latest execution to System.out.
     */
    public static void printStatsOFRandom(int pop_size,int iterations,int gbestFitness, long totalTime,double maxMemory, int huiSetsSize) {
        System.out
                .println("=============  HUIM-Random ALGORITHM v.1.0 - STATS =============");
        System.out
                .println(" pop_size ~ " + pop_size);
        System.out
                .println(" iterations ~ "+ iterations);
        System.out
                .println(" gbest hui ~ "+ gbestFitness);
        System.out.println(" Total time ~ " + totalTime
                + " ms");
        System.out.println(" Memory ~ " + maxMemory + " MB");
        System.out.println(" High-utility itemsets count : " + huiSetsSize);
        System.out
                .println("===================================================");
    }

    /**
     * Method to check the memory usage and keep the maximum memory usage.
     */
    public static double checkMemory(double maxMemory) {
        // get the current memory usage
        double currentMemory = (Runtime.getRuntime().totalMemory() - Runtime
                .getRuntime().freeMemory()) / 1024d / 1024d;
        // if higher than the maximum until now
        if (currentMemory > maxMemory) {
            // replace the maximum with the current memory usage
            maxMemory = currentMemory;
        }
        return maxMemory;
    }

    /**
     * 计算particles的平均适应度值
     * @param particles
     * @return
     */
    public static int calAvg(List<Particle> particles){
        int sum = 0;
        for(int i= 0; i < particles.size(); i++){
            sum += particles.get(i).getFitness();
        }
        return sum/particles.size();
    }
    /**
     * 计算particles的相似度
     * @param particles
     * @return
     */
    public static float calSim(List<Particle> particles){
        float jaccard = 0;
        for(int i= 0; i < particles.size(); i++){
            for(int j = i+1 ; j < particles.size(); j++){
                jaccard += calJaccard(particles.get(i).getX(),particles.get(j).getX());
            }
        }
        return jaccard;
    }
    static float calJaccard(List<Integer> a, List<Integer> b){
        float p = 0;
        float q = 0;
        float r = 0;
        float s = 0;

        for(int i = 0; i < a.size(); i++){
            if(a.get(i).equals(1)){
                if(b.get(i).equals(1)){
                    p++;
                }
                else{
                    q++;
                }
            }else{
                if(b.get(i).equals(1)){
                    r++;
                }
                else{
                    s++;
                }
            }
        }
        return p/(p+q+r);
    }

    /**
     * Method to write a high utility itemset to the output file.
     *
     * @throws IOException
     */
    public static void writeOut(BufferedWriter writer, List<HUI> huiSets) throws IOException {
        // Create a string buffer
        StringBuilder buffer = new StringBuilder();
        // append the prefix
        for (int i = 0; i < huiSets.size(); i++) {
            buffer.append(huiSets.get(i).getItemset());
            // append the utility value
            buffer.append("#UTIL: ");
            buffer.append(huiSets.get(i).getFitness());
            buffer.append(System.lineSeparator());
        }
        // write to file
        writer.write(buffer.toString());
        writer.newLine();
    }

    /**
     * Method to write a high utility itemset to the output file.
     *
     * @throws IOException
     */
    public static void writeGbest(BufferedWriter gBestWriter, List<Integer> gBestList, List<Integer> pBestList,  List<Float> pBestSim, List<Float> popSim, List<Integer> numsOFHUI) throws IOException {
        // Create a string buffer
        StringBuilder buffer = new StringBuilder();
        int size = gBestList.size();
        // append the prefix
        for (int i = 0; i < size; i++) {
            buffer.append(gBestList.get(i));
            buffer.append(" ");
            buffer.append(pBestList.get(i));
            buffer.append(" ");
            buffer.append(pBestSim.get(i));
            buffer.append(" ");
            buffer.append(popSim.get(i));
            buffer.append(" ");
            buffer.append(numsOFHUI.get(i));
            buffer.append(System.lineSeparator());
        }
        // write to file
        gBestWriter.write(buffer.toString());
        gBestWriter.newLine();
    }
}
