import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Book {
private String title;
    private String author;
    private String type;
    private boolean isAvailable;
    private Date borrowedDate;

    public Book(String title, String author, String type) {
        this.title = title;
        this.author = author;
        this.type = type;
        this.isAvailable = true;
    }
    public void setBorrowedDate(Date borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    // Getter for borrowed date
    public Date getBorrowedDate() {
        return borrowedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

public class LibraryManagementSystem1 {
    private List<Book> catalog;
    private JFrame frame;
    private DefaultListModel<String> catalogListModel;
    private JList<String> catalogList;
    private JButton addButton;
    private JButton issueButton;
    private JButton loginButton;
    private JButton logoutButton;
    private boolean isAdminLoggedIn;
    private JLabel adminImageLabel;
    private JLabel penalty;
    private JButton searchButton;
    private String adminPassword = "admin123"; // Store the initial password

    public LibraryManagementSystem1() {
        catalog = new ArrayList<>();
        catalogListModel = new DefaultListModel<>();
        catalogList = new JList<>(catalogListModel);

        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("C:\\Users\\Asus\\Downloads\\253371.jpg"); // Replace with your image file path
                g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        mainPanel.setLayout(new BorderLayout());

        addButton = new JButton("Add Book");
        issueButton = new JButton("Issue Book");
        loginButton = new JButton("Login");
        logoutButton = new JButton("Logout");
        JButton penaltyButton = new JButton("Penalty");
        JButton changePasswordButton = new JButton("Change Password");
        searchButton = new JButton("Search Book");
        logoutButton.setEnabled(false); // Initially disable logout

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeAdminPassword();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAdminLoggedIn) {
                    addBookDialog();
                } else {
                    JOptionPane.showMessageDialog(null, "Please log in as admin to perform this action.");
                }
            }
        });
    
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBook();
            }
        });

        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAdminLoggedIn) {
                    issueBookDialog();
                } else {
                    JOptionPane.showMessageDialog(null, "Please log in as admin to perform this action.");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminLogin();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminLogout();
            }
        });
    // ...

penaltyButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = catalogList.getSelectedIndex();

        if (selectedIndex >= 0) {
            Book selectedBook = catalog.get(selectedIndex);
            Date today = new Date(System.currentTimeMillis()); // Current date (for demonstration)

            // Prompt user for the number of days
            String daysOverdueString = JOptionPane.showInputDialog("Enter the number of days overdue:");
            if (daysOverdueString != null && !daysOverdueString.isEmpty()) {
                try {
                    long daysOverdue = Long.parseLong(daysOverdueString);

                    // Prompt user for penalty rate
                    String penaltyRateString = JOptionPane.showInputDialog("Enter penalty rate per day:");
                    if (penaltyRateString != null && !penaltyRateString.isEmpty()) {
                        try {
                            double penaltyRate = Double.parseDouble(penaltyRateString);

                            double penaltyAmount = penaltyRate * daysOverdue;

                            String message = String.format("Penalty for '%s' is $%.2f for %d days overdue.",
                                    selectedBook.getTitle(), penaltyAmount, daysOverdue);

                            JOptionPane.showMessageDialog(null, message);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid penalty rate. Please enter a valid number.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Penalty rate not provided. Please enter a valid rate.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid number of days. Please enter a valid number.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Number of days not provided. Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to check for penalties.");
        }
    }
});

// ...

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(issueButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(penaltyButton);
        buttonPanel.add(searchButton);

        frame.add(new JScrollPane(catalogList), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        adminImageLabel = new JLabel();
        adminImageLabel.setPreferredSize(new Dimension(200, 200)); // Setting default size
        
        JButton addImageButton = new JButton("Add Image");
        addImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon image = new ImageIcon(selectedFile.getPath());
                    adminImageLabel.setIcon(image);
                }
            }
        });

        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.add(adminImageLabel, BorderLayout.CENTER);
        adminPanel.setBorder(BorderFactory.createTitledBorder("Admin Section"));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(addButton);
        controlPanel.add(issueButton);
        controlPanel.add(loginButton);
        controlPanel.add(logoutButton);
        controlPanel.add(addImageButton);
        controlPanel.add(penaltyButton);
        controlPanel.add(searchButton);
        controlPanel.add(changePasswordButton);

        frame.add(adminPanel, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.EAST);

        frame.setSize(600, 400);
        frame.setVisible(true);

        addInitialBooks();
    }

    private void changeAdminPassword() {
        String oldPassword = JOptionPane.showInputDialog("Enter the old password:");
        if (oldPassword != null && oldPassword.equals(adminPassword)) {
            String newPassword = JOptionPane.showInputDialog("Enter the new password:");
            if (newPassword != null && !newPassword.isEmpty()) {
                adminPassword = newPassword; // Update the admin password
                JOptionPane.showMessageDialog(null, "Password changed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid new password.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid old password.");
        }
    }

    private void addBookDialog() {
        String title = JOptionPane.showInputDialog("Enter book title:");
        String author = JOptionPane.showInputDialog("Enter author:");
        String type = JOptionPane.showInputDialog("Enter book type:");

        if (title != null && author != null && type != null) {
            Book newBook = new Book(title, author, type);
            catalog.add(newBook);
            catalogListModel.addElement(newBook.getTitle());
        }
    }

    private void issueBookDialog() {
        int selectedIndex = catalogList.getSelectedIndex();

        if (selectedIndex >= 0) {
            Book selectedBook = catalog.get(selectedIndex);
            if (selectedBook.isAvailable()) {
                selectedBook.setAvailable(false);
                JOptionPane.showMessageDialog(null, "Book issued: " + selectedBook.getTitle());
            } else {
                JOptionPane.showMessageDialog(null, "Book is not available: " + selectedBook.getTitle());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to issue.");
        }
    }

    private void adminLogin() {
        String password = JOptionPane.showInputDialog("Enter admin password:");
        if (password != null && password.equals(adminPassword)) {
            isAdminLoggedIn = true;
            loginButton.setEnabled(false);
            logoutButton.setEnabled(true);
            JOptionPane.showMessageDialog(null, "Admin logged in successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid password.");
        }
    }

    private void adminLogout() {
        isAdminLoggedIn = false;
        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);
        JOptionPane.showMessageDialog(null, "Admin logged out.");
    }
    private void searchBook() {
        String search = JOptionPane.showInputDialog("Enter book title to search:");
        if (search != null) {
            for (Book book : catalog) {
                if (book.getTitle().equalsIgnoreCase(search)) {
                    int index = catalog.indexOf(book);
                    catalogList.setSelectedIndex(index);
                    catalogList.ensureIndexIsVisible(index);
                    return;
                }
            }
        }
    }

    private void addInitialBooks() {
        catalog.add(new Book("1984", "George Orwell", "Dystopian Fiction"));
        catalog.add(new Book("To Kill a Mockingbird", "Harper Lee", "Fiction"));
        catalog.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Classic"));
        catalog.add(new Book("Pride and Prejudice", "Jane Austen", "Classic"));
        catalog.add(new Book("Listen to Your Heart: The London Adventure","Ruskin Bond","Fiction"));
        
        for (Book book : catalog) {
            catalogListModel.addElement(book.getTitle());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystem1();
        });
    }
}