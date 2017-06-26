package marytts.io.serializer;

import marytts.data.Utterance;
import marytts.io.MaryIOException;
import marytts.io.serializer.XMLSerializer;

import java.io.InputStream;
import java.util.Scanner;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.*;


/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class MaryTextGridSerializerTest {
    @Test
    public void checkBaselineExample() throws Exception {

	// Load XML
        String testResourceName = "baseline_example.xml";
        InputStream input = this.getClass().getResourceAsStream(testResourceName);
        String string_xml = new Scanner(input, "UTF-8").useDelimiter("\\A").next();

	// Load TextGrid
        testResourceName = "baseline_example.TextGrid";
        input = this.getClass().getResourceAsStream(testResourceName);
        String string_actual_tg = new Scanner(input, "UTF-8").useDelimiter("\\A").next();

        try {
            XMLSerializer xml_ser = new XMLSerializer();
            Utterance utt = xml_ser.fromString(string_xml);


            MaryTextGridSerializer tg_ser = new MaryTextGridSerializer();
            String string_expected_tg = tg_ser.toString(utt);
            Assert.assertEquals(string_expected_tg, string_actual_tg);
        } catch (MaryIOException ex) {
            if (ex.getEmbeddedException() != null) {
                throw ex.getEmbeddedException();
            } else {
                throw ex;
            }
        }
    }
}


/* MaryTextGridSerializerTest.java ends here */
