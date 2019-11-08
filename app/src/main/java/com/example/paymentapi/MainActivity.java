package com.example.paymentapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paypal.android.sdk.bm;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import okhttp3.Request;

import static com.example.paymentapi.config.config.PAYPAL_CLIENT_ID;
import static com.paypal.android.sdk.bm.S;
import static com.paypal.android.sdk.bm.s;

public class MainActivity extends AppCompatActivity {
    private static final  int payment_request_code = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
    .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID);
    Button button;
    EditText edtamount;
    String amount ="";

    protected void onDestroy(){
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);


        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        edtamount = (EditText) findViewById(R.id.edtamount);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });
    }
    private void processPayment(){
        amount =  edtamount.getText().toString();
       PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
    "pay now" ,PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,payment_request_code);


    }

    protected  void onActivityResult(int requestCode , int resultCode, Intent data){
    if(requestCode == payment_request_code){
         if (resultCode == RESULT_OK){
             PaymentConfirmation Confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
             if (Confirmation != null){
                 try{
                        String paymentDetails = Confirmation.toJSONObject(). toString(4);

                        startActivity(new Intent(this,PaymentDetails.class)
                        .putExtra("PaymentDetails",paymentDetails)
                                .putExtra("paymentAmount",amount)
                        );
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }

             }
             super.onActivityResult(requestCode,resultCode,data);
         }
         else if(resultCode == Activity.RESULT_CANCELED){
             Toast.makeText(this,"cancel",Toast.LENGTH_SHORT).show();
         }
    }
    else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        Toast.makeText(this,"cancel",Toast.LENGTH_SHORT).show();

    }
}

