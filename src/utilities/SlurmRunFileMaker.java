/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import tables.Table;

/**
 *
 * @author aiyetanpo
 * Slurm...Maker makes srun/sbatch input files for each output Node...
 */
public class SlurmRunFileMaker {
    
    private void makeJConfigFile(String jconfigFilePath, 
                                    String inputFileAbsolutePath, 
                                    String runJFuzzyDir, 
                                    int numberOfInputs,
                                    double fitCutOff,
                                    int start, int end, 
                                    HashMap<String, String> params) throws FileNotFoundException {
                                // throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        //---- jFuzzyMachine Configuration --------
        //### In this implementation, the jFuzzy config file is associated with 
        //###   each outputted slurm file run generated. The following configurations
        //###   are simply copied into the associated .jconfig file
        //#inputFile=./etc/proj_diss/_1.5mZ/rnaseqiExpMat_1.5mZ.txt
        //#outputDir=./etc/proj_diss/_1.5mZ/runJFuzzy
        //maxNumberOfInputs=-1
        //#numberOfInputs=1 # shall depend on the number of inputs (1, 2, ... numberOfInputs) being considered by slurm maker
        //outputInRealtime=TRUE
        //#eCutOff=0.500 # be determined by specified 'fitCutOff 
        //useAllGenesAsOutput=FALSE
        //#iGeneStart=1
        //#iGeneEnd=1 #shall depend on the number of features in exprs matrix being considered...
        //### NOTE: in this implementation, if the numberOfInputs is 1 or 2, start (iGeneStart) shall be 1, 
        //###       and end (iGeneEnd) shall be the last feature index in the exprs matrix 
        //useParallel=TRUE
        //modelPhenotype=FALSE
        //inputPhenoFile=./etc/projects/dissertation/inputs/cASPPhenoTable.txt

        PrintWriter printer = new PrintWriter(jconfigFilePath);
        printer.println("###---- jFuzzyMachine Configuration ----###");
        printer.println("inputFile=" + inputFileAbsolutePath);
        printer.println("outputDir=" + runJFuzzyDir);
        printer.println("maxNumberOfInputs=" + params.get("maxNumberOfInputs"));
        printer.println("numberOfInputs=" + numberOfInputs);
        printer.println("outputInRealtime=" + params.get("outputInRealtime"));
        printer.println("eCutOff=" + fitCutOff);
        printer.println("useAllGenesAsOutput=" + params.get("useAllGenesAsOutput"));
        printer.println("iGeneStart=" + start);
        printer.println("iGeneEnd=" + end);
        printer.println("useParallel=" + params.get("useParallel"));
        printer.println("modelPhenotype=" + params.get("modelPhenotype"));
        printer.println("inputPhenoFile=" + params.get("inputPhenoFile"));
        
        printer.close();
        
    }

    
    @SuppressWarnings("ConvertToTryWithResources")
    public void makeSlurmRunFile(String outputFileName, // slurm batch file...
                         int start, // start gene or feature...
                         int end, // end gene or feature...
                         int numberOfInputs,
                         double fitCutOff,
                         String jconfigFilePath,
                         HashMap<String, String> params
                        ) throws FileNotFoundException{
        
        PrintWriter printer = new PrintWriter(outputFileName);
        printer.println("#!/bin/bash");
        printer.println();
        printer.println("#SBATCH --partition=" + params.get("partition"));
        printer.println("#SBATCH --job-name=" + params.get("jobNamePrepend") + 
                "." + start + "." + end + "." + numberOfInputs + "  # Job name");
        printer.println("#SBATCH --mail-type=" + params.get("mailType") + "          # Mail events (NONE, BEGIN, END, FAIL, ALL)");
        printer.println("#SBATCH --mail-user=" + params.get("mailUser") + "          # Where to send mail");
        printer.println("#SBATCH --output=" + params.get("slurmLogOuputDir") + 
                        "/" + params.get("jobNamePrepend") + "." + 
                        start + "." + end + "." + numberOfInputs + ".log   # Standard output and error log");
        //printer.println("#SBATCH --mem=16000						   # memory per compute node in MB");
        //printer.println("#SBATCH --nodes=16-32						   # nodes per compute node in MB");
        printer.println("#SBATCH --cpus-per-task=" + params.get("cPUsPerTask"));
        printer.println("#SBATCH --mem-per-cpu=" + params.get("memPerCPU"));
        printer.println();        
        printer.println("pwd; hostname; date");
        printer.println();
        printer.println("echo \"Running JFuzzyMachine program on $SLURM_JOB_NUM_NODES nodes with $SLURM_NTASKS tasks, each with $SLURM_CPUS_PER_TASK cores.\"");
        printer.println();    
        printer.println("# ======================== #");
        printer.println("# Start  Executables...");
        printer.println("# ======================== #");
        //printer.println("export WORKDIR=\"/scratch/cluster_tmp/aiyetanpo/Applications/Personal/JFuzzyMachine/20191203/\"");
        printer.println("export WORKDIR=\"" + params.get("slurmWorkingDir") + "/\"");
        printer.println("echo \"Program Output begins: \"");
        printer.println("cd $WORKDIR ## cd into working directory...");
        printer.println();
        printer.println("startdate=$(date '+%m/%d/%Y %H:%M:%S')");
        printer.println("out=\"startdate = \"$startdate");
        printer.println("echo $out"); 
        printer.println("echo \"..in $WORKDIR\"");
        printer.println();
        printer.println("# ======================== #");
        printer.println("# run program script(s) here");
        printer.println("# ======================== #");
        printer.println("# define needed program(s) locations");
        printer.println();
        //printer.println("java -Xmx16G -cp ./JFuzzyMachine.jar jfuzzymachine.JFuzzyMachine ./JFuzzy.config " + 
        //                start + " " + end + " " + numberOfInputs + " " + fitCutOff);
        printer.println("java -Xmx" + (Integer.parseInt(params.get("memPerCPU").replace("G", "")) - 1) + 
                        "G -cp ./JFuzzyMachine.jar jfuzzymachine.JFuzzyMachine " + jconfigFilePath + " " + 
                        start + " " + end + " " + numberOfInputs + " " + fitCutOff);
        printer.println();
        printer.println("# ======================== #");
        printer.println("# tidy up when done here...");
        printer.println("# =======================+ #");
        printer.println("enddate=$(date '+%m/%d/%Y %H:%M:%S');");
        printer.println("newout=\"enddate = \"$enddate");
        printer.println("echo $newout"); 

        printer.close();
        
    }
    
    private void makeFiles(HashMap<String, String> params) throws IOException {
        // throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        String outputDir = params.get("outputDir"); // output directory for current project task
        String inputDir = params.get("inputDir"); // input(s) directory for current project task 
                       // =./etc/projects/dissertation/inputs/expMat")
        // get exprs matrix files in inputDir...
        File[] inputFiles = new File(inputDir).listFiles();
        
        // definitions
        String slurmscriptFilePath;
        String jconfigFilePath;
        int start; 
        int end;
        int numberOfInputs = Integer.parseInt(params.get("numberOfInputs"));
        double fitCutOff = Double.parseDouble(params.get("fitCutOff"));
        long startTime = new Date().getTime();
        
        // list of slurm run files... (for creating the batch run file)...
        LinkedList<String> slurmRunFiles = new LinkedList();
        
        
        for (File inputFile : inputFiles) {
            //create an output subdirectory to be associated with this file in the outputDir
            String inputFilename = inputFile.getName().replace(".txt", "");
            String outputFilesDir = outputDir + File.separator + inputFilename;
            new File(outputFilesDir).mkdir(); // make subdirectory...
            //create subsubdirectories: /runJFuzzy and /slurmscripts...
            String runJFuzzyDir = outputFilesDir + File.separator + "runJFuzzy";
            String slurmscriptsDir = outputFilesDir + File.separator + "slurmscripts";
            new File(runJFuzzyDir).mkdir(); 
            new File(slurmscriptsDir).mkdir(); 
            //extract exprs matrix related information...
            Table exprs = new Table(inputFile.getAbsolutePath());
            int numberOfFeatures = exprs.getRowIds().length;
            
            //get number of inputs from params
            
            for(int j = 1; j <= numberOfInputs; j++){
                if(j > 2){
                    for( int k = 1; k <= numberOfFeatures; k++ ){
                        start = k;
                        end = k;
                        // create a slurm output log file unique prepend identifier
                        // create a single slurmscript and config file for these (j = {1, 2})...
                        // create a .jconfig file path
                        // create a .jconfig file...
                        String jobNamePrependId = startTime + "." + inputFilename + ".runFuzzy";
                        params.put("jobNamePrepend", jobNamePrependId);
                        slurmscriptFilePath = slurmscriptsDir + File.separator + 
                                startTime + "." + inputFilename + "." + 
                                start + "." + end + "." + j + ".sh";
                        jconfigFilePath = slurmscriptsDir + File.separator + 
                                startTime + "." + inputFilename + "." + 
                                start + "." + end + "." + j + ".jconfig";
                        makeJConfigFile(jconfigFilePath, inputFile.getPath(),
                                runJFuzzyDir, j, fitCutOff,
                                start, end, params);
                        makeSlurmRunFile(slurmscriptFilePath, start, end, j, fitCutOff, jconfigFilePath, params);

                        String currDir = System.getProperty("user.dir");
                        slurmRunFiles.add(slurmscriptFilePath.replace(currDir, "."));
                    }
                    
                }else{
                    start = 1;
                    end = numberOfFeatures;
                    // create a slurm output log file unique prepend identifier
                    // create a single slurmscript and config file for these (j = {1, 2})...
                    // create a .jconfig file path
                    // create a .jconfig file...
                    String jobNamePrependId = startTime + "." + inputFilename + ".runFuzzy";
                    params.put("jobNamePrepend", jobNamePrependId);
                    slurmscriptFilePath = slurmscriptsDir + File.separator +  
                            startTime + "." + inputFilename + "." + 
                            start + "." + end + "." + j + ".sh";
                    jconfigFilePath = slurmscriptsDir + File.separator + 
                            startTime + "." + inputFilename + "." + 
                            start + "." + end + "." + j + ".jconfig";
                    makeJConfigFile(jconfigFilePath, inputFile.getPath(),
                            runJFuzzyDir, j, fitCutOff,
                            start, end, params);
                    makeSlurmRunFile(slurmscriptFilePath, start, end, j, fitCutOff, jconfigFilePath, params);
                    
                    String currDir = System.getProperty("user.dir");
                    slurmRunFiles.add(slurmscriptFilePath.replace(currDir, "."));
                }
            }
        }
        
        //make parent SLURM run file...
        String slurmRunParentFile = System.getProperty("user.dir") + File.separator + "slurmRuns.sh";
        PrintWriter pr = new PrintWriter(slurmRunParentFile);
        slurmRunFiles.forEach((slurmRunFile) -> {
            pr.println("sbatch " + slurmRunFile);
        });
        pr.close();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        
        HashMap<String, String> params = new ConfigFileReader().read(args[0]);
        SlurmRunFileMaker maker = new SlurmRunFileMaker();
        maker.makeFiles(params);
        
        /*
        String outputDirectory = params.get("outputDir"); // output directory
        int start = Integer.parseInt(params.get("start"));// start gene or feature...
        int end = Integer.parseInt(params.get("end")); // end gene or feature...
        int numberOfInputs = Integer.parseInt(params.get("numberOfInputs"));
        double fitCutOff = Double.parseDouble(params.get("fitCutOff"));
        
        SlurmRunFileMaker fileMaker = new SlurmRunFileMaker();
               
        for(int i = start; i <= end; i++){
            String outputFile = outputDirectory + File.separator + 
                    "runFuzzyMachine." + i + "." + i + "." + numberOfInputs + ".sh";
            fileMaker.makeSlurmRunFile(outputFile, i, i, 
                               numberOfInputs, fitCutOff, params);
        }
        */
       
        System.out.println("...Done!");
    }

    
    
    
}
