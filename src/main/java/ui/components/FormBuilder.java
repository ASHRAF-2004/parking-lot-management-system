package ui.components;

import ui.theme.AppTheme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FormBuilder {
    private final JPanel panel;
    private int row = 0;

    public FormBuilder() {
        this.panel = new JPanel(new GridBagLayout());
        this.panel.setOpaque(false);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void addRow(String labelText, JComponent component) {
        GridBagConstraints gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(createLabel(labelText), gbc);

        gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        row++;
    }

    public void addDoubleRow(String label1, JComponent comp1, String label2, JComponent comp2) {
        GridBagConstraints gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(createLabel(label1), gbc);

        gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp1, gbc);

        gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 2;
        panel.add(createLabel(label2), gbc);

        gbc = baseConstraints();
        gbc.gridy = row;
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp2, gbc);
        row++;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.BASE_FONT);
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }
}