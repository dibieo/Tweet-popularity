package org.carrot2.examples.clustering;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class TweetState {

	public StringBuilder content = new StringBuilder();
	public String state = null;
	public String line = null;
	public URL url = null;

	/**
	 * Returns a state when given the comma separated latitude and longitude
	 * 
	 * @param String latlng
	 * @return String
	 */

	public String getState(String latlng) {
		//System.out.println("Processing : " + latlng);
		// Processes the JSON object to get back the state

		try {
			this.process(latlng);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Sometimes we get exceptions so we try again
			try {
				this.process(latlng);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return state;
	}

	/**
	 * Processes Google Geocode's Web Service to get the state.
	 * 
	 * @throws JSONException
	 */
	public void process(String lat_lng) throws Exception {
		String latlng = URLEncoder.encode(lat_lng, "UTF-8");
		url = new URL("http://maps.googleapis.com/maps/api/geocode/json?"+ "latlng=" + latlng + "&sensor=false");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		reader.close();
		
		JSONObject c = new JSONObject(content.toString());
		JSONArray data = c.getJSONArray("results");
		if (data.length() >= 1) {
			JSONArray d = data.getJSONObject(0).getJSONArray("address_components");

			for (int i = 0; i < d.length(); i++) {
				JSONObject state_name = d.getJSONObject(i);
				if ((state_name.getJSONArray("types").get(0).toString()).contentEquals("administrative_area_level_1")) {
					state = state_name.get("long_name").toString();
				}
			}
		}

	}

	/**
	 * Main method used primarily for evaluation
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TweetState ts = new TweetState();
		System.out.println("Hello");
		System.out.println(ts.getState("39.50091362,-84.33465421"));
	}

}
