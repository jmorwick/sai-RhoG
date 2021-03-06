package net.sourcedestination.sai.rhog.metrics;

import dlg.core.DLG;
import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.rhog.graph.GMLPopulator;
import net.sourcedestination.sai.rhog.graph.SaiDlgAdapter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by jmorwick on 7/21/17.
 */
public class ConnectedStatTest {

    @Test
    public void testLoadGMLFile() throws Exception {
        File f = new File(getClass().getClassLoader().getResource("sponge-instances.gml").getFile());
        BufferedReader in = new BufferedReader(new FileReader(f));
        DBInterface db = new BasicDBInterface();
        Set<DLG> graphs = new HashSet<>();
        for(Iterator<SaiDlgAdapter> i = GMLPopulator.gmlCollectionToDLG(in);
            i.hasNext();
            db.addGraph(i.next()));

        ConnectedMetric stat = new ConnectedMetric();
        double result = (double)db.getGraphIDStream()
                .map(db::retrieveGraph)
                .filter(stat::test)
                .count()
                / db.getDatabaseSize();
        assertTrue(0.99999999 < result);
        assertTrue(1.00000001 > result);
    }

}
