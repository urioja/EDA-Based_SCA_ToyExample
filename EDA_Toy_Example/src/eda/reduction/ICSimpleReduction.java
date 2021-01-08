package eda.reduction;

import eda.ICPopulation;
import java.util.ArrayList;

public class ICSimpleReduction implements ICReduction {

	private ArrayList<Integer> selPop;
	private double percentage;
    
    public ICSimpleReduction(double p){
    	
    	percentage = p; 	
    }
    
    public ArrayList<Integer> reduce(ICPopulation pop){
        
        selPop = new ArrayList<Integer>();
        
        for(int i=pop.size() - 1; i>pop.size()*percentage - 1; i--)
        	selPop.add(i);
        
        return selPop;
    }

}
