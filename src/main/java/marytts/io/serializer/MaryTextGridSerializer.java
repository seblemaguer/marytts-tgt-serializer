package marytts.io.serializer;

/* TextGrid part */
import org.m2ci.msp.jtgt.TextGrid;
import org.m2ci.msp.jtgt.Tier;
import org.m2ci.msp.jtgt.tier.IntervalTier;
import org.m2ci.msp.jtgt.Annotation;
import org.m2ci.msp.jtgt.annotation.IntervalAnnotation;
import org.m2ci.msp.jtgt.io.TextGridSerializer;
import org.m2ci.msp.jtgt.io.TextGridIOException;

/* MaryTTS data part */
import marytts.data.item.Item;
import marytts.data.Sequence;
import marytts.data.Relation;
import marytts.data.Utterance;
import marytts.data.item.phonology.Phoneme;
import marytts.data.SupportedSequenceType;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/* IO part */
import marytts.io.MaryIOException;
import marytts.io.serializer.Serializer;
import java.io.File;

/* Utils part */
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * TextGrid serializer for marytts
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le
 *         Maguer</a>
 */
public class MaryTextGridSerializer implements Serializer {

    /** */
    protected static final SupportedSequenceType segment_sequence = SupportedSequenceType.PHONE;

    /**
     * Constructor
     *
     */
    public MaryTextGridSerializer() {
    }

    /**
     * Generate the TextGrid formatted string of the ROOTS utterance
     *
     * @param utt the utterance to export
     * @return the JSON formatted string
     * @throws MaryIOException if anything is going wrong
     */
    public String toString(Utterance utt) throws MaryIOException {
        TextGrid tg = toTextGrid(utt);

        TextGridSerializer tgs = new TextGridSerializer();
        try {
            return tgs.toString(tg);
        } catch (TextGridIOException ex) {
            throw new MaryIOException("", ex);
        }
    }

    protected TextGrid toTextGrid(Utterance utt) throws MaryIOException {
        ArrayList<ImmutablePair<Double, Double>> start_dur_list = generateListDurationAnchors(utt);
        double start = 0.0;
        ImmutablePair<Double, Double> last_item = start_dur_list.get(start_dur_list.size() - 1);
        double end = last_item.getRight();
        TextGrid tg = new TextGrid(null, start, end);

        for (SupportedSequenceType cur_seq_type : utt.listAvailableSequences()) {
            Tier the_tier = generateTier(utt, cur_seq_type, start_dur_list);
            tg.addTier(the_tier);
        }

        return tg;
    }

    protected ArrayList<ImmutablePair<Double, Double>> generateListDurationAnchors(Utterance utt) throws MaryIOException {
        ArrayList<ImmutablePair<Double, Double>> start_dur_list = new ArrayList<ImmutablePair<Double, Double>>();
        Sequence<Phoneme> seg_seq = (Sequence<Phoneme>) utt.getSequence(segment_sequence);
        if (seg_seq == null) {
            throw new MaryIOException("Reference sequence \"" + segment_sequence + "\" is not define in the utterance", null);
        }

        double start = 0.0;
        for (Phoneme ph : seg_seq) {
            double dur = 1.0;
            start_dur_list.add(new ImmutablePair<Double,  Double>(start, start + dur));
            start += dur;
        }

        return start_dur_list;
    }

    protected Tier generateTier(Utterance utt, SupportedSequenceType cur_seq_type,
                                ArrayList<ImmutablePair<Double, Double>> start_dur_list) throws MaryIOException {
        Relation rel = utt.getRelation(cur_seq_type, segment_sequence);
        if (rel == null) {
            throw new MaryIOException("name = " + cur_seq_type.toString() + "is not related to the reference sequence", null);
        }
        IntervalTier the_tier = new IntervalTier(cur_seq_type.toString());
        Sequence<Item> seq_origin = (Sequence<Item>) utt.getSequence(cur_seq_type);

        for (int i = 0; i < seq_origin.size(); i++) {
            String name = seq_origin.get(i).toString();
            // Get the start and the end
            int[] rel_indexes = rel.getRelatedIndexes(i);
            if (rel_indexes.length <= 0) {
                continue;
            }

            double start = start_dur_list.get(rel_indexes[0]).getLeft();
            ImmutablePair<Double, Double> last_item = start_dur_list.get(rel_indexes[rel_indexes.length - 1]);
            double end = last_item.getRight();

            IntervalAnnotation an = new IntervalAnnotation(start, end, name);
            the_tier.addAnnotation(an);
            if (i == 0) {
                the_tier.setStart(start);
            }
        }

        return the_tier;
    }

    /**
     * Generate an utterance from the TextGrid stored in the string
     * format. For now, it is not supported.
     *
     * @param content the textgrid
     * @return the created utterance
     * @throws MaryIOException
     *             if anything is going wrong
     */
    public Utterance fromString(String content) throws MaryIOException {
        throw new UnsupportedOperationException();
    }
}

/* MaryTextGridSerializer.java ends here */
