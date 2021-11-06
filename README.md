#  Central Idea

Local search approximation algorithms exist for the Graph Coloring problem. 

Parallelizing the local searches and aggregating information can be beneficial. This was shown in this paper: https://baldur.iti.kit.edu/theses/CooperativeTabucol.pdf


Given this, if change the base algorithm from Tabucol, do we get any benefits? 

CHECKCOL is another example of a local search algorithm: https://www.sciencedirect.com/science/article/pii/S1570866705000262

Otherwise the Java project is broken down like so:

- algorithms package contains algorithms for solving the GCP.
- formatter is just useful stuff for formatting the output
- definition is our problem source definition and some metadata for us to use.
- reader is the graph reader. At the moment it only reads DIMACS, but that's all we need. 
- utility is a bunch of helper functions that check whether the graph satisfies certain criteria.


Outside of that, the output of this is a .soln file that will be used by the plotting directory. It is a python project that just plots graphs. 

At the moment, the graphs are too dense to be usable. 

## Next steps in terms of priority 

- DONE
- Implementing CHECKCOL or HCD as our base local search algorithm.
- Other implementations include PARTIALCOL, ANTCOL, Hill Climbing. 
- Working on plotting infra.
