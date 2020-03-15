package mahoroba.uruhashi.common;

import android.support.annotation.NonNull;

public interface NonNullObserver<T> {
    void onChanged(@NonNull T t);
}
