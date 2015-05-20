package myapps.courier;

import android.os.Parcel;
import android.os.Parcelable;

class Order implements Parcelable {
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        // распаковываем объект из Parcel
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    int index;
    String name, time, phone, address, product, comments = "", completed = "";

    public Order() {
    }

    // конструктор, считывающий данные из Parcel
    private Order(Parcel parcel) {
        index = parcel.readInt();
        name = parcel.readString();
        time = parcel.readString();
        phone = parcel.readString();
        address = parcel.readString();
        product = parcel.readString();
        comments = parcel.readString();
        completed = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(index);
        parcel.writeString(name);
        parcel.writeString(time);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeString(product);
        parcel.writeString(comments);
        parcel.writeString(completed);
    }


}
