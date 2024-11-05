package example.demo.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@Service
public class DemoService1 implements IDemoService {

    @Override
    public String getName() {
        return "demo service 1";
    }
}
