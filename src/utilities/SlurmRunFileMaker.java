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
import java.util.HashMap;

/**
 *
 * @author aiyetanpo
 * Slurm...Maker makes srun/sbatch input files for each output Node...
 */
public class SlurmRunFileMaker {
    
    @SuppressWarnings("ConvertToTryWithResources")
    public void makeFile(String outputFileName, // slurm batch file...
                         int start, // start gene or feature...
                         int end, // end gene or feature...
                         int numberOfInputs,
                         double fitCutOff,
                         HashMap<String, String> params
                        ) throws FileNotFoundException{
        
        PrintWriter printer = new PrintWriter(outputFileName);
        printer.println("#!/bin/bash");
        printer.println();
        printer.println("#SBATCH --partition=" + params.get("partition"));
        printer.println("#SBATCH --job-name=" + params.get("jobNamePrepend") + 
                "." + start + "." + end + "." + numberOfInputs + "  # Job name");
        printer.println("#SBATCH --mail-type=" + params.get("mailType") + "                                           # Mail events (NONE, BEGIN, END, FAIL, ALL)");
        printer.println("#SBATCH --mail-user=" + params.get("mailUser") + "                          # Where to send mail	");
        printer.println("#SBATCH --output=" + params.get("slurmLogOuputDir") + 
                "/" + params.get("jobNamePrepend")+"."+start+"."+end+"."+numberOfInputs+".log   # Standard output and error log");
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
        printer.println("java -Xmx8G -cp ./JFuzzyMachine.jar jfuzzymachine.JFuzzyMachine ./JFuzzyMachine.config " + 
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
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        
        HashMap<String, String> params = new ConfigFileReader().read(args[0]);
        
        String outputDirectory = params.get("outputDir"); //args[0], output directory
        int start = Integer.parseInt(params.get("start"));// start gene or feature...
        int end = Integer.parseInt(params.get("end")); // end gene or feature...
        int numberOfInputs = Integer.parseInt(params.get("numberOfInputs"));
        double fitCutOff = Double.parseDouble(params.get("fitCutOff"));
        
        SlurmRunFileMaker fileMaker = new SlurmRunFileMaker();
               
        for(int i = start; i <= end; i++){
            String outputFile = outputDirectory + File.separator + 
                    "runFuzzyMachine." + i + "." + i + "." + numberOfInputs + ".sh";
            fileMaker.makeFile(outputFile, i, i, 
                               numberOfInputs, fitCutOff, params);
        }
        
        System.out.println("...Done!");
    }
    
}
