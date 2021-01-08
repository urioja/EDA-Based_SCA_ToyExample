package eda.repairs;

import eda.ICIndividual;

public class ICSetInBoundsRepair implements ICRepairMethod {

    float _min;
    float _max;

    public ICSetInBoundsRepair(float min,float max)
    {
        _min=min;
        _max=max;
    }
    public void repair(ICIndividual ind) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
