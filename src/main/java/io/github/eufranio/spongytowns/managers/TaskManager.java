package io.github.eufranio.spongytowns.managers;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.tasks.ExpirationTask;
import io.github.eufranio.spongytowns.tasks.LastActiveUpdateTask;
import io.github.eufranio.spongytowns.tasks.TaxChargeTask;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 14/03/2018.
 */
@Singleton
public class TaskManager {

    private Task lastActiveUpdateTask;
    private Task expirationTask;
    private Task taxChargeTask;

    public void init(SpongyTowns plugin) {
        this.lastActiveUpdateTask = Task.builder()
                .interval(5, TimeUnit.MINUTES)
                .delay(1, TimeUnit.MINUTES)
                .execute(LastActiveUpdateTask::new)
                .submit(plugin);

        this.expirationTask = Task.builder()
                .interval(5, TimeUnit.MINUTES)
                .delay(1, TimeUnit.MINUTES)
                .execute(ExpirationTask::new)
                .submit(plugin);

        this.taxChargeTask = Task.builder()
                .interval(5, TimeUnit.MINUTES)
                .delay(1, TimeUnit.MINUTES)
                .execute(TaxChargeTask::new)
                .submit(plugin);
    }


}
