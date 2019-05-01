package one.block.androidexampleapp;

import android.os.AsyncTask;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.runner.AndroidJUnit4;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import one.block.eosiojava.error.rpcProvider.GetBlockRpcError;
import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Authorization;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest;
import one.block.eosiojava.models.rpcProvider.request.GetRawAbiRequest;
import one.block.eosiojava.models.rpcProvider.request.GetRequiredKeysRequest;
import one.block.eosiojava.models.rpcProvider.request.PushTransactionRequest;
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse;
import one.block.eosiojava.models.rpcProvider.response.GetInfoResponse;
import one.block.eosiojava.models.rpcProvider.response.GetRawAbiResponse;
import one.block.eosiojava.models.rpcProvider.response.GetRequiredKeysResponse;
import one.block.eosiojava.models.rpcProvider.response.PushTransactionResponse;
import one.block.eosiojava.models.rpcProvider.response.RPCResponseError;
import one.block.eosiojava.models.rpcProvider.response.RpcError;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderCallError;
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EosioRpcProviderInstrumentedTest {

    @Test
    public void getInfoTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(GET_INFO_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);
            GetInfoResponse response = rpcProvider.getInfo();
            assertNotNull(response);
            assertEquals("687fa513e18843ad3e820744f4ffcf93b1354036d80737db8dc444fe4b15ad17",
                    response.getChainId());
            assertEquals("0f6695cb", response.getServerVersion());
            assertEquals("v1.3.0", response.getServerVersionString());
        } catch (Exception ex) {
            fail("Should not get exception when calling getInfo(): " + "\n" + getStackTraceString(ex));
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void getBlockTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(GET_BLOCK_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);
            GetBlockRequest request = new GetBlockRequest("25260032");
            GetBlockResponse response = rpcProvider.getBlock(request);
            assertNotNull(response);
            assertEquals("0181700002e623f2bf291b86a10a5cec4caab4954d4231f31f050f4f86f26116",
                    response.getId());
            assertEquals(new BigInteger("2249927103"), response.getRefBlockPrefix());
            assertEquals("de5493939e3abdca80deeab2fc9389cc43dc1982708653cfe6b225eb788d6659",
                    response.getActionMroot());
        } catch (Exception ex) {
            fail("Should not get exception when calling getBlock(): " + ex.getLocalizedMessage()
                    + "\n" + getStackTraceString(ex));
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void getRawAbiTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(GET_RAW_EOSIO_TOKEN_ABI_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);
            GetRawAbiRequest request = new GetRawAbiRequest("eosio.token");
            GetRawAbiResponse response = rpcProvider.getRawAbi(request);
            assertNotNull(response);
            assertEquals("eosio.token", response.getAccountName());
            assertEquals("43864d5af0fe294d44d19c612036cbe8c098414c4a12a5a7bb0bfe7db1556248", response.getAbiHash());
            assertEquals("DmVvc2lvOjphYmkvMS4wAQxhY2NvdW50X25hbWUEbmFtZQUIdHJhbnNmZXIABARmcm9tDGFjY291bnRfbmFtZQJ0bwxhY2NvdW50X25hbWUIcXVhbnRpdHkFYXNzZXQEbWVtbwZzdHJpbmcGY3JlYXRlAAIGaXNzdWVyDGFjY291bnRfbmFtZQ5tYXhpbXVtX3N1cHBseQVhc3NldAVpc3N1ZQADAnRvDGFjY291bnRfbmFtZQhxdWFudGl0eQVhc3NldARtZW1vBnN0cmluZwdhY2NvdW50AAEHYmFsYW5jZQVhc3NldA5jdXJyZW5jeV9zdGF0cwADBnN1cHBseQVhc3NldAptYXhfc3VwcGx5BWFzc2V0Bmlzc3VlcgxhY2NvdW50X25hbWUDAAAAVy08zc0IdHJhbnNmZXK8By0tLQp0aXRsZTogVG9rZW4gVHJhbnNmZXIKc3VtbWFyeTogVHJhbnNmZXIgdG9rZW5zIGZyb20gb25lIGFjY291bnQgdG8gYW5vdGhlci4KaWNvbjogaHR0cHM6Ly9jZG4udGVzdG5ldC5kZXYuYjFvcHMubmV0L3Rva2VuLXRyYW5zZmVyLnBuZyNjZTUxZWY5ZjllZWNhMzQzNGU4NTUwN2UwZWQ0OWU3NmZmZjEyNjU0MjJiZGVkMDI1NWYzMTk2ZWE1OWM4YjBjCi0tLQoKIyMgVHJhbnNmZXIgVGVybXMgJiBDb25kaXRpb25zCgpJLCB7e2Zyb219fSwgY2VydGlmeSB0aGUgZm9sbG93aW5nIHRvIGJlIHRydWUgdG8gdGhlIGJlc3Qgb2YgbXkga25vd2xlZGdlOgoKMS4gSSBjZXJ0aWZ5IHRoYXQge3txdWFudGl0eX19IGlzIG5vdCB0aGUgcHJvY2VlZHMgb2YgZnJhdWR1bGVudCBvciB2aW9sZW50IGFjdGl2aXRpZXMuCjIuIEkgY2VydGlmeSB0aGF0LCB0byB0aGUgYmVzdCBvZiBteSBrbm93bGVkZ2UsIHt7dG99fSBpcyBub3Qgc3VwcG9ydGluZyBpbml0aWF0aW9uIG9mIHZpb2xlbmNlIGFnYWluc3Qgb3RoZXJzLgozLiBJIGhhdmUgZGlzY2xvc2VkIGFueSBjb250cmFjdHVhbCB0ZXJtcyAmIGNvbmRpdGlvbnMgd2l0aCByZXNwZWN0IHRvIHt7cXVhbnRpdHl9fSB0byB7e3RvfX0uCgpJIHVuZGVyc3RhbmQgdGhhdCBmdW5kcyB0cmFuc2ZlcnMgYXJlIG5vdCByZXZlcnNpYmxlIGFmdGVyIHRoZSB7eyR0cmFuc2FjdGlvbi5kZWxheV9zZWN9fSBzZWNvbmRzIG9yIG90aGVyIGRlbGF5IGFzIGNvbmZpZ3VyZWQgYnkge3tmcm9tfX0ncyBwZXJtaXNzaW9ucy4KCklmIHRoaXMgYWN0aW9uIGZhaWxzIHRvIGJlIGlycmV2ZXJzaWJseSBjb25maXJtZWQgYWZ0ZXIgcmVjZWl2aW5nIGdvb2RzIG9yIHNlcnZpY2VzIGZyb20gJ3t7dG99fScsIEkgYWdyZWUgdG8gZWl0aGVyIHJldHVybiB0aGUgZ29vZHMgb3Igc2VydmljZXMgb3IgcmVzZW5kIHt7cXVhbnRpdHl9fSBpbiBhIHRpbWVseSBtYW5uZXIuAAAAAAClMXYFaXNzdWUAAAAAAKhs1EUGY3JlYXRlAAIAAAA4T00RMgNpNjQBCGN1cnJlbmN5AQZ1aW50NjQHYWNjb3VudAAAAAAAkE3GA2k2NAEIY3VycmVuY3kBBnVpbnQ2NA5jdXJyZW5jeV9zdGF0cwAAAAA==",
                    response.getAbi());
        } catch (Exception ex) {
            fail("Should not get exception when calling getRawAbi(): " + ex.getLocalizedMessage()
                    + "\n" + getStackTraceString(ex));
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void getRequiredKeysTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(GET_REQUIRED_KEYS_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);

            GetRequiredKeysRequest request = new GetRequiredKeysRequest(availableKeys(), transactionForRequiredKeys());
            GetRequiredKeysResponse response = rpcProvider.getRequiredKeys(request);
            assertNotNull(response);
            assertFalse(response.getRequiredKeys().isEmpty());
            assertNotNull(response.getRequiredKeys().get(0));
            assertEquals("EOS5j67P1W2RyBXAL8sNzYcDLox3yLpxyrxgkYy1xsXzVCvzbYpba",
                    response.getRequiredKeys().get(0));
        } catch (Exception ex) {
            fail("Should not get exception when calling getRequiredKeys(): " + ex.getLocalizedMessage()
                    + "\n" + getStackTraceString(ex));
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void pushTransactionTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(PUSH_TRANSACTION_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);

            List<String> signatures = new ArrayList<>();
            signatures.add("SIG_K1_JzFA9ffefWfrTBvpwMwZi81kR6tvHF4mfsRekVXrBjLWWikg9g1FrS9WupYuoGaRew5mJhr4d39tHUjHiNCkxamtEfxi68");
            PushTransactionRequest request = new PushTransactionRequest(signatures,
                    0,
                    "",
                    "C62A4F5C1CEF3D6D71BD000000000290AFC2D800EA3055000000405DA7ADBA0072CBDD956F52ACD910C3C958136D72F8560D1846BC7CF3157F5FBFB72D3001DE4597F4A1FDBECDA6D59C96A43009FC5E5D7B8F639B1269C77CEC718460DCC19CB30100A6823403EA3055000000572D3CCDCD0143864D5AF0FE294D44D19C612036CBE8C098414C4A12A5A7BB0BFE7DB155624800A6823403EA3055000000572D3CCDCD0100AEAA4AC15CFD4500000000A8ED32323B00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C69657320686561767900");
            PushTransactionResponse response = rpcProvider.pushTransaction(request);
            assertNotNull(response);
            assertEquals("ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a",
                    response.getTransactionId());
        } catch (Exception ex) {
            fail("Should not get exception when calling pushTransaction(): " + ex.getLocalizedMessage()
                    + "\n" + getStackTraceString(ex));
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void pushTransactionErrorTest() {

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(500).setBody(PUSH_TRANSACTION_ERROR_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);

            List<String> signatures = new ArrayList<>();
            signatures.add("SIG_K1_JzFA9ffefWfrTBvpwMwZi81kR6tvHF4mfsRekVXrBjLWWikg9g1FrS9WupYuoGaRew5mJhr4d39tHUjHiNCkxamtEfxi68");
            PushTransactionRequest request = new PushTransactionRequest(signatures,
                    0,
                    "",
                    "C62A4F5C1CEF3D6D71BD000000000290AFC2D800EA3055000000405DA7ADBA0072CBDD956F52ACD910C3C958136D72F8560D1846BC7CF3157F5FBFB72D3001DE4597F4A1FDBECDA6D59C96A43009FC5E5D7B8F639B1269C77CEC718460DCC19CB30100A6823403EA3055000000572D3CCDCD0143864D5AF0FE294D44D19C612036CBE8C098414C4A12A5A7BB0BFE7DB155624800A6823403EA3055000000572D3CCDCD0100AEAA4AC15CFD4500000000A8ED32323B00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C69657320686561767900");
            PushTransactionResponse response = rpcProvider.pushTransaction(request);
            fail("Push transaction should not succeed.");
        } catch (Exception ex) {
            assertEquals("Error pushing transaction.", ex.getLocalizedMessage());
            assertNotNull(ex.getCause());
            assertEquals("Bad status code: 500 (Server Error), returned from server. Additional error information: See further error information in RPCProviderError.", ex.getCause().getMessage());
            RPCResponseError rpcResponseError = ((EosioJavaRpcProviderCallError)ex.getCause()).getRpcResponseError();
            assertNotNull(rpcResponseError);
            assertEquals(new BigInteger("500"), rpcResponseError.getCode());
            assertEquals("Internal Service Error", rpcResponseError.getMessage());
            RpcError rpcError = rpcResponseError.getError();
            assertNotNull(rpcError);
            assertEquals(new BigInteger("3040005"), rpcError.getCode());
            assertEquals("Expired Transaction", rpcError.getWhat());
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void getInfoTimeoutTest() {

        MockWebServer server = new MockWebServer();
        MockResponse mockResponse = new MockResponse().setResponseCode(200).setBody(GET_INFO_RESPONSE);
        // This will cause the call to time out.
        mockResponse.setSocketPolicy(SocketPolicy.NO_RESPONSE);
        server.enqueue(mockResponse);

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);
            GetInfoResponse response = rpcProvider.getInfo();
            fail("Should not succeed when calling getInfo().  Should time out.");
        } catch (Exception ex) {
            assertEquals("Error retrieving chain information.", ex.getMessage());
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof SocketTimeoutException);
            assertEquals("timeout", ex.getCause().getMessage());
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    @Test
    public void getBlockAsyncTest() {

        // This test shows how an RPC provider call might be made asynchronously.

        final CountDownLatch testLock = new CountDownLatch(1);

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(GET_BLOCK_RESPONSE));

        try {
            server.start();
            String baseUrl = server.url("/").toString();

            final EosioJavaRpcProviderImpl rpcProvider = new EosioJavaRpcProviderImpl(
                    baseUrl);
            GetBlockRequest[] request = { new GetBlockRequest("25260032") };

            AsyncTask<GetBlockRequest, Void, GetBlockResponse> asyncTask = new AsyncTask<GetBlockRequest, Void, GetBlockResponse>() {
                GetBlockRpcError getBlockError = null;
                @Override
                protected GetBlockResponse doInBackground(GetBlockRequest... getBlockRequests) {
                    // Here we are on a background thread.
                    GetBlockResponse response = null;
                    try {
                        response = rpcProvider.getBlock(getBlockRequests[0]);
                    } catch (GetBlockRpcError err) {
                        getBlockError = err;
                    }
                    return response;
                }

                protected void onPostExecute(GetBlockResponse response) {
                    // Here we are back on the main thread and could update the UI.
                    assertNotNull(response);
                    assertEquals("0181700002e623f2bf291b86a10a5cec4caab4954d4231f31f050f4f86f26116",
                            response.getId());
                    assertEquals(new BigInteger("2249927103"), response.getRefBlockPrefix());
                    assertEquals("de5493939e3abdca80deeab2fc9389cc43dc1982708653cfe6b225eb788d6659",
                            response.getActionMroot());
                    testLock.countDown();
                }
            }.execute(request);

            try {
                testLock.await(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException interruptedException) {
                fail("Interrupted waiting for getBlock() to complete: " +
                        interruptedException.getLocalizedMessage());
            }

        } catch (Exception ex) {
            fail("Should not get exception when calling getBlock(): " + ex.getLocalizedMessage());
        } finally {
            try {
                server.shutdown();
            } catch (Exception ex) {
                // No need for anything here.
            }
        }

    }

    private String getStackTraceString(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private List<String> availableKeys() {

        List<String> availableKeys = new ArrayList<>();
        availableKeys.add("PUB_K1_5j67P1W2RyBXAL8sNzYcDLox3yLpxyrxgkYy1xsXzVCw1oi9eG");
        return availableKeys;

    }

    private Transaction transactionForRequiredKeys() {
        List<Authorization> authList = new ArrayList<>();
        authList.add(new Authorization("cryptkeeper", "active"));

        List<Action> actionList = new ArrayList<>();
        Action action = new Action("eosio.token",
                "transfer",
                authList,
                "00AEAA4AC15CFD4500000060D234CD3DA06806000000000004454F53000000001A746865206772617373686F70706572206C696573206865617679"
                );
        actionList.add(action);

        Transaction transaction = new Transaction(
                "2019-01-25T22:13:55",
                new BigInteger("44503"),
                new BigInteger("1776994640"),
                BigInteger.ZERO,
                BigInteger.ZERO,
                BigInteger.ZERO,
                new ArrayList<Action>(),
                actionList,
                new ArrayList<String>()
        );

        return transaction;

    }

    private static final String GET_INFO_RESPONSE = "{\n"
            + "    \"server_version\": \"0f6695cb\",\n"
            + "    \"chain_id\": \"687fa513e18843ad3e820744f4ffcf93b1354036d80737db8dc444fe4b15ad17\",\n"
            + "    \"head_block_num\": 20583056,\n"
            + "    \"last_irreversible_block_num\": 20583039,\n"
            + "    \"last_irreversible_block_id\": \"013a127fab9a79403a20b55914cdc7e1ac136618387325ad3c1914d27528a1f1\",\n"
            + "    \"head_block_id\": \"013a129048f4486ce8a5ac8380870a8ce1bcbd4ff45b40fd0792503dc44c427d\",\n"
            + "    \"head_block_time\": \"2019-01-25T16:39:38.000\",\n"
            + "    \"head_block_producer\": \"blkproducer1\",\n"
            + "    \"virtual_block_cpu_limit\": 200000000,\n"
            + "    \"virtual_block_net_limit\": 1048576000,\n"
            + "    \"block_cpu_limit\": 199900,\n"
            + "    \"block_net_limit\": 1048576,\n"
            + "    \"server_version_string\": \"v1.3.0\"\n"
            + "}";

    private static final String GET_BLOCK_RESPONSE = "{\n"
            + "    \"timestamp\": \"2019-02-21T18:31:40.000\",\n"
            + "    \"producer\": \"blkproducer2\",\n"
            + "    \"confirmed\": 0,\n"
            + "    \"previous\": \"01816fffa4548475add3c45d0e0620f59468a6817426137b37851c23ccafa9cc\",\n"
            + "    \"transaction_mroot\": \"0000000000000000000000000000000000000000000000000000000000000000\",\n"
            + "    \"action_mroot\": \"de5493939e3abdca80deeab2fc9389cc43dc1982708653cfe6b225eb788d6659\",\n"
            + "    \"schedule_version\": 3,\n"
            + "    \"new_producers\": null,\n"
            + "    \"header_extensions\": [],\n"
            + "    \"producer_signature\": \"SIG_K1_KZ3ptku7orAgcyMzd9FKW4jPC9PvjW9BGadFoyxdJFWM44VZdjW28DJgDe6wkNHAxnpqCWSzaBHB1AfbXBUn3HDzetemoA\",\n"
            + "    \"transactions\": [],\n"
            + "    \"block_extensions\": [],\n"
            + "    \"id\": \"0181700002e623f2bf291b86a10a5cec4caab4954d4231f31f050f4f86f26116\",\n"
            + "    \"block_num\": 25260032,\n"
            + "    \"ref_block_prefix\": 2249927103\n"
            + "}";

    private static final String GET_RAW_EOSIO_TOKEN_ABI_RESPONSE = "{\n"
            + "    \"account_name\": \"eosio.token\",\n"
            + "    \"code_hash\": \"3e0cf4172ab025f9fff5f1db11ee8a34d44779492e1d668ae1dc2d129e865348\",\n"
            + "    \"abi_hash\": \"43864d5af0fe294d44d19c612036cbe8c098414c4a12a5a7bb0bfe7db1556248\",\n"
            + "    \"abi\": \"DmVvc2lvOjphYmkvMS4wAQxhY2NvdW50X25hbWUEbmFtZQUIdHJhbnNmZXIABARmcm9tDGFjY291bnRfbmFtZQJ0bwxhY2NvdW50X25hbWUIcXVhbnRpdHkFYXNzZXQEbWVtbwZzdHJpbmcGY3JlYXRlAAIGaXNzdWVyDGFjY291bnRfbmFtZQ5tYXhpbXVtX3N1cHBseQVhc3NldAVpc3N1ZQADAnRvDGFjY291bnRfbmFtZQhxdWFudGl0eQVhc3NldARtZW1vBnN0cmluZwdhY2NvdW50AAEHYmFsYW5jZQVhc3NldA5jdXJyZW5jeV9zdGF0cwADBnN1cHBseQVhc3NldAptYXhfc3VwcGx5BWFzc2V0Bmlzc3VlcgxhY2NvdW50X25hbWUDAAAAVy08zc0IdHJhbnNmZXK8By0tLQp0aXRsZTogVG9rZW4gVHJhbnNmZXIKc3VtbWFyeTogVHJhbnNmZXIgdG9rZW5zIGZyb20gb25lIGFjY291bnQgdG8gYW5vdGhlci4KaWNvbjogaHR0cHM6Ly9jZG4udGVzdG5ldC5kZXYuYjFvcHMubmV0L3Rva2VuLXRyYW5zZmVyLnBuZyNjZTUxZWY5ZjllZWNhMzQzNGU4NTUwN2UwZWQ0OWU3NmZmZjEyNjU0MjJiZGVkMDI1NWYzMTk2ZWE1OWM4YjBjCi0tLQoKIyMgVHJhbnNmZXIgVGVybXMgJiBDb25kaXRpb25zCgpJLCB7e2Zyb219fSwgY2VydGlmeSB0aGUgZm9sbG93aW5nIHRvIGJlIHRydWUgdG8gdGhlIGJlc3Qgb2YgbXkga25vd2xlZGdlOgoKMS4gSSBjZXJ0aWZ5IHRoYXQge3txdWFudGl0eX19IGlzIG5vdCB0aGUgcHJvY2VlZHMgb2YgZnJhdWR1bGVudCBvciB2aW9sZW50IGFjdGl2aXRpZXMuCjIuIEkgY2VydGlmeSB0aGF0LCB0byB0aGUgYmVzdCBvZiBteSBrbm93bGVkZ2UsIHt7dG99fSBpcyBub3Qgc3VwcG9ydGluZyBpbml0aWF0aW9uIG9mIHZpb2xlbmNlIGFnYWluc3Qgb3RoZXJzLgozLiBJIGhhdmUgZGlzY2xvc2VkIGFueSBjb250cmFjdHVhbCB0ZXJtcyAmIGNvbmRpdGlvbnMgd2l0aCByZXNwZWN0IHRvIHt7cXVhbnRpdHl9fSB0byB7e3RvfX0uCgpJIHVuZGVyc3RhbmQgdGhhdCBmdW5kcyB0cmFuc2ZlcnMgYXJlIG5vdCByZXZlcnNpYmxlIGFmdGVyIHRoZSB7eyR0cmFuc2FjdGlvbi5kZWxheV9zZWN9fSBzZWNvbmRzIG9yIG90aGVyIGRlbGF5IGFzIGNvbmZpZ3VyZWQgYnkge3tmcm9tfX0ncyBwZXJtaXNzaW9ucy4KCklmIHRoaXMgYWN0aW9uIGZhaWxzIHRvIGJlIGlycmV2ZXJzaWJseSBjb25maXJtZWQgYWZ0ZXIgcmVjZWl2aW5nIGdvb2RzIG9yIHNlcnZpY2VzIGZyb20gJ3t7dG99fScsIEkgYWdyZWUgdG8gZWl0aGVyIHJldHVybiB0aGUgZ29vZHMgb3Igc2VydmljZXMgb3IgcmVzZW5kIHt7cXVhbnRpdHl9fSBpbiBhIHRpbWVseSBtYW5uZXIuAAAAAAClMXYFaXNzdWUAAAAAAKhs1EUGY3JlYXRlAAIAAAA4T00RMgNpNjQBCGN1cnJlbmN5AQZ1aW50NjQHYWNjb3VudAAAAAAAkE3GA2k2NAEIY3VycmVuY3kBBnVpbnQ2NA5jdXJyZW5jeV9zdGF0cwAAAAA==\"\n"
            + "}";


    private static final String GET_REQUIRED_KEYS_RESPONSE = "{\n"
            + "    \"required_keys\": [\n"
            + "        \"EOS5j67P1W2RyBXAL8sNzYcDLox3yLpxyrxgkYy1xsXzVCvzbYpba\"\n"
            + "    ]\n"
            + "}";

    private static final String PUSH_TRANSACTION_RESPONSE = "{\n"
            + "    \"transaction_id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "    \"processed\": {\n"
            + "        \"id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "        \"block_num\": 21098575,\n"
            + "        \"block_time\": \"2019-01-28T16:15:37.500\",\n"
            + "        \"producer_block_id\": null,\n"
            + "        \"receipt\": {\n"
            + "            \"status\": \"executed\",\n"
            + "            \"cpu_usage_us\": 3837,\n"
            + "            \"net_usage_words\": 36\n"
            + "        },\n"
            + "        \"elapsed\": 3837,\n"
            + "        \"net_usage\": 288,\n"
            + "        \"scheduled\": false,\n"
            + "        \"action_traces\": [\n"
            + "            {\n"
            + "                \"receipt\": {\n"
            + "                    \"receiver\": \"eosio.assert\",\n"
            + "                    \"act_digest\": \"a4caeedd5e5824dd916c1aaabc84f0a114ddbda83728c8c23ba859d4a8a93721\",\n"
            + "                    \"global_sequence\": 21103875,\n"
            + "                    \"recv_sequence\": 332,\n"
            + "                    \"auth_sequence\": [],\n"
            + "                    \"code_sequence\": 1,\n"
            + "                    \"abi_sequence\": 1\n"
            + "                },\n"
            + "                \"act\": {\n"
            + "                    \"account\": \"eosio.assert\",\n"
            + "                    \"name\": \"require\",\n"
            + "                    \"authorization\": [],\n"
            + "                    \"data\": {\n"
            + "                        \"chain_params_hash\": \"cbdd956f52acd910c3c958136d72f8560d1846bc7cf3157f5fbfb72d3001de45\",\n"
            + "                        \"manifest_id\": \"97f4a1fdbecda6d59c96a43009fc5e5d7b8f639b1269c77cec718460dcc19cb3\",\n"
            + "                        \"actions\": [\n"
            + "                            {\n"
            + "                                \"contract\": \"eosio.token\",\n"
            + "                                \"action\": \"transfer\"\n"
            + "                            }\n"
            + "                        ],\n"
            + "                        \"abi_hashes\": [\n"
            + "                            \"43864d5af0fe294d44d19c612036cbe8c098414c4a12a5a7bb0bfe7db1556248\"\n"
            + "                        ]\n"
            + "                    },\n"
            + "                    \"hex_data\": \"cbdd956f52acd910c3c958136d72f8560d1846bc7cf3157f5fbfb72d3001de4597f4a1fdbecda6d59c96a43009fc5e5d7b8f639b1269c77cec718460dcc19cb30100a6823403ea3055000000572d3ccdcd0143864d5af0fe294d44d19c612036cbe8c098414c4a12a5a7bb0bfe7db1556248\"\n"
            + "                },\n"
            + "                \"context_free\": false,\n"
            + "                \"elapsed\": 1264,\n"
            + "                \"cpu_usage\": 0,\n"
            + "                \"console\": \"\",\n"
            + "                \"total_cpu_usage\": 0,\n"
            + "                \"trx_id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "                \"block_num\": 21098575,\n"
            + "                \"block_time\": \"2019-01-28T16:15:37.500\",\n"
            + "                \"producer_block_id\": null,\n"
            + "                \"account_ram_deltas\": [],\n"
            + "                \"inline_traces\": []\n"
            + "            },\n"
            + "            {\n"
            + "                \"receipt\": {\n"
            + "                    \"receiver\": \"eosio.token\",\n"
            + "                    \"act_digest\": \"9eab239d66d13c34b9cc35a6f79fb2f6d61a2d9df9a484075c82e65d73a0cbc8\",\n"
            + "                    \"global_sequence\": 21103876,\n"
            + "                    \"recv_sequence\": 1366,\n"
            + "                    \"auth_sequence\": [\n"
            + "                        [\n"
            + "                            \"cryptkeeper\",\n"
            + "                            875\n"
            + "                        ]\n"
            + "                    ],\n"
            + "                    \"code_sequence\": 1,\n"
            + "                    \"abi_sequence\": 4\n"
            + "                },\n"
            + "                \"act\": {\n"
            + "                    \"account\": \"eosio.token\",\n"
            + "                    \"name\": \"transfer\",\n"
            + "                    \"authorization\": [\n"
            + "                        {\n"
            + "                            \"actor\": \"cryptkeeper\",\n"
            + "                            \"permission\": \"active\"\n"
            + "                        }\n"
            + "                    ],\n"
            + "                    \"data\": {\n"
            + "                        \"from\": \"cryptkeeper\",\n"
            + "                        \"to\": \"brandon\",\n"
            + "                        \"quantity\": \"42.0000 EOS\",\n"
            + "                        \"memo\": \"the grasshopper lies heavy\"\n"
            + "                    },\n"
            + "                    \"hex_data\": \"00aeaa4ac15cfd4500000060d234cd3da06806000000000004454f53000000001a746865206772617373686f70706572206c696573206865617679\"\n"
            + "                },\n"
            + "                \"context_free\": false,\n"
            + "                \"elapsed\": 2197,\n"
            + "                \"cpu_usage\": 0,\n"
            + "                \"console\": \"\",\n"
            + "                \"total_cpu_usage\": 0,\n"
            + "                \"trx_id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "                \"block_num\": 21098575,\n"
            + "                \"block_time\": \"2019-01-28T16:15:37.500\",\n"
            + "                \"producer_block_id\": null,\n"
            + "                \"account_ram_deltas\": [],\n"
            + "                \"inline_traces\": [\n"
            + "                    {\n"
            + "                        \"receipt\": {\n"
            + "                            \"receiver\": \"cryptkeeper\",\n"
            + "                            \"act_digest\": \"9eab239d66d13c34b9cc35a6f79fb2f6d61a2d9df9a484075c82e65d73a0cbc8\",\n"
            + "                            \"global_sequence\": 21103877,\n"
            + "                            \"recv_sequence\": 496,\n"
            + "                            \"auth_sequence\": [\n"
            + "                                [\n"
            + "                                    \"cryptkeeper\",\n"
            + "                                    876\n"
            + "                                ]\n"
            + "                            ],\n"
            + "                            \"code_sequence\": 1,\n"
            + "                            \"abi_sequence\": 4\n"
            + "                        },\n"
            + "                        \"act\": {\n"
            + "                            \"account\": \"eosio.token\",\n"
            + "                            \"name\": \"transfer\",\n"
            + "                            \"authorization\": [\n"
            + "                                {\n"
            + "                                    \"actor\": \"cryptkeeper\",\n"
            + "                                    \"permission\": \"active\"\n"
            + "                                }\n"
            + "                            ],\n"
            + "                            \"data\": {\n"
            + "                                \"from\": \"cryptkeeper\",\n"
            + "                                \"to\": \"brandon\",\n"
            + "                                \"quantity\": \"42.0000 EOS\",\n"
            + "                                \"memo\": \"the grasshopper lies heavy\"\n"
            + "                            },\n"
            + "                            \"hex_data\": \"00aeaa4ac15cfd4500000060d234cd3da06806000000000004454f53000000001a746865206772617373686f70706572206c696573206865617679\"\n"
            + "                        },\n"
            + "                        \"context_free\": false,\n"
            + "                        \"elapsed\": 6,\n"
            + "                        \"cpu_usage\": 0,\n"
            + "                        \"console\": \"\",\n"
            + "                        \"total_cpu_usage\": 0,\n"
            + "                        \"trx_id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "                        \"block_num\": 21098575,\n"
            + "                        \"block_time\": \"2019-01-28T16:15:37.500\",\n"
            + "                        \"producer_block_id\": null,\n"
            + "                        \"account_ram_deltas\": [],\n"
            + "                        \"inline_traces\": []\n"
            + "                    },\n"
            + "                    {\n"
            + "                        \"receipt\": {\n"
            + "                            \"receiver\": \"brandon\",\n"
            + "                            \"act_digest\": \"9eab239d66d13c34b9cc35a6f79fb2f6d61a2d9df9a484075c82e65d73a0cbc8\",\n"
            + "                            \"global_sequence\": 21103878,\n"
            + "                            \"recv_sequence\": 582,\n"
            + "                            \"auth_sequence\": [\n"
            + "                                [\n"
            + "                                    \"cryptkeeper\",\n"
            + "                                    877\n"
            + "                                ]\n"
            + "                            ],\n"
            + "                            \"code_sequence\": 1,\n"
            + "                            \"abi_sequence\": 4\n"
            + "                        },\n"
            + "                        \"act\": {\n"
            + "                            \"account\": \"eosio.token\",\n"
            + "                            \"name\": \"transfer\",\n"
            + "                            \"authorization\": [\n"
            + "                                {\n"
            + "                                    \"actor\": \"cryptkeeper\",\n"
            + "                                    \"permission\": \"active\"\n"
            + "                                }\n"
            + "                            ],\n"
            + "                            \"data\": {\n"
            + "                                \"from\": \"cryptkeeper\",\n"
            + "                                \"to\": \"brandon\",\n"
            + "                                \"quantity\": \"42.0000 EOS\",\n"
            + "                                \"memo\": \"the grasshopper lies heavy\"\n"
            + "                            },\n"
            + "                            \"hex_data\": \"00aeaa4ac15cfd4500000060d234cd3da06806000000000004454f53000000001a746865206772617373686f70706572206c696573206865617679\"\n"
            + "                        },\n"
            + "                        \"context_free\": false,\n"
            + "                        \"elapsed\": 5,\n"
            + "                        \"cpu_usage\": 0,\n"
            + "                        \"console\": \"\",\n"
            + "                        \"total_cpu_usage\": 0,\n"
            + "                        \"trx_id\": \"ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "                        \"block_num\": 21098575,\n"
            + "                        \"block_time\": \"2019-01-28T16:15:37.500\",\n"
            + "                        \"producer_block_id\": null,\n"
            + "                        \"account_ram_deltas\": [],\n"
            + "                        \"inline_traces\": []\n"
            + "                    }\n"
            + "                ]\n"
            + "            }\n"
            + "        ],\n"
            + "        \"except\": null\n"
            + "    }\n"
            + "}";

    private static final String PUSH_TRANSACTION_ERROR_RESPONSE = "{\n"
            + "    \"code\": 500,\n"
            + "    \"message\": \"Internal Service Error\",\n"
            + "    \"error\": {\n"
            + "        \"code\": 3040005,\n"
            + "        \"name\": \"expired_tx_exception\",\n"
            + "        \"what\": \"Expired Transaction\",\n"
            + "        \"details\": [\n"
            + "            {\n"
            + "                \"message\": \"expired transaction ae735820e26a7b771e1b522186294d7cbba035d0c31ca88237559d6c0a3bf00a\",\n"
            + "                \"file\": \"producer_plugin.cpp\",\n"
            + "                \"line_number\": 378,\n"
            + "                \"method\": \"on_incoming_transaction_async\"\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "}";
}
