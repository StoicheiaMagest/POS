
package org.example;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MainTest {

    @Test
    void main_should_call_RegisterSystem_main() {
        String[] args = {};

        try (var registerSystem = mockStatic(RegisterSystem.class)) {

            Main.main(args);

            registerSystem.verify(() -> RegisterSystem.main(args));
        }
    }

    @Test
    void constructor_should_create_Main_instance() {
        new Main();
    }

}
