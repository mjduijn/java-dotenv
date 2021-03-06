package tests;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavaTests {
    private Map<String, String> envVars;

    @Before
    public void setUp() {
        envVars = new HashMap<>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test(expected = DotEnvException.class)
    public void throwIfMalconfigured() {
        Dotenv.configure().load();
    }

    @Test(expected = DotEnvException.class)
    public void load() {
        Dotenv dotenv = Dotenv.load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void iteratorOverDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        Map<String,String> m = new HashMap<String, String>() {{
            put("test", "hi");
            put("test1", "hi1");
        }};

        dotenv.entries().forEach(e -> assertEquals(dotenv.get(e.getKey()), e.getValue()));

        for (DotenvEntry e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToRemoveFromDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        Iterator<DotenvEntry> iter = dotenv.entries().iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToAddToDotenv() {

        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        dotenv.entries().add(new DotenvEntry("new", "value"));
    }

    @Test
    public void configureWithIgnoreMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMissingAndMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .directory("/missing/dir")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

        assertNotNull(dotenv.get("PATH"));
    }
}
