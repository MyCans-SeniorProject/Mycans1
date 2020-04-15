package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.PantryItem;
import com.app.mycan1.model.User;
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

public class FamilyMembersActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    FirebaseDatabase mDatabase;


    RecyclerView recyclerView;
    TextView noResultsTV;

    ArrayList<User> users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);
        mDatabase = FirebaseDatabase.getInstance();


        noResultsTV = findViewById(R.id.noResultsTV);


        users = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        UsersAdapter notificationsAdapter = new UsersAdapter(users, ctx);
        recyclerView.setAdapter(notificationsAdapter);



        dialog.setMessage("Loading...");
        dialog.show();
        getFamilyMembers();

    }


    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

        private List<User> usersSet;
        private Context mContext;

        // View holder class whose objects represent each list item

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            Context mContext;

            TextView nameTV, phoneTV;


            public MyViewHolder(@NonNull View itemView , Context context) {
                super(itemView);
                mContext = context;

                nameTV = itemView.findViewById(R.id.nameTV);
                phoneTV = itemView.findViewById(R.id.phoneTV);

                itemView.setOnClickListener(this);
            }

            public void bindData(final User user) {

                nameTV.setText(user.getName());
                phoneTV.setText(user.getPhone());

            }

            @Override
            public void onClick(View v) {

                int pos = getLayoutPosition();


            }
        }

        public UsersAdapter(List<User> modelList, Context context) {
            usersSet = modelList;
            mContext = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate out card list item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_family_member, parent, false);

            // Return a new view holder

            return new MyViewHolder(view , mContext);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // Bind data for the item at position

            holder.bindData(usersSet.get(position));
        }

        @Override
        public int getItemCount() {
            // Return the total number of items

            return usersSet == null ? 0 : usersSet.size();
        }

    }


    void getFamilyMembers(){


        DatabaseReference ref = mDatabase.getReference("users");
        ref.orderByChild("familyID").equalTo(SharedPrefManager.getFamilyId(ctx)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.dismiss();

                if(dataSnapshot.getChildrenCount() == 0){
                    noResultsTV.setVisibility(View.VISIBLE);
                    noResultsTV.setText("No family members found.");
                }
                else{
                    noResultsTV.setVisibility(View.GONE);

                    users.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        users.add(user);

                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                noResultsTV.setVisibility(View.VISIBLE);
                noResultsTV.setText("Error.");
                dialog.dismiss();
            }
        });


    }


    void showToast(String m){
        Toast.makeText(FamilyMembersActivity.this, m, Toast.LENGTH_SHORT).show();
    }

}
