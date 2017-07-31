package arm.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxiaole on 2017/6/29.
 */
public class Particle {
    List<Integer> X;// the particle
    float fitness;// fitness value of particle
    int utility;
    float sim;
    int numOfOne;
    public List<Integer> getX() {
        return X;
    }

    public void setX(List<Integer> x) {
        X = x;
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public float getSim() {
        return sim;
    }

    public void setSim(float sim) {
        this.sim = sim;
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
        utility = particle.utility;
        sim = particle.sim;
        numOfOne = particle.numOfOne;
    }


}
