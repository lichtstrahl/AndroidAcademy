package root.iv.androidacademy.background;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import root.iv.androidacademy.app.App;

public class NewsWorker extends Worker {
    private static final String INPUT_SECTION = "input:section";

    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data input = getInputData();
        NewsService.call(App.getContext(), input.getString(INPUT_SECTION));
        App.logI("Действие выполнено");

        return Result.success();
    }

    public static Data getRequiredData(String section) {
        return new Data.Builder()
                .putString(INPUT_SECTION, section)
                .build();
    }
}
