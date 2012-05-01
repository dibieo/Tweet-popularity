package org.carrot2.examples.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.carrot2.core.*;

public class GetClusterStates {
	/**
	 * Returns a cluster with the states represented in them.
	 * 
	 * @param clusters
	 * @return HahMap of clusters where each key is a topic and the value a list
	 *         of states within each cluster is also returned. Each state is a
	 *         dictionary where the key is the name of the state and frequency
	 *         is the number of tweets from that state under the topic
	 */

	public static void drawingMap(final Collection<Cluster> clusters, String path) {
		BufferedWriter bw = null;
		deleteAll(new File(path));
		new File(path).mkdir();

		for (Cluster c : clusters) {
			try {
				if (!c.getLabel().equals("Other Topics")) {
					bw = new BufferedWriter(new FileWriter("./"+ c.getLabel().replaceAll(" ", "_") + ".txt"));
				}
				for (final Document document : c.getDocuments()) {
					final String geocode = document.getField(Document.CONTENT_URL);
					String[] res = geocode.split(",");
					if (!c.getLabel().equals("Other Topics")) {
						bw.write(res[0] + "\t" + res[1] + "\n");
					}
					try {
						Thread.sleep(100);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				if (!c.getLabel().equals("Other Topics")) {
					bw.close();
					try {
						Runtime rt = Runtime.getRuntime();
						String strName = c.getLabel().replaceAll(" ", "_");
						Process pr = rt.exec("./map.sh " + strName);
//						BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//						String line = "";
//						while ((line = input.readLine()) != null) {
//							System.out.println(line);
//						}
						pr.waitFor();
						pr.destroy();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				File dic = new File("./");
				String[] childrens = dic.list();
				for (String str : childrens) {
					if (str.contains("pdf")) {
						new File("./" + str).renameTo(new File(path + str));
					} else if (str.contains("txt")) {
						new File("./" + str).delete();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static HashMap<String, List<HashMap<String, Integer>>> sequentialGetClusterStates(final Collection<Cluster> clusters) {

		HashMap<String, List<HashMap<String, Integer>>> clusterMap = new HashMap<String, List<HashMap<String, Integer>>>();
		for (Cluster c : clusters) {
			HashMap<String, Integer> state_count = new HashMap<String, Integer>();
			for (final Document document : c.getDocuments()) {
				final String geocode = document.getField(Document.CONTENT_URL);
				TweetState st = new TweetState();
				String state = st.getState(geocode);
				try {
					Thread.sleep(100);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Integer count = state_count.get(state);
				if (count == null) {
					state_count.put(state, 1);
				} else {
					state_count.put(state, count + 1);
				}
			}

			ArrayList<HashMap<String, Integer>> l = new ArrayList<HashMap<String, Integer>>();
			l.add(state_count);
			HashMap<String, Integer> spread = new HashMap<String, Integer>();
			HashMap<String, Integer> cluster_size = new HashMap<String, Integer>();
			cluster_size.put("Number of tweets", c.size());
			spread.put("Number of states", state_count.size());
			l.add(spread);
			l.add(cluster_size);
			clusterMap.put(c.getLabel(), l);

		}
		return clusterMap;
	}

	public static HashMap<String, List<HashMap<String, Integer>>> concurrentGetClusterStates(final Collection<Cluster> clusters) throws Exception {
		final HashMap<String, List<HashMap<String, Integer>>> clusterMap = new HashMap<String, List<HashMap<String, Integer>>>();
		final List<Callable<Cluster>> partitions = new ArrayList<Callable<Cluster>>();

		double blockingCoefficient = 0.95;
		final int numberOfCores = Runtime.getRuntime().availableProcessors();
		final int poolSize = (int) (numberOfCores / (1 - blockingCoefficient));
		System.out.println("Number of cores:" + numberOfCores);
		System.out.println("Pool size:" + poolSize);
		Iterator<Cluster> it = clusters.iterator();

		while (it.hasNext()) {
			final Cluster c = it.next();
			partitions.add(new Callable<Cluster>() {

				public Cluster call() throws Exception {
					HashMap<String, Integer> state_count = new HashMap<String, Integer>();
					if (!c.getLabel().contentEquals("Other Topics")) {
						for (final Document document : c.getDocuments()) {
							final String geocode = document.getField(Document.CONTENT_URL);
							TweetState st = new TweetState();
							String state = st.getState(geocode);
							try {
								Thread.sleep(100);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							Integer count = state_count.get(state);
							if (count == null) {
								state_count.put(state, 1);
							} else {
								state_count.put(state, count + 1);
							}

						}
					}
					ArrayList<HashMap<String, Integer>> l = new ArrayList<HashMap<String, Integer>>();
					l.add(state_count);
					HashMap<String, Integer> spread = new HashMap<String, Integer>();
					HashMap<String, Integer> cluster_size = new HashMap<String, Integer>();
					cluster_size.put("Number of tweets", c.size());
					spread.put("Number of states", state_count.size());
					l.add(spread);
					l.add(cluster_size);
					clusterMap.put(c.getLabel(), l);
					return c;
				}
			});
		}
		System.out.println("Partition size:" + partitions.size());
		final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
		executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
		executorPool.shutdown();
		return clusterMap;
	}

	public static boolean deleteAll(File dir) {
		if (dir.isDirectory()) {
			String children[] = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteAll(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

}
