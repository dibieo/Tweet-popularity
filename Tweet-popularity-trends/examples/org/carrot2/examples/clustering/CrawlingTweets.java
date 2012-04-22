package org.carrot2.examples.clustering;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import org.carrot2.core.Document;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;


public class CrawlingTweets {
	String boundingBox="-179.15,18.9,-66.94,71.44";
	String urlSchema = "https://stream.twitter.com/1/statuses/filter.json?locations=".concat(boundingBox);
	
	public Document[] run(int TweetsNo){
		Document[] TweetsCollection = new Document[TweetsNo];
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("hjhang", "hungry1526!".toCharArray());
			}
		});
		
		try {
			URLConnection connection = new URL(urlSchema).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String readline = "";
			int count=0;
			System.out.println("Starting to fetch Tweets..");
			while ((readline = in.readLine()) != null) {
				if(count==TweetsNo)
					break;
				JSONObject data = new JSONObject(readline);
				if(data.has("text")&&!data.get("geo").toString().equals("null")){
					String tweet=data.get("text").toString();
					tweet=tweet.replaceAll("RT\\p{Blank}", "").trim();
					tweet=tweet.replaceAll("RT", "").trim();
					tweet=tweet.replaceAll("@[a-zA-z_0-9]+[\\space|:\\space]", "").trim();
					tweet=tweet.replaceAll("\\p{Space}", " ").trim();
					tweet=tweet.replaceAll("http://[a-zA-z_0-9/\\p{Punct}]+","").trim();
					tweet=tweet.replaceAll("\\p{Punct}", "").trim();
					
					String coordinates=new JSONObject(data.get("geo").toString()).getString("coordinates").toString();
					coordinates=coordinates.replace("[", "");
					coordinates=coordinates.replace("]", "");
					coordinates=coordinates.replace(",", " ");
					
					if(tweet.length()>20){
						//String str="\""+count+"\"\t\""+tweet+"\"\t\""+coordinates+"\"\n";	
						TweetsCollection[count]=new Document(String.valueOf(count),tweet,coordinates);
						count++;
					}
				}	
			}
			in.close();
			System.out.println("Finished fetching");
			return TweetsCollection;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
