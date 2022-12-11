package projects.askfm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Serializable {

	private static final long serialVersionUID = 3L;
	transient Scanner sc = new Scanner(System.in);

	private User CurrentUser = null; // initial
	List<User> users = new ArrayList<>();
	private static final String EMAIL_PATTERN = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
			+ "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";

	public void go() {
		// TODO: make sure you Uncomment the line below if initially there is no users
		// in
		// the system.
//		users = deserialize(); // fetch data from database(users.txt)   // here
		int tries = 3;
		if (!register())
			return;
		serialize(); // update database
		menu();
		while (tries > 0) {
			System.out.print("Enter number in range 1 - 8: ");
			int choice = sc.nextInt();
			users = deserialize(); // before we go to any action, we get the latest updates uploaded to the system
									// from the other users.
			CurrentUser = getUser(CurrentUser.getID());// update the reference(CurrentUser) after we fetch new changes.
			switch (choice) {
			case 1:
				CurrentUser.printQuestionToMe();
				serialize(); // update database(users.txt)
				break;
			case 2:
				CurrentUser.printQuestionFromMe();
				serialize();
				break;
			case 3: {
				answerQuestion_helper();
				serialize();
				break;
			}
			case 4:
				deleteQuestion_helper();
				serialize();
				break;
			case 5:
				askQuestion_helper();
				serialize();
				break;
			case 6:
				listSystemUsers();
				serialize();
				break;
			case 7:
				listFeedQuestions();
				serialize();
				break;
			case 8:
				System.out.println("Hope You Visite Us Again. \nGoodbye :)");
				serialize();
				return;
			default:
				System.out.println("Invalid Choice. Try Again");
				tries--;
				break;
			}
		}
	}

	private void answerQuestion_helper() {
		System.out.println("Enter Question Id or -1 to cancel: ");
		int id = sc.nextInt();
		if (id != -1) {
			Question question = CurrentUser.answerQuestion(id);
			if (question != null) { // it's means that the process goes well.
				// update the answer in the asker's QuestionFromMe list.
				getUser(question.getID_FROM()).getAnswerFor_a_Question(id, question.getAnswer());
				System.out.println("The answer updated successfully in your list & in the asker's list");
			}
		}
	}

	private void deleteQuestion_helper() {
		System.out.println("Enter Question Id or -1 to cancel: ");
		int id = sc.nextInt();
		if (id != -1) {
			Question question = CurrentUser.deleteQuestion(id); // delete questions I asked (from me) Only.
			if (question != null) { // it's means that the process goes well.
				// delete the question in the answerer's QuestionToMe list.
				getUser(question.getID_TO()).deleteQuestion_to_Me(question); // like a sync
				System.out.println(
						"The Qustion deleted successfully from your list & from the user's list who suppose to answer it.");
			}
		}
	}

	private void askQuestion_helper() {
		System.out.println("Enter the answerer's Id or -1 to cancel: ");
		int AnswererId = sc.nextInt();
		if (AnswererId != -1) {
			User Answerer = getUser(AnswererId);
			if (Answerer != null) {
				System.out.println("If thread question, enter parent question's id OR -1 to ask new qustion: ");
				int parentID = sc.nextInt(); // no way to errors here :|
				Question Q = CurrentUser.askQuestion(AnswererId, Answerer.isALLOW_ANONYMOUSE_QUESTIONS(), parentID);
				Answerer.getQuestion(Q);
				System.out.println("The question sent successfully.");
			} else {
				System.out.println("Ther is no users with this Id.");
			}
		}
	}

	private void listFeedQuestions() { // list of question that was answered so far.
		for (User user : users) {
			user.list_All_AnsweredQ();
		}
	}

	private void listSystemUsers() {
		for (User user : users) {
			System.out.println("ID: " + user.getID() + "\t Name: " + user.getUSERNAME());
		}
	}

	private void menu() {
		System.out.println("Menu:");
		System.out.println("\t 1: Print Question To Me");
		System.out.println("\t 2: Print Question From Me");
		System.out.println("\t 3: Answer Question");
		System.out.println("\t 4: Delete Question");
		System.out.println("\t 5: Ask Question");
		System.out.println("\t 6: List System Users");
		System.out.println("\t 7: List Feed Question");
		System.out.println("\t 8: Logout");
	}

	private boolean register() {
		while (true) {
			System.out.println("1: Login \n2: Sign Up \n3: Exit");
			int choice = sc.nextInt();
			if (choice == 1) {
				if (login())
					return true;
			} else if (choice == 2) {
				if (singup())
					return true;
			} else if (choice == 3) {
				return false;
			} else {
				System.out.println("Invalid Choice. Try again");
			}
		}
	}

	private boolean login() {
		System.out.println("Enter user name (no spaces) And Password");
		String UserName = sc.next();
		String PassWord = sc.next();
		User temp = getUser(UserName, PassWord);
		if (temp != null) {
			CurrentUser = temp;
			return true;
		}
		System.out.println("UserName or PassWord is Invalid. try again");
		return false;
	}

	private boolean singup() {
		System.out.println("Enter user name (no spaces):");
		String UserName = sc.next();
		System.out.println("Enter Password:");
		String PassWord = sc.next();
		System.out.println("Email:");
		String Email = sc.next();
		if (!isValidEmail(Email))
			return false;
		System.out.println("Allow anonymous questions (1 or 0):"); // no chance for wrong input :|
		int temp = sc.nextInt();
		boolean AllowAnonymousQuestions;
		if (temp == 1)
			AllowAnonymousQuestions = true;
		else
			AllowAnonymousQuestions = false;
		User newUser = new User(UserName, PassWord, Email, AllowAnonymousQuestions);
		users.add(newUser);
		CurrentUser = newUser;
		return true;

	}

	private User getUser(String userName, String passWord) {
		for (User user : users) {
			if ((user.getUSERNAME().equals(userName)) && (user.getPASSWORD().equals(passWord)))
				return user;
		}
		return null;
	}

	private User getUser(int UserId) {
		for (User user : users) {
			if (user.getID() == UserId)
				return user;
		}
		return null;
	}

	private boolean isValidEmail(String Email) {
		int tries = 3;
		while (tries-- > 0) {
			if (!VerifyEmail(Email)) {
				System.out.println("Invalid Email!");
				System.out.println("Email: ");
				Email = sc.next();
				continue;
			} else
				break;
		}
		if (tries < 0) {
			System.out.println("Your tries end. \nplease try again later");
			return false;
		}
		return true;
	}

	private boolean VerifyEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void serialize() {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("users.txt"));
			os.writeObject(users);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<User> deserialize() {
		List<User> users = new ArrayList<>();
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream("users.txt"));
			users = (ArrayList<User>) is.readObject();
			sc = new Scanner(System.in);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

}