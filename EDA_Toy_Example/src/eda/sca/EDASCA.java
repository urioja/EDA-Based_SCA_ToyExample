/**
 * @author <a href="mailto:jlflores@ikerlan.es">J.L. Flores</a>
 * @author <a href="mailto:urioja@ikerlan.es">Unai Rioja</a>
 * 
 * @version Alpha
 * @since 31-12-2020
 * 
 * @Description: This code performs a "Toy Example" for Estimation of Distribution Algorithm (EDA)-Based Side Channel Analysis.
 * In other words, this code represents the "EDAs part" of the EDA-Based Profiling Attack (EDA-Based PA) purposed in [1].
 * In short, a random sample of candidate groups of POIs (Individuals) is generated. Each individual is represented as a binary string of length L. 
 * These candidate POIs are evaluated by means of an objective function wich evaluates the performance of the attacks. According to this evaluation, 
 * the best points are selected. Then, the selected solutions are used to learn a probabilistic model, and based on this new model a new set of 
 * groups of POIs is sampled. The process is iterated until the optimal value has been found or another termination criterion is fulfilled.
 * In this example, instead of performing Template attacks with the selected POIs and evaluate each individual based on the performance of the attack 
 * (as sown in [1]), we show a simpler example: solving the "MaxOne" problem. This problem consists of searching for all binary strings of length L 
 * (Individuals) for the string containing the maximum number of ones. 
 * The EDA-Based PA can be implemented by modifying the "evaluation" part and performing some kind of PA (Template attack, Machine learning, 
 * Deep Learning, etc.) on a set of traces instead of just count the number of ones in each string (Individual). 
 * Coded with Apache NetBeans IDE 12.1.
 * 
 * @References:
 * [1] <a href="https://eprint.iacr.org/2020/1600">[Preprint] Unai Rioja, Lejla Batina, Jose Luis Flores and Igor Armendariz. Auto-tune POIs: Estimation of distribution algorithms for efficient side-channel analysis. IACR Cryptol. ePrint Arch. 2020.</a>
 * 
 */

package eda.sca;

import java.io.IOException;
import eda.ICBernouilli;
import eda.ICIndividual;
import eda.ICPopulation;
import eda.problem.ICSCAttack;
import eda.problem.ICProblem;
import eda.reduction.ICReduction;
import eda.reduction.ICSimpleReduction;
import eda.selection.ICSelectBest;
import eda.selection.ICSelectionMethod;
import eda.exceptions.ICIncorrectNumberOfVariablesException;
import eda.exceptions.ICIncorrectNumberOfObjectivesException;
import eda.exceptions.ICInvalidInitialPopSize;
import eda.exceptions.ICInvalidNewPopSize;
import eda.exceptions.ICInvalidPopSize;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EDASCA {
    
    public String _textFileFolder;
    public String _traceFileFolder;
    public String _parameterFileFolder;
    public String _userModulesFolder;
    public String _systemModulesFolder;
    public String _dataContainerFolder;
    public String _imageFileFolder;
    public String _cutPointFile;
    
    ICPopulation        _pop;
    ICPopulation        _newPop;
    ICSelectionMethod   _selection;
    ICReduction         _reduction;
    ICProblem           _problem;
    ArrayList<Integer>  _selPop;
    ArrayList<Integer>  _redPop;
    ICBernouilli[]      _modelo;

    int                 _maxPoints;
    int                 _numTraces;
    int                 _popSize;
    int                 _numDiscVars;
    int                 _numContVars;
    int                 _nFuncs;
    boolean             _statistics;
    float               _selectionProbability;
    float               _reductionProbability;
    int                 _MAX_ITERATIONS;
    
    
    // Initialize UMDA parameters
    private void initUMDA() {
        this._MAX_ITERATIONS = 20;
        this._maxPoints = 50;
        this._popSize   = 100;
        this._numDiscVars = this._maxPoints;
        this._numContVars = 0;
        this._nFuncs = 1;
        this._statistics = false;
        this._selectionProbability = 0.50f;
        this._selection = new ICSelectBest(this._selectionProbability);
        this._reductionProbability = 0.50f;
        this._reduction = new ICSimpleReduction(this._reductionProbability);
        this._modelo = new ICBernouilli[this._numDiscVars];
        this._problem = new ICSCAttack();
    }
    
    // Initialize population
    private void createPopulation() {
        try {
            this._pop = new ICPopulation(this._popSize, this._popSize, this._popSize, this._numDiscVars,this._numContVars, this._nFuncs, this._problem, this._statistics);
            for(int i=0; i<this._popSize; i++) {
                this._pop.add(new ICIndividual(this._numDiscVars, this._numContVars, this._nFuncs));
            }
        } catch (ICIncorrectNumberOfObjectivesException ex) {
            Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ICInvalidInitialPopSize ex) {
            Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ICInvalidPopSize ex) {
            Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ICInvalidNewPopSize ex) {
            Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ICIncorrectNumberOfVariablesException ex) {
            Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Initialize probabilistic model
    private void initializeModel() {
        for(int i=0; i<this._numDiscVars; i++) {
            this._modelo[i] = new ICBernouilli(0.5);
        }     
    }
    
    // Random sample of Individuals (candidate groups of POIs).
    private void sampleModel() {
        for(int i=0; i<this._pop.popSize; i++){
            for(int j=0; j<this._numDiscVars; j++) {
                this._pop.pop.get(i).D[j] = this._modelo[j].newSample();
            }
        }
    }
    
    // Evaluate population (see eda.problem.ICSCAttack.java)
    private void evaluatePopulation() throws IOException {
        /*
        *   Here the user can implement his own evaluation technique 
        *   (e.g. Perform a TA and evaluate its performance)
        */
        for(int t=0;t<this._popSize;t++) {
            // Score function
            this._problem.evaluate(this._pop.pop.get(t)); 
        }        
    }
    
    // Sort population based on evaluation (see eda.population.java)
    private void sortingPopulation() {
        this._pop.sort();
    }
    
    // Output results of certain iteration (see eda.population.java)
    private void showPopulation(int iteration) {
        this._pop.printInfo(iteration);
    }
    
    // Select population (see eda.selection.ICSelectionMethod.java)
    private void selectPopulation() {
        this._selPop = this._selection.select(this._pop);
    }
    
    // Recompute probabilistic model 
    private void learnModel() {
        for(int i=0; i<this._numDiscVars; i++) {
            int ones = 0;
            double newP = 0.0;
            for(int j=0; j<this._selPop.size(); j++) {
                if (this._pop.pop.get(this._selPop.get(j)).D[i] == 1) ones++;
            }
            newP = (double)ones/(double)this._selPop.size();
            this._modelo[i].setP(1.0-newP);
        }    
    }
    
    // Run EDA-SCA
    public boolean run() {
        int iteration;
        
        this.initUMDA();
        this.createPopulation();
        this.initializeModel();
        this.sampleModel();
        iteration = 0;
        while ( iteration < this._MAX_ITERATIONS ) {
            
            try {
                this.evaluatePopulation();
                this.sortingPopulation();
                this.showPopulation(iteration);
                this.selectPopulation();
                this.learnModel();
                this.sampleModel();

                iteration = iteration + 1;                
            } catch (IOException ex) {
                Logger.getLogger(EDASCA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    // Main function
    public static void main(String[] args) throws IOException {
        System.out.println("SCA Attack guided by UMDA (Toy Example)");
        EDASCA edaSca;
        edaSca = new EDASCA();        
        edaSca.run();
    }
}
