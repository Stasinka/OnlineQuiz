package com.example.peter.onlinequiz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peter.onlinequiz.Common.Common;
import com.example.peter.onlinequiz.Interface.ItemClickListener;
import com.example.peter.onlinequiz.Interface.RankingCallBack;
import com.example.peter.onlinequiz.Model.Question;
import com.example.peter.onlinequiz.Model.QuestionScore;
import com.example.peter.onlinequiz.Model.Ranking;
import com.example.peter.onlinequiz.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment {


    View myfragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking,RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore,rankingTbl;

    int sum=0;

    public static RankingFragment newInstance(){
        RankingFragment rankingFragment=new RankingFragment();
        return rankingFragment;

    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        database=FirebaseDatabase.getInstance();
        questionScore=database.getReference("Question_Score");
        rankingTbl =database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myfragment =inflater.inflate(R.layout.fragment_ranking,container,false);

        //init View
        rankingList =(RecyclerView)myfragment.findViewById(R.id.rankingList);
        layoutManager=new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        //Becasue order child method of firebase will sort list with asc so we nned reverse
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        //now need implement callback
        updateScore(Common.currentUser.getUserName(), new RankingCallBack<Ranking>() {
            @Override
            public void callback(Ranking ranking) {
                //update to ranking table
                rankingTbl.child(ranking.getUserName())
                        .setValue(ranking);
               // showRanking();//after upload we will sort ranking table and show

            }
        });

  //set adapter
        adapter=new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>( Ranking.class,
                                                                              R.layout.layout_ranking,
                                                                               RankingViewHolder.class,
                                                                                rankingTbl.orderByChild("score")

                )

                {
                    @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {

                    viewHolder.txt_name.setText(model.getUserName());
                    viewHolder.txt_score.setText(String.valueOf(model.getScore()));

                    //fixed crash when click to idem
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent scoreDetail=new Intent(getActivity(),ScoreDetail.class);
                            scoreDetail.putExtra("viewUser",model.getUserName());
                            startActivity(scoreDetail);

                        }
                    });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return myfragment;
    }

    private void showRanking(){

        rankingTbl.orderByChild("score")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            Ranking local=data.getValue(Ranking.class);
                            Log.d("DEBUG",local.getUserName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




    }

    private void updateScore(final String userName, final RankingCallBack<Ranking> callback) {

        questionScore.orderByChild("user").equalTo(userName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren())
                            {
                                QuestionScore ques=data.getValue(QuestionScore.class);
                                sum+= Integer.parseInt(ques.getScore());
                            }
                            //after sumary all score
                            Ranking ranking=new Ranking(userName,sum);
                            callback.callback(ranking);
                         }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

}
