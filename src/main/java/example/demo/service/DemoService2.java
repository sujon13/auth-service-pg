package example.demo.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class DemoService2 implements IDemoService {
    @Override
    public String getName() {
        return "demo service 2";
    }
}
