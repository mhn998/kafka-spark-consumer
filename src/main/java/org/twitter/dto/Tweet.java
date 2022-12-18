package org.twitter.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tweet {

	private String id;

	private String text;
	private boolean isRetweet;
	private String inReplyToStatusId;

	private List<String> hashTags = new ArrayList<String>();

	private String username;

	private String timeStamp;
	private String lang;

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

	public void setRetweet() {
		this.isRetweet = this.text != null ? this.text.startsWith("RT @") : false;
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
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

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", text=" + text + ", isRetweet="
				+ isRetweet + ", inReplyToStatusId=" + inReplyToStatusId
				+ ", hashTags=" + hashTags + ", username=" + username
				+ ", timeStamp=" + timeStamp + ", lang=" + lang + "]";
	}

	
}
