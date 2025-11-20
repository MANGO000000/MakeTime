import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class MainUI extends JFrame {

    private PomodoroTimer timer;
    private JComboBox<String> taskBox;
    private JLabel timeLabel;
    private JLabel setLabel;    // 세트 수 표시
    private ArrayList<String> taskList = new ArrayList<>();

    private static final String TASK_FILE = "../records/tasks.txt";

    public MainUI() {

        setTitle("Pomodoro Timer");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        timer = new PomodoroTimer(this);

        loadTasks();  // 파일에서 카테고리 불러오기

        // ===== 상단 작업 선택 + 입력 =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        taskBox = new JComboBox<>(taskList.toArray(new String[0]));
        taskBox.setEditable(true);
        taskBox.setFont(new Font("Arial", Font.PLAIN, 15));

        // Enter 입력 시 새로운 task 저장
        taskBox.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String newTask = taskBox.getEditor().getItem().toString();

                    if (!taskList.contains(newTask)) {
                        taskList.add(newTask);
                        saveTask(newTask);
                        taskBox.addItem(newTask);
                        taskBox.setSelectedItem(newTask);
                    }
                }
            }
        });

        topPanel.add(taskBox);
        add(topPanel, BorderLayout.NORTH);

        // ===== 중앙: 시간 표시 + 세트 표시 =====
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setLayout(new GridLayout(2, 1));

        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        centerPanel.add(timeLabel);

        setLabel = new JLabel("세트: 0 / 4", SwingConstants.CENTER);
        setLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        centerPanel.add(setLabel);

        add(centerPanel, BorderLayout.CENTER);

        // ===== 하단: 버튼 =====
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JPanel presetPanel = new JPanel(new FlowLayout());
        presetPanel.setBackground(new Color(245, 245, 245));

        JButton btn25 = new JButton("25/5");
        JButton btn50 = new JButton("50/10");
        JButton btn90 = new JButton("90/30");

        presetPanel.add(btn25);
        presetPanel.add(btn50);
        presetPanel.add(btn90);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(245, 245, 245));

        JButton startBtn = new JButton("Start");
        JButton pauseBtn = new JButton("Pause");
        JButton resetBtn = new JButton("Reset");
        JButton statsBtn = new JButton("통계");

        controlPanel.add(startBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(statsBtn);

        bottomPanel.add(presetPanel);
        bottomPanel.add(controlPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== 이벤트 =====
        btn25.addActionListener(e -> timer.setTimer(25, 5));
        btn50.addActionListener(e -> timer.setTimer(50, 10));
        btn90.addActionListener(e -> timer.setTimer(90, 30));

        startBtn.addActionListener(e -> timer.start());
        pauseBtn.addActionListener(e -> timer.pause());
        resetBtn.addActionListener(e -> timer.reset());
        statsBtn.addActionListener(e -> new StatsWindow());

        setVisible(true);
    }

    public void updateTimer(String t) {
        timeLabel.setText(t);
    }

    public void updateSet(int n) {
        setLabel.setText("세트: " + n + " / 4");
    }

    public String getTask() {
        return taskBox.getSelectedItem().toString();
    }

    // ==========================
    //   작업 카테고리 저장 기능
    // ==========================

   private void loadTasks() {
    taskList.clear(); // 기본값 없음!

    try {
        File f = new File(TASK_FILE);
        if (!f.exists()) return;

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            String task = line.trim();
            if (!task.isEmpty() && !taskList.contains(task)) {
                taskList.add(task);
            }
        }
        br.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void saveTask(String task) {
        try {
            File dir = new File("../records");
            if (!dir.exists()) dir.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(TASK_FILE, true));
            bw.write(task);
            bw.newLine();
            bw.close();
        } catch (Exception e) {}
    }
}
