package example.demo.service;

import java.util.List;

public class Util {
    int count = 0;

    public int incrementAndGet() {
        this.count++;
        return this.count;
    }

    public int compare(int a, int b) {
        if (a < b)
            return -1;
        else if (a > b)
            return 1;
        else return 0;
    }

    public static boolean isEmail(String input) {
        return input.contains("@");
    }

    public static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
