package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.Invoice;
import com.app.mycan1.model.InvoiceItem;
import com.app.mycan1.model.PantryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InvoiceItemsListActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    FirebaseDatabase mDatabase;


    RecyclerView recyclerView;
    TextView noResultsTV;
    Button edit_btn, delete_btn;
    ArrayList<InvoiceItem> items;
    ArrayList<InvoiceItem> selectedItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_items_list);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        mDatabase = FirebaseDatabase.getInstance();


        noResultsTV = findViewById(R.id.noResultsTV);
        edit_btn = findViewById(R.id.edit_btn);
        delete_btn = findViewById(R.id.delete_btn);

        items = new ArrayList<>();
        selectedItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemsAdapter notificationsAdapter = new ItemsAdapter(items, ctx);
        recyclerView.setAdapter(notificationsAdapter);

        String receiptId = getIntent().getStringExtra("receiptId");


        dialog.setMessage("Loading...");
        dialog.show();
        getInvoiceItems(receiptId);


        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, InvoiceItemsListEditActivity.class);
                i.putParcelableArrayListExtra("items", items);
                startActivity(i);
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(InvoiceItem item: selectedItems){
                    if(items.contains(item)){
                        items.remove(item);
                    }
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_logout){

            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(InvoiceItemsListActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

        private List<InvoiceItem> itemsSet;
        private Context mContext;

        // View holder class whose objects represent each list item

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            Context mContext;

            AppCompatCheckBox checkbox;
            TextView itemNameTV, itemQuantityTV;


            public MyViewHolder(@NonNull View itemView , Context context) {
                super(itemView);
                mContext = context;

                checkbox = itemView.findViewById(R.id.checkbox);
                itemNameTV = itemView.findViewById(R.id.itemNameTV);
                itemQuantityTV = itemView.findViewById(R.id.itemQuantityTV);

                itemView.setOnClickListener(this);
            }

            public void bindData(final InvoiceItem item) {

                itemNameTV.setText(item.getName());
                itemQuantityTV.setText(item.getQuantity()+"");

                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){

                            selectedItems.add(item);

                        }
                        else{
                            selectedItems.remove(item);
                        }

                    }
                });
            }

            @Override
            public void onClick(View v) {

                int pos = getLayoutPosition();


            }
        }

        public ItemsAdapter(List<InvoiceItem> modelList, Context context) {
            itemsSet = modelList;
            mContext = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate out card list item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_invoice_item, parent, false);

            // Return a new view holder

            return new MyViewHolder(view , mContext);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // Bind data for the item at position

            holder.bindData(itemsSet.get(position));
        }

        @Override
        public int getItemCount() {
            // Return the total number of items

            return itemsSet == null ? 0 : itemsSet.size();
        }

    }

    void getInvoiceItems(final String receipt_id){

        DatabaseReference ref = mDatabase.getReference("invoices");
        ref.orderByChild("receiptId").equalTo(receipt_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.dismiss();

                if(dataSnapshot.getChildrenCount() == 0){
                    edit_btn.setVisibility(View.GONE);
                    delete_btn.setVisibility(View.GONE);
                    showToast("Receipt ID doesn't belong to any invoice.");
                }
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Invoice invoice = snapshot.getValue(Invoice.class);
                    items.addAll(invoice.getItems());
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error",databaseError.getMessage());
                dialog.dismiss();
            }
        });


    }


    void showToast(String m){
        Toast.makeText(InvoiceItemsListActivity.this, m, Toast.LENGTH_SHORT).show();
    }
}
