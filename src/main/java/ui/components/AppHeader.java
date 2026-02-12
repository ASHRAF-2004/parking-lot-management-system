package ui.components;

import ui.theme.AppTheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

public class AppHeader extends JPanel {
    public AppHeader(String title, String subtitle) {
        setLayout(new BorderLayout(0, 2));
        setBackground(AppTheme.PRIMARY);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.H1_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(AppTheme.SMALL_FONT);
        subtitleLabel.setForeground(new Color(230, 238, 255));

        add(titleLabel, BorderLayout.NORTH);
        add(subtitleLabel, BorderLayout.SOUTH);
    }
}