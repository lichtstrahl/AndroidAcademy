package root.iv.androidacademy.news;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NewsEntity item);
    @Update
    int update(NewsEntity item);

    @Query("DELETE FROM NewsEntity WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM NewsEntity")
    Single<List<NewsEntity>> getAllAsSingle();

    @Query("DELETE FROM NewsEntity")
    int deleteAll();

    @Query("SELECT * FROM NewsEntity WHERE id = :id")
    Single<NewsEntity> getItemByIdAsSingle(int id);

    @Query("SELECT * FROM NewsEntity WHERE id = :id")
    NewsEntity getItemById(int id);

    @Query("SELECT id FROM NewsEntity WHERE title = :title AND previewText = :preview AND publishDate = :date")
    Single<Integer> getIdAsSingle(String title, String preview, String date);
}
