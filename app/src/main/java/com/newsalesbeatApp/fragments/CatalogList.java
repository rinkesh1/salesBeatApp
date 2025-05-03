package com.newsalesbeatApp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.CatalogListAdapter;
import com.newsalesbeatApp.services.RefreshDataService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;

/*
 * Created by MTC on 28-09-2017.
 */

public class CatalogList extends Fragment {

    private String[] FilePathStrings;
    private ArrayList<String> desc = new ArrayList<>();

    private SharedPreferences myPref;
    private ShimmerRecyclerView rcvCatalogList;
    private TextView tvNoDocs;
    private SwipeRefreshLayout pullToRefresh;

    private UtilityClass utilityClass;
    private SalesBeatDb salesBeatDb;
    private RefreshData refreshData;
    private ProgressBar pbCatalogList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {

        View view = inflater.inflate(R.layout.catalog_list, parent, false);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rcvCatalogList = view.findViewById(R.id.catalogList);
        tvNoDocs = view.findViewById(R.id.tvNoDocs);
        pbCatalogList = view.findViewById(R.id.pbCatalogList);

        utilityClass = new UtilityClass(getContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());


        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (utilityClass.isInternetConnected())
                    startRefreshService();
                pullToRefresh.setRefreshing(false);
            }
        });

        prepareData();

        return view;
    }

    private void startRefreshService() {

        rcvCatalogList.setVisibility(View.GONE);
        tvNoDocs.setVisibility(View.GONE);
        pbCatalogList.setVisibility(View.VISIBLE);
        refreshData = new RefreshData(new Handler());
        Intent intent = new Intent(getContext(), RefreshDataService.class);
        intent.putExtra("api", "getCatalog");
        intent.putExtra("receiver", refreshData);
        requireActivity().startService(intent);

    }

    public void onDestroy() {

        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void prepareData() {

        new LoadCatalogList().execute();

    }

    private class RefreshData extends ResultReceiver {

        private RefreshData(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case RefreshDataService.DOWNLOAD_ERROR:
                    Toast.makeText(requireContext(), "Not refreshed", Toast.LENGTH_SHORT).show();
                    break;

                case RefreshDataService.DOWNLOAD_SUCCESS:
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                    prepareData();
                    break;
            }

            super.onReceiveResult(resultCode, resultData);
        }

    }

    private class LoadCatalogList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Cursor cursor = null;
            try {
                cursor = salesBeatDb.gettDocs();

                int pos = 0;

                desc.clear();

                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    FilePathStrings = new String[cursor.getCount()];

                    do {

                        String imgUrl = cursor.getString(cursor.getColumnIndex("docs_img"));
                        String campContent = cursor.getString(cursor.getColumnIndex("docs_content"));

                        FilePathStrings[pos] = imgUrl;
                        desc.add(campContent);

                        pos++;
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                Toast.makeText(getContext(), "Pull to refresh", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // pbCatalogList.setVisibility(View.VISIBLE);
//            rcvCatalogList.setVisibility(View.GONE);
//            tvNoDocs.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (FilePathStrings != null && FilePathStrings.length > 0) {

                CatalogListAdapter catalogListAdapter = new CatalogListAdapter(getContext(),
                        FilePathStrings, desc);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                rcvCatalogList.setLayoutManager(layoutManager);
                rcvCatalogList.setAdapter(catalogListAdapter);
                catalogListAdapter.notifyDataSetChanged();

                rcvCatalogList.setVisibility(View.VISIBLE);
                tvNoDocs.setVisibility(View.GONE);
                pbCatalogList.setVisibility(View.GONE);

            } else {

                if (utilityClass.isInternetConnected())
                    startRefreshService();
                pullToRefresh.setRefreshing(false);

//                rcvCatalogList.setVisibility(View.GONE);
//                tvNoDocs.setVisibility(View.VISIBLE);
//                pbCatalogList.setVisibility(View.GONE);
            }


        }
    }
}
