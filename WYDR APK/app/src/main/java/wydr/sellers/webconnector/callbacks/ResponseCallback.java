package wydr.sellers.webconnector.callbacks;

/**
 * Created by Techwider on 5/19/2016.
 */
public interface ResponseCallback {
    void onSuccess(ResponseHolder holder, int token);
    void onFailure(String error);
}
