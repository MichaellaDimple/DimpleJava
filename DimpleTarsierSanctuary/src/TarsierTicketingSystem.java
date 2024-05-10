import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;


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

    public void addTicketStock(int index, int quantity) {
        if (index < 0 || index >= tickets.length) {
            JOptionPane.showMessageDialog(null, "Invalid ticket type.");
            return;
        }

        TarsierTicket selectedTicket = tickets[index];
        selectedTicket.setQuantityAvailable(selectedTicket.getQuantityAvailable() + quantity);
        JOptionPane.showMessageDialog(null, "Stocks added successfully.");
    }

    public void reduceTicketStock(int index, int quantity) {
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
        JOptionPane.showMessageDialog(null, "Stocks reduced successfully.");
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

    public ModifyPricesStocksPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("Modify Prices and Stocks");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addTicketsButton = new JButton("Add Tickets");
        addTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddTicketsPage(ticketingService).setVisible(true);
            }
        });

        JButton changePricesButton = new JButton("Change Prices");
        changePricesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChangePricesPage(ticketingService).setVisible(true);
            }
        });

        JButton viewInventoryButton = new JButton("View Inventory");
        viewInventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewInventoryPage(ticketingService).setVisible(true);
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panel.add(addTicketsButton);
        panel.add(changePricesButton);
        panel.add(viewInventoryButton);
        panel.add(exitButton);

        add(panel);
    }
}

// Class for the Add Tickets GUI
class AddTicketsPage extends JFrame {
    private TarsierTicketingService ticketingService;

    private JComboBox<String> ticketTypeComboBox;
    private JTextField quantityField;

    public AddTicketsPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("Add Tickets");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ticketTypeLabel = new JLabel("Ticket Type:");
        JLabel quantityLabel = new JLabel("Quantity:");

        ticketTypeComboBox = new JComboBox<>(new String[]{"Senior Citizen", "Adult", "Child Below 10"});
        quantityField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = ticketTypeComboBox.getSelectedIndex();
                int quantity = Integer.parseInt(quantityField.getText());
                ticketingService.addTicketStock(index, quantity);
            }
        });

        panel.add(ticketTypeLabel);
        panel.add(ticketTypeComboBox);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(new JLabel());
        panel.add(addButton);

        add(panel);
    }
}

// Class for the Change Prices GUI
class ChangePricesPage extends JFrame {
    private TarsierTicketingService ticketingService;

    private JComboBox<String> ticketTypeComboBox;
    private JTextField priceField;

    public ChangePricesPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("Change Prices");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ticketTypeLabel = new JLabel("Ticket Type:");
        JLabel priceLabel = new JLabel("New Price:");

        ticketTypeComboBox = new JComboBox<>(new String[]{"Senior Citizen", "Adult", "Child Below 10"});
        priceField = new JTextField();

        JButton changeButton = new JButton("Change");
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = ticketTypeComboBox.getSelectedIndex();
                double price = Double.parseDouble(priceField.getText());
                if (index == 0) {
                    ticketingService.modifyTicketPrices(price, ticketingService.getTickets()[1].getPrice(), ticketingService.getTickets()[2].getPrice());
                } else if (index == 1) {
                    ticketingService.modifyTicketPrices(ticketingService.getTickets()[0].getPrice(), price, ticketingService.getTickets()[2].getPrice());
                } else {
                    ticketingService.modifyTicketPrices(ticketingService.getTickets()[0].getPrice(), ticketingService.getTickets()[1].getPrice(), price);
                }
                JOptionPane.showMessageDialog(null, "Prices changed successfully.");
            }
        });

        panel.add(ticketTypeLabel);
        panel.add(ticketTypeComboBox);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(new JLabel());
        panel.add(changeButton);

        add(panel);
    }
}

// Class for the View Inventory GUI
class ViewInventoryPage extends JFrame {
    private TarsierTicketingService ticketingService;

    public ViewInventoryPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("View Inventory");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel seniorCitizenStockLabel = new JLabel("Senior Citizen Stock: " + ticketingService.getTickets()[0].getQuantityAvailable());
        JLabel adultStockLabel = new JLabel("Adult Stock: " + ticketingService.getTickets()[1].getQuantityAvailable());
        JLabel childStockLabel = new JLabel("Child Below 10 Stock: " + ticketingService.getTickets()[2].getQuantityAvailable());

        panel.add(seniorCitizenStockLabel);
        panel.add(adultStockLabel);
        panel.add(childStockLabel);

        add(panel);
    }
}

// Class for the Purchase Tickets GUI
class PurchaseTicketsPage extends JFrame {
    private TarsierTicketingService ticketingService;

    private JComboBox<String> ticketTypeComboBox;
    private JTextField quantityField;

    public PurchaseTicketsPage(TarsierTicketingService ticketingService) {
        this.ticketingService = ticketingService;

        setTitle("Purchase Tickets");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ticketTypeLabel = new JLabel("Ticket Type:");
        JLabel quantityLabel = new JLabel("Quantity:");

        ticketTypeComboBox = new JComboBox<>(new String[]{"Senior Citizen", "Adult", "Child Below 10"});
        quantityField = new JTextField();

        JButton purchaseButton = new JButton("Purchase");
        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = ticketTypeComboBox.getSelectedIndex();
                int quantity = Integer.parseInt(quantityField.getText());
                ticketingService.buyTicket(index, quantity);
                dispose(); // Close purchase window after successful purchase
            }
        });

        panel.add(ticketTypeLabel);
        panel.add(ticketTypeComboBox);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(new JLabel());
        panel.add(purchaseButton);

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
                ImageIcon backgroundImage = new ImageIcon("Tarsier.jpg"); // Replace "Tarsier.jpg" with your actual image file
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Create a panel for the logo
        ImageIcon logoIcon = new ImageIcon("TarsierIcon.jpg"); // Replace "TarsierIcon.jpg" with your logo image file
        JLabel logoLabel = new JLabel(logoIcon);

        // Button to navigate to the purchase tickets page
        JButton purchaseTicketsButton = new JButton("Purchase Tickets");
        purchaseTicketsButton.setFont(font);
        purchaseTicketsButton.setBackground(buttonColor);
        purchaseTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PurchaseTicketsPage(new TarsierTicketingService()).setVisible(true);
            }
        });

        // Button to navigate to the modify prices and stocks page
        JButton modifyButton = new JButton("Modify Ticket Stock and Price");
        modifyButton.setFont(font);
        modifyButton.setBackground(buttonColor);
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AuthenticationPage().setVisible(true);
            }
        });

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(font);
        exitButton.setBackground(buttonColor);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(purchaseTicketsButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(exitButton);

        // Add components to background panel
        backgroundPanel.add(logoLabel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Set background panel as content pane
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }
}
