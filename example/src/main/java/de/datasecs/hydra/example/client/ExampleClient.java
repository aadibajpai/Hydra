package de.datasecs.hydra.example.client;

import de.datasecs.hydra.client.Client;
import de.datasecs.hydra.client.HydraClient;
import de.datasecs.hydra.example.server.ExampleServerProtocol;
import de.datasecs.hydra.example.shared.ExamplePacket;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.handler.listener.HydraSessionListener;
import io.netty.channel.ChannelOption;

import java.util.Arrays;

/**
 * Created with love by DataSecs on 02.11.2017.
 */
public class ExampleClient {

    private static Session session;

    public static void main(String[] args) {
        /*
         * The session listener is optional, that's why it's a method that may be called in the builder.
         * It adds a listener to the client and is supposed to be called when
         * a session is created (in this case, when the client connects to a server). For demonstration purposes
         * this is done via a direct instantiation (anonymous class). It's advised to do this in a separate class
         * for clearness, especially when there are other methods than just the two small from the
         * SessionListener interface.
         */

        // The builder returns a session which you can use for several things
        HydraClient client = new Client.Builder("localhost", 8888, new ExampleServerProtocol())
                .workerThreads(4)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .addSessionListener(new HydraSessionListener() {
                    @Override
                    public void onConnected(Session session) {
                        System.out.println("Connected to server!");
                    }

                    @Override
                    public void onDisconnected(Session session) {
                        System.out.println("\nDisconnected from server!");
                    }
                })
                .build();

        // Checks if the client is connected to its remote host (not obligatory)
        if (client.isConnected()) {
            // Returns the session that was created for the client and its remote host
            session = client.getSession();
            System.out.println("\nClient is online!");
            System.out.printf("Socket address: %s%n", session.getAddress());
        }

        /* Send a packet to the server via the session the client has saved */
        // Sends a String, that is converted to a Object and an array, the type of the array is defined in ExamplePacket.class
        session.send(new ExamplePacket("This is a message", new String[]{"This", "is", "a", "message"}));
        // Sends a list, that is converted to a Object and the array, like above
        session.send(new ExamplePacket(Arrays.asList("This", "is", "a", "message", "2"), new String[]{"This", "is", "a", "message", "2"}));
        /* Sends an object the user wants to send with the limitation that the object has to be serializable.
         * Hydra internally uses a standard packet that comes ready out of the box. The only thing that is important to notice
         * is the fact, that the Handler for the packet still has to be created by the user itself. Therefore see
         * the ExampleClientPacketListener of the server example classes.
         */
        session.send("This is a String and dealt with as object by Hydra");

        // Closes the connection and releases all occupied resources
        //client.close();
    }
}