package one.block.androidexampleapp;

import android.os.AsyncTask;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.session.TransactionPrepareError;
import one.block.eosiojava.error.session.TransactionSignAndBroadCastError;
import one.block.eosiojava.implementations.ABIProviderImpl;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Authorization;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.models.rpcProvider.response.RPCResponseError;
import one.block.eosiojava.models.rpcProvider.response.SendTransactionResponse;
import one.block.eosiojava.session.TransactionProcessor;
import one.block.eosiojava.session.TransactionSession;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderInitializerError;
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl;
import one.block.eosiosoftkeysignatureprovider.SoftKeySignatureProviderImpl;
import one.block.eosiosoftkeysignatureprovider.error.ImportKeyError;

/**
 * This class is an example about the most basic/easy way to use eosio-java to send a transaction.
 * <p>
 * Basic steps:
 * <p>
 *     - Create serialization provider as an instant of {@link AbiEosSerializationProviderImpl} from [eosiojavaandroidabieosserializationprovider] library
 *     <p>
 *     - Create RPC provider as an instant of {@link EosioJavaRpcProviderImpl} with an input string point to a node backend.
 *     <p>
 *     - Create ABI provider as an instant of {@link ABIProviderImpl} with instants of Rpc provider and serialization provider.
 *     <p>
 *     - Create Signature provider as an instant of {@link SoftKeySignatureProviderImpl} which is not recommended for production because of its simple key management.
 *     <p>
 *         - Import an EOS private key which associate with sender's account which will be used to sign the transaction.
 * <p>
 *     - Create an instant of {@link TransactionSession} which is used for spawning/factory {@link TransactionProcessor}
 * <p>
 *     - Create an instant of {@link TransactionProcessor} from the instant of {@link TransactionSession} above by calling {@link TransactionSession#getTransactionProcessor()} or {@link TransactionSession#getTransactionProcessor(Transaction)} if desire to use a preset {@link Transaction} object.
 * <p>
 *     - Call {@link TransactionProcessor#prepare(List)} with a list of Actions which is desired to be sent to backend. The method will serialize the list of action to list of hex and keep them inside
 * the list of {@link Transaction#getActions()}. The transaction now is ready to be signed and broadcast.
 * <p>
 *     - Call {@link TransactionProcessor#signAndBroadcast()} to sign the transaction inside {@link TransactionProcessor} and broadcast it to backend.
 */
public class TransactionTask extends AsyncTask<String, String, Void> {

    /**
     * Whether the network logs will be enabled for RPC provider
     */
    private static final boolean ENABLE_NETWORK_LOG = true;

    private TransactionTaskCallback callback;

    public TransactionTask(@NonNull TransactionTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values.length == 1) {
            String message = values[0];
            this.callback.update(message);
        } else if (values.length == 2) {
            boolean isSuccess = Boolean.parseBoolean(values[0]);
            String message = values[1];
            this.callback.finish(isSuccess, message);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String nodeUrl = params[0];
        String fromAccount = params[1];
        String toAccount = params[2];
        String privateKey = params[3];
        String amount = params[4];
        String memo = params[5];

        this.publishProgress("Transferring " + amount + " to " + toAccount);

        // Creating serialization provider
        ISerializationProvider serializationProvider;
        try {
            serializationProvider = new AbiEosSerializationProviderImpl();
        } catch (SerializationProviderError serializationProviderError) {
            serializationProviderError.printStackTrace();
            return null;
        }

        // Creating RPC Provider
        IRPCProvider rpcProvider;
        try {
            rpcProvider = new EosioJavaRpcProviderImpl(nodeUrl, ENABLE_NETWORK_LOG);
        } catch (EosioJavaRpcProviderInitializerError eosioJavaRpcProviderInitializerError) {
            eosioJavaRpcProviderInitializerError.printStackTrace();
            this.publishProgress(Boolean.toString(false), eosioJavaRpcProviderInitializerError.getMessage());
            return null;
        }

        // Creating ABI provider
        IABIProvider abiProvider = new ABIProviderImpl(rpcProvider, serializationProvider);

        // Creating Signature provider
        ISignatureProvider signatureProvider = new SoftKeySignatureProviderImpl();

        try {
            ((SoftKeySignatureProviderImpl) signatureProvider).importKey(privateKey);
        } catch (ImportKeyError importKeyError) {
            importKeyError.printStackTrace();
            this.publishProgress(Boolean.toString(false), importKeyError.getMessage());
            return null;
        }

        // Creating TransactionProcess
        TransactionSession session = new TransactionSession(serializationProvider, rpcProvider, abiProvider, signatureProvider);
        TransactionProcessor processor = session.getTransactionProcessor();

        // Apply transaction data to Action's data
        String jsonData = "{\n" +
                "\"from\": \"" + fromAccount + "\",\n" +
                "\"to\": \"" + toAccount + "\",\n" +
                "\"quantity\": \"" + amount + "\",\n" +
                "\"memo\" : \"" + memo + "\"\n" +
                "}";

        // Creating action with action's data, eosio.token contract and transfer action.
        Action action = new Action("eosio.token", "transfer", Collections.singletonList(new Authorization(fromAccount, "active")), jsonData);
        try {

            // Prepare transaction with above action. A transaction can be executed with multiple action.
            this.publishProgress("Preparing Transaction...");
            processor.prepare(Collections.singletonList(action));

            // Sign and broadcast the transaction.
            this.publishProgress("Signing and Broadcasting Transaction...");
            SendTransactionResponse response = processor.signAndBroadcast();

            this.publishProgress(Boolean.toString(true), "Finished!  Your transaction id is:  " + response.getTransactionId());
        } catch (TransactionPrepareError transactionPrepareError) {
            // Happens if preparing transaction unsuccessful
            transactionPrepareError.printStackTrace();
            this.publishProgress(Boolean.toString(false), transactionPrepareError.getLocalizedMessage());
        } catch (TransactionSignAndBroadCastError transactionSignAndBroadCastError) {
            // Happens if Sign transaction or broadcast transaction unsuccessful.
            transactionSignAndBroadCastError.printStackTrace();

            // try to get backend error if the error come from backend
            RPCResponseError rpcResponseError = ErrorUtils.getBackendError(transactionSignAndBroadCastError);
            if (rpcResponseError != null) {
                String backendErrorMessage = ErrorUtils.getBackendErrorMessageFromResponse(rpcResponseError);
                this.publishProgress(Boolean.toString(false), backendErrorMessage);
                return null;
            }

            this.publishProgress(Boolean.toString(false), transactionSignAndBroadCastError.getMessage());
        }

        return null;
    }

    public interface TransactionTaskCallback {
        void update(String updateContent);

        void finish(boolean success, String updateContent);
    }
}
