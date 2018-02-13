package norn;

import static org.junit.Assert.*;
import static org.hamcrest.core.StringStartsWith.startsWith;
import org.junit.Test;

import lib6005.parser.Parser;

public class ParserLibVersionTest {
    
    public static final String REQUIRED_VERSION_PREFIX = "2.0";
    
    @Test
    public void testParserLibVersion() {
        assertThat("Please update lib/parserlib.jar, your version is incompatible",
                   Parser.VERSION,
                   startsWith(REQUIRED_VERSION_PREFIX));
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
}
