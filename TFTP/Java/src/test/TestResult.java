package test;

/**
 * Reference: https://www.delftstack.com/howto/java/java-key-value-pair/#implement-key-value-pair-using-map-entry-in-java
 */

import java.util.Map;
import java.util.Map.Entry;

public class TestResult{
	Entry<String,Boolean> tr;
    public TestResult(String key, Boolean value) {
    	this.tr = Map.entry(key, value);
    }
    
    public String getKey() {
    	return this.tr.getKey();
    }
    
    public Boolean getValue() {
    	return this.tr.getValue();
    }
}