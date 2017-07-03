package arm.entity;

/**
 * Created by jinxiaole on 2017/6/29.
 */
public class HUI {
    String itemset;
    int fitness;

    public HUI(String itemset, int fitness) {
        super();
        this.itemset = itemset;
        this.fitness = fitness;
    }

    public String getItemset() {
        return itemset;
    }

    public void setItemset(String itemset) {
        this.itemset = itemset;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}
