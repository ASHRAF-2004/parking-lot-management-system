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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class EntryExitPanel extends JPanel {
    private final ParkingLotService parkingLotService;
    private final EntryService entryService;
    private final ExitService exitService;

    private final JTextField entryPlateField = new JTextField();
    private final JComboBox<VehicleType> vehicleTypeCombo = new JComboBox<>(VehicleType.values());
    private final JCheckBox handicappedCardCheck = new JCheckBox("Has handicapped card");
    private final JCheckBox vipCheck = new JCheckBox("Has VIP reservation?");
    private final DefaultTableModel availableSpotsModel = new DefaultTableModel(new Object[]{"Spot", "Type", "Rate"}, 0);
    private final JTable availableSpotsTable = new JTable(availableSpotsModel);
    private final JTextArea ticketArea = new JTextArea(5, 40);

    private final JTextField exitPlateField = new JTextField();
    private final JTextArea billArea = new JTextArea(8, 40);
    private final JRadioButton cashRadio = new JRadioButton("Cash", true);
    private final JRadioButton cardRadio = new JRadioButton("Card");
    private final JTextField cashField = new JTextField();
    private final JTextField cardField = new JTextField();
    private final JTextArea receiptArea = new JTextArea(8, 40);

    public EntryExitPanel(Database database) {
        this.parkingLotService = new ParkingLotService(database);
        this.entryService = new EntryService(database);
        this.exitService = new ExitService(database);

        setLayout(new GridLayout(2, 1, 8, 8));
        add(buildEntryPanel());
        add(buildExitPanel());

        ticketArea.setEditable(false);
        billArea.setEditable(false);
        receiptArea.setEditable(false);
        togglePaymentFields();
    }

    private JPanel buildEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel form = new JPanel(new GridLayout(3, 4, 6, 6));
        JButton searchBtn = new JButton("Search Spots");
        JButton parkBtn = new JButton("Park");

        form.add(new JLabel("Plate"));
        form.add(entryPlateField);
        form.add(new JLabel("Vehicle Type"));
        form.add(vehicleTypeCombo);
        form.add(handicappedCardCheck);
        form.add(vipCheck);
        form.add(searchBtn);
        form.add(parkBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(availableSpotsTable), BorderLayout.CENTER);
        panel.add(new JScrollPane(ticketArea), BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchSpots());
        parkBtn.addActionListener(e -> parkVehicle());
        return panel;
    }

    private JPanel buildExitPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel form = new JPanel(new GridLayout(4, 4, 6, 6));

        JButton billBtn = new JButton("Calculate Bill");
        JButton payBtn = new JButton("Pay");

        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(cardRadio);

        cashRadio.addActionListener(e -> togglePaymentFields());
        cardRadio.addActionListener(e -> togglePaymentFields());

        form.add(new JLabel("Plate"));
        form.add(exitPlateField);
        form.add(billBtn);
        form.add(new JLabel(""));
        form.add(cashRadio);
        form.add(cashField);
        form.add(cardRadio);
        form.add(cardField);
        form.add(new JLabel(""));
        form.add(payBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(billArea), BorderLayout.CENTER);
        panel.add(new JScrollPane(receiptArea), BorderLayout.SOUTH);

        billBtn.addActionListener(e -> calculateBill());
        payBtn.addActionListener(e -> processPayment());
        return panel;
    }

    private void searchSpots() {
        try {
            VehicleType type = (VehicleType) vehicleTypeCombo.getSelectedItem();
            List<ParkingSpotRecord> spots = parkingLotService.findAvailableSpotsFor(type);
            availableSpotsModel.setRowCount(0);
            for (ParkingSpotRecord spot : spots) {
                availableSpotsModel.addRow(new Object[]{spot.getSpotId(), spot.getType(), spot.getRate()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void parkVehicle() {
        int selected = availableSpotsTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select a spot from table first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String spotId = (String) availableSpotsModel.getValueAt(selected, 0);
        try {
            Ticket ticket = entryService.registerEntry(
                    entryPlateField.getText(),
                    (VehicleType) vehicleTypeCombo.getSelectedItem(),
                    handicappedCardCheck.isSelected(),
                    vipCheck.isSelected(),
                    spotId
            );
            ticketArea.setText("Ticket Generated\n"
                    + "Ticket ID: " + ticket.getTicketId() + "\n"
                    + "Plate: " + ticket.getPlate() + "\n"
                    + "Spot: " + ticket.getSpotId() + "\n"
                    + "Entry Time: " + ticket.getEntryTime());
            searchSpots();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateBill() {
        try {
            ExitBill bill = exitService.buildBill(exitPlateField.getText());
            billArea.setText("Exit Bill\n"
                    + "Plate: " + bill.getPlate() + "\n"
                    + "Spot: " + bill.getSpotId() + "\n"
                    + "Hours: " + bill.getHours() + "\n"
                    + "Hourly Rate: RM " + bill.getHourlyRate() + "\n"
                    + "Parking Fee: RM " + bill.getParkingFee() + "\n"
                    + "Unpaid Fines Before: RM " + bill.getUnpaidFinesBefore() + "\n"
                    + "New Fines This Exit: RM " + bill.getNewFineThisExit() + "\n"
                    + "Total Due: RM " + bill.getTotalDue());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPayment() {
        try {
            PaymentMethod method = cashRadio.isSelected() ? PaymentMethod.CASH : PaymentMethod.CARD;
            Double cash = null;
            if (method == PaymentMethod.CASH) {
                cash = Double.parseDouble(cashField.getText().trim());
            }
            String cardNumber = method == PaymentMethod.CARD ? cardField.getText().trim() : null;
            Payment payment = exitService.processPayment(exitPlateField.getText(), method, cash, cardNumber);
            receiptArea.setText("Payment Success\n"
                    + "Method: " + payment.getMethod() + "\n"
                    + "Amount: RM " + payment.getAmountDue() + "\n"
                    + "Cash Given: " + payment.getCashGiven() + "\n"
                    + "Change: RM " + payment.getChange() + "\n"
                    + "Card: " + payment.getMaskedCardNo());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cash amount must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void togglePaymentFields() {
        boolean isCash = cashRadio.isSelected();
        cashField.setEnabled(isCash);
        cardField.setEnabled(!isCash);
    }
}