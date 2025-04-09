# P2P Network message communication


## Overview
This project implements a Peer-to-Peer (P2P) networking application using Java Swing for the graphical user interface. It allows users to connect with peers, send messages, and manage peer connections via a simple interface.

## Features
- **Start Server**: Launch a server on a specified port to accept incoming connections.
- **Connect to Peer**: Add a peer by entering their name, IP address, and port.
- **Send Message**: Send messages to a selected peer.
- **List Peers**: Display connected peers in the application.
- **Exit**: Cleanly exit the application and stop the server.

## Requirements
- Java Development Kit (JDK) 8 or higher
- Basic understanding of Java programming and networking concepts

## How to Run
1. **Clone the repository** or download the source code.
2. **Compile the code** using a Java compiler or IDE.
   ```bash
   javac P2PNetworkGUI.java
Run the application:
bash

Copy
java P2PNetworkGUI
Enter your name and port in the top panel, then click "Start Server" to begin accepting connections.
To connect to a peer, fill in the peer's name, IP address, and port, then click "Connect to Peer."
Send messages to the connected peers by selecting them from the dropdown and clicking "Send Message."
Code Structure
Peer Class: Represents a peer with a name, IP address, and port.
Main Class: Implements the GUI and handles networking operations.
main: The entry point of the application.
startServer: Starts the server to accept incoming client connections.
handleClient: Handles communication with connected clients.
sendMessage: Sends messages to a specified peer.
log: Updates the log area in the GUI.
closeServer: Stops the server when exiting the application.
Future Improvements
Implement error handling for invalid inputs.
Add features such as message history and user authentication.
Enhance the user interface for better usability.
