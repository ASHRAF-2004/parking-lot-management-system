package ui;

import repository.Database;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    public MainFrame(Database database) {
        setTitle("Parking Lot Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Admin", new AdminPanel(database));
        tabs.addTab("Entry/Exit", new EntryExitPanel(database));
        tabs.addTab("Reports", new ReportsPanel(database));

        add(tabs);
    }
}