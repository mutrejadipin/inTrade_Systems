package common;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Price;
import quickfix.field.OrderQty;

public class MessageValidator {

    // Validate NewOrderSingle message
    public static boolean validateNewOrderSingle(Message message) {
        try {
            // Validate ClOrdID (Tag 11: ClOrdID)
            String clOrdID = message.getString(11);
            if (clOrdID.isEmpty()) {
                return false;
            }

            // Validate Side (Tag 54: Side)
            int side = message.getInt(54);
            if (side != 1 && side != 2) { // 1 for Buy, 2 for Sell
                return false;
            }

            // Validate Symbol (Tag 55: Symbol)
            String symbol = message.getString(55);
            if (!symbol.equals("AAPL") && !symbol.equals("GOOG") && !symbol.equals("TSLA") && !symbol.equals("AMZN")) {
                return false;
            }

            // Validate Price (Tag 44: Price)
            double price = message.getDouble(44);
            if (price <= 0) {
                return false;
            }

            // Validate Quantity (Tag 32: Quantity)
            int quantity = message.getInt(32);
            if (quantity <= 0) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
