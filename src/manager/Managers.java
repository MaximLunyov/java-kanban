package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import servers.adapters.FileAdapter;
import servers.adapters.HistoryManagerAdapter;
import servers.adapters.LocalDateTimeAdapter;

import java.io.File;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(File.class, new FileAdapter())
                .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
                .serializeNulls().create();
    }
}