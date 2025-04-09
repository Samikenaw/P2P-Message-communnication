import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class P2PNetworkGUI {
    // Peer class to hold user details
    static class Peer {
        String name;
        String ipAddress;
        int port;

        Peer(String name, String ipAddress, int port) {
            this.name = name;
            this.ipAddress = ipAddress;
            this.port = port;
        }

        @Override
        public String toString() {
            return name + " (" + ipAddress + ":" + port + ")";
        }
    }

    // List of connected peers
    private static final List<Peer> peers = new ArrayList<>();
    private static JTextArea logArea;
    private static JComboBox<String> peerDropdown;
    private static String localName;
    private static int localPort;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("P2P Network");
            frame.setSize(700, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));

            // Top Panel for Inputs
            JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextField nameField = new JTextField();
            JTextField portField = new JTextField();
            JTextField peerNameField = new JTextField();
            JTextField peerIPField = new JTextField();
            JTextField peerPortField = new JTextField();

            topPanel.add(new JLabel("Your Name:"));
            topPanel.add(nameField);
            topPanel.add(new JLabel("Your Port:"));
            topPanel.add(portField);
            topPanel.add(new JLabel("Peer Name:"));
            topPanel.add(peerNameField);
            topPanel.add(new JLabel("Peer IP Address:"));
            topPanel.add(peerIPField);
            topPanel.add(new JLabel("Peer Port:"));
            topPanel.add(peerPortField);

            // Center Panel for Log Area
            JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

            logArea = new JTextArea();
            logArea.setEditable(false);
            logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(logArea);
            centerPanel.add(new JLabel("Log Area:"), BorderLayout.NORTH);
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            // Bottom Panel for Buttons and Peer Dropdown
            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

            JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
            JButton startButton = new JButton("Start Server");
            JButton connectButton = new JButton("Connect to Peer");
            JButton sendMessageButton = new JButton("Send Message");
            JButton listPeersButton = new JButton("List Peers");
            JButton exitButton = new JButton("Exit");

            buttonPanel.add(startButton);
            buttonPanel.add(connectButton);
            buttonPanel.add(sendMessageButton);
            buttonPanel.add(listPeersButton);
            buttonPanel.add(exitButton);

            peerDropdown = new JComboBox<>();
            peerDropdown.addItem("Select Peer");

            bottomPanel.add(peerDropdown, BorderLayout.NORTH);
            bottomPanel.add(buttonPanel, BorderLayout.CENTER);

            // Add Panels to Frame
            frame.add(topPanel, BorderLayout.NORTH);
            frame.add(centerPanel, BorderLayout.CENTER);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            frame.setVisible(true);

            // Action Listeners
            startButton.addActionListener(e -> {
                localName = nameField.getText();
                try {
                    localPort = Integer.parseInt(portField.getText());
                    new Thread(() -> startServer(localName, localPort)).start();
                    log("Server started on port " + localPort);
                } catch (NumberFormatException ex) {
                    log("Invalid port number.");
                }
            });

            connectButton.addActionListener(e -> {
                String peerName = peerNameField.getText();
                String peerIP = peerIPField.getText();
                int peerPort;
                try {
                    peerPort = Integer.parseInt(peerPortField.getText());
                    Peer peer = new Peer(peerName, peerIP, peerPort);
                    peers.add(peer);
                    peerDropdown.addItem(peer.name);
                    log("Connected to peer: " + peer);
                } catch (NumberFormatException ex) {
                    log("Invalid peer port.");
                }
            });

            sendMessageButton.addActionListener(e -> {
                String selectedPeerName = (String) peerDropdown.getSelectedItem();
                if (selectedPeerName == null || selectedPeerName.equals("Select Peer")) {
                    log("No peer selected.");
                    return;
                }

                Peer targetPeer = peers.stream()
                        .filter(p -> p.name.equals(selectedPeerName))
                        .findFirst()
                        .orElse(null);

                if (targetPeer == null) {
                    log("Peer not found.");
                    return;
                }

                String message = JOptionPane.showInputDialog("Enter your message:");
                if (message != null && !message.isEmpty()) {
                    sendMessage(targetPeer, message, localName);
                }
            });

            listPeersButton.addActionListener(e -> {
                log("Connected peers:");
                for (Peer peer : peers) {
                    log(peer.toString());
                }
            });

            exitButton.addActionListener(e -> {
                log("Exiting...");
                closeServer();
                System.exit(0);
            });
        });
    }

    private static void startServer(String name, int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                log("Message received: " + message);
                out.println("Message received: " + message);
            }
        } catch (IOException e) {
            log("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static void sendMessage(Peer peer, String message, String senderName) {
        try (Socket socket = new Socket(peer.ipAddress, peer.port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(senderName + ": " + message);
            String response = in.readLine();
            log("Response from " + peer.name + ": " + response);
        } catch (IOException e) {
            log("Error sending message: " + e.getMessage());
        }
    }

    private static void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    private static void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log("Server stopped.");
            }
        } catch (IOException e) {
            log("Error stopping server: " + e.getMessage());
        }
    }
}