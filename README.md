# jFuzzyMachine

Elucidating mechanistic relationships among intracellular macromolecules is fundamental to understanding the molecular basis of normal and diseased processes. The classical fuzzy logic approach employs varying degrees of truth to describe relationships between interacting molecules. jFuzzyMachine implements the fuzzy logic approach to elucidate mechanistic relationships among biological molecules and makes more readily available the fuzzy logic inference approach in a freely available tool.

## Getting jFuzzyMachine

jFuzzyMachine’s source codes and precompiled binaries are freely available at the repository locations: 
https://github.com/paiyetan/jfuzzymachine  
https://github.com/paiyetan/jfuzzymachine/releases/tag/v1.7.21 
https://bitbucket.org/paiyetan/jfuzzymachine/src/master/
https://bitbucket.org/paiyetan/jfuzzymachine/downloads/

## Installation Requirements

jFuzzyMachine is platform independent. It would run on a Windows, Mac, or UNIX-based Operating System (OS) with an appropriately preinstalled Java Runtime Environment (JRE). Java 7 or above is required. You may download the latest version of Java from https://www.java.com/en/download/.

To run the visualization add-on (plugin), provided as an added-value, a UNIX-based OS with the R program statistical computing environment preinstalled, is required. R may be downloaded from https://cran.r-project.org/.
2.6 Installing jFuzzyMachine

Unzip the compressed application package into a directory of choice. The content of the unzipped folder should include: One primary java archive (.jar) folder, four runtime configuration (.config) files, and four subdirectories (etc/, lib/, plugins/, and src/),

    JFuzzyMachine.jar

    jfuzzymachine.config

    jfuzzymachine.graph.config

    jfuzzymachine.evaluator.config

    jfuzzymachine.simulator.config

    etc

    lib

    plugins

    src

The configuration files are pre-filled to satisfy required parameters for this manual’s demonstration. Users may appropriately fill-in their own specifications and experiment with the tool. See configuration options below.

## Running jFuzzyMachine

To run the tool, on the command-line,
    Navigate into the application directory
    Appropriately fill-in the desired run-time options in the configuration files and
    Depending on application module or functional unit of interest, type the appropriate commands

For a full description of jFuzzyMachine, please this publication: https://www.biorxiv.org/content/10.1101/2020.10.06.315994v1.full 
