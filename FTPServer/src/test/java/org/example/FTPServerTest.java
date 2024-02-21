package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class FTPServerTest {

    private FTPServer ftpServer;
    private CompletableFuture<Void> futureClientConnection;
    private BufferedWriter mockOutputBufferedWriter;
    private BufferedReader mockInputBufferedReader;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private String input;
    private ServerSocket mockServerSocket;
    private Socket mockClientSocket;
    private String mockFileName;
    private static final String END_OF_MESSAGE = "END_OF_MESSAGE";
    private String fileContent = "some file content";


    @BeforeAll
    public static void setupAll() {
        System.out.println("Should Print Before All Tests");
    }

    @BeforeEach
    public void setup() {
        ftpServer = new FTPServer();
        mockOutputBufferedWriter = mock(BufferedWriter.class);
        mockInputBufferedReader = mock(BufferedReader.class);
        mockServerSocket = mock(ServerSocket.class);
        mockClientSocket = mock(Socket.class);
        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);
        ftpServer.setServerSocket(mockServerSocket);
        ftpServer.setClientSocket(mockClientSocket);
        ftpServer.setInputBufferedReader(mockInputBufferedReader);
        ftpServer.setOutputBufferedWriter(mockOutputBufferedWriter);
        ftpServer.setInputStream(mockInputStream);
        ftpServer.setOutputStream(mockOutputStream);
        futureClientConnection = CompletableFuture.runAsync(() -> ftpServer.init(55555));
    }

    @Test
    public void shouldEstablishConnectionWithClient() throws IOException {
        Assertions.assertNotNull(new Socket("localhost", 55555));
        //Assertions.assertTrue(futureClientConnection.isDone());
    }

    @Test
    public void shouldNotEstablishConnectionWithClient() throws IOException {
        Assertions.assertThrows(IOException.class, () -> new Socket("localhost", 55900));
        //Assertions.assertTrue(futureClientConnection.cancel(true));
        //mockServerSocket.close();
    }

    @Test
    public void shouldTerminateConnectionWithBYECommand() throws IOException {
        when(mockInputBufferedReader.readLine()).thenReturn("BYE");
        ftpServer.terminateConnection();
        futureClientConnection.cancel(true);
        //mockServerSocket.close();
//        verify(mockServerSocket).close();
    }

    @Test
    public void shouldTerminateConnectionWithQUITCommand() throws IOException {
        when(mockInputBufferedReader.readLine()).thenReturn("QUIT");
        ftpServer.terminateConnection();
        futureClientConnection.cancel(true);
        //mockServerSocket.close();
//        verify(mockServerSocket).close();
    }

    @Test
    public void shouldTerminateConnectionWithDISCONNECTCommand() throws IOException {
        when(mockInputBufferedReader.readLine()).thenReturn("DISCONNECT");
        ftpServer.terminateConnection();
        futureClientConnection.cancel(true);
        //mockServerSocket.close();
//        verify(mockServerSocket).close();
    }

    @Test
    public void shouldShowSAvailableCommandsWithHELPCommand() throws IOException {
        when(mockInputBufferedReader.readLine()).thenReturn("HELP");
        ftpServer.sendHELPResponse();
        verify(mockOutputBufferedWriter).write("DISCONNECT, BYE, QUIT, LS, PUT, GET, MKDIR, HELP\n");
        verify(mockOutputBufferedWriter).flush();
    }

    @Test
    public void shouldListFilesAvailableWithLSCommand() throws IOException {
        when(mockInputBufferedReader.readLine()).thenReturn("LS");
        ftpServer.sendLSResponse();
        verify(mockOutputBufferedWriter).flush();
    }

    @Test
    public void shouldReceivedAFileWithPUTCommand() throws IOException {
        mockFileName = "fileTest.txt";
        input = "PUT " + mockFileName;

        when(mockInputBufferedReader.readLine())
                .thenReturn(input)
                .thenReturn(fileContent)
                .thenReturn(END_OF_MESSAGE);

        mockOutputBufferedWriter.write(fileContent);

        //ftpServer.executePUTCommand(input);
        //verify(mockInputBufferedReader).read();
        //verify(mockOutputBufferedWriter).flush();
    }

    @Test
    public void shouldSendAFileWithGETCommand() throws IOException {
        mockFileName = "dummy.txt";
        input = "GET " + mockFileName;
        when(mockInputBufferedReader.readLine())
                .thenReturn(input)
                .thenReturn(fileContent)
                .thenReturn(END_OF_MESSAGE); // Then return the end of message

        mockOutputBufferedWriter.write(fileContent);
        ftpServer.executeGETCommand(input);
        verify(mockOutputBufferedWriter).flush();
    }

    @Test
    public void shouldCreateANewDirectoryWithMKDIRCommand() throws IOException {
        input = "MKDIR";

        when(mockInputBufferedReader.readLine()).thenReturn("MKDIR");
        ftpServer.executeMkdirCommand(input);
        verify(mockOutputBufferedWriter).write("Directory was successfully created on: ");
        verify(mockOutputBufferedWriter).flush();
    }

    @Test
    public void shouldNotReturnAnInvalidCommand() throws IOException {
        input = "invalid command";

        when(mockInputBufferedReader.readLine()).thenReturn(input);
        ftpServer.endOfMessage();
        mockOutputBufferedWriter.write("Not a valid command: command " + input + " not known\n");
        verify(mockOutputBufferedWriter).flush();
    }

    @AfterEach
    public void tearDown(){
        try {
            if (mockOutputBufferedWriter != null)
                mockOutputBufferedWriter.close();
            if (mockInputBufferedReader != null)
                mockInputBufferedReader.close();
            if (mockClientSocket != null)
                mockClientSocket.close();
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