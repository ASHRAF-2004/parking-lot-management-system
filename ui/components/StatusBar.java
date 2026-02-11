package ui.components;

import ui.theme.AppTheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusBar extends JPanel {
    private final JLabel statusLabel = new JLabel("Ready.");
    private final JLabel timeLabel = new JLabel();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatusBar() {
        setLayout(new BorderLayout(8, 8));
        setBackground(AppTheme.SURFACE);
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
                javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        statusLabel.setFont(AppTheme.SMALL_FONT);
        statusLabel.setForeground(AppTheme.TEXT);

        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabel.setFont(AppTheme.SMALL_FONT);
        timeLabel.setForeground(AppTheme.MUTED);
        updateClock();

        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        add(statusLabel, BorderLayout.WEST);
        add(timeLabel, BorderLayout.EAST);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    private void updateClock() {
        timeLabel.setText(LocalDateTime.now().format(formatter));
    }
}