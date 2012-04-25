package org.carrot2.examples.clustering;
import java.util.*;
import org.carrot2.core.*;

/**
 * 
 * @author root
 * This class handles the processing of documents within clusters as states
 */
public class GetClusterStates {

	
	/**
	 * Returns a cluster with the states represented in them.
	 * @param clusters
	 * @return HahMap of clusters where each key is a topic and the value a list of states within each cluster is also returned.
	 * Each state is a dictionary where the key is the name of the state and frequency is the number of tweets from that state
	 * under the topic
	 */
	public HashMap returnClusterStates(final Collection<Cluster> clusters){
		
		HashMap<String, List>clusterMap = new HashMap<String, List>();
		
		for (Cluster c : clusters){
			  HashMap<String, Integer>state_count = new HashMap<String, Integer>();

			for (final Document document : c.getDocuments())
		        {
				  final String geocode = document.getField(Document.CONTENT_URL);
				  TweetState st = new TweetState();
				  String state = st.getState(geocode);
				  Integer count = state_count.get(state);
				  if (count == null){
					  state_count.put(state, 1);
				  }else{
					  state_count.put(state, count + 1);
				  }

				  
		        }
			   ArrayList<HashMap>l = new ArrayList<HashMap>();
			   l.add(state_count);
			   HashMap<String, Integer>spread = new HashMap<String,Integer>();
			   spread.put("number of states", state_count.size());
			   l.add(spread);
			   clusterMap.put(c.getLabel(), l);
		}
		
		return clusterMap;
	}
}
