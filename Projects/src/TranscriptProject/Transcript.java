package TranscriptProject;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/* PLEASE DO NOT MODIFY A SINGLE STATEMENT IN THE TEXT BELOW.
READ THE FOLLOWING CAREFULLY AND FILL IN THE GAPS

I hereby declare that all the work that was required to 
solve the following problem including designing the algorithms
and writing the code below, is solely my own and that I received
no help in creating this solution and I have not discussed my solution 
with anybody. I affirm that I have read and understood
the Senate Policy on Academic honesty at 
https://secretariat-policies.info.yorku.ca/policies/academic-honesty-senate-policy-on/
and I am well aware of the seriousness of the matter and the penalties that I will face as a 
result of committing plagiarism in this assignment.

BY FILLING THE GAPS,YOU ARE SIGNING THE ABOVE STATEMENTS.

Full Name: Erika Grandy
Student Number: 217300948
Course Section: Section E
*/

/**
 * This class generates a transcript for each student, whose information is in
 * the text file.
 * 
 *
 */

public class Transcript {

	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;

	/**
	 * This the the constructor for Transcript class that initializes its instance
	 * variables and call readFie private method to read the file and construct
	 * this.grade.
	 * 
	 * @param inFile  is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);
		outputFile = outFile;
		grade = new ArrayList<Object>();
		this.readFile();
	}// end of Transcript constructor

	// Transcript Getters

	public File getInputFile() {
		return inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	// Transcript Setters

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * This method reads a text file and add each line as an entry of grade
	 * ArrayList.
	 * 
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null;
		try {
			sc = new Scanner(inputFile);
			while (sc.hasNextLine()) {
				grade.add(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	} // end of readFile

	/**
	 * This method builds an array list of students, using the objects that were
	 * read from the txt file.
	 * 
	 * @return An array list of students created from text file.
	 */
	public ArrayList<Student> buildStudentArray() {

		ArrayList<Student> toReturn = new ArrayList<Student>(); // Array of Students already built (Parallel)
		ArrayList<Integer> idDone = new ArrayList<Integer>(); // ID's of Students already built (Parallel)

		ArrayList<Double> grades = new ArrayList<Double>(); // Grades student received
		ArrayList<Integer> weights = new ArrayList<Integer>(); // Weightings for those grades

		ArrayList<Assessment> assignments = new ArrayList<Assessment>(); // Assignments for a specific course
		Course course; // The specific course

		Student currentStudent; // Student currently working on
		String[] tokens; // Tokens from line
		int id; // ID of current student

		for (Object e : grade) { // For all lines in file

			e = (String) e; // Convert line to string
			tokens = ((String) e).split(","); // Tokenize line

			id = Integer.parseInt(tokens[2]); // Get ID from line

			grades = new ArrayList<Double>();
			weights = new ArrayList<Integer>();
			assignments = new ArrayList<Assessment>();

			if (!idDone.contains(id)) { // New student found
				// Create new Student Object
				currentStudent = new Student(tokens[2], tokens[tokens.length - 1], new ArrayList<Course>());

			} else { // Existing Student
				currentStudent = toReturn.get(idDone.indexOf(id)); // Get their student object
			}

			String courseID = tokens[0]; // Get the courseID from line
			int credit = Integer.parseInt(tokens[1]); // Get credit amount from line

			for (int i = 3; i < tokens.length - 1; i++) { // For all assessments in course

				char type = tokens[i].charAt(0); // Get type of assessment
				int worth = Integer.parseInt(tokens[i].substring(1, 3)); // Get worth of assessment
				double grade = Double.parseDouble(tokens[i].substring(4, tokens[i].length() - 1)); // Get Grade of
																									// assessment
				grades.add(grade); // Add grade to grade list
				weights.add(worth); // Add weight to weight list

				assignments.add(Assessment.getInstance(type, worth)); // Create the assignment, and add to assignment
																		// list

			}

			course = new Course(courseID, assignments, credit); // Create the course, using ID, assignment list, and
																// credit
			currentStudent.addCourse(course); // Add course for student

			try {
				currentStudent.addGrade(grades, weights); // Add grade for student, given weightings and final grade
															// were correct
			} catch (Exception e1) {
			}

			if (!idDone.contains(id)) { // If student was created for first time
				idDone.add(id); // Add ID to list of ID's done
				toReturn.add(currentStudent); // Add the new Student to the list
			}

		}
		printTranscript(toReturn);
		return toReturn; // Return completed list of students
	}

	/**
	 * Prints the information about each student to the output file.
	 * 
	 * @param input - ArrayList of type Students to be printed to outputFile
	 */
	public void printTranscript(ArrayList<Student> input) {
		
		PrintWriter outputStream = null;
		String output = "";
		
		try {
			
			File outFile = new File(outputFile);
			outputStream = new PrintWriter(outFile);
			
			ArrayList<Course> courses; // Lists of courses and grades to be printed
			ArrayList<Double> grades;

			for (Student e : input) { // For all Students inputted
				courses = e.getCourseTaken(); // Get their list of courses and grades
				grades = e.getFinalGrade();

				output += e.getName() + "\t" + e.getStudentID() + "\n"; // Output their name and ID
				output += "--------------------\n";

				for (int i = 0; i < courses.size(); i++) { // For every course they took
					output += courses.get(i).getCode() + "\t" + grades.get(i) + "\n"; // Output the course ID and their
																							// final grade
				}

				output += "--------------------\n";
				output += "GPA: " + e.weightedGPA() + "\n\n"; // Output their weighted GPA
			}
			
			outputStream.println(output);
			System.out.println(output);
			System.out.println("Transcript Written To File: " + outputFile);
			
		} catch (FileNotFoundException e) {
			System.out.println("Output file cannot be created");
		} finally {
			outputStream.close();
		}
		
	}
} // end of Transcript

/**
 * Exception for a final grade above 100% or assessment weightings not adding up
 * to 100
 */
class InvalidTotalException extends Exception {
	public InvalidTotalException() {
		super();
	}

	public InvalidTotalException(String message) {
		super(message);
	}
}

class Student { // --------------------------------------------Student Class
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade;

	public Student() {
		studentID = "";
		name = "";
		courseTaken = new ArrayList<Course>();
		finalGrade = new ArrayList<Double>();
	}

	public Student(String id, String name, ArrayList<Course> courses) {
		this();
		this.studentID = id;
		this.name = name;
		for (int i = 0; i < courses.size(); i++) {
			courseTaken.add(new Course(courses.get(i)));
		}
	}

	/**
	 * 
	 * @param grades  - List of grades to be added
	 * @param weights - ArrayList parallel to grades, of each grades weighting
	 * @throws InvalidTotalException - Throws if students total grade is above 100%,
	 *                               or weights do not add up to 100.
	 */
	public void addGrade(ArrayList<Double> grades, ArrayList<Integer> weights)  {
		double grade = 0;
		double finalGrade = 0;
		int totalWeight = 0;

		for (int i = 0; i < grades.size(); i++) { // For all grades given

			grade = grades.get(i) * weights.get(i) / 100.0; // Determine contribution to total grade
			//grade = Math.round(grade * 10.0) / 10.0; // Round to 1 decimal place

			finalGrade += grade; // Add to finalGrade
			totalWeight += weights.get(i); // Keep count of total weightings
		}
		finalGrade = Math.round(finalGrade * 10.0) / 10.0; // Round the final grade
		

		try {
			if (finalGrade > 100) { // Throw exception for final grade greater than 100
				throw new InvalidTotalException("Error: The student's total grade exceeded 100%. " + "(Student Number: "
						+ this.studentID + ")\nGrade has been set to -1, please fix.\n");
			}
			if (totalWeight != 100) { // Throw exception for assessment weightings not adding up to 100.
				throw new InvalidTotalException("Error: The total weight of assessments does not add up to 100. "
						+ "(Student Number: " + this.studentID + ")\nGrade has been set to -1, please fix.\n");
			}

		} catch (Exception e) {

			finalGrade = -1; // Grade set to -1 if error occurs.
			System.out.println(e.getMessage());

		} finally {

			this.finalGrade.add(finalGrade); // Add the final grade to the list.

		}

	}

	/**
	 * Determines the weightedGPA for the student, using the courses they've taken
	 * and grades from those courses.
	 * 
	 * @return - Weighted GPA, on a 9.0 scale
	 */
	public double weightedGPA() {
		double gpa = 0;
		double grade;
		int numCredits = 0;
		int gradePoint = 0;

		for (int i = 0; i < this.courseTaken.size(); i++) { // For all courses taken

			grade = this.finalGrade.get(i); // Get grade

			if (grade < 47) { // Convert grade to grade point (9.0 scale)
				gradePoint = 0;
			} else if (grade < 50) {
				gradePoint = 1;
			} else if (grade < 55) {
				gradePoint = 2;
			} else if (grade < 60) {
				gradePoint = 3;
			} else if (grade < 65) {
				gradePoint = 4;
			} else if (grade < 70) {
				gradePoint = 5;
			} else if (grade < 75) {
				gradePoint = 6;
			} else if (grade < 80) {
				gradePoint = 7;
			} else if (grade < 90) {
				gradePoint = 8;
			} else if (grade <= 100) {
				gradePoint = 9;
			}
			if (grade >= 0) { // Only for applicable grades (A grade with an error = -1)
				gpa += gradePoint * this.courseTaken.get(i).getCredit(); // Consider weighting based on credits
				numCredits += this.courseTaken.get(i).getCredit(); // Keep count of credits
			}

		}
		gpa = gpa / numCredits; // Determine GPA (averaging)
		return Math.round(gpa * 10.0) / 10.0; // Round to one decimal place and return
	}

	/**
	 * Adds the given course to students list of courses taken
	 * 
	 * @param c - Course to be added for student
	 */
	public void addCourse(Course c) {
		this.courseTaken.add(new Course(c));
	}

	// STUDENT GETTERS
	public String getStudentID() {
		return studentID;
	}
	
	public String getStudentId() {
		return studentID;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Course> getCourseTaken() {
		ArrayList<Course> courses = new ArrayList<Course>();
		for (int i = 0; i < this.courseTaken.size(); i++) {
			courses.add(new Course(this.courseTaken.get(i)));
		}
		return courses;
	}

	public ArrayList<Double> getFinalGrade() {
		ArrayList<Double> finalGrades = new ArrayList<Double>();
		for (int i = 0; i < this.finalGrade.size(); i++) {
			finalGrades.add(this.finalGrade.get(i));
		}
		return finalGrades;
	}

	// STUDENT SETTERS
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCourseTaken(ArrayList<Course> courseTaken) {
		this.courseTaken = new ArrayList<Course>();
		for (int i = 0; i < courseTaken.size(); i++)
			this.courseTaken.add(new Course(courseTaken.get(i)));
	}

	public void setFinalGrade(ArrayList<Double> finalGrade) {
		this.finalGrade = new ArrayList<Double>();
		for (int i = 0; i < finalGrade.size(); i++)
			this.finalGrade.add(finalGrade.get(i));
	}

	/**
	 * Determines if the given object equals this Student
	 * 
	 * @param Object to check
	 * @return True if student ID's match, false otherwise
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Student))
			return false;

		Student other = (Student) o;
		return this.getStudentID().equals(other.getStudentID());
	}

}

class Course { // --------------------------------------------Course Class

	private String code;
	private ArrayList<Assessment> assignment;
	private double credit;

	public Course() {
		code = "";
		assignment = new ArrayList<Assessment>();
		credit = 0;
	}

	public Course(String code, ArrayList<Assessment> assignment, double credit) {
		this.code = code;
		this.credit = credit;
		this.assignment = new ArrayList<Assessment>();
		for (int i = 0; i < assignment.size(); i++) {
			this.assignment.add(Assessment.getInstance(assignment.get(i).getType(), assignment.get(i).getWeight()));
		}
	}

	public Course(Course other) {
		//ArrayList<Assessment> assign = new ArrayList<Assessment>();
		//this(other.getCode(), other.getAssignment(), other.getCredit());
		this.code = other.code;
		this.assignment = new ArrayList<Assessment>();
		for (int i = 0; i < other.assignment.size(); i++)
			this.assignment.add(other.assignment.get(i));
		this.credit = other.credit;
	}

	// COURSE GETTERS
	public String getCode() {
		return this.code;
	}

	public ArrayList<Assessment> getAssignment() {
		ArrayList<Assessment> assign = new ArrayList<Assessment>();
		for (int i = 0; i < this.assignment.size(); i++) {
			assign.add(Assessment.getInstance(this.assignment.get(i).getType(), this.assignment.get(i).getWeight()));
		}
		return assign;
	}

	public double getCredit() {
		return this.credit;
	}

	// COURSE SETTERS
	public void setCode(String code) {
		this.code = code;
	}

	public void setAssignment(ArrayList<Assessment> assignment) {
		this.assignment = new ArrayList<Assessment>();
		for (int i = 0; i < assignment.size(); i++)
			this.assignment.add(Assessment.getInstance(assignment.get(i).getType(), assignment.get(i).getWeight()));
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	/**
	 * Determines if the given object equals this Course
	 * 
	 * @param Object to check
	 * @return True if course course codes match, false otherwise
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Course))
			return false;

		Course other = (Course) o;
		return this.getCode().equals(other.getCode());
	}

}

class Assessment { // --------------------------------------------Assessment Class

	private char type;
	private int weight;

	private Assessment() {
		type = ' ';
		weight = 0;
	}

	private Assessment(char type, int weight) {
		this.type = type;
		this.weight = weight;
	}

	private Assessment(Assessment a) {
		this.type = a.getType();
		this.weight = a.getWeight();
	}

	// Assessment Getters
	public char getType() {
		return this.type;
	}

	public int getWeight() {
		return this.weight;
	}

	// Assessment Setters
	public void setType(char type) {
		this.type = type;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public static Assessment getInstance(char type, int weight) {
		return new Assessment(type, weight);
	}

	/**
	 * Determines if given parameter is equal to this Assessment
	 * 
	 * @param o: Object to be checked
	 * @return true if object is equal to this assessment, false otherwise.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Assessment))
			return false;

		Assessment other = (Assessment) o;

		if (this.getType() == other.getType()) {
			return this.getWeight() == other.getWeight();
		}
		return false;
	}

}
