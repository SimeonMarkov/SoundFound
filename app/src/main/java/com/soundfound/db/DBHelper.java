package com.soundfound.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soundfound.model.Song;
import com.soundfound.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.j256.ormlite.dao.DaoManager.createDao;

/**
 *
 * Created by Simeon Markov on 6/25/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "soundfound.db";
    private static final String EMAIL_PATTERN = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@(([a-zA-Z]+\\.)+[a-zA-Z]{2,})$";
    private static final String TAG = DBHelper.class.getName();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            Log.i(TAG, "Users table create...");
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            Log.i(TAG, "Songs table create...");
            TableUtils.createTableIfNotExists(connectionSource, Song.class);
            Log.i(TAG, "TABLES CREATED!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connectionSource.close();
    }

    public boolean insertUsers (String email, String password) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            Dao<User, String> userDao = createDao(connectionSource, User.class);
            User user = new User(email, password);
            if(userDao.create(user) != 1) {
                throw new Exception("Failure adding account");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean existsUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select email from users where email = '" + email + "'", null).getCount() > 0;
    }

    public boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    public User findUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        Dao<User, String> userDao = null;
        try {
            userDao = createDao(connectionSource, User.class);
            return userDao.queryBuilder().where().eq("email", email).and().eq("password", password).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean insertSongs(String artist, String track, String duration, String userId) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            Dao<Song, String> songDao = createDao(connectionSource, Song.class);
            Song song = new Song(artist, track, duration, userId);
            if(songDao.create(song) != 1) {
                throw new Exception("Failure adding account");
            }
            Log.i(TAG, String.format("INSERTED SONG:%s", song));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public List<Song> getSongsForUser(String userId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        Dao<Song, String> songDao = createDao(connectionSource, Song.class);
        List<Song> songs = songDao.queryBuilder().where().eq(Song.USER_ID_COLUMN, userId).query();
        return songs;
    }


}
