package eda.selection;

import eda.ICPopulation;
import java.util.ArrayList;

public interface ICSelectionMethod {
    public ArrayList<Integer>  select(ICPopulation pop);
}
