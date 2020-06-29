/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.simulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author aiyetanpo
 */
public class Table {

    
            
    private String[] rowIds;
    private String[] columnIds;
    private String[][] matrix;
    
    private double[][] dMatrix;
    private float[][] fMatrix;

    //private void getUnkownMethod() {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    
    public enum TableType {INTEGER, DOUBLE, FLOAT};
    public enum BindType {COLUMN, ROW};
        
    public Table(String inFilePath) throws IOException{
        read(inFilePath);
    }

    public Table(String inFilePath, Table.TableType type) throws IOException{
        read(inFilePath);
        switch(type){
            case DOUBLE:
               dMatrix = new double[rowIds.length][columnIds.length];
               for(int i = 0; i < rowIds.length; i++){
                   for(int j = 0; j < columnIds.length; j++){
                       dMatrix[i][j] = Double.parseDouble(matrix[i][j]);
                   }
               }
               break;
            case FLOAT:
                fMatrix = new float[rowIds.length][columnIds.length];
               for(int i = 0; i < rowIds.length; i++){
                   for(int j = 0; j < columnIds.length; j++){
                       fMatrix[i][j] = Float.parseFloat(matrix[i][j]);
                   }
               }
               break;
                
            case INTEGER:
                //yet to be implemented...
                break;
            default:
                break;
        }
    }

    
    public Table(String[] rowIds, 
                 String[] columnIds, 
                 String[][] matrix) {
        this.rowIds = rowIds;
        this.columnIds = columnIds;
        this.matrix = matrix;
    }
    
    public Table(String[] rowIds,
                 String[] columnIds,
                 double[][] matrix){
        this.rowIds = rowIds;
        this.columnIds = columnIds;
        this.dMatrix = matrix;
    }
  
    public String[] getColumnIds() {
        return columnIds;
    }

    public String[] getRowIds() {
        return rowIds;
    }

    public String[][] getMatrix() {
        return matrix;
    }
    
    public double[][] getDoubleMatrix() {
        return dMatrix;
    }
    
    public float[][] getFloatMatrix() {
        return fMatrix;
    }
    
    public int getNumberOfRows(){
        return this.getRowIds().length;
    }
    
    public int getNumberOfColumns(){
        return this.getColumnIds().length;
    }
        
    public int getRowIndex(String rowId){
        int index = -1;
        for (int i = 0; i < rowIds.length; i++){
            if (rowId.equalsIgnoreCase(rowIds[i])){
                index = i;
                break;
            }
        }
        return index;
    }
    
    public int getColumnIndex(String columnId){
        int index = -1;
        for (int i = 0; i < columnIds.length; i++){
            if (columnId.equalsIgnoreCase(columnIds[i])){
                index = i;
                break;
            }
        }
        return index;
    }
    
    
    public String[] getRow(int index){
        String[] indexedRow = new String[columnIds.length];
        for(int i=0; i < indexedRow.length; i++){
            indexedRow[i] = matrix[index][i];
        }
        return indexedRow;
    }
    
    public String[] getColumn(int index){
        String[] indexedColumn = new String[rowIds.length];
        for(int i=0; i < indexedColumn.length; i++){
            indexedColumn[i] = matrix[i][index];
        }
        return indexedColumn;
    }
    
    public double[] getRow(int index, Table.TableType type){
        double[] indexedRow = new double[columnIds.length];
        for(int i=0; i < indexedRow.length; i++){
            indexedRow[i] = dMatrix[index][i];
        }
        return indexedRow;
    }
    
    public double[] getColumn(int index, Table.TableType type){
        double[] indexedColumn = new double[rowIds.length];
        for(int i=0; i < indexedColumn.length; i++){
            indexedColumn[i] = dMatrix[i][index];
        }
        return indexedColumn;
    }
    
    public float[] getRowF(int index, Table.TableType type){
        float[] indexedRow = new float[columnIds.length];
        for(int i=0; i < indexedRow.length; i++){
            indexedRow[i] = fMatrix[index][i];
        }
        return indexedRow;
    }
    
    public float[] getColumnF(int index, Table.TableType type){
        float[] indexedColumn = new float[rowIds.length];
        for(int i=0; i < indexedColumn.length; i++){
            indexedColumn[i] = fMatrix[i][index];
        }
        return indexedColumn;
    }
    
    
    public String[] removeItem(String[] items, String itemToRemove){
        String[] newArr = new String[items.length - 1];
        int newArrIndex = 0;
        for(int i = 0; i < items.length; i++){
            if(!items[i].equalsIgnoreCase(itemToRemove)){
                newArr[newArrIndex] = items[i];
                newArrIndex++;
            }
        }
        return newArr;
    }
    
    public Table bind(Table table, BindType bindType) throws TableBindingException {
        Table combinedTable = null;
        String[] newTableRowIds = null;
        String[] newTableColumnIds = null;
        double[][] newTableMatrix = null;
        
        switch(bindType){
            
            case COLUMN:
                // yet-to-implement...
                throw new UnsupportedOperationException("Not supported yet.");                
                //break;
                
            default: // by ROW...               
                if(this.getNumberOfColumns() != table.getNumberOfColumns()) //first, ensure the columns are of the same length...
                    throw new TableBindingException("incompartible colums");
                // instantiate new 'combined' table
                int newTableRows = this.getNumberOfRows() + table.getNumberOfRows();
                int newTableColumns = this.getNumberOfColumns();
                newTableRowIds = new String[newTableRows];
                newTableColumnIds = this.columnIds;
                newTableMatrix = new double[newTableRows][newTableColumns];
                // populate new table rowIds, columnIds, and table fields...
                for(int i = 0; i < newTableRowIds.length; i++){
                    if(i < this.getNumberOfRows())
                        newTableRowIds[i] = this.getRowIds()[i];
                    else
                        newTableRowIds[i] = table.getRowIds()[newTableRows - this.getNumberOfRows() - 1];
                }
                // populate new table fields
                for(int i = 0; i < newTableRowIds.length; i++){
                    for(int j = 0; j < newTableColumnIds.length; j++){
                        if(i < this.getNumberOfRows())
                            newTableMatrix[i][j] = this.getDoubleMatrix()[i][j];
                        else
                            newTableMatrix[i][j] = table.getDoubleMatrix()[newTableRows - this.getNumberOfRows() - 1][j];
                            
                    }
                }
                                
                break;
                
        }
        combinedTable = new Table(newTableRowIds, newTableColumnIds, newTableMatrix);
        return combinedTable;
    }
    
    
    
    public void print(String outFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outFile);
        // print header...
        printer.print("Features");
        for(String columnID : columnIds){
           printer.print("\t" + columnID);
        }
        printer.print("\n");
        
        // print the body
        for(int i = 0; i < rowIds.length; i++){
            printer.print(rowIds[i]);
            for(int j = 0; j < columnIds.length; j++){
                printer.print("\t" + matrix[i][j]);
            }
            printer.print("\n");
        }
        
        printer.close();
    }
    
    
    public void print(String outFile, Table.TableType tbType) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outFile);
        // print header...
        printer.print("Features");
        for(String columnID : columnIds){
           printer.print("\t" + columnID);
        }
        printer.print("\n");
        
        // print the body
        switch(tbType){
            case DOUBLE:
                for(int i = 0; i < rowIds.length; i++){
                    printer.print(rowIds[i]);
                    for(int j = 0; j < columnIds.length; j++){
                        printer.print("\t" + dMatrix[i][j]);
                    }
                    printer.print("\n");
                }     
                break;
            default://String...
                for(int i = 0; i < rowIds.length; i++){
                    printer.print(rowIds[i]);
                    for(int j = 0; j < columnIds.length; j++){
                        printer.print("\t" + matrix[i][j]);
                    }
                    printer.print("\n");
                }                        
        }
        
        printer.close();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void read(String inFilePath) throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(inFilePath));
        String line;
        int lineIndex = 0;
        ArrayList<String> rows = new ArrayList();
        
        while((line = reader.readLine())!=null){
            lineIndex++;
            String[] lineArr;
            if(lineIndex==1){// this is the header line...
                lineArr = line.split("\t");
                columnIds = new String[lineArr.length-1];
                for(int i = 1; i < lineArr.length; i++){
                    columnIds[i-1] = lineArr[i];
                }
            }else{
                rows.add(line);
            }
        }
        
        reader.close();
        
        rowIds = new String[rows.size()];
        matrix = new String[rowIds.length][columnIds.length];
        //populate matrix...
        for(int i = 0; i < rows.size(); i++){
            String rowInfo = rows.get(i);
            String[] infoArr = rowInfo.split("\t");
            for(int j = 0; j < infoArr.length; j ++){
                try{
                    if(j == 0)
                        rowIds[i] = infoArr[j];
                    else
                    matrix[i][j-1] = infoArr[j];
                }catch(ArrayIndexOutOfBoundsException err){
                    System.out.println("rowIndex: " + i + ", colIndex: " + (j-1));
                    err.printStackTrace();
                }
            }            
        }        
    }    
}
