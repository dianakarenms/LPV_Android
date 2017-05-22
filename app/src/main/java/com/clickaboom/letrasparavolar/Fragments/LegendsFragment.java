package com.clickaboom.letrasparavolar.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clickaboom.letrasparavolar.Adapters.BooksAdapter;
import com.clickaboom.letrasparavolar.Models.Book;
import com.clickaboom.letrasparavolar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class LegendsFragment extends Fragment {

    private Book mBook;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private BooksAdapter mAdapter;

    public LegendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = new Book();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_legends, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.legends_recycler);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BooksAdapter(getBooks(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private List<Book> getBooks() {
        List<Book> list = new ArrayList<>();
        for(int i=0; i<20; i ++) {
            list.add(new Book("Los primeros dioses" + i, "Leyenda popular " + i, R.drawable.test_1));
        }
        return list;
    }
}
