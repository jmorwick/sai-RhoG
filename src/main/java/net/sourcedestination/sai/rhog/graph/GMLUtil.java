package net.sourcedestination.sai.rhog.graph;

import dlg.bridges.GMLBridge;
import dlg.core.DLG;
import dlg.core.TreeDLG;
import net.sourcedestination.sai.graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by jmorwick on 6/30/17.
 */
public class GMLUtil {

    public static Iterator<SaiDlgAdapter> gmlCollectionToDLG(final BufferedReader in) {
        final GMLBridge bridge = new GMLBridge();
        return new Iterator<SaiDlgAdapter>() {
            SaiDlgAdapter g = null;

            private void peekNextGraph() {
                try {
                    DLG dlg = bridge.load(in);
                    if(dlg instanceof TreeDLG)
                        g = new SaiTreeDlg((TreeDLG)dlg);
                   else g = new SaiDlg(dlg);
                } catch (Exception e) { g = null; }
            }

            @Override
            public boolean hasNext() {
                try {
                    if(!in.ready()) return false;
                    if(g == null) {
                        peekNextGraph();
                        if(g == null) return false;
                    }
                    return true;
                } catch(Exception e) {
                    return false;
                }
            }

            @Override
            public SaiDlgAdapter next() {
                if(g == null) peekNextGraph();
                SaiDlgAdapter ret = g;
                g = null;
                return ret;
            }
        };
    }

    public static String dlgToGml(DLG g) {
        return FileFormatUtil.serialize(g, new GMLBridge());
    }

    public static String saiToGml(Graph g, String featureName, String defaultLabel) {
        DLGFactory f = new DLGFactory(featureName, defaultLabel);
        return dlgToGml(f.copy(g));
    }

    /** reference this static method as a deserializer object where needed */
    public static SaiDlg gmlToDlg(String gml) {
        DLGReader reader = new GMLBridge()::load;
        return reader.apply(gml);
    }

    /** reference this static method as a serializer object where needed */
    public static String saiToGml(Graph g) {
        return saiToGml(g,"","");
    }
}
