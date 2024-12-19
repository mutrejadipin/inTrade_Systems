package client;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;


public class Main implements Application {

    public void onCreate(SessionID sessionID) {}

    public void onLogon(SessionID sessionID) {
        try {
            // Sending NewOrderSingle Message
            NewOrderSingle order = new NewOrderSingle(
                    new ClOrdID("12345"),
                    new Side(Side.BUY),
                    new TransactTime(),
                    new OrdType(OrdType.LIMIT)
            );
            order.set(new Symbol("AAPL"));
            order.set(new OrderQty(100));
            order.set(new Price(145.67));

            Session.sendToTarget(order, sessionID);
            System.out.println("Order Sent: " + order);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }

    public void onLogout(SessionID sessionID) {}

    public void toAdmin(Message message, SessionID sessionID) {}

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {}

    public void fromAdmin(Message message, SessionID sessionID) {}

    public void fromApp(Message message, SessionID sessionID) {
        System.out.println("Received from server: " + message);
    }

    public static void main(String[] args) {
        try {
            SessionSettings settings = new SessionSettings("../config/client.cfg");
            Application application = new Main();
            FileStoreFactory storeFactory = new FileStoreFactory(settings);
            ScreenLogFactory logFactory = new ScreenLogFactory();
            MessageFactory messageFactory = new DefaultMessageFactory();
            Initiator initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);

            System.out.println("Client started...");
            initiator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
