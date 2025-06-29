package test.java;

import main.java.service.InMemoryTaskManager;
import main.java.service.Manager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    void initializationManager() {
        this.manager = new InMemoryTaskManager(Manager.getDefaultHistory());
    }

    @BeforeEach
    void beforeEach() {
        initializationManager();
    }
}
