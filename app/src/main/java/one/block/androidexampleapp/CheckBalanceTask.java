package one.block.androidexampleapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.RequestBody;
import one.block.eosiojava.error.rpcProvider.RpcProviderError;
import one.block.eosiojava.models.rpcProvider.response.RPCResponseError;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderInitializerError;
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl;

public class CheckBalanceTask extends AsyncTask<String, String, Void> {

    /**
     * Whether the network logs will be enabled for RPC provider
     */
    private static final boolean ENABLE_NETWORK_LOG = true;

    private CheckBalanceTaskCallback callback;

    public CheckBalanceTask(CheckBalanceTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        // Send back statuses to caller for progress update or finishing task with failure or success.
        if (values.length == 1) {
            // Updating case
            String message = values[0];
            this.callback.update(message);
        } else if (values.length == 2) {
            // Failing case
            boolean isSuccess = Boolean.parseBoolean(values[0]);
            String message = values[1];
            this.callback.finish(isSuccess, message, null);
        } else if (values.length == 3) {
            // Successful case
            boolean isSuccess = Boolean.parseBoolean(values[0]);
            String message = values[1];
            String balance = values[2];
            this.callback.finish(isSuccess, message, balance);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String nodeUrl = params[0];
        String fromAccount = params[1];

        EosioJavaRpcProviderImpl rpcProvider;
        try {
            this.publishProgress("Checking Account Balance...");
            rpcProvider = new EosioJavaRpcProviderImpl(nodeUrl, ENABLE_NETWORK_LOG);
            String getCurrentBalanceRequestJSON = "{\n" +
                    "\t\"code\" : \"eosio.token\"\n" +
                    "\t\"account\" : \"" + fromAccount + "\"\n" +
                    "}";

            RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), getCurrentBalanceRequestJSON);
            String responseJSON = rpcProvider.getCurrencyBalance(requestBody);

            this.publishProgress("Account Balance Check Successful!");

            JSONArray jsonArray = new JSONArray(responseJSON);
            if (jsonArray.length() == 0) {
                this.publishProgress(Boolean.toString(false), "Invalid Account!");
                return null;
            }

            String accountBalance = jsonArray.getString(0);

            this.publishProgress(Boolean.toString(true), "Current Account Balance: " + accountBalance, accountBalance);
        } catch (EosioJavaRpcProviderInitializerError eosioJavaRpcProviderInitializerError) {
            // Happens if creating EosioJavaRpcProviderImpl unsuccessful
            eosioJavaRpcProviderInitializerError.printStackTrace();

            this.publishProgress(Boolean.toString(false), eosioJavaRpcProviderInitializerError.asJsonString());
        } catch (RpcProviderError rpcProviderError) {
            // Happens if calling getCurrentBalance unsuccessful
            rpcProviderError.printStackTrace();

            // try to get response from backend if the process fail from backend
            RPCResponseError rpcResponseError = ErrorUtils.getBackendError(rpcProviderError);
            if (rpcResponseError != null) {
                String backendErrorMessage = ErrorUtils.getBackendErrorMessageFromResponse(rpcResponseError);
                this.publishProgress(Boolean.toString(false), backendErrorMessage);
                return null;
            }

            this.publishProgress(Boolean.toString(false), rpcProviderError.getMessage());
        } catch (JSONException e) {
            // Happens if parsing JSON response unsuccessful
            e.printStackTrace();
            this.publishProgress(Boolean.toString(false), e.getMessage());
        }

        return null;
    }

    public interface CheckBalanceTaskCallback {
        void update(String updateContent);

        void finish(boolean success, String updateContent, String balance);
    }
}
