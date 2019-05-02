import java.io.*;
import java.util.*;

public class x15Calculator {

    // Process the contents of the input file
    private static void processText(String filePath, File testFolder) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<String, Integer> variableMap = new HashMap<>();
        String line , errorText ;
        errorText="";

        // Loop through each line in the file
        while ((line = br.readLine()) != null) {

            line = line.substring(0, line.indexOf(";")); //Extract the string until ;
            line = line.replaceAll(" ", ""); // Replace all whitespaces in the expression

            errorText = "";
            String[] strArr = line.split("="); //Split the line based on the equal to sign

            if(!isVariable(strArr[0])){  //Check if variable follows program constructs
                errorText = strArr[0]+" is not a variable";
                break;
            }
            if (strArr.length == 1) {  // Check if variable is not initialized
                    errorText = "Variable " + strArr[0] + " is not intialized";
                    break;

            }

            //Check if a number is assigned to the variable
            boolean isNumber = isNumber(strArr[1]);

            //Store the variable and its value in a hashmap
            if (isNumber) {
                variableMap.put(strArr[0], Integer.valueOf(strArr[1]));
            }
            else {
                // Evaluate the expression
                Map<Integer, String> map = processExpression(variableMap, strArr[1]);
                String mapValue = map.get(map.keySet().toArray()[0]);
                //Check if an error text is set in hashmap
                if (mapValue.length()!=0){
                    errorText = mapValue;
                    break;
                };

                //Store the variable and its value in a hashmap
                variableMap.put(strArr[0], (Integer)map.keySet().toArray()[0]);

            }
        }
        // Call function to write the result to the file
        writeToFile(variableMap,testFolder,errorText);

    }


   // To write the output of the calculation to a file
    private static void writeToFile(Map<String,Integer> variableMap, File testFolder,String errorText) throws IOException{
        String pathname;
        String writeText;
        pathname = testFolder.getPath()+"/test.err"; // By default set the pathname to that of the error file
        if (!"".equals(errorText)){    // If error text is not empty set it to the String WriteText
            writeText = errorText ;
        }

        else if(variableMap.containsKey("result")){ //Check if result variable has been set
            pathname = testFolder.getPath()+"/test.out"; // Change the pathname to that of the output file
            writeText = String.valueOf(variableMap.get("result")); // Get the value assigned to the variable result


        } else{ //If result variable has not been set
            writeText = "Variable \"result\" not found";
        }
        //Write to file
        File writeFile = new File(pathname);
        FileWriter fileWriter = new FileWriter(writeFile.getPath());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf(writeText);
        printWriter.close();
    }

    // Traverse the directories inside the directory tests
    private static void folderTraverse(String filePath) throws IOException {
        File testDirectory = new File(filePath);

        // Loop through the directory to find a test.txt file and get input from that file
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

    //Check if the variable is given according to the regex
    private static boolean isVariable(String var){
        String varRegex = "[A-Za-z]+";
        return var.matches(varRegex);
    }

    // Evaluate the expression and return the result after evaluating
    private static Map<Integer,String> processExpression(Map<String, Integer> variableMap, String expr) {
        int len = expr.length();
        Map<Integer,String> result = new HashMap<Integer, String>();
        int val = 0;
        char operator = '+';
        String curr = "";
        String errorText ="";

        Stack<Integer> stack = new Stack<Integer>();

        for (int i = 0; i < len; i++) {
            if (Character.isDigit(expr.charAt(i))) {
                val = val * 10 + expr.charAt(i) - '0';
            } else if (Character.isLetter(expr.charAt(i))) {
                curr += expr.charAt(i);
                if(!variableMap.containsKey(curr)){ //Check if the variable is defined
                    errorText= "Variable "+curr+" is not defined";
                    result.put(0,errorText);
                    return result;
                }
                //Retrieve the value of the variable
                val = variableMap.get(curr);
                curr = "";
            }
            if ((!Character.isLetterOrDigit(expr.charAt(i)) && ' '!= expr.charAt(i)) || i == len - 1) {

                switch (operator){
                    //Push the the value onto the stack
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
                            errorText= "Cannot recognize operator "+operator;
                            result.put(0,errorText);
                            return result;
                }

                operator = expr.charAt(i);
                val = 0;
                System.out.println("Stack "+stack);
            }
        }


        int re = 0;
        //Get the sum of all elements in the stack
        for (int i : stack) {
            re += i;
        }
        result.put(re,errorText);
        return result;
    }

    public static void main(String[] args) throws IOException {
        String filePath=args[0];
        folderTraverse(filePath);
    }
}



