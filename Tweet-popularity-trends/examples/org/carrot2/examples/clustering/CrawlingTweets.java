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
	
	public Document[] run(int TweetsNo, String boundingBox){
		String url="https://stream.twitter.com/1/statuses/filter.json?locations=".concat(boundingBox);
		Document[] TweetsCollection = new Document[TweetsNo];
		
		//Setup authentication before using streaming API
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("hjhang", "hungry1526!".toCharArray());
			}
		});
		
		try {
			//Create connection with streaming API
			URLConnection connection = new URL(url).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			int count=0;
			
			System.out.println("Fetching Tweets...");
			String readline = "";
			long start_time = System.nanoTime();
			while ((readline = in.readLine()) != null) {
				if(count==TweetsNo)
					break;
				JSONObject data = new JSONObject(readline);
				if(data.has("text")&&!data.get("geo").toString().equals("null")){
					String tweet=data.get("text").toString();
					tweet=POSTagger.POSTaggerGetNouns(tweet);
//					tweet=tweet.replaceAll("RT\\p{Blank}", "").trim();
//					tweet=tweet.replaceAll("RT", "").trim();
//					tweet=tweet.replaceAll("@[a-zA-z_0-9]+[\\space|:\\space]", "").trim();
//					tweet=tweet.replaceAll("\\p{Space}", " ").trim();
//					tweet=tweet.replaceAll("http://[a-zA-z_0-9/\\p{Punct}]+","").trim();
//					tweet=tweet.replaceAll("\\p{Punct}", "").trim();
					
					String coordinates=new JSONObject(data.get("geo").toString()).getString("coordinates").toString();
					coordinates=coordinates.replace("[", "");
					coordinates=coordinates.replace("]", "");
					
					if(tweet.length()>2){
						//String str="\""+count+"\"\t\""+tweet+"\"\t\""+coordinates+"\"\n";	
						TweetsCollection[count]=new Document(String.valueOf(count),tweet,coordinates);
						count++;
					}
				}	
			}
			long end_time = System.nanoTime();
			System.out.println("Finished!\nFetching "+TweetsNo+" tweets took: "+(end_time - start_time) / 1.0e9 + "s\n");
			in.close();
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
