package ui;

import model.dto.ActiveParkingRecord;
import model.dto.FineRecord;
import model.dto.OccupancyRecord;
import model.dto.ParkingSpotRecord;
import model.enums.FineSchemeType;
import repository.Database;
import service.FineService;
import service.ParkingLotService;
import service.ReportService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class AdminPanel extends JPanel {
    private final ParkingLotService parkingLotService;
    private final ReportService reportService;
    private final FineService fineService;

    private final DefaultTableModel spotModel;
    private final DefaultTableModel currentVehiclesModel;
    private final DefaultTableModel unpaidFinesModel;
    private final JLabel occupancyLabel;
    private final JLabel revenueLabel;
    private final JComboBox<FineSchemeType> schemeCombo;

    public AdminPanel(Database database) {
        this.parkingLotService = new ParkingLotService(database);
        this.reportService = new ReportService(database);
        this.fineService = new FineService(database);

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new GridLayout(2, 3, 8, 8));
        occupancyLabel = new JLabel();
        revenueLabel = new JLabel();
        schemeCombo = new JComboBox<>(FineSchemeType.values());
        JButton saveSchemeBtn = new JButton("Save Fine Scheme");
        JButton refreshBtn = new JButton("Refresh");

        top.add(occupancyLabel);
        top.add(revenueLabel);
        top.add(schemeCombo);
        top.add(saveSchemeBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        spotModel = new DefaultTableModel(new Object[]{"Spot", "Type", "Rate", "Status", "Current Plate"}, 0);
        JTable spotsTable = new JTable(spotModel);

        currentVehiclesModel = new DefaultTableModel(new Object[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Ticket"}, 0);
        JTable currentTable = new JTable(currentVehiclesModel);

        unpaidFinesModel = new DefaultTableModel(new Object[]{"Plate", "Reason", "Amount", "Created At"}, 0);
        JTable finesTable = new JTable(unpaidFinesModel);

        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));
        center.add(new JScrollPane(spotsTable));
        center.add(new JScrollPane(currentTable));
        center.add(new JScrollPane(finesTable));
        add(center, BorderLayout.CENTER);

        saveSchemeBtn.addActionListener(e -> saveScheme());
        refreshBtn.addActionListener(e -> refreshData());

        refreshData();
    }

    private void saveScheme() {
        try {
            FineSchemeType selected = (FineSchemeType) schemeCombo.getSelectedItem();
            fineService.setCurrentScheme(selected);
            JOptionPane.showMessageDialog(this, "Fine scheme saved: " + selected);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        try {
            schemeCombo.setSelectedItem(fineService.getCurrentScheme());
            reloadSpots();
            reloadCurrentVehicles();
            reloadUnpaidFines();
            reloadMetrics();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadSpots() {
        spotModel.setRowCount(0);
        for (ParkingSpotRecord spot : parkingLotService.getAllSpots()) {
            spotModel.addRow(new Object[]{
                    spot.getSpotId(),
                    spot.getType(),
                    spot.getRate(),
                    spot.getStatus(),
                    spot.getCurrentPlate()
            });
        }
    }

    private void reloadCurrentVehicles() {
        currentVehiclesModel.setRowCount(0);
        for (ActiveParkingRecord record : reportService.getCurrentVehicles()) {
            currentVehiclesModel.addRow(new Object[]{
                    record.getPlate(),
                    record.getVehicleType(),
                    record.getSpotId(),
                    record.getEntryTime(),
                    record.getTicketId()
            });
        }
    }

    private void reloadUnpaidFines() {
        unpaidFinesModel.setRowCount(0);
        for (FineRecord fine : reportService.getOutstandingFines()) {
            unpaidFinesModel.addRow(new Object[]{
                    fine.getPlate(),
                    fine.getReason(),
                    fine.getAmount(),
                    fine.getCreatedAt()
            });
        }
    }

    private void reloadMetrics() {
        OccupancyRecord occupancy = reportService.getOccupancy();
        occupancyLabel.setText(String.format("Occupancy: %.2f%% (%d/%d)", occupancy.getOccupancyRate(), occupancy.getOccupiedSpots(), occupancy.getTotalSpots()));
        revenueLabel.setText(String.format("Revenue: RM %.2f", reportService.getTotalRevenue()));
    }
}