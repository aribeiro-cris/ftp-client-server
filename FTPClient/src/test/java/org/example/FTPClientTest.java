package org.example;

import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class FTPClientTest {

    FTPClient ftpClient;
    private CompletableFuture<Void> futureServerConnection;
    private BufferedReader mockKeyboardInputBufferedReader;
    private BufferedWriter mockOutPutBufferedWriter;
    private BufferedReader mockInputBufferedReader;
    private Socket mockSocket;

    @BeforeAll
    public static void setupAll() {
        System.out.println("Should Print Before All Tests");
    }

    @BeforeEach
    public void setup() {
        ftpClient = mock(FTPClient.class);
        mockSocket = mock(Socket.class);
        futureServerConnection = CompletableFuture.runAsync(() -> ftpClient.init("localhost",55555));
        mockKeyboardInputBufferedReader = mock(BufferedReader.class);
        mockOutPutBufferedWriter = mock(BufferedWriter.class);
        mockInputBufferedReader = mock(BufferedReader.class);
        ftpClient.setSocket(mockSocket);
        ftpClient.setKeyboardInputBufferedReader(mockKeyboardInputBufferedReader);
        ftpClient.setOutPutBufferedWriter(mockOutPutBufferedWriter);
        ftpClient.setInputBufferedReader(mockInputBufferedReader);
    }

    @Test
    public void shouldEstablishConnectionWithServer() throws IOException {
        Assertions.assertNotNull(new ServerSocket(55555));
    }

    @Test
    public void shouldNotEstablishConnectionWithServer() throws IOException {
        Assertions.assertNotNull(new ServerSocket(55900));
        Assertions.assertThrows(IOException.class, () -> new ServerSocket(55900));
    }

    @Test
    public void shouldTerminateConnectionWithBYECommand() throws IOException {
        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("BYE");
        // Method to test
        ftpClient.closingSocketAndBuffers();
        mockSocket.close();
    }

    @Test
    public void shouldTerminateConnectionWithQUITCommand() throws IOException {

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("QUIT");
        // Method to test
        ftpClient.closingSocketAndBuffers();
        mockSocket.close();
    }

    @Test
    public void shouldTerminateConnectionWithDISCONNECTCommand() throws IOException{

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("DISCONNECT");
        // Method to test
        ftpClient.closingSocketAndBuffers();
        mockSocket.close();
    }

    @Test
    public void shouldReceivedSAvailableCommandsWithHELPCommand() throws IOException {
        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("HELP");

        // Method to test
        ftpClient.clientCom();
    }

    @Test
    public void shouldReceivedAListWithFilesAvailableWithLSCommand() throws IOException {

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("LS");

        // Method to test
        ftpClient.clientCom();
    }

    @Test
    public void shouldSendAFileWithPUTCommand() throws IOException {

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("PUT");

        // Method to test
        ftpClient.executePUTCommand(mockInputBufferedReader.readLine());
    }

    @Test
    public void shouldReceivedAFileWithGETCommand() throws IOException {

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("GET");

        // Method to test
        ftpClient.executeGETCommand(mockInputBufferedReader.readLine());
    }
    @Test
    public void shouldCreateANewDirectoryWithMKDIRCommandOnTheServer() throws IOException {

        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("MDKIR");

        // Method to test
        ftpClient.clientCom();
    }

    @Test
    public void shouldNotReturnAnInvalidCommand() throws IOException {
        when(mockKeyboardInputBufferedReader.readLine()).thenReturn("invalid command");

        ftpClient.clientCom();
    }

    @AfterEach
    public void tearDown(){
        try {
            if (mockInputBufferedReader != null)
                mockInputBufferedReader.close();
            if (mockOutPutBufferedWriter != null)
                mockOutPutBufferedWriter.close();
            if(mockKeyboardInputBufferedReader != null) {
                mockKeyboardInputBufferedReader.close();
            }
            if (mockSocket != null)
                mockSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Should Execute After Each Test");
    }

    @AfterAll
    public static void tearDownAll(){
        System.out.println("Should be executed at the end of the Test");
    }
}