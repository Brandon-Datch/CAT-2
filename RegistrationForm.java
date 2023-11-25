import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnSubmit;
    private JButton btnReset;
    private JPasswordField pfConfirmPass;
    private JPanel registerPanel;
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JButton btnClose;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void resetFields() {
        tfUsername.setText("");
        pfPassword.setText("");
        tfEmail.setText("");
        tfAddress.setText("");
        tfPhone.setText("");
        tfName.setText("");
        pfConfirmPass.setText("");
    }

    private void registerUser() {
        String username = tfUsername.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPass.getPassword());
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            addUserToDatabase(name, username, password, email, address, phone);
            if (user != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register new user",
                        "Try again",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private User user;

    private void addUserToDatabase(String name, String username, String password, String email, String address, String phone) {
        String DB_URL = "jdbc:mysql://localhost:3306/registration";
        String USERNAME = "root";
        String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO details(name,username,password,email,address,phone)" +
                    "VALUES(?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, address);
                preparedStatement.setString(6, phone);
                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    user = new User();
                    user.name = name;
                    user.username = username;
                    user.password = password;
                    user.email = email;
                    user.address = address;
                    user.phone = phone;
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistrationForm myForm = new RegistrationForm(null);
            User user = myForm.user;
            if (user != null) {
                System.out.println("Successful registration of: " + user.username);
            } else {
                System.out.println("Registration cancelled");
            }
        });
    }
}
