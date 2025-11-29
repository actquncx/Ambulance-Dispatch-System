package view;

import service.AmbulanceService;

import javax.swing.*;
import java.awt.*;

public class MedicalSystemSwingApp extends JFrame {
    private AmbulanceService service;

    // Поля введення
    private JTextField nameField = new JTextField(15);
    private JTextField surnameField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JTextField ageField = new JTextField(5);
    private JTextField addressField = new JTextField(20);
    private JTextField diagnosisField = new JTextField(20);
    private JTextArea logArea = new JTextArea(10, 40);

    public MedicalSystemSwingApp(AmbulanceService service) {
        this.service = service;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Система диспетчеризації (ЛР1 Рефакторинг)");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель форми
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Ім'я:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Прізвище:")); formPanel.add(surnameField);
        formPanel.add(new JLabel("Телефон:")); formPanel.add(phoneField);
        formPanel.add(new JLabel("Вік:")); formPanel.add(ageField);
        formPanel.add(new JLabel("Адреса:")); formPanel.add(addressField);
        formPanel.add(new JLabel("Діагноз:")); formPanel.add(diagnosisField);

        JButton createCallButton = new JButton("Створити виклик");
        formPanel.add(new JLabel(""));
        formPanel.add(createCallButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Обробка події натискання кнопки
        createCallButton.addActionListener(e -> createCall());
    }

    private void createCall() {
        try {
            String name = nameField.getText();
            String surname = surnameField.getText();
            long phone = Long.parseLong(phoneField.getText());
            int age = Integer.parseInt(ageField.getText());
            String address = addressField.getText();
            String diagnosis = diagnosisField.getText();

            // Виклик сервісу замість прямої роботи з БД!
            String result = service.registerNewCall(name, surname, phone, age, address, diagnosis);

            logArea.append(result + "\n");
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Перевірте правильність числових полів (телефон, вік)!");
        } catch (Exception ex) {
            logArea.append("Помилка: " + ex.getMessage() + "\n");
        }
    }

    private void clearFields() {
        nameField.setText("");
        surnameField.setText("");
        phoneField.setText("");
        ageField.setText("");
        diagnosisField.setText("");
    }
}