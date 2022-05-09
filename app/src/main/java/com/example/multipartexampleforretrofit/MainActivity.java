package com.example.multipartexampleforretrofit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
Button button,button2;
ImageView imageView;
String currentImagePath=null;
    File imageFile=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        imageView=findViewById(R.id.imageView);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestClient restClient = new RestClient();
                ApiService apiService = restClient.getApiService();
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("FileData", imageFile.getName(), requestFile);

                RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), paraBioMetric("1", "9", "BiometricPhoto", "Yes", "2050", "Functional", "Yes", "No", "26.7834784", "23.67674", "12345", "29", "AV", "VA223345456", "5634"," arrayListImages1"));
                Call<List<JsonObject>> call=apiService.uploadDataInBioMetric(body,description);
                call.enqueue(new Callback<List<JsonObject>>() {
                    @Override
                    public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                        Log.d("TAG", "onResponse: "+response.body()+response);
                    }

                    @Override
                    public void onFailure(Call<List<JsonObject>> call, Throwable t) {

                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Dexter.withContext(MainActivity.this)
                        .withPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()){
                                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (i.resolveActivity(getPackageManager())!=null){

                                    try {
                                        imageFile =getImageFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (imageFile!=null){
                                        Uri imageUri=FileProvider.getUriForFile(MainActivity.this,"com.example.multipartexampleforretrofit.provider",imageFile);
                                        i.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                        startActivityForResult(i,2);
                                    }
                                }

                                }else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    // below line is the title
                                    // for our alert dialog.
                                    builder.setTitle("Need Permissions");

                                    // below line is our message for our dialog
                                    builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
                                    builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // this method is called on click on positive
                                            // button and on clicking shit button we
                                            // are redirecting our user from our app to the
                                            // settings page of our app.
                                            dialog.cancel();
                                            // below is the intent from which we
                                            // are redirecting our user.
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, 101);
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // this method is called when
                                            // user click on negative button.
                                            dialog.cancel();
                                        }
                                    });
                                    // below line is used
                                    // to display our dialog
                                    builder.show();
                                }
                                }


                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }
        private File getImageFile() throws IOException{
        String timeStamp=new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageName="jpg+"+timeStamp+"_";
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile=File.createTempFile(imageName,".jpg",storageDir);

        currentImagePath=imageFile.getAbsolutePath();
            Log.d("TAG", "getImageFile: "+currentImagePath);
            return imageFile;
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: "+resultCode+requestCode);
        if (requestCode==2 && resultCode==Activity.RESULT_OK){

         Bitmap bitmap= BitmapFactory.decodeFile(currentImagePath);
            imageView.setImageBitmap(bitmap);

        }
    }

    private String paraBioMetric(String action, String paramId, String bioMetricDetails, String availabilty, String yearOfInstallation, String workingStatis, String biometricForStaff, String biometricForStudent, String latitude, String longitude, String schoolId, String periodID, String usertypeid, String userid, String noOfMachines, String arrayListImages1) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("Action",action);
        jsonObject.addProperty("ParamId",paramId);
        jsonObject.addProperty("ParamName",bioMetricDetails);
        jsonObject.addProperty("Lat",latitude);
        jsonObject.addProperty("Long",longitude);
        jsonObject.addProperty("SchoolId",schoolId);
        jsonObject.addProperty("PeriodID",periodID);
        jsonObject.addProperty("CreatedBy",usertypeid);
        jsonObject.addProperty("UserCode",userid);
        jsonObject.addProperty("InstallationYear",yearOfInstallation);
        jsonObject.addProperty("WorkingStatus",workingStatis);
        jsonObject.addProperty("BiometricUseStaff",biometricForStaff);
        jsonObject.addProperty("BiometricUseStudent",biometricForStudent);
        jsonObject.addProperty("Availabilty",availabilty);
        jsonObject.addProperty("NoOfMachines",noOfMachines);

//        JsonArray jsonArray = new JsonArray();
//        for (int i = 0; i < arrayListImages1.size(); i++) {
//            jsonArray.add(paraGetImageBase64( arrayListImages1.get(i), i));
//
//        }
//        jsonObject.add("BiometricPhoto", (JsonElement) jsonArray);
        return jsonObject.toString();

    }


}