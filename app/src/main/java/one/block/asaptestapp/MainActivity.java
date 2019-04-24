package one.block.asaptestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String nodeUrl = this.getString(R.string.node_url);
        final String fromAccount = this.getString(R.string.from_account);
        final String toAccount = this.getString(R.string.to_account);
        final String privateKey = this.getString(R.string.from_account_private_key);
        final String amount = this.getString(R.string.amount);
        final String memo = this.getString(R.string.memo);

        findViewById(R.id.btn_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeTransaction(nodeUrl, fromAccount, toAccount, privateKey, amount, memo);
                findViewById(R.id.btn_push).setEnabled(false);
            }
        });

        this.tvStatus = findViewById(R.id.tv_status);
    }

    private void update(final String updateContent) {
        tvStatus.setText("Update: " + updateContent);
    }

    private void executeTransaction(String nodeUrl, String fromAccount, String toAccount, String privateKey, String amount, String memo) {
        new TransactionTask(new TransactionTask.TransactionCallback() {
            @Override
            public void update(String updateContent) {
                MainActivity.this.update(updateContent);
            }

            @Override
            public void finish(boolean success, String updateContent) {
                MainActivity.this.update(updateContent);
                findViewById(R.id.btn_push).setEnabled(true);
            }
        }).execute(nodeUrl, fromAccount, toAccount, privateKey, amount, memo);
    }
}
