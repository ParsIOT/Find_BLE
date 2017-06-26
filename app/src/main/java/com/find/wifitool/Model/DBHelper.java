package com.find.wifitool.Model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "BoothDB";
    private static final String table_Booth = "Booth";
    private static final String booth_id = "booth_id";
    private static final String booth_name = "booth_name";
    private static final String booth_owner = "booth_owner";
    private static final String booth_image = "booth_image";
    private static final String booth_description = "booth_description";

    private static final String table_Product = "Product";
    private static final String product_id = "product_id";
    private static final String product_name = "product_name";
    private static final String product_model = "product_model";
    private static final String product_image = "product_image";
    private static final String product_description = "product_description";
    private static final String product_price = "product_price";
    private static final String product_status = "product_status";

    private static final String table_booth_product = "booth_product";
    private static final String booth_product_id = "id";





  


    private static final String[] Booth_COLUMNS = {booth_id, booth_name, booth_owner
            , booth_description, booth_image};

    private static final String[] Products_COLUMNS = {product_id, product_name, product_model
            , product_image, product_description, product_price, product_status};

    private static final String[] Booth_Product_COLUMNS = {booth_product_id, booth_id, product_id};

    public DBHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE "+ table_Booth + " ( "
                + booth_id + " INTEGER, "
                + booth_name + " TEXT, "
                + booth_owner + " TEXT, "
                + booth_description + " TEXT, "
                + booth_image + " TEXT "
                + ")";

        String CREATE_PRODUCT_TABLE = "CREATE TABLE "+ table_Product + " ( "
                + product_id + " INTEGER, "
                + product_name + " TEXT, "
                + product_model + " TEXT, "
                + product_description + " TEXT, "
                + product_price + " INTEGER, "
                + product_status + " INTEGER DEFAULT 0, "
                + product_image + " TEXT "
                + ")";

        String CREATE_BOOTH_PRODUCT_TABLE = "CREATE TABLE "+ table_Product + " ( "
                + booth_id + " INTEGER, "
                + product_id + " INTEGER "
                + ")";

        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_BOOTH_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop books table if already exists
        db.execSQL("DROP TABLE IF EXISTS " + table_Booth);
        db.execSQL("DROP TABLE IF EXISTS " + table_Product);
        db.execSQL("DROP TABLE IF EXISTS " + table_booth_product);
        this.onCreate(db);
    }

    public void createProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(product_id, product.getId());
        values.put(product_name, product.getName());
        values.put(product_model, product.getModel());
        values.put(product_description, product.getDescription());
        values.put(product_price, product.getPrice());
        values.put(product_status, product.getStatus() ? 1 : 0);
        values.put(product_image, product.getImageUrl());
        db.insert(table_Product, null, values);
        db.close();
    }
    private Product readProduct(int id) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get book query
        Cursor cursor = db.query(table_Product, // a. table
                Products_COLUMNS," " + product_id + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Product product = new Product(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5) == 1 , cursor.getString(6));
        cursor.close();
        return product;
    }
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        // select book query
        String query = "SELECT * FROM " + table_Product;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results
        Product product;
        if (cursor.moveToFirst()) {
            do {
                product = new Product(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5) == 1 , cursor.getString(6));

                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public void createBooth(Booth booth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(booth_id, booth.getId());
        values.put(booth_name, booth.getName());
        values.put(booth_owner, booth.getOwner());
        values.put(booth_description, booth.getDescription());
        values.put(booth_image, booth.getImage_url());
        db.insert(table_Booth, null, values);
        db.close();
    }
    public Booth readBooth(int id) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get book query
        Cursor cursor = db.query(table_Booth, // a. table
                Booth_COLUMNS," " + booth_id + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;

        Booth booth = new Booth(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        return booth;
    }
    public List<Booth> getAllBooths() {
        List<Booth> books = new ArrayList<>();
        // select book query
        String query = "SELECT  * FROM " + table_Booth;
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // parse all results
        Booth booth;
        if (cursor.moveToFirst()) {
            do {
                booth = new Booth(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4));
                books.add(booth);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }
    public void createBoothProduct(int booth_id, int product_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.booth_id, booth_id);
        values.put(DBHelper.product_id, product_id);
        db.insert(table_booth_product, null, values);
        db.close();
    }
    public List<Product> getProductsByBoothId(int booth_id) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get book query
        Cursor cursor = db.query(table_booth_product, // a. table
                Booth_Product_COLUMNS," " + booth_id + " = ?", new String[]{String.valueOf(booth_id)}, null, null, null, null);
        List<Product> products = new ArrayList<>();
        // if results !=null, parse the first one
        if (cursor.moveToFirst()) {
            do {
                Product product = readProduct(cursor.getInt(1));
                /*book.setId(Integer.parseInt(cursor.getString(0)));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));*/

                // Add book to books
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

}
/*    public int updatebooth(Booth booth) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(booth_local_id, booth.getId());
        values.put(booth_server_id, booth.getId());
        values.put(booth_name, booth.getName());
        values.put(booth_owner, booth.getOwner());
        values.put(booth_description, booth.getDescription());
        values.put(booth_image, booth.getImage_url());

        // update
        int i = db.update(table_Booth, values, booth_server_id + " = ?", new String[]{String.valueOf(booth.getId())});

        db.close();
        return i;
    }*/


/*    public void deleteBooth(Booth booth) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete book
        db.delete(table_Booth, booth_local_id + " = ?", new String[]{String.valueOf(booth.getId())});
        db.close();
    }*/