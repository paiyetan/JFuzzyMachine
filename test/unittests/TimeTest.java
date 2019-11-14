/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unittests;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author aiyetanpo
 */
public class TimeTest {
    
    
    public static void main(String[] args){
        
        int count = 0;
        long start = System.currentTimeMillis();
        for(int i = 1; i <= 3; i++){
            for(int j = 1; j <= 3; j++){
                for(int k = 1; k <= 3; k++){

                    for(int l = 1; l <= 3; l++){
                        for(int m = 1; m <= 3; m++){
                            for(int n = 1; n <= 3; n++){

                                for(int o = 1; o <= 3; o++){
                                    for(int p = 1; p <= 3; p++){
                                        for(int q = 1; q <= 3; q++){

                                            for(int r = 1; r <= 3; r++){
                                                for(int s = 1; s <= 3; s++){
                                                    for(int t = 1; t <= 3; t++){

                                                        for(int u = 1; u <= 3; u++){
                                                            for(int v = 1; v <= 3; v++){
                                                                for(int w = 1; w <= 3; w++){

                                                                    count++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        
        System.out.println("Total counts: " + count);   
        System.out.println("    Duration: " + TimeUnit.MILLISECONDS.toSeconds(duration) + " secs");
        System.out.println("    Duration: " + TimeUnit.MILLISECONDS.toMinutes(duration) + " mins");
        System.out.println("\n ...Done!");
        

        
    }
    
}
