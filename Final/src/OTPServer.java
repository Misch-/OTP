import static org.kohsuke.args4j.ExampleMode.ALL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the one-time-pad server, it requests a port number, and a hex seed for SecureRandom. It then waits for
 * a client to connect and spawns a separate thread for it.
 * <p>
 * Arg switches generated with args4j, no need for that tedium.
 */

public class OTPServer {

    @Option(name = "-p", usage = "port to connect to")
    private int portnumber = 9090;
    @Option(name = "-k", usage = "32bits/8hex/16char(0-F) string to seed SecureRandom for a one-time-pad", required = true)
    private String hexStringSeed;

    public static void main(String[] args) throws IOException, CmdLineException {
        new OTPServer().doMain(args);
    }

    public void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            // parse the arguments.
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java OTPServer [options...]");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            // print option sample. This is useful some time
            System.err.println("  Example: java OTPServer" + parser.printExample(ALL));
            return;
        }
        ServerSocket server = null;
        boolean serverRunning = true;

        try {
            server = new ServerSocket(portnumber);
            System.out.println("Started Server Listening to Port: " + portnumber);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + portnumber);
            System.exit(-1);
        }

        while (serverRunning) {
            Socket acceptedSocket = server.accept();
            new OTPServerThread(acceptedSocket, acceptedSocket.getRemoteSocketAddress(), hexStringSeed).start();
        }
        server.close();
    }
}
