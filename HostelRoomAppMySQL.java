import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Interface for hostel operations
interface HostelActions {
    void allocateRoom(String studentName, String roomNumber);
    String getAvailableRooms();
    String getAllocations();
}

// Database Manager
class HostelDatabaseManager implements HostelActions {
    Connection conn;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HostelDatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hostel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "hostelapp",
                "Hostel@2025"  // <-- change this if needed
            );
            System.out.println("✅ Connected to MySQL successfully!");
        } catch (Exception e) {
            System.out.println("❌ MySQL Connection Failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void allocateRoom(String studentName, String roomNumber) {
        // Prevent inserting null or empty values
        if (studentName == null || studentName.trim().isEmpty() ||
            roomNumber == null || roomNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "⚠️ Please enter both Student Name and Room Number!",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Check if room exists and is available
            String checkRoomSQL = "SELECT status FROM rooms WHERE room_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkRoomSQL);
            checkStmt.setString(1, roomNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(null,
                        "❌ Room number not found in database!",
                        "Invalid Room",
                        JOptionPane.ERROR_MESSAGE);
                rs.close();
                checkStmt.close();
                return;
            }

            String status = rs.getString("status");
            rs.close();
            checkStmt.close();

            if ("Occupied".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null,
                        "⚠️ Room " + roomNumber + " is already occupied!",
                        "Room Unavailable",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert into students table
            String insertSQL = "INSERT INTO students (student_name, room_number, allocation_date) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSQL);
            stmt.setString(1, studentName);
            stmt.setString(2, roomNumber);
            stmt.setString(3, LocalDateTime.now().format(dtf));
            stmt.executeUpdate();
            stmt.close();

            // Update room status to occupied
            String updateSQL = "UPDATE rooms SET status='Occupied' WHERE room_number=?";
            PreparedStatement stmt2 = conn.prepareStatement(updateSQL);
            stmt2.setString(1, roomNumber);
            stmt2.executeUpdate();
            stmt2.close();

            JOptionPane.showMessageDialog(null,
                    "✅ Room " + roomNumber + " successfully allocated to " + studentName + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            System.out.println("✅ Room Allocated Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "❌ Database Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public String getAvailableRooms() {
        StringBuilder sb = new StringBuilder();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms WHERE status='Available'");
            while (rs.next()) {
                sb.append("Room: ").append(rs.getString("room_number"))
                  .append(" | Capacity: ").append(rs.getInt("capacity"))
                  .append(" | Status: ").append(rs.getString("status"))
                  .append("\n");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sb.length() == 0)
            sb.append("⚠️ No available rooms found.\n");
        return sb.toString();
    }

    @Override
    public String getAllocations() {
        StringBuilder sb = new StringBuilder();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY allocation_date DESC");
            while (rs.next()) {
                sb.append("Student: ").append(rs.getString("student_name"))
                  .append(" | Room: ").append(rs.getString("room_number"))
                  .append(" | Date: ").append(rs.getString("allocation_date"))
                  .append("\n");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sb.length() == 0)
            sb.append("⚠️ No allocations found.\n");
        return sb.toString();
    }
}

// Main GUI Application
public class HostelRoomAppMySQL {
    private HostelDatabaseManager dbManager = new HostelDatabaseManager();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HostelRoomAppMySQL().createGUI());
    }

    public void createGUI() {
        JFrame frame = new JFrame("🏠 Hostel Room Allocation System");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        inputPanel.setBackground(new Color(240, 248, 255));

        JLabel nameLabel = new JLabel("Student Name:");
        JTextField nameField = new JTextField();
        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField();

        JButton allocateBtn = new JButton("Allocate Room");
        allocateBtn.setBackground(new Color(46, 204, 113));
        allocateBtn.setForeground(Color.WHITE);
        allocateBtn.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(roomLabel);
        inputPanel.add(roomField);
        inputPanel.add(allocateBtn);

        JTextArea logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Logs"));

        JButton viewRoomsBtn = new JButton("View Available Rooms");
        JButton viewAllocationsBtn = new JButton("View Allocations");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewRoomsBtn);
        bottomPanel.add(viewAllocationsBtn);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        allocateBtn.addActionListener(e -> {
            dbManager.allocateRoom(nameField.getText().trim(), roomField.getText().trim());
            logArea.setText(dbManager.getAllocations());
        });

        viewRoomsBtn.addActionListener(e -> {
            logArea.setText(dbManager.getAvailableRooms());
        });

        viewAllocationsBtn.addActionListener(e -> {
            logArea.setText(dbManager.getAllocations());
        });

        frame.setVisible(true);
    }
}
//*cd "D:\hostel_allocation_system"
//javac -cp ".;lib\mysql-connector-j-9.5.0.jar" HostelRoomAppMySQL.java
//java -cp ".;lib\mysql-connector-j-9.5.0.jar" HostelRoomAppMySQL
