package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.mycan1.model.Invoice;
import com.app.mycan1.model.InvoiceItem;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EnterReceiptIdActivity extends AppCompatActivity {

    Context ctx;

    EditText receiptIdTV;
    Button next_btn, fill_btn;
    ProgressDialog dialog;

    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_id);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);

        receiptIdTV = findViewById(R.id.receiptIdTV);
        next_btn = findViewById(R.id.next_btn);
        fill_btn = findViewById(R.id.fill_btn);

        mDatabase = FirebaseDatabase.getInstance();



        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String receiptId = receiptIdTV.getText().toString().trim();
                if(receiptId.isEmpty()){
                    showToast("Enter receipt ID");
                    return;
                }

                Intent i = new Intent(ctx, InvoiceItemsListActivity.class);
                i.putExtra("receiptId", receiptId);
                startActivity(i);


            }
        });

        fill_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInvoices();
            }
        });
    }

    void showToast(String m){
        Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show();
    }


    void addInvoices(){

        List<InvoiceItem> items;

        items = new ArrayList<>();
        items.add(new InvoiceItem(1, "Corn","122333", 5));
        items.add(new InvoiceItem(2, "Pesto Sauce","121212",  6));
        items.add(new InvoiceItem(3, "White Beans","232323",  7));

        Invoice invoice1 = new Invoice();
        invoice1.setReceiptId("123456");
        invoice1.setItems(items);

        Invoice invoice2 = new Invoice();
        invoice2.setReceiptId("122333");
        invoice2.setItems(items);

        Invoice invoice3 = new Invoice();
        invoice3.setReceiptId("112233");
        invoice3.setItems(items);

        mDatabase.getReference("invoices").push().setValue(invoice1);
        mDatabase.getReference("invoices").push().setValue(invoice2);
        mDatabase.getReference("invoices").push().setValue(invoice3);
    }
}
