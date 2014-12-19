package course.work.muccoursework;

public class RSSitem {
	
	//a class which represents an individual item on the RSS feed.
	
	private String title;
	private String content;
	private String pubDate;

	public RSSitem() {
	}
	
	public RSSitem(String title, String content, String pubDate){
		this.title = title;
		this.content = content;
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

}
