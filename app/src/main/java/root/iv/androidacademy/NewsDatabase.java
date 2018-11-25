package root.iv.androidacademy;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import root.iv.androidacademy.news.NewsDAO;
import root.iv.androidacademy.news.NewsEntity;


@Database(entities = {NewsEntity.class}, version = 2, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDAO getNewsDAO();
}
