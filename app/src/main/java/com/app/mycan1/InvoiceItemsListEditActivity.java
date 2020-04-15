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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.Invoice;
import com.app.mycan1.model.InvoiceItem;
import com.app.mycan1.model.PantryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InvoiceItemsListEditActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    FirebaseDatabase mDatabase;


    RecyclerView recyclerView;
    TextView noResultsTV;
    Button done_btn;
    ArrayList<InvoiceItem> invoiceItems;
    ArrayList<PantryItem> pantryItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_items_edit);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        mDatabase = FirebaseDatabase.getInstance();


        noResultsTV = findViewById(R.id.noResultsTV);
        done_btn = findViewById(R.id.done_btn);

        invoiceItems = new ArrayList<>();
        pantryItems = new ArrayList<>();

        invoiceItems = getIntent().getParcelableArrayListExtra("items");
        for(InvoiceItem item: invoiceItems){
            PantryItem pantryItem = new PantryItem();
            pantryItem.setId(item.getId());
            pantryItem.setName(item.getName());
            pantryItem.setCode(item.getCode());
            pantryItem.setQuantity(item.getQuantity());
            pantryItem.setThreshold(1);
            pantryItems.add(pantryItem);
        }


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemsAdapter notificationsAdapter = new ItemsAdapter(pantryItems, ctx);
        recyclerView.setAdapter(notificationsAdapter);


        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addInvoiceItemsToPantry(pantryItems);
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
            Intent i = new Intent(InvoiceItemsListEditActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

        private List<PantryItem> itemsSet;
        private Context mContext;

        // View holder class whose objects represent each list item

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            Context mContext;

            TextView itemNameTV, itemThresholdTV;
            ImageView plus_btn, minus_btn;


            public MyViewHolder(@NonNull View itemView , Context context) {
                super(itemView);
                mContext = context;

                itemNameTV = itemView.findViewById(R.id.itemNameTV);
                itemThresholdTV = itemView.findViewById(R.id.itemThresholdTV);
                plus_btn = itemView.findViewById(R.id.plus_btn);
                minus_btn = itemView.findViewById(R.id.minus_btn);

                itemView.setOnClickListener(this);
            }

            public void bindData(final PantryItem item) {

                itemNameTV.setText(item.getName());
                itemThresholdTV.setText(item.getThreshold()+"");

                plus_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        item.setThreshold(item.getThreshold() + 1);
                        itemThresholdTV.setText(item.getThreshold()+"");

                        pantryItems.get(pantryItems.indexOf(item)).setThreshold(pantryItems.get(pantryItems.indexOf(item)).getThreshold());
                    }
                });

                minus_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(item.getThreshold() == 0){
                            return;
                        }
                        item.setThreshold(item.getThreshold() - 1);
                        itemThresholdTV.setText(item.getThreshold()+"");

                        pantryItems.get(pantryItems.indexOf(item)).setThreshold(pantryItems.get(pantryItems.indexOf(item)).getThreshold());
                    }
                });

            }

            @Override
            public void onClick(View v) {

                int pos = getLayoutPosition();


            }
        }

        public ItemsAdapter(List<PantryItem> modelList, Context context) {
            itemsSet = modelList;
            mContext = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate out card list item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pantry_item_edit, parent, false);

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


    void addInvoiceItemsToPantry(final List<PantryItem> newPantryItems){


        final List<PantryItem> oldPantryItems = new ArrayList<>();

        mDatabase.getReference("pantry").child(SharedPrefManager.getFamilyId(ctx)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.getChildrenCount() == 0){
                    updatePantry(oldPantryItems, newPantryItems);
                }
                else{
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        PantryItem pi = snapshot.getValue(PantryItem.class);
                        oldPantryItems.add(pi);
                    }
                    updatePantry(oldPantryItems, newPantryItems);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error getting old pantry items");
                dialog.dismiss();
            }
        });


    }

    void updatePantry(List<PantryItem> oldPantryItems, List<PantryItem> newPantryItems){

        for(PantryItem item: newPantryItems){
            int index = isItemAlreadyExists(oldPantryItems, item);
            if(index == -1){
                oldPantryItems.add(item);
            }
            else{
                oldPantryItems.get(index).setQuantity(oldPantryItems.get(index).getQuantity() + item.getQuantity());
                oldPantryItems.get(index).setThreshold(item.getThreshold());
            }
        }

        mDatabase.getReference("pantry").child(SharedPrefManager.getFamilyId(ctx)).setValue(oldPantryItems).addOnCompleteListener(InvoiceItemsListEditActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    showToast("Pantry items updated");

                    Intent i = new Intent(ctx, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else{
                    dialog.dismiss();
                    showToast("Error updating pantry");
                }
            }
        });
    }

    int isItemAlreadyExists(List<PantryItem> items, PantryItem item){
        for(int i=0; i<items.size(); i++){
            if(items.get(i).getId() == item.getId())
                return i;
        }
        return -1;
    }

    void showToast(String m){
        Toast.makeText(InvoiceItemsListEditActivity.this, m, Toast.LENGTH_SHORT).show();
    }
}
