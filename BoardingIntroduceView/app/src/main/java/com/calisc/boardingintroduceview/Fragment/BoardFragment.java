package com.calisc.boardingintroduceview.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.calisc.boardingintroduceview.Models.BoardModel;
import com.calisc.boardingintroduceview.R;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnChangeListener;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnLeftOutListener;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnRightOutListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BoardFragment extends Fragment {
    private PaperBoardingOnChangeListener mOnChangeListener;
    private PaperBoardingOnLeftOutListener mOnLeftOutListener;
    private PaperBoardingOnRightOutListener mOnRightOutListener;

    private ArrayList<BoardModel> mElements = null;

    public static BoardFragment newInstance(ArrayList<BoardModel> data) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putSerializable("elements", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mElements = (ArrayList<BoardModel>) getArguments().get("elements");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_fragment_layout, container, false);

        PaperBoardingEngine engine = new PaperBoardingEngine(view.findViewById(R.id.root), mElements, getActivity().getApplicationContext());
        engine.setOnChangeListener(mOnChangeListener);
        engine.setOnLeftOutListener(mOnLeftOutListener);
        engine.setOnRightOutListener(mOnRightOutListener);

        return view;
    }

    public PaperBoardingOnChangeListener getmOnChangeListener() {
        return mOnChangeListener;
    }

    public void setmOnChangeListener(PaperBoardingOnChangeListener mOnChangeListener) {
        this.mOnChangeListener = mOnChangeListener;
    }

    public PaperBoardingOnLeftOutListener getmOnLeftOutListener() {
        return mOnLeftOutListener;
    }

    public void setmOnLeftOutListener(PaperBoardingOnLeftOutListener mOnLeftOutListener) {
        this.mOnLeftOutListener = mOnLeftOutListener;
    }

    public PaperBoardingOnRightOutListener getmOnRightOutListener() {
        return mOnRightOutListener;
    }

    public void setmOnRightOutListener(PaperBoardingOnRightOutListener mOnRightOutListener) {
        this.mOnRightOutListener = mOnRightOutListener;
    }

    public ArrayList<BoardModel> getmElements() {
        return mElements;
    }

    public void setmElements(ArrayList<BoardModel> mElements) {
        this.mElements = mElements;
    }
}
