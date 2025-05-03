package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.RoundedImageView;
import com.newsalesbeatApp.pojo.ChatItem;

import java.util.ArrayList;

/*
 * Created by Dhirendra Thakur on 03-01-2018.
 */

public class RetailersFeedBackAdapter extends RecyclerView.Adapter<RetailersFeedBackAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ChatItem> chatHistory;
    //private SharedPreferences prefSFA;

    public RetailersFeedBackAdapter(Context ctx, ArrayList<ChatItem> chatHistory) {
        this.context = ctx;
        this.chatHistory = chatHistory;
        //prefSFA = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.retailers_feedbak_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvFeedBackBy.setText(chatHistory.get(position).getEmpName());
        holder.tvDate.setText(chatHistory.get(position).getDate());

        if (chatHistory.get(position).getStatus().contains("fail"))
            holder.imgServerStatus.setImageResource(R.drawable.ic_done_white_36dp);
        else if (chatHistory.get(position).getStatus().contains("success"))
            holder.imgServerStatus.setImageResource(R.drawable.ic_done_all_black_24dp);


//        Log.e("DATATATA","==="+chatHistory.get(position).getDocs()+"\n"+chatHistory.get(position).getVideo()
//                +"\n"+chatHistory.get(position).getAudio()+"\n"+chatHistory.get(position).getLocation()
//                +"\n"+chatHistory.get(position).getContact()+"\n"+chatHistory.get(position).getImage()
//                +"\n"+chatHistory.get(position).getMessage());

        if (position != 0) {

            if (chatHistory.get((position - 1)).getDate().equalsIgnoreCase(chatHistory.get(position).getDate())) {
                holder.tvDate.setVisibility(View.GONE);
            }
        }

        if (chatHistory.get(position).getChannel().contains("user")) {

            holder.rlUser.setVisibility(View.VISIBLE);
            holder.rlAdmin.setVisibility(View.GONE);

            holder.tvTimeStampChat2.setText(chatHistory.get(position).getTimeStamp());

            if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() != null && !chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.GONE);
                holder.documnetViewerUser.setVisibility(View.GONE);

                holder.videoViewerUser.setVisibility(View.GONE);
                holder.audioViewerUser.setVisibility(View.GONE);
                holder.tvMessageUser.setVisibility(View.VISIBLE);
                holder.tvMessageUser.setText(chatHistory.get(position).getMessage());

//                Log.e("INNNN","=====HELOOO");

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() != null || !chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.VISIBLE);
                holder.documnetViewerUser.setVisibility(View.GONE);
                holder.tvMessageUser.setVisibility(View.GONE);
                holder.videoViewerUser.setVisibility(View.GONE);
                holder.audioViewerUser.setVisibility(View.GONE);

                try {

                    holder.imgageViewerUser.setImageURI(Uri.parse(chatHistory.get(position).getImage()));

                } catch (Exception e) {

//                    Glide.with(context)
//                            .load("https://dgeqtkz13qm3j.cloudfront.net/feedback/orig/"+ chatHistory.get(position).getImage())
//                            .into(holder.imgageViewerUser);

                }


            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() != null || !chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.GONE);
                holder.documnetViewerUser.setVisibility(View.GONE);
                holder.tvMessageUser.setVisibility(View.GONE);
                holder.audioViewerUser.setVisibility(View.GONE);
                holder.videoViewerUser.setVisibility(View.VISIBLE);
                holder.videoViewerUser.setVideoURI(Uri.parse(chatHistory.get(position).getVideo()));
                holder.videoViewerUser.seekTo(5000);

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() != null || !chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.GONE);
                holder.documnetViewerUser.setVisibility(View.GONE);
                holder.tvMessageUser.setVisibility(View.GONE);
                holder.audioViewerUser.setVisibility(View.VISIBLE);
                holder.videoViewerUser.setVisibility(View.GONE);

            } else if ((chatHistory.get(position).getDocs() != null || !chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.GONE);
                holder.documnetViewerUser.setVisibility(View.VISIBLE);
                holder.tvMessageUser.setVisibility(View.GONE);
                holder.audioViewerUser.setVisibility(View.GONE);
                holder.videoViewerUser.setVisibility(View.GONE);

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() != null || !chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewerUser.setVisibility(View.GONE);
                holder.documnetViewerUser.setVisibility(View.GONE);
                holder.tvMessageUser.setVisibility(View.VISIBLE);
                holder.audioViewerUser.setVisibility(View.GONE);
                holder.videoViewerUser.setVisibility(View.GONE);

                holder.tvMessageUser.setText(chatHistory.get(position).getContact());

            }


        } else {

            holder.rlUser.setVisibility(View.GONE);
            holder.rlAdmin.setVisibility(View.VISIBLE);

            holder.tvTimeStampChat1.setText(chatHistory.get(position).getTimeStamp());
            if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() != null && !chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.GONE);
                holder.documnetViewer.setVisibility(View.GONE);

                holder.videoViewer.setVisibility(View.GONE);
                holder.audioViewer.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.VISIBLE);
                holder.tvMessage.setText(chatHistory.get(position).getMessage());

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() != null || !chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.VISIBLE);
                holder.documnetViewer.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.videoViewer.setVisibility(View.GONE);
                holder.audioViewer.setVisibility(View.GONE);
                holder.imgageViewer.setImageURI(Uri.parse(chatHistory.get(position).getImage()));

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() != null || !chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.GONE);
                holder.documnetViewer.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.audioViewer.setVisibility(View.GONE);
                holder.videoViewer.setVisibility(View.VISIBLE);
                holder.videoViewer.setVideoURI(Uri.parse(chatHistory.get(position).getVideo()));
                holder.videoViewer.seekTo(5000);

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() != null || !chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.GONE);
                holder.documnetViewer.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.audioViewer.setVisibility(View.VISIBLE);
                holder.videoViewer.setVisibility(View.GONE);

            } else if ((chatHistory.get(position).getDocs() != null || !chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() == null || chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.GONE);
                holder.documnetViewer.setVisibility(View.VISIBLE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.audioViewer.setVisibility(View.GONE);
                holder.videoViewer.setVisibility(View.GONE);

            } else if ((chatHistory.get(position).getDocs() == null || chatHistory.get(position).getDocs().isEmpty())
                    && (chatHistory.get(position).getVideo() == null || chatHistory.get(position).getVideo().isEmpty())
                    && (chatHistory.get(position).getAudio() == null || chatHistory.get(position).getAudio().isEmpty())
                    && (chatHistory.get(position).getLocation() == null || chatHistory.get(position).getLocation().isEmpty())
                    && (chatHistory.get(position).getContact() != null || !chatHistory.get(position).getContact().isEmpty())
                    && (chatHistory.get(position).getImage() == null || chatHistory.get(position).getImage().isEmpty())
                    && (chatHistory.get(position).getMessage() == null && chatHistory.get(position).getMessage().isEmpty())) {

                holder.imgageViewer.setVisibility(View.GONE);
                holder.documnetViewer.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.VISIBLE);
                holder.audioViewer.setVisibility(View.GONE);
                holder.videoViewer.setVisibility(View.GONE);

                holder.tvMessage.setText(chatHistory.get(position).getContact());

            }

        }
    }

    @Override
    public int getItemCount() {
        return chatHistory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvMessage, tvMessageUser, tvFeedBackBy, tvTimeStampChat1, tvTimeStampChat2;
        RoundedImageView imgAdmin, imgUser;
        ImageView imgageViewer, imgageViewerUser, audioViewer, audioViewerUser, documnetViewer, documnetViewerUser, imgServerStatus;
        VideoView videoViewer, videoViewerUser;
        RelativeLayout rlAdmin, rlUser;


        public MyViewHolder(View itemView) {
            super(itemView);

            rlAdmin = (RelativeLayout) itemView.findViewById(R.id.rlAdmin);
            rlUser = (RelativeLayout) itemView.findViewById(R.id.rlUser);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvMessageUser = (TextView) itemView.findViewById(R.id.tvMessageUser);
            tvFeedBackBy = (TextView) itemView.findViewById(R.id.tvFeedBackBy);
            tvTimeStampChat1 = (TextView) itemView.findViewById(R.id.tvTimeStampChat1);
            tvTimeStampChat2 = (TextView) itemView.findViewById(R.id.tvTimeStampChat2);
            imgAdmin = (RoundedImageView) itemView.findViewById(R.id.imgAdmin);
            imgUser = (RoundedImageView) itemView.findViewById(R.id.imgUser);
            imgageViewerUser = (ImageView) itemView.findViewById(R.id.imageViewerUser);
            documnetViewerUser = (ImageView) itemView.findViewById(R.id.documnetViewerUer);
            audioViewerUser = (ImageView) itemView.findViewById(R.id.audioViewerUser);
            videoViewerUser = (VideoView) itemView.findViewById(R.id.videoViewerUser);

            imgageViewer = (ImageView) itemView.findViewById(R.id.imageViewer);
            documnetViewer = (ImageView) itemView.findViewById(R.id.documnetViewer);
            audioViewer = (ImageView) itemView.findViewById(R.id.audioViewer);
            videoViewer = (VideoView) itemView.findViewById(R.id.videoViewer);
            imgServerStatus = (ImageView) itemView.findViewById(R.id.imgServerStatus);
        }
    }
}

