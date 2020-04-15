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
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.Invoice;
import com.app.mycan1.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    FirebaseDatabase mDatabase;


    RecyclerView recyclerView;
    TextView noResultsTV;
    Button clear_btn;
    ArrayList<PantryItem> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_items);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        mDatabase = FirebaseDatabase.getInstance();


        noResultsTV = findViewById(R.id.noResultsTV);
        clear_btn = findViewById(R.id.clear_btn);

        items = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemsAdapter notificationsAdapter = new ItemsAdapter(items, ctx);
        recyclerView.setAdapter(notificationsAdapter);


        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearShoppingList();

            }
        });


        dialog.setMessage("Loading...");
        dialog.show();
        getShoppingListItems();

    }


    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

        private List<PantryItem> itemsSet;
        private Context mContext;

        // View holder class whose objects represent each list item

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            Context mContext;

            TextView itemNameTV, itemQuantityTV;


            public MyViewHolder(@NonNull View itemView , Context context) {
                super(itemView);
                mContext = context;

                itemNameTV = itemView.findViewById(R.id.itemNameTV);
                itemQuantityTV = itemView.findViewById(R.id.itemQuantityTV);

                itemView.setOnClickListener(this);
            }

            public void bindData(final PantryItem item) {

                itemNameTV.setText(item.getName());
                itemQuantityTV.setText(item.getQuantity()+"");

                itemQuantityTV.setVisibility(View.GONE);
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
                    .inflate(R.layout.item_pantry_item, parent, false);

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


    void getShoppingListItems(){


        DatabaseReference ref = mDatabase.getReference("shopping_list");
        ref.child(SharedPrefManager.getFamilyId(ctx)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.dismiss();

                if(dataSnapshot.getChildrenCount() == 0){
                    noResultsTV.setVisibility(View.VISIBLE);
                    noResultsTV.setText("Your shopping list is empty.");
                }
                else{
                    noResultsTV.setVisibility(View.GONE);

                    items.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        PantryItem item = snapshot.getValue(PantryItem.class);
                        items.add(item);

                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                noResultsTV.setVisibility(View.VISIBLE);
                noResultsTV.setText("Error getting items.");
                dialog.dismiss();
            }
        });


    }

    void clearShoppingList(){

        DatabaseReference ref = mDatabase.getReference("shopping_list");
        ref.child(SharedPrefManager.getFamilyId(ctx)).setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                items.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                showToast("Shopping list cleared");
            }
        });
    }
    void showToast(String m){
        Toast.makeText(ShoppingListActivity.this, m, Toast.LENGTH_SHORT).show();
    }

}
