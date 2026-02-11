package ui;

import model.dto.ActiveParkingRecord;
import model.dto.FineRecord;
import model.dto.OccupancyRecord;
import repository.Database;
import service.ReportService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ReportsPanel extends JPanel {
    private final ReportService reportService;
    private final DefaultTableModel tableModel;
    private final JLabel revenueLabel;
    private final JLabel occupancyLabel;

    public ReportsPanel(Database database) {
        this.reportService = new ReportService(database);
        this.tableModel = new DefaultTableModel();
        this.revenueLabel = new JLabel("Revenue: RM 0.00");
        this.occupancyLabel = new JLabel("Occupancy: 0.00%");

        setLayout(new BorderLayout(8, 8));

        JPanel buttons = new JPanel(new GridLayout(1, 4, 8, 8));
        JButton currentBtn = new JButton("Current Vehicles Report");
        JButton revenueBtn = new JButton("Revenue Report");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton fineBtn = new JButton("Fine Report");
        buttons.add(currentBtn);
        buttons.add(revenueBtn);
        buttons.add(occupancyBtn);
        buttons.add(fineBtn);

        JPanel metrics = new JPanel(new GridLayout(1, 2, 8, 8));
        metrics.add(revenueLabel);
        metrics.add(occupancyLabel);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(metrics, BorderLayout.SOUTH);

        currentBtn.addActionListener(e -> showCurrentVehicles());
        revenueBtn.addActionListener(e -> showRevenue());
        occupancyBtn.addActionListener(e -> showOccupancy());
        fineBtn.addActionListener(e -> showFines());
    }

    private void showCurrentVehicles() {
        try {
            tableModel.setDataVector(new Object[][]{}, new Object[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Ticket"});
            for (ActiveParkingRecord record : reportService.getCurrentVehicles()) {
                tableModel.addRow(new Object[]{record.getPlate(), record.getVehicleType(), record.getSpotId(), record.getEntryTime(), record.getTicketId()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRevenue() {
        try {
            revenueLabel.setText(String.format("Revenue: RM %.2f", reportService.getTotalRevenue()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOccupancy() {
        try {
            OccupancyRecord occupancy = reportService.getOccupancy();
            occupancyLabel.setText(String.format("Occupancy: %.2f%% (%d/%d)", occupancy.getOccupancyRate(), occupancy.getOccupiedSpots(), occupancy.getTotalSpots()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFines() {
        try {
            tableModel.setDataVector(new Object[][]{}, new Object[]{"Plate", "Reason", "Scheme", "Amount", "Created At"});
            for (FineRecord fine : reportService.getOutstandingFines()) {
                tableModel.addRow(new Object[]{fine.getPlate(), fine.getReason(), fine.getSchemeAtTime(), fine.getAmount(), fine.getCreatedAt()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}