package course.work.muccoursework;


import java.io.IOException;
import java.util.ArrayList;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnMenuItemClickListener, OnItemSelectedListener, OnClickListener {

	private String RSSURL = "http://www.repeatserver.com/Users/JamesMuir/development.xml";
	private DatabaseReader databaseReader = new DatabaseReader(this, "MAUCDatabas.s3db", null, 1);
	private ArrayList<Location> locationList;
	private ArrayList<Quiz> quizList;
	private Spinner screenSelect;
	private Spinner quizSelect;
	private TableLayout quizTable;
	private Button quizStart;
	private int currentQuiz;
	private boolean screenDrawn = false;
	private GoogleMap googleMap;
	private float markerColour = 210.0f;
	private RSSParser parser;
	private int currentScreen;
	private float tempMarker = 0f;
	private SurfaceHolder surfaceHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//prepare the RSS feed for when the user opens that page.
		parser = new RSSParser(RSSURL);
		Thread parsingThread = new Thread(parser);
		parsingThread.start();
		
		//load in the currently preferred marker colour,default to azure.
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		markerColour = settings.getFloat("markerColour", 210f);
		
		//initialise the MapsInitializer for when the map needs to be loaded
		MapsInitializer.initialize(getApplicationContext());
		
		//set the layout to the default and set it up
		setContentView(R.layout.activity_main);
		screenSelect = (Spinner)findViewById(R.id.SpinnerSelectScreen);
		ArrayAdapter<CharSequence> screenSelectAdapter = ArrayAdapter.createFromResource(this,
				R.array.screensArray, android.R.layout.simple_spinner_item);
		screenSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		screenSelect.setAdapter(screenSelectAdapter);
		try {
			databaseReader.dbCreate();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Problem occurred trying to instantiate the database.");
		}
		
		//get the list of locations from the database
		locationList = databaseReader.createLocationsFromDatabase();
		for(Location l : locationList){
			MarkerOptions marker = new MarkerOptions();
			marker.icon(BitmapDescriptorFactory.defaultMarker(markerColour));
			marker.position(l.getCoordinates());
			marker.title(l.getName());
			marker.snippet(l.getDescription());
			l.setMarker(marker);
		}
		
		//get the list of quizzes from the database
		quizList = databaseReader.createQuizzesFromDatabase();
		String[] nameList = new String[quizList.size()];
		for(int i = 0; i < quizList.size(); i++){
			nameList[i] = quizList.get(i).getName();
		}
		
		//populate the quiz selecting spinner with the retrieved quizzes.
		ArrayAdapter<String> quizSelectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, nameList);
		quizSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		quizSelect = (Spinner)findViewById(R.id.SpinnerSelectQuiz);
		quizSelect.setAdapter(quizSelectAdapter);
		
		//prep the layout for population if a quiz is started
		quizTable = (TableLayout)findViewById(R.id.TableQuizDisplay);
		quizStart = (Button)findViewById(R.id.QuizStartButton);
		quizStart.setOnClickListener(this);
		screenSelect.setOnItemSelectedListener(this);
	}
	
	//opens a specific quiz, so that it can be seen and played by the user.
	public void openQuiz(Quiz quiz, TableLayout quizTable, Context context){
		//get rid of any existing quiz.
		quizTable.removeAllViews();
		ArrayList<Question> currentQuestions = quiz.getQuestionList();
		//get the width of the screen so that the text can be restricted and won't flow off.
		int width = (context.getResources().getDisplayMetrics().widthPixels - 100);
		int i = 1;
		//for each question in the quiz, contruct a row in the quiz table.
		for(Question q : currentQuestions){
			TableRow questionRow = new TableRow(context);
			LinearLayout questionLayout = new LinearLayout(context);
			questionLayout.setOrientation(LinearLayout.VERTICAL);
			TextView questionText = new TextView(context);
			questionText.setMaxWidth(width);
			questionText.setPadding(0, 10, 0, 10);
			TextView answerText1 = new TextView(context);
			answerText1.setMaxWidth(width);
			answerText1.setPadding(0, 10, 0, 10);
			TextView answerText2 = new TextView(context);
			answerText2.setMaxWidth(width);
			answerText2.setPadding(0, 10, 0, 10);
			TextView answerText3 = new TextView(context);
			answerText3.setMaxWidth(width);
			answerText3.setPadding(0, 10, 0, 10);
			answerText1.setTag("answer" + i);
			answerText1.setOnClickListener(this);
			answerText2.setTag("answer" + i);
			answerText2.setOnClickListener(this);
			answerText3.setTag("answer" + i);
			answerText3.setOnClickListener(this);
			questionText.setText(i + ". " + q.getQuestion());
			answerText1.setText("A: " + q.getAnswer1());
			answerText2.setText("B: " + q.getAnswer2());
			answerText3.setText("C: " + q.getAnswer3());
			TextView spacerText = new TextView(this);
			spacerText.setText("\n");
			spacerText.setTag("spacer");
			questionLayout.addView(questionText);
			questionLayout.addView(answerText1);
			questionLayout.addView(answerText2);
			questionLayout.addView(answerText3);
			questionLayout.addView(spacerText);
			questionRow.addView(questionLayout);
			quizTable.addView(questionRow);
			i++;
		}
		//add the button to finish the quiz.
		TableRow buttonRow = new TableRow(context);
		Button finishButton = new Button(context);
		finishButton.setText("Finish Quiz");
		finishButton.setTag("quizFinish");
		finishButton.setOnClickListener(this);
		buttonRow.addView(finishButton);
		quizTable.addView(buttonRow);
		currentQuiz = quizSelect.getSelectedItemPosition();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//remember the screen the user was on so that we can go back to it.
			currentScreen = screenSelect.getSelectedItemPosition();
			//manually load the settings screen.
			setScreen(-1);
			setUpViews(-1);
			return true;
		}
		if(id == R.id.action_about){
			//create a dialogue to display the about text.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("This app was created as part of the coursework requirements for the Mobile and Ubiquitous Computing module. \n \n Complete quizzes correctly to unlock locations and view them on the map.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//do nothing
				}
			});
			AlertDialog showResults = builder.create();
			showResults.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		//when the spinner is initially loaded, this counts as an item being selected, screenDrawn will be set to true
		//whenever a new screen is drawn to prevent infinite looping.
		if(!screenDrawn){
			if(parent.getId() == R.id.SpinnerSelectScreen){
				setScreen(screenSelect.getSelectedItemPosition());
				setUpViews(position);
			}
		}
		else if(parent.getId() == R.id.SpinnerSelectScreen){
			//aborted a change because it was initiated by the spinner being drawn- re-enable changes.
			screenDrawn = false;
		}
		if(parent.getId() == R.id.SpinnerSelectColour){
			//extract the selected colour as a hue-based float.
			String colourString = "";
			switch(position){
			case 0: tempMarker = 210f;
					colourString = "azure";
				break;
			case 1: tempMarker = 240f;
					colourString = "blue";
				break;
			case 2: tempMarker = 180f;
					colourString = "cyan";
				break;
			case 3: tempMarker = 120f;
					colourString = "green";
				break;
			case 4: tempMarker = 300f;
					colourString = "magenta";
				break;
			case 5: tempMarker = 30f;
					colourString = "orange";
				break;
			case 6: tempMarker = 0f;
					colourString = "red";
				break;
			case 7: tempMarker = 330f;
					colourString = "rose";
				break;
			case 8: tempMarker = 270f;
					colourString = "violet";
				break;
			case 9: tempMarker = 60f;
					colourString = "yellow";
				break;
			}
			//draw a rectangle in the chosen colour onto a canvas, for preview.
			surfaceHolder = ((SurfaceView) findViewById(R.id.colourCanvas)).getHolder();
			Canvas c = surfaceHolder.lockCanvas();
			Paint paint = new Paint();
			float[] hsv = new float[3];
			hsv[0] = tempMarker;
			hsv[1] = 1f;
			hsv[2] = 1f;
			int colour = Color.HSVToColor(hsv);
			paint.setColor(colour);
			c.drawRect(0f, 0f, c.getWidth(), c.getHeight(), paint);
			surfaceHolder.unlockCanvasAndPost(c);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ButtonNormal){
			//the "normal view" button on the maps page- revert the map to its default state
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		if(v.getId() == R.id.ButtonSatellite){
			//the satellite button on the maps page- turn the map into satellite mode
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		}
		if(v.getId() == R.id.QuizStartButton){
			//the Start Quiz button- open the selected quiz.
			this.openQuiz(quizList.get(quizSelect.getSelectedItemPosition()), quizTable, this);
		}
		if(v.getId() == R.id.ButtonSavePreferences){
			//The save preferences button on the settings page- save the chosen colour as the preferred one.
			markerColour = tempMarker;
			saveSettings();
			updateMarkers();
			setScreen(currentScreen);
			setUpViews(currentScreen);
		}
		if(v.getId() == R.id.ButtonQuitPreferences){
			//revert back to the old screen without saving any changes.
			setScreen(currentScreen);
			setUpViews(currentScreen);
		}
		if(v.getTag() != null && v.getTag().equals("quizFinish")){
			//the quiz finish button- assess the user's performance and print the results.
			ArrayList<Integer> correctQuestions = new ArrayList<Integer>();
			for(int i=0; i<quizTable.getChildCount() -1; i++){
				TableRow questionRow = (TableRow)quizTable.getChildAt(i);
				LinearLayout questionLayout = (LinearLayout)questionRow.getChildAt(0);
				//go through each question looking for the selected answer.
				for(int j=1; j < questionLayout.getChildCount(); j++){
					View answer = questionLayout.getChildAt(j);
					if(answer.getTag().toString().contains("selectedAns") && j == quizList.get(currentQuiz).getQuestionList().get(i).getCorrect()){
						correctQuestions.add(Integer.valueOf(i+1));
					}
				}
			}
			String quizResults = "You got the following questions correct: ";
			for(Integer q : correctQuestions){
				quizResults += q.toString() + ", ";
			}
			quizResults += "\n \n Giving you a score of: " + correctQuestions.size() + "/" + quizList.get(currentQuiz).getQuestionList().size(); 
			if(correctQuestions.size()==quizList.get(currentQuiz).getQuestionList().size() && !locationList.get(currentQuiz).isUnlocked()){
				//tell the user they unlocked a new location if they didn't already have it and got full score.
				quizResults += "\n You've unlocked a new location!";
				locationList.get(currentQuiz).setLock(true);
				//play a sound, just for funsies.
				Ding ding = new Ding(this);
				Thread musicThread = new Thread(ding);
				musicThread.start();
				//unfinished- attempt to save the location being unlocked to the database.
				try {
					databaseReader.setLocationLock(locationList.get(currentQuiz), (true));
				} catch (IOException e) {
					throw new Error("Couldn't save results!");
				}
			}
			//output the results as a dialogue.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(quizResults)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//do nothing
				}
			});
			AlertDialog showResults = builder.create();
			showResults.show();
		}
		//if the user hit an answer, set that to a selected answer, and go through all other answers 
		//for that question, setting them to unselected.
		if(v.getTag() != null && v.getTag().toString().contains("answer")){
			int i = Integer.parseInt(v.getTag().toString().substring(v.getTag().toString().length()-1));
			TableRow questionRow = (TableRow)quizTable.getChildAt(i-1);
			LinearLayout questionLayout = (LinearLayout)questionRow.getChildAt(0);
			for(int j = 1; j < questionLayout.getChildCount(); j++){
				if(questionLayout.getChildAt(j).getTag().toString().contains("selectedAns")){
					questionLayout.getChildAt(j).setTag("answer" + i);
					questionLayout.getChildAt(j).setBackgroundColor(0xFFEBEBEB);
				}
			}
			v.setTag("selectedAns" + i);
			v.setBackgroundColor(0xFFBBBBBB);
		}

	}
	
	//used to set up the views for a particular layout- the screen select spinner is always used for any layout which
	//can be loaded using it.
	private void setUpViews(int layoutID){
		if(googleMap != null){
			//destroy the map's container fragment if it exists- this avoids issues when trying to reload it.
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment f = fm.findFragmentById(R.id.map);
			ft.remove(f);
			ft.commit();
			googleMap = null;
		}
		//get the screen width so that views can be reigned in.
		int width = (this.getResources().getDisplayMetrics().widthPixels - 100);
		if(layoutID > -1){
			//load the screen select spinner, unless the layout cannot possibly be reached by using the spinner.
			screenSelect = (Spinner)findViewById(R.id.SpinnerSelectScreen);
			screenSelect.setOnItemSelectedListener(this);
			ArrayAdapter<CharSequence> screenSelectAdapter = ArrayAdapter.createFromResource(this,
				R.array.screensArray, android.R.layout.simple_spinner_item);
			screenSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			screenSelect.setAdapter(screenSelectAdapter);
			screenSelect.setSelection(layoutID);
		}
		switch(layoutID){
		//load resources for settings.
		case -1: Spinner colourSelect = (Spinner)findViewById(R.id.SpinnerSelectColour);
		colourSelect.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> colourSelectAdapter = ArrayAdapter.createFromResource(this,
				R.array.colourArray, android.R.layout.simple_spinner_item);
		colourSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		colourSelect.setAdapter(colourSelectAdapter);
		
		Button saveButton = (Button)findViewById(R.id.ButtonSavePreferences);
		saveButton.setOnClickListener(this);
		
		Button quitButton = (Button)findViewById(R.id.ButtonQuitPreferences);
		quitButton.setOnClickListener(this);
			break;
			//load resources for quizzes.
		case 0:  String[] nameList = new String[quizList.size()];
		for(int i = 0; i < quizList.size(); i++){
			nameList[i] = quizList.get(i).getName();
		}
		ArrayAdapter<String> quizSelectAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, nameList);
		quizSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		quizSelect = (Spinner)findViewById(R.id.SpinnerSelectQuiz);
		quizSelect.setAdapter(quizSelectAdapter);
		quizTable = (TableLayout)findViewById(R.id.TableQuizDisplay);
		quizStart = (Button)findViewById(R.id.QuizStartButton);
		quizStart.setOnClickListener(this);
		break;
		//load resources for locations.
		case 1: for(Location l : locationList){
			TableLayout locationTable = (TableLayout)findViewById(R.id.TableLocationDisplay);
			TableRow locationRow = new TableRow(this);
			TextView locationText = new TextView(this);
			locationText.setMaxWidth(width);
			TableRow spacerRow = new TableRow(this);
			TextView spacerView = new TextView(this);
			spacerView.setText("\n \n");
			spacerRow.addView(spacerView);
			if(l.isUnlocked()){
				locationText.setText(l.getName() + "\n \n" + l.getDescription());
				locationRow.addView(locationText);
				locationRow.setBackgroundResource(R.drawable.back);
				locationTable.addView(locationRow);
				locationTable.addView(spacerRow);
			}
		}
		break;
		//load resources for the map.
		case 2: initialiseMap();
				Button normalMapButton = (Button)findViewById(R.id.ButtonNormal);
				normalMapButton.setOnClickListener(this);
				Button satMapButton = (Button)findViewById(R.id.ButtonSatellite);
				satMapButton.setOnClickListener(this);
		break;
		//load resources for the RSS feed.
		case 3: TableLayout RssTable = (TableLayout)findViewById(R.id.TableRssDisplay);
			if(parser.getItems().size() < 1){
			TextView itemText = new TextView(this);
			itemText.setText("Could not read or have not finished reading RSS- please try again in a minute.");
			RssTable.addView(itemText);
			}
		for(RSSitem r : parser.getItems()){
			TableRow itemRow = new TableRow(this);
			TextView itemText = new TextView(this);
			itemText.setText(r.getTitle() + "\n \n" + r.getContent() + "\n \n" + r.getPubDate() + "\n \n \n");
			
			itemRow.addView(itemText);
			RssTable.addView(itemRow);
		}
		break;
		}
		screenDrawn = true;
	}

	//load the map, or print an error if it doesn't work.
	private void initialiseMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment)getFragmentManager().findFragmentById(
					R.id.map)).getMap(); 
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8635984,-4.2544313) , 15.0f));
			for(Location l : locationList){
				if(l.isUnlocked()){
					googleMap.addMarker(l.getMarker());
				}
			}
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	
	//set the current layout to the one denoted by screenChoice.
	private void setScreen(int screenChoice){
		switch(screenChoice){
		case -1:setContentView(R.layout.preferences);
		break;
		case 0: setContentView(R.layout.activity_main);
		break;
		case 1: setContentView(R.layout.activity_location);
		break;
		case 2: setContentView(R.layout.activity_map);
		break;
		case 3: setContentView(R.layout.activity_rss);
		break;
		}
	}
	
	//save the settings to the shared preferences.
	private void saveSettings(){
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("markerColour", markerColour);
		editor.commit();
	}
	
	//update the colour of markers.
	private void updateMarkers(){
		for(Location l : locationList){
			l.setMarker(l.getMarker().icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
		}
	}
}

