package wydr.sellers.network;

/**
 * Created by surya on 27/1/16.
 */
public class Transactions {
    public String transaction_id;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Transactions(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
