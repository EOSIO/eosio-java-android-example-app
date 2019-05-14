package one.block.androidexampleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import one.block.eosiojava.error.EosioError;
import one.block.eosiojava.models.rpcProvider.response.Detail;
import one.block.eosiojava.models.rpcProvider.response.RPCResponseError;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderCallError;

public class ErrorUtils {

    /**
     * Recursively look for a specific error inside causes loop of an EosioError
     *
     * @param errorClass - the error class to find
     * @param error      - the error object to search
     * @param <T>        - the generic class which extends from EosioError
     * @return the error which class is specified by input. Return null if could not find the specific class.
     */
    @Nullable
    public static <T extends Exception> T getErrorObject(Class errorClass, Exception error) {
        if (error.getClass() == errorClass) {
            return (T) error;
        }

        if (error.getCause() == null) {
            return null;
        }

        // Recursively look deeper
        return getErrorObject(errorClass, (Exception) error.getCause());
    }

    /**
     * Recursively look for the error message of a specific error inside causes loop of an EosioError
     *
     * @param errorClass - the error class to get the message
     * @param error      - the error object to search
     * @return the error message which class is specified by input. Return the root cause message if could not find the specific class.
     */
    public static String getError(Class errorClass, EosioError error) {
        if (error.getClass() == errorClass || error.getCause() == null) {
            return error.getMessage();
        }

        return getError(errorClass, (EosioError) error.getCause());
    }

    /**
     * Get backend error class {@link RPCResponseError} if an backend error is available
     *
     * @param error the error class to get the backend error
     * @return {@link RPCResponseError} object. Return null if input error does not contain any backend error.
     */
    @Nullable
    public static RPCResponseError getBackendError(EosioError error) {
        EosioJavaRpcProviderCallError rpcError = ErrorUtils.getErrorObject(EosioJavaRpcProviderCallError.class, error);
        if (rpcError != null) {
            return rpcError.getRpcResponseError();
        }

        return null;
    }

    /**
     * Format and return a back end error message from a {@link RPCResponseError} object
     *
     * @param error the backend error
     * @return Formatted backend error message from input
     */
    public static String getBackendErrorMessageFromResponse(@NonNull RPCResponseError error) {
        StringBuilder detail = new StringBuilder();
        if (!error.getError().getDetails().isEmpty()) {
            for (Detail errorDetail : error.getError().getDetails()) {
                detail.append(errorDetail.getMessage()).append(" - ");
            }
        }

        return error.getMessage() + " - Code: " + error.getError().getCode() + " - What " + error.getError().getCode() + " - detail: " + detail.toString();
    }
}
