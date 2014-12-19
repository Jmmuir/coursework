package course.work.muccoursework;

public class Question {
	
	private int ID;
	private String question;
	private String answer1;
	private String answer2;
	private String answer3;
	private int correct;
	
	//a class which represents a question in a quiz.

	public Question() {
		// TODO Auto-generated constructor stub
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}
	
	public void setAnswers(String ans1, String ans2, String ans3){
		this.answer1 = ans1;
		this.answer2 = ans2;
		this.answer3 = ans3;
	}

}
