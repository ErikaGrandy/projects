package TranscriptProject;

public class TranscriptDemo {

	public static void main(String[] args) {
		Transcript ts = new Transcript("input.txt", "TransOut.txt");
		ts.buildStudentArray();
	}

}
