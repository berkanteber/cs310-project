package com.example.bumb.miser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class AddItemActivity extends AppCompatActivity {
    RequestQueue queue;

    Switch switchOnOff;
    RadioButton rb5pc, rb10pc, rb15pc, rb20pc;
    Button buttonSave, buttonSubmit;
    TextView tvName, tvUrl, tvPrice;
    ImageView imageView;

    String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        tvName = findViewById(R.id.item_name_editText);
        tvUrl = findViewById(R.id.url_editText);
        tvPrice = findViewById(R.id.item_price_editText);

        buttonSubmit = findViewById(R.id.submit_url_button);

        buttonSave = findViewById(R.id.save_item_button);

        switchOnOff = findViewById(R.id.switch_alarm);
        rb5pc = findViewById(R.id.radioButton1);
        rb10pc = findViewById(R.id.radioButton2);
        rb15pc = findViewById(R.id.radioButton3);
        rb20pc = findViewById(R.id.radioButton4);

        imageView = findViewById(R.id.item_imageView);

        switchOnOff.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        rb5pc.setEnabled(true);
                        rb10pc.setEnabled(true);
                        rb15pc.setEnabled(true);
                        rb20pc.setEnabled(true);
                    } else {
                        rb5pc.setEnabled(false);
                        rb10pc.setEnabled(false);
                        rb15pc.setEnabled(false);
                        rb20pc.setEnabled(false);
                    }
                }
            }
        );


        queue = Volley.newRequestQueue(this);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textURL = tvUrl.getText().toString();
                if (!textURL.substring(0, textURL.lastIndexOf("/") + 1).equals("shop.com/")) {
                    Toast.makeText(getApplicationContext(), "Unsupported URL", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://10.0.2.2:8000/api/" + textURL.substring(textURL.lastIndexOf("/") + 1) + "/all";

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(
                            Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String resName, resPrice, resImage;

                                    try {
                                        resName = response.getString("name");
                                        tvName.setText(resName);

                                        resPrice = String.valueOf(response.getDouble("price"));
                                        tvPrice.setText(resPrice);

                                        resImage = response.getString("image");
                                        byte[] decodedString = Base64.decode(resImage, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        imageView.setImageBitmap(decodedByte);
                                        base64 = resImage;
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), "JSON Exception", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Bad Request", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                    queue.add(jsonRequest);
                }
            }
        });

        buttonSave.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = tvUrl.getText().toString();
                        String name = tvName.getText().toString();
                        double price = Double.valueOf(tvPrice.getText().toString());

                        boolean alarm = switchOnOff.isChecked();

                        int percentage = 0;
                        if (rb5pc.isChecked()) {
                            percentage = 5;
                        } else if (rb10pc.isChecked()) {
                            percentage = 10;
                        } else if (rb15pc.isChecked()) {
                            percentage = 15;
                        } else if (rb20pc.isChecked()) {
                            percentage = 20;
                        }

                        if (price <= 0) {
                            Toast.makeText(getApplicationContext(), "Price must be positive!", Toast.LENGTH_LONG).show();
                        } else if (alarm && percentage == 0) {
                            Toast.makeText(getApplicationContext(), "You must check a percentage", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("image", base64);
                            intent.putExtra("name", name);
                            intent.putExtra("price", price);
                            intent.putExtra("alarm", alarm);
                            intent.putExtra("percentage", percentage);
                            setResult(1, intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Please fill in all the spaces!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        );
}


}


