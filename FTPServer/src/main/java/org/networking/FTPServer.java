package org.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FTPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader inputBufferedReader;
    private BufferedWriter outputBufferedWriter;
    private String outputHelp = "DISCONNECT, BYE, QUIT, LS, PUT, GET, MKDIR, HELP\n";
    private String serverRoot = "./serverRoot/";
    private File serverRootPath = new File(serverRoot);
    private String fileOfServerRoot[];
    private String END_OF_MESSAGE = "ENDOFMESSAGE";
    private InputStream inputStream;
    private OutputStream outputStream;
    private String fileName;

    /**
     * This method has the main goal of establish connection between server and client
     * It also instantiates the buffer reader and writer
     */
    public void init(int port) {
        try {
            //Create server socket and listen
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);

            //Create client socket
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            //Set up input stream
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputBufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Method to terminate connection between client and server
     */
    public void terminateConnection() {
        try {
            System.out.println("Connection terminated.");
            if (inputBufferedReader != null) inputBufferedReader.close();
            if (outputBufferedWriter != null) outputBufferedWriter.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Method that represents the end of message send between client and server
     */
    public void endOfMessage() {
        try {
            outputBufferedWriter.write(END_OF_MESSAGE);
            outputBufferedWriter.newLine();
            outputBufferedWriter.flush(); //
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute PUT command;
     * PUT command is responsible uploading a file from the client side to the server
     * @param input represents the message received from the client
     */
    public void executePUTCommand(String input) {
        try{
            fileName = input.substring(3).trim(); //PUT theNewColossus.txt
            System.out.println("Client sent a PUT command for the file: " + fileName);
            File newFileCreated = new File(serverRoot + fileName);
            newFileCreated.createNewFile();
            FileWriter fileWriter = new FileWriter(newFileCreated);

            while(!input.equals(END_OF_MESSAGE)){
                System.out.println(input);
                input = inputBufferedReader.readLine();
                if(!input.equals(END_OF_MESSAGE)) {
                    fileWriter.write(input + "\n");
                }
            }
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute GET command;
     * GET command is responsible for get a file from the server and add it on the client side
     * @param input represents the message received from the client
     */
    public void executeGETCommand(String input) {
        try {
            fileName = input.substring(3).trim(); //GET theRaven.txt
            System.out.println("Client asked a GET command for the file: " + fileName);
            File newFileName = new File(serverRoot + fileName);

            if(!newFileName.exists()) {
                System.out.println("File doesn't exist.");
            }
            System.out.println("Starting file download to client...");
            Scanner reader = new Scanner(newFileName);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                System.out.println(data);
                outputBufferedWriter.write(data);
                outputBufferedWriter.newLine();
            }
            endOfMessage();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute the LS command;
     * LS is responsible to list files available on the server
     */
    public void sendLSResponse() {
        try{
            fileOfServerRoot = serverRootPath.list();
            for(int i = 0; i < fileOfServerRoot.length; i++) {
                outputBufferedWriter.write(fileOfServerRoot[i]);
                outputBufferedWriter.newLine();
            }
            endOfMessage();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute the HELP command;
     * HELP is responsible to show available commands on the client side
     */
    public void sendHELPResponse() {
        try {
            outputBufferedWriter.write(outputHelp);
            endOfMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to execute the MKDIR command;
     * MKDIR is responsible to create a directory on the server
     * @param input represents the message received from the client
     */
    public void executeMkdirCommand(String input) {
        try {
            String pathToDirectory = input.substring(5).trim(); //
            File theDir = new File(pathToDirectory);
            if (!theDir.exists()){
                outputBufferedWriter.write("Directory was successfully created on: " + pathToDirectory);
                theDir.mkdirs();
            } else {
                outputBufferedWriter.write("Directory already exists.");
            }
            outputBufferedWriter.newLine();
            endOfMessage();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Method responsible to received input from the client according to the commands
     */
    public void serverCom() {
        try {
            //Get input from client
            String input = "";
            while (true) {
                input = inputBufferedReader.readLine();
                System.out.println("Input received from the client: " + input);

                if (input.equals("QUIT") || input.equals("BYE") || input.equals("DISCONNECT")) {
                    terminateConnection();
                    break;
                }
                else if (input.equals("HELP")) {
                    sendHELPResponse();
                }
                else if (input.equals("LS")) {
                    sendLSResponse();
                }
                else if(input.startsWith("PUT")) {
                    executePUTCommand(input);
                }
                else if(input.startsWith("GET")) {
                    executeGETCommand(input);
                }
                else if(input.startsWith("MKDIR")) {
                    executeMkdirCommand(input);
                }
                else {
                    outputBufferedWriter.write("Not a valid command: command " + input + " not known\n");
                    endOfMessage();
                }
            }
            } catch(IOException e){
                e.printStackTrace();
            }
        }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setInputBufferedReader(BufferedReader inputBufferedReader) {
        this.inputBufferedReader = inputBufferedReader;
    }

    public void setOutputBufferedWriter(BufferedWriter outputBufferedWriter) {
        this.outputBufferedWriter = outputBufferedWriter;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static void main(String[] args) {
        FTPServer ftpServer = new FTPServer();
        ftpServer.init(55555);
        ftpServer.serverCom();
    }
}