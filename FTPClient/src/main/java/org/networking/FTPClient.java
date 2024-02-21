package org.networking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FTPClient {
    private Socket socket;
    private BufferedReader keyboardInputBufferedReader;
    private BufferedWriter outPutBufferedWriter;
    private BufferedReader inputBufferedReader;
    private String clientRoot = "./clientRoot/";
    private String END_OF_MESSAGE = "ENDOFMESSAGE";

    /**
     * This method has the main goal of establish connection between server and client
     * It also instantiates the buffers readers and buffer writer
     */
    public void init(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            System.out.println("Connection established with the server on port " + port);

            keyboardInputBufferedReader = new BufferedReader(new InputStreamReader(System.in));
            outPutBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inputBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException io) {
            System.out.println("error trying to connect");
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute PUT command;
     * PUT command is responsible uploading a file from the client side to the server
     * @param line represents the input from the client
     */
    public void executePUTCommand(String line) {
        try {
            String fileName = line.substring(4).trim();
            System.out.println("Client needs to PUT file on the server: " + fileName);
            File newFileCreated = new File(clientRoot + fileName);

            if (!newFileCreated.exists()) {
                System.out.println("File doesn't exist.");
            }
            System.out.println("Starting file upload...");
            Scanner reader = new Scanner(newFileCreated); //reads the file
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                System.out.println(data);
                outPutBufferedWriter.write(data);
                outPutBufferedWriter.newLine();
            }
            outPutBufferedWriter.write(END_OF_MESSAGE);
            outPutBufferedWriter.newLine();
            outPutBufferedWriter.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * This method is used to execute GET command;
     * GET command is responsible for get a file from the server and add it on the client side
     * @param line represents the input from the client
     */
    public void executeGETCommand(String line) {
        try {
            String fileName = line.substring(4).trim();
            System.out.println("The file asked with the GET command was: " + fileName);
            File newFileName = new File(clientRoot + fileName);
            newFileName.createNewFile();
            FileWriter fileWriter = new FileWriter(newFileName);
            while(!line.equals(END_OF_MESSAGE)){
                System.out.println(line);
                line = inputBufferedReader.readLine();
                if(!line.equals(END_OF_MESSAGE)) {
                    fileWriter.write(line + "\n");
                }
            }
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Constructor of the FTPClient
     */
    public FTPClient() {

    }

    /**
     * Method responsible to send input to the server according to the commands
     */
    public void clientCom() {
        String line = "";
        String serverEcho = "";

        try {
            while(!line.equals("QUIT") && !line.equals("BYE") && !line.equals("DISCONNECT")){

                line = keyboardInputBufferedReader.readLine();
                outPutBufferedWriter.write(line);
                outPutBufferedWriter.newLine();
                outPutBufferedWriter.flush();

                if(line.startsWith("PUT")) {
                    executePUTCommand(line);
                }
                else if(line.startsWith("GET")) {
                    executeGETCommand(line);
                }
                else {
                    while (!serverEcho.equals(END_OF_MESSAGE)) {
                        System.out.println(serverEcho);
                        serverEcho = inputBufferedReader.readLine();
                    }
                }
                serverEcho = "";

            }
            closingSocketAndBuffers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method responsible to close the buffers readers and buffer writer
     */
    public void closingSocketAndBuffers() {
        try {
            inputBufferedReader.close();
            outPutBufferedWriter.close();
            keyboardInputBufferedReader.close();
            System.out.println("Connection terminated.");
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setKeyboardInputBufferedReader(BufferedReader keyboardInputBufferedReader) {
        this.keyboardInputBufferedReader = keyboardInputBufferedReader;
    }

    public void setOutPutBufferedWriter(BufferedWriter outPutBufferedWriter) {
        this.outPutBufferedWriter = outPutBufferedWriter;
    }

    public void setInputBufferedReader(BufferedReader inputBufferedReader) {
        this.inputBufferedReader = inputBufferedReader;
    }

    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.init("localhost", 55555);
        ftpClient.clientCom();
    }
}