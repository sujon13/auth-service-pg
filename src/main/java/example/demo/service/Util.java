package example.demo.service;

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
}
