import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Parking Lot Management System");
            System.out.println("Headless environment detected. Swing UI is not available.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            ParkingLotService service = new ParkingLotService();
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }

    enum VehicleType { MOTORCYCLE, CAR, SUV_TRUCK, HANDICAPPED }
    enum SpotType { MOTORCYCLE, COMPACT, LARGE, HANDICAPPED }

    static class ParkingSpot {
        final String id;
        final SpotType type;
        boolean occupied;

        ParkingSpot(String id, SpotType type) {
            this.id = id;
            this.type = type;
        }
    }

    static class Ticket {
        final String ticketId;
        final String plate;
        final VehicleType vehicleType;
        final String spotId;
        final SpotType spotType;
        final LocalDateTime entryTime;

        Ticket(String ticketId, String plate, VehicleType vehicleType, String spotId, SpotType spotType, LocalDateTime entryTime) {
            this.ticketId = ticketId;
            this.plate = plate;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;
        }
    }

    static class Fine {
        final String ticketId;
        final String reason;
        final BigDecimal amount;

        Fine(String ticketId, String reason, BigDecimal amount) {
            this.ticketId = ticketId;
            this.reason = reason;
            this.amount = amount;
        }
    }

    static class ExitBill {
        final Ticket ticket;
        final LocalDateTime exitTime;
        final long parkedMinutes;
        final BigDecimal parkingFee;
        final java.util.List<Fine> fines;

        ExitBill(Ticket ticket, LocalDateTime exitTime, long parkedMinutes, BigDecimal parkingFee, java.util.List<Fine> fines) {
            this.ticket = ticket;
            this.exitTime = exitTime;
            this.parkedMinutes = parkedMinutes;
            this.parkingFee = parkingFee;
            this.fines = fines;
        }

        BigDecimal total() {
            BigDecimal total = parkingFee;
            for (Fine fine : fines) total = total.add(fine.amount);
            return total;
        }
    }

    static class ParkingLotService {
        private final Map<String, ParkingSpot> spots = new LinkedHashMap<>();
        private final Map<String, Ticket> activeTickets = new LinkedHashMap<>();
        private final java.util.List<ExitBill> bills = new ArrayList<>();
        private int ticketSequence = 1;

        ParkingLotService() {
            for (int i = 1; i <= 4; i++) addSpot("M-" + i, SpotType.MOTORCYCLE);
            for (int i = 1; i <= 6; i++) addSpot("C-" + i, SpotType.COMPACT);
            for (int i = 1; i <= 4; i++) addSpot("L-" + i, SpotType.LARGE);
            for (int i = 1; i <= 2; i++) addSpot("H-" + i, SpotType.HANDICAPPED);
        }

        synchronized boolean addSpot(String id, SpotType type) {
            String normalized = id == null ? "" : id.trim().toUpperCase(Locale.ROOT);
            if (normalized.isEmpty() || spots.containsKey(normalized)) return false;
            spots.put(normalized, new ParkingSpot(normalized, type));
            return true;
        }

        synchronized Ticket entry(String plate, VehicleType vehicleType) {
            String normalizedPlate = normalizePlate(plate);
            if (normalizedPlate.isEmpty()) throw new IllegalArgumentException("Plate is required");
            for (Ticket t : activeTickets.values()) {
                if (t.plate.equals(normalizedPlate)) throw new IllegalArgumentException("Vehicle already inside");
            }

            ParkingSpot spot = findFirstSuitableSpot(vehicleType);
            if (spot == null) throw new IllegalStateException("No available spot for " + vehicleType);

            spot.occupied = true;
            String ticketId = String.format("T%05d", ticketSequence++);
            Ticket ticket = new Ticket(ticketId, normalizedPlate, vehicleType, spot.id, spot.type, LocalDateTime.now());
            activeTickets.put(ticketId, ticket);
            return ticket;
        }

        synchronized ExitBill exit(String ticketId) {
            Ticket ticket = activeTickets.remove(ticketId);
            if (ticket == null) throw new IllegalArgumentException("Ticket not found: " + ticketId);

            ParkingSpot spot = spots.get(ticket.spotId);
            if (spot != null) spot.occupied = false;

            LocalDateTime now = LocalDateTime.now();
            long minutes = Math.max(1, Duration.between(ticket.entryTime, now).toMinutes());
            long hours = (minutes + 59) / 60;
            BigDecimal fee = hourlyRate(ticket.vehicleType).multiply(BigDecimal.valueOf(hours));

            java.util.List<Fine> fines = new ArrayList<>();
            if (hours > 12) {
                long overstayHours = hours - 12;
                BigDecimal fineAmount = BigDecimal.valueOf(overstayHours).multiply(new BigDecimal("5.00"));
                fines.add(new Fine(ticket.ticketId, "Overstay beyond 12 hours", fineAmount));
            }

            ExitBill bill = new ExitBill(ticket, now, minutes, fee, fines);
            bills.add(bill);
            return bill;
        }

        synchronized java.util.List<ParkingSpot> allSpots() { return new ArrayList<>(spots.values()); }
        synchronized java.util.List<Ticket> activeTickets() { return new ArrayList<>(activeTickets.values()); }
        synchronized java.util.List<ExitBill> allBills() { return new ArrayList<>(bills); }

        synchronized Map<SpotType, Integer> occupancyByType() {
            EnumMap<SpotType, Integer> map = new EnumMap<>(SpotType.class);
            for (SpotType type : SpotType.values()) map.put(type, 0);
            for (ParkingSpot spot : spots.values()) {
                if (spot.occupied) map.put(spot.type, map.get(spot.type) + 1);
            }
            return map;
        }

        synchronized BigDecimal totalRevenue() {
            BigDecimal sum = BigDecimal.ZERO;
            for (ExitBill bill : bills) sum = sum.add(bill.total());
            return sum;
        }

        private String normalizePlate(String plate) {
            return plate == null ? "" : plate.trim().toUpperCase(Locale.ROOT);
        }

        private BigDecimal hourlyRate(VehicleType type) {
            return switch (type) {
                case MOTORCYCLE -> new BigDecimal("2.00");
                case CAR -> new BigDecimal("4.00");
                case SUV_TRUCK -> new BigDecimal("5.00");
                case HANDICAPPED -> new BigDecimal("3.00");
            };
        }

        private ParkingSpot findFirstSuitableSpot(VehicleType vt) {
            for (ParkingSpot spot : spots.values()) {
                if (!spot.occupied && isSuitable(vt, spot.type)) return spot;
            }
            return null;
        }

        private boolean isSuitable(VehicleType vt, SpotType st) {
            return switch (vt) {
                case MOTORCYCLE -> st == SpotType.MOTORCYCLE || st == SpotType.COMPACT || st == SpotType.LARGE;
                case CAR -> st == SpotType.COMPACT || st == SpotType.LARGE;
                case SUV_TRUCK -> st == SpotType.LARGE;
                case HANDICAPPED -> st == SpotType.HANDICAPPED || st == SpotType.COMPACT || st == SpotType.LARGE;
            };
        }
    }

    static class MainFrame extends JFrame {
        private final ParkingLotService service;
        private final JLabel status = new JLabel("Ready");
        private final DefaultTableModel spotsModel = new DefaultTableModel(new Object[]{"Spot", "Type", "Status"}, 0);
        private final DefaultTableModel activeModel = new DefaultTableModel(new Object[]{"Ticket", "Plate", "Vehicle", "Spot", "Entry"}, 0);
        private final DefaultTableModel billModel = new DefaultTableModel(new Object[]{"Ticket", "Plate", "Minutes", "Parking Fee", "Fines", "Total"}, 0);
        private final JTextArea reportArea = new JTextArea();

        MainFrame(ParkingLotService service) {
            this.service = service;
            setTitle("Parking Lot Management System");
            setSize(980, 680);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.add("Entry / Exit", buildEntryExitPanel());
            tabs.add("Admin", buildAdminPanel());
            tabs.add("Reports", buildReportsPanel());

            JPanel root = new JPanel(new BorderLayout());
            root.add(tabs, BorderLayout.CENTER);
            status.setBorder(new EmptyBorder(8, 12, 8, 12));
            root.add(status, BorderLayout.SOUTH);
            setContentPane(root);

            refreshAll();
        }

        private JPanel buildEntryExitPanel() {
            JPanel panel = new JPanel(new BorderLayout(12, 12));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel forms = new JPanel(new GridLayout(1, 2, 12, 12));

            JPanel entryPanel = new JPanel(new GridBagLayout());
            entryPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Entry"));
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(4, 4, 4, 4);
            gc.anchor = GridBagConstraints.WEST;

            JTextField plateField = new JTextField(12);
            JComboBox<VehicleType> vehicleType = new JComboBox<>(VehicleType.values());
            JButton entryBtn = new JButton("Create Ticket");

            gc.gridx = 0; gc.gridy = 0; entryPanel.add(new JLabel("Plate"), gc);
            gc.gridx = 1; entryPanel.add(plateField, gc);
            gc.gridx = 0; gc.gridy = 1; entryPanel.add(new JLabel("Vehicle Type"), gc);
            gc.gridx = 1; entryPanel.add(vehicleType, gc);
            gc.gridx = 1; gc.gridy = 2; entryPanel.add(entryBtn, gc);

            JTextArea entryResult = new JTextArea(8, 30);
            entryResult.setEditable(false);
            gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
            entryPanel.add(new JScrollPane(entryResult), gc);

            entryBtn.addActionListener(e -> {
                try {
                    Ticket t = service.entry(plateField.getText(), (VehicleType) vehicleType.getSelectedItem());
                    entryResult.setText("Ticket: " + t.ticketId + "\nPlate: " + t.plate + "\nSpot: " + t.spotId +
                            " (" + t.spotType + ")\nEntry: " + fmt(t.entryTime));
                    plateField.setText("");
                    setStatus("Entry created: " + t.ticketId);
                    refreshAll();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });

            JPanel exitPanel = new JPanel(new GridBagLayout());
            exitPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Exit / Billing"));
            JTextField ticketField = new JTextField(12);
            JButton exitBtn = new JButton("Process Exit");
            JTextArea exitResult = new JTextArea(8, 30);
            exitResult.setEditable(false);

            gc = new GridBagConstraints();
            gc.insets = new Insets(4, 4, 4, 4);
            gc.anchor = GridBagConstraints.WEST;
            gc.gridx = 0; gc.gridy = 0; exitPanel.add(new JLabel("Ticket ID"), gc);
            gc.gridx = 1; exitPanel.add(ticketField, gc);
            gc.gridx = 1; gc.gridy = 1; exitPanel.add(exitBtn, gc);
            gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
            exitPanel.add(new JScrollPane(exitResult), gc);

            exitBtn.addActionListener(e -> {
                try {
                    ExitBill bill = service.exit(ticketField.getText().trim());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ticket: ").append(bill.ticket.ticketId).append("\n")
                            .append("Plate: ").append(bill.ticket.plate).append("\n")
                            .append("Minutes: ").append(bill.parkedMinutes).append("\n")
                            .append("Parking fee: $").append(money(bill.parkingFee)).append("\n");
                    if (bill.fines.isEmpty()) sb.append("Fines: none\n");
                    else {
                        sb.append("Fines:\n");
                        for (Fine f : bill.fines) {
                            sb.append(" - ").append(f.reason).append(" $").append(money(f.amount)).append("\n");
                        }
                    }
                    sb.append("Total: $").append(money(bill.total()));
                    exitResult.setText(sb.toString());
                    ticketField.setText("");
                    setStatus("Exit processed: " + bill.ticket.ticketId);
                    refreshAll();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });

            forms.add(entryPanel);
            forms.add(exitPanel);
            panel.add(forms, BorderLayout.NORTH);
            panel.add(new JScrollPane(tableWithModel(activeModel)), BorderLayout.CENTER);
            return panel;
        }

        private JPanel buildAdminPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel controls = new JPanel();
            controls.setBorder(BorderFactory.createTitledBorder("Parking Spots"));
            JTextField spotId = new JTextField(8);
            JComboBox<SpotType> spotType = new JComboBox<>(SpotType.values());
            JButton add = new JButton("Add Spot");
            controls.add(new JLabel("Spot ID"));
            controls.add(spotId);
            controls.add(new JLabel("Type"));
            controls.add(spotType);
            controls.add(add);

            add.addActionListener(e -> {
                boolean ok = service.addSpot(spotId.getText(), (SpotType) spotType.getSelectedItem());
                if (!ok) showError("Could not add spot (empty or duplicate id)");
                else {
                    setStatus("Spot added");
                    spotId.setText("");
                    refreshAll();
                }
            });

            panel.add(controls, BorderLayout.NORTH);

            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JScrollPane(tableWithModel(spotsModel)),
                    new JScrollPane(tableWithModel(billModel)));
            split.setDividerLocation(250);
            panel.add(split, BorderLayout.CENTER);
            return panel;
        }

        private JPanel buildReportsPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            JButton refresh = new JButton("Refresh Report");
            refresh.addActionListener(e -> refreshReport());
            panel.add(refresh, BorderLayout.NORTH);
            reportArea.setEditable(false);
            panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
            return panel;
        }

        private JTable tableWithModel(DefaultTableModel model) {
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setAutoCreateRowSorter(true);
            return table;
        }

        private void refreshAll() {
            refreshSpots();
            refreshActiveTickets();
            refreshBills();
            refreshReport();
        }

        private void refreshSpots() {
            spotsModel.setRowCount(0);
            for (ParkingSpot s : service.allSpots()) {
                spotsModel.addRow(new Object[]{s.id, s.type, s.occupied ? "OCCUPIED" : "AVAILABLE"});
            }
        }

        private void refreshActiveTickets() {
            activeModel.setRowCount(0);
            for (Ticket t : service.activeTickets()) {
                activeModel.addRow(new Object[]{t.ticketId, t.plate, t.vehicleType, t.spotId, fmt(t.entryTime)});
            }
        }

        private void refreshBills() {
            billModel.setRowCount(0);
            for (ExitBill b : service.allBills()) {
                BigDecimal fines = b.total().subtract(b.parkingFee);
                billModel.addRow(new Object[]{b.ticket.ticketId, b.ticket.plate, b.parkedMinutes,
                        "$" + money(b.parkingFee), "$" + money(fines), "$" + money(b.total())});
            }
        }

        private void refreshReport() {
            int totalSpots = service.allSpots().size();
            int occupied = service.activeTickets().size();
            int available = totalSpots - occupied;

            StringBuilder sb = new StringBuilder();
            sb.append("=== Parking Lot Report ===\n");
            sb.append("Generated: ").append(fmt(LocalDateTime.now())).append("\n\n");
            sb.append("Total spots: ").append(totalSpots).append("\n");
            sb.append("Occupied: ").append(occupied).append("\n");
            sb.append("Available: ").append(available).append("\n\n");

            sb.append("Occupancy by Spot Type:\n");
            Map<SpotType, Integer> byType = service.occupancyByType();
            for (SpotType t : SpotType.values()) {
                sb.append(" - ").append(t).append(": ").append(byType.getOrDefault(t, 0)).append(" occupied\n");
            }

            BigDecimal totalRevenue = service.totalRevenue();
            int completedSessions = service.allBills().size();
            long finesCount = service.allBills().stream().mapToLong(b -> b.fines.size()).sum();

            sb.append("\nCompleted sessions: ").append(completedSessions).append("\n");
            sb.append("Total fines issued: ").append(finesCount).append("\n");
            sb.append("Total revenue: $").append(money(totalRevenue)).append("\n");

            reportArea.setText(sb.toString());
        }

        private void showError(String message) {
            setStatus("Error: " + message);
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        private void setStatus(String text) {
            status.setText(text);
        }

        private String fmt(LocalDateTime dt) {
            return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        private String money(BigDecimal value) {
            return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
    }
}
