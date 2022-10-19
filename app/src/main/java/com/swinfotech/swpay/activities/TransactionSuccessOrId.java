package com.swinfotech.swpay.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.databinding.ActivityTransactionSuccessOrIdBinding;
import com.swinfotech.swpay.model.SearchByOrderIdGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class TransactionSuccessOrId extends AppCompatActivity {

    ActivityTransactionSuccessOrIdBinding binding;
    TransactionSuccessOrId activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionSuccessOrIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        searchOrderId();

//        binding.complain2.setOnClickListener(v -> {
//            startActivity(new Intent(activity, RaiseComplaint.class));
//        });

        binding.share2.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(findViewById(R.id.rootView2)));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            binding.share2.performClick();
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    private Bitmap getMainFrameBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return CropBitmapTransparency(bitmap);
    }

    private Bitmap CropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                if (((sourceBitmap.getPixel(x, y) >> 24) & 255) > 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }


    private boolean CheckPermission() {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    private void shareImage(Bitmap bitmap) {
        try {
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT, ""); // add your coustom msg here
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
            // showMessage("Something Went Wrong...");
        }
    }


    private void searchOrderId() {
        if (getIntent() != null) {
            SearchByOrderIdGetSet data = (SearchByOrderIdGetSet) getIntent().getSerializableExtra("data");
            // check if data not null
            if (data != null) {
                binding.successAmount1.setText(getAmountFormat(data.getAmount()));
                binding.successStatus1.setText(data.getStatus());
                if (data.getStatus().equalsIgnoreCase("FAILED")) {
                    binding.successStatus1.setTextColor(ContextCompat.getColor(this, R.color.red));
                    binding.successImg1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.failed));
                } else {
                    binding.successImg1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.success));
                    binding.successStatus1.setTextColor(ContextCompat.getColor(this, R.color.green));
                }

                binding.successOrId1.setText("id " + data.getTrId());

                binding.successMob1.setText("Phone " + data.getPhNum());
                binding.successOperator1.setText(data.getOpName());
                binding.successDate1.setText(data.getDate());
                binding.successDate1.setText(getDateAndMonth(data.getDate())+" " + getTime(data.getDate()));

                String image = data.getImage();

                // check if image is svg or not
                if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
                    // make uri from url
                    Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + data.getImage());
                    // load image from uri using GlideToVector
                    GlideToVectorYou
                            .init()
                            .with(activity)
                            .withListener(new GlideToVectorYouListener() {
                                @Override
                                public void onLoadFailed() {
                                }

                                @Override
                                public void onResourceReady() {
                                }
                            })
                            .load(uri, binding.successLogo2);

                } else {
                    Glide.with(activity)
                            .load(Constant.COMMISSION_IMAGES_BASE_URL + data.getImage())
                            .into(binding.successLogo2);
                }

            } else {
                Toast.makeText(this, "Data is null please check your code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent is null please check your code", Toast.LENGTH_SHORT).show();
        }
    }




    public static String getDateAndMonth(String fullDate) {
        String day = "", month = "", year = "";
        try {
            // getting day from date
            day = StringUtils.substringBetween(fullDate, "-", "T");
            // split the extra text from date
            day = day.substring(day.lastIndexOf("-") + 1);
            year = fullDate.substring(0, 4);
            String demoMonth = StringUtils.substringBetween(fullDate, "-", "-");
            if (demoMonth.equalsIgnoreCase("01") || demoMonth.equalsIgnoreCase("1")) {
                month = "Jan";
            }
            if (demoMonth.equalsIgnoreCase("02") || demoMonth.equalsIgnoreCase("2")) {
                month = "Feb";
            }
            if (demoMonth.equalsIgnoreCase("03") || demoMonth.equalsIgnoreCase("3")) {
                month = "March";
            }
            if (demoMonth.equalsIgnoreCase("04") || demoMonth.equalsIgnoreCase("4")) {
                month = "April";
            }
            if (demoMonth.equalsIgnoreCase("05") || demoMonth.equalsIgnoreCase("5")) {
                month = "May";
            }
            if (demoMonth.equalsIgnoreCase("06") || demoMonth.equalsIgnoreCase("6")) {
                month = "June";
            }
            if (demoMonth.equalsIgnoreCase("07") || demoMonth.equalsIgnoreCase("7")) {
                month = "July";
            }
            if (demoMonth.equalsIgnoreCase("08") || demoMonth.equalsIgnoreCase("8")) {
                month = "August";
            }
            if (demoMonth.equalsIgnoreCase("09") || demoMonth.equalsIgnoreCase("9")) {
                month = "September";
            }
            if (demoMonth.equalsIgnoreCase("10")) {
                month = "October";
            }
            if (demoMonth.equalsIgnoreCase("11")) {
                month = "November";
            }
            if (demoMonth.equalsIgnoreCase("12")) {
                month = "December";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getDateAndMonth: " + day + " month " + month + " year " + year);
        return day + " " + month + " " + year;
    }

    public String getTime(String startTime) {
        // get the date from time
        String date1 = StringUtils.substringBetween(startTime, "", "T");
        // print the value you can see the actual result
        Log.e("DATE", "onBindViewHolder: " + " date " + date1 + " " + startTime + " time ");
        // get the time from date
        startTime = StringUtils.substringBetween(startTime, "T", ".");
        Log.e("DATE", "onBindViewHolder: " + startTime + " after time ");
        try {
            StringTokenizer tk = new StringTokenizer(date1 + " " + startTime);
            String date = tk.nextToken();
            String time = tk.nextToken();
            Log.e("DATE", "onBindViewHolder: " + date + " time " + time);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
            Date dt = new Date();
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt));
            return sdfs.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getAmountFormat(String data) {
        if (data == null) {
            return "0.0";
        }
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }


}