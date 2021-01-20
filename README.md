# EDA-Based SCA "Toy Example"

This code performs a "Toy Example" for Estimation of Distribution Algorithm (EDA)-Based Side Channel Analysis. In other words, this code represents the "EDAs part" of the EDA-Based Profiling Attack (EDA-Based PA) purposed in [[1]](#1). 

In short, a random sample of candidate groups of POIs (Individuals) is generated. Each individual is represented as a binary string of length L. 
These candidate POIs are evaluated by means of an objective function wich evaluates the performance of the attacks. According to this evaluation, the best points are selected. Then, the selected solutions are used to learn a probabilistic model, and based on this new model a new set of groups of POIs is sampled. The process is iterated until the optimal value has been found or another termination criterion is fulfilled.

In this example, instead of performing Template attacks with the selected POIs and evaluate each individual based on the performance of the attack (as sown in [[1]](#1)), we show a simpler example: solving the "MaxOne" problem. This problem consists of searching for all binary strings of length L (Individuals) for the string containing the maximum number of ones. 

The EDA-Based PA can be implemented by modifying the "evaluation" part and performing some kind of PA (Template attack, Machine learning, Deep Learning, etc.) on a set of traces instead of just count the number of ones in each string (Individual). 

Coded with Apache NetBeans IDE 12.1. 
(main class => eda.sca.EDASCA)
 
## References
<a id="1">[1]</a> 
[Preprint] Unai Rioja, Lejla Batina, Jose Luis Flores and Igor Armendariz. Auto-tune POIs: Estimation of distribution algorithms for efficient side-channel analysis. IACR Cryptol. ePrint Arch. 2020.
https://eprint.iacr.org/2020/1600
