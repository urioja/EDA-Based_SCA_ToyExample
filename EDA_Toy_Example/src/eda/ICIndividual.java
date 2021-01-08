package eda;

import eda.exceptions.ICIncorrectNumberOfVariablesException;
import eda.exceptions.ICIncorrectNumberOfObjectivesException;


public class ICIndividual {

    public float   C[];
    public int     D[];
    public float   F[];

    public ICIndividual(int nDiscVars,int nContVars,int nFuncs)
            throws  ICIncorrectNumberOfVariablesException,
                    ICIncorrectNumberOfObjectivesException
    {
        if ( nDiscVars < 0 || nContVars < 0 || (nDiscVars + nContVars) < 1) {
            throw new ICIncorrectNumberOfVariablesException("");
        }

        if ( nFuncs < 1 ) {
            throw new ICIncorrectNumberOfObjectivesException("");
        }

        int i;
        boolean isDiscrete = (nDiscVars > 0)?true:false;
        boolean isContinuous = (nContVars >0)?true:false;
        boolean isMultiObjective = (nFuncs>1)?true:false;

        if ( isDiscrete ) {
            D = new int[nDiscVars];
            for(i=0;i<nDiscVars;i++)
            {
                D[i]=0;
            }
        }
        if ( isContinuous ) {
            C = new float[nContVars];
            for(i=0;i<nContVars;i++)
            {
                C[i] = (float)0.0;
            }
        }
        if ( isMultiObjective ) {
            F = new float[nFuncs];
            for(i=0;i<nFuncs;i++)
            {
                F[i]= (float)0.0;
            }
        } else {
            F = new float[1];
            F[0] = (float) 0.0;
        }
    }
    public void show(){
    	for(int i=0; i<D.length; i++){
            System.out.print(D[i]);
    	}
    }
}
