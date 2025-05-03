package com.newsalesbeatApp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.OfflineNotificationModel;
import com.newsalesbeatApp.adapters.OfflineNotificationAdapter;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private View view;
    private SalesBeatDb db;
    private TextView noNotif;
    private RecyclerView notifView;
    private ArrayList<OfflineNotificationModel> notifList;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        notifView = view.findViewById(R.id.notif_recyclerview);
        noNotif = view.findViewById(R.id.no_notif);
        db = SalesBeatDb.getHelper(getContext());

        notifList = (ArrayList<OfflineNotificationModel>) getArguments().getSerializable("notiflist");
        notifView.setLayoutManager(new LinearLayoutManager(getContext()));

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        notifView.setLayoutAnimation(animation);

        OfflineNotificationAdapter adapter = new OfflineNotificationAdapter(notifList);
        notifView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        db.deleteNotifItem(notifList.get(viewHolder.getAdapterPosition()).getNotifId());
                        notifList.remove(viewHolder.getAdapterPosition());
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        if (notifList.size() == 0) {
                            notifView.setVisibility(View.INVISIBLE);
                            noNotif.setVisibility(View.VISIBLE);
                        }
                    }
                };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notifView);
        return view;
    }

}
