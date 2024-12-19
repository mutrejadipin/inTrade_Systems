import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.ExecutionReport;

public class FixServer implements Application {

    public void onCreate(SessionID sessionID) {}

    public void onLogon(SessionID sessionID) {
        System.out.println("Server logged on.");
    }

    public void onLogout(SessionID sessionID) {
        System.out.println("Server logged out.");
    }

    public void toAdmin(Message message, SessionID sessionID) {}

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {}

    public void fromAdmin(Message message, SessionID sessionID) {}

    public void fromApp(Message message, SessionID sessionID) {
        System.out.println("Received from client: " + message);
        
        // Assuming you're expecting a NewOrderSingle message
        if (message instanceof NewOrderSingle) {
            handleNewOrderSingle((NewOrderSingle) message, sessionID);
        }
    }

    private void handleNewOrderSingle(NewOrderSingle newOrder, SessionID sessionID) {
        try {
            String clOrdID = newOrder.getString(ClOrdID.FIELD);
            char side = newOrder.getChar(Side.FIELD);
            String symbol = newOrder.getString(Symbol.FIELD);
            double price = newOrder.getDouble(Price.FIELD);
            int quantity = (int) newOrder.getDouble(OrderQty.FIELD);

            System.out.println("Processing order: ClOrdID=" + clOrdID + ", Side=" + side +
                    ", Symbol=" + symbol + ", Price=" + price + ", Quantity=" + quantity);

            // Validate the order
            if (isValidOrder(side, symbol, price, quantity)) {
                sendExecutionReport(clOrdID, side, symbol, price, quantity, sessionID);
            } else {
                sendReject(clOrdID, "Invalid order parameters", sessionID);
            }
        } catch (FieldNotFound e) {
            System.out.println("Missing field in NewOrderSingle: " + e.getMessage());
        }
    }

    private boolean isValidOrder(char side, String symbol, double price, int quantity) {
        return (side == Side.BUY || side == Side.SELL) &&
                (symbol.equals("AAPL") || symbol.equals("GOOG") || symbol.equals("TSLA") || symbol.equals("AMZN")) &&
                price > 0 && quantity > 0;
    }

    private void sendExecutionReport(String clOrdID, char side, String symbol, double price, int quantity, SessionID sessionID) {
        try {
            ExecutionReport execReport = new ExecutionReport();
            execReport.setField(new OrderID("12345")); // Order ID
            execReport.setField(new ExecID("123")); // Execution ID
            execReport.setField(new ExecType(ExecType.FILL)); // Execution Type
            execReport.setField(new OrdStatus(OrdStatus.FILLED)); // Order Status
            execReport.setField(new ClOrdID(clOrdID));
            execReport.setField(new Side(side));
            execReport.setField(new Symbol(symbol));
            execReport.setField(new LeavesQty(0));
            execReport.setField(new CumQty(quantity));
            execReport.setField(new AvgPx(price));

            Session.sendToTarget(execReport, sessionID);
            System.out.println("Sent Execution Report");
        } catch (SessionNotFound e) {
            System.out.println("Session not found: " + e.getMessage());
        }
    }

    private void sendReject(String clOrdID, String reason, SessionID sessionID) {
        try {
            Message reject = new Message();
            reject.getHeader().setField(new MsgType(MsgType.REJECT));
            reject.setField(new ClOrdID(clOrdID));
            reject.setField(new Text(reason));

            Session.sendToTarget(reject, sessionID);
            System.out.println("Sent Reject: " + reason);
        } catch (SessionNotFound e) {
            System.out.println("Session not found: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Point to your FIX configuration file for the server
            SessionSettings settings = new SessionSettings("../config/server.cfg");
            Application application = new FixServer();

            // Store and log factories
            FileStoreFactory storeFactory = new FileStoreFactory(settings);
            ScreenLogFactory logFactory = new ScreenLogFactory();  // Use ScreenLogFactory to log to the console
            MessageFactory messageFactory = new DefaultMessageFactory();

            // Create and start the server (initiator)
            Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
            System.out.println("Server started...");
            acceptor.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
