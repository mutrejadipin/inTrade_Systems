import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

public class FixClient {
    public static void main(String[] args) {
        try {
            // Define the FIX configuration for the client
            SessionSettings settings = new SessionSettings("../config/client.cfg"); // path to your client configuration file
            FileStoreFactory storeFactory = new FileStoreFactory(settings);
            ScreenLogFactory logFactory = new ScreenLogFactory(); // Log to console
            MessageFactory messageFactory = new DefaultMessageFactory();

            // Create an anonymous Application implementation
            Application application = new Application() {
                // onLogon method
                public void onLogon(SessionID sessionID) {
                    System.out.println("Client logged on.");
                }

                // onLogout method
                public void onLogout(SessionID sessionID) {
                    System.out.println("Client logged out.");
                }

                // fromApp method
                public void fromApp(Message message, SessionID sessionID) {
                    System.out.println("Received message: " + message);
                }

                // toApp method
                public void toApp(Message message, SessionID sessionID) {
                    System.out.println("Sending message: " + message);
                }

                // fromAdmin method (added missing implementation)
                public void fromAdmin(Message message, SessionID sessionID) {
                    System.out.println("Received admin message: " + message);
                }

                // toAdmin method (added missing implementation)
                public void toAdmin(Message message, SessionID sessionID) {
                    System.out.println("Sending admin message: " + message);
                }

                // onCreate method (added missing implementation)
                public void onCreate(SessionID sessionID) {
                    System.out.println("Session created: " + sessionID);
                }
            };

            // Create a SocketInitiator (client-side connection)
            SocketInitiator initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
            initiator.start();

            // Create and send a NewOrderSingle message
            NewOrderSingle newOrderSingle = new NewOrderSingle(
                new ClOrdID("12345"), 
                new Side(Side.BUY), 
                new TransactTime(), 
                new OrdType(OrdType.LIMIT)
            );
            newOrderSingle.set(new Symbol("AAPL"));
            newOrderSingle.set(new Price(150.25));
            newOrderSingle.set(new OrderQty(100));

            // Send the message to the server
            Session.sendToTarget(newOrderSingle);
            System.out.println("Sent NewOrderSingle: " + newOrderSingle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
