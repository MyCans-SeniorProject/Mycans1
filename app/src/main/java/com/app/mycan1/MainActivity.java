package com.app.mycan1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    FirebaseDatabase mDatabase;


    RecyclerView recyclerView;
    TextView noResultsTV;
    FloatingActionButton add_btn;
    ArrayList<PantryItem> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        mDatabase = FirebaseDatabase.getInstance();


        noResultsTV = findViewById(R.id.noResultsTV);
        add_btn = findViewById(R.id.add_btn);

        items = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemsAdapter notificationsAdapter = new ItemsAdapter(items, ctx);
        recyclerView.setAdapter(notificationsAdapter);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ctx, EnterReceiptIdActivity.class));
            }
        });


        dialog.setMessage("Loading...");
        dialog.show();
        getPantryItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_list:{

                Intent i = new Intent(MainActivity.this, ShoppingListActivity.class);
                startActivity(i);

                break;
            }

            case R.id.action_scan:{

                scanItemCode();

                break;
            }
            case R.id.action_family:{

                Intent i = new Intent(MainActivity.this, FamilyMembersActivity.class);
                startActivity(i);

                break;
            }
            case R.id.action_profile:{

                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);

                break;
            }
            case R.id.action_logout:{

                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

                break;
            }

            default:{
                break;
            }
        }


        return super.onOptionsItemSelected(item);
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


    void getPantryItems(){


        DatabaseReference ref = mDatabase.getReference("pantry");
        ref.child(SharedPrefManager.getFamilyId(ctx)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.dismiss();

                if(dataSnapshot.getChildrenCount() == 0){
                    noResultsTV.setVisibility(View.VISIBLE);
                    noResultsTV.setText("Your pantry is empty.");
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

    void read(){
        mDatabase.getReference("invoices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Invoice invoice = postSnapshot.getValue(Invoice.class);
                    Log.e("Invoice", invoice.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error ", ">>>> " + databaseError.getMessage());
            }
        });
    }

    void showToast(String m){
        Toast.makeText(MainActivity.this, m, Toast.LENGTH_SHORT).show();
    }

    public void scanItemCode(){

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setPrompt("Scan Item Code");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                decreaseItemQuantity(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }

    }

    void decreaseItemQuantity(final String itemCode){

        final DatabaseReference ref = mDatabase.getReference("pantry");
        ref.child(SharedPrefManager.getFamilyId(ctx))
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();

                if(dataSnapshot.getChildrenCount() == 0){
                    showToast("Your pantry doesn't contain any items.");
                }
                else{
                    List<PantryItem> items = new ArrayList<>();
                    boolean found = false;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        PantryItem item = snapshot.getValue(PantryItem.class);
                        if(item.getCode().equals(itemCode)){
                            found = true;
                            int newQuantity = item.getQuantity() -1;
                            item.setQuantity(newQuantity);
                            if(item.getThreshold() >= newQuantity){
                               addItemToShoppingList(item);
                            }
                        }

                        items.add(item);
                    }

                    if(found == false)
                        showToast("Your pantry doesn't contain the scanned item.");
                    else{
                        ref.child(SharedPrefManager.getFamilyId(ctx)).setValue(items);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void addItemToShoppingList(final PantryItem item){

        final DatabaseReference ref = mDatabase.getReference("shopping_list");
        ref.child(SharedPrefManager.getFamilyId(ctx)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.getChildrenCount() == 0){
                   // showToast("Shopping list is empty");
                }

                List<PantryItem> shoppingListItems = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    PantryItem item = snapshot.getValue(PantryItem.class);
                    shoppingListItems.add(item);
                }

                for (PantryItem i: shoppingListItems){
                    if(i.getId() == item.getId()){
                        showToast("Item already exists in shopping list.");
                        return;
                    }
                }

                shoppingListItems.add(item);
                ref.child(SharedPrefManager.getFamilyId(ctx)).setValue(shoppingListItems);


                showNotification("Item " + item.getName() + " added to shopping list");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showNotification(String content){

        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.round_notifications_white_24)
                .setContentTitle(getApplicationContext().getString(R.string.app_name)+"\n\n")
                .setTicker(content)
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1000 , notification);

    }
}
