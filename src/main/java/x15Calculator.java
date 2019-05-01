import java.io.*;
import java.util.*;

public class x15Calculator {

    private static void processText(String filePath, File testFolder) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<String, Integer> variableMap = new HashMap<>();
        String line , errorText ;
        errorText="";
        while ((line = br.readLine()) != null) {
            line = line.substring(0, line.indexOf(";")); //Extract the string until the ;
            line = line.replaceAll(" ", ""); // Replace all whitespaces in the expression
            errorText = "";
            String[] strArr = line.split("="); //Split the line based on the equal to sign
            if (strArr.length == 1) {  //If variable is not initialized
                    errorText = "Variable " + strArr[0] + " is not intialized";
                    break;

            }
            //Check if a number is assigned to the variable
            boolean isNumber = isNumber(strArr[1]);
            //Store the variable and its value in a hashmap
            if (isNumber) {
                variableMap.put(strArr[0], Integer.valueOf(strArr[1]));
            } else {
                variableMap.put(strArr[0], processExpression(variableMap, strArr[1]));
            }
        }
        // Call function to write the result to the file
        writeToFile(variableMap,testFolder,errorText);

    }

    private static void writeToFile(Map<String,Integer> variableMap, File testFolder,String errorText) throws IOException{
        String pathname;
        String writeText;
        pathname = testFolder.getPath()+"/test.err"; // By default set the pathname to that error file
        if (!"".equals(errorText)){    // If error text is not empty set it to the String WriteText
            writeText = errorText ;
        }

        else if(variableMap.containsKey("result")){ //Successful
            pathname = testFolder.getPath()+"/test.out"; // Change the pathname to that output file
            writeText = String.valueOf(variableMap.get("result"));


        } else{ //Failure
            writeText = "Variable \"result\" not found";
        }
        //Write to file
        File writeFile = new File(pathname);
        FileWriter fileWriter = new FileWriter(writeFile.getPath());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf(writeText);
        printWriter.close();
    }

    // Traverse the directories inside the directory test
    private static void folderTraverse(String filePath) throws IOException {
        File testDirectory = new File(filePath);

        // Loop through the directory to get the input from a test.txt file
        for(File testFiles : testDirectory.listFiles()){
            if(testFiles.isFile()){
                if(testFiles.getPath().contains("test.txt")){
                    processText(testFiles.getPath(),testDirectory);
                }
            }
        }
    }


    // Check if a number has been assigned to a variable of if it is an expression
    private static boolean isNumber(String text) {
        String numberRegex = "[0-9]+";
        return text.matches(numberRegex);
    }


    // Evaluate the expression and return the result after evaluating
    private static int processExpression(Map<String, Integer> variableMap, String expr) {
        int len = expr.length();
        if (expr == null || len == 0) return 0;
        int val = 0;
        char operator = '+';
        String curr = "";
        Stack<Integer> stack = new Stack<Integer>();

        for (int i = 0; i < len; i++) {
            if (Character.isDigit(expr.charAt(i))) {
                curr = "";
                val = val * 10 + expr.charAt(i) - '0';
            } else if (Character.isLetter(expr.charAt(i))) {
                curr += expr.charAt(i);
            }
            if ((!Character.isLetterOrDigit(expr.charAt(i)) && ' ' != expr.charAt(i)) || i == len - 1) {
                if (!curr.isEmpty()) {
                    val = variableMap.getOrDefault(curr, 0);
                    curr = "";
                }

                switch (operator){
                    case '^':
                            stack.push((int) Math.pow(stack.pop(), val));
                            break;

                    case '-':
                            stack.push(-val);
                            break;
                    case '+':
                            stack.push(val);
                            break;
                    case '*':
                            stack.push(stack.pop() * val);
                            break;
                    case '/':
                            stack.push(stack.pop() / val);
                            break;
                    default:
                            break;
                }

                operator = expr.charAt(i);
                val = 0;
            }
        }
        if (!curr.isEmpty()) {
            stack.push(variableMap.getOrDefault(curr, 0));
        }

        int re = 0;
        for (int i : stack) {
            re += i;
        }
        return re;
    }

    public static void main(String[] args) throws IOException {
        String filePath=args[0];
        folderTraverse(filePath);
    }
}



