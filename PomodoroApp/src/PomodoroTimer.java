import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class PomodoroTimer {

    private MainUI ui;

    private int studyMinutes = 25;
    private int breakMinutes = 5;
    private int longBreakMinutes = 15;

    private int remainingSeconds;
    private boolean isStudy = true;

    private int setCount = 0; // 몇 세트째인지

    private Timer timer;

    public PomodoroTimer(MainUI ui) {
        this.ui = ui;
    }

    public void setTimer(int study, int rest) {
        this.studyMinutes = study;
        this.breakMinutes = rest;
        this.remainingSeconds = study * 60;
        this.isStudy = true;
        this.setCount = 0;

        ui.updateTimer(make(remainingSeconds));
    }

    public void start() {
        if (timer != null) timer.cancel();
        if (remainingSeconds <= 0) return;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                remainingSeconds--;

                SwingUtilities.invokeLater(() ->
                        ui.updateTimer(make(remainingSeconds))
                );

                if (remainingSeconds <= 0) {
                    timer.cancel();

                    if (isStudy) {
                        // 공부 완료 기록
                        DataLogger.save(ui.getTask(), studyMinutes);

                        setCount++; // 1세트 완료
                        System.out.println("세트 완료: " + setCount);

                        // 4세트면 긴 휴식
                        if (setCount == 4) {
                            isStudy = false;
                            remainingSeconds = longBreakMinutes * 60;
                            start();
                            return;
                        }

                        // 나머지 세트는 짧은 휴식
                        isStudy = false;
                        remainingSeconds = breakMinutes * 60;
                        start();
                    } else {
                        // 휴식이 끝나면 공부 시작
                        isStudy = true;
                        remainingSeconds = studyMinutes * 60;
                        start();
                    }
                }
            }
        }, 1000, 1000);
    }

    public void pause() {
        if (timer != null) timer.cancel();
    }

    public void reset() {
        if (timer != null) timer.cancel();
        setCount = 0;
        remainingSeconds = studyMinutes * 60;
        ui.updateTimer(make(remainingSeconds));
    }

    private String make(int s) {
        int m = s / 60;
        int sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }
}
