package com.netcracker.swingui.frames;

import com.netcracker.swingui.data.Book;
import com.netcracker.swingui.data.Library;
import com.netcracker.swingui.data.Record;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.GregorianCalendar;

public abstract class AbstractFrame extends JFrame {
    protected Font font = new Font("Verdana", Font.PLAIN, 12);

    public AbstractFrame(final String nameFrame) {
        super(nameFrame);
        setVisible(false);
    }

    public JPanel createField(String sourceLabel, String sourceText, boolean editableFlag) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 0 , 0));
        JTextField field = new JTextField(sourceText);
        JLabel label = new JLabel(sourceLabel);
        field.setEditable(editableFlag);
        field.setFont(font);
        label.setFont(font);
        addItem(panel, label, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        addItem(panel, field, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        return panel;
    }

    public JPanel createComboBox(String sourceLabel, Record.State state, boolean editableFlag) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        JLabel label = new JLabel(sourceLabel);
        Record.State[] items = new Record.State[]{
                Record.State.BAD,
                Record.State.GOOD,
                Record.State.EXCELLENT
        };
        JComboBox<Record.State> comboBox = new JComboBox<>(items);

        label.setFont(font);
        comboBox.setFont(font);
        comboBox.setEditable(editableFlag);
        panel.add(label);
        panel.add(comboBox);
        return panel;
    }

    public JPanel createCheckBox(String sourceLabel, char sourceValue, boolean editableFlag) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 0, 0));
        JCheckBox box1 = new JCheckBox("Женщина");
        JCheckBox box2 = new JCheckBox("Мужчина");
        box1.setFont(font);
        box2.setFont(font);
        ButtonGroup group = new ButtonGroup();
        group.add(box1);
        group.add(box2);
        if (sourceValue == 'F') box1.setSelected(true);
        else box2.setSelected(true);

        JLabel label = new JLabel(sourceLabel);
        label.setFont(font);
        box1.setEnabled(editableFlag);
        box2.setEnabled(editableFlag);
        panel.add(label);
        panel.add(box1);
        panel.add(box2);
        return panel;
    }

    public JPanel createButtons(final String textButton1, final String textButton2) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setFont(font);
        grid.setPreferredSize(new Dimension(700, 30));
        JButton button1 = new JButton(textButton1);
        JButton button2 = new JButton(textButton2);
        button1.addActionListener(e -> button1ActionListener(button1, button2));
        button2.addActionListener(e -> button2ActionListener(button1, button2));

        addItem(grid, button1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(grid, button2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        return grid;
    }

    public JPanel createButtons(final String textButton1, final String textButton2, final String textButton3) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setFont(font);
        grid.setPreferredSize(new Dimension(700, 30));
        JButton button1 = new JButton(textButton1);
        JButton button2 = new JButton(textButton2);
        JButton button3 = new JButton(textButton3);

        button1.addActionListener(e -> button1ActionListener());
        button2.addActionListener(e -> button2ActionListener());
        button3.addActionListener(e -> button3ActionListener());
        addItem(grid, button1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(grid, button2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        addItem(grid, button3, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

        return grid;
    }

    public JPanel createSpinner(final String sourceLabel, Integer sourceValue, Integer minVal, Integer maxVal, boolean editableFlag) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        SpinnerModel model = new SpinnerNumberModel();
        ((SpinnerNumberModel) model).setMinimum(minVal);
        ((SpinnerNumberModel) model).setMaximum(maxVal);
        model.setValue(sourceValue);
        JSpinner spinner = new JSpinner(model);
        JLabel label = new JLabel(sourceLabel);
        spinner.setEnabled(editableFlag);

        spinner.setFont(font);
        label.setFont(font);

        panel.add(label);
        panel.add(spinner);
        return panel;
    }

    public void button1ActionListener() {

    }

    public void button2ActionListener() {

    }

    public void button3ActionListener() {

    }

    public void button1ActionListener(JButton button1, JButton button2) {

    }

    public void button2ActionListener(JButton button1, JButton button2) {

    }

    public void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align, int fill) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = width;
        gc.gridheight = height;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = align;
        gc.fill = fill;
        p.add(c, gc);
    }

    public JPanel createPhoneField(String sourceLabel, String sourceText) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        MaskFormatter mf2 = null;
        try {
            mf2 = new MaskFormatter("+7(###) ###-####");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JFormattedTextField field = new JFormattedTextField(mf2);

        JLabel label = new JLabel(sourceLabel);
        field.setFont(font);
        label.setFont(font);

        addItem(panel, label, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        addItem(panel, field, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
        return panel;
    }

}
