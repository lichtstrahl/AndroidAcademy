package root.iv.androidacademy.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import root.iv.androidacademy.news.NewsDAO;
import root.iv.androidacademy.news.NewsEntity;


@Database(entities = {NewsEntity.class}, version = 2, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDAO getNewsDAO();
}
