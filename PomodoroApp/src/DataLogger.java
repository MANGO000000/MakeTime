import java.io.*;
import java.time.LocalDate;

public class DataLogger {

    private static final String FOLDER = "../records/";
    private static final String FILE = FOLDER + "study_log.csv";

    public static void save(String task, int minutes) {
        try {
            File dir = new File(FOLDER);
            if (!dir.exists()) dir.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true));
            bw.write(LocalDate.now() + "," + task + "," + minutes);
            bw.newLine();
            bw.close();

            System.out.println("저장됨: " + task + " " + minutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
