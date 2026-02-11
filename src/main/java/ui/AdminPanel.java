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
import ui.components.FormBuilder;
import ui.components.StatusBar;
import ui.theme.AppTheme;
import util.FormatUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class AdminPanel extends JPanel {
    private final ParkingLotService parkingLotService;
    private final ReportService reportService;
    private final FineService fineService;
	private final StatusBar statusBar;

    private final DefaultTableModel spotModel;
    private final DefaultTableModel currentVehiclesModel;
    private final DefaultTableModel unpaidFinesModel;
    private final JLabel occupancyLabel = createMetricValueLabel();
    private final JLabel revenueLabel = createMetricValueLabel();
    private final JComboBox<FineSchemeType> schemeCombo = new JComboBox<>(FineSchemeType.values());
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"All", "Available", "Occupied"});
    private final JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{"All types", "Compact", "Regular", "Handicapped", "Reserved"});
    private final TableRowSorter<DefaultTableModel> spotsSorter;

    public AdminPanel(Database database, StatusBar statusBar) {
        this.parkingLotService = new ParkingLotService(database);
        this.reportService = new ReportService(database);
        this.fineService = new FineService(database);
		this.statusBar = statusBar;

        setLayout(new BorderLayout(10, 10));
        setBorder(AppTheme.panelPadding());

        spotModel = createReadOnlyModel(new Object[]{"Spot", "Floor", "Row", "No", "Type", "Rate", "Status", "Current Plate"});
        currentVehiclesModel = createReadOnlyModel(new Object[]{"Plate", "Vehicle Type", "Spot", "Entry Time", "Ticket"});
        unpaidFinesModel = createReadOnlyModel(new Object[]{"Plate", "Reason", "Amount", "Created At"});

        JTable spotsTable = new JTable(spotModel);
        JTable currentTable = new JTable(currentVehiclesModel);
        JTable finesTable = new JTable(unpaidFinesModel);
		AppTheme.styleTable(spotsTable);
        AppTheme.styleTable(currentTable);
        AppTheme.styleTable(finesTable);

        spotsSorter = new TableRowSorter<>(spotModel);
        spotsTable.setRowSorter(spotsSorter);
        currentTable.setAutoCreateRowSorter(true);
        finesTable.setAutoCreateRowSorter(true);

        spotsTable.getColumnModel().getColumn(5).setCellRenderer(AppTheme.rightAlignedCellRenderer());
        spotsTable.getColumnModel().getColumn(6).setCellRenderer(AppTheme.occupancyStatusRenderer(6));

        add(buildMetricsTop(), BorderLayout.NORTH);

        JPanel spotPanel = new JPanel(new BorderLayout(8, 8));
        spotPanel.setBorder(BorderFactory.createTitledBorder("Parking Spots"));
        spotPanel.add(buildFilterPanel(), BorderLayout.NORTH);
        spotPanel.add(new JScrollPane(spotsTable), BorderLayout.CENTER);

        JPanel vehiclesPanel = new JPanel(new BorderLayout());
        vehiclesPanel.setBorder(BorderFactory.createTitledBorder("Current Vehicles"));
        vehiclesPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);

        JPanel finesPanel = new JPanel(new BorderLayout());
        finesPanel.setBorder(BorderFactory.createTitledBorder("Outstanding Fines"));
        finesPanel.add(new JScrollPane(finesTable), BorderLayout.CENTER);
		
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vehiclesPanel, finesPanel);
        bottomSplit.setResizeWeight(0.5);

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spotPanel, bottomSplit);
        verticalSplit.setResizeWeight(0.62);
        add(verticalSplit, BorderLayout.CENTER);

        refreshData();
    }

    private DefaultTableModel createReadOnlyModel(Object[] cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel buildMetricsTop() {
        JButton saveSchemeBtn = new JButton("Save");
        JButton refreshBtn = new JButton("Refresh");
        AppTheme.styleSecondaryButton(saveSchemeBtn);
        AppTheme.stylePrimaryButton(refreshBtn);

        saveSchemeBtn.addActionListener(e -> saveScheme());
        refreshBtn.addActionListener(e -> refreshData());

        JPanel cardsRow = new JPanel(new BorderLayout(8, 8));
        cardsRow.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 8, 8));
        cards.setOpaque(false);
        cards.add(createMetricCard("Occupancy", occupancyLabel));
        cards.add(createMetricCard("Revenue", revenueLabel));

        FormBuilder fineForm = new FormBuilder();
        fineForm.addRow("Fine Scheme", schemeCombo);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(saveSchemeBtn);
        JPanel fineCard = createMetricCard("Fine Scheme", fineForm.getPanel());
        fineCard.add(actions, BorderLayout.SOUTH);
        cards.add(fineCard);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(refreshBtn);

        cardsRow.add(cards, BorderLayout.CENTER);
        cardsRow.add(right, BorderLayout.EAST);
        return cardsRow;
    }

    private JPanel buildFilterPanel() {
        AppTheme.styleTextField(searchField);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        FormBuilder formBuilder = new FormBuilder();
        formBuilder.addDoubleRow("Search Spot / Plate", searchField, "Status", statusFilterCombo);
        formBuilder.addRow("Type", typeFilterCombo);

        searchField.getDocument().addDocumentListener((SimpleDocumentListener) e -> applyFilter());
        statusFilterCombo.addActionListener(e -> applyFilter());
        typeFilterCombo.addActionListener(e -> applyFilter());

        panel.add(formBuilder.getPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, Component content) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(AppTheme.cardBorder());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.H2_FONT);
        titleLabel.setForeground(AppTheme.TEXT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel("-", SwingConstants.LEFT);
        label.setFont(AppTheme.H2_FONT);
        label.setForeground(AppTheme.PRIMARY);
        return label;
    }

    private void saveScheme() {
        try {
            FineSchemeType selected = (FineSchemeType) schemeCombo.getSelectedItem();
            fineService.setCurrentScheme(selected);
            statusBar.setStatus("Fine scheme updated to " + selected + ".");
            JOptionPane.showMessageDialog(this, "Fine scheme saved: " + selected, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        try {
            schemeCombo.setSelectedItem(fineService.getCurrentScheme());
            reloadSpots();
            reloadCurrentVehicles();
            reloadUnpaidFines();
            reloadMetrics();
			applyFilter();
            statusBar.setStatus("Admin data refreshed.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedStatus = ((String) statusFilterCombo.getSelectedItem()).toLowerCase();
        String selectedType = ((String) typeFilterCombo.getSelectedItem()).toLowerCase();

        spotsSorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String spot = entry.getStringValue(0).toLowerCase();
                String type = entry.getStringValue(4).toLowerCase();
                String status = entry.getStringValue(6).toLowerCase();
                String plate = entry.getStringValue(7).toLowerCase();
                boolean matchesSearch = search.isEmpty() || spot.contains(search) || plate.contains(search);
                boolean matchesStatus = selectedStatus.equals("all") || status.equals(selectedStatus);
                boolean matchesType = selectedType.equals("all types") || type.equals(selectedType);
                return matchesSearch && matchesStatus && matchesType;
            }
        });
    }

	private void reloadSpots() {
        spotModel.setRowCount(0);
        for (ParkingSpotRecord spot : parkingLotService.getAllSpots()) {
            spotModel.addRow(new Object[]{
                    spot.getSpotId(),
					spot.getFloorNo(),
                    spot.getRowNo(),
                    spot.getSpotNo(),
                    spot.getType(),
                    FormatUtil.formatMoney(spot.getRate()),
                    spot.getStatus(),
                    spot.getCurrentPlate() == null ? "" : spot.getCurrentPlate()
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
                    FormatUtil.formatMoney(fine.getAmount()),
                    fine.getCreatedAt()
            });
        }
    }

    private void reloadMetrics() {
        OccupancyRecord occupancy = reportService.getOccupancy();
        occupancyLabel.setText(String.format("%.2f%% (%d/%d)", occupancy.getOccupancyRate(), occupancy.getOccupiedSpots(), occupancy.getTotalSpots()));
        revenueLabel.setText(FormatUtil.formatMoney(reportService.getTotalRevenue()));
    }
}