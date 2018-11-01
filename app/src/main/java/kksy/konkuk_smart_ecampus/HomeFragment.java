package kksy.konkuk_smart_ecampus;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static kksy.konkuk_smart_ecampus.ExpandableListAdapter.CHILD2;
import static kksy.konkuk_smart_ecampus.ExpandableListAdapter.HEADER;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    TimelineListAdapter adapter;

    List<TimelineListAdapter.Item> timelineList;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    /*YEORI*/
    ArrayList<TimeLine> timeLines=new ArrayList<TimeLine>();
    ArrayList<String> boardID=new ArrayList<String>();
    FirebaseDatabase mdatabase;
    DatabaseReference mdbRef;
    Query query;
    Query query2;
    static boolean on=false;
    ArrayList<Board> boards=new ArrayList<Board>();
    String userID="201611210";  //MainActivity에서 HomeFragment로 접근할 때 넘겨줄 것(열->자영언니에게 말하기)
    String subject="s1";    //MainActivity에서 HomeFragment로 접근할 때 넘겨줄 것(열->자영언니에게 말하기)
    //수강과목은 여러개 일 수 있으니까 나중에는 배열로 고칠 것.

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.listViewTimeline);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        initTimeline();

        return view;
    }

    public void initTimeline(){
        timelineList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        boardID=new ArrayList<String>();
        mdatabase = FirebaseDatabase.getInstance();
        //sugang 에 가서 학번으로 timeline에 접근 한 뒤에 타임라인을 가지고온다.
        //그리고 이 타임라인을 가지고 온 것에 보드 ID를 가지고 와서
        //이 보드에 맞는 걸 가져오면 된다.
        mdbRef=mdatabase.getReference("sugang");
        query = mdbRef.orderByChild("studentID").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Sugang sugang=snapshot.getValue(Sugang.class);
                    Log.i("str", sugang.getSubID());
                    TimeLine timeLine=sugang.getTimeLine();
//                    Log.i("str2", timeLine.getMaterials().get(0).getBoardID());

                    timeLines.add(timeLine);

                    mdbRef=mdatabase.getReference("board");
                    //(임시)
                    query2=mdbRef.orderByKey().equalTo("s1-p1");    //다스리
                    //s1만 가지고 s1-p1에 접근하기 위한 것을 다스리에게 문의

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                on=true;
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    Iterable<DataSnapshot> data = snapshot1.getChildren();
                                    //강의자료
                                    //->강의자료의 모든 value들을 가져온다. 2018...
                                    Iterable<DataSnapshot> data1 = data.iterator().next().getChildren();  //2018-10-26 21:51:45, 2018-10-27...
                                    for (Iterator<DataSnapshot> da = data1.iterator(); data1.iterator().hasNext(); ) {
                                        Board board = da.next().getChildren().iterator().next().getValue(Board.class);
                                        for (int i = 0; i < timeLines.size(); i++) {
                                            List<TimeLineBoardFormat> timeLineA = timeLines.get(i).getMaterials();

                                            for (int j = 0; j < timeLineA.size(); j++) {
                                                if (board.getBoardID().equals(timeLineA.get(j).getBoardID())) {
                                                    Log.i("board title", i + " " + board.getType());
                                                    timelineList.add(new TimelineListAdapter.Item(
                                                            i + "",
                                                            "산학협력 프로젝트2",
                                                            board.getTitle(),
                                                            board.getUploadDate(),
                                                            timeLineA.get(j).isIsread(),
                                                            timeLineA.get(j).isWantTop(),
                                                            board.getSubID_proID(),
                                                            board.getBoardID()
                                                    ));
                                                }
                                            }

                                        }
                                        //Log.i("보쟈", da.next().getValue().toString()); //-LP11bhR2..., -LPIJ...
                                    }

                                    //공지
                                    Iterable<DataSnapshot> data2 = data.iterator().next().getChildren();
                                    for (Iterator<DataSnapshot> da = data2.iterator(); data2.iterator().hasNext(); ) {
                                        Board board = da.next().getChildren().iterator().next().getValue(Board.class);
                                        for (int i = 0; i < timeLines.size(); i++) {
                                            List<TimeLineBoardFormat> timeLineA = timeLines.get(i).getMaterials();

                                            for (int j = 0; j < timeLineA.size(); j++) {
                                                if (board.getBoardID().equals(timeLineA.get(j).getBoardID())) {
                                                    timelineList.add(new TimelineListAdapter.Item(
                                                            i + "",
                                                            "산학협력 프로젝트2",
                                                            board.getTitle(),
                                                            board.getUploadDate(),
                                                            timeLineA.get(j).isIsread(),
                                                            timeLineA.get(j).isWantTop(),
                                                            board.getSubID_proID(),
                                                            board.getBoardID()
                                                    ));

                                                }
                                            }

                                        }
                                    }

                                    //과제
                                    Iterable<DataSnapshot> data3 = data.iterator().next().getChildren();
                                    for (Iterator<DataSnapshot> da = data3.iterator(); data3.iterator().hasNext(); ) {
                                        Board board = da.next().getChildren().iterator().next().getValue(Board.class);
                                        for (int i = 0; i < timeLines.size(); i++) {
                                            List<TimeLineBoardFormat> timeLineA = timeLines.get(i).getMaterials();

                                            for (int j = 0; j < timeLineA.size(); j++) {
                                                if (board.getBoardID().equals(timeLineA.get(j).getBoardID())) {
                                                    timelineList.add(new TimelineListAdapter.Item(
                                                            i + "",
                                                            "산학협력 프로젝트2",
                                                            board.getTitle(),
                                                            board.getUploadDate(),
                                                            timeLineA.get(j).isIsread(),
                                                            timeLineA.get(j).isWantTop(),
                                                            board.getSubID_proID(),
                                                            board.getBoardID()
                                                    ));

                                                }
                                            }

                                        }
                                        //Log.i("value", board.getTitle());
                                    }
                                    /*Collections.sort(data, sortByDate);*/
//                                    Collections.sort(timelineList, sortByChecked);
//                                    Collections.sort(timelineList, sortByBookmark);
                                    adapter.notifyDataSetChanged();
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
        - 여리
        TimelineListAdapter.Item 객체 생성을 통해 Timeline의 Item들 생성
        생성한 Item들을 data에 추가

        - TimelineListAdapter.Item
            TimelineListAdapter.Item(강의명, 게시물 제목, 게시물 업로드 날짜, 게시물 확인 여부, 게시물 북마크 여부)
        - timelineList : Timeline의 List
         */

//        timelineList.add(new TimelineListAdapter.Item(
//                "0",
//                "산학협력프로젝트2(종합설계)",
//                "[10/2] 미팅 요약서",
//                "2018.10.02 오후 11:59",
//                false, false,"s1_p1",
//                "LP1JCk"
//        ));
//        timelineList.add(new TimelineListAdapter.Item(
//                "0",
//                "산학협력프로젝트2(종합설계)",
//                "[10/4] 2차 요구사항 분석서 제출",
//                "2018.10.05 오후 11:59",
//                false, true,"s1_p1",
//                "LP1JCk"
//        ));
//        timelineList.add(new TimelineListAdapter.Item(
//                "0",
//                "발명과특허",
//                "자기소개서",
//                "2018.10.07 오후 11:59",
//                true, false,"s1_p1",
//                "LP1JCk"
//        ));

        // timeline에서 보여질 item 정렬

        /*Collections.sort(data, sortByDate);*/
        Collections.sort(timelineList, sortByChecked);
        Collections.sort(timelineList, sortByBookmark);

        adapter = new TimelineListAdapter(timelineList);
        adapter.setItemPickClick(new TimelineListAdapter.TimelineItemPickClick() {
            @Override
            public void onPickClick(View view, int postion) {
                if(!timelineList.get(postion).isNotPick){
                    timelineList.get(postion).isNotPick = true;

                    /*
                    - 여리
                    Bookmark 해제 시, bookmark 여부 DB에도 반영
                     */
//                    TimeLine timeLine;
//                    timeLine.getHomework().get(0).setIsread(true);
                }
                else{

                    timelineList.get(postion).isNotPick = false;

                    /*
                    - 여리
                    Bookmark 등록 시, bookmark 여부 DB에도 반영
                     */
                    DatabaseReference relation_table;
                    mdbRef=mdatabase.getReference("sugang");

                    String subid_proid=timelineList.get(postion).subid_proid;
                    String[] temp=subid_proid.split("_");

                    relation_table=mdbRef.child(temp[0]+"-"+userID).child("timeLine").push();
                    TimelineListAdapter.Item item=timelineList.get(postion);
                    TimeLineBoardFormat temp2=new TimeLineBoardFormat();
                    temp2.setBoardID(item.boardID);
                    temp2.setIsread(item.isOpen);
                    temp2.setWantTop(item.isCheck);
                    //relation_table.setValue()
                }

                Collections.sort(timelineList, sortByChecked);
                Collections.sort(timelineList, sortByBookmark);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setItemClick(new TimelineListAdapter.TimelineItemClick() {
            @Override
            public void onClick(View view, int position) {
                if(!timelineList.get(position).isCheck){ // 게시물 미확인 -> 게시물 확인
                    timelineList.get(position).isCheck = true;

                    /*
                    - 여리
                    게시물 확인 시, 게시물 확인 여부 DB에도 반영
                     */
                }

//                Collections.sort(timelineList, sortByChecked);
//                Collections.sort(timelineList, sortByBookmark);
                adapter.notifyDataSetChanged();

                /*
                게시물 클릭 시, 해당 게시물 ID를 통해 게시물 fragment로 이동
                 */

                fragmentTransaction = fragmentManager.beginTransaction();
                Log.i("boardnum", position+"");
                Log.i("boardnum", timelineList.get(0).boardID);
                Log.i("boardnum", timelineList.get(1).boardID);
                fragmentTransaction.replace(R.id.fragmentContainer, PostFragment.newInstance(timelineList.get(position).boardID));
                fragmentTransaction.commit();

                Snackbar.make(view, "해당 게시물로 이동", Snackbar.LENGTH_SHORT)
                        .setAction("해당 게시물로 이동", null).show();
            }
        });
        adapter.setItemLongClick(new TimelineListAdapter.TimelineItemLongClick() {
            @Override
            public void onLongClick(View view, int position) {

                /*
                - 여리
                게시물 삭제 시, Timeline DB에서도 삭제
                 */

                timelineList.remove(position);
                adapter.notifyDataSetChanged();

                Snackbar.make(view, "아이템 삭제", Snackbar.LENGTH_SHORT)
                        .setAction("아이템 삭제", null).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    // Bookmark 설정 된 게시글이 위로 가도록 정렬
    private final static Comparator<TimelineListAdapter.Item> sortByBookmark = new Comparator<TimelineListAdapter.Item>() {
        @Override
        public int compare(TimelineListAdapter.Item o1, TimelineListAdapter.Item o2) {
            return Boolean.compare(o1.isNotPick, o2.isNotPick);
        }
    };

    // 읽지 않은 게시글이 위로 가도록 정렬
    private final static Comparator<TimelineListAdapter.Item> sortByChecked = new Comparator<TimelineListAdapter.Item>() {
        @Override
        public int compare(TimelineListAdapter.Item o1, TimelineListAdapter.Item o2) {
            if(Boolean.compare(o1.isCheck, o2.isCheck) == 0)
                return 1;
            return Boolean.compare(o1.isCheck, o2.isCheck);
        }
    };

    // 최근 업로드 게시글이 위로 가도록 정렬
    private final static Comparator<TimelineListAdapter.Item> sortByDate = new Comparator<TimelineListAdapter.Item>() {
        @Override
        public int compare(TimelineListAdapter.Item o1, TimelineListAdapter.Item o2) {
            return 0;
        }
    };

}
