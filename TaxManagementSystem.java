import java.util.*;
import javax.management.Notification;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
   
class User {
    String username;
    String hashedPassword;
    String role;
   private  List<Notification> reminders;

    public User(String username, String hashedPassword, String role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.reminders = new ArrayList<>(); // lIst to store reminders for the user
    }

   
    public void addReminder(Notification notification) {
        this.reminders.add(notification);
        System.out.println("Reminder set successfully.");
    }
    public void checkReminders() {
        if(reminders.isEmpty()) {
            System.out.println("NO remiders set.");
        } else {
            System.out.println("Reminders:");
            for (Notification reminder : reminders) {
               if (reminder.isReminderDue()) {
                  System.out.println("Reminder due: " + reminder.getMessage());
               } else {
                   System.out.println("Upcoming remider:" + reminder.getMessage() + "at" + reminder.getReminderTime());
               }
            }
        }
    }
}
class Notification {
    private String message;
    private LocalDateTime reminderTime;

    // Constructor to create a notification with a message and reminder time
    public Notification(String message, LocalDateTime reminderTime) {
        this.message = message;
        this.reminderTime = reminderTime;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    // Method to check if the reminder is due
    public boolean isReminderDue() {
        return LocalDateTime.now().isAfter(reminderTime);
    }
}
class AuditLog {
    String username;
    String action;
    Date timestamp;

    public AuditLog(String username, String action) {
        this.username = username;
        this.action = action;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return "User: " + username + ", Action: " + action + ", Timestamp: " + timestamp;
    }
}
class TaxManagementSystem {
    private static HashMap<String, User> users = new HashMap<>();
    private static User currentUser;  // Track the logged-in user
    private static HashMap<String, TaxpayerData> taxpayerData = new HashMap<>();  // Store taxpayer data
    private static List<AuditLog> auditLogs = new ArrayList<>();
    public static void recordAuditLog(String action) {
        if (currentUser != null) {
            auditLogs.add(new AuditLog(currentUser.username, action));
        }
    }

    public static void viewAuditLogs() {
        for (AuditLog log : auditLogs) {
            System.out.println(log);
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Register\n2. Login\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.println("Enter username: ");
                String username = sc.nextLine().trim();
                System.out.println("Enter password: ");
                String password = sc.nextLine().trim();
                System.out.println("Enter role (Admin, Taxpayer, Auditor): ");
                String role = sc.nextLine().trim();
                if (!isValidRole(role)) {
                    System.out.println("Invalid role. Please enter Admin, Taxpayer, or Auditor.");
                    continue;
                }
                register(username, password, role);
            } else if (choice == 2) {
                System.out.println("Enter username: ");
                String username = sc.nextLine().trim();
                System.out.println("Enter password: ");
                String password = sc.nextLine().trim();
                if (login(username, password)) {
                    System.out.println("Login successful!");
                    System.out.println("Logged in as: " + currentUser.role);
                    if (currentUser.role.equals("Admin")) {
                        adminMenu();
                    } else if (currentUser.role.equals("Taxpayer")) {
                        taxpayerMenu();
                    } else if (currentUser.role.equals("Auditor")) {
                        auditorMenu();
                    }
                } else {
                    System.out.println("Invalid credentials.");
                }
            } else if (choice == 3) {
                System.out.println("Exiting the system.");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void register(String username, String password, String role) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
        } else {
            String hashedPassword = SecurityUtils.hashPassword(password);
            users.put(username, new User(username, hashedPassword, role));
            if (role.equals("Taxpayer")) {
                taxpayerData.put(username, new TaxpayerData());  // Initialize income/expense data for the taxpayer
            }
            System.out.println("Registration successful. Role: " + role);
            recordAuditLog("Registered new user: " + username + " with role: " + role);
        }
    }

    public static void setReminder(User user) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter reminder message: ");
        String message = sc.nextLine();
    
        System.out.println("Enter reminder time in format (yyyy-MM-dd HH:mm): ");
        String timeInput = sc.nextLine();
    
        // Parse the reminder time into a LocalDateTime object
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime reminderTime = LocalDateTime.parse(timeInput, formatter);
    
        // Create a new notification with the message and the reminder time
        Notification notification = new Notification(message, reminderTime);
    
        // Add the reminder to the user
        user.addReminder(notification);
    }
    public static boolean login(String username, String password) {
        if (users.containsKey(username)) {
            String hashedInputPassword = SecurityUtils.hashPassword(password);
            User user = users.get(username);
            if (user.hashedPassword.equals(hashedInputPassword)) {
                currentUser = user;
                recordAuditLog("User logged in: " + username);
                return true;
            }
        }
        return false;
    }


    // Helper method to validate roles
    public static boolean isValidRole(String role) {
        return role.equals("Admin") || role.equals("Taxpayer") || role.equals("Auditor");
    }

    // Menu for Admin-specific actions
    public static void adminMenu() {
         Scanner sc = new Scanner(System.in);
    while (true) {
        System.out.println("Admin Menu:");
        System.out.println("1. View All Users\n2. Delete User\n3. Logout");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        if (choice == 1) {
            viewAllUsers();
            recordAuditLog("Admin viewed all users");
        } else if (choice == 2) {
            System.out.println("Enter username to delete: ");
            String username = sc.nextLine().trim();
            deleteUser(username);
            recordAuditLog("Admin deleted user: " + username);
        } else if (choice == 3) {
            currentUser = null;
            System.out.println("Logged out.");
            break;
        } else {
            System.out.println("Invalid choice. Please try again.");
        }
    }
}

public static void viewAllUsers() {
    for (String username : users.keySet()) {
        User user = users.get(username);
        System.out.println("Username: " + user.username + ", Role: " + user.role);
    }
}

public static void deleteUser(String username) {
    if (users.containsKey(username)) {
        users.remove(username);
        taxpayerData.remove(username); // Remove associated taxpayer data
        System.out.println("User deleted.");
    } else {
        System.out.println("User not found.");
    }
}

    // Menu for Taxpayer-specific actions
    public static void taxpayerMenu() {
        Scanner sc = new Scanner(System.in);
        TaxpayerData data = taxpayerData.get(currentUser.username);

        while (true) {
            System.out.println("\n1. Manage Incomes\n2. Manage Expenses\n3. View Tax Summary\n4. Generate Tax Report\n5. Set a Reminder\n6. View Reminder\n7. Logout");
            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            if (choice == 1) {
                data.manageIncomes();
            } else if (choice == 2) {
                data.manageExpenses();
            } else if (choice == 3) {
                data.viewTaxSummary();
            } else if (choice == 4) {
                data.generateTaxReport(currentUser.username);
            } else if (choice ==5) {
                setReminder(currentUser);
            } else if (choice == 6) {
                currentUser.checkReminders();
            } else if (choice == 7) {
                System.out.println("Logging out...");
                currentUser = null;
                break;
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    public static void auditorMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Auditor Menu:");
            System.out.println("1. View All Taxpayer Data\n2. Flag Discrepancy\n3. View Audit Logs\n4. Logout");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (choice == 1) {
                viewAllTaxpayerData();
                recordAuditLog("Viewed all taxpayer data");
            } else if (choice == 2) {
                System.out.println("Enter taxpayer username to flag: ");
                String username = sc.nextLine().trim();
                flagDiscrepancy(username);
                recordAuditLog("Flagged discrepancy for taxpayer: " + username);
            } else if (choice == 3) {
                viewAuditLogs();
                recordAuditLog("Viewed audit logs");
            } else if (choice == 4) {
                currentUser = null;
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void viewAllTaxpayerData() {
        for (String username : taxpayerData.keySet()) {
            TaxpayerData data = taxpayerData.get(username);
            double totalIncome = data.getIncomes().stream().mapToDouble(Double::doubleValue).sum();
            double totalExpenses = data.getExpenses().stream().mapToDouble(Double::doubleValue).sum();
            double tax = TaxCalculator.calculateTax(totalIncome, totalExpenses);
        }
    }

    public static void flagDiscrepancy(String username) {
        if (taxpayerData.containsKey(username)) {
            System.out.println("Discrepancy flagged for taxpayer: " + username);
            // Implement further actions for flagging discrepancies
        } else {
            System.out.println("Taxpayer not found.");
        }
    }        
}
class TaxpayerData {
    private ArrayList<Double> incomes = new ArrayList<>();
    ArrayList<Double> expenses = new ArrayList<>();
    
    public void manageIncomes() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter income amount to add: ");
        double income = sc.nextDouble();
        incomes.add(income);
        System.out.println("Income added successfully!");
    }

    public void manageExpenses() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter expense amount to add: ");
        double expense = sc.nextDouble();
        expenses.add(expense);
        System.out.println("Expense added successfully!");
    }

    public ArrayList<Double> getIncomes() {
        return incomes;
    }

    public ArrayList<Double> getExpenses() {
        return expenses;
    }

    public void viewTaxSummary() {
        double totalIncome = incomes.stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = expenses.stream().mapToDouble(Double::doubleValue).sum();
        
        double taxableIncome = totalIncome - totalExpenses;
        double tax = TaxCalculator.calculateTax(totalIncome, totalExpenses);

        System.out.println("\n--- Tax Summary ---");
        System.out.println("Total Income: $" + totalIncome);
        System.out.println("Total Expenses: $" + totalExpenses);
        System.out.println("Taxable Income: $" + taxableIncome);
        System.out.println("Total Tax Due: $" + tax);
    }

    // New method to generate a tax report and save it to a file
    public void generateTaxReport(String username) {
        double totalIncome = incomes.stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = expenses.stream().mapToDouble(Double::doubleValue).sum();
        double taxableIncome = totalIncome - totalExpenses;
        double tax = TaxCalculator.calculateTax(totalIncome, totalExpenses);

        StringBuilder report = new StringBuilder();
        report.append("\n--- Tax Report for ").append(username).append(" ---\n");
        report.append("Total Income: $").append(totalIncome).append("\n");
        report.append("Total Expenses: $").append(totalExpenses).append("\n");
        report.append("Taxable Income: $").append(taxableIncome).append("\n");
        report.append("Total Tax Due: $").append(tax).append("\n");

        System.out.println(report.toString());

        // Optionally save the report to a file
        System.out.println("Do you want to save this report to a file? (yes/no)");
        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine().trim();
        if (choice.equalsIgnoreCase("yes")) {
            saveReportToFile(username, report.toString());
        }
    }

    private void saveReportToFile(String username, String report) {
        String filename = username + "_TaxReport.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(report);
            System.out.println("Tax report saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}
class TaxCalculator {
    // Define the tax brackets and corresponding rates
    private static final double[] BRACKETS = { 10000, 40000, 80000, 180000 };
    private static final double[] RATES = { 0.1, 0.15, 0.2, 0.25, 0.3 };  // Tax rates for each bracket

    // Method to calculate taxes based on income and deductions
    public static double calculateTax(double income, double expenses) {
        double taxableIncome = income - expenses;
        if (taxableIncome <= 0) {
            return 0;
        }
        double tax = 0;
        int i = 0;

        // Loop through tax brackets
        while (i < BRACKETS.length && taxableIncome > BRACKETS[i]) {
            double bracketIncome = (i == 0) ? BRACKETS[i] : (BRACKETS[i] - BRACKETS[i - 1]);
            tax += bracketIncome * RATES[i];
            i++;
        }

        // Tax on the remaining taxable income
        if (taxableIncome > BRACKETS[BRACKETS.length - 1]) {
            tax += (taxableIncome - BRACKETS[BRACKETS.length - 1]) * RATES[RATES.length - 1];
        }

        return tax;
    }
}

class SecurityUtils {
    // Method to hash passwords using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException(e);
        }
    }
}
