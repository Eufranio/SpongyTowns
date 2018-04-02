package io.github.eufranio.spongytowns.managers;

import com.google.inject.Singleton;
import org.spongepowered.api.scheduler.Task;

/**
 * Created by Frani on 14/03/2018.
 */
@Singleton
public class TaskManager {

    private Task lastActiveUpdateTask;
    private Task expirationTask;


}
