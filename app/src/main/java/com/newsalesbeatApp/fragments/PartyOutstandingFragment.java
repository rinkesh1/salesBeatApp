package com.newsalesbeatApp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.CommentListAdapter;
import com.newsalesbeatApp.adapters.RetailersFeedBackAdapter;
import com.newsalesbeatApp.pojo.ChatItem;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ByteArrayBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;

/*
 * Created by abc on 1/4/19.
 */

public class PartyOutstandingFragment extends Fragment {

    String id = "";
    boolean isShowing;
    private SharedPreferences prefSFA;
    private boolean isSynced = false;
    private RecyclerView rvPartyMessageList, rvCommentList;
    private EditText edtPartyFeedback;
    private LinearLayout llSend;
    private SalesBeatDb salesBeatDb;
    private Handler handler;
    private Runnable runnable;
    private RetailersFeedBackAdapter adapter;
    private UtilityClass utilityClass;
    private ImageView imgShowHide;
    private RelativeLayout llComment, rlPartyOutStandingFeedback;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View partyOutstandingDialog = inflater.inflate(R.layout.party_outstanding_dialog, parent, false);
        prefSFA = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        TextView tvCreditDays = partyOutstandingDialog.findViewById(R.id.tvCreditDays);
        TextView tvCreditLimit = partyOutstandingDialog.findViewById(R.id.tvCreditLimit);
        TextView tvBillDate = partyOutstandingDialog.findViewById(R.id.tvBillDate);
        TextView tvBillNumber = partyOutstandingDialog.findViewById(R.id.tvBillNumber);
        TextView tvBillAmt = partyOutstandingDialog.findViewById(R.id.tvBillAmount);
        TextView tvDueDays = partyOutstandingDialog.findViewById(R.id.tvDueDays);
        TextView tvPendingAmt = partyOutstandingDialog.findViewById(R.id.tvPendingAmt);
        TextView tvLastUpdated = partyOutstandingDialog.findViewById(R.id.tvLastUpdated);
        rvPartyMessageList = partyOutstandingDialog.findViewById(R.id.rvPartyMessageList);
        rvCommentList = partyOutstandingDialog.findViewById(R.id.rvCommentList);
        edtPartyFeedback = partyOutstandingDialog.findViewById(R.id.edtPartyFeedBack);
        llSend = partyOutstandingDialog.findViewById(R.id.llSend);
        llComment = partyOutstandingDialog.findViewById(R.id.llComment);
        rlPartyOutStandingFeedback = partyOutstandingDialog.findViewById(R.id.rlPartyOutStandingFeedback);
        imgShowHide = partyOutstandingDialog.findViewById(R.id.imgShowHide);

        Bundle arg = getArguments();
        handler = new Handler();
        utilityClass = new UtilityClass(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        isShowing = false;

        if (arg != null) {

            Item item = (Item) arg.getSerializable("itemVal");

            String credit_days = item.getItem1();
            String credit_limit = item.getItem2();
            String bill_date = item.getItem3();
            String bill_number = item.getItem4();
            String bill_amount = item.getItem5();
            String due_days = item.getItem6();
            String pending_amount = item.getItem7();
            String updated_at = item.getItem8();
            id = item.getItemId();
            String cmnt = item.getItemData();

            tvCreditDays.setText(credit_days);
            tvCreditLimit.setText(credit_limit);
            tvBillDate.setText(bill_date);
            tvBillNumber.setText(bill_number);
            tvBillAmt.setText(bill_amount);
            tvDueDays.setText(due_days + " days");
            tvPendingAmt.setText(pending_amount);
            tvLastUpdated.setText(updated_at);

            salesBeatDb.deleteAllChat(id);

            try {

                JSONArray comments = new JSONArray(cmnt);
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject obj = (JSONObject) comments.get(i);

                    String chatId = String.valueOf(System.currentTimeMillis());
                    String outstandingId = obj.getString("outstanding_id");
                    String msg = obj.getString("comment");
//                    String adminId = obj.getString("aid");
//                    String eid = obj.getString("eid");
//
//                    String from = "";
//                    if (adminId == null || adminId.equalsIgnoreCase("null")) {
//                        if (eid != null && !eid.equalsIgnoreCase("null"))
//                            from = "user";
//                    } else {
//                        from = "admin";
//                    }

                    String takenAt = obj.getString("takenAt");
                    String dateArr[] = takenAt.split(" ");
                    String dateInput = dateArr[0];
                    String timeInput = dateArr[1];

                    String empName = "", from = "";
                    if (!obj.isNull("employee") && obj.has("employee")) {
                        JSONObject emp = obj.getJSONObject("employee");
                        empName = emp.getString("name");
                        from = "user";
                    }

                    if (!obj.isNull("admin") && obj.has("admin")) {
                        JSONObject emp = obj.getJSONObject("admin");
                        empName = emp.getString("name");
                        from = "admin";
                    }


                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    //Date/time pattern of desired output date
                    SimpleDateFormat outputformat = new SimpleDateFormat("dd MMMM yyyy");

                    Date date = null;
                    String output = null;

                    try {
                        //Conversion of input String to date
                        date = df.parse(dateInput);
                        //old date format to new date format
                        output = outputformat.format(date);

                    } catch (ParseException pe) {
                        pe.printStackTrace();
                        return null;
                    }

                    String time12F = "";
                    try {

                        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                        Date _24HourDt = _24HourSDF.parse(timeInput);
                        time12F = _12HourSDF.format(_24HourDt);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e("#######", " " + output + "  " + time12F + " " + msg);

                    salesBeatDb.insertInTableChatHistory(chatId, msg, output, "",
                            from, time12F, "", "", "", "", "",
                            outstandingId, empName, "success");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            chatSync();

        }


        imgShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isShowing) {
                    expand(rlPartyOutStandingFeedback);
                    rotateImage(0, 180, imgShowHide);
                    isShowing = true;
                } else {
                    collapse(rlPartyOutStandingFeedback);
                    rotateImage(180, 360, imgShowHide);
                    isShowing = false;
                }
            }
        });


        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdff = new SimpleDateFormat("dd MMMM yyyy");
                String date = sdff.format(Calendar.getInstance().getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(Calendar.getInstance().getTime());

                if (!edtPartyFeedback.getText().toString().isEmpty()) {

                    String msg = edtPartyFeedback.getText().toString();
                    String chatId = String.valueOf(System.currentTimeMillis());

                    salesBeatDb.insertInTableChatHistory(chatId, msg, date, "",
                            "user", time, "", "", "", "", "",
                            id, prefSFA.getString("employee_name", ""), "fail");

                    edtPartyFeedback.getText().clear();

                } else {
                    Toast.makeText(getContext(), "No text input", Toast.LENGTH_SHORT).show();
                }

                ArrayList<ChatItem> chatUpdated = getChatHistory();

                adapter = new RetailersFeedBackAdapter(getContext(), chatUpdated);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvPartyMessageList.setLayoutManager(layoutManager);
                rvPartyMessageList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // Call smooth scroll
                rvPartyMessageList.smoothScrollToPosition(adapter.getItemCount());


            }
        });

        return partyOutstandingDialog;
    }

    private void rotateImage(int init, int degree, ImageView imgDropDown) {

        RotateAnimation rotate =
                new RotateAnimation(init, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);

        imgDropDown.startAnimation(rotate);

    }


    private void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int targetHeight = 0;
        if (Build.VERSION.SDK_INT < 21)
            targetHeight = v.getHeight();
        else
            targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        final int finalTargetHeight = targetHeight;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.MATCH_PARENT
                        : (int) (finalTargetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    private void chatSync() {

        ArrayList<ChatItem> chatUpdated1 = getChatHistory();
        if (chatUpdated1 != null && chatUpdated1.size() > 0) {

            CommentListAdapter adapter = new CommentListAdapter(getContext(), chatUpdated1);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvCommentList.setLayoutManager(layoutManager);
            rvCommentList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


        runnable = new Runnable() {
            @Override
            public void run() {


                ArrayList<ChatItem> chatUpdated = getChatHistory();

                if (adapter == null) {

                    adapter = new RetailersFeedBackAdapter(getContext(), chatUpdated);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rvPartyMessageList.setLayoutManager(layoutManager);
                    rvPartyMessageList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // Call smooth scroll
                    rvPartyMessageList.smoothScrollToPosition(adapter.getItemCount());

                } else if (chatUpdated.size() > adapter.getItemCount()) {

                    adapter = new RetailersFeedBackAdapter(getContext(), chatUpdated);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rvPartyMessageList.setLayoutManager(layoutManager);
                    rvPartyMessageList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    // Call smooth scroll
                    rvPartyMessageList.smoothScrollToPosition(adapter.getItemCount());

                }

                if (!isSynced)
                    uploadChat();

                handler.postDelayed(runnable, 500);

            }
        };

        handler.post(runnable);

    }

    private void uploadChat() {

        isSynced = true;
        Cursor chatHistory = null;
        try {

            chatHistory = salesBeatDb.getDataFromTableChatHistory2(id);
            if (chatHistory != null && chatHistory.getCount() > 0 && chatHistory.moveToFirst()
                    && utilityClass.isInternetConnected()) {

//                do {

                String path = "";

                String chatId = chatHistory.getString(chatHistory.getColumnIndex("chat_id"));
                String chat = chatHistory.getString(chatHistory.getColumnIndex("message"));
//                    String date = chatHistory.getString(chatHistory.getColumnIndex("date"));
//                    String contact = chatHistory.getString(chatHistory.getColumnIndex("contact"));
//                    String channel = chatHistory.getString(chatHistory.getColumnIndex("channel"));
//                    String timeStamp = chatHistory.getString(chatHistory.getColumnIndex("time_stamp"));
//                    String image = chatHistory.getString(chatHistory.getColumnIndex("image"));
//                    String video = chatHistory.getString(chatHistory.getColumnIndex("video"));
//                    String audio = chatHistory.getString(chatHistory.getColumnIndex("audio"));
//                    String docs = chatHistory.getString(chatHistory.getColumnIndex("docs"));
//                    String loc = chatHistory.getString(chatHistory.getColumnIndex("loc"));

//                    if (!image.isEmpty())
//                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(image));
//                    else if (!video.isEmpty())
//                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(video));
//                    else if (!audio.isEmpty())
//                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(audio));
//                    else if (!docs.isEmpty())
//                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(docs));
//                    else if (!loc.isEmpty())
//                        path = FileUtils.getPath(RetailersFeedBack.this, Uri.parse(loc));

                new SynchChat(chatId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chat, id, path);

//                } while (chatHistory.moveToNext());

            } else {

                isSynced = false;
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

                // getChatHistoryFromServer(from);
            }

        } catch (Exception e) {

            //Log.e(TAG, "===" + e.getMessage());
        } finally {

            if (chatHistory != null)
                chatHistory.close();
        }

        return chathistory;
    }

    private class SynchChat extends AsyncTask<String, Void, String> {

        String chatId;

        String id, comment;

        public SynchChat(String chatId) {

            this.chatId = chatId;
        }

        @Override
        protected String doInBackground(String... params) {

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


            id = params[1];
            comment = params[0];

            // Http Client
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost postRequest = new HttpPost(SbAppConstants.API_SUBMIT_OUTSTANDING_FEEDBACK + "/" + params[1] + "/comment");
//            postRequest.addHeader("authorization", "Bearer" + " " + prefSFA.getString("token", ""));
//            postRequest.addHeader("Accept", "application/json");
//            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

//            try {
//
//                if (!params[0].isEmpty()) {
//                    reqEntity.addPart("comment", new StringBody(params[0]));
//
//                    DateFormat df = utilityClass.getTimeStampFormat();
//                    String output;
//
//                    try {
//
//                        long milliSeconds = Long.parseLong(chatId);
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(milliSeconds);
//                        //old date format to new date format
//                        output = df.format(calendar.getTime());
//
//                    } catch (Exception pe) {
//                        pe.printStackTrace();
//                        return null;
//                    }
//
//                    Log.e("*****", "Output date: " + output);
//
//                    reqEntity.addPart("takenAt", new StringBody(output));
//                }
//
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
//                Log.e("Response is: ", "Comment: === " + s);
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

            return "";
        }

        protected void onPostExecute(String response) {

            StringRequest partyOutstandingReq = new StringRequest(Request.Method.POST,
                    SbAppConstants.API_SUBMIT_OUTSTANDING_FEEDBACK + "/" + id + "/comment",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //{"status":"success","statusMessage":"success"}
                                JSONObject object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.contains("success")) {
                                    salesBeatDb.updateInTableChatHistory(chatId);

                                    ArrayList<ChatItem> chatUpdated = getChatHistory();

                                    adapter = new RetailersFeedBackAdapter(getContext(), chatUpdated);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    rvPartyMessageList.setLayoutManager(layoutManager);
                                    rvPartyMessageList.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    // Call smooth scroll
                                    rvPartyMessageList.smoothScrollToPosition(adapter.getItemCount());
                                    isSynced = false;
                                }

                            } catch (Exception e) {

                                isSynced = false;
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", prefSFA.getString("token", ""));
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    HashMap<String, String> params2 = new HashMap<String, String>();
                    params2.put("comment", comment);
                    return new JSONObject(params2).toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            Volley.newRequestQueue(requireContext()).add(partyOutstandingReq);

        }
    }
}
