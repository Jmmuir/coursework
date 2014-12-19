package course.work.muccoursework;

import java.util.ArrayList;

import android.content.Context;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Quiz {
	
	//a class which represents an individual quiz
	
	private int ID;
	private String name;
	private ArrayList<Question> questionList;

	public Quiz() {
		
	}
	
	public Quiz(int ID, String name, ArrayList<Question> questionList) {
		this.ID = ID;
		this.name = name;
		this.questionList = questionList;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Question> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(ArrayList<Question> questionList) {
		this.questionList = questionList;
	}

	
}
