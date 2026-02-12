package ui.theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class AppTheme {
    public static final Color PRIMARY = Color.decode("#2E5AAC");
    public static final Color SECONDARY = Color.decode("#2BAE66");
    public static final Color BACKGROUND = Color.decode("#F5F7FA");
    public static final Color SURFACE = Color.decode("#FFFFFF");
    public static final Color TEXT = Color.decode("#1F2937");
    public static final Color MUTED = Color.decode("#6B7280");
    public static final Color BORDER = Color.decode("#D1D5DB");
    public static final Color WARNING = Color.decode("#F59E0B");
    public static final Color DANGER = Color.decode("#EF4444");
    public static final Color SUCCESS = Color.decode("#22C55E");

    public static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font H1_FONT = BASE_FONT.deriveFont(Font.BOLD, 20f);
    public static final Font H2_FONT = BASE_FONT.deriveFont(Font.BOLD, 16f);
    public static final Font SMALL_FONT = BASE_FONT.deriveFont(Font.PLAIN, 12f);
    public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 13);

    private AppTheme() {
    }

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("TabbedPane.font", BASE_FONT);
        UIManager.put("Label.font", BASE_FONT);
        UIManager.put("Button.font", BASE_FONT);
        UIManager.put("TextField.font", BASE_FONT);
        UIManager.put("TextArea.font", BASE_FONT);
        UIManager.put("ComboBox.font", BASE_FONT);
        UIManager.put("Table.font", BASE_FONT);
        UIManager.put("TableHeader.font", H2_FONT.deriveFont(13f));
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.background", BACKGROUND);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10, 10, 10, 10));
        UIManager.put("OptionPane.messageFont", BASE_FONT);
        UIManager.put("OptionPane.buttonFont", BASE_FONT);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY.darker()),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(SECONDARY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY.darker()),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    public static void styleNeutralButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(SURFACE);
        button.setForeground(TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    public static void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setBackground(SURFACE);
        field.setForeground(TEXT);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    public static Border panelPadding() {
        return BorderFactory.createEmptyBorder(12, 12, 12, 12);
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setFillsViewportHeight(true);
        table.setBackground(SURFACE);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static DefaultTableCellRenderer rightAlignedCellRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        return renderer;
    }

    public static DefaultTableCellRenderer occupancyStatusRenderer(int statusColumn) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(SURFACE);
                    int modelRow = table.convertRowIndexToModel(row);
                    Object status = table.getModel().getValueAt(modelRow, statusColumn);
                    if (status != null) {
                        String text = status.toString();
                        if ("AVAILABLE".equalsIgnoreCase(text)) {
                            component.setBackground(new Color(34, 197, 94, 35));
                        } else if ("OCCUPIED".equalsIgnoreCase(text)) {
                            component.setBackground(new Color(245, 158, 11, 35));
                        }
                    }
                }
                return component;
            }
        };
    }

    public static void setPreferredHeight(JComponent component, int height) {
        Dimension preferred = component.getPreferredSize();
        component.setPreferredSize(new Dimension(preferred.width, height));
    }
}