package projects.askfm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class User implements Serializable {

	private static final long serialVersionUID = 2L;
	transient Scanner sc = new Scanner(System.in);

	private final int ID;
	private final String USERNAME;
	private final int PASSWORD;
	private final String EMAIL;
	private final boolean ALLOW_ANONYMOUSE_QUESTIONS; // false ==> I can ask in an anonymous way (depend of the answerer
														// sittings) AND other Can't ask me
														// in an anonymous way.
	private Map<Question, List<Question>> QuestionToMe; // map parent q to list of thread questions
	private List<Question> QuestionFromMe;

	public User(String uSERNAME, int pASSWORD, String eMAIL, Boolean aLLOW_ANONYMOUSE_QUESTIONS) {
		ID = (int) Math.floor(Math.random() * (200 - 100 + 1) + 100); // 100 <= id <= 200
		USERNAME = uSERNAME;
		PASSWORD = pASSWORD;
		EMAIL = eMAIL;
		ALLOW_ANONYMOUSE_QUESTIONS = aLLOW_ANONYMOUSE_QUESTIONS;
		QuestionToMe = new HashMap<>();
		QuestionFromMe = new ArrayList<>();
	}

	public int getID() {
		return ID;
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public int getPASSWORD() {
		return PASSWORD;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public boolean isALLOW_ANONYMOUSE_QUESTIONS() {
		return ALLOW_ANONYMOUSE_QUESTIONS;
	}

	public void printQuestionToMe() {
		if (QuestionToMe.isEmpty())
			System.out.println("There is no question to you, yet.");
		else
			for (Map.Entry<Question, List<Question>> entry : QuestionToMe.entrySet()) {
				Question key = entry.getKey();
				List<Question> val = entry.getValue();
				System.out.println(key.toString());
				for (Question question : val) {
					System.out.println(question.toString());
				}
			}
	}

	public void printQuestionFromMe() {
		if (QuestionFromMe.isEmpty())
			System.out.println("There is no question from you, yet.");
		else
			for (Question question : QuestionFromMe) {
				System.out.println(question.toString());
			}
	}

	public Question answerQuestion(int QuestionId) {
		sc = new Scanner(System.in);
		Question question = getQuestionById_ToMe(QuestionId);
		if (question != null) {
			if (!question.getAnswer().equals("")) {
				System.out.println(
						"Warning, This question is already answerd \nDo you want to update your answer? (1 or 0)");
				int choice = sc.nextInt();
				if (choice == 0)
					return null;
			}
			System.out.print("Enter Your Answer: ");
			String answer = sc.nextLine();
			question.setAnswer(answer); // update the answer in my QuestionToMe list.
			return question;
		}
		System.out.println("Ther is no question to you with this Id.");
		return null;
	}

	private Question getQuestionById_ToMe(int QId) {
		for (Map.Entry<Question, List<Question>> entry : QuestionToMe.entrySet()) {
			Question key = entry.getKey();
			List<Question> val = entry.getValue();
			if (key.getId() == QId)
				return key;
			for (Question question : val) {
				if (question.getId() == QId)
					return question;
			}
		}
		return null;
	}

	public Question deleteQuestion(int QId) {
		int tempIndex = -1;
		Question tempQuestion = null;
		for (int i = 0; i < QuestionFromMe.size(); i++) {
			if (QuestionFromMe.get(i).getId() == QId) {
				tempIndex = i;
				break;
			}
		}
		if (tempIndex == -1) {
			System.out.println("Ther is no question from you with this Id.");
			return null;
		}
		tempQuestion = QuestionFromMe.get(tempIndex);
		QuestionFromMe.remove(tempIndex); // remove from mine
		return tempQuestion;
	}

	public Question askQuestion(int IdTo, boolean isAllowAnonyQ, int parentID) {
		sc = new Scanner(System.in);
		if (!isAllowAnonyQ)
			System.out.println("Note: Anonymous question not allowed to this user.");
		System.out.print("Enter the question text: ");
		String text = sc.nextLine();
		Question question = new Question(ID, IdTo, text, isAllowAnonyQ, parentID);
		QuestionFromMe.add(question);
		return question;
	}

	public void getAnswerFor_a_Question(int QId, String answer) {
		for (int i = 0; i < QuestionFromMe.size(); i++) {
			Question question = QuestionFromMe.get(i);
			if (question.getId() == QId) {
				question.setAnswer(answer);
				return;
			}
		}
	}

	public void deleteQuestion_to_Me(Question q) { // update my QuestionToMe list (sync)
		Question temp = null;
		if (q.getParentID() != -1) { // thread
			for (Map.Entry<Question, List<Question>> entry : QuestionToMe.entrySet()) {
				Question key = entry.getKey();
				if (key.getId() == q.getParentID()) {
					temp = key;
					break;
				}
			}
			QuestionToMe.get(temp).remove(q);
		} else { // parent q
			QuestionToMe.remove(q);
		}
	}

	public void getQuestion(Question q) {
		Question temp = null;
		if (q.getParentID() != -1) { // thread q
			for (Map.Entry<Question, List<Question>> entry : QuestionToMe.entrySet()) {
				Question key = entry.getKey();
				if (key.getId() == q.getParentID())
					temp = key;
				break;
			}
			QuestionToMe.get(temp).add(q);
		} else // parent q
			QuestionToMe.put(q, new ArrayList<Question>());
	}

	public void list_All_AnsweredQ() {
		for (Map.Entry<Question, List<Question>> entry : QuestionToMe.entrySet()) {
			Question key = entry.getKey();
			List<Question> val = entry.getValue();
			if (!key.getAnswer().equals(""))
				System.out.println(key.toString());
			for (Question question : val) {
				if (!question.getAnswer().equals(""))
					System.out.println(question.toString());
			}
		}
	}
}
