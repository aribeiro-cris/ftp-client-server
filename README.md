# FTP Client-Server

This project implements a simple File Transfer Protocol (FTP) client-server application using Java sockets. The application allows users to upload (PUT) and download (GET) files between the client and server. Additionally, it supports commands such as listing files (LS), creating directories (MKDIR), and displaying available commands (HELP). The client-server communication is based on text-based messages, where clients can send commands and receive responses from the server.

## FTP Client
The FTP client (FTPClient) establishes a connection to the server and provides a command-line interface for users to interact with. Users can input commands to upload files to the server (PUT), download files from the server (GET), or execute other supported commands. The client manages input/output streams for communication with the server and handles the execution of various commands.

## FTP Server
The FTP server (FTPServer) listens for incoming connections from clients and handles client requests. It supports commands sent by clients, such as uploading files (PUT), downloading files (GET), listing files on the server (LS), creating directories (MKDIR), and providing help on available commands (HELP). The server manages incoming connections, interprets client commands, and performs corresponding actions, such as transferring files or responding with appropriate messages.

## Key Features
- Client-Server Communication: Enables file transfer and command execution between the client and server over TCP/IP sockets.
- Command-Line Interface: Provides a user-friendly command-line interface for users to interact with the FTP client.
- File Transfer: Supports uploading and downloading files between the client and server.
- Command Execution: Executes various commands, including listing files, creating directories, and providing help.
  
## Usage
- Start the Server: Run the FTPServer class to start the FTP server. The server will listen for incoming connections on the specified port.
- Connect the Client: Run the FTPClient class to start the FTP client. Enter the server's IP address and port to establish a connection.
- Interact with the Client: Use the command-line interface of the FTP client to upload/download files, execute commands, and interact with the server.

## Testing
This package contains a set of JUnit tests for the FTPClient and FTPServer classes. The tests ensure that the functionality of both the client and server components behaves as expected under different scenarios.
