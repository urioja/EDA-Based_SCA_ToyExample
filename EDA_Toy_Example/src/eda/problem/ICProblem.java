package eda.problem;

import eda.ICIndividual;

abstract public class ICProblem {
    abstract public boolean isValid(ICIndividual ind);
    abstract public void evaluate(ICIndividual ind);
    abstract public void repair(ICIndividual ind);
    abstract public boolean precedes(ICIndividual ind1,ICIndividual ind2);
}
