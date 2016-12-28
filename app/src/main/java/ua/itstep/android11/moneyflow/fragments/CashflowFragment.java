package ua.itstep.android11.moneyflow.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;



import ua.itstep.android11.moneyflow.R;
import ua.itstep.android11.moneyflow.dialogs.CategoryDialog;
import ua.itstep.android11.moneyflow.utils.Prefs;
import ua.itstep.android11.moneyflow.views.Graphics;



/**
 * Created by Maksim Baydala on 04/10/16.
 */
public class CashflowFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView tvCashflowExpenses;
    private TextView tvCashflowExpensesSumma;
    private TextView tvCashflowIncomes;
    private TextView tvCashflowIncomesSumma;
    private Graphics graphics;
    private ViewGroup.LayoutParams layoutParams;

    private ListView lvCaregories;
    SimpleCursorAdapter scCategoriesAdapter;

    private  static  final int CATEGORY_LOADER_ID = 3;
    private  static  final int CASHFLOW_LOADER_ID = 0;

    public static final int URI_EXPENSES_CODE = 1;
    public static final int URI_INCOMES_CODE = 2;
    private static final int URI_CATEGORY_CODE = 3;
    private static final int URI_BALANCE_CODE = 4;

    //public static HashMap<String, String> result;

    private ContentObserver observer;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);



    static {
        uriMatcher.addURI(Prefs.URI_EXPENSES_AUTHORITIES,
                Prefs.URI_EXPENSES_TYPE,
                URI_EXPENSES_CODE);
        uriMatcher.addURI(Prefs.URI_INCOMES_AUTHORITIES,
                Prefs.URI_INCOMES_TYPE,
                URI_INCOMES_CODE);
        uriMatcher.addURI(Prefs.URI_CATEGORY_AUTHORITIES,
                Prefs.URI_CATEGORY_TYPE,
                URI_CATEGORY_CODE);
        uriMatcher.addURI(Prefs.URI_BALANCE_AUTHORITIES,
                Prefs.URI_BALANCE_TYPE,
                URI_BALANCE_CODE);
    }



    @Override
    public void onResume() {
        super.onResume();
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onResume ");
        getActivity().getContentResolver().registerContentObserver(Prefs.URI_BALANCE, true, observer);
        getActivity().getContentResolver().registerContentObserver(Prefs.URI_CATEGORY, true, observer);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onPause ");
        getActivity().getContentResolver().unregisterContentObserver(observer);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onCreateView ");

        String[] from = new String[] {Prefs.FIELD_CATEGORY};
        int[] to = new int[] {R.id.text1};


        View view =  inflater.inflate( R.layout.fragment_cashflow, container , false);
        tvCashflowExpenses = (TextView)view.findViewById(R.id.tvCashflowExpenses);
        tvCashflowExpensesSumma = (TextView)view.findViewById(R.id.tvCashflowSumma);
        tvCashflowIncomes = (TextView)view.findViewById(R.id.tvCashflowIncomes);
        tvCashflowIncomesSumma = (TextView)view.findViewById(R.id.tvCashflowIncomesSumma);
        graphics = (Graphics) view.findViewById(R.id.vGraphics);

        tvCashflowExpenses.setText(Prefs.URI_EXPENSES_TYPE);
        tvCashflowIncomes.setText(Prefs.URI_INCOMES_TYPE);


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        layoutParams = graphics.getLayoutParams();

        layoutParams.width = metrics.widthPixels / 3;
        layoutParams.height = metrics.heightPixels / 2;



        Log.d(Prefs.LOG_TAG,"layoutParams.width: " + layoutParams.width);
        Log.d(Prefs.LOG_TAG,"layoutParams.height: " + layoutParams.height);


        scCategoriesAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.category_item,
                null, from, to,
                CursorAdapter.NO_SELECTION);

        lvCaregories = (ListView) view.findViewById(R.id.lvCategories);
        lvCaregories.setAdapter(scCategoriesAdapter);
        lvCaregories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment setOnItemClickListener onItemClick id:" + id);

                CategoryDialog categoryDialog = new CategoryDialog();

                Bundle args = new Bundle();
                args.putLong(Prefs.FIELD_ID, id);

                categoryDialog.setArguments(args);
                categoryDialog.show(getFragmentManager(), "CF");

            }
        });

        observer = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "ContentObserver CashFlow onChange selfChange ="+ selfChange +"||"+uri);

                switch (uriMatcher.match(uri)) {
                    case URI_BALANCE_CODE:
                        getActivity().getSupportLoaderManager().restartLoader(CASHFLOW_LOADER_ID, null, CashflowFragment.this);
                        Log.d(Prefs.LOG_TAG, "ContentObserver CashFlow onChange URI_BALANCE_CODE ");
                        break;

                    case URI_CATEGORY_CODE:
                        getActivity().getSupportLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, CashflowFragment.this);
                        Log.d(Prefs.LOG_TAG, "ContentObserver CashFlow onChange URI_CATEGORY_CODE ");
                        break;
                }


            }
        };


        getActivity().getContentResolver().registerContentObserver(Prefs.URI_BALANCE, false, observer);
        getActivity().getContentResolver().registerContentObserver(Prefs.URI_CATEGORY, false, observer);

        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onCreateView observer = "+ observer);


        setHasOptionsMenu(true);

        getActivity().getSupportLoaderManager().initLoader(CASHFLOW_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);

        Loader<Object> loader = getActivity().getSupportLoaderManager().getLoader(CASHFLOW_LOADER_ID);
        getActivity().getSupportLoaderManager().enableDebugLogging(Prefs.DEBUG);
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onCreateView getLoader = "+ loader);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onCreateLoader " + id);

        switch (id) {
            case CASHFLOW_LOADER_ID:
                return new CashflowCursorLoader(getActivity());

            case CATEGORY_LOADER_ID:
                return new CategoryCursorLoader(getActivity());


        }
        return null;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished expenses- " +data.get(Prefs.FIELD_SUMMA_EXPENSES) );
        //if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished incomes- " +data.get(Prefs.FIELD_SUMMA_INCOMES) );
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished getLoader = "+ loader.getId());
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG,"CashflowFragment onLoadFinished layoutParams.width: " + layoutParams.width);
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG,"CashflowFragment onLoadFinished layoutParams.height: " + layoutParams.height);

        switch (loader.getId()) {
            case CASHFLOW_LOADER_ID:
                if ( data != null && data.moveToFirst() ) {
                    tvCashflowExpensesSumma.setText( data.getString(data.getColumnIndex(Prefs.FIELD_SUMMA_EXPENSES)) + Prefs.UAH );
                    tvCashflowIncomesSumma.setText( data.getString(data.getColumnIndex(Prefs.FIELD_SUMMA_INCOMES)) + Prefs.UAH );

                    graphics.setValues( data.getFloat(data.getColumnIndex(Prefs.FIELD_SUMMA_INCOMES)),
                                        data.getFloat(data.getColumnIndex(Prefs.FIELD_SUMMA_EXPENSES)) );
                    graphics.invalidate();
                }

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished observer = "+ observer.getClass().toString());
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished  CASHFLOW_LOADER_ID");
                break;

            case CATEGORY_LOADER_ID:
                scCategoriesAdapter.swapCursor(data);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished  CATEGORY_LOADER_ID");
                break;
        }




    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoaderReset");

        switch (loader.getId()) {
            case CASHFLOW_LOADER_ID:
                //todo

                if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished  CASHFLOW_LOADER_ID");
                break;

            case CATEGORY_LOADER_ID:
                scCategoriesAdapter.swapCursor(null);
                if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onLoadFinished  CATEGORY_LOADER_ID");
                break;

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);


        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onCreateOptionsMenu");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                getActivity().getSupportLoaderManager().restartLoader(CASHFLOW_LOADER_ID, null, this);
                getActivity().getSupportLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, this);

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onOptionsItemSelected item_refresh");
                break;

            case R.id.item_reCalc:
                recalcData(Prefs.URI_INCOMES);
                recalcData(Prefs.URI_EXPENSES);

                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onOptionsItemSelected item_reCalc");
                break;
        }
        return true;
    }

    private void recalcData(Uri uriTable) {
        ContentValues cvBalance;

        Cursor cursor = getContext().getContentResolver().query(uriTable, new String[]{Prefs.FIELD_SUMMA}, null, null, null);
        String key = uriTable.getLastPathSegment();

        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData: " + key);

        if ( (cursor != null) && cursor.moveToFirst() ) {
            double summa = 0.0;
            int updatedRows = 0;
            //cursor.moveToFirst();

            if ( cursor.getCount() != 0 ) {

                do {
                    double value = 0;
                    value = cursor.getDouble(cursor.getColumnIndex(Prefs.FIELD_SUMMA));
                    summa += value;
                    if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData value: " + summa);
                } while (cursor.moveToNext());
                //Log.d(Prefs.LOG_TAG, "ExpensesFragment HashMapLoader onStartLoading  " + String.valueOf(value));

                cvBalance = new ContentValues();

                switch (uriMatcher.match(uriTable)) {
                    case URI_EXPENSES_CODE:
                        cvBalance.put(Prefs.FIELD_SUMMA_EXPENSES, summa);
                        Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData SUMMA_EXPENSES: " + summa);
                        break;

                    case URI_INCOMES_CODE:
                        cvBalance.put(Prefs.FIELD_SUMMA_INCOMES, summa);
                        Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData SUMMA_INCOMES: " + summa);
                        break;
                }


                updatedRows = getContext().getContentResolver().update(Prefs.URI_BALANCE, cvBalance, null, null);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData updatedRows: " + updatedRows);

            } else {
                cvBalance = new ContentValues();

                switch (uriMatcher.match(uriTable)) {
                    case URI_EXPENSES_CODE:
                        cvBalance.put(Prefs.FIELD_SUMMA_EXPENSES, summa);
                        break;

                    case URI_INCOMES_CODE:
                        cvBalance.put(Prefs.FIELD_SUMMA_INCOMES, summa);
                        break;
                }

                updatedRows = getContext().getContentResolver().update(Prefs.URI_BALANCE, cvBalance, null, null);
                if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment recalcData cursor.getCount = 0  updatedRows: " + updatedRows);

            }
            cursor.close();

        } else {
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment onStartLoading - Cursor is NULL");
            Log.e(Prefs.LOG_TAG, "CashflowFragment onStartLoading - Cursor is NULL");
        }
    }



    private static class CategoryCursorLoader extends CursorLoader {

        CategoryCursorLoader( Context context ) {
            super(context, Prefs.URI_CATEGORY,
                    new String[] {Prefs.FIELD_ID, Prefs.FIELD_CATEGORY},
                    null, null, null);
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment CategoryCursorLoader() ");

            //result = new HashMap<>();
        }

        @Override
        public Cursor loadInBackground() {
            //if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "IncomesFragment IncomesCursorLoader loadInBackground");

            Cursor cursor = getContext().getContentResolver().query(Prefs.URI_CATEGORY, new String[]{Prefs.FIELD_ID, Prefs.FIELD_CATEGORY}, null, null, null);
            //if(Prefs.DEBUG) logCursor(cursor);
            if ( (null == cursor) || !cursor.moveToFirst() || (0 == cursor.getCount()) ) {
                throw new SQLException("Failed to load data from "+ Prefs.URI_CATEGORY);
            }
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment CategoryCursorLoader loadInBackground rows: " +cursor.getCount());

            return cursor;
        }




    }


    private static class CashflowCursorLoader extends CursorLoader {

        CashflowCursorLoader( Context context ) {
            super(context, Prefs.URI_BALANCE,
                    new String[] {Prefs.FIELD_ID, Prefs.FIELD_SUMMA_EXPENSES, Prefs.FIELD_SUMMA_INCOMES},
                    null, null, null);
            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment CashflowCursorLoader() ");

            //result = new HashMap<>();
        }

        @Override
        public Cursor loadInBackground() {
            //if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "IncomesFragment IncomesCursorLoader loadInBackground");

            Cursor cursor = getContext().getContentResolver().query(Prefs.URI_BALANCE, new String[]{Prefs.FIELD_ID, Prefs.FIELD_SUMMA_EXPENSES, Prefs.FIELD_SUMMA_INCOMES}, null, null, null);
            //if(Prefs.DEBUG) logCursor(cursor);



            if ( (null == cursor) || !cursor.moveToFirst() || (0 == cursor.getCount()) ) {
                Log.e(Prefs.LOG_TAG, "Failed to load data from "+ Prefs.URI_BALANCE);
            }

            if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment CashflowCursorLoader loadInBackground rows: " +cursor.getCount());


            return cursor;
        }

        /*

        @Override
        protected void onStartLoading() {
            if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader onStartLoading");

            super.onStartLoading();

            if (takeContentChanged()) {
                forceLoad();
                if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader onStartLoading forseLoad");
            }

            loadData();



        }


        private void loadData() {
            Cursor cursor = getContext().getContentResolver().query(Prefs.URI_BALANCE, new String[]{Prefs.FIELD_ID, Prefs.FIELD_SUMMA_EXPENSES, Prefs.FIELD_SUMMA_INCOMES}, null, null, null);

            String key = Prefs.URI_BALANCE.getLastPathSegment();

            if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader loadData  " + key);

            if ( cursor.moveToFirst() ) {

                    double summaExpenses = cursor.getDouble(cursor.getColumnIndex(Prefs.FIELD_SUMMA_EXPENSES));
                    double summaIncomes = cursor.getDouble(cursor.getColumnIndex(Prefs.FIELD_SUMMA_INCOMES));

                    result.put(Prefs.FIELD_SUMMA_EXPENSES, String.valueOf(summaExpenses));
                    result.put(Prefs.FIELD_SUMMA_INCOMES, String.valueOf(summaIncomes));

                    if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader loadData  " + String.valueOf(summaExpenses));
                    if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader loadData  " + String.valueOf(summaIncomes));

            } else {
                result.put(Prefs.FIELD_SUMMA_EXPENSES, "0");
                result.put(Prefs.FIELD_SUMMA_INCOMES, "0");

                if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment loadData - Cursor is NULL");
            }

            if(DEBUG) Log.d(Prefs.LOG_TAG, "CashflowFragment HashMapLoader loadData  deliverResult");

            cursor.close();
            deliverResult(result);


        }


        */



    }
}
