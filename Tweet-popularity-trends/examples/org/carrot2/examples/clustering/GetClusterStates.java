package org.carrot2.examples.clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import org.carrot2.core.*;

/**
 * 
 * @author root This class handles the processing of documents within clusters
 *         as states
 */
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
	
	public static HashMap<String, List<HashMap<String, Integer>>> returnClusterStates(final Collection<Cluster> clusters) {
		
		HashMap<String, List<HashMap<String, Integer>>> clusterMap = new HashMap<String, List<HashMap<String, Integer>>>();
		BufferedWriter bw=null;	
		deleteAll(new File("./maps/"));
		new File("./maps/").mkdir();
		
		for (Cluster c : clusters) {
			try {
				if(!c.getLabel().equals("Other Topics")){
					bw=new BufferedWriter(new FileWriter("./"+c.getLabel().replaceAll(" ", "_")+".txt"));
				}
				HashMap<String, Integer> state_count = new HashMap<String, Integer>();
				for (final Document document : c.getDocuments()){
					final String geocode = document.getField(Document.CONTENT_URL);
					String[] res=geocode.split(",");
					if(!c.getLabel().equals("Other Topics")){
						bw.write(res[0]+"\t"+res[1]+"\n");
					}
					TweetState st = new TweetState();
					String state = st.getState(geocode);
					Integer count = state_count.get(state);
					if (count == null) {
						state_count.put(state, 1);
					} else {
						state_count.put(state, count + 1);
					}
				}
				if(!c.getLabel().equals("Other Topics")){
					bw.close();
					try {
						Runtime rt=Runtime.getRuntime();
						String strName=c.getLabel().replaceAll(" ", "_");
						Process pr=rt.exec("./map.sh "+strName);
						BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			            String line="";
			            while((line=input.readLine()) != null) {
			                System.out.println(line);
			            }
						pr.waitFor();
						pr.destroy();
					} catch (InterruptedException e) {
						e.printStackTrace();
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
				
				File dic=new File("./");
				String[] childrens=dic.list();
				for(String str:childrens){
					if(str.contains("pdf")){
						new File("./"+str).renameTo(new File("./maps/"+str));
					}
					else if(str.contains("txt")){
						new File("./"+str).delete();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
