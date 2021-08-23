package androidx.appcompat.app;

import android.os.Bundle;

import androidx.annotation.Nullable;


//解决hermes-eventbusbug
public class ActionBarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
