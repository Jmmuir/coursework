package course.work.muccoursework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class RSSParser implements Runnable{
	
	//a class to retrieve and parse the RSS feed.
	
	private ArrayList<RSSitem> items = new ArrayList<RSSitem>();
	private String RSSURL = "";
	private String rawRSS = "";

	public RSSParser(String url) {
		this.RSSURL = url;
	}

	@Override
	public void run() {
        getRSS();
		parseRSS(rawRSS);
	}
	
	//retrieve the raw RSS string from the given URL.
	private void getRSS(){
		String result = "";
		InputStream inStream = null;
		int response = -1;
		try
		{
			URLConnection conn = new URL(RSSURL).openConnection();

			// Check that the connection can be opened
			if (!(conn instanceof HttpURLConnection))
				throw new IOException("Not an HTTP connection");

			// Open connection
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK)
			{ 
				inStream = httpConn.getInputStream();
				InputStreamReader in= new InputStreamReader(inStream);
				BufferedReader bin= new BufferedReader(in);
				String line = new String();
				while (( (line = bin.readLine())) != null)
				{
					result = result + "\n" + line;
				}
				rawRSS = result.substring(2);
			}
		}
		catch (Exception e)
		{
			String message = e.getMessage();
			System.out.println(message);
		}

		
	}
	
	public ArrayList<RSSitem> getItems() {
		return items;
	}

	//parse the raw string retrieved into individual RSSitems.
	private void parseRSS(String RSSString){
		try{
			RSSitem currentItem;
			String title = "";
			String description = "";
			String pubDate = "";

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(RSSString));
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if(eventType == XmlPullParser.START_TAG) 
				{
					String tagName = xpp.getName().toLowerCase(new Locale("UK"));
					if(tagName.equals("title")){
						title = xpp.nextText();
					}
					else if(tagName.equals("description")){
						description = xpp.nextText();
					}
					else if(tagName.equals("pubdate")){
						pubDate = xpp.nextText();
						currentItem = new RSSitem(title, description, pubDate);
						items.add(currentItem);
					}
					
				}
				eventType = xpp.next();
			}
		}
		catch(Exception e){
			String message = e.getMessage();
			System.out.println(message);
		}
	}

}
