package eda.selection;

import eda.ICIndividual;
import eda.ICPopulation;
import java.util.ArrayList;

public class ICSelectBest implements ICSelectionMethod {

	private ArrayList<Integer> selPop;
	private double percentage;
    
    public ICSelectBest(double p){
    	
    	percentage = p; 	
    }
    
    public ArrayList<Integer> select(ICPopulation pop){
        
        selPop = new ArrayList<Integer>();
        
        for(int i=0; i<pop.popSize*percentage; i++)
        	selPop.add(pop.indexPop.get(i));
        
        if(pop.statisticInfo){
    		ArrayList<ICIndividual> bestInGeneration = new ArrayList<ICIndividual>();
    		
    		for(int i=0; i<selPop.size(); i++)
    			bestInGeneration.add(pop.pop.get(selPop.get(i)));
    			
    		pop.bestInd.add(bestInGeneration);
    	}
        
        return selPop;
    }
    
}
