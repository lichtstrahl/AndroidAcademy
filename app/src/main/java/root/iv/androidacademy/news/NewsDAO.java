package root.iv.androidacademy.news;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NewsEntity item);
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insertAll(NewsEntity ... items);

    @Delete
    int delete(NewsEntity item);
    @Update
    void update(NewsEntity item);

    @Query("DELETE FROM NewsEntity WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM NewsEntity")
    Flowable<List<NewsEntity>> getAllAsFlowable();

    @Query("SELECT * FROM NewsEntity")
    List<NewsEntity> getAllAsList();

    @Query("DELETE FROM NewsEntity")
    void deleteAll();

    @Query("SELECT * FROM NewsEntity WHERE id = :id")
    NewsEntity getItemById(int id);

    @Query("SELECT id FROM NewsEntity WHERE title = :title AND previewText = :preview AND publishDate = :date")
    int getId(String title, String preview, String date);
}
