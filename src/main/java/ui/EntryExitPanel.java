package ui;

import model.core.ExitBill;
import model.core.Payment;
import model.core.Ticket;
import model.dto.ParkingSpotRecord;
import model.enums.PaymentMethod;
import model.enums.VehicleType;
import repository.Database;
import service.EntryService;
import service.ExitService;
import service.ParkingLotService;
import ui.components.FormBuilder;
import ui.components.StatusBar;
import ui.theme.AppTheme;
import util.FormatUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntryExitPanel extends JPanel {
    private final ParkingLotService parkingLotService;
    private final EntryService entryService;
    private final ExitService exitService;
	private final StatusBar statusBar;

    private final JTextField entryPlateField = new JTextField();
    private final JComboBox<VehicleType> vehicleTypeCombo = new JComboBox<>(VehicleType.values());
    private final JCheckBox handicappedCardCheck = new JCheckBox("Has handicapped card");
    private final JCheckBox vipCheck = new JCheckBox("Has VIP reservation");
    private final DefaultTableModel availableSpotsModel = new DefaultTableModel(new Object[]{"Spot", "Floor", "Row", "SpotNo", "Type", "Rate"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable availableSpotsTable = new JTable(availableSpotsModel);
    private final JTextArea ticketArea = new JTextArea(8, 40);
    private final JTextField entryStepStatus = new JTextField("Step 1: Enter vehicle details");

    private final JTextField exitPlateField = new JTextField();
    private final JTextArea billArea = new JTextArea(9, 40);
    private final JComboBox<PaymentMethod> paymentMethodCombo = new JComboBox<>(PaymentMethod.values());
    private final JTextField paymentField = new JTextField();
    private final JTextArea receiptArea = new JTextArea(10, 40);
    private final JButton payBtn = new JButton("Pay Now");
    private ExitBill currentBill;

    public EntryExitPanel(Database database, StatusBar statusBar) {
        this.parkingLotService = new ParkingLotService(database);
        this.entryService = new EntryService(database);
        this.exitService = new ExitService(database);
		this.statusBar = statusBar;

        setLayout(new BorderLayout(10, 10));
        setBorder(AppTheme.panelPadding());

        AppTheme.styleTable(availableSpotsTable);
        availableSpotsTable.setAutoCreateRowSorter(true);

        configureTextAreas();
        configureInputs();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildEntryPanel(), buildExitPanel());
        splitPane.setResizeWeight(0.52);
        add(splitPane, BorderLayout.CENTER);

        updatePaymentFieldHint();
        payBtn.setEnabled(false);
    }

    private void configureTextAreas() {
        ticketArea.setEditable(false);
		ticketArea.setLineWrap(true);
        ticketArea.setWrapStyleWord(true);
        ticketArea.setFont(AppTheme.MONO_FONT);
		
        billArea.setEditable(false);
		billArea.setLineWrap(true);
        billArea.setWrapStyleWord(true);
        billArea.setFont(AppTheme.MONO_FONT);
		
        receiptArea.setEditable(false);
        receiptArea.setLineWrap(true);
        receiptArea.setWrapStyleWord(true);
        receiptArea.setFont(AppTheme.MONO_FONT);
    }

    private void configureInputs() {
        entryStepStatus.setEditable(false);
        entryStepStatus.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        AppTheme.styleTextField(entryPlateField);
        AppTheme.styleTextField(exitPlateField);
        AppTheme.styleTextField(paymentField);

        paymentField.setToolTipText("For Cash: amount given | For Card: 12-19 digits");
        paymentMethodCombo.addActionListener(e -> updatePaymentFieldHint());

        FocusAdapter plateNormalizer = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                normalizePlate((JTextField) e.getComponent());
            }
        };
        entryPlateField.addFocusListener(plateNormalizer);
        exitPlateField.addFocusListener(plateNormalizer);
        entryPlateField.addActionListener(e -> normalizePlate(entryPlateField));
        exitPlateField.addActionListener(e -> normalizePlate(exitPlateField));
    }

    private JPanel buildEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Vehicle Entry"));

        JButton searchBtn = new JButton("Find Available Spots");
        JButton parkBtn = new JButton("Confirm Parking");
        JButton resetBtn = new JButton("Reset Entry");
        AppTheme.stylePrimaryButton(searchBtn);
        AppTheme.styleSecondaryButton(parkBtn);
        AppTheme.styleNeutralButton(resetBtn);

        FormBuilder form = new FormBuilder();
        form.addDoubleRow("1) Plate", entryPlateField, "Vehicle Type", vehicleTypeCombo);

        JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        flags.setOpaque(false);
        flags.add(handicappedCardCheck);
        flags.add(vipCheck);
        form.addRow("Flags", flags);

        form.addRow("Step status", entryStepStatus);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);
        actionRow.add(searchBtn);
        actionRow.add(parkBtn);
        actionRow.add(resetBtn);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(form.getPanel());
        top.add(Box.createVerticalStrut(6));
        top.add(actionRow);

        JScrollPane tableScroll = new JScrollPane(availableSpotsTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("2) Select a spot"));

        JScrollPane ticketScroll = new JScrollPane(ticketArea);
        ticketScroll.setBorder(BorderFactory.createTitledBorder("Ticket Card"));

        JSplitPane bottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, ticketScroll);
        bottom.setResizeWeight(0.65);

        panel.add(top, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchSpots());
        parkBtn.addActionListener(e -> parkVehicle());
		resetBtn.addActionListener(e -> resetEntryForm());
        return panel;
    }

    private JPanel buildExitPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Vehicle Exit & Payment"));

        JButton billBtn = new JButton("Calculate Bill");
        JButton resetExitBtn = new JButton("Reset Exit");
        JButton copyReceiptBtn = new JButton("Copy Receipt");
        AppTheme.stylePrimaryButton(billBtn);
        AppTheme.styleSecondaryButton(payBtn);
        AppTheme.styleNeutralButton(resetExitBtn);
        AppTheme.styleNeutralButton(copyReceiptBtn);

        FormBuilder form = new FormBuilder();
        form.addRow("1) Plate", exitPlateField);

        JPanel billRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        billRow.setOpaque(false);
        billRow.add(billBtn);
        billRow.add(resetExitBtn);
        form.addRow("2) Bill", billRow);

        form.addRow("3) Payment Method", paymentMethodCombo);
        form.addRow("4) Payment Details", paymentField);

        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        payRow.setOpaque(false);
        payRow.add(payBtn);
        payRow.add(copyReceiptBtn);
        form.addRow("", payRow);

        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));
        JScrollPane billScroll = new JScrollPane(billArea);
        billScroll.setBorder(BorderFactory.createTitledBorder("Calculated Bill"));
        JScrollPane receiptScroll = new JScrollPane(receiptArea);
        receiptScroll.setBorder(BorderFactory.createTitledBorder("Receipt"));
        center.add(billScroll);
        center.add(receiptScroll);

        panel.add(form.getPanel(), BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        billBtn.addActionListener(e -> calculateBill());
        payBtn.addActionListener(e -> processPayment());
		resetExitBtn.addActionListener(e -> resetExitForm());
        copyReceiptBtn.addActionListener(e -> copyReceipt());
        return panel;
    }

    private void searchSpots() {
        normalizePlate(entryPlateField);
        VehicleType type = (VehicleType) vehicleTypeCombo.getSelectedItem();

        executeAsync(
                () -> parkingLotService.findAvailableSpotsFor(type),
                spots -> {
                    availableSpotsModel.setRowCount(0);
                    for (ParkingSpotRecord spot : spots) {
                        availableSpotsModel.addRow(new Object[]{
                                spot.getSpotId(),
                                spot.getFloorNo(),
                                spot.getRowNo(),
                                spot.getSpotNo(),
                                spot.getType(),
                                FormatUtil.formatMoney(spot.getRate())
                        });
                    }
                    entryStepStatus.setText("Step 2 complete: Select a spot and click Confirm Parking.");
                    statusBar.setStatus("Loaded available spots.");
                },
                "Processing..."
        );
    }

    private void parkVehicle() {
        int selected = availableSpotsTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a spot from table first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setStatus("Invalid operation: no spot selected.");
            return;
        }
        
        int modelRow = availableSpotsTable.convertRowIndexToModel(selected);
        String spotId = (String) availableSpotsModel.getValueAt(modelRow, 0);
        normalizePlate(entryPlateField);
		
        try {
            Ticket ticket = entryService.registerEntry(
                    entryPlateField.getText(),
                    (VehicleType) vehicleTypeCombo.getSelectedItem(),
                    handicappedCardCheck.isSelected(),
                    vipCheck.isSelected(),
                    spotId
            );
            ticketArea.setText("Ticket Generated\n"
                    + "Ticket ID   : " + ticket.getTicketId() + "\n"
                    + "Plate       : " + ticket.getPlate() + "\n"
                    + "Vehicle Type: " + vehicleTypeCombo.getSelectedItem() + "\n"
                    + "Spot        : " + ticket.getSpotId() + "\n"
                    + "Entry Time  : " + FormatUtil.formatDateTime(ticket.getEntryTime()));
            availableSpotsTable.clearSelection();
            entryStepStatus.setText("Step 4 complete: Parking confirmed.");
            statusBar.setStatus("Vehicle parked successfully.");
            searchSpots();
			JOptionPane.showMessageDialog(this, "Parking confirmed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            statusBar.setStatus("Parking operation failed.");
        }
    }

    private void calculateBill() {
        normalizePlate(exitPlateField);
        executeAsync(
                () -> exitService.buildBill(exitPlateField.getText()),
                bill -> {
                    currentBill = bill;
                    billArea.setText("Exit Bill\n"
                            + "Plate               : " + bill.getPlate() + "\n"
                            + "Spot                : " + bill.getSpotId() + "\n"
                            + "Entry Time          : " + FormatUtil.formatDateTime(bill.getEntryTime()) + "\n"
                            + "Exit Time           : " + FormatUtil.formatDateTime(bill.getExitTime()) + "\n"
                            + "Duration Hours      : " + bill.getHours() + "\n"
                            + "Rate Breakdown      : " + bill.getHours() + " x " + FormatUtil.formatMoney(bill.getHourlyRate()) + "\n"
                            + "Parking Fee         : " + FormatUtil.formatMoney(bill.getParkingFee()) + "\n"
                            + "Unpaid Fines Before : " + FormatUtil.formatMoney(bill.getUnpaidFinesBefore()) + "\n"
                            + "New Fines This Exit : " + FormatUtil.formatMoney(bill.getNewFineThisExit()) + "\n"
                            + "Total Due           : " + FormatUtil.formatMoney(bill.getTotalDue()));
                    payBtn.setEnabled(true);
                    exitPlateField.setEnabled(false);
                    statusBar.setStatus("Bill calculated for " + bill.getPlate() + ".");
                },
                "Processing..."
        );
    }

    private void processPayment() {
		if (currentBill == null) {
            JOptionPane.showMessageDialog(this, "Calculate bill before payment.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PaymentMethod method = (PaymentMethod) paymentMethodCombo.getSelectedItem();
        Double cash = null;
        String cardNumber = null;
        
        try {
            if (method == PaymentMethod.CASH) {
                String input = paymentField.getText().trim();
                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter cash amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cash = Double.parseDouble(input);
            } else if (method == PaymentMethod.CARD) {
                cardNumber = paymentField.getText().trim();
                if (cardNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter card number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid payment value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Double finalCash = cash;
        String finalCard = cardNumber;
        executeAsync(
                () -> exitService.processPayment(currentBill.getPlate(), method, finalCash, finalCard),
                payment -> {
                    receiptArea.setText(buildReceipt(payment));
                    JOptionPane.showMessageDialog(this, "Payment completed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    statusBar.setStatus("Payment completed.");
                    resetExitForm();
                    searchSpots();
                },
                "Processing..."
        );
    }

    private String buildReceipt(Payment payment) {
        return "Payment Receipt\n"
                + "Plate               : " + currentBill.getPlate() + "\n"
                + "Entry Time          : " + FormatUtil.formatDateTime(currentBill.getEntryTime()) + "\n"
                + "Exit Time           : " + FormatUtil.formatDateTime(currentBill.getExitTime()) + "\n"
                + "Duration Hours      : " + currentBill.getHours() + "\n"
                + "Rate Breakdown      : " + currentBill.getHours() + " x " + FormatUtil.formatMoney(currentBill.getHourlyRate()) + "\n"
                + "Unpaid Fines Before : " + FormatUtil.formatMoney(currentBill.getUnpaidFinesBefore()) + "\n"
                + "New Fines This Exit : " + FormatUtil.formatMoney(currentBill.getNewFineThisExit()) + "\n"
                + "Total Due           : " + FormatUtil.formatMoney(currentBill.getTotalDue()) + "\n"
                + "Payment Method      : " + payment.getMethod() + "\n"
                + (payment.getMethod() == PaymentMethod.CASH
                ? "Cash Given          : " + FormatUtil.formatMoney(payment.getCashGiven()) + "\n"
                + "Change              : " + FormatUtil.formatMoney(payment.getChange())
                : "Card               : " + payment.getMaskedCardNo());
    }

    private void copyReceipt() {
        String text = receiptArea.getText();
        if (text == null || text.isBlank()) {
            JOptionPane.showMessageDialog(this, "No receipt to copy.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        statusBar.setStatus("Receipt copied to clipboard.");
    }

    private void resetEntryForm() {
        entryPlateField.setText("");
        handicappedCardCheck.setSelected(false);
        vipCheck.setSelected(false);
        availableSpotsModel.setRowCount(0);
        availableSpotsTable.clearSelection();
        ticketArea.setText("");
        entryStepStatus.setText("Step 1: Enter vehicle details");
        statusBar.setStatus("Entry form reset.");
    }

    private void resetExitForm() {
        exitPlateField.setEnabled(true);
        exitPlateField.setText("");
        billArea.setText("");
        paymentField.setText("");
        receiptArea.setText("");
        paymentMethodCombo.setSelectedIndex(0);
        payBtn.setEnabled(false);
        currentBill = null;
        updatePaymentFieldHint();
        statusBar.setStatus("Exit form reset.");
    }

    private void updatePaymentFieldHint() {
        PaymentMethod selected = (PaymentMethod) paymentMethodCombo.getSelectedItem();
        if (selected == null) return;
        
        switch (selected) {
            case CASH:
                paymentField.setEnabled(true);
                paymentField.setToolTipText("Enter cash amount (e.g., 20.00)");
                break;
            case CARD:
                paymentField.setEnabled(true);
                paymentField.setToolTipText("Enter 12-19 digit card number");
                break;
        }
    }

    private void normalizePlate(JTextField field) {
        field.setText(field.getText() == null ? "" : field.getText().trim().toUpperCase());
    }

    private <T> void executeAsync(Supplier<T> supplier, Consumer<T> onSuccess, String busyMessage) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusBar.setStatus(busyMessage);

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return supplier.get();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    onSuccess.accept(get());
                } catch (Exception ex) {
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(EntryExitPanel.this, message, "Operation Failed", JOptionPane.ERROR_MESSAGE);
                    statusBar.setStatus("Operation failed.");
                }
            }
        }.execute();
    }
}