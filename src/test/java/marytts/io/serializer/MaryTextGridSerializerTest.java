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
    public void testLoadingTextGridWithoutException() throws Exception {
        String testResourceName = "in.xml";
        InputStream input = this.getClass().getResourceAsStream(testResourceName);
        String string_xml = new Scanner(input, "UTF-8").useDelimiter("\\A").next();


        try {
            XMLSerializer xml_ser = new XMLSerializer();
            Utterance utt = xml_ser.fromString(string_xml);


            MaryTextGridSerializer tg_ser = new MaryTextGridSerializer();
            String string_tg = tg_ser.toString(utt);
            System.out.println(string_tg);
            Assert.assertEquals(1, 2);
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
