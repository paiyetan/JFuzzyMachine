/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.simulation;

import utilities.graph.Model;
import utilities.graph.Vertex;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import utilities.ConfigFileReader;
import utilities.ModelFitFileReader;

/**
 *
 * @author aiyetanpo
 */
public class Simulator {

    
    
    private enum INIT{FIRST, RANDOM, AVERAGE, ALL}; //initial values
    
    @SuppressWarnings("FieldMayBeFinal")
    private LinkedList<Simulation> simulations;
    //private HashMap<Vertex, LinkedList<Vertex>> outputToInputNodes;
    //private HashMap<Integer, LinkedList<Edge>> edgeIdToMappedEdges;
    private HashMap<Vertex, LinkedList<Model>> outputsToModelsMap;
    private double alpha;
    private int maxIteration = 50; //default...
    private double eCutOff;
    private INIT initType;
    private Table exprs;
    
    private boolean tanTransform;
    private boolean logitTransform; 
    private double k;
    
    private Simulator(String fitFile, // use _GraphOutput.fit - best fit...
                        String exprsMatFile, 
                            double fitCutOff, 
                                double alpha, 
                                    boolean includesPheno, 
                                        String phenotypeId, 
                                            String phenoExprsMatFile, 
                                                int maxIterations, double eCutOff,
                                                    INIT initType,
                                                        boolean tanTransform,
                                                            boolean logitTransform, 
                                                                double k) throws IOException, TableBindingException 
    {
        this.maxIteration = maxIterations;
        this.eCutOff = eCutOff;
        this.exprs = new Table(exprsMatFile, Table.TableType.DOUBLE); 
        this.initType = initType;        
        this.outputsToModelsMap = ModelFitFileReader.read(fitFile, fitCutOff);
        this.logitTransform = logitTransform;
        this.tanTransform = tanTransform;
        this.k = k;
        
        System.out.println("In Simulator: #outputsToModelsMap: " + outputsToModelsMap.keySet().size());
        if(includesPheno){            
            Table phenoExprs = new Table(phenoExprsMatFile, Table.TableType.DOUBLE); //merge pheno and exprs Table into a single table
            exprs = exprs.bind(phenoExprs, Table.BindType.ROW);
        }
        
        this.alpha = alpha;                    
        this.simulations = new LinkedList();
        
        switch(initType){
            case ALL:
                for(int col = 0; col < exprs.getNumberOfColumns(); col++){
                    double[] initialValues = exprs.getColumn(col, Table.TableType.DOUBLE); //this assumes a single sample/perturbation provides the initial outputs values              
                    this.simulations.add( new Simulation(outputsToModelsMap,
                                                            initialValues,
                                                            exprs,
                                                            alpha,
                                                            maxIterations,
                                                            eCutOff,
                                                            includesPheno,
                                                            phenotypeId,
                                                            tanTransform,
                                                            logitTransform, 
                                                            k));
                   
                }                
                break;
                
            default: //all others...(RANDOM, FIRST, AVERAGE)
                //String[] vertexIds = exprs.getRowIds();
                double[] initialValues = getInitialValues(exprs, INIT.RANDOM); //this assumes a single sample/perturbation provides the initial outputs values              
                this.simulations.add( new Simulation(outputsToModelsMap,
                                                            initialValues,
                                                            exprs,
                                                            alpha,
                                                            maxIterations,
                                                            eCutOff,
                                                            includesPheno,
                                                            phenotypeId,
                                                            tanTransform,
                                                            logitTransform, 
                                                            k));
              
        }
            
    }  
    
    /*
    private void getOutputToModelsMap(String fitFile, double fitCutOff) {
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        
    }
    */

    
    private double[] getInitialValues(Table exprs, INIT init) {
        double[] initValues = null;
        switch(init){
            case FIRST:
                initValues = exprs.getColumn(0, Table.TableType.DOUBLE);
                break;
            default: //init.RANDOM.....
                int randomIndex = (int) (Math.random() * exprs.getColumnIds().length);
                initValues = exprs.getColumn(randomIndex, Table.TableType.DOUBLE);
        }
        return initValues;
    }
    
    /*
    private void getOutputToInputNodes(String edgesFile, double fitCutOff) throws FileNotFoundException, 
            IOException{
        this.outputToInputNodes = new HashMap();
        this.edgeIdToMappedEdges = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(edgesFile));
        String line;
        int lineIndex = 0;
        while((line = reader.readLine())!=null){
            lineIndex++;
            if(lineIndex > 1){//skip the header line....
                String[] lineArr = line.split("\t");
                double fit = Double.parseDouble(lineArr[4]);               
                if(fit >= fitCutOff){// only use nodes above specified fit value...
                    Vertex inputNode = new Vertex(lineArr[0].trim());//inputNode (origin)
                    Vertex outputNode = new Vertex(lineArr[1].trim());//outputNode (destination)
                    if(outputToInputNodes.containsKey(outputNode)){                           
                        LinkedList<Vertex> mappedInputNodes = outputToInputNodes.remove(outputNode);                
                        if(!mappedInputNodes.contains(inputNode)){
                            mappedInputNodes.add(inputNode);
                        }
                        outputToInputNodes.put(outputNode, mappedInputNodes);
                    }else{
                        LinkedList<Vertex> mappedInputNodes = new LinkedList();
                        mappedInputNodes.add(inputNode);
                        outputToInputNodes.put(outputNode, mappedInputNodes);
                    }
                    
                    String rule = lineArr[2]; 
                    
                    Edge edge = new Edge(inputNode, outputNode, rule, fit);
                    int edgeId = edge.hashCode();
                    if(edgeIdToMappedEdges.containsKey(edgeId)){
                        LinkedList<Edge> mappedEdges = edgeIdToMappedEdges.remove(edgeId);
                        mappedEdges.add(edge);
                        edgeIdToMappedEdges.put(edgeId, mappedEdges);
                    }else{
                        LinkedList<Edge> mappedEdges = new LinkedList();
                        mappedEdges.add(edge);
                        edgeIdToMappedEdges.put(edgeId, mappedEdges);
                    }                   
                }
            }
        }
        System.out.println("Found outputNodes#: " + outputToInputNodes.keySet().size());
        printOutputNodes();
        
    }
    */
    
    public void printOutputNodes(String outFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outFile + ".out");
        //Set<Vertex> os = outputToInputNodes.keySet();
        Set<Vertex> os = outputsToModelsMap.keySet();
        for(Vertex o : os){
            printer.println(o.getId()); 
            System.out.println(o.getId());
        }
        printer.close();
    }
    
    
    public void runSimulations(){
        // implement a parallel running of simulations here...
        simulations.parallelStream().forEach((simulation) -> {
            simulation.run();
        });
    } 
    
    /*
    public void printSimulations(String outFile) throws FileNotFoundException{
        //printDeltas(outFile + ".dta"); //deltas
        //printTValues(outFile + ".sim"); //simulated values...
        printDeltas(outFile); //deltas
        printTValues(outFile); //simulated values...
    }
    */
    public void printSimulations(String outFile) throws FileNotFoundException{
        printDeltas(outFile); //deltas
        printSimulatedValues(outFile); //simulated values...
    }
    
    public void printDeltas(String outFile) throws FileNotFoundException{
        /*
        for(int i = 0; i < simulations.size(); i++){
            PrintWriter printer = new PrintWriter(outFile + "." + i + ".dta");
            Simulation sim = simulations.get(i);
            LinkedList<double[]> deltaValuesList = sim.getSimulatedValues();
            // print table_header...
            for(int itr = 0; itr < deltaValuesList.size(); itr++){
                printer.print("\t" + "itr_" + itr);
            }
            printer.print("\n");
            // print table_body...
            for(int j = 0; j < exprs.getRowIds().length; j++){
                printer.print(exprs.getRowIds()[j]); //print rowId
                for(int k = 0; k < deltaValuesList.size(); k++){
                    double delta;
                    if(k == 0){
                        delta = deltaValuesList.get(k)[j];
                    }else{
                        delta = deltaValuesList.get(k)[j] - deltaValuesList.get(k-1)[j];
                    }
                    printer.print("\t" + delta);
                }
                printer.print("\n");
            }//end table
            printer.close();
        }
        */
        for(int i = 0; i < simulations.size(); i++){
            PrintWriter printer = new PrintWriter(outFile + "." + i + ".dta");
            Simulation sim = simulations.get(i);
            LinkedList<double[]> deltaValuesList = sim.getDeltaValuesList();
            // print table_header...
            for(int itr = 0; itr < deltaValuesList.size(); itr++){
                printer.print("\t" + "itr_" + itr);
            }
            printer.print("\n");
            // print table_body...
            for(int j = 0; j < exprs.getRowIds().length; j++){
                printer.print(exprs.getRowIds()[j]); //print rowId
                for(int k = 0; k < deltaValuesList.size(); k++){
                    
                    printer.print("\t" + deltaValuesList.get(k)[j]);
                    
                }
                printer.print("\n");
            }// end table
            printer.close();
        }
    }
    
    public void printSimulatedValues(String outFile) throws FileNotFoundException{
        for(int i = 0; i < simulations.size(); i++){
            PrintWriter printer = new PrintWriter(outFile + "." + i + ".sim");
            Simulation sim = simulations.get(i);
            LinkedList<double[]> simValues = sim.getSimulatedValues();
            // print table_header...
            for(int itr = 0; itr < simValues.size(); itr++){
                printer.print("\t" + "itr_" + itr);
            }
            printer.print("\n");
            // print table_body...
            for(int j = 0; j < exprs.getRowIds().length; j++){
                printer.print(exprs.getRowIds()[j]); //print rowId
                for(int k = 0; k < simValues.size(); k++){
                    
                    printer.print("\t" + simValues.get(k)[j]);
                    
                }
                printer.print("\n");
            }// end table
            printer.close();
        }
    }
   
   
    /*
    private void printDeltas(String outFile) throws FileNotFoundException {
        PrintWriter printer;
        switch(initType){
            case ALL:
                int simIndex = 0;
                int colIndex = 0;
                printer = new PrintWriter(outFile + "." + colIndex + ".dta");
                for(Simulation sim : simulations){
                    printer.print(sim.getId());           
                    for(double delta : sim.getDeltas())
                        printer.print("\t" + delta);
                    printer.print("\n");
                    simIndex++;
                    if(simIndex%exprs.getNumberOfRows() == 0){
                        printer.close();
                        colIndex++;
                        printer = new PrintWriter(outFile + "." + colIndex + ".dta");
                    }
                } 
                break;
            default: // all others.....
                printer = new PrintWriter(outFile + ".dta");
                for(Simulation sim : simulations){
                    printer.print(sim.getId());           
                    for(double delta : sim.getDeltas())
                        printer.print("\t" + delta);
                    printer.print("\n");
                }        
                printer.close();
        }
        
    }

    private void printTValues(String outFile) throws FileNotFoundException {
        PrintWriter printer;
        switch(initType){
            
            case ALL:
                int simIndex = 0;
                int colIndex = 0;
                printer = new PrintWriter(outFile + "." + colIndex + ".sim");
                for(Simulation sim : simulations){
                    printer.print(sim.getId());           
                    for(double value : sim.getSimulatedValues())
                        printer.print("\t" + value);
                    printer.print("\n");
                    simIndex++;
                    if(simIndex%exprs.getNumberOfRows() == 0){
                        printer.close();
                        colIndex++;
                        printer = new PrintWriter(outFile + "." + colIndex + ".sim");
                    }
                } 
                break;
            default: // all others...
                printer = new PrintWriter(outFile + ".sim");
                for(Simulation sim : simulations){
                    printer.print(sim.getId());           
                    for(double value : sim.getSimulatedValues())
                        printer.print("\t" + value);
                    printer.print("\n");
                }  
                printer.close();
        }
        
    }
    */
   
    public static void main(String[] args) throws IOException, TableBindingException{
                
        System.out.println("Starting...");       
        Date start = new Date();
        long start_time = start.getTime();
        
        
        //String edgesFile; 
        String fitFile; 
        String exprsMatFile;
        double fitCutOff;
        double alpha;        
        boolean includesPheno;//FALSE (default), a logical to indicate phenotype simulation be done as well
        String phenotypeId; // "Pheno" (default), identifier used for phenotype in input files...
        String phenoExprsMatFile; // a corresponding "Pheno" expression matrix file        
        int maxIterations = -1; // if greater than -1 
        double eCutOff;
        
        boolean tanTransform;
        boolean logitTransform;
        double k;
        
        
        String outputFile; // a common-name to use for output file(s)
        
        /***
         * edgesFile=./path-to-edges-file/   ### output generated by JFuzzyMachineUtils.graph.Graph, a 'filename.edg' file
         * exprsMatFile=./path-to-expression-matrix-file/
         * fitCutOff=0.6
         * alpha=0.01
         * includesPheno=FALSE
         * phenotypeId=Pheno
         * phenoExprsMatFile=./path-to-phenotype-expression-matrix-file/
         * maxIterations=50                  ### maximum iteration for simulation
         * eCutOff=0.0001                    ### error estimate cut-off
         * initialOutputsValues=ALL                 ### a flag {FIRST, RANDOM, AVERAGE, ALL} to determine initial values for simulation run...
         * 
         */      
        ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = cReader.read(args[0]); // configuration file path
        
        //initialize run-time variables....       
        //edgesFile = config.get("edgesFile"); 
        fitFile = config.get("fitFile");
        exprsMatFile = config.get("exprsMatFile"); 
        fitCutOff = Double.parseDouble(config.get("fitCutOff")); 
        alpha = Double.parseDouble(config.get("alpha"));
        
        includesPheno = Boolean.parseBoolean(config.get("includesPheno"));
        phenotypeId = config.get("phenotypeId"); 
        phenoExprsMatFile = config.get("phenoExprsMatFile");
        
        maxIterations = Integer.parseInt(config.get("maxIterations"));
        eCutOff = Double.parseDouble(config.get("eCutOff"));
        String initValues = config.get("initialOutputsValues");
        
        
        
        //determine initial outputs values enum type...
        INIT initType = null;
        if(initValues.equalsIgnoreCase("FIRST"))
            initType = INIT.FIRST;
        else if(initValues.equalsIgnoreCase("RANDOM"))
            initType = INIT.RANDOM;
        else if(initValues.equalsIgnoreCase("AVERAGE"))
            initType = INIT.AVERAGE;
        else if(initValues.equalsIgnoreCase("ALL"))
            initType = INIT.ALL;
        
        tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
        logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
        k = Double.parseDouble(config.get("kValue"));
        
                       
        //outputFile = edgesFile.replace(".txt", "").replace(".tsv", "").replace(".edg", "");        
        outputFile = fitFile.replace(".fit", "").replace(".fit2", "").replace(".txt", "");        
        
        //Simulator sim = new Simulator(edgesFile, exprsMatFile, fitCutOff, alpha,
        //                                includesPheno, phenotypeId, phenoExprsMatFile, 
        //                                    maxIterations, eCutOff, initType);
        
        //Simulator sim = new Simulator(edgesFile, exprsMatFile, fitCutOff, alpha,
        //                                includesPheno, //phenotypeId, 
        //                                    phenoExprsMatFile, 
        //                                        maxIterations, eCutOff, initType);
        Simulator sim = new Simulator(fitFile, exprsMatFile, fitCutOff, alpha,
                                        includesPheno, phenotypeId, 
                                            phenoExprsMatFile, 
                                                maxIterations, eCutOff, initType,
                                                tanTransform, logitTransform,  k);
        
        
        System.out.println("Running simulations...");
        sim.runSimulations();
        
        System.out.println("Printing simulations results...");
        sim.printSimulations(outputFile);
        
        
        System.out.println("\n...Done!");        
        Date end = new Date();
        long end_time = end.getTime();
        
        System.out.println("\n     Started: " + start.toString());
        System.out.println("     Ended: " + end.toString());
        System.out.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + 
                                                      " seconds.");
        
        
        
    }
    
}
