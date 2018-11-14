/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest {

    @Test    
    public void testAdd() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */
    
    @Test
    public void testSubtract() {
    	Money val1 = new Money("30.64");
    	Money val2 = new Money("10.01");
    	Money val3 = new Money("-10.01");
    	Money result1 = val1.subtract(val2);
    	Money result2 = val1.subtract(val3);
    	assertEquals("20.63", result1.toString());
    	assertEquals("40.65", result2.toString());
    }
    
    @Test
    public void testAddPercent() {
    	Money val1 = new Money("10.00");
    	Money val2 = new Money("-10.00");
    	double percent = 20;
    	Money result1 = val1.addPercent(percent);
    	Money result2 = val2.addPercent(percent);
    	assertEquals("12.00", result1.toString());
    	assertEquals("-12.00", result2.toString());
    }
    
    @Test
    public void testCompareTo() {
    	Money val1 = new Money("33.66");
    	Money val2 = new Money("33.66");
    	Money val3 = new Money("10.00");
    	Money val4 = new Money("-20.00");
    	int result1 = val1.compareTo(val2);
    	int result2 = val1.compareTo(val3);
    	int result3 = val3.compareTo(val1);
    	int result4 = val4.compareTo(val1);
    	assertEquals(0, result1);
    	assertEquals(1, result2);
    	assertEquals(-1 ,result3);
    	assertEquals(-1 ,result4);
    }
    
    @Test
    public void testLessEqual() {
    	Money val1 = new Money("33.66");
    	Money val2 = new Money("33.66");
    	Money val3 = new Money("10.00");
    	Money val4 = new Money("-10.00");
    	boolean result1 = val1.lessEqual(val2);
    	boolean result2 = val1.lessEqual(val3);
    	boolean result3 = val3.lessEqual(val1);
    	boolean result4 = val4.lessEqual(val1);
    	assertEquals(true, result1);
    	assertEquals(false, result2);
    	assertEquals(true,result3);
    	assertEquals(true,result4);
    }
    
    @Test
    public void testEquals() {
    	Money val1 = new Money("30.64");
    	Money val2 = new Money("10.01");
    	Money val3 = new Money("10.01");
    	Money val4 = new Money("-10.00");
    	Money val5 = new Money("-10.00");
    	boolean result1 = val1.equals(val2);
    	boolean result2 = val2.equals(val3);
    	boolean result3 = val4.equals(val5);
    	assertEquals(false, result1);
    	assertEquals(true, result2);
    	assertEquals(true, result3);
    }

    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}
