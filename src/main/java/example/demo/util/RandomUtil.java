package example.demo.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUtil {

    public String getUUID() {
        return UUID.randomUUID().toString();
    }
}
