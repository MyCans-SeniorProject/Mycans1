package com.app.mycan1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InvoiceItem implements Parcelable {

    private int id;
    private String name;
    private String code;
    private int quantity;

    public InvoiceItem(){}

    public InvoiceItem(int id, String name, String code, int quantity) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.quantity = quantity;
    }

    protected InvoiceItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InvoiceItem> CREATOR = new Creator<InvoiceItem>() {
        @Override
        public InvoiceItem createFromParcel(Parcel in) {
            return new InvoiceItem(in);
        }

        @Override
        public InvoiceItem[] newArray(int size) {
            return new InvoiceItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
