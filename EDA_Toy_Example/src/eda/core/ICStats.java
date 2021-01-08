package eda.core;

public class ICStats {
	
    private float max;
    private float min;
    private float avg;

    public ICStats(float max, float min, float avg){
        this.max = max;
        this.min = min;
        this.avg = avg;
    }

    public float getMax(){
        return max;
    }

    public float getMin(){
        return min;
    }

    public float getAvg(){
        return avg;
    }
}
