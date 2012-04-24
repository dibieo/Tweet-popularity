package org.carrot2.examples.clustering;
import java.util.List;
import edu.cmu.cs.lti.ark.tweetnlp.TweetTaggerInstance;
import edu.cmu.cs.lti.ark.tweetnlp.Twokenize;

public class POSTagger {

	public static List<String> doPOSTagging(List<String> toks) {
		return tweetTagging(toks);
	}
	
	public static List<String> tweetTagging(List<String> toks) {
		return TweetTaggerInstance.getInstance().getTagsForOneSentence(toks);
	}
	
	public static String POSTaggerGetNouns(String textline){
		String nounString="";
		List<String> toks = Twokenize.tokenizeForTagger_J(textline);
		List<String> tags = doPOSTagging(toks);
		for (int i=0; i < toks.size(); i++) {
			if(tags.get(i).toString().equals("N"))
				nounString=nounString.concat(toks.get(i).toString()+" ");
		}
		return nounString.trim();
	}
}
