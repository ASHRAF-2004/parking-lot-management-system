package ui;

import model.dto.ActiveParkingRecord;
import model.dto.FineRecord;
import model.dto.OccupancyByTypeRecord;
import model.dto.OccupancyRecord;
import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.SpotType;
import repository.Database;
import service.ParkingLotService;
import service.ReportService;
import ui.components.StatusBar;
import ui.theme.AppTheme;
import util.FormatUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final ReportService reportService;
    private final ParkingLotService parkingLotService;
    private final StatusBar statusBar;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable reportTable = new JTable(tableModel);

    private final JLabel revenueBig = new JLabel("RM 0.00", SwingConstants.LEFT);
    private final JLabel occupancyBig = new JLabel("0.00%", SwingConstants.LEFT);
    private final JLabel occupancyCounts = new JLabel("0 occupied / 0 total", SwingConstants.LEFT);
    private final DefaultTableModel typeBreakdownModel = new DefaultTableModel(new Object[]{"Type", "Total", "Occupied"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public ReportsPanel(Database database, StatusBar statusBar) {
        this.reportService = new ReportService(database);
        this.parkingLotService = new ParkingLotService(database);
        this.statusBar = statusBar;

        setLayout(new BorderLayout(10, 10));
        setBorder(AppTheme.panelPadding());

        add(buildMenu(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        AppTheme.styleTable(reportTable);
    }

    private JPanel buildMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(220, 0));
        menu.setBorder(AppTheme.cardBorder());
        menu.setBackground(AppTheme.SURFACE);

        JButton currentBtn = createMenuButton("Current Vehicles");
        JButton revenueBtn = createMenuButton("Revenue");
        JButton occupancyBtn = createMenuButton("Occupancy");
        JButton fineBtn = createMenuButton("Outstanding Fines");

        currentBtn.addActionListener(e -> showCurrentVehicles());
        revenueBtn.addActionListener(e -> showRevenue());
        occupancyBtn.addActionListener(e -> showOccupancy());
        fineBtn.addActionListener(e -> showFines());
		
		menu.add(currentBtn);
        menu.add(Box.createVerticalStrut(8));
        menu.add(revenueBtn);
        menu.add(Box.createVerticalStrut(8));
        menu.add(occupancyBtn);
        menu.add(Box.createVerticalStrut(8));
        menu.add(fineBtn);
        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private JPanel buildContent() {
        JPanel tableView = new JPanel(new BorderLayout());
        tableView.setBorder(AppTheme.cardBorder());
        tableView.setBackground(AppTheme.SURFACE);
        tableView.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel revenueView = new JPanel(new BorderLayout(8, 8));
        revenueView.setBorder(AppTheme.cardBorder());
        revenueView.setBackground(AppTheme.SURFACE);
        revenueBig.setFont(AppTheme.H1_FONT);
        revenueBig.setForeground(AppTheme.PRIMARY);
        JLabel revenueNote = new JLabel("Includes parking fees and fines (paid).");
        revenueNote.setFont(AppTheme.SMALL_FONT);
        revenueNote.setForeground(AppTheme.MUTED);
        revenueView.add(revenueBig, BorderLayout.NORTH);
        revenueView.add(revenueNote, BorderLayout.CENTER);

        JPanel occupancyView = new JPanel(new BorderLayout(8, 8));
        occupancyView.setBorder(AppTheme.cardBorder());
        occupancyView.setBackground(AppTheme.SURFACE);
        occupancyBig.setFont(AppTheme.H1_FONT);
        occupancyBig.setForeground(AppTheme.SECONDARY);
        occupancyCounts.setFont(AppTheme.BASE_FONT);
        JTable byTypeTable = new JTable(typeBreakdownModel);
        AppTheme.styleTable(byTypeTable);
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(occupancyBig);
        top.add(occupancyCounts);
        occupancyView.add(top, BorderLayout.NORTH);
        occupancyView.add(new JScrollPane(byTypeTable), BorderLayout.CENTER);

        cardPanel.add(tableView, "table");
        cardPanel.add(revenueView, "revenue");
        cardPanel.add(occupancyView, "occupancy");
        return cardPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        AppTheme.styleNeutralButton(button);
        return button;
    }

    private void showCurrentVehicles() {
        try {
            tableModel.setDataVector(new Object[][]{}, new Object[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Ticket"});
            for (ActiveParkingRecord record : reportService.getCurrentVehicles()) {
                tableModel.addRow(new Object[]{record.getPlate(), record.getVehicleType(), record.getSpotId(), record.getEntryTime(), record.getTicketId()});
            }
			cardLayout.show(cardPanel, "table");
            statusBar.setStatus("Loaded current vehicles report.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRevenue() {
        try {
            revenueBig.setText(FormatUtil.formatMoney(reportService.getTotalRevenue()));
            cardLayout.show(cardPanel, "revenue");
            statusBar.setStatus("Loaded revenue report.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOccupancy() {
        try {
            OccupancyRecord occupancy = reportService.getOccupancy();
            occupancyBig.setText(String.format("%.2f%%", occupancy.getOccupancyRate()));
            occupancyCounts.setText(String.format("%d occupied / %d total", occupancy.getOccupiedSpots(), occupancy.getTotalSpots()));

            typeBreakdownModel.setRowCount(0);
            for (OccupancyByTypeRecord record : buildByTypeRecords(parkingLotService.getAllSpots())) {
                typeBreakdownModel.addRow(new Object[]{record.getType(), record.getTotal(), record.getOccupied()});
            }
            cardLayout.show(cardPanel, "occupancy");
            statusBar.setStatus("Loaded occupancy report.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<OccupancyByTypeRecord> buildByTypeRecords(List<ParkingSpotRecord> spots) {
        List<OccupancyByTypeRecord> records = new ArrayList<>();
        for (SpotType type : SpotType.values()) {
            int total = 0;
            int occupied = 0;
            for (ParkingSpotRecord spot : spots) {
                if (spot.getType() == type) {
                    total++;
                    if (spot.getStatus() == SpotStatus.OCCUPIED) {
                        occupied++;
                    }
                }
            }
            records.add(new OccupancyByTypeRecord(type, total, occupied));
        }
		return records;
    }

    private void showFines() {
        try {
            tableModel.setDataVector(new Object[][]{}, new Object[]{"Plate", "Reason", "Scheme", "Amount", "Created At"});
            for (FineRecord fine : reportService.getOutstandingFines()) {
                tableModel.addRow(new Object[]{fine.getPlate(), fine.getReason(), fine.getSchemeAtTime(), FormatUtil.formatMoney(fine.getAmount()), fine.getCreatedAt()});
            }
			cardLayout.show(cardPanel, "table");
            statusBar.setStatus("Loaded outstanding fines report.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}