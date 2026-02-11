package ui;

import repository.Database;
import ui.components.AppHeader;
import ui.components.StatusBar;
import ui.theme.AppTheme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    public MainFrame(Database database) {
		initLookAndFeel();
		
        setTitle("Parking Lot Management System");
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
		setLayout(new BorderLayout());

        add(new AppHeader("Parking Lot Management System", "University Parking Lot Office"), BorderLayout.NORTH);

        StatusBar statusBar = new StatusBar();
        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);
        tabs.setFont(AppTheme.BASE_FONT);

        tabs.addTab("Admin", new AdminPanel(database, statusBar));
        tabs.addTab("Entry/Exit", new EntryExitPanel(database, statusBar));
        tabs.addTab("Reports", new ReportsPanel(database, statusBar));

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(AppTheme.panelPadding());
        content.add(tabs, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

        private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    AppTheme.applyGlobalDefaults();
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fallback is acceptable
        }
        AppTheme.applyGlobalDefaults();
    }
}