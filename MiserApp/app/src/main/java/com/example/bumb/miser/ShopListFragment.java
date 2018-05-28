package com.example.bumb.miser;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ShopListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ShopListFragment";

    private ListView listView_ShopList;
    private List<Item> itemShopList;

    private ItemShopAdapter itemShopAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_list, container,false);

        // Define the list
        itemShopList = MainActivity.itemShopList;

        //Find the list view inside the fragment
        listView_ShopList = (ListView) view.findViewById(R.id.list_view_shop_list);

        //Create the Array adapter
        itemShopAdapter = new ItemShopAdapter(getContext(), R.layout.row_layout_shop_list, itemShopList, this);

        //Set the adapter for listview
        listView_ShopList.setAdapter(itemShopAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_shoplist);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    public NotifyAdapterInterface myNA;

    public interface NotifyAdapterInterface {
        void notifyAdapter();
    }

    public void notifyAdapter() {
        if (itemShopAdapter != null) {
            itemShopAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            myNA = (NotifyAdapterInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onRefresh(){
        refreshList();
    }

    public void refreshList() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());;
        for (final Item item : ((MainActivity) getActivity()).itemShopList) {
            String url = "http://10.0.2.2:8000/api/" + item.getItemURL().substring(item.getItemURL().lastIndexOf("/") + 1) + "/price";

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Double resPrice;

                            try {
                                resPrice = response.getDouble("price");
                                ((MainActivity) getActivity()).db.updateItemPrice(item.getItemName(), resPrice);
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "JSON Exception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            queue.add(jsonRequest);
        }

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request)
            {
                myNA.notifyAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
