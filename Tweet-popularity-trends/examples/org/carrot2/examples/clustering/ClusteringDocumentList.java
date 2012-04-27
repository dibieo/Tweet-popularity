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

	public static void rankTopics(
			HashMap<String, List<HashMap<String, Integer>>> topics,final double per) {
		List<Map.Entry<String, List<HashMap<String, Integer>>>> al = new ArrayList<Map.Entry<String, List<HashMap<String, Integer>>>>(topics.entrySet());
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
		int top=10;
		System.out.println("\nTop " + top +" topics ranked by " + key + ":");
		int count = 0;
		for (Entry<String, List<HashMap<String, Integer>>> entry : al) {
			if (count == top)
				break;
			if (!entry.getKey().equals("Other Topics")) {
				System.out.println((count + 1) + ". " + entry.getKey());
				count++;
			}
		}
	}

	public static void main(String[] args) {
		/*
		 * [[[start:clustering-document-list-intro]]]
		 * 
		 * <div> <p> The easiest way to get started with Carrot2 is to cluster a
		 * collection of {@link org.carrot2.core.Document}s. Each document can
		 * consist of: </p>
		 * 
		 * <ul> <li>document content: a query-in-context snippet, document
		 * abstract or full text,</li> <li>document title: optional, some
		 * clustering algorithms give more weight to document titles,</li>
		 * <li>document URL: optional, used by the {@link
		 * org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, ignored
		 * by other algorithms.</li> </ul>
		 * 
		 * <p> To make the example short, the code shown below clusters only 5
		 * documents. Use at least 20 to get reasonable clusters. If you have
		 * access to the query that generated the documents being clustered, you
		 * should also provide it to Carrot2 to get better clusters. </p> </div>
		 * 
		 * [[[end:clustering-document-list-intro]]]
		 */

		/* Prepare Carrot2 documents */
		final ArrayList<Document> documents = new ArrayList<Document>();
		Document[] tweets = new CrawlingTweets().run(30,"-179.15,18.9,-66.94,71.44");
		documents.addAll(Arrays.asList(tweets));

		/* A controller to manage the processing pipeline. */
		final Controller controller = ControllerFactory.createSimple();
            
            //Document[] tweets=new CrawlingTweets().run(120,"-179.15,18.9,-66.94,71.44");
            //documents.addAll(Arrays.asList(tweets));


		/*
		 * Perform clustering by topic using the Lingo algorithm. Lingo can take
		 * advantage of the original query, so we provide it along with the
		 * documents.
		 */
		System.out.println("Begining clustering");
		final ProcessingResult byTopicClusters = controller.process(documents,
				null, LingoClusteringAlgorithm.class);
		final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
		System.out.println("Finished clustering");

		HashMap<String, List<HashMap<String, Integer>>> topics = GetClusterStates.returnClusterStates(clustersByTopic);
		//System.out.println(topics);

		rankTopics(topics, 1);
		rankTopics(topics, 0);
		rankTopics(topics, 0.2);
		rankTopics(topics, 0.8);

		//ConsoleFormatter.displayClusters(clustersByTopic);
		ConsoleFormatter.displayJustClusterTopics(clustersByTopic);
	}
}
