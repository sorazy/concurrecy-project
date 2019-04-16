import java.io.*;
import java.util.*;

public class RangePolicy
{
    int maxRange;
    int currentRange = 1;

    RangePolicy(int maxRange) {
        this.maxRange = maxRange;
    }

    public void recordEliminationSuccess() {
        if (currentRange > 1)
            currentRange--;
    }

    public void recordEliminationTimeout() {
        if (currentRange < maxRange)
            currentRange++;
    }

    public int getRange() {
        return currentRange;
    }
}