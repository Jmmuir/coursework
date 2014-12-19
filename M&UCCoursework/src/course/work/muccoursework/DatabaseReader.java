package course.work.muccoursework;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseReader extends SQLiteOpenHelper {
	
	//a class for reading from the database.
	
	private static String DB_Path;
	private static String DB_Name;
	private Context context;
	
	//create a database reader, so that we can load resources from the specified path.
	public DatabaseReader(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		DB_Path = "data/data/" + "course.work.muccoursework" + "/databases/";
		DB_Name = name;
		this.context = context;
	}

	//load the locations in.
	public ArrayList<Location> createLocationsFromDatabase(){
		ArrayList<Location> locationList = new ArrayList<Location>();
		Location location;
		String query = "Select * FROM Locations";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
        	do{
        	location = new Location();
            location.setID(Integer.parseInt(cursor.getString(0)));
            location.setName(cursor.getString(1));
            location.setDescription(cursor.getString(2));
            location.setImage(cursor.getString(3));
            location.setLock(cursor.getInt(4)>0);
            location.setLatitude(cursor.getFloat(5));
            location.setLongitude(cursor.getFloat(6));
            location.setCoordinates(new LatLng(location.getLatitude(), location.getLongitude()));
            locationList.add(location);
        	}while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
		return locationList;
	}
	
	//load the quizzes from the database
	public ArrayList<Quiz> createQuizzesFromDatabase(){
		ArrayList<Quiz> quizList = new ArrayList<Quiz>();
		Quiz quiz;
		String query = "Select * FROM Quizzes";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
        	do{
        	quiz = new Quiz();
            quiz.setID(Integer.parseInt(cursor.getString(0)));
            quiz.setName(cursor.getString(1));
            quizList.add(quiz);
        	}while(cursor.moveToNext());
        }
        
        Question question;
        
        for(Quiz q : quizList){
        	query = "Select * FROM Questions WHERE Quiz = " + q.getID();
        	cursor = db.rawQuery(query, null);
        	ArrayList<Question> questionList = new ArrayList<Question>();
        	if (cursor.moveToFirst()) {
            	do{
            	question = new Question();
                question.setID(Integer.parseInt(cursor.getString(0)));
                question.setQuestion(cursor.getString(2));
                question.setAnswer1(cursor.getString(3));
                question.setAnswer2(cursor.getString(4));
                question.setAnswer3(cursor.getString(5));
                question.setCorrect(Integer.parseInt(cursor.getString(6)));
                questionList.add(question);
            	}while(cursor.moveToNext());
            } else {
              throw new Error("Found quiz with no questions!"); 
            }
        	q.setQuestionList(questionList);
        }
        
        cursor.close();
        db.close();
		return quizList;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	//create the database on startup.
	public void dbCreate() throws IOException {

        boolean dbExist = dbCheck();

        if(!dbExist){
            //By calling this method an empty database will be created into the default system path
            //of your application so we can overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDBFromAssets();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }
	
	private boolean dbCheck(){

        SQLiteDatabase db = null;

        try{
            String dbPath = DB_Path + DB_Name;
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            db.setLocale(Locale.getDefault());
            db.setVersion(1);

        }catch(SQLiteException e){

            Log.e("SQLHelper","Database not Found!");

        }

        if(db != null){

            db.close();

        }

        return db != null;
    }
	
	private void copyDBFromAssets() throws IOException{

        InputStream dbInput = null;
        OutputStream dbOutput = null;
        String dbFileName = DB_Path + DB_Name;

        try {

            dbInput = this.context.getAssets().open(DB_Name);
            dbOutput = new FileOutputStream(dbFileName);
            //transfer bytes from the dbInput to the dbOutput
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dbInput.read(buffer)) > 0) {
                dbOutput.write(buffer, 0, length);
            }

            //Close the streams
            dbOutput.flush();
            dbOutput.close();
            dbInput.close();
        } catch (IOException e)
        {
            throw new Error("Problems copying DB!");
        }
    }

	public boolean setLocationLock(Location location, boolean isLocked) throws IOException{
		try{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues newValues = new ContentValues();
			newValues.put("Unlocked", String.valueOf(isLocked));
			String helpme = String.valueOf(isLocked);
			//int i =db.update("Locations", newValues, "ID=" + String.valueOf(location.getID()), null);
			
			String query = "UPDATE Locations SET Unlocked = 'true' WHERE ID = '1'";
			Cursor cursor = db.rawQuery(query, null);
			
			//ArrayList<Location> locationList = this.createLocationsFromDatabase();
			
			ArrayList<Location> locationList = new ArrayList<Location>();
			
		    query = "SELECT * FROM Locations";
			cursor = db.rawQuery(query, null);

	        if (cursor.moveToFirst()) {
	        	do{
	        	location = new Location();
	            location.setID(Integer.parseInt(cursor.getString(0)));
	            location.setName(cursor.getString(1));
	            location.setDescription(cursor.getString(2));
	            location.setImage(cursor.getString(3));
	            location.setLock(cursor.getInt(4)>0);
	            locationList.add(location);
	        	}while(cursor.moveToNext());
	        } else {
	          //do nothing 
	        }
			
			db.close();
			return true;
		}
		catch(SQLiteException e){
			return false;
		}
	}
}
