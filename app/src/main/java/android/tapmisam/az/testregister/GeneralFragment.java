package android.tapmisam.az.testregister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class GeneralFragment extends Fragment implements SearchView.OnQueryTextListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private JSONArray jsonArray;
    private JSONArray cast;
    private String url;
    private String nextUrl;
    private String lastPageUrl;
    private String currentPage;
    private GeneralFragment.OnFragmentInteractionListener mListener;


    private LinearLayoutManager mLayoutManager;
    private ArrayList<Item>items;
    private RecyclerView recyclerView;
    private ItemsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private boolean isLoading=true;
    private boolean isLastPage;


    public GeneralFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items=new ArrayList<>();
        getItems("http://aysel.campuncke.com/public/api/v1/items/get?page=1");
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_general, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar=view.findViewById(R.id.main_progress);
        progressBar.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);

//
//        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#b9296b"), PorterDuff.Mode.MULTIPLY);
//        progressBar.setVisibility(View.GONE);
        adapter = new ItemsAdapter(getContext(), items);
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        swipeRefresh=view.findViewById(R.id.swiperefresh);
        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
        */
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        url="http://aysel.campuncke.com/public/api/v1/items/get?page=1";
                        refreshItems(url);
                    }
                }
        );

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), "Single Click on position :"+position,
                        Toast.LENGTH_SHORT).show();

                int h=recyclerView.getChildAdapterPosition(view);
                int id=  items.get(h).getId();
//                getFullInfo(id);

//                InfoDialogFragment infoDialogFragment=new InfoDialogFragment();
                Bundle args=new Bundle();
                args.putInt("id",id);
//                infoDialogFragment.setArguments(args);
                Intent intent = new Intent(getActivity(),ItemInfoActivity.class);
                intent.putExtras(args);

                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));
        return view;
    }
    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;
        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount=mLayoutManager.getChildCount();
            int totalItemCount=mLayoutManager.getItemCount();
            int firstVisibleItemPosition=mLayoutManager.findFirstVisibleItemPosition();
            if(dy>0) {
                if (!isLoading&&nextUrl!=null) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreItems();
                        isLoading=true;
                        Log.e(Integer.toString(visibleItemCount),Integer.toString(totalItemCount)+" "+Integer.toString(firstVisibleItemPosition));
                    }
                }
            }else{
                isLoading=false;
                progressBar.setVisibility(View.GONE);

            }
        }
    };

    private void loadMoreItems(){
        if(currentPage==lastPageUrl) {
            Log.e("adas","asdas");
        }else{
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
            getItems(nextUrl);
        }

    }

    private void getItems(String url){
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading=false;
                progressBar.setVisibility(View.GONE);
                try {
                    cast = response.getJSONArray("data");
                    jsonArray=new JSONArray();

                    lastPageUrl=response.getString("last_page");
                    currentPage=response.getString("current_page");
                    Log.e(currentPage,lastPageUrl);

                        nextUrl = response.getString("next_page_url");

                        Log.e("LAST", lastPageUrl);
                        Log.e("NEXT", nextUrl);
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject actor = cast.getJSONObject(i);
                            String description = actor.getString("description");
                            String title = actor.getString("title");
                            jsonArray = actor.getJSONArray("images");
                            int id = actor.getInt("id");
                            Item item;
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject im = jsonArray.getJSONObject(x);
                                String name = im.getString("name");
                                String image = "http://aysel.campuncke.com" + im.getString("image");
                                item = new Item(id, description, title, image);
                                items.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                }catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String e=new String(networkResponse.data);
                    try {
                        JSONObject json = new JSONObject(e);
                        Toast.makeText(getContext(),json.get("errors").toString(),Toast.LENGTH_LONG).show();
                    }catch(JSONException exc){
                        exc.printStackTrace();
                    }
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);


    }
    private void refreshItems(String url){
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading=false;
                progressBar.setVisibility(View.GONE);
                try {
                    items.clear();
                    cast = response.getJSONArray("data");
                    jsonArray=new JSONArray();
                    nextUrl=response.getString("next_page_url");
                    Log.e("NEXT",nextUrl);
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        String description = actor.getString("description");
                        String title=actor.getString("title");
                        jsonArray=actor.getJSONArray("images");
                        Item item;
                        for (int x=0; x<jsonArray.length(); x++) {
                            JSONObject im = jsonArray.getJSONObject(x);
                            String name = im.getString("name");
                            int id=im.getInt("id");
                            String image = "http://aysel.campuncke.com"+im.getString("image");
                            item=new Item(id,description,title,image);
                            items.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    onItemsLoadComplete();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String e=new String(networkResponse.data);
                    try {
                        JSONObject json = new JSONObject(e);
                        Toast.makeText(getContext(),json.get("errors").toString(),Toast.LENGTH_LONG).show();
                    }catch(JSONException exc){
                        exc.printStackTrace();
                    }
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);


    }
    public void onItemsLoadComplete(){
        swipeRefresh.setRefreshing(false);
    }
    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchMenu:
//                getItems();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu,menu);

        final MenuItem item=menu.findItem(R.id.searchMenu);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                url="http://aysel.campuncke.com/public/api/v1/items/get?page=1";
                getItems(url);
                //Find EditText view


                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                //Collapse the search widget

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        findItems(newText);
        return true;
    }

    private void findItems(String item) {
        items.clear();
        url="http://aysel.campuncke.com/public/api/v1/items/search/"+item;
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                isLoading = false;
//                progressBar.setVisibility(View.GONE);
                try {
                    cast = response.getJSONArray("data");
                    jsonArray = new JSONArray();
                    nextUrl = response.getString("next_page_url");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        String description = actor.getString("description");
                        String title = actor.getString("title");
                        jsonArray = actor.getJSONArray("images");
                        int id = actor.getInt("id");
                        Item item;
                        for (int x = 0; x < jsonArray.length(); x++) {
                            JSONObject im = jsonArray.getJSONObject(x);
                            String name = im.getString("name");
                            String image = "http://aysel.campuncke.com" + im.getString("image");
                            item = new Item(id, description, title, image);
                            items.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String e = new String(networkResponse.data);
                    try {
                        JSONObject json = new JSONObject(e);
                        Toast.makeText(getContext(), json.get("errors").toString(), Toast.LENGTH_LONG).show();
                    } catch (JSONException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        });
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);


    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof InfoDialogFragment.OnFragmentInteractionListener) {
//            mListener = (GeneralFragment.OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}