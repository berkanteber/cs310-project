package com.example.bumb.miser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class ItemBoughtAdapter extends ArrayAdapter<Item>{
    private BoughtListFragment myFragment;

    public ItemBoughtAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects, BoughtListFragment myFragment) {
        super(context, resource, objects);
        this.myFragment = myFragment;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_layout_bought_list, null);
        }

        if (getItem(position).isAlarmOn() &&
            getItem(position).getPriceChangeRatio() + getItem(position).getAlarmPercentage() < 0.01) {
            rowView.setBackgroundColor(Color.CYAN);
        } else {
            rowView.setBackgroundColor(Color.TRANSPARENT);
        }

        ImageView imageView = rowView.findViewById(R.id.imageView_small);
        byte[] decodedString = Base64.decode(getItem(position).getItemImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);

        //Handle TextView and display string from your list
        TextView textViewItemName = rowView.findViewById(R.id.item_name);
        TextView textViewItemPrice = rowView.findViewById(R.id.item_current_price);

        textViewItemName.setText(getItem(position).getItemName());
        textViewItemPrice.setText(getItem(position).getItemPriceUpdated() + " USD");

        //Handle Calculation of price change ratio
        TextView textViewPriceChangeRatio = rowView.findViewById(R.id.price_change_ratio);

        textViewPriceChangeRatio.setText(getItem(position).getPriceChangeRatio()+"%");

        //Handle the change of ratio sign
        ImageView imageViewChangeRatio = rowView.findViewById(R.id.imageView_change_ratio);

        if (getItem(position).getPriceChangeRatio() < 0){
            imageViewChangeRatio.setImageResource(android.R.drawable.arrow_down_float);
        }
        else if (getItem(position).getPriceChangeRatio() > 0){
            imageViewChangeRatio.setImageResource(android.R.drawable.arrow_up_float);
        }
        else {
            imageViewChangeRatio.setImageResource(android.R.drawable.bottom_bar);
        }

        //On click to a row, there occurs a toast

        LinearLayout rowLayout = (LinearLayout) rowView.findViewById(R.id.LinearLayoutRow_BoughtList);

        rowLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View rowView) {
                // Toast Description
                String text =
                        "Item Name: " + getItem(position).getItemName() + " \n\n" +
                                "Initial Price: " + getItem(position).getItemPrice() + " USD\n" +
                                "Current Price: " + getItem(position).getItemPriceUpdated() + " USD\n" +
                                "Price Change Ratio: " + getItem(position).getPriceChangeRatio() + "%\n\n";

                if (!getItem(position).isAlarmOn()) {
                    text += "Alarm: Off";
                } else {
                    text += "Alarm: " + getItem(position).getAlarmPercentage()  + "%";
                }

                Toast toast = Toast.makeText(((Activity) getContext()), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        //Handle Image button and add favorite items operation

        final ImageButton addFavBtn = (ImageButton) rowView.findViewById(R.id.delete_from_bought_list_button);

        addFavBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View rowView) {
                Database db = ((MainActivity) myFragment.getActivity()).db;

                if (getItem(position).isFavorite() == false) {
                    getItem(position).setFavorite(true);
                    addFavBtn.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    getItem(position).setFavorite(false);
                    addFavBtn.setImageResource(android.R.drawable.btn_star_big_off);
                }

                db.updateItemFav(getItem(position).getItemName(), getItem(position).isFavorite());

                myFragment.myNA.notifyAdapter();
            }
        });

        return rowView;
    }
}
