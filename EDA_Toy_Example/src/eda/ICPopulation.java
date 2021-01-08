package eda;

import eda.core.ICStats;
import eda.exceptions.ICIncorrectNumberOfVariablesException;
import eda.exceptions.ICIncorrectNumberOfObjectivesException;
import eda.exceptions.ICInvalidInitialPopSize;
import eda.exceptions.ICInvalidNewPopSize;
import eda.exceptions.ICInvalidPopSize;
import eda.problem.ICProblem;
import java.util.ArrayList;

public class ICPopulation {
	public ArrayList<ICIndividual> pop;
	public ArrayList<Integer> indexPop;
	public ArrayList<Integer> freePointers;
	public ArrayList<ArrayList<ICIndividual>> bestInd;
	public ArrayList<Float> avgValues;
	public ArrayList<ICStats> maxMinAvg;
	public int initPopSize;
	public int popSize;
	public int newPopSize;
	public boolean statisticInfo;
	private int nDiscVars;
	private int nContVars;
	private int nFuncs;
	public int index;
	private ICProblem problem;
	

	public ICPopulation(int initpopsize, int popsize, int newPopSize, int nDiscVars,
			int nContVars, int nFuncs, ICProblem problem, boolean statisticInfo)

	throws ICIncorrectNumberOfVariablesException,
			ICIncorrectNumberOfObjectivesException, ICInvalidInitialPopSize,
			ICInvalidPopSize, ICInvalidNewPopSize {
		this.problem = problem;

		if (initpopsize < popsize) {
			throw new ICInvalidInitialPopSize(
					"The initial size of the population is lower than popsize.");
		}

		if (popsize <= 2) {
			throw new ICInvalidPopSize("Insufficient population size.");
		}

		if (newPopSize <= 2) {
			throw new ICInvalidNewPopSize(
					"Insufficient offspring population size.");
		}

		index = -1;
		pop = new ArrayList<ICIndividual>();
		indexPop = new ArrayList<Integer>();
		freePointers = new ArrayList<Integer>();

		this.initPopSize = initpopsize;
		this.popSize = popsize;
		this.newPopSize = newPopSize;
		this.nDiscVars = nDiscVars;
		this.nContVars = nContVars;
		this.nFuncs = nFuncs;
		this.statisticInfo = statisticInfo;
	}

	/**
	 * 
	 * @param newPop
	 * @throws iclab.exceptions.ICIncorrectNumberOfVariablesException
	 * @throws iclab.exceptions.ICIncorrectNumberOfObjectivesException
	 */
	public void join(ICPopulation newPop)
			throws ICIncorrectNumberOfVariablesException,
			ICIncorrectNumberOfObjectivesException {
		int i;

		for (i = 0; i < newPop.pop.size(); i++) {
			this.add(newPop.pop.get(i));
		}
		newPopSize = newPopSize + newPop.pop.size();
		return;
	}
	public void add(ICIndividual ind)
			throws ICIncorrectNumberOfVariablesException,
			ICIncorrectNumberOfObjectivesException {
		
		int i = 0;
		int size;
		index = index + 1;
		
		if(0 == index){
			pop.add(ind);
			indexPop.add(new Integer(0));
		}else{
			size = indexPop.size();	
			if(index >= size){
				pop.add(ind);
				indexPop.add(new Integer(size));
			}else{
				
				while(indexPop.get(i) != -1) i++;
				
				indexPop.set(i, freePointers.get(0));
				freePointers.remove(0);
				pop.set(indexPop.get(i), ind);	
			}	
		}
		return;
	}

	private void shiftIndividual(int index) {
		int i;
		int numIndexes = this.index - index + 1;
		for (i = 0; i < numIndexes; i++) {
			indexPop.set(this.index + 1 - i, new Integer(indexPop
					.get(this.index - i)));
		}
		return;
	}
	public void sort() {

		int[] arrayAux = new int[indexPop.size()];
		for (int i = 0; i < indexPop.size(); i++) {
			arrayAux[i] = indexPop.get(i);
		}
		
		
		ordenarQuicksort(arrayAux, 0, indexPop.size()-1);
		
		indexPop.clear();
		for(int i=0; i<arrayAux.length; i++)
			indexPop.add(arrayAux[i]);
	}


	void ordenarQuicksort(int[] vector, int primero, int ultimo){
        int i = primero, j = ultimo;
        float pivote = pop.get(indexPop.get((primero+ultimo)/2)).F[0];
        ICIndividual auxiliar;

        do{
            while(pop.get(indexPop.get(i)).F[0] > pivote) i++;
            
            while(pop.get(indexPop.get(j)).F[0] < pivote) j--;

            if (i<=j){
     	
            	auxiliar = pop.get(indexPop.get(j));

            	pop.set(indexPop.get(j), pop.get(indexPop.get(i)));

            	pop.set(indexPop.get(i), auxiliar);
    	
            	i++;
                j--;      
            }

        } while (i<=j);

        if(primero<j) {
            ordenarQuicksort(vector, primero, j);
        }
        if(ultimo>i) {
            ordenarQuicksort(vector, i, ultimo);
        }
    }


	/**
     *
     */
	public void clean() {
		pop.clear();
		pop.trimToSize();
		return;
	}

	/**
	 * 
	 * @param selPop
	 */
	public void reduce(ArrayList<Integer> selPop) {
		
		int i;
		int j;
		int size;
		size = selPop.size();
		for (i = 0; i < size; i++) {
			
			freePointers.add(indexPop.get(selPop.get(i)));
			indexPop.set(selPop.get(i), -1);
		}
		
		for(i = 0; i<size; i++){
			
			if(indexPop.get(i) == -1){
				
				for (j = i; j < indexPop.size() - 1; j++)
					indexPop.set(j, indexPop.get(j + 1));
				
				i--;		
			}
		}
		
		if(indexPop.get(size) != -1){
			
			for(i = size; i<indexPop.size(); i++)
				indexPop.set(i, -1);
			
		}
		
		index = index - size;
		return;
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		int result;
		result = index + 1;
		return result;
	}
	
	public void printInfo(int iter) {
            String type;
            System.out.println("*| Iteration | Individual | type(Best,Worst,Normal) | Individual(Vector) | Evaluation |*");    // Print Number of POI
            for (int i = 0; i <= index; i++) {
               
                if (i == 0) {
                    type = "B";
                    System.out.print(iter + " " + i + " " + type + " ");        // Print iteration, individual(number) and type (Best,Worst,Normal)
                    pop.get(indexPop.get(i)).show();                            // Print individual (0s & 1s)
                    System.out.println(" " + pop.get(indexPop.get(i)).F[0]);      // Print Evaluation
                }
                else if (i == index) {
                    type = "W";
                    System.out.print(iter + " " + i + " " + type + " ");        // Print iteration, individual(number) and type (Best,Worst,Normal)
                    pop.get(indexPop.get(i)).show();                            // Print individual (0s & 1s)
                    System.out.println(" " + pop.get(indexPop.get(i)).F[0]);      // Print Evaluation
                }
                else {
                    type = "N";
                    System.out.print(iter + " " + i + " " + type + " ");        // Print iteration, individual(number) and type (Best,Worst,Normal)
                    pop.get(indexPop.get(i)).show();                            // Print individual (0s & 1s)
                    System.out.println(" " + pop.get(indexPop.get(i)).F[0]);      // Print Evaluation
                }
            }
	}
	
	public int getnDiscVars(){
		
		return nDiscVars;
	}
	
	public int getnContVars(){
		
		return nContVars;
	}
	
	public int getnFuncs(){
		
		return nFuncs;
	}
	
	public ICProblem getICProblem(){
		
		return problem;
	}
}