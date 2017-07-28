package recurlytest;
 
import org.testng.annotations.*;
 
public class SimpleTest {
 
 @BeforeClass
 public void setUp() {
   // code that will be invoked when this test is instantiated
 }
 
 @Test(groups = { "first" })
 public void aFastTest() {
   System.out.println("Fast test");
 }
 
}
