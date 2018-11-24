package root.iv.androidacademy.news;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NewsEntity item);
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insertAll(NewsEntity ... items);

    @Delete
    int delete(NewsEntity item);
    @Insert
    void update(NewsEntity item);

    @Query("SELECT * FROM NewsEntity")
    List<NewsEntity> getAll();

    @Query("SELECT * FROM NewsEntity WHERE id = :id")
    NewsEntity getItemById(int id);
}
