package arm.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxiaole on 2017/6/29.
 */
public class Particle {
    List<Integer> X;// the particle
    int fitness;// fitness value of particle
    int numOfOne;
    public List<Integer> getX() {
        return X;
    }

    public void setX(List<Integer> x) {
        X = x;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getNumOfOne() {
        return numOfOne;
    }

    public void setNumOfOne(int numOfOne) {
        this.numOfOne = numOfOne;
    }

    public Particle() {
        X = new ArrayList<Integer>();
    }

    public Particle(int length) {
        X = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            X.add(i, 0);
        }
    }
    public void copyParticle(Particle particle) {
        if(X==null){
            X = new ArrayList<Integer>();
        }else{
            X.clear();
        }
        X.addAll(particle.X);
        fitness = particle.fitness;
        numOfOne = particle.numOfOne;
    }


}
