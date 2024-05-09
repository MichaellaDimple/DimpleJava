import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// Class to represent a ticket for Tarsier Sanctuary
class TarsierTicket {
    public enum TicketType {
        SeniorCitizen,
        Adult,
        ChildBelow10
    }

    private TicketType ticketType;
    private double price;
    private int quantityAvailable;

    public TarsierTicket(TicketType ticketType, double price, int quantityAvailable) {
        this.ticketType = ticketType;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
}

// Class to manage the ticketing service
class TarsierTicketingService {
    private TarsierTicket[] tickets;

    public TarsierTicketingService() {
        // Initializing tickets with some default values
        tickets = new TarsierTicket[]{
                new TarsierTicket(TarsierTicket.TicketType.SeniorCitizen, 80.0, 50),
                new TarsierTicket(TarsierTicket.TicketType.Adult, 100.0, 100),
                new TarsierTicket(TarsierTicket.TicketType.ChildBelow10, 50.0, 20)
        };
    }

    public TarsierTicket[] getTickets() {
        return tickets;
    }

    public void buyTicket(int index, int quantity) {
        if (index < 0 || index >= tickets.length) {
            JOptionPane.showMessageDialog(null, "Invalid ticket type.");
            return;
        }

        TarsierTicket selectedTicket = tickets[index];
        if (selectedTicket.getQuantityAvailable() < quantity) {
            JOptionPane.showMessageDialog(null, "Not enough tickets available.");
            return;
        }

        selectedTicket.setQuantityAvailable(selectedTicket.getQuantityAvailable() - quantity);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String purchaseDate = dateFormat.format(date);
        ImageIcon icon = new ImageIcon("tarsier.jpg"); // Replace "tarsier.jpg" with your actual image file
        JOptionPane.showMessageDialog(null, "You have successfully purchased " + quantity + " " + formatTicketType(selectedTicket.getTicketType()) + " tickets.\nTotal cost: ₱" + (selectedTicket.getPrice() * quantity) + "\nPurchase Date: " + purchaseDate, "Ticket Purchase", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    // Helper method to format ticket type
    private String formatTicketType(TarsierTicket.TicketType ticketType) {
        String formattedType = ticketType.toString().toLowerCase();
        formattedType = formattedType.substring(0, 1).toUpperCase() + formattedType.substring(1);
        formattedType = formattedType.replaceAll("_", " ");
        return formattedType;
    }

    public void modifyTicketPrices(double seniorCitizenPrice, double adultPrice, double childPrice) {
        tickets[TarsierTicket.TicketType.SeniorCitizen.ordinal()].setPrice(seniorCitizenPrice);
        tickets[TarsierTicket.TicketType.Adult.ordinal()].setPrice(adultPrice);
        tickets[TarsierTicket.TicketType.ChildBelow10.ordinal()].setPrice(childPrice);
    }

    public void modifyTicketStocks(int seniorCitizenStock, int adultStock, int childStock) {
        tickets[TarsierTicket.TicketType.SeniorCitizen.ordinal()].setQuantityAvailable(seniorCitizenStock);
        tickets[TarsierTicket.TicketType.Adult.ordinal()].setQuantityAvailable(adultStock);
        tickets[TarsierTicket.TicketType.ChildBelow10.ordinal()].setQuantityAvailable(childStock);
    }
}

// Class for the Authentication GUI
class AuthenticationPage extends JFrame {
    private String username = "admin";
    private String password = "admin";

    private JTextField usernameField;
    private JPasswordField passwordField;

    public AuthenticationPage() {
        setTitle("Authentication");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                String enteredPassword = new String(passwordField.getPassword());

                if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
                    JOptionPane.showMessageDialog(null, "Login successful.");
                    new ModifyPricesStocksPage(new TarsierTicketingService()).setVisible(true);
                    dispose(); // Close authentication window after successful login
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }
}

// Class for the Modify Prices and Stocks GUI
class ModifyPricesStocksPage extends JFrame {
    private TarsierTicketingService ticketingService;

    private JTextField seniorCitizenPriceField;
    private JTextField adultPriceField;
    private JTextField childPriceField;
    private JTextField seniorCitizenStockField;
    private JTextField adultStockField;
    private JTextField childStockField;

    public ModifyPricesStocksPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("Modify Prices and Stocks");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel seniorCitizenPriceLabel = new JLabel("Senior Citizen Price:");
        JLabel adultPriceLabel = new JLabel("Adult Price:");
        JLabel childPriceLabel = new JLabel("Child Price:");
        JLabel seniorCitizenStockLabel = new JLabel("Senior Citizen Stock:");
        JLabel adultStockLabel = new JLabel("Adult Stock:");
        JLabel childStockLabel = new JLabel("Child Stock:");

        seniorCitizenPriceField = new JTextField();
        adultPriceField = new JTextField();
        childPriceField = new JTextField();
        seniorCitizenStockField = new JTextField();
        adultStockField = new JTextField();
        childStockField = new JTextField();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double seniorCitizenPrice = Double.parseDouble(seniorCitizenPriceField.getText());
                    double adultPrice = Double.parseDouble(adultPriceField.getText());
                    double childPrice = Double.parseDouble(childPriceField.getText());
                    int seniorCitizenStock = Integer.parseInt(seniorCitizenStockField.getText());
                    int adultStock = Integer.parseInt(adultStockField.getText());
                    int childStock = Integer.parseInt(childStockField.getText());

                    ticketingService.modifyTicketPrices(seniorCitizenPrice, adultPrice, childPrice);
                    ticketingService.modifyTicketStocks(seniorCitizenStock, adultStock, childStock);

                    JOptionPane.showMessageDialog(null, "Prices and stocks updated successfully.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers.");
                }
            }
        });

        panel.add(seniorCitizenPriceLabel);
        panel.add(seniorCitizenPriceField);
        panel.add(adultPriceLabel);
        panel.add(adultPriceField);
        panel.add(childPriceLabel);
        panel.add(childPriceField);
        panel.add(seniorCitizenStockLabel);
        panel.add(seniorCitizenStockField);
        panel.add(adultStockLabel);
        panel.add(adultStockField);
        panel.add(childStockLabel);
        panel.add(childStockField);
        panel.add(new JLabel());
        panel.add(saveButton);

        add(panel);
    }
}

// Main class to run the ticketing service with GUI
public class TarsierTicketingSystem {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tarsier Ticketing System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Set font
        Font font;
        try {
            InputStream is = TarsierTicketingSystem.class.getResourceAsStream("DelliaScript.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 16);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("DelliaScript", Font.PLAIN, 16); // Fallback font
        }

        // Set color
        Color buttonColor = new Color(216, 231, 204); // RGB (216, 231, 204)

        // Create a panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("Tarsier.jpg"); // Replace "Tarsier.jpg" with your background image file
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Create a panel for the logo
        ImageIcon logoIcon = new ImageIcon("TarsierIcon.jpg"); // Replace "TarsierIcon.jpg" with your logo image file
        JLabel logoLabel = new JLabel(logoIcon);

        // Button to navigate to the authentication page
        JButton modifyButton = new JButton("Modify Prices and Stocks");
        modifyButton.setFont(font);
        modifyButton.setBackground(buttonColor);
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AuthenticationPage().setVisible(true);
            }
        });

        // Create a panel for the tickets
        JPanel ticketPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        ticketPanel.setOpaque(false); // Make ticket panel transparent
        ticketPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        TarsierTicketingService ticketingService = new TarsierTicketingService();

        for (int i = 0; i < ticketingService.getTickets().length; i++) {
            TarsierTicket ticket = ticketingService.getTickets()[i];
            JButton button = new JButton(formatTicketType(ticket.getTicketType()) + " - Price: ₱" + ticket.getPrice() + " - Quantity Available: " + ticket.getQuantityAvailable());
            button.setFont(font);
            button.setBackground(buttonColor);
            int index = i; // Capture index variable
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input = JOptionPane.showInputDialog(null, "Enter the quantity:");
                    if (input != null) {
                        try {
                            int quantity = Integer.parseInt(input);
                            ticketingService.buyTicket(index, quantity); // Pass index directly
                            button.setText(formatTicketType(ticket.getTicketType()) + " - Price: ₱" + ticket.getPrice() + " - Quantity Available: " + ticket.getQuantityAvailable());
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid input.");
                        }
                    }
                }
            });
            ticketPanel.add(button);
        }

        // Add components to background panel
        backgroundPanel.add(logoLabel, BorderLayout.NORTH);
        backgroundPanel.add(ticketPanel, BorderLayout.CENTER);
        backgroundPanel.add(modifyButton, BorderLayout.SOUTH);

        // Set background panel as content pane
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }

    // Helper method to format ticket type
    private static String formatTicketType(TarsierTicket.TicketType ticketType) {
        String formattedType = ticketType.toString().toLowerCase();
        formattedType = formattedType.substring(0, 1).toUpperCase() + formattedType.substring(1);
        formattedType = formattedType.replaceAll("_", " ");
        return formattedType;
    }
}
