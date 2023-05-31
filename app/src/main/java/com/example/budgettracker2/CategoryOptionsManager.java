package com.example.budgettracker2;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.budgettracker2.Model.AccountsList;
import com.example.budgettracker2.Model.CategoryList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryOptionsManager {
    private static CategoryOptionsManager manager = new CategoryOptionsManager();

    ArrayList<CategoryList> mCategoryList;
    ArrayList<AccountsList> mListAccounts;
    private CategoryOptionsManager() {
        mCategoryList = null;
        mListAccounts = null;
    }

    public static CategoryOptionsManager getInstance(){
        return manager;
    }

    //getter and setters


    public ArrayList<CategoryList> getCategoryList() {
        return mCategoryList;
    }

    public void setCategoryList(ArrayList<CategoryList> categoryLists) {
        this.mCategoryList = categoryLists;
    }

    public ArrayList<AccountsList> getListAccounts() {
        return mListAccounts;
    }

    public void setListAccounts(ArrayList<AccountsList> listAccounts) {
        this.mListAccounts = listAccounts;
    }

    public void requestFetchAccount(ManagerCallback callback) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("AccountsCategory");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListAccounts = new ArrayList<>();
                Gson gson = new Gson();
                Set<String> accountNamesSet = new HashSet<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String values = gson.toJson(dataSnapshot.getValue());
                    // Parse the string as a JSON object
                    Map<String, Object> data = gson.fromJson(values, Map.class);
                    // Extract the value of "accountsName"
                    String accountName = (String) data.get("accountsName");
                    String uniqueKey = dataSnapshot.getKey();

                    AccountsList accountsList = dataSnapshot.getValue(AccountsList.class);
                    if(accountsList != null) {
                        accountsList.setId(uniqueKey);
                        accountsList.setAccountName(accountName);

                        // Check if the account name already exists in the set
                        if (!accountNamesSet.contains(accountName)) {
                            // Account name is not in the set, add the account to the list
                            mListAccounts.add(accountsList);
                            accountNamesSet.add(accountName);
                        }
                    }
                }
                if(callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(callback != null) {
                    callback.onError(error.getMessage());
                }
            }
        });
    }

    public void requestFetchCategory(ManagerCallback callback) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Category");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCategoryList = new ArrayList<>();
                Gson gson = new Gson();
                Set<String> accountNamesSet = new HashSet<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String values = gson.toJson(dataSnapshot.getValue());
                    // Parse the string as a JSON object
                    Map<String, Object> data = gson.fromJson(values, Map.class);
                    // Extract the value of "accountsName"
                    String categoryName = (String) data.get("categoryName");
                    String uniqueKey = dataSnapshot.getKey();

                    CategoryList categoryList = dataSnapshot.getValue(CategoryList.class);
                    if (categoryList != null) {
                        categoryList.setId(uniqueKey);
                        categoryList.setCategoryName(categoryName);

                        // Check if the account name already exists in the set
                        if (!accountNamesSet.contains(categoryName)) {
                            // Account name is not in the set, add the account to the list
                            mCategoryList.add(categoryList);
                            accountNamesSet.add(categoryName);
                        }
                    }
                }

                if (callback != null) {
                    callback.onFinish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(callback != null) {
                    callback.onError(error.getMessage());
                }
            }
        });
    }
}
