package maccevedor.maveru;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private MainDBAdapter mDBAdapter;
    private MainSimpleCursorAdapter mCursorAdapter;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.avisos_list_view);
        findViewById(R.id.avisos_list_view);
        mListView.setDivider(null);
        mDBAdapter = new MainDBAdapter(this);
        mDBAdapter.open();


        // cuando pulsamos un item individual en la  listview
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                ListView modeListView = new ListView(MainActivity.this);
                String[] modes = new String[] { "Editar Aviso", "Borrar Aviso" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //editar aviso
                        if (position == 0) {
                            Toast.makeText(MainActivity.this, "editar "
                                    + masterListPosition, Toast.LENGTH_SHORT).show();
                            //borrar aviso
                        } else {
                            Toast.makeText(MainActivity.this, "borrar "
                                    + masterListPosition, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });




        Cursor cursor = mDBAdapter.fetchAllReminders();

        //desde las columnas definidas en la base de datos
        String[] from = new String[]{
                MainDBAdapter.COL_CONTENT
        };

        //a la id de views en el layout
        int[] to = new int[]{
                R.id.row_text
        };

        mCursorAdapter = new MainSimpleCursorAdapter(
                //context
                MainActivity.this,
                //el layout de la fila
                R.layout.avisos_row,
                //cursor
                cursor,
                //desde columnas definidas en la base de datos
                from,
                //a las ids de views en el layout
                to,
                //flag - no usado
                0);

        //el cursorAdapter (controller) está ahora actualizando la listView (view)
        //con datos desde la base de datos (modelo)
        mListView.setAdapter(mCursorAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_aviso:
                            for (int nC = mCursorAdapter.getCount() - 1; nC >= 0; nC--) {
                                if (mListView.isItemChecked(nC)) {
                                    mDBAdapter.deleteReminderById(getIdFromPosition(nC));
                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDBAdapter.fetchAllReminders());
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            });

        }

    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void fireCustomDialog(final Main aviso){
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (aviso != null);

        //esto es para un edit
        if (isEditOperation){
            titleView.setText("Editar Aviso");
            checkBox.setChecked(aviso.getImportant() == 1);
            editCustom.setText(aviso.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.azul_neutro));
        }

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText = editCustom.getText().toString();
                if (isEditOperation) {
                    Main reminderEdited = new Main(aviso.getId(),
                            reminderText, checkBox.isChecked() ? 1 : 0);
                    mDBAdapter.updateReminder(reminderEdited);
                    //esto es para nuevo aviso
                } else {
                    mDBAdapter.createReminder(reminderText, checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDBAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
