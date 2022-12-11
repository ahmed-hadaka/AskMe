package projects.askfm;

import java.io.Serializable;

public class Question implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int ID;
	private final int ID_FROM;
	private final int ID_TO;
	private final boolean ANONYMOUSE_Q;
	private final int PARENT_ID;
	private final String TEXT;
	private String Answer;

	public Question(int iD_FROM, int iD_TO, String tEXT, boolean aNONYMOUSE_Q, int parentID) {
		ID = (int) Math.floor(Math.random() * (50 - 1 + 1) + 1); // 1 <= id <= 50
		ID_FROM = iD_FROM;
		ID_TO = iD_TO;
		TEXT = tEXT;
		Answer = "";
		ANONYMOUSE_Q = aNONYMOUSE_Q;
		PARENT_ID = parentID;
	}

	public int getId() {
		return ID;
	}

	public String getAnswer() {
		return this.Answer;
	}

	public int getID_FROM() {
		return ID_FROM;
	}

	public int getID_TO() {
		return ID_TO;
	}

	public int getParentID() {
		return this.PARENT_ID;
	}

	public void setAnswer(String Answer) {
		this.Answer = Answer;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("");
		if (PARENT_ID != -1) {
			s.append("\t Thread: ");
		}
		if (ANONYMOUSE_Q) {
			s.append("Question [ID: " + ID + ", ID_From: Anonymouse" + ", ID_TO: " + ID_TO + ", TEXT: " + TEXT
					+ ", Answer: " + Answer + "]");
			return s.toString();
		}

		s.append("Question [ID: " + ID + ", ID_FROM: " + ID_FROM + ", ID_TO: " + ID_TO + ", TEXT: " + TEXT
				+ ", Answer: " + Answer + "]");
		return s.toString();
	}

}