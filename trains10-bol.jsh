/env -class-path ~/.m2/repository/net/sourcedestination/sai-rhog/1.0-SNAPSHOT/sai-rhog-1.0-SNAPSHOT.jar:~/.m2/repository/net/sourcedestination/sai/2.0-SNAPSHOT/sai-2.0-SNAPSHOT.jar:~/.m2/repository/net/sourcedestination/funcles/2.0/funcles-2.0.jar:lib/rhog-2017-07-01.jar:~/.m2/repository/com/google/guava/guava/22.0/guava-22.0.jar

import net.sourcedestination.sai.experiment.retrieval.GraphSimilarityMetric;
import net.sourcedestination.sai.db.graph.GraphTransformation;
import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.rhog.graph.DLGFactory;
import net.sourcedestination.sai.rhog.graph.SaiDlg;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.experiment.retrieval.Retriever;
import net.sourcedestination.sai.rhog.ClassifierWrapper;
import net.sourcedestination.sai.rhog.graph.GMLPopulator;
import net.sourcedestination.sai.rhog.comparison.distance.RhogDistanceAdapter;
import net.sourcedestination.sai.rhog.ClassificationLabelLoader;
import net.sourcedestination.sai.experiment.learning.ClassificationModelGenerator;
import net.sourcedestination.sai.experiment.learning.CrossValidatedClassificationExperiment;
import java.net.URL;
import java.io.*;

import dlg.bridges.GMLBridge;
import dlg.core.subsumption.FlatSubsumption;
import dlg.ml.KNearestNeighbors;
import dlg.ml.distance.BagOfLabelsDistance;

import static net.sourcedestination.sai.experiment.retrieval.Retriever.simpleSequentialRetrieverFactory;
import static net.sourcedestination.sai.experiment.retrieval.Retriever.rerankingRetriever;
import static net.sourcedestination.sai.rhog.comparison.distance.RhogDistanceAdapter.getBagOfLabelsDistance;
import static net.sourcedestination.sai.experiment.learning.ClassificationModelGenerator.knnClassifierGenerator;
// experiment parameters
var k = 1;
var dataUrl = new URL("https://raw.githubusercontent.com/santiontanon/RHOG/master/data/gml/trains/trains-instances.gml");
var labelsUrl = new URL("https://raw.githubusercontent.com/santiontanon/RHOG/master/data/gml/trains/trains-labels.txt");


// initialize basic objects
var dlgGen = new DLGFactory();
var dist = getBagOfLabelsDistance(true,true);
var db = new BasicDBInterface();

// load data in to database
var populator = new GMLPopulator(dataUrl);
var task1 = populator.apply(db);
task1.get();

// find expected classifications
var classes = new ClassificationLabelLoader(labelsUrl);

// create model generator for k-NN
var modelGen = knnClassifierGenerator(
  db -> rerankingRetriever(simpleSequentialRetrieverFactory(db), db, dist),
  k);


// run leave-one-out classification experiment
var experiment = new CrossValidatedClassificationExperiment(modelGen, db, "db1", classes);
System.out.println(experiment.get());

/exit
