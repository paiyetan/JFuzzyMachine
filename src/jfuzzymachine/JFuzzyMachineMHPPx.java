/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import jfuzzymachine.exceptions.TableBindingException;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.ConfigFileReader;
import jfuzzymachine.utilities.Evaluator;
import jfuzzymachine.utilities.SlurmRunFileMaker;
import jfuzzymachine.utilities.graph.Graph;
import jfuzzymachine.utilities.sysconnect.SystemCaller;

/**
 *
 * @author paiyetan
 */
public class JFuzzyMachineMHPPx {
    
    private HashMap<String, String> config;
    
    public JFuzzyMachineMHPPx(String c) throws IOException{        
        //ConfigFileReader cReader = new ConfigFileReader();
        config = ConfigFileReader.read(c); // configuration file path            
    }
    
    public HashMap<String, String> getConfig(){
        return config;
    }
       
    private synchronized void monitor() throws FileNotFoundException, IOException, InterruptedException {
        
        //monitor execution of provious initiated jFuzzyMachine executions....
        // 1. get the number of expected .jFuzz output files...
        //     - this would be equal to the number of sbatch processes in the 'slurmruns.sh" file in working directory...
        int processes = 0;
        LinkedList<String> slurmscriptFiles = new LinkedList();
        String slurmBatchRunsFile = System.getProperty("user.dir") + File.separator + "slurmruns.sh";
        
        
        BufferedReader reader = new BufferedReader(new FileReader(slurmBatchRunsFile));
        String line;
        while((line = reader.readLine())!= null){
            if(line.startsWith("sbatch")){
                processes++;
                String slurmscriptFile = line.replace("sbatch ", "");
                slurmscriptFiles.add(slurmscriptFile);
            }
        }
        reader.close();
        System.out.println("[" + new Date().toString() + "]:Identified " + processes + " processes to monitor");
        

        // 2. get the output directory where jfuzzymachine places it's .jfuz output files...
        //    - since this extends the MHPP plug, output directory would be as formulated in the "jfuzzymachine.utilities.SlurmFileMaker"
        String outputDir = config.get("outputDir");
        String input = config.get("input");
        File inputFile = new File(input);
        String inputFilename = inputFile.getName().replace(".txt", "");
        String outputFilesDir = outputDir + File.separator + inputFilename;
        String runJFuzzyDir = outputFilesDir + File.separator + "runJFuzzy";
        // alternatively,
        /*
        String slurmscriptFile = slurmscriptFiles.getFirst();
        String slurmscriptDir = new File(slurmscriptFile).getParent();
        runJFuzzyDir = slurmscriptDir.replace("slurmscripts","runJFuzzy");
        */        
        config.put("runJFuzzyDir", runJFuzzyDir);
        
        // b. get the output .jfuz file associated with each slurmscriptFile..
        LinkedList<String> jfuzFiles = new LinkedList();
        for(String slurmscriptFile : slurmscriptFiles){
            String slurmscriptDir = new File(slurmscriptFile).getParent();
            String slurmscriptFilename = new File(slurmscriptFile).getName();
            runJFuzzyDir = slurmscriptDir.replace("slurmscripts","runJFuzzy"); 
            String prefix = slurmscriptFilename.split("\\.")[0];
            String basename = slurmscriptFilename.replace(prefix+".", "").replace("sh", "");
            String jfuzFile = runJFuzzyDir + File.separator + basename + config.get("useParallel") + ".jfuz";
            jfuzFiles.add(jfuzFile);
        }
        File[] jfuzzFiles = new File[jfuzFiles.size()];
        for(int i=0; i < jfuzFiles.size(); i++){
            jfuzzFiles[i] = new File(jfuzFiles.get(i));
        }
        
               
        // 3. check for completion...
        boolean completed = false;
        long itr = 0;
        while(!completed){ // while sbatch processes are not completed...
            itr++;
            if((itr % 100) == 0){
                    System.out.println("[" + new Date().toString() + "]:...Monitoring, all .jfuz processes still incomplete");
            } 
            //Runtime.getRuntime().wait(5000); // wait a little bit...
            wait(5000); //wait for five seconds (5000 milliseconds)...
            //jfuzzFiles = new File(runJFuzzyDir).listFiles(new FileFilter() {
            //                                                            @Override
            //                                                            public boolean accept(File pathname) {
            //                                                                return pathname.getName().endsWith(".jfuz");
            //                                                            } //ensures only .jfuz files are selected...
            //                                                        });
            // check if all the associated .jfuz files have all  been initiated or started, a situation that
            // may arise because the slurm process has not been initiated
            if((itr % 25) == 0){
                System.out.println("[" + new Date().toString() + "]:...Checking if all .jfuz processes have began");
            }            
            boolean allInitiated = true;
            for(File jfuzzFile : jfuzzFiles){
                if(!jfuzzFile.exists()){
                    allInitiated = false;
                    break;
                }
            }
            
            if(allInitiated){               
                System.out.println("[" + new Date().toString() + "]: All batched run .jfuz processes have began...");
               
                // check that all outputted .jfuz files have the last line entry...
                int filesWithCompletedExec = 0;
                for(File jfuzzFile : jfuzzFiles){
                    reader = new BufferedReader(new FileReader(jfuzzFile));
                    while((line = reader.readLine())!= null){
                        if(line.startsWith("> End Search")){
                            filesWithCompletedExec++;
                        }
                    }
                    reader.close();
                }
                
                if(filesWithCompletedExec == processes){
                    completed = true;
                    break;
                }                                
            }           
        }
    }
    
    
    public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException, TableBindingException, Throwable{
        
        Date start = new Date();
        System.out.println("[" + start.toString() + "]:Starting JFuzzyMachineMHPPx...");       
        long start_time = start.getTime();

        JFuzzyMachineMHPPx jFuzzMachMHPPx = new JFuzzyMachineMHPPx(args[0]); // mphh config file
        
        SlurmRunFileMaker sFMaker = new SlurmRunFileMaker();        
        sFMaker.makeFiles(jFuzzMachMHPPx.getConfig());
        String slurmRunParentFile = sFMaker.getSlurmRunParentFile();

        SystemCaller syscaller = new SystemCaller();
        String command;
        //chmod
        command = "chmod 777 "  + slurmRunParentFile;
        syscaller.execute(command); // change file mode to executable        
        command = "sbatch " + slurmRunParentFile;
        syscaller.execute(command); // execute the sbatch call...

        System.out.println("[" + new Date().toString() + "]:Monitoring first batched JFuzzyMachine(s) processes...");
        jFuzzMachMHPPx.monitor();//monitoring...
        
        //jFuzzMachMHPPx.monitor();//monitoring...        
        System.out.println("[" + new Date().toString() + "]:Batched jfuz processes are completed..."); //if completed
        
        
        System.out.println("[" + new Date().toString() + "]:Running initial post-processing...");
        String graphConfigFilePath = args[1]; // graph config files..
        HashMap<String, String> gConfig = ConfigFileReader.read(graphConfigFilePath);       
        gConfig.replace("input", jFuzzMachMHPPx.getConfig().get("runJFuzzyDir")); //update the output (input to Graph) parameter
        Graph graph = new Graph(gConfig);
        
        // get the regulon map using a call to the Rscript..
        String graphOutputsDir = graph.getOutputsDir(); //inputDir to regulon extracting script.
        String rCMD = "Rscript ." + File.separator + 
                      "src" + File.separator + 
                      "rJFuzzyMachineRegulons.R " +
                      "-i " + graphOutputsDir + 
                      " -t " + graphOutputsDir +
                      " -x oneTwoThreeInputs.";
        // "Rscript path-to-rscript.R -i inputFir -o outputTextDir -p prefixText"
        SystemCaller rcaller = new SystemCaller();
        rcaller.execute(rCMD);
                
        /////////////////////////////////////////////////////////
        System.out.println("[" + new Date().toString() + "]:Running JfuzzyMachine for 4 or more regulatory input nodes...");
        String jConfigFilePath = args[2];
        HashMap<String, String> jConfig = ConfigFileReader.read(jConfigFilePath);  
        // re-modify the parameters: 
        // outputDir, maxNumberOfInputs, numberOfInputs, useProbableRegulonsMap, regulonsMapFile
        jConfig.replace("outputDir", jFuzzMachMHPPx.getConfig().get("runJFuzzyDir"));
        jConfig.replace("maxNumberOfInputs","-1");
        jConfig.replace("useProbableRegulonsMap","TRUE");
        // re-compose rCaller output file (regulonMap)
        String regulonsMapFile = graphOutputsDir + File.separator + 
                                    "oneTwoThreeInputs.topProbableRegulonsMap.txt";
        jConfig.replace("regulonsMapFile", regulonsMapFile);    
        
        
        if(Boolean.parseBoolean(jFuzzMachMHPPx.getConfig().get("useMHPPxx"))){
            
            System.out.println("[" + new Date().toString() + "]:Using the MHPPxx approach...");       
            // call slurm run file maker,
            // modify input config file..
            // make slurm files,
            // run the slurm batch
            // monitor runs
            sFMaker = new SlurmRunFileMaker();
            jFuzzMachMHPPx.getConfig().replace("numberOfInputs", "4");
            jFuzzMachMHPPx.getConfig().replace("allInputsToNumberOfInputs", "FALSE");
            //jFuzzMachMHPPx.getConfig().replace("", "");
            
            sFMaker.makeFiles(jFuzzMachMHPPx.getConfig());
            slurmRunParentFile = sFMaker.getSlurmRunParentFile();
            
            syscaller = new SystemCaller();
            //String command;
            
            //chmod
            command = "chmod 777 "  + slurmRunParentFile;
            syscaller.execute(command); // change file mode to executable        
            command = "sbatch " + slurmRunParentFile;
            syscaller.execute(command); // execute the sbatch call...
            
            System.out.println("[" + new Date().toString() + "]:Monitoring batched jfuz processes...");
            jFuzzMachMHPPx.monitor();//monitoring...
            
        }else{       
            // run jFuzzyMachine for 4 regulatory inputs...
            System.out.println("Running JfuzzyMachine for 4 input nodes...");
            jConfig.replace("numberOfInputs","4");
            JFuzzyMachine jfuzzy = null;
            if(Boolean.parseBoolean(jFuzzMachMHPPx.getConfig().get("modelPhenotype"))){
                jConfig.replace("iGeneStart","0");
                jConfig.replace("iGeneEnd","0");
                jConfig.replace("modelPhenotype","TRUE");
                jfuzzy = new JFuzzyMachine(jConfig); //run jFuzzy with modeling for phenotype first, then

                jConfig.replace("iGeneStart","1");// first row...
                jConfig.replace("iGeneEnd",String.valueOf(new Table(jConfig.get("inputFile")).getRowIds().length));
                jConfig.replace("modelPhenotype","FALSE"); // replace attribute 
                jfuzzy = new JFuzzyMachine(jConfig); // and run without considering to model phenotype...

            }else{
                jConfig.replace("iGeneStart","1");// first row...
                jConfig.replace("iGeneEnd",String.valueOf(new Table(jConfig.get("inputFile")).getRowIds().length));
                jConfig.replace("modelPhenotype","FALSE"); // replace attribute 
                jfuzzy = new JFuzzyMachine(jConfig); // and run without considering to model phenotype...
            }

            jfuzzy.finalize();
            //new JFuzzyMachine(jConfig);

            /**
             * 
            System.out.println("Running JfuzzyMachine for 5 regulatory nodes...");
            jConfig.replace("numberOfInputs","5");
            // run jFuzzyMachine for 5 regulatory inputs...
            JFuzzyMachine jfuzzy2 = new JFuzzyMachine(jConfig);
            jfuzzy2.finalize();


            // run jFuzzyMachine for 5 regulatory inputs...
            System.out.println("\nRunning JfuzzyMachine for 5 input nodes...");
            jConfig.replace("numberOfInputs","5");
            JFuzzyMachine jfuzzy2 = null;
            if(Boolean.parseBoolean(jFuzzMachMHPPx.getConfig().get("modelPhenotype"))){
                jConfig.replace("iGeneStart","0");
                jConfig.replace("iGeneEnd","0");
                jConfig.replace("modelPhenotype","TRUE");
                jfuzzy2 = new JFuzzyMachine(jConfig); //run jFuzzy with modeling for phenotype first, then

                jConfig.replace("iGeneStart","1");// first row...
                jConfig.replace("iGeneEnd",String.valueOf(new Table(jConfig.get("inputFile")).getRowIds().length));
                jConfig.replace("modelPhenotype","FALSE"); // replace attribute 
                jfuzzy2 = new JFuzzyMachine(jConfig); // and run without considering to model phenotype...

            }else{
                jConfig.replace("iGeneStart","1");// first row...
                jConfig.replace("iGeneEnd",String.valueOf(new Table(jConfig.get("inputFile")).getRowIds().length));
                jConfig.replace("modelPhenotype","FALSE"); // replace attribute 
                jfuzzy2 = new JFuzzyMachine(jConfig); // and run without considering to model phenotype...
            }

            jfuzzy2.finalize();
            *
            */ 
        }
        
        System.out.println("[" + new Date().toString() + "]:Re-running post-processing...");
        graph = new Graph(gConfig);
        
        System.out.println("[" + new Date().toString() + "]:Running evaluation...");
        String eConfigFilePath = args[3];
        HashMap<String, String> eConfig = ConfigFileReader.read(eConfigFilePath);  
        // modify the parameter: fitFile (as formulated in the Graph class)
        String fittedModelFile = graphOutputsDir + File.separator + 
                                    gConfig.get("runId") + 
                                        "_runJFuzzUtils.fit";
        eConfig.replace("fitFile", fittedModelFile);
        Evaluator eval = new Evaluator(eConfig);
        
        
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
    
    
    
    
    
    
    
    
    
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
              .forEach(consumer);
        }
    }
     
}
