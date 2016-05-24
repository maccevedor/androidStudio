package maccevedor.maveru;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private MainDBAdapter mDBAdapter;
    private MainSimpleCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.avisos_list_view);
        findViewById(R.id.avisos_list_view);
        mListView.setDivider(null);
        mDBAdapter = new MainDBAdapter(this);
        mDBAdapter.open();

        if(savedInstanceState == null){
            mDBAdapter.deleteAllReminders();
            mDBAdapter.createReminder("1",true);
            mDBAdapter.createReminder("2",true);
            mDBAdapter.createReminder("3",false);
            mDBAdapter.createReminder("4",true);

        }


        Cursor cursor = mDBAdapter.fetchAllReminders();
        String[] from = new String[]{
                MainDBAdapter.COL_CONTENT
        };

        int[] to = new int[]{
            R.id.row_text
        };

        mCursorAdapter = new MainSimpleCursorAdapter(
                //content
                MainActivity.this,
                R.layout.avisos_row,
                cursor,
                from,
                to,
                0
        );

        mListView.setAdapter(mCursorAdapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_nuevo:
                Log.d(getLocalClassName(),"Crear Nuevo Aviso");
                return true;
            case R.id.action_salir:
                finish();
                return true;
            default:
                return false;
        }
    }
}
