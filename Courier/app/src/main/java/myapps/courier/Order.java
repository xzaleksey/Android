package myapps.courier;

import android.os.Parcel;
import android.os.Parcelable;

class Order implements Parcelable {
    int index;
    String name,time,phone,address,product,comments;

    public Order() {
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
    }
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        // распаковываем объект из Parcel
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Order(Parcel parcel) {
        index = parcel.readInt();
        name = parcel.readString();
        time = parcel.readString();
        phone = parcel.readString();
        address = parcel.readString();
        product = parcel.readString();
        comments = parcel.readString();

    }


}
