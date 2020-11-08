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
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author aiyetanpo
 */
public class AnnotatedGraph {
    
    private final File[] jfuzzFiles;
    private final double fitCutOff;
    private LinkedList<Vertex> vertices;
    private LinkedList<Edge> edges;
    //private HashMap<Vertex, LinkedList<Vertex>> outputToInputNodes;
    private HashMap<Vertex, LinkedList<Model>> outputToModelsMap;
    private HashMap<Integer, LinkedList<Edge>> edgeIdToMappedEdges; //hashedEdgeId<=>mappedEdges
    private int[][] directedAdjMatrix;
    private HashMap<String, Integer> ruleFrequencies;
    private boolean penalizeNullRules;
    private LinkedList<String> nullRules;
    
    private boolean reduceMemoryFootprint;
    private HashMap<Vertex, MappedModels> outputToMappedModelsMap;

    public AnnotatedGraph(File[] jfuzzFiles, 
                          double fitCutOff, 
                          boolean penalizeNullRules,
                          boolean reduceMemoryFootprint) throws IOException {
        
        
        vertices = new LinkedList();
        edges = new LinkedList();        
        outputToModelsMap = new HashMap();
        edgeIdToMappedEdges = new HashMap();
        ruleFrequencies = new HashMap();        
        
        this.jfuzzFiles = jfuzzFiles;
        this.fitCutOff = fitCutOff;
        this.penalizeNullRules = penalizeNullRules;
        
        nullRules = new LinkedList();
        nullRules.add("1, 1, 1");
        nullRules.add("1, 2, 1");
        nullRules.add("1, 3, 1");
        
        nullRules.add("2, 1, 2");
        nullRules.add("2, 2, 2");
        nullRules.add("2, 3, 2");
        
        nullRules.add("3, 1, 3");
        nullRules.add("3, 2, 3");
        nullRules.add("3, 3, 3");
        
        this.reduceMemoryFootprint = reduceMemoryFootprint;
        this.outputToMappedModelsMap = new HashMap();       
        
        readInputFiles();
        extractDirectedAdjMatrix();
    }
    
    private void readInputFiles() throws FileNotFoundException, IOException {
        
        
        for(File jfuzzFile : jfuzzFiles){
            System.out.println("Reading file: " + jfuzzFile.getName());
            BufferedReader reader = new BufferedReader(new FileReader(jfuzzFile));
            String line;
            
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
                    
                    double fit = 0;
                    try{
                        fit = Double.parseDouble(lineArr[4]);
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("jFuzzFile: " + jfuzzFile.getName());
                        e.printStackTrace();
                    }
                    
                    // only use nodes above specified fit value...
                    if(fit >= fitCutOff){
                        
                        Vertex outputNode = new Vertex(lineArr[0].trim());
                        if(!(vertices.contains(outputNode))){
                            vertices.add(outputNode);
                        }                    
                        LinkedList<Vertex> inputNodes = getInputNodes(lineArr[2]);
                        //update vertices list...
                        for(Vertex inputNode : inputNodes){
                            if(!(vertices.contains(inputNode))){
                                vertices.add(inputNode);
                            }
                        }

                        //update edgeIdToMappedEdges HashMap and edges...
                        LinkedList<String> rules = getInputRules(lineArr[3]);
                        //update rule frequencies..
                        for(String rule : rules){
                            if(ruleFrequencies.containsKey(rule)){
                                int freq = ruleFrequencies.remove(rule);
                                freq++;
                                ruleFrequencies.put(rule, freq);
                            }else{
                                ruleFrequencies.put(rule, 1);
                            }
                        }

                        Model model = new Model(outputNode, inputNodes, rules, fit);
                        if(this.reduceMemoryFootprint){
                            if(outputToMappedModelsMap.containsKey(outputNode)){                           
                                MappedModels mappedModels = outputToMappedModelsMap.remove(outputNode);
                                if(mappedModels.getNumOfMappedModels() < mappedModels.getMaxMappedModels()){
                                    mappedModels.add(model);
                                }else if(model.getFit() > mappedModels.getMaxEstimatedFit()){
                                    mappedModels.removeLeastFitted();
                                    mappedModels.add(model);                                    
                                }                                
                                outputToMappedModelsMap.put(outputNode, mappedModels);
                            }else{ 
                                LinkedList<Model> mappedModelsList = new LinkedList();
                                MappedModels mappedModels = 
                                        new MappedModels(outputNode, 
                                                         mappedModelsList, //empty mapped models...
                                                         250, //maxMappedModels, defaults to 250
                                                         -100.0, //maxEstimatedFit 
                                                         100.0 //minEstimatedFit 
                                                        );
                                mappedModels.add(model);
                                outputToMappedModelsMap.put(outputNode, mappedModels);
                            }                                                         
                        }else{                                                       
                            if(outputToModelsMap.containsKey(outputNode)){                           
                                LinkedList<Model> mappedModels = outputToModelsMap.remove(outputNode);
                                mappedModels.add(model);
                                outputToModelsMap.put(outputNode, mappedModels);
                            }else{ 
                                LinkedList<Model> mappedModels = new LinkedList();
                                mappedModels.add(model);
                                outputToModelsMap.put(outputNode, mappedModels);
                            }  
                            
                        }                                                
                    }                    
                }
                if(line.contains("> Begin")){
                    readTable = true;
                    readHeaderLineNext = true;                    
                }
            }
            //update the edgesIdToMappedEdges
            if(this.reduceMemoryFootprint){
                Set<Vertex> outputNodes = outputToMappedModelsMap.keySet();
                for(Vertex outputNode : outputNodes){
                    LinkedList<Model> mappedModels = outputToMappedModelsMap.get(outputNode).getMappedModels();
                    Collections.sort(mappedModels);
                    Model model = mappedModels.getLast();

                    LinkedList<Vertex> inputNodes = model.getInputNodes();
                    LinkedList<String> rules = model.getRules();
                    double fit = model.getFit();
                    for(int i = 0; i < inputNodes.size(); i++){
                        Vertex inputNode = inputNodes.get(i);
                        String rule = rules.get(i);
                        Edge edge = new Edge(inputNode, outputNode, rule, fit);
                        int edgeId = edge.hashCode();
                        if(edgeIdToMappedEdges.containsKey(edgeId)){
                            LinkedList<Edge> mappedEdges = edgeIdToMappedEdges.remove(edgeId);    
                            //mappedEdges.add(edge);
                            if(!mappedEdges.contains(edge))
                                mappedEdges.add(edge);

                            edgeIdToMappedEdges.put(edgeId, mappedEdges);
                        }else{
                            LinkedList<Edge> mappedEdges = new LinkedList();
                            mappedEdges.add(edge);
                            edgeIdToMappedEdges.put(edgeId, mappedEdges);
                        }
                    }
                }
                
            }else{
                Set<Vertex> outputNodes = outputToModelsMap.keySet();
                for(Vertex outputNode : outputNodes){
                    LinkedList<Model> mappedModels = outputToModelsMap.get(outputNode);
                    Collections.sort(mappedModels);
                    Model model = mappedModels.getLast();

                    LinkedList<Vertex> inputNodes = model.getInputNodes();
                    LinkedList<String> rules = model.getRules();
                    double fit = model.getFit();
                    for(int i = 0; i < inputNodes.size(); i++){
                        Vertex inputNode = inputNodes.get(i);
                        String rule = rules.get(i);
                        Edge edge = new Edge(inputNode, outputNode, rule, fit);
                        int edgeId = edge.hashCode();
                        if(edgeIdToMappedEdges.containsKey(edgeId)){
                            LinkedList<Edge> mappedEdges = edgeIdToMappedEdges.remove(edgeId);    
                            //mappedEdges.add(edge);
                            if(!mappedEdges.contains(edge))
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
        }
        // if reduceMemoryFootprint option was employed, tranfer all objects in outputToMappedModelsModels to outputToModelsMap
        if(this.reduceMemoryFootprint){
            Set<Vertex> outputNodes = outputToMappedModelsMap.keySet();
            outputNodes.forEach((outputNode) -> {
                outputToModelsMap.put(outputNode, outputToMappedModelsMap.get(outputNode).getMappedModels());
            });
        }
    }
    
    private LinkedList<Vertex> getInputNodes(String str){
        LinkedList<Vertex> inputNodes = new LinkedList();
        str = str.replace("[", "").replace("]", "");
        String[] strArr = str.split(", ");
        for(String strA : strArr)
            inputNodes.add(new Vertex(strA.trim()));
        return(inputNodes);
    }
    
    private LinkedList<String> getInputRules(String str) {
        LinkedList<String> rules = new LinkedList();
        str = str.replace("[", "").replace("]", "");
        String[] strArr = str.split(", ");
        int fuzzyRuleElements  = strArr.length;
        int ruleElementCount = 0;
        for(int i = 0; i < fuzzyRuleElements; i++){
            ruleElementCount++;
            if(ruleElementCount%3==0){
                int ruleStartPosition = (i+1) - 3;
                String rule = strArr[ruleStartPosition];
                for(int j = ruleStartPosition+1; j <= i; j++){
                    rule = rule + ", " + strArr[j];
                }
                rules.add(rule);
            }
        }        
        return rules;
    }

    /**
    private void updateOutputNodeToInputNodesMap() {
        for(Vertex vertex : vertices){
            if(outputToInputNodes.containsKey(vertex)==false)
                outputToInputNodes.put(vertex, new LinkedList());
        }
    }
    */ 

    private void extractDirectedAdjMatrix() {
        directedAdjMatrix = new int[vertices.size()][vertices.size()];
        for(int i = 0; i < vertices.size(); i++){
            Vertex inputNode = vertices.get(i);                
            for(int j = 0; j < vertices.size(); j++){
                Vertex outputNode = vertices.get(j);
                //if(outputToInputNodes.get(outputNode).contains(inputNode)){
                //    directedAdjMatrix[i][j] = 1;
                //}   
                LinkedList<Model> mappedNodeInputs = outputToModelsMap.get(outputNode);
                if(mappedNodeInputs != null){
                    Collections.sort(mappedNodeInputs);
                    LinkedList<Vertex> inputNodes = mappedNodeInputs.getLast().getInputNodes();
                    if(inputNodes.contains(inputNode))
                        directedAdjMatrix[i][j] = 1;
                }
            }
        }
    }
    
    public Matrix getAdjMatrix() {
        Vertex[] nodesArr = vertices.toArray(new Vertex[vertices.size()]);
        return new Matrix(nodesArr, nodesArr, directedAdjMatrix);
    }
    
    public void printEdges(String outputFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outputFile);
        printer.println("From\tTo\tRule\tHashCode\tWeight");        
        Set<Integer> edgeIds  = edgeIdToMappedEdges.keySet();
        for(int edgeId : edgeIds){
            LinkedList<Edge> mappedEdges = edgeIdToMappedEdges.get(edgeId);
            //print the collection of edge(s) with the maximum weight (best fit)...
            for(Edge edge : mappedEdges){
                printer.println(edge.getOrigin().getId() + "\t" +
                        edge.getDestination().getId() + "\t" +
                        edge.getRule() + "\t" +
                        edge.hashCode() + "\t" +
                        edge.getWeight());
            }
        }        
        printer.close();
    }
    
    public void printEdges2(String outputFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outputFile + 2); // _EdgesOutputFile.edg2
        printer.println("From\tTo\tRule\tWeight");        
        Set<Vertex> outputs = outputToModelsMap.keySet();
        outputs.forEach((output) -> {
            LinkedList<Model> mappedModels = outputToModelsMap.get(output);
            Collections.sort(mappedModels); // sort bestFitModel bestFitModel ascending order of fit...
            
            Model bestFitModel = mappedModels.getLast();// get the bestFitModel with the best fit...
            
            if(this.penalizeNullRules){
                //iterate from the last model (best fitted model) till you have a model without null rules...
                boolean foundBestFit = false;
                int modelIndex = mappedModels.size() - 1;
                
                while(!foundBestFit && (modelIndex >= 0)){
                    Model model = mappedModels.get(modelIndex);
                    LinkedList<String> modelRules = model.getRules();
                    boolean containsNullRule = false;
                    checkRules:
                    for(int i = 0; i < modelRules.size(); i++){
                        //iterate thru the rules and ensure non is a null rule
                        if(this.nullRules.contains(modelRules.get(i))){
                            containsNullRule = true;
                            break checkRules; 
                        }                        
                    }
                    if(containsNullRule){
                       modelIndex--;
                    }else{
                        foundBestFit = true;
                        bestFitModel = model;
                    }
                }
            }
            
            LinkedList<Vertex> inputsNodes = bestFitModel.getInputNodes();
            //inputs.forEach((input) -> {
            for(int i = 0; i < inputsNodes.size(); i++){
                printer.println(inputsNodes.get(i).getId() + "\t" +
                                output.getId() + "\t" +
                                bestFitModel.getRules().get(i) + "\t" +
                                bestFitModel.getFit());
            }
        });
        printer.close();  
    }
    
    public void printBestFitModels(String outputFile) throws FileNotFoundException{
        // how is this different from an edge(s) table....we could describe with filename _OutputFile.fit
        PrintWriter printer = new PrintWriter(outputFile);
        printer.println("Output\tNumberOfFittedModels\tInputNodes(BestFit)\tRules\tFit");
        Set<Vertex> outputs = outputToModelsMap.keySet();
        
        outputs.forEach((output) -> {
        //for(Vertex output : outputs){
            
            LinkedList<Model> mappedModels = outputToModelsMap.get(output);
            Collections.sort(mappedModels); // sort mapped models in ascending order           
            
            Model bestFitModel = mappedModels.getLast(); 
            
            if(this.penalizeNullRules){
                //iterate from the last model (best fitted model) till you have a model without null rules...
                boolean foundBestFit = false;
                int modelIndex = mappedModels.size() - 1;
                
                while(!foundBestFit && (modelIndex >= 0)){
                    Model model = mappedModels.get(modelIndex);
                    LinkedList<String> modelRules = model.getRules();
                    boolean containsNullRule = false;
                    checkRules:
                    for(int i = 0; i < modelRules.size(); i++){
                        //iterate thru the rules and ensure non is a null rule
                        if(this.nullRules.contains(modelRules.get(i))){
                            containsNullRule = true;
                            break checkRules; 
                        }                        
                    }
                    if(containsNullRule){
                       modelIndex--;
                    }else{
                        foundBestFit = true;
                        bestFitModel = model;
                    }
                }
            }
                       
            printer.println(output.getId() + "\t" +
                            mappedModels.size() + "\t" +
                            bestFitModel.getInputNodesString() + "\t" +
                            bestFitModel.getRulesString() + "\t" +
                            bestFitModel.getFit());
        //}
        });
        printer.close();       
    }
    
    public void printAllFittedModels(String outputFile, int topFittedModelsToOutput) throws FileNotFoundException{
        // how is this different from an edge(s) table....we could describe with filename _OutputFile.fit2
        PrintWriter printer = new PrintWriter(outputFile+2);
        printer.println("Output\tInputNodes\tRules\tFits");
        Set<Vertex> outputs = outputToModelsMap.keySet();
        for(Vertex output : outputs){
            LinkedList<Model> mappedModels = outputToModelsMap.get(output);
            Collections.sort(mappedModels);
            //Model in = mappedModels.getLast();
            //printer.println(output.getId() + "\t" +
            //                mappedModels.size() + "\t" + 
            //                this.getInputNodesString(mappedModels) + "\t" +
            //                this.getRulesString(mappedModels) + "\t" +
            //                this.getFitsString(mappedModels));
            //for(int i = mappedModels.size()-1; i >= 0; i--){
            if(mappedModels.size() <= topFittedModelsToOutput){
                for(int i = mappedModels.size()-1; i >= 0; i--){
                    Model fittedModel = mappedModels.get(i);
                    printer.println(output.getId() + "\t" +
                                    fittedModel.getInputNodesString() + "\t" +
                                    fittedModel.getRulesString() + "\t" +
                                    fittedModel.getFit());
                }
                
            }else{
                for(int i = mappedModels.size()-1; i >= (mappedModels.size() - topFittedModelsToOutput); i--){
                    Model fittedModel = mappedModels.get(i);
                    printer.println(output.getId() + "\t" +
                                    fittedModel.getInputNodesString() + "\t" +
                                    fittedModel.getRulesString() + "\t" +
                                    fittedModel.getFit());
                }
            }
        }
        printer.close();  
    }
    
    public void printRuleFrequencies(String outputFile) throws FileNotFoundException{
        // how is this different from an edge(s) table....we could describe with filename _OutputFile.freq
        PrintWriter printer = new PrintWriter(outputFile);
        Set<String> rules = ruleFrequencies.keySet();
        printer.println("Rule\tFrequency");
        rules.forEach((rule) -> {
            printer.println(rule + "\t" + ruleFrequencies.get(rule));
        });
        printer.close();
        
    }
    
}
