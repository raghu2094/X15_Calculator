import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class _x15CalculatorTest {

    @Test
    public void main() throws IOException {
        String filePath=System.getProperty("user.dir")+"/tests";
        File testParentFolder = new File(filePath);

        //Get all directories inside the directory tests
        File[] listofTestFolders = testParentFolder.listFiles();

        for (File testDirectory : listofTestFolders) {
            if (testDirectory.isDirectory()) {
                //System.out.println("Directory: " + testDirectory.getName());
                x15Calculator.main(new String[] {testDirectory.getPath()});
            }
        }

//        String testPath = System.getProperty("user.dir")+"/tests/rghu_test";

    }
}