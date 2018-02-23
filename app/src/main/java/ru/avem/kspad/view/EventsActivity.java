package ru.avem.kspad.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.avem.kspad.R;
import ru.avem.kspad.model.EventsHolder;

public class EventsActivity extends AppCompatActivity {

    @BindView(R.id.events)
    ListView mEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);

        ArrayAdapter<?> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                EventsHolder.getEventLogs());
        mEvents.setAdapter(arrayAdapter);
    }
}
