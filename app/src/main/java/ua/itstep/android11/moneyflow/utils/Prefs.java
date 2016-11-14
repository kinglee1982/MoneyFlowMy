package ua.itstep.android11.moneyflow.utils;

import android.net.Uri;

/**
 * Created by Test on 13.04.2016.
 */
public class Prefs {
    public static final String LOG_TAG = "MoneyFlow >>>>>>>>>>";

    public static final boolean DEBUG = true;

    public static final int    DB_CURRENT_VERSION = 2;

    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_LAST_NAME = "last_name";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_EMAIL = "email";


    public static final String API_SERVER = "http://cityfinder.esy.es";

    public static final String DB_NAME = "MoneyFlowDB";

    //description(id, description)
    public static final String TABLE_DESCRIPTION = "description";
    public static final String FIELD_DESC = "description";

    //incomes(id, summa, date, desc_id)
    public static final String TABLE_INCOMES = "incomes";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_SUMMA = "summa";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_DESC_ID = "desc_id";
    public static final String TABLE_INCOMES_JOINED = TABLE_INCOMES + " LEFT JOIN " + TABLE_DESCRIPTION + " ON " + TABLE_INCOMES+"."+FIELD_DESC_ID + " = " + TABLE_DESCRIPTION+"."+FIELD_ID;

    //expenses(id, summa, date, desc_id)
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_EXPENSES_JOINED = TABLE_EXPENSES + " LEFT JOIN " + TABLE_DESCRIPTION + " ON " + TABLE_EXPENSES+"."+FIELD_DESC_ID + " = " + TABLE_DESCRIPTION+"."+FIELD_ID;

    //balance(id, summa_expenses, summa_incomes)
    public static final String TABLE_BALANCE = "balance";
    public static final String FIELD_SUMMA_EXPENSES = "summa_expenses";
    public static final String FIELD_SUMMA_INCOMES = "summa_incomes";

    //category(_id, category)
    public static final String TABLE_CATEGORY = "category";
    public static final String FIELD_CATEGORY = "category";






    //The provider constants:
    public static final String URI_EXPENSES_AUTHORITIES = "ua.itstep.android11.moneyflow.provider";
    public static final String URI_EXPENSES_TYPE = "expenses";
    public static final Uri    URI_EXPENSES = Uri.parse("content://" + URI_EXPENSES_AUTHORITIES + "/" + URI_EXPENSES_TYPE);

    public static final String URI_INCOMES_AUTHORITIES = "ua.itstep.android11.moneyflow.provider";
    public static final String URI_INCOMES_TYPE = "incomes";
    public static final Uri    URI_INCOMES = Uri.parse("content://" + URI_INCOMES_AUTHORITIES + "/" + URI_INCOMES_TYPE);

    public static final String URI_DESCRIPTION_AUTHORITIES = "ua.itstep.android11.moneyflow.provider";
    public static final String URI_DESCRIPTION_TYPE = "description";
    public static final Uri    URI_DESCRIPTION = Uri.parse("content://" + URI_DESCRIPTION_AUTHORITIES + "/" + URI_DESCRIPTION_TYPE);

    public static final String URI_BALANCE_AUTHORITIES = "ua.itstep.android11.moneyflow.provider";
    public static final String URI_BALANCE_TYPE = "balance";
    public static final Uri    URI_BALANCE = Uri.parse("content://" + URI_BALANCE_AUTHORITIES + "/" + URI_BALANCE_TYPE);

    public static final String URI_CATEGORY_AUTHORITIES = "ua.itstep.android11.moneyflow.provider";
    public static final String URI_CATEGORY_TYPE = "category";
    public static final Uri    URI_CATEGORY = Uri.parse("content://" + URI_CATEGORY_AUTHORITIES + "/" + URI_CATEGORY_TYPE);

    public static final String CURRENT_MONTH = "current";

    public static final String QUERY = "SELECT * FROM expenses LEFT JOIN description ON expenses.desc_id = description._id;";


    public static final String UAH = " грн";

    public static final String SBERBANK_RF = "5595"; //320627
    public static final int SBERBANK_RF_OPLATA = 3206271;
    public static final int SBERBANK_RF_ZACHISLENIE = 3206272;


    public static final int EXPENSES = 1;
    public static final int INCOMES = 2;

}
