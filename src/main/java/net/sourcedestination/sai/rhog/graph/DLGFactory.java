package net.sourcedestination.sai.rhog.graph;

import dlg.util.Label;
import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.GraphTransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sourcedestination.sai.rhog.graph.SaiDlg.labelFromString;

/**
 * Created by jmorwick on 6/30/17.
 */
public class DLGFactory implements GraphTransformation<SaiDlg> {

    private final String featureName;
    private final String defaultLabel;
    /**
     *
     * @param featureName the name of the feature to extract for labels
     * @param defaultLabel the label value to use if no feature with given name is available for a node/edge
     */
    public DLGFactory(String featureName, String defaultLabel) {
        this.featureName = featureName;
        this.defaultLabel = defaultLabel;
    }

    public DLGFactory() {
        featureName = "";
        defaultLabel = "";
    }

    @Override
    public SaiDlg apply(Graph g) {
        List<Integer> nodeIds = new ArrayList<>(g.getNodeIDsSet());
        Map<Integer,Integer> nodeIdMap = new HashMap<>();
        SaiDlg gc = new SaiDlg(nodeIds.size(),
                g.getFeature("label") != null ?
                        g.getFeature("label").getValue() : "");
        for(int i=0; i<nodeIds.size(); i++) {
            Feature f = g.getNodeFeature(featureName, nodeIds.get(i));
            String labelValue = f == null ? defaultLabel : f.getValue();
            Label l = labelFromString(labelValue);
            gc.setVertex(i, l);
            nodeIdMap.put(nodeIds.get(i), i);
        }
        g.getEdgeIDs().forEach(eid -> {
            Feature f = g.getEdgeFeature(featureName, eid);
            String labelValue = f == null ? defaultLabel : f.getValue();
            Label l = labelFromString(labelValue);
            gc.setEdge(
                    nodeIdMap.get(g.getEdgeSourceNodeID(eid)),
                    nodeIdMap.get(g.getEdgeTargetNodeID(eid)),
                    l);
        });

        return gc;
    }
}
