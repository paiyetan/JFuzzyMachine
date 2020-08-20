/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import jfuzzymachine.utilities.ConfigFileReader;

/**
 *
 * @author aiyetanpo
 */
public class Graph {
    
    private HashMap<String, LinkedList<String>> nodeToInputNodes;
    private LinkedList<String> nodes;
    private int[][] directedAdjMatrix;
    private File[] jfuzzFiles;
    private double fitCutoff;
    
    private String outputsDir;
    
     
    public Graph(File[] jfuzzFiles, double fitCutOff) throws IOException{
        this.jfuzzFiles = jfuzzFiles;
        this.fitCutoff = fitCutOff;
        getNodeToInputNodes();
        extractNodes();
        updateNodeToInputNodesMap();
        extractDirectedAdjMatrix();
    }
    
    //public Graph(String configFilePath) throws IOException{
    public Graph(HashMap<String, String> config) throws IOException{
        
        String runId;
        String input;
        double fitCutOff;       
        boolean useAnnotatedGraphModel;
        boolean outputEdges;                    
        
        String adjMatOutputFile;
        String edgesOutputFile = null;  
        String fittedModelFile = null;
        String ruleFrequenciesFile = null;
        
        int topFittedModelsToOutput = 0;
        
        
        //include reading from a .config file..
        /**
         * 
         * runId="xxxix" //user specified id prepended to outputted filenames. 
         * input="./inputFile-or-directory-path/"
         * fitCutOff=0.0000  // fit cutOff for which an observed edge is considered
         * useAnnotatedGraphModel=FALSE //[default=FALSE] if TRUE, outputted adjacency matrix is a directed graph
         * outputEdges=FALSE //by default, program outputs only an adjacency matrix file (.adj or .mat file)
         * 
         * 
         * 
         * 
         * 
         */
        //ConfigFileReader cReader = new ConfigFileReader();
        //HashMap<String, String> config = cReader.read(configFilePath); // configuration file path
        
        // initialize run variables/configuration...
        runId = config.get("runId");
        input = config.get("input");
        fitCutOff = Double.parseDouble(config.get("fitCutOff"));
        
        useAnnotatedGraphModel = Boolean.parseBoolean(config.get("useAnnotatedGraphModel"));
        outputEdges = Boolean.parseBoolean(config.get("outputEdges"));
        //includesPheno = Boolean.parseBoolean(config.get("includesPheno"));   
        
        topFittedModelsToOutput = Integer.parseInt(config.get("topFittedModelsToOutput"));
        
        File inputFile = new File(input);
        File[] inputFiles;
        //String outputsDir;
        if(inputFile.isDirectory()){
            inputFiles = inputFile.listFiles();
            //place output(s) in a subdirectory called "runJFuzzUtils/"
            outputsDir = inputFile.getPath() + File.separator + "runJFuzzUtils";
            new File(outputsDir).mkdirs();
            adjMatOutputFile = outputsDir + File.separator + runId + "_runJFuzzUtils.adj";           
            if(outputEdges){
                edgesOutputFile = outputsDir + File.separator + runId + "_runJFuzzUtils.edg"; 
                
            }
            fittedModelFile = outputsDir + File.separator + runId + "_runJFuzzUtils.fit";
            ruleFrequenciesFile = outputsDir + File.separator + runId + "_runJFuzzUtils.fre";
                        
        }else{
            inputFiles = new File[1];
            inputFiles[0] = inputFile;            
            adjMatOutputFile = inputFile.getParent() + File.separator + runId + "_runJFuzzUtils.adj";
            if(outputEdges){
                edgesOutputFile = inputFile.getParent() + File.separator + runId + "_runJFuzzUtils.edg";              
            }
            fittedModelFile = inputFile.getParent()  + File.separator + runId + "_runJFuzzUtils.fit";
            ruleFrequenciesFile = inputFile.getParent()  + File.separator + runId + "_runJFuzzUtils.fre";
        }
        
        if(useAnnotatedGraphModel){
            System.out.println("Using the 'Annotated (Directed) Graph' model...");
            AnnotatedGraph graph = new AnnotatedGraph(inputFiles, fitCutOff);           
            System.out.println("Printing output(s)...");
            graph.getAdjMatrix().print(adjMatOutputFile);
            if(outputEdges){
                graph.printEdges(edgesOutputFile);
                graph.printEdges2(edgesOutputFile);
            }
            //print fitted models
            graph.printBestFitModels(fittedModelFile); // best fit models...
            graph.printAllFittedModels(fittedModelFile, topFittedModelsToOutput); // all fitted models...
            //print rule frequencies...
            graph.printRuleFrequencies(ruleFrequenciesFile);
            
        }else{
            System.out.println("Using the 'Undirected Graph' model...");
            Graph graph = new Graph(inputFiles, fitCutOff); 
            
            System.out.println("Printing output(s)...");
            graph.getAdjMatrix().print(adjMatOutputFile);
        }
        
    }
    
    private void getNodeToInputNodes() throws FileNotFoundException, IOException {
        nodeToInputNodes = new HashMap();
        for(File jfuzzFile : jfuzzFiles){
            BufferedReader reader = new BufferedReader(new FileReader(jfuzzFile));
            String line;
            //ArrayList<String> fileLines = new ArrayList<>();
            boolean readTable = false;
            boolean readHeaderLineNext = false;
            while((line = reader.readLine())!=null){
                //fileLines.add(line); 
                if(line.contains("> End")){
                    readTable = false;
                    readHeaderLineNext = false;
                    continue;
                }
                if(readTable && readHeaderLineNext){                 
                    readHeaderLineNext = false; //switch flag and go to next line...
                    continue;
                }
                if(readTable && !readHeaderLineNext){
                    String[] lineArr = line.split("\t");
                    double fit = Double.parseDouble(lineArr[4]);
                    // only use nodes above specified fit value...
                    if(fit >= fitCutoff){
                        String outputNode = lineArr[0].trim(); //output node
                        if(nodeToInputNodes.containsKey(outputNode)){
                            LinkedList<String> inputNodes = getInputNodes(lineArr[2]);
                            LinkedList<String> mappedInputNodes = nodeToInputNodes.remove(outputNode);
                            for(String inputNode : inputNodes){
                                if(!mappedInputNodes.contains(inputNode)){
                                    mappedInputNodes.add(inputNode);
                                }
                            }
                            nodeToInputNodes.put(outputNode, mappedInputNodes);
                        }else{
                            LinkedList<String> inputNodes = getInputNodes(lineArr[2]);
                            nodeToInputNodes.put(outputNode, inputNodes);
                        }
                    }
                }
                if(line.contains("> Begin")){
                    readTable = true;
                    readHeaderLineNext = true;                    
                }
            }
        }
    }
    
    private LinkedList<String> getInputNodes(String str){
        LinkedList<String> inputNodes = new LinkedList();
        str = str.replace("[", "").replace("]", "");
        String[] strArr = str.split(", ");
        for(String strA : strArr)
            inputNodes.add(strA.trim());
        return(inputNodes);
    }

    private void extractNodes() {
        nodes = new LinkedList();
        Set<String> keys = nodeToInputNodes.keySet();
        for(String key : keys){
            if(nodes.contains(key)==false)
                nodes.add(key);
            LinkedList<String> mappedNodes = nodeToInputNodes.get(key);
            for(String mappedNode : mappedNodes){
                if(nodes.contains(mappedNode)==false)
                    nodes.add(mappedNode);
            }
        } 
        System.out.println("...Nodes# found: " + nodes.size());
    }

    private void updateNodeToInputNodesMap() {
        for(String node : nodes){
            if(nodeToInputNodes.containsKey(node)==false)
                nodeToInputNodes.put(node, new LinkedList());
        }
    }
    
    private void extractDirectedAdjMatrix() {
        directedAdjMatrix = new int[nodes.size()][nodes.size()];
        for(int i = 0; i < nodes.size(); i++){
            String inputNode = nodes.get(i);                
            for(int j = 0; j < nodes.size(); j++){
                String outputNode = nodes.get(j);
                if(nodeToInputNodes.get(outputNode).contains(inputNode)){
                    directedAdjMatrix[i][j] = 1;
                }                   
            }
        }
    }
    
    public Matrix getAdjMatrix(){
        String[] nodesArr = nodes.toArray(new String[nodes.size()]);
        return new Matrix(nodesArr, nodesArr, directedAdjMatrix);
    }

    public String getOutputsDir() {
        return outputsDir;
    }    
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("Starting...");
        Date start = new Date();
        long start_time = start.getTime();              
        
        HashMap<String, String> config = ConfigFileReader.read(args[0]); // configuration file path        
        Graph gGraph = new Graph(config);
                
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
