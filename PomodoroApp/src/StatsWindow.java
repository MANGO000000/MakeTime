import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;

public class StatsWindow extends JFrame {

    private JButton dayBtn;
    private JButton weekBtn;
    private JButton monthBtn;

    private JPanel chartPanel;

    // CSV 자동 탐색 경로
    private final String[] possiblePaths = {
            "../records/study_log.csv",
            "records/study_log.csv",
            "./records/study_log.csv",
            "../study_log.csv",
            "study_log.csv"
    };

    // CSV 전체 기록 저장
    private ArrayList<Record> records = new ArrayList<>();

    public StatsWindow() {
        setTitle("공부 통계");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout());

        dayBtn = new JButton("일별");
        weekBtn = new JButton("주별");
        monthBtn = new JButton("월별");

        topPanel.add(dayBtn);
        topPanel.add(weekBtn);
        topPanel.add(monthBtn);

        add(topPanel, BorderLayout.NORTH);

        chartPanel = new JPanel(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        // CSV 로딩
        loadCSV();

        // 버튼 동작
        dayBtn.addActionListener(e -> showDayStats());
        weekBtn.addActionListener(e -> showWeekStats());
        monthBtn.addActionListener(e -> showMonthStats());

        showDayStats();  // 첫 화면
    }

    // CSV 파일 로드 (날짜, 작업명, 시간 순서)
    private void loadCSV() {
        String found = null;

        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                found = path;
                break;
            }
        }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "CSV 파일을 찾을 수 없습니다.");
            return;
        }

        System.out.println("CSV 로딩: " + found);

        try (BufferedReader br = new BufferedReader(new FileReader(found))) {
            String line;
            records.clear();

            while ((line = br.readLine()) != null) {
                if (!line.contains(",")) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                // CSV 순서 = 날짜, 작업명, 분
                String dateStr = parts[0].trim();
                String task = parts[1].trim();
                int minutes = Integer.parseInt(parts[2].trim());

                LocalDate date = LocalDate.parse(dateStr);

                records.add(new Record(task, minutes, date));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 일별 통계
    private void showDayStats() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate today = LocalDate.now();
        boolean hasData = false;

        for (Record r : records) {
            if (r.date.equals(today)) {
                dataset.addValue(r.minutes, r.task, today.toString());
                hasData = true;
            }
        }

        if (!hasData) {
            showNoDataMessage("오늘 데이터가 없습니다.");
            return;
        }

        updateChart("일별 통계", dataset);
    }

    // 주별 통계
    private void showWeekStats() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        boolean hasData = false;

        for (Record r : records) {
            if (!r.date.isBefore(monday) && !r.date.isAfter(monday.plusDays(6))) {
                dataset.addValue(r.minutes, r.task, r.date.toString());
                hasData = true;
            }
        }

        if (!hasData) {
            showNoDataMessage("이번 주 데이터가 없습니다.");
            return;
        }

        updateChart("주별 통계", dataset);
    }

    // 월별 통계
    private void showMonthStats() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);

        boolean hasData = false;

        for (Record r : records) {
            if (YearMonth.from(r.date).equals(thisMonth)) {
                dataset.addValue(r.minutes, r.task, r.date.toString());
                hasData = true;
            }
        }

        if (!hasData) {
            showNoDataMessage("이번 달 데이터가 없습니다.");
            return;
        }

        updateChart("월별 통계", dataset);
    }

    // 데이터 없을 때 화면에 메시지 출력
    private void showNoDataMessage(String msg) {
        chartPanel.removeAll();

        JLabel label = new JLabel(msg, SwingConstants.CENTER);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        chartPanel.add(label, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // 차트 갱신
    private void updateChart(String title, DefaultCategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                "날짜",
                "분",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel cp = new ChartPanel(barChart);

        chartPanel.removeAll();
        chartPanel.add(cp, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // 기록 클래스
    private static class Record {
        String task;
        int minutes;
        LocalDate date;

        Record(String t, int m, LocalDate d) {
            task = t;
            minutes = m;
            date = d;
        }
    }
}
