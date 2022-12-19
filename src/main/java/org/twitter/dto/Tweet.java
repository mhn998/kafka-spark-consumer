package org.twitter.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.parse.HiveParser_IdentifiersParser.nullCondition_return;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.twitter.clientlib.model.Expansions;
import com.twitter.clientlib.model.Geo;
import com.twitter.clientlib.model.Place;
import com.twitter.clientlib.model.StreamingTweetResponse;
import com.twitter.clientlib.model.User;

public class Tweet {

	private String id;

	private String text;
	private boolean isRetweet;

	private List<String> hashTags = new ArrayList<>();

	private String username;

	private String timeStamp;
	private String lang;
	private String source;
	private String authorId;
	private String geoBbox;
	
	public String getGeoBbox() {
		return geoBbox;
	}
	
	public void setGeoBbox(String bbox) {
		geoBbox = bbox;
	}
	
	public String getAuthorId() {
		return authorId;
	}
	
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public void setRetweet(Boolean isRetweet) {
		this.isRetweet = isRetweet;
	}

	public void setRetweet() {
		this.isRetweet = this.text != null ? this.text.startsWith("RT @") : false;
	}

	public List<String> getHashTags() {
		return hashTags;
	}

	public void setHashTags() {
		List<String> strs=new ArrayList<>();

		if (this.text != null) {
			Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
			Matcher mat = MY_PATTERN.matcher(this.text);
			while (mat.find()) {
				strs.add(mat.group(1));
			}
		}

		this.hashTags = strs;
	}

	public void setHashTags(List<String> hashTags) {
		this.hashTags = hashTags;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername() {
		if (this.text != null) {
			Pattern MY_PATTERN = Pattern.compile("@(\\S+)");
			Matcher mat = MY_PATTERN.matcher(this.text);
			if (mat.find()) {
				this.username = mat.group().substring(1, mat.group().length() - 1);
			}
		}

	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", text=" + text + ", isRetweet="
				+ isRetweet + ", hashTags=" + hashTags + ", username=" + username
				+ ", timeStamp=" + timeStamp + ", lang=" + lang + "]";
	}

	public static Tweet buildTweet(StreamingTweetResponse response) {
		Tweet tweet = new Tweet();
		
		com.twitter.clientlib.model.Tweet tweetData = response.getData();
		
		if(tweetData != null) {
			tweet.setId(tweetData.getId());
			tweet.setAuthorId(tweetData.getAuthorId());
			tweet.setSource(tweetData.getSource());
			tweet.setText(tweetData.getText());
			tweet.setLang(tweetData.getLang());
			tweet.setTimeStamp((tweetData.getCreatedAt().toEpochSecond() * 1000) + "");
		}
		
		Expansions includes = response.getIncludes();
		if(includes != null) {
			List<User> users = includes.getUsers();
			
			if(users != null) {
				for(User user: users) {
					if(tweet.authorId.equals(user.getId())) {
						tweet.setUsername(user.getUsername());
						break;
					}
				}
			}
			
			List<Place> places = includes.getPlaces();
			
			if(places != null && places.size() > 0) {
				Place place = places.get(0);
				
				if(place != null && place.getGeo() != null && place.getGeo().getBbox() != null) {
					String s = place.getGeo().getBbox().toString();
					tweet.setGeoBbox(s.substring(1, s.length() - 1)); // 23, 23, 23, 23 (removed the square brackets)
				}
			}
		}
		
		tweet.setHashTags();
		tweet.setRetweet();
		
		return tweet;
	}
}
