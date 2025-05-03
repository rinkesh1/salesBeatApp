package com.newsalesbeatApp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.RetailersFeedBackAdapter;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.pojo.ChatItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.FileUtils;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ByteArrayBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.BasicResponseHandler;
//import org.apache.http.impl.client.DefaultHttpClient;

/*
 * Created by Dhirendra Thakur on 03-01-2018.
 */

public class RetailersFeedBack extends AppCompatActivity implements View.OnClickListener {

    String from;
    private String TAG = getClass().getName();
    private Runnable runnable;
    private RetailersFeedBackAdapter adapter;
    private SharedPreferences prefSFA;
    private Uri imageURI, videoURI, contactData, audioUri, docsUri;
    private String id;
    private RecyclerView rvMessageList;
    private UtilityClass utilityClass;
    private SalesBeatDb salesBeatDb;
    private GPSLocation locationProvider;
    private EditText edtFeedBack;
    private ImageView imgBack, imgAttachment;
    private LinearLayout llSend;
    private int SELECT_IMAGE = 100, CAMERA_PIC_REQUEST = 101, SELECT_VIDEO = 102,
            SELECT_AUDIO = 103, SELECT_DOCUMENT = 104, SELECT_LOCATION = 105, SELECT_CONTACT = 106;
    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ClientInterface apiIntentface;
    private Handler handler = new Handler();

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.retailers_feedback_layout);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rvMessageList = (RecyclerView) findViewById(R.id.rvMessageList);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        edtFeedBack = (EditText) findViewById(R.id.edtFeedBack);
        llSend = (LinearLayout) findViewById(R.id.llSend);
        imgAttachment = (ImageView) findViewById(R.id.imgAttachment);
        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);

        utilityClass = new UtilityClass(this);
        locationProvider = new GPSLocation(this);
        //salesBeatDb = new SalesBeatDb(this);
        salesBeatDb = SalesBeatDb.getHelper(this);

        //check gps status if on/off
        locationProvider.checkGpsStatus();


        id = getIntent().getStringExtra("id");
        from = getIntent().getStringExtra("from");

        if (from.equalsIgnoreCase("distributor"))
            tvPageTitle.setText("Distributor Feedback");
        else
            tvPageTitle.setText("Retailer Feedback");


        initView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RetailersFeedBack.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        imgAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cx = (mRevealView.getLeft() + mRevealView.getRight());
                int cy = mRevealView.getBottom();
                int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(700);

                    SupportAnimator animator_reverse = animator.reverse();

                    if (hidden) {

                        mRevealView.setVisibility(View.VISIBLE);
                        animator.start();
                        hidden = false;

                    } else {

                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {

                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;

                            }

                            @Override
                            public void onAnimationCancel() {

                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });

                        animator_reverse.start();
                    }

                } else {

                    if (hidden) {

                        Animator anim = android.view.ViewAnimationUtils.
                                createCircularReveal(mRevealView, cy, cx, 0, radius);
                        mRevealView.setVisibility(View.VISIBLE);
                        anim.start();
                        hidden = false;

                    } else {

                        Animator anim = android.view.ViewAnimationUtils.
                                createCircularReveal(mRevealView, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                        });
                        anim.start();

                    }
                }
            }
        });


        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
                String date = sdff.format(Calendar.getInstance().getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
                String time = sdf.format(Calendar.getInstance().getTime());

                if (!edtFeedBack.getText().toString().isEmpty()) {

                    String msg = edtFeedBack.getText().toString();
                    String chatId = String.valueOf(System.currentTimeMillis());

                    salesBeatDb.insertInTableChatHistory(chatId, msg, date, "",
                            "user", time, "", "", "", "", "",
                            id, prefSFA.getString("employee_name", ""), "fail");

                    edtFeedBack.getText().clear();

                } else {
                    Toast.makeText(RetailersFeedBack.this, "No text input", Toast.LENGTH_SHORT).show();
                }

                ArrayList<ChatItem> chatUpdated = getChatHistory();

                adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
                rvMessageList.setLayoutManager(layoutManager);
                rvMessageList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // Call smooth scroll
                rvMessageList.smoothScrollToPosition(adapter.getItemCount());


            }
        });


        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(RetailersFeedBack.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                chatSync();
            }

        });


    }

    private void chatSync() {

        runnable = new Runnable() {
            @Override
            public void run() {


                ArrayList<ChatItem> chatUpdated = getChatHistory();

                if (adapter == null) {

                    adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
                    rvMessageList.setLayoutManager(layoutManager);
                    rvMessageList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // Call smooth scroll
                    rvMessageList.smoothScrollToPosition(adapter.getItemCount());

                } else if (chatUpdated.size() > adapter.getItemCount()) {

                    adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
                    rvMessageList.setLayoutManager(layoutManager);
                    rvMessageList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    // Call smooth scroll
                    rvMessageList.smoothScrollToPosition(adapter.getItemCount());


                }

                if (from.equalsIgnoreCase("retailer"))
                    uploadRetailerChat();
                else
                    uploadDistributorChat();


                handler.postDelayed(runnable, 500);

            }
        };

        handler.post(runnable);

    }

    public void onDestroy() {
        super.onDestroy();

        try {

            handler.removeCallbacks(runnable);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadRetailerChat() {

        Cursor chatHistory = null;
        try {

            chatHistory = salesBeatDb.getDataFromTableChatHistory2(id);
            if (chatHistory != null && chatHistory.getCount() > 0 && chatHistory.moveToFirst()
                    && utilityClass.isInternetConnected()) {

                do {

                    String path = "";

                    String chatId = chatHistory.getString(chatHistory.getColumnIndex("chat_id"));
                    String chat = chatHistory.getString(chatHistory.getColumnIndex("message"));
                    String date = chatHistory.getString(chatHistory.getColumnIndex("date"));
                    String contact = chatHistory.getString(chatHistory.getColumnIndex("contact"));
                    String channel = chatHistory.getString(chatHistory.getColumnIndex("channel"));
                    String timeStamp = chatHistory.getString(chatHistory.getColumnIndex("time_stamp"));
                    String image = chatHistory.getString(chatHistory.getColumnIndex("image"));
                    String video = chatHistory.getString(chatHistory.getColumnIndex("video"));
                    String audio = chatHistory.getString(chatHistory.getColumnIndex("audio"));
                    String docs = chatHistory.getString(chatHistory.getColumnIndex("docs"));
                    String loc = chatHistory.getString(chatHistory.getColumnIndex("loc"));

                    if (!image.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(image));
                    else if (!video.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(video));
                    else if (!audio.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(audio));
                    else if (!docs.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(docs));
                    else if (!loc.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(loc));

                    // new MyAsynchTask1(chatId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chat, id, path);

                } while (chatHistory.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (chatHistory != null)
                chatHistory.close();
        }

    }


    private void uploadDistributorChat() {

        Cursor chatHistory = null;
        try {

            chatHistory = salesBeatDb.getDataFromTableChatHistory2(id);
            if (chatHistory != null && chatHistory.getCount() > 0 && chatHistory.moveToFirst()
                    && utilityClass.isInternetConnected()) {

                do {

                    String path = "";

                    String chatId = chatHistory.getString(chatHistory.getColumnIndex("chat_id"));
                    String chat = chatHistory.getString(chatHistory.getColumnIndex("message"));
                    String date = chatHistory.getString(chatHistory.getColumnIndex("date"));
                    String contact = chatHistory.getString(chatHistory.getColumnIndex("contact"));
                    String channel = chatHistory.getString(chatHistory.getColumnIndex("channel"));
                    String timeStamp = chatHistory.getString(chatHistory.getColumnIndex("time_stamp"));
                    String image = chatHistory.getString(chatHistory.getColumnIndex("image"));
                    String video = chatHistory.getString(chatHistory.getColumnIndex("video"));
                    String audio = chatHistory.getString(chatHistory.getColumnIndex("audio"));
                    String docs = chatHistory.getString(chatHistory.getColumnIndex("docs"));
                    String loc = chatHistory.getString(chatHistory.getColumnIndex("loc"));

                    if (!image.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(image));
                    else if (!video.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(video));
                    else if (!audio.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(audio));
                    else if (!docs.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(docs));
                    else if (!loc.isEmpty())
                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(loc));

                    //new MyAsynchTask3(chatId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chat, id, path);

                } while (chatHistory.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (chatHistory != null)
                chatHistory.close();
        }

    }

    private ArrayList<ChatItem> getChatHistory() {

        ArrayList<ChatItem> chathistory = new ArrayList<>();
        Cursor chatHistory = null;

        try {

            ///getChatHistoryFromServer(from);

            chatHistory = salesBeatDb.getDataFromTableChatHistory(id);
            if (chatHistory != null && chatHistory.getCount() > 0 && chatHistory.moveToFirst()) {

                do {

                    ChatItem chatItem = new ChatItem();
                    String msg = chatHistory.getString(chatHistory.getColumnIndex("message"));
                    if (msg == null || msg.equalsIgnoreCase("null"))
                        chatItem.setMessage("");
                    else
                        chatItem.setMessage(msg);

                    String date = chatHistory.getString(chatHistory.getColumnIndex("date"));
                    if (date == null || date.equalsIgnoreCase("null"))
                        chatItem.setDate("");
                    else
                        chatItem.setDate(date);

                    String contact = chatHistory.getString(chatHistory.getColumnIndex("contact"));
                    if (contact == null || contact.equalsIgnoreCase("null"))
                        chatItem.setContact("");
                    else
                        chatItem.setContact(contact);

                    chatItem.setChannel(chatHistory.getString(chatHistory.getColumnIndex("channel")));
                    chatItem.setTimeStamp(chatHistory.getString(chatHistory.getColumnIndex("time_stamp")));

                    String img = chatHistory.getString(chatHistory.getColumnIndex("image"));
                    if (img == null || img.equalsIgnoreCase("null"))
                        chatItem.setImage("");
                    else
                        chatItem.setImage(img);

                    String vid = chatHistory.getString(chatHistory.getColumnIndex("video"));
                    if (vid == null || vid.equalsIgnoreCase("null"))
                        chatItem.setVideo("");
                    else
                        chatItem.setVideo(vid);

                    String aud = chatHistory.getString(chatHistory.getColumnIndex("audio"));
                    if (aud == null || aud.equalsIgnoreCase("null"))
                        chatItem.setAudio("");
                    else
                        chatItem.setAudio(aud);

                    String docs = chatHistory.getString(chatHistory.getColumnIndex("docs"));
                    if (docs == null || docs.equalsIgnoreCase("null"))
                        chatItem.setDocs("");
                    else
                        chatItem.setDocs(docs);

                    String loc = chatHistory.getString(chatHistory.getColumnIndex("loc"));
                    if (loc == null || loc.equalsIgnoreCase("null"))
                        chatItem.setLocation("");
                    else
                        chatItem.setLocation(loc);

                    String emp = chatHistory.getString(chatHistory.getColumnIndex("feedbackby"));
                    if (emp == null || emp.equalsIgnoreCase("null"))
                        chatItem.setEmpName("");
                    else
                        chatItem.setEmpName(emp);

                    chatItem.setStatus(chatHistory.getString(chatHistory.getColumnIndex("server_status")));
                    chathistory.add(chatItem);

                } while (chatHistory.moveToNext());

            } else {

                getChatHistoryFromServer(from);
            }

        } catch (Exception e) {

            Log.e(TAG, "===" + e.getMessage());
        } finally {

            if (chatHistory != null)
                chatHistory.close();
        }

        return chathistory;
    }

    private void getDistributorChatHistory() {
    }

    private void getChatHistoryFromServer(String from) {

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(RetailersFeedBack.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                //new MyAsynchTask2(from).execute(id);
            }

        });


    }

    private void initView() {

        ImageButton gallery_btn, photo_btn, document_btn, audio_btn, location_btn, contact_btn;

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.GONE);

        gallery_btn = (ImageButton) findViewById(R.id.gallery_img_btn);
        photo_btn = (ImageButton) findViewById(R.id.photo_img_btn);
        document_btn = (ImageButton) findViewById(R.id.documents_img_btn);
        audio_btn = (ImageButton) findViewById(R.id.audio_img_btn);
        location_btn = (ImageButton) findViewById(R.id.location_img_btn);
        contact_btn = (ImageButton) findViewById(R.id.contact_img_btn);

        gallery_btn.setOnClickListener(this);
        photo_btn.setOnClickListener(this);
        document_btn.setOnClickListener(this);
        audio_btn.setOnClickListener(this);
        location_btn.setOnClickListener(this);
        contact_btn.setOnClickListener(this);

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }


    @Override
    public void onClick(View view) {
        hideRevealView();

        int viewId = view.getId();
        if (viewId == R.id.gallery_img_btn) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        }
        else if (viewId == R.id.photo_img_btn) {
            dispatchTakePictureIntent();
        }
        else if (viewId == R.id.documents_img_btn) {
            Intent documentIntent = new Intent();
            documentIntent.setType("*/*");
            documentIntent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(documentIntent, "Select Picture"), SELECT_DOCUMENT);
        }
        else if (viewId == R.id.audio_img_btn) {
            Intent audioIntent = new Intent();
            audioIntent.setAction(Intent.ACTION_VIEW);
            audioIntent.setType("audio/*");
            audioIntent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(audioIntent, "Select Picture"), SELECT_AUDIO);
        }
        else if (viewId == R.id.location_img_btn) {

        }
        else if (viewId == R.id.contact_img_btn) {
            Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactIntent, SELECT_CONTACT);
        }

//        switch (view.getId()) {

//            case R.id.gallery_img_btn:
//
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);//
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
//
//                break;
//            case R.id.photo_img_btn:
//
////                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//
//                dispatchTakePictureIntent();
//
//                break;
//            case R.id.documents_img_btn:
//
////                Intent videoIntent = new Intent();
////                videoIntent.setAction(Intent.ACTION_VIEW);
////                videoIntent.setType("video/*");
////                videoIntent.setAction(Intent.ACTION_GET_CONTENT);//
////                startActivityForResult(Intent.createChooser(videoIntent, "Select Picture"), SELECT_VIDEO);
//
//                Intent documentIntent = new Intent();
//                documentIntent.setType("*/*");
//                documentIntent.setAction(Intent.ACTION_GET_CONTENT);//
//                startActivityForResult(Intent.createChooser(documentIntent, "Select Picture"), SELECT_DOCUMENT);
//
//                break;
//            case R.id.audio_img_btn:
//
//                Intent audioIntent = new Intent();
//                audioIntent.setAction(Intent.ACTION_VIEW);
//                audioIntent.setType("audio/*");
//                audioIntent.setAction(Intent.ACTION_GET_CONTENT);//
//                startActivityForResult(Intent.createChooser(audioIntent, "Select Picture"), SELECT_AUDIO);
//
//                break;
//            case R.id.location_img_btn:
//
//                break;
//            case R.id.contact_img_btn:
//
//                Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                startActivityForResult(contactIntent, SELECT_CONTACT);
//
//                break;
//        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {

                photoFile = createImageFile();

                // Continue only if the File was successfully created
                if (photoFile != null) {

                    Uri photoURI = FileProvider.getUriForFile(this, "com.newsalesbeat.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
                }

            } catch (IOException ex) {

            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        imageURI = Uri.parse(image.getAbsolutePath());
        return image;
    }

    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                Uri imageURI = data.getData();
                SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
                String date = sdff.format(Calendar.getInstance().getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
                String time = sdf.format(Calendar.getInstance().getTime());

                String chatId = String.valueOf(System.currentTimeMillis());
                salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                        "user", time, String.valueOf(imageURI), "", "", "", "",
                        id, prefSFA.getString("employee_name", ""), "fail");

                ArrayList<ChatItem> chatUpdated = getChatHistory();

                adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
                rvMessageList.setLayoutManager(layoutManager);
                rvMessageList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // Call smooth scroll
                rvMessageList.smoothScrollToPosition(adapter.getItemCount());

            }

        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {


            Log.e("PIMAGE", "===" + imageURI);
            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());

            String chatId = String.valueOf(System.currentTimeMillis());
            salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                    "user", time, String.valueOf(imageURI), "", "", "", "",
                    id, prefSFA.getString("employee_name", ""), "fail");


            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());


        } else if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {

            videoURI = data.getData();
            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());


            String chatId = String.valueOf(System.currentTimeMillis());
            salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                    "user", time, "", String.valueOf(videoURI), "", "", "",
                    id, prefSFA.getString("employee_name", ""), "fail");


            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());

        } else if (requestCode == SELECT_AUDIO && resultCode == Activity.RESULT_OK) {

            audioUri = data.getData();
            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());

            String chatId = String.valueOf(System.currentTimeMillis());
            salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                    "user", time, "", "", String.valueOf(audioUri), "", "",
                    id, prefSFA.getString("employee_name", ""), "fail");

            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());

        } else if (requestCode == SELECT_DOCUMENT && resultCode == Activity.RESULT_OK) {

            docsUri = data.getData();
            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());

            String chatId = String.valueOf(System.currentTimeMillis());
            salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                    "user", time, "", "", "", String.valueOf(docsUri), "",
                    id, prefSFA.getString("employee_name", ""), "fail");

            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());


        } else if (requestCode == SELECT_LOCATION && resultCode == Activity.RESULT_OK) {

            audioUri = data.getData();
            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());

            String chatId = String.valueOf(System.currentTimeMillis());
            salesBeatDb.insertInTableChatHistory(chatId, "", date, "",
                    "user", time, "Image", "", "", "", String.valueOf(audioUri),
                    id, prefSFA.getString("employee_name", ""), "fail");

            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());

        } else if (requestCode == SELECT_CONTACT && resultCode == Activity.RESULT_OK) {


            contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);

            String contact = "";

            if (c.moveToFirst()) {

                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                contact = contact.concat("Name : ");
                contact = contact.concat(name);
                contact = contact.concat("\n");
                contact = contact.concat("Phone : ");
                contact = contact.concat(number);
            }

            SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
            String date = sdff.format(Calendar.getInstance().getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
            String time = sdf.format(Calendar.getInstance().getTime());

            String chatId = String.valueOf(System.currentTimeMillis());

            salesBeatDb.insertInTableChatHistory(chatId, "", date, contact,
                    "user", time, "", "", "", "", "",
                    id, prefSFA.getString("employee_name", ""), "fail");

            ArrayList<ChatItem> chatUpdated = getChatHistory();

            adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
            rvMessageList.setLayoutManager(layoutManager);
            rvMessageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Call smooth scroll
            rvMessageList.smoothScrollToPosition(adapter.getItemCount());

        }

    }

//    private class MyAsynchTask1 extends AsyncTask<String, Void, String> {
//
//        String chatId;
//
//        public MyAsynchTask1(String chatId) {
//
//            this.chatId = chatId;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            ByteArrayBody[] file = new ByteArrayBody[1];
//            InputStream inputStream = null;
//
//            try {
//
//                inputStream = new FileInputStream(params[2]);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//                byte[] buffer = new byte[8192];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    bos.write(buffer, 0, bytesRead);
//                }
//
//                byte[] data = bos.toByteArray();
//                String filename = params[2].substring(params[2].lastIndexOf("/") + 1);
//                file[0] = new ByteArrayBody(data, filename);
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//
//            }
//            // TODO Implement Retrofit
//
//            // Http Client
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost postRequest = new HttpPost(SbAppConstants.API_GET_RETAILERS_FEEDBACK);
//            postRequest.addHeader("authorization", prefSFA.getString("token", ""));
//            postRequest.addHeader("Accept", "application/json");
//            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//            try {
//
//                if (!params[0].isEmpty())
//                    reqEntity.addPart("feedback", new StringBody(params[0]));
//                reqEntity.addPart("rid", new StringBody(params[1]));
//
//                for (int i = 0; i < file.length; i++) {
//                    if (file[i] != null)
//                        reqEntity.addPart("attatchment", file[i]);
//                }
//
//                postRequest.setEntity(reqEntity);
//                HttpResponse response = httpClient.execute(postRequest);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
//                        "UTF-8"));
//
//                String sResponse;
//                StringBuilder s = new StringBuilder();
//
//                while ((sResponse = reader.readLine()) != null) {
//
//                    s = s.append(sResponse);
//
//                }
//
//                Log.e("Response is: ", "===" + s);
//
//                return s.toString();
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return "";
//        }
//
//        protected void onPostExecute(String response) {
//
//            try {
//                //{"status":"success","statusMessage":"success"}
//                JSONObject object = new JSONObject(response);
//                String status = object.getString("status");
//                if (status.contains("success")) {
//                    salesBeatDb.updateInTableChatHistory(chatId);
//
//                    ArrayList<ChatItem> chatUpdated = getChatHistory();
//
//                    adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
//
//                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
//                    rvMessageList.setLayoutManager(layoutManager);
//                    rvMessageList.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//
//                    // Call smooth scroll
//                    rvMessageList.smoothScrollToPosition(adapter.getItemCount());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    private class MyAsynchTask2 extends AsyncTask<String, Void, String> {
//
//        String from;
//
//        public MyAsynchTask2(String from) {
//            this.from = from;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            if (from.equalsIgnoreCase("retailer")) {
//
//                HttpGet getRequest = new HttpGet(SbAppConstants.API_GET_RETAILERS_FEEDBACK + "?rid=" + params[0] + "&page=0");
//                getRequest.addHeader("authorization", "Bearer" + " " + prefSFA.getString("token", ""));
//                getRequest.addHeader("Accept", "application/json");
//
//                try {
//
//                    HttpClient httpClient = new DefaultHttpClient();
//                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                    String sResponse = httpClient.execute(getRequest, responseHandler);
//
//                    Log.e("Response is: ", "===" + sResponse);
//
//                    return sResponse;
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//
//                HttpGet getRequest = new HttpGet(SbAppConstants.API_TO_SUBMIT_FEEDBACK_D + "?did=" + params[0] + "&page=0");
//                getRequest.addHeader("authorization", prefSFA.getString("token", ""));
//                getRequest.addHeader("Accept", "application/json");
//
//                try {
//
//                    HttpClient httpClient = new DefaultHttpClient();
//                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                    String sResponse = httpClient.execute(getRequest, responseHandler);
//
//                    Log.e("Response is: ", "===" + sResponse);
//
//                    return sResponse;
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            return "";
//        }
//
//        protected void onPostExecute(String response) {
//
//            try {
//
//                JSONObject resObj = new JSONObject(response);
//                JSONArray feedbackArr = resObj.getJSONArray("feedback");
//                for (int i = 0; i < feedbackArr.length(); i++) {
//                    JSONObject obj = (JSONObject) feedbackArr.get(i);
//
//                    String msg = obj.getString("feedback");
//                    String file = obj.getString("filename");
//                    if (msg != null || file != null) {
//
//                        String img = "", video = "", audio = "", docs = "";
//                        if (file != null && file.contains(".jpg") || file.contains(".png"))
//                            img = file;
//                        else if (file != null && file.contains(".mp4") || file.contains(".avi"))
//                            video = file;
//                        else if (file != null && file.contains(".mp3") || file.contains(".avi"))
//                            audio = file;
//                        else if (file != null)
//                            docs = file;
//
//                        if (msg == null)
//                            msg = "";
//
//
//                        String date = obj.getString("created_at");
//
//                        String[] dateT = date.split(" ");
//
//                        Log.e("DATETTT", "==" + dateT[0] + "===" + dateT[1]);
//
//                        String empName = "", adminName = "";
//                        if (!obj.isNull("employee") && obj.has("employee")) {
//                            JSONObject emp = obj.getJSONObject("employee");
//                            empName = emp.getString("name");
//                        }
//
//                        if (!obj.isNull("admin") && obj.has("admin")) {
//                            JSONObject emp = obj.getJSONObject("admin");
//                            adminName = emp.getString("name");
//                        }
//
//
//                        String chatId = String.valueOf(System.currentTimeMillis());
//                        if (empName != null && !empName.isEmpty()) {
//                            salesBeatDb.insertInTableChatHistory(chatId, msg, dateT[0], "",
//                                    "user", dateT[1], img, video, audio, docs, "", id, empName, "success");
//                        } else if (adminName != null && !adminName.isEmpty()) {
//                            salesBeatDb.insertInTableChatHistory(chatId, msg, dateT[0], "",
//                                    "admin", dateT[1], img, video, audio, docs, "", id, adminName, "success");
//                        }
//
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//
//    private class MyAsynchTask3 extends AsyncTask<String, Void, String> {
//
//        String chatId;
//
//        public MyAsynchTask3(String chatId) {
//
//            this.chatId = chatId;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            ByteArrayBody[] file = new ByteArrayBody[1];
//            InputStream inputStream = null;
//
//            try {
//
//                inputStream = new FileInputStream(params[2]);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//                byte[] buffer = new byte[8192];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    bos.write(buffer, 0, bytesRead);
//                }
//
//                byte[] data = bos.toByteArray();
//                String filename = params[2].substring(params[2].lastIndexOf("/") + 1);
//                file[0] = new ByteArrayBody(data, filename);
//
//            } catch (Exception e) {
//
//                //e.printStackTrace();
//            }
//
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost postRequest = new HttpPost(SbAppConstants.API_TO_SUBMIT_FEEDBACK_D);
//            postRequest.addHeader("authorization", "Bearer" + " " + prefSFA.getString("token", ""));
//            postRequest.addHeader("Accept", "application/json");
//            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//            try {
//
//                if (!params[0].isEmpty())
//                    reqEntity.addPart("feedback", new StringBody(params[0]));
//                reqEntity.addPart("did", new StringBody(params[1]));
//
//                for (int i = 0; i < file.length; i++) {
//                    if (file[i] != null)
//                        reqEntity.addPart("attatchment", file[i]);
//                }
//
//                postRequest.setEntity(reqEntity);
//                HttpResponse response = httpClient.execute(postRequest);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
//                        "UTF-8"));
//
//                String sResponse;
//                StringBuilder s = new StringBuilder();
//
//                while ((sResponse = reader.readLine()) != null) {
//
//                    s = s.append(sResponse);
//
//                }
//
//                Log.e("Response is: ", "===" + s);
//
//                return s.toString();
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return "";
//        }
//
//        protected void onPostExecute(String response) {
//
//            try {
//                //{"status":"success","statusMessage":"success"}
//                JSONObject object = new JSONObject(response);
//                String status = object.getString("status");
//                if (status.contains("success")) {
//                    salesBeatDb.updateInTableChatHistory(chatId);
//
//                    ArrayList<ChatItem> chatUpdated = getChatHistory();
//
//                    adapter = new RetailersFeedBackAdapter(RetailersFeedBack.this, chatUpdated);
//
//                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailersFeedBack.this);
//                    rvMessageList.setLayoutManager(layoutManager);
//                    rvMessageList.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//
//                    // Call smooth scroll
//                    rvMessageList.smoothScrollToPosition(adapter.getItemCount());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
}