package one.block.androidexampleapp;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TransactionInstrumentedTest {

    @Test
    public void testSubmitTransaction() throws JSONException, InterruptedException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String nodeURL = appContext.getString(R.string.node_url);

        String fromAccount = appContext.getString(R.string.from_account);
        String toAccount = appContext.getString(R.string.to_account);
        String amount = appContext.getString(R.string.amount);
        String memo = appContext.getString(R.string.memo);

        JSONArray privateKeysJSON = new JSONArray(appContext.getString(R.string.private_keys));

        for (int i = 0; i < privateKeysJSON.length(); i++) {
            testSubmitTransactionByMultipleKey(fromAccount, toAccount, amount, memo, privateKeysJSON.getString(i), nodeURL);
            Thread.sleep(1000);
        }
    }

    private void testSubmitTransactionByMultipleKey(
            String fromAccount,
            String toAccount,
            String amount,
            String memo,
            final String privateKey,
            String url) throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);

        new TransactionTask(new TransactionTask.TransactionTaskCallback() {
            @Override
            public void update(String updateContent) {

            }

            @Override
            public void finish(boolean success, String updateContent) {
                System.out.println("Finish Transaction " + updateContent + " - key " + privateKey);
                assertTrue(success);
                signal.countDown();
            }
        }).execute(url, fromAccount, toAccount, privateKey, amount, memo);

        signal.await(5000, TimeUnit.MILLISECONDS);
        assertTrue(true);
    }
}
