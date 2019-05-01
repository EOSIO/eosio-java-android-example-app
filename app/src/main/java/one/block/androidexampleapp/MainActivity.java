package one.block.androidexampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnTransfer;
    private Button btnCheckBalance;
    private TextView tvBalanceStatus;
    private List<String> logs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String nodeUrl = this.getString(R.string.node_url);

        this.tvStatus = findViewById(R.id.tv_status);
        this.tvStatus.setMovementMethod(new ScrollingMovementMethod());

        this.btnCheckBalance = findViewById(R.id.btn_check_balance);
        this.btnTransfer = findViewById(R.id.btn_transfer);
        this.tvBalanceStatus = findViewById(R.id.tv_balance);

        this.btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeTransaction(nodeUrl);
            }
        });

        this.btnCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeCheckBalance(nodeUrl);
            }
        });

        findViewById(R.id.btn_clear_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logs.clear();
                tvStatus.setText("");
            }
        });
    }

    private void update() {
        StringBuilder updateContentBuilder = new StringBuilder();
        for (String log : this.logs) {
            updateContentBuilder.append(log);
        }

        this.tvStatus.setText(Html.fromHtml(updateContentBuilder.toString()));
    }

    private void executeTransaction(final String nodeUrl) {
        // Collecting necessary data to send transaction
        final String fromAccount = ((TextInputEditText) this.findViewById(R.id.edt_from_account)).getText().toString();
        final String toAccount = ((TextInputEditText) this.findViewById(R.id.edt_to_account)).getText().toString();
        final String privateKey = ((TextInputEditText) this.findViewById(R.id.edt_private_key)).getText().toString();
        final String amount = ((TextInputEditText) this.findViewById(R.id.edt_amount)).getText().toString();
        final String memo = ((TextInputEditText) this.findViewById(R.id.edt_memo)).getText().toString();

        this.btnTransfer.setEnabled(false);
        new TransactionTask(new TransactionTask.TransactionTaskCallback() {
            @Override
            public void update(String updateContent) {
                logs.add("<p>" + updateContent + "</p>");
                MainActivity.this.update();
            }

            @Override
            public void finish(boolean success, String updateContent) {
                String message = success ? htmlSuccessFormat(updateContent) : htmlErrorFormat(updateContent);
                message += "<p/>";
                logs.add(message);
                MainActivity.this.update();
                btnTransfer.setEnabled(true);

                if (success) {
                    executeCheckBalance(nodeUrl);
                }
            }
        }).execute(nodeUrl, fromAccount, toAccount, privateKey, amount, memo);
    }

    private void executeCheckBalance(String nodeUrl) {
        // Collecting necessary data to check account balance
        final String account = ((EditText) this.findViewById(R.id.edt_from_account)).getText().toString();

        this.btnCheckBalance.setEnabled(false);
        new CheckBalanceTask(new CheckBalanceTask.CheckBalanceTaskCallback() {
            @Override
            public void update(String updateContent) {
                logs.add("<p>" + updateContent + "</p>");
                MainActivity.this.update();
            }

            @Override
            public void finish(boolean success, String updateContent, String balance) {
                String message = success ? htmlSuccessFormat(updateContent) : htmlErrorFormat(updateContent);
                message += "<p/>";
                logs.add(message);
                MainActivity.this.update();
                btnCheckBalance.setEnabled(true);

                if (success) {
                    tvBalanceStatus.setVisibility(View.VISIBLE);
                    tvBalanceStatus.setText(String.format("%s %s", getString(R.string.account_balance), balance));
                } else {
                    tvBalanceStatus.setVisibility(View.GONE);
                }
            }
        }).execute(nodeUrl, account);
    }

    private String htmlErrorFormat(String error) {
        return "<p style='color: #FF6B68;'>" + error + "</p>";
    }

    private String htmlSuccessFormat(String msg) {
        return "<p style='color: #008000;'>" + msg + "</p>";
    }
}
