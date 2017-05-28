package com.clickaboom.letrasparavolar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.BooksAdapter;
import com.clickaboom.letrasparavolar.models.Book;
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
    private List<Book> mBooksList = new ArrayList<>();

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

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.legends_title));
        v.findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.right_bnt).setVisibility(View.VISIBLE);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BooksAdapter(mBooksList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        loadBooks();
        return v;
    }

    private void loadBooks() {
        for(int i=0; i<20; i ++) {
            mBooksList.add(new Book("Los primeros dioses " + i, "Leyenda popular " + i, R.drawable.test_1));
            mAdapter.notifyItemInserted(i);
        }
    }
}
