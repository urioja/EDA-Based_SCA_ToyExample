package eda.problem;

import eda.ICIndividual;
import java.util.ArrayList;

public class ICSCAttack extends ICProblem {
    ArrayList<Float> _confidence;
    public void set(ArrayList<Float> confidence) {
        this._confidence = new ArrayList<Float>(confidence);
    }
    public boolean isValid(ICIndividual ind) {
        boolean result;
        if (ind.D.length <= 0 || ind.C.length > 0 || ind.F.length != 1)
        {
            result = false;
        } else {
            result = true;
            for(int i=0;i<ind.D.length;i++)
            {
                if (ind.D[i]!=0 && ind.D[i]!=1)
                {
                    result = false;
                }
            }
        }
        return result;
    }
    
    // EVALUATE: counting the number of ones in the string
    public void evaluate(ICIndividual ind)
    {
        float f=0;
        for(int j=0;j<ind.D.length;j++) {
            if (ind.D[j]==1) f=f+1;                
        }        
        ind.F[0] = f;
        return;
    }
    public void repair(ICIndividual ind) {
        return;
    }
    public boolean precedes(ICIndividual ind1, ICIndividual ind2) {
        boolean result;
        if (ind1.F[0]<= ind2.F[0]) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

}