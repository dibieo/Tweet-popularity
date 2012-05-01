/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 *Clustering main class
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.examples.ConsoleFormatter;

/**
 * This example shows how to cluster a set of documents available as an
 * {@link ArrayList}. This setting is particularly useful for quick experiments
 * with custom data for which there is no corresponding {@link IDocumentSource}
 * implementation. For production use, it's better to implement a
 * {@link IDocumentSource} for the custom document source, so that e.g., the
 * {@link Controller} can cache its results, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class ClusteringDocumentList {
	/**
	 * Sequentially gets all the cluster states and outputs the result.
	 * 
	 * @param cl List of Clusters
	 */
	public static HashMap<String, List<HashMap<String, Integer>>> processSequential(List<Cluster> cl) {
		System.out.println("Calculating tweets popularity sequentially...");
		long start_time = System.nanoTime();
		HashMap<String, List<HashMap<String, Integer>>> result = GetClusterStates.sequentialGetClusterStates(cl);
		long end_time = System.nanoTime();
		System.out.println("Finished!\nSequential processing took: "+ (end_time - start_time) / 1.0e9 + "s\n");
		return result;
	}

	
	/**
	 * Concurrently gets all cluster states and outputs the results
	 * 
	 * @param cl ListOfClusters
	 */
	public static HashMap<String, List<HashMap<String, Integer>>> processConcurrent(List<Cluster> cl) {
		System.out.println("Calculating tweets popularity concurrently...");
		long start_time = System.nanoTime();
		HashMap<String, List<HashMap<String, Integer>>> result = null;
		try {
			result = GetClusterStates.concurrentGetClusterStates(cl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end_time = System.nanoTime();
		System.out.println("Finished! \nConcurrent processing took: "+ (end_time - start_time) / 1.0e9 + "s\n");
		return result;
	}
	
	
	public static void rankTopics(HashMap<String, List<HashMap<String, Integer>>> topics,final double per) {
		List<Map.Entry<String, List<HashMap<String, Integer>>>> al = new ArrayList<Map.Entry<String, List<HashMap<String, Integer>>>>(topics.entrySet());
		//Sorting the HashMap by given criteria
		Collections.sort(al, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				@SuppressWarnings("unchecked")
				Map.Entry<String, List<HashMap<String, Integer>>> e1 = (Entry<String, List<HashMap<String, Integer>>>) o1;
				@SuppressWarnings("unchecked")
				Map.Entry<String, List<HashMap<String, Integer>>> e2 = (Entry<String, List<HashMap<String, Integer>>>) o2;
				List<HashMap<String, Integer>> first = e1.getValue();
				List<HashMap<String, Integer>> second = e2.getValue();
				return per * first.get(1).get("Number of states") + (1 - per)* first.get(2).get("Number of tweets") < per* second.get(1).get("Number of states") + (1 - per)* second.get(2).get("Number of tweets") ? 1 : 0;
			}
		});

		String key = "";
		if (per == 0)
			key = "Number of Tweet";
		else if (per == 1)
			key = "Number of States";
		else
			key = "Combined measurment";
		
		int top = 10;
		System.out.println("\nTop " + top + " topics ranked by " + key + " ("+per+")");
		int count = 0;
		for (Entry<String, List<HashMap<String, Integer>>> entry : al){
			if (count == top)
				break;
			if (!entry.getKey().equals("Other Topics")) {
				System.out.println((count + 1) + ". " + entry.getKey());
				count++;
			}
		}
	}
	

	public static void main(String[] args) {
		/* Prepare Carrot2 documents */
		String USLatLong="-179.15,18.9,-66.94,71.44";
		final ArrayList<Document> documents = new ArrayList<Document>();
		Document[] tweets = new CrawlingTweets().run(Integer.parseInt(args[0]), USLatLong);
		documents.addAll(Arrays.asList(tweets));

		
		/* A controller to manage the processing pipeline. */
		final Controller controller = ControllerFactory.createSimple();
		System.out.println("Clustering...");
		long start_time = System.nanoTime();
		final ProcessingResult byTopicClusters = controller.process(documents,null, LingoClusteringAlgorithm.class);
		final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
		long end_time = System.nanoTime();
		System.out.println("Finished!\nClustering took: "+ (end_time - start_time) / 1.0e9 + "s\n");

		
		//Drawing maps to ./maps folder
		System.out.println("Drawing maps...");
		long s = System.nanoTime();
		GetClusterStates.drawingMap(clustersByTopic, "./maps/");
		long e = System.nanoTime();
		System.out.println("Finished!\nDrawing took: "+ (e - s) / 1.0e9 + "s\n");
		
		
		// Run it sequentially and concurrently
		HashMap<String, List<HashMap<String, Integer>>> topicsSeq = processSequential(clustersByTopic);
		HashMap<String, List<HashMap<String, Integer>>> topicsCon=processConcurrent(clustersByTopic);

		
		//Rank the results
		rankTopics(topicsSeq, 1);
		rankTopics(topicsCon, 0);
		rankTopics(topicsSeq, 0.2);
		rankTopics(topicsSeq, 0.8);

		
		//ConsoleFormatter.displayClusters(clustersByTopic);
		ConsoleFormatter.displayJustClusterTopics(clustersByTopic);
	}
}